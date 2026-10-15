package edu.utexas.ece422c.minispotify;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class User {
    private static final int HISTORY_LIMIT = 50;
    private final String username;
    private final Map<String, PlaylistState> playlists = new HashMap<>();
    private final Set<PlaylistState> followedPlaylists = new LinkedHashSet<>();
    private final Set<Song> favorites = new LinkedHashSet<>();
    private final Deque<Song> history = new ArrayDeque<>();
    private final Set<String> everPlayed = new HashSet<>();

    public User(String username) {
        this.username = Validation.require(username, "username");
    }

    public String getUsername() {
        return username;
    }

    void createPlaylist(String name) {
        Validation.require(name, "playlistName");
        if (playlists.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate playlist: " + name);
        }
        playlists.put(name, new PlaylistState(name, username));
    }

    PlaylistState requirePlaylist(String name) {
        PlaylistState playlist = playlists.get(Validation.require(name, "playlistName"));
        if (playlist == null) {
            throw new IllegalArgumentException("Unknown playlist: " + name);
        }
        return playlist;
    }

    void follow(PlaylistState playlist) {
        if (username.equals(playlist.owner())) {
            throw new IllegalArgumentException("User cannot follow their own playlist");
        }
        followedPlaylists.add(playlist);
    }

    List<Playlist> followedSnapshots() {
        List<Playlist> snapshots = new ArrayList<>();
        for (PlaylistState playlist : followedPlaylists) {
            snapshots.add(playlist.snapshot());
        }
        return List.copyOf(snapshots);
    }

    void addFavorite(Song song) {
        favorites.add(song);
    }

    void removeFavorite(Song song) {
        favorites.remove(song);
    }

    public Set<Song> getFavorites() {
        return Set.copyOf(favorites);
    }

    Set<String> favoriteGenres() {
        Set<String> genres = new HashSet<>();
        for (Song song : favorites) {
            genres.add(SongCatalog.normalize(song.getGenre()));
        }
        return genres;
    }

    boolean excludesRecommendation(Song song) {
        return favorites.contains(song) || everPlayed.contains(song.getId());
    }

    void recordPlay(Song song) {
        //keep play events separately from lifetime membership | repeats consume history slots
        if (history.size() == HISTORY_LIMIT) {
            history.removeLast();
        }
        history.addFirst(song);
        everPlayed.add(song.getId());
    }

    List<Song> recentlyPlayed(int n) {
        List<Song> recent = new ArrayList<>();
        Set<Song> seen = new HashSet<>();
        for (Song song : history) {
            if (recent.size() == n) {
                break;
            }
            if (seen.add(song)) {
                recent.add(song);
            }
        }
        return List.copyOf(recent);
    }
}
