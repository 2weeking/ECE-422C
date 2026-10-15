package edu.utexas.ece422c.minispotify;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class MusicServiceRequirementsTest {
    private MusicService service() {
        MusicService service = MusicServices.create();
        service.addSong(new Song("a", "alpha", "Artist", "Rock", 100));
        service.addSong(new Song("b", "Beta", "ARTIST", "rock", 120));
        service.addSong(new Song("c", "ALPHA", "Other", "Jazz", 80));
        service.createUser("alice");
        service.createUser("bob");
        service.createUser("eve");
        service.createPlaylist("alice", "mix");
        return service;
    }

    private List<String> ids(List<Song> songs) {
        return songs.stream().map(Song::getId).toList();
    }

    @Test
    void songIdentityAndConstructorInvariants() {
        Song song = new Song("id", "Title", "Artist", "Genre", 1);
        Song sameId = new Song("id", "Other", "Other", "Other", 2);
        assertEquals(song, sameId);
        assertEquals(song.hashCode(), sameId.hashCode());
        assertEquals(1, Set.of(song).size());
        assertNotEquals(song, new Song("ID", "Title", "Artist", "Genre", 1));
        assertNotEquals(song, null);
        assertNotEquals(song, "id");
        assertEquals("Title", song.getTitle());
        assertEquals("Artist", song.getArtist());
        assertEquals("Genre", song.getGenre());
        assertEquals(1, song.getDurationSeconds());
        assertThrows(IllegalArgumentException.class, () -> new Song(null, "t", "a", "g", 1));
        assertThrows(IllegalArgumentException.class, () -> new Song(" ", "t", "a", "g", 1));
        assertThrows(IllegalArgumentException.class, () -> new Song("i", null, "a", "g", 1));
        assertThrows(IllegalArgumentException.class, () -> new Song("i", "t", null, "g", 1));
        assertThrows(IllegalArgumentException.class, () -> new Song("i", "t", "a", null, 1));
        assertThrows(IllegalArgumentException.class, () -> new Song("i", "t", "a", "g", 0));
        assertThrows(IllegalArgumentException.class, () -> new Song("i", "t", "a", "g", -1));
    }

    @Test
    void duplicateSongsDoNotPolluteIndexesAndIdsAreCaseSensitive() {
        MusicService service = service();
        assertThrows(IllegalArgumentException.class, () -> service.addSong(new Song("a", "Oops", "New", "New", 1)));
        assertTrue(service.searchByArtist("New").isEmpty());
        assertTrue(service.searchByGenre("New").isEmpty());
        assertEquals("alpha", service.getSong("a").getTitle());
        assertNull(service.getSong("missing"));
        assertNull(service.getSong("A"));
        service.addSong(new Song("A", "Separate", "New", "New", 1));
        assertNotNull(service.getSong("A"));
    }

    @Test
    void searchesPreserveInsertionOrderAndUseExactArtistAndGenreMatches() {
        MusicService service = service();
        service.addSong(new Song("d", "alphabet soup", "Artist Plus", "Rockabilly", 1));
        assertEquals(List.of("a", "b"), ids(service.searchByArtist("artist")));
        assertEquals(List.of("a", "b"), ids(service.searchByGenre("ROCK")));
        assertEquals(List.of("a", "c", "d"), ids(service.searchByTitle("ALPH")));
        assertEquals(List.of("a", "b", "c", "d"), ids(service.searchByTitle("")));
        assertTrue(service.searchByArtist("art").isEmpty());
        assertTrue(service.searchByGenre("roc").isEmpty());
        assertTrue(service.searchByTitle("not found").isEmpty());
    }

    @Test
    void searchesAndRecommendationsAreIndependentOfDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            MusicService service = MusicServices.create();
            service.addSong(new Song("1", "INDIGO", "INDIE", "INDIE", 1));
            service.addSong(new Song("2", "indigo two", "indie", "indie", 1));
            service.createUser("u");
            service.addFavorite("u", "1");
            assertEquals(List.of("1", "2"), ids(service.searchByArtist("indie")));
            assertEquals(List.of("1", "2"), ids(service.searchByGenre("indie")));
            assertEquals(List.of("1", "2"), ids(service.searchByTitle("indigo")));
            assertEquals(List.of("2"), ids(service.getRecommendations("u", 10)));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void unicodeSimpleCaseVariantsShareIndexesAndRecommendationGenres() {
        MusicService service = MusicServices.create();
        service.addSong(new Song("1", "\u0130\u03a3", "\u0130", "\u03a3", 1));
        service.addSong(new Song("2", "i\u03c2", "i", "\u03c2", 1));
        service.createUser("u");
        service.addFavorite("u", "1");
        assertEquals(List.of("1", "2"), ids(service.searchByArtist("i")));
        assertEquals(List.of("1", "2"), ids(service.searchByGenre("\u03c3")));
        assertEquals(List.of("1", "2"), ids(service.searchByTitle("i\u03c3")));
        assertEquals(List.of("2"), ids(service.getRecommendations("u", 10)));
    }

    @Test
    void usersAndPlaylistNamesHaveCorrectUniquenessScopes() {
        MusicService service = service();
        assertThrows(IllegalArgumentException.class, () -> service.createUser("alice"));
        service.createUser("Alice");
        service.createPlaylist("bob", "mix");
        service.createPlaylist("alice", "Mix");
        assertThrows(IllegalArgumentException.class, () -> service.createPlaylist("alice", "mix"));
        assertTrue(service.getPlaylist("bob", "mix").isEmpty());
        assertTrue(service.getPlaylist("alice", "Mix").isEmpty());
    }

    @Test
    void playlistsAllowDuplicatesAndRemoveOnlyFirstOccurrence() {
        MusicService service = service();
        service.addToPlaylist("alice", "alice", "mix", "a");
        service.addToPlaylist("alice", "alice", "mix", "b");
        service.addToPlaylist("alice", "alice", "mix", "a");
        assertEquals(List.of("a", "b", "a"), ids(service.getPlaylist("alice", "mix")));
        service.removeFromPlaylist("alice", "alice", "mix", "a");
        assertEquals(List.of("b", "a"), ids(service.getPlaylist("alice", "mix")));
        assertThrows(IllegalArgumentException.class, () -> service.removeFromPlaylist("alice", "alice", "mix", "c"));
        assertEquals(List.of("b", "a"), ids(service.getPlaylist("alice", "mix")));
    }

    @Test
    void onlyOwnerGrantsPermissionsAndCollaboratorsCanEdit() {
        MusicService service = service();
        assertThrows(IllegalArgumentException.class, () -> service.addToPlaylist("bob", "alice", "mix", "a"));
        assertThrows(IllegalArgumentException.class, () -> service.removeFromPlaylist("eve", "alice", "mix", "a"));
        service.addCollaborator("alice", "alice", "mix", "bob");
        service.addCollaborator("alice", "alice", "mix", "bob");
        service.addToPlaylist("bob", "alice", "mix", "a");
        service.removeFromPlaylist("bob", "alice", "mix", "a");
        assertTrue(service.getPlaylist("alice", "mix").isEmpty());
        assertThrows(IllegalArgumentException.class, () -> service.addCollaborator("bob", "alice", "mix", "eve"));
        assertThrows(IllegalArgumentException.class, () -> service.addCollaborator("alice", "alice", "mix", "alice"));
        assertThrows(IllegalArgumentException.class, () -> service.addToPlaylist("eve", "alice", "mix", "a"));
    }

    @Test
    void followsAreUniqueOwnerScopedAndDoNotGrantEditing() {
        MusicService service = service();
        service.createPlaylist("eve", "mix");
        service.followPlaylist("bob", "alice", "mix");
        service.followPlaylist("bob", "alice", "mix");
        service.followPlaylist("bob", "eve", "mix");
        assertEquals(2, service.getFollowedPlaylists("bob").size());
        assertEquals(Set.of("alice", "eve"), service.getFollowedPlaylists("bob").stream().map(Playlist::getOwner).collect(java.util.stream.Collectors.toSet()));
        assertTrue(service.getFollowedPlaylists("alice").isEmpty());
        assertThrows(IllegalArgumentException.class, () -> service.followPlaylist("alice", "alice", "mix"));
        assertThrows(IllegalArgumentException.class, () -> service.addToPlaylist("bob", "alice", "mix", "a"));
        service.addToPlaylist("alice", "alice", "mix", "a");
        Playlist followed = service.getFollowedPlaylists("bob").get(0);
        assertEquals("mix", followed.getName());
        assertEquals(List.of("a"), ids(followed.getSongs()));
    }

    @Test
    void favoritesAreUniqueAndRemovingAbsentKnownSongIsNoOp() {
        MusicService service = service();
        service.addFavorite("alice", "a");
        service.addFavorite("alice", "a");
        assertEquals(Set.of(service.getSong("a")), service.getFavorites("alice"));
        service.removeFavorite("alice", "b");
        assertEquals(1, service.getFavorites("alice").size());
        service.removeFavorite("alice", "a");
        service.removeFavorite("alice", "a");
        assertTrue(service.getFavorites("alice").isEmpty());
        assertThrows(IllegalArgumentException.class, () -> service.removeFavorite("alice", "missing"));
    }

    @ParameterizedTest
    @ValueSource(ints = {49, 50, 51})
    void historyBoundariesDiscardOnlyOldestPlay(int plays) {
        MusicService service = MusicServices.create();
        service.createUser("u");
        for (int index = 0; index < plays; index++) {
            String id = Integer.toString(index);
            service.addSong(new Song(id, id, "a", "g", 1));
            service.playSong("u", id);
        }
        List<Song> recent = service.getRecentlyPlayed("u", Integer.MAX_VALUE);
        assertEquals(Math.min(50, plays), recent.size());
        assertEquals(Integer.toString(plays - 1), recent.get(0).getId());
        assertEquals(Integer.toString(Math.max(0, plays - 50)), recent.get(recent.size() - 1).getId());
        assertEquals(1, service.getRecentlyPlayed("u", 1).size());
    }

    @Test
    void recentHistoryDeduplicatesAtMostRecentPosition() {
        MusicService service = service();
        for (String id : List.of("a", "b", "a", "c", "b")) {
            service.playSong("alice", id);
        }
        assertEquals(List.of("b", "c", "a"), ids(service.getRecentlyPlayed("alice", 50)));
        assertEquals(List.of("b", "c"), ids(service.getRecentlyPlayed("alice", 2)));
        assertTrue(service.getRecentlyPlayed("bob", 50).isEmpty());
        assertTrue(service.getRecentlyPlayed("alice", 0).isEmpty());
    }

    @Test
    void repeatedPlaysConsumeHistorySlotsAndNeverLoseGlobalCounts() {
        MusicService service = service();
        service.playSong("alice", "a");
        for (int index = 0; index < 50; index++) {
            service.playSong("alice", "b");
        }
        assertEquals(List.of("b"), ids(service.getRecentlyPlayed("alice", 100)));
        assertEquals(List.of("b", "a", "c"), ids(service.getTopSongs(100)));
    }

    @Test
    void topSongsIncludeUnplayedSongsAndUseCaseInsensitiveTitleThenId() {
        MusicService service = service();
        assertEquals(List.of("a", "c", "b"), ids(service.getTopSongs(Integer.MAX_VALUE)));
        service.playSong("bob", "b");
        service.playSong("alice", "c");
        assertEquals(List.of("c", "b", "a"), ids(service.getTopSongs(10)));
        service.playSong("alice", "b");
        assertEquals(List.of("b", "c"), ids(service.getTopSongs(2)));
        assertTrue(service.getTopSongs(0).isEmpty());
    }

    @Test
    void recommendationsUseAllFavoriteGenresAndGlobalRanking() {
        MusicService service = service();
        service.addSong(new Song("d", "delta", "Other", "ROCK", 1));
        service.addSong(new Song("e", "Echo", "Other", "jazz", 1));
        service.addSong(new Song("f", "Foxtrot", "Other", "Pop", 1));
        service.addFavorite("alice", "a");
        service.addFavorite("alice", "c");
        service.playSong("bob", "e");
        assertEquals(List.of("e", "b", "d"), ids(service.getRecommendations("alice", 100)));
        assertEquals(List.of("e"), ids(service.getRecommendations("alice", 1)));
        service.playSong("alice", "e");
        assertEquals(List.of("b", "d"), ids(service.getRecommendations("alice", 100)));
        service.removeFavorite("alice", "c");
        assertEquals(List.of("b", "d"), ids(service.getRecommendations("alice", 100)));
        service.removeFavorite("alice", "a");
        assertTrue(service.getRecommendations("alice", 100).isEmpty());
    }

    @Test
    void recommendationsExcludePlaysEvictedFromHistory() {
        MusicService service = service();
        service.addFavorite("alice", "a");
        service.playSong("alice", "b");
        for (int index = 0; index < 50; index++) {
            service.playSong("alice", "c");
        }
        assertEquals(List.of("c"), ids(service.getRecentlyPlayed("alice", 100)));
        assertTrue(service.getRecommendations("alice", 100).isEmpty());
        service.removeFavorite("alice", "a");
        service.addFavorite("alice", "b");
        assertEquals(List.of("a"), ids(service.getRecommendations("alice", 100)));
    }

    @Test
    void allReturnedCollectionsAndPlaylistObjectsProtectServiceState() {
        MusicService service = service();
        service.addToPlaylist("alice", "alice", "mix", "a");
        service.addFavorite("alice", "a");
        service.playSong("alice", "a");
        service.followPlaylist("bob", "alice", "mix");
        List<Song> playlist = service.getPlaylist("alice", "mix");
        Set<Song> favorites = service.getFavorites("alice");
        Playlist snapshot = service.getFollowedPlaylists("bob").get(0);
        assertThrows(UnsupportedOperationException.class, playlist::clear);
        assertThrows(UnsupportedOperationException.class, favorites::clear);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.getSongs().add(service.getSong("b")));
        assertThrows(UnsupportedOperationException.class, () -> service.getFollowedPlaylists("bob").clear());
        assertThrows(UnsupportedOperationException.class, () -> service.searchByArtist("Artist").clear());
        assertThrows(UnsupportedOperationException.class, () -> service.searchByGenre("Rock").clear());
        assertThrows(UnsupportedOperationException.class, () -> service.searchByTitle("alpha").clear());
        assertThrows(UnsupportedOperationException.class, () -> service.getRecentlyPlayed("alice", 10).clear());
        assertThrows(UnsupportedOperationException.class, () -> service.getTopSongs(10).clear());
        assertThrows(UnsupportedOperationException.class, () -> service.getRecommendations("alice", 10).clear());
        service.addToPlaylist("alice", "alice", "mix", "b");
        service.addFavorite("alice", "b");
        assertEquals(List.of("a"), ids(playlist));
        assertEquals(List.of("a"), ids(snapshot.getSongs()));
        assertEquals(1, favorites.size());
        assertEquals(List.of("a", "b"), ids(service.getPlaylist("alice", "mix")));
        assertEquals(List.of("a", "b"), ids(service.getFollowedPlaylists("bob").get(0).getSongs()));
    }

    @Test
    void emptyCatalogAndZeroLimitsReturnEmptyResults() {
        MusicService service = MusicServices.create();
        service.createUser("u");
        assertTrue(service.getTopSongs(100).isEmpty());
        assertTrue(service.getTopSongs(0).isEmpty());
        assertTrue(service.searchByTitle("").isEmpty());
        assertTrue(service.searchByArtist("").isEmpty());
        assertTrue(service.searchByGenre("").isEmpty());
        assertTrue(service.getFavorites("u").isEmpty());
        assertTrue(service.getRecommendations("u", Integer.MAX_VALUE).isEmpty());
        assertTrue(service.getRecommendations("u", 0).isEmpty());
        assertTrue(service.getRecentlyPlayed("u", 0).isEmpty());
    }

    @Test
    void factoryCreatesIndependentServiceInstances() {
        MusicService first = service();
        MusicService second = MusicServices.create();
        assertNull(second.getSong("a"));
        assertThrows(IllegalArgumentException.class, () -> second.getFavorites("alice"));
        first.playSong("alice", "b");
        assertTrue(second.getTopSongs(10).isEmpty());
    }

    //each invalid call is a separate test | includes every nullable service argument position
    @TestFactory
    Stream<DynamicTest> nullArgumentsFailConsistently() {
        MusicService service = service();
        List<Executable> calls = List.of(
                () -> service.addSong(null),
                () -> service.getSong(null),
                () -> service.searchByArtist(null),
                () -> service.searchByGenre(null),
                () -> service.searchByTitle(null),
                () -> service.createUser(null),
                () -> service.createPlaylist(null, "mix"),
                () -> service.createPlaylist("alice", null),
                () -> service.addToPlaylist(null, "alice", "mix", "a"),
                () -> service.addToPlaylist("alice", null, "mix", "a"),
                () -> service.addToPlaylist("alice", "alice", null, "a"),
                () -> service.addToPlaylist("alice", "alice", "mix", null),
                () -> service.removeFromPlaylist(null, "alice", "mix", "a"),
                () -> service.removeFromPlaylist("alice", null, "mix", "a"),
                () -> service.removeFromPlaylist("alice", "alice", null, "a"),
                () -> service.removeFromPlaylist("alice", "alice", "mix", null),
                () -> service.getPlaylist(null, "mix"),
                () -> service.getPlaylist("alice", null),
                () -> service.addCollaborator(null, "alice", "mix", "bob"),
                () -> service.addCollaborator("alice", null, "mix", "bob"),
                () -> service.addCollaborator("alice", "alice", null, "bob"),
                () -> service.addCollaborator("alice", "alice", "mix", null),
                () -> service.followPlaylist(null, "alice", "mix"),
                () -> service.followPlaylist("bob", null, "mix"),
                () -> service.followPlaylist("bob", "alice", null),
                () -> service.getFollowedPlaylists(null),
                () -> service.addFavorite(null, "a"),
                () -> service.addFavorite("alice", null),
                () -> service.removeFavorite(null, "a"),
                () -> service.removeFavorite("alice", null),
                () -> service.getFavorites(null),
                () -> service.playSong(null, "a"),
                () -> service.playSong("alice", null),
                () -> service.getRecentlyPlayed(null, 0),
                () -> service.getRecommendations(null, 0)
        );
        return java.util.stream.IntStream.range(0, calls.size()).mapToObj(index -> DynamicTest.dynamicTest("null argument " + index, () -> assertThrows(IllegalArgumentException.class, calls.get(index))));
    }

    @TestFactory
    Stream<DynamicTest> unknownReferencesAndNegativeLimitsFail() {
        MusicService service = service();
        List<Executable> calls = List.of(
                () -> service.createPlaylist("missing", "p"),
                () -> service.addToPlaylist("missing", "alice", "mix", "a"),
                () -> service.addToPlaylist("alice", "missing", "mix", "a"),
                () -> service.addToPlaylist("alice", "alice", "missing", "a"),
                () -> service.addToPlaylist("alice", "alice", "mix", "missing"),
                () -> service.removeFromPlaylist("missing", "alice", "mix", "a"),
                () -> service.removeFromPlaylist("alice", "missing", "mix", "a"),
                () -> service.removeFromPlaylist("alice", "alice", "missing", "a"),
                () -> service.removeFromPlaylist("alice", "alice", "mix", "missing"),
                () -> service.getPlaylist("missing", "mix"),
                () -> service.getPlaylist("alice", "missing"),
                () -> service.addCollaborator("missing", "alice", "mix", "bob"),
                () -> service.addCollaborator("alice", "missing", "mix", "bob"),
                () -> service.addCollaborator("alice", "alice", "missing", "bob"),
                () -> service.addCollaborator("alice", "alice", "mix", "missing"),
                () -> service.followPlaylist("missing", "alice", "mix"),
                () -> service.followPlaylist("bob", "missing", "mix"),
                () -> service.followPlaylist("bob", "alice", "missing"),
                () -> service.getFollowedPlaylists("missing"),
                () -> service.addFavorite("missing", "a"),
                () -> service.addFavorite("alice", "missing"),
                () -> service.removeFavorite("missing", "a"),
                () -> service.removeFavorite("alice", "missing"),
                () -> service.getFavorites("missing"),
                () -> service.playSong("missing", "a"),
                () -> service.playSong("alice", "missing"),
                () -> service.getRecentlyPlayed("missing", 0),
                () -> service.getRecommendations("missing", 0),
                () -> service.getRecentlyPlayed("alice", -1),
                () -> service.getTopSongs(-1),
                () -> service.getRecommendations("alice", -1)
        );
        return java.util.stream.IntStream.range(0, calls.size()).mapToObj(index -> DynamicTest.dynamicTest("invalid operation " + index, () -> assertThrows(IllegalArgumentException.class, calls.get(index))));
    }

    @Test
    void failedOperationsDoNotChangeHistoryRankingsOrFavorites() {
        MusicService service = service();
        assertThrows(IllegalArgumentException.class, () -> service.playSong("missing", "b"));
        assertThrows(IllegalArgumentException.class, () -> service.playSong("alice", "missing"));
        assertThrows(IllegalArgumentException.class, () -> service.addFavorite("alice", "missing"));
        assertThrows(IllegalArgumentException.class, () -> service.addCollaborator("alice", "alice", "mix", "missing"));
        assertTrue(service.getRecentlyPlayed("alice", 10).isEmpty());
        assertTrue(service.getFavorites("alice").isEmpty());
        assertTrue(service.getPlaylist("alice", "mix").isEmpty());
        assertEquals(List.of("a", "c", "b"), ids(service.getTopSongs(10)));
    }

    @Test
    void rankingsMatchIndependentFullSortAfterRandomizedPlays() {
        MusicService service = MusicServices.create();
        service.createUser("u");
        service.createUser("other");
        List<Song> songs = new ArrayList<>();
        Map<String, Integer> counts = new HashMap<>();
        Set<String> playedByUser = new java.util.HashSet<>();
        Random random = new Random(422);
        for (int index = 0; index < 200; index++) {
            Song song = new Song("id" + index, (index % 2 == 0 ? "Title" : "title") + index % 7, "a", index % 2 == 0 ? "ROCK" : "rock", 1);
            songs.add(song);
            service.addSong(song);
        }
        service.addFavorite("u", "id0");
        for (int index = 0; index < 400; index++) {
            Song song = songs.get(random.nextInt(songs.size()));
            String username = index % 3 == 0 ? "u" : "other";
            service.playSong(username, song.getId());
            counts.merge(song.getId(), 1, Integer::sum);
            if (username.equals("u")) {
                playedByUser.add(song.getId());
            }
        }
        Comparator<Song> order = Comparator.<Song>comparingInt(song -> counts.getOrDefault(song.getId(), 0)).reversed().thenComparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER).thenComparing(Song::getId);
        List<Song> allRanked = songs.stream().sorted(order).toList();
        List<Song> recommended = songs.stream().filter(song -> !song.getId().equals("id0") && !playedByUser.contains(song.getId())).sorted(order).toList();
        for (int n : List.of(0, 1, 7, 50, 200, Integer.MAX_VALUE)) {
            assertEquals(allRanked.subList(0, Math.min(n, allRanked.size())), service.getTopSongs(n));
            assertEquals(recommended.subList(0, Math.min(n, recommended.size())), service.getRecommendations("u", n));
        }
    }

    @Test
    void halfMillionCatalogMeetsIndexedLookupCalibration() {
        //generous timeout allows slower graders | measured work matches the assignment calibration
        assertTimeout(Duration.ofSeconds(15), () -> {
            long started = System.nanoTime();
            MusicService service = MusicServices.create();
            for (int index = 0; index < 500_000; index++) {
                service.addSong(new Song("large" + index, "Title " + index, "Artist " + index % 5000, "Genre " + index % 100, 1));
            }
            for (int index = 0; index < 100_000; index++) {
                assertNotNull(service.getSong("large" + index * 5));
            }
            for (int index = 0; index < 10_000; index++) {
                assertEquals(100, service.searchByArtist("ARTIST " + index % 5000).size());
            }
            double seconds = (System.nanoTime() - started) / 1_000_000_000.0;
            System.out.printf(Locale.ROOT, "Calibration: 500,000 adds + 100,000 lookups + 10,000 artist searches = %.3f seconds%n", seconds);
            assertEquals(5000, service.searchByGenre("GENRE 1").size());
            assertEquals(10, service.getTopSongs(10).size());
        });
    }
}
