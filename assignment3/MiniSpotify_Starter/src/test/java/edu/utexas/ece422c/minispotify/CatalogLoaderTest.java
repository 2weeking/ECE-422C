package edu.utexas.ece422c.minispotify;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CatalogLoaderTest {
    private InputStream csv(String text) {
        return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void providedLargeCatalogLoadsAndIsSearchable() throws Exception {
        MusicService service = MusicServices.create();
        try (InputStream input = getClass().getResourceAsStream("/minispotify_catalog_100000.csv")) {
            assertNotNull(input);
            List<Song> songs = CatalogLoader.load(input);
            assertEquals(100_000, songs.size());
            for (Song song : songs) {
                service.addSong(song);
            }
            Song first = songs.get(0);
            assertSame(first, service.getSong(first.getId()));
            assertTrue(service.searchByArtist(first.getArtist()).contains(first));
            assertTrue(service.searchByGenre(first.getGenre()).contains(first));
        }
    }

    @Test
    void headerOnlyAndBlankRowsAreHandled() throws Exception {
        assertTrue(CatalogLoader.load(csv("id,title,artist,genre,durationSeconds\n")).isEmpty());
        assertTrue(CatalogLoader.load(csv("")).isEmpty());
        List<Song> songs = CatalogLoader.load(csv("id,title,artist,genre,durationSeconds\n\ns1,Cafe,A,G,1\n\n"));
        assertEquals(1, songs.size());
        assertEquals("Cafe", songs.get(0).getTitle());
    }

    @Test
    void malformedRowsAndNullStreamsFailClearly() {
        assertThrows(IllegalArgumentException.class, () -> CatalogLoader.load(null));
        assertThrows(IllegalArgumentException.class, () -> CatalogLoader.load(csv("header\nbroken,row\n")));
        assertThrows(IllegalArgumentException.class, () -> CatalogLoader.load(csv("header\nid,t,a,g,no\n")));
        assertThrows(IllegalArgumentException.class, () -> CatalogLoader.load(csv("header\nid,t,a,g,0\n")));
    }
}
