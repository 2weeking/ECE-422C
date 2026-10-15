package edu.utexas.ece422c.minispotify;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MusicServiceAuditTest {
    private List<String> ids(List<Song> songs) {
        return songs.stream().map(Song::getId).toList();
    }

    @Test
    void editingPermissionsStayWithinOnePlaylistAndUseExactUsernames() {
        MusicService service = MusicServices.create();
        service.addSong(new Song("s", "Song", "Artist", "Rock", 1));
        for (String username : List.of("alice", "Alice", "bob", "eve")) {
            service.createUser(username);
        }
        service.createPlaylist("alice", "mix");
        service.createPlaylist("alice", "other");
        service.createPlaylist("eve", "mix");
        service.addCollaborator("alice", "alice", "mix", "bob");
        service.addToPlaylist("bob", "alice", "mix", "s");
        assertThrows(IllegalArgumentException.class, () -> service.addToPlaylist("bob", "alice", "other", "s"));
        assertThrows(IllegalArgumentException.class, () -> service.addToPlaylist("bob", "eve", "mix", "s"));
        assertThrows(IllegalArgumentException.class, () -> service.addToPlaylist("Alice", "alice", "mix", "s"));
        assertThrows(IllegalArgumentException.class, () -> service.addCollaborator("Alice", "alice", "mix", "eve"));
        service.followPlaylist("Alice", "alice", "mix");
        assertEquals("alice", service.getFollowedPlaylists("Alice").get(0).getOwner());
        assertEquals(List.of("s"), ids(service.getPlaylist("alice", "mix")));
        assertTrue(service.getPlaylist("alice", "other").isEmpty());
        assertTrue(service.getPlaylist("eve", "mix").isEmpty());
    }

    @Test
    void snapshotsRemainStableAfterLaterCatalogPlaysAndHistoryChanges() {
        MusicService service = MusicServices.create();
        service.createUser("u");
        service.addSong(new Song("a", "alpha", "Artist", "Rock", 1));
        service.addSong(new Song("b", "beta", "Artist", "Rock", 1));
        service.playSong("u", "a");
        List<Song> artistSnapshot = service.searchByArtist("artist");
        List<Song> genreSnapshot = service.searchByGenre("rock");
        List<Song> titleSnapshot = service.searchByTitle("a");
        List<Song> rankingSnapshot = service.getTopSongs(10);
        List<Song> historySnapshot = service.getRecentlyPlayed("u", 10);
        service.addSong(new Song("c", "gamma", "ARTIST", "ROCK", 1));
        service.playSong("u", "b");
        service.playSong("u", "b");
        assertEquals(List.of("a", "b"), ids(artistSnapshot));
        assertEquals(List.of("a", "b"), ids(genreSnapshot));
        assertEquals(List.of("a", "b"), ids(titleSnapshot));
        assertEquals(List.of("a", "b"), ids(rankingSnapshot));
        assertEquals(List.of("a"), ids(historySnapshot));
        assertEquals(List.of("a", "b", "c"), ids(service.searchByArtist("artist")));
        assertEquals(List.of("b", "a", "c"), ids(service.getTopSongs(10)));
        assertEquals(List.of("b", "a"), ids(service.getRecentlyPlayed("u", 10)));
    }

    @Test
    void identicalTitlesUseCaseSensitiveIdsInBothRankings() {
        MusicService service = MusicServices.create();
        service.createUser("u");
        service.addSong(new Song("seed", "seed", "Artist", "Rock", 1));
        service.addSong(new Song("z", "SAME", "Artist", "rock", 1));
        service.addSong(new Song("a", "same", "Artist", "ROCK", 1));
        service.addSong(new Song("A", "Same", "Artist", "Rock", 1));
        service.addFavorite("u", "seed");
        assertEquals(List.of("A", "a", "z"), ids(service.getRecommendations("u", Integer.MAX_VALUE)));
        assertEquals(List.of("A", "a", "z", "seed"), ids(service.getTopSongs(Integer.MAX_VALUE)));
    }

    @Test
    void interleavedUserActivityMatchesIndependentFullHistoryModel() {
        MusicService service = MusicServices.create();
        List<Song> songs = new ArrayList<>();
        List<String> usernames = List.of("alice", "bob", "eve");
        Map<String, List<Song>> completeHistories = new HashMap<>();
        Map<String, Set<Song>> favorites = new HashMap<>();
        Map<Song, Integer> counts = new HashMap<>();
        for (String username : usernames) {
            service.createUser(username);
            completeHistories.put(username, new ArrayList<>());
            favorites.put(username, new HashSet<>());
        }
        for (int index = 0; index < 120; index++) {
            String genre = (index % 2 == 0 ? "GENRE " : "genre ") + index % 6;
            Song song = new Song("s" + index, "Title " + index % 9, "Artist", genre, 1);
            songs.add(song);
            service.addSong(song);
        }
        Comparator<Song> rank = Comparator.<Song>comparingInt(song -> counts.getOrDefault(song, 0))
                .reversed()
                .thenComparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Song::getId);
        Random random = new Random(20261014);
        for (int step = 0; step < 1500; step++) {
            String username = usernames.get(random.nextInt(usernames.size()));
            Song song = songs.get(random.nextInt(songs.size()));
            int operation = random.nextInt(5);
            if (operation < 3) {
                service.playSong(username, song.getId());
                completeHistories.get(username).add(song);
                counts.merge(song, 1, Integer::sum);
            } else if (operation == 3) {
                service.addFavorite(username, song.getId());
                favorites.get(username).add(song);
            } else {
                service.removeFavorite(username, song.getId());
                favorites.get(username).remove(song);
            }
            if (step % 25 == 0 || step == 1499) {
                for (String checkedUser : usernames) {
                    List<Song> completeHistory = completeHistories.get(checkedUser);
                    List<Song> expectedRecent = new ArrayList<>();
                    //derive the last fifty events from an unbounded reference log | then deduplicate
                    for (int index = completeHistory.size() - 1; index >= Math.max(0, completeHistory.size() - 50); index--) {
                        Song recentSong = completeHistory.get(index);
                        if (!expectedRecent.contains(recentSong)) {
                            expectedRecent.add(recentSong);
                        }
                    }
                    Set<Song> expectedFavorites = favorites.get(checkedUser);
                    Set<Song> lifetimePlays = new HashSet<>(completeHistory);
                    Set<String> genres = new HashSet<>();
                    for (Song favorite : expectedFavorites) {
                        genres.add(favorite.getGenre().toLowerCase(Locale.ROOT));
                    }
                    List<Song> expectedRecommendations = songs.stream()
                            .filter(candidate -> genres.contains(candidate.getGenre().toLowerCase(Locale.ROOT)))
                            .filter(candidate -> !expectedFavorites.contains(candidate) && !lifetimePlays.contains(candidate))
                            .sorted(rank)
                            .toList();
                    for (int n : List.of(0, 1, 7, 1000)) {
                        assertEquals(expectedRecent.subList(0, Math.min(n, expectedRecent.size())), service.getRecentlyPlayed(checkedUser, n));
                        assertEquals(expectedRecommendations.subList(0, Math.min(n, expectedRecommendations.size())), service.getRecommendations(checkedUser, n));
                    }
                    assertEquals(expectedFavorites, service.getFavorites(checkedUser));
                }
                assertEquals(songs.stream().sorted(rank).limit(17).toList(), service.getTopSongs(17));
            }
        }
    }
}
