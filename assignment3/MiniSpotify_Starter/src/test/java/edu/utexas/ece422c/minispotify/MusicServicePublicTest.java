package edu.utexas.ece422c.minispotify;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

//starter public tests | preserve their assertions while applying the requested code style
class MusicServicePublicTest {
    private MusicService service() {
        MusicService service = MusicServices.create();
        service.addSong(new Song("1", "A", "Artist", "Rock", 100));
        service.addSong(new Song("2", "B", "Artist", "Jazz", 120));
        service.createUser("u");
        return service;
    }

    @Test
    void playlistPreservesOrderAndDuplicates() {
        MusicService service = service();
        service.createPlaylist("u", "p");
        service.addToPlaylist("u", "u", "p", "1");
        service.addToPlaylist("u", "u", "p", "2");
        service.addToPlaylist("u", "u", "p", "1");
        assertEquals(java.util.List.of("1", "2", "1"), service.getPlaylist("u", "p").stream().map(Song::getId).toList());
    }

    @Test
    void duplicateSongIdsRejected() {
        MusicService service = service();
        assertThrows(IllegalArgumentException.class, () -> service.addSong(new Song("1", "Other", "X", "Y", 1)));
    }

    @Test
    void topSongsUsesPlayCounts() {
        MusicService service = service();
        service.playSong("u", "2");
        service.playSong("u", "2");
        service.playSong("u", "1");
        assertEquals("2", service.getTopSongs(1).get(0).getId());
    }
}
