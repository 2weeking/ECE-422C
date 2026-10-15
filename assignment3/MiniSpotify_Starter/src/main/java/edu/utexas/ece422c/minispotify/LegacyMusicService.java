package edu.utexas.ece422c.minispotify;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

//refactored starter implementation | coordinates validation and focused state classes
public class LegacyMusicService implements MusicService {
    private final SongCatalog catalog = new SongCatalog();
    private final Map<String, User> users = new HashMap<>();
    private final SongRanking ranking = new SongRanking();

    @Override
    public void addSong(Song song) {
        catalog.add(song);
    }

    @Override
    public Song getSong(String songId) {
        return catalog.get(songId);
    }

    @Override
    public List<Song> searchByArtist(String artist) {
        return catalog.byArtist(artist);
    }

    @Override
    public List<Song> searchByGenre(String genre) {
        return catalog.byGenre(genre);
    }

    @Override
    public List<Song> searchByTitle(String query) {
        return catalog.byTitle(query);
    }

    @Override
    public void createUser(String username) {
        Validation.require(username, "username");
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Duplicate user: " + username);
        }
        users.put(username, new User(username));
    }

    private User requireUser(String username) {
        User user = users.get(Validation.require(username, "username"));
        if (user == null) {
            throw new IllegalArgumentException("Unknown user: " + username);
        }
        return user;
    }

    private PlaylistState requirePlaylist(String owner, String name) {
        return requireUser(owner).requirePlaylist(name);
    }

    @Override
    public void createPlaylist(String username, String playlistName) {
        requireUser(username).createPlaylist(playlistName);
    }

    @Override
    public void addToPlaylist(String actingUser, String owner, String playlistName, String songId) {
        requireUser(actingUser);
        PlaylistState playlist = requirePlaylist(owner, playlistName);
        playlist.requireEditor(actingUser);
        playlist.addSong(catalog.requireSong(songId));
    }

    @Override
    public void removeFromPlaylist(String actingUser, String owner, String playlistName, String songId) {
        requireUser(actingUser);
        PlaylistState playlist = requirePlaylist(owner, playlistName);
        playlist.requireEditor(actingUser);
        playlist.removeSong(catalog.requireSong(songId));
    }

    @Override
    public List<Song> getPlaylist(String owner, String playlistName) {
        return requirePlaylist(owner, playlistName).songsSnapshot();
    }

    @Override
    public void addCollaborator(String actingUser, String owner, String playlistName, String collaborator) {
        requireUser(actingUser);
        requireUser(collaborator);
        requirePlaylist(owner, playlistName).addCollaborator(actingUser, collaborator);
    }

    @Override
    public void followPlaylist(String username, String owner, String playlistName) {
        requireUser(username).follow(requirePlaylist(owner, playlistName));
    }

    @Override
    public List<Playlist> getFollowedPlaylists(String username) {
        return requireUser(username).followedSnapshots();
    }

    @Override
    public void addFavorite(String username, String songId) {
        requireUser(username).addFavorite(catalog.requireSong(songId));
    }

    @Override
    public void removeFavorite(String username, String songId) {
        requireUser(username).removeFavorite(catalog.requireSong(songId));
    }

    @Override
    public Set<Song> getFavorites(String username) {
        return requireUser(username).getFavorites();
    }

    @Override
    public void playSong(String username, String songId) {
        User user = requireUser(username);
        Song song = catalog.requireSong(songId);
        user.recordPlay(song);
        ranking.recordPlay(song);
    }

    @Override
    public List<Song> getRecentlyPlayed(String username, int n) {
        Validation.nonnegative(n);
        return requireUser(username).recentlyPlayed(n);
    }

    @Override
    public List<Song> getTopSongs(int n) {
        Validation.nonnegative(n);
        return ranking.select(catalog.allSongs(), Math.min(n, catalog.size()), song -> true);
    }

    @Override
    public List<Song> getRecommendations(String username, int n) {
        Validation.nonnegative(n);
        User user = requireUser(username);
        Set<String> genres = user.favoriteGenres();
        if (n == 0 || genres.isEmpty()) {
            return List.of();
        }
        //walk only favorite-genre buckets | each song belongs to exactly one genre bucket
        Iterable<Song> candidates = () -> new Iterator<Song>() {
            private final Iterator<String> remainingGenres = genres.iterator();
            private Iterator<Song> currentSongs = List.<Song>of().iterator();

            @Override
            public boolean hasNext() {
                while (!currentSongs.hasNext() && remainingGenres.hasNext()) {
                    currentSongs = catalog.genreCandidates(remainingGenres.next()).iterator();
                }
                return currentSongs.hasNext();
            }

            @Override
            public Song next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                return currentSongs.next();
            }
        };
        return ranking.select(candidates, Math.min(n, catalog.size()), song -> !user.excludesRecommendation(song));
    }
}
