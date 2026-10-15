package edu.utexas.ece422c.minispotify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class SongCatalog {
    //one ordered catalog | hash lookup and insertion-order title searches share storage
    private final Map<String, Song> songsById = new LinkedHashMap<>();
    private final Map<String, List<Song>> songsByArtist = new HashMap<>();
    private final Map<String, List<Song>> songsByGenre = new HashMap<>();

    static String normalize(String value) {
        //fold unicode code points without locale rules | matches Java's simple case-insensitive equality
        StringBuilder normalized = new StringBuilder(value.length());
        for (int offset = 0; offset < value.length();) {
            int codePoint = value.codePointAt(offset);
            normalized.appendCodePoint(Character.toLowerCase(Character.toUpperCase(codePoint)));
            offset += Character.charCount(codePoint);
        }
        return normalized.toString();
    }

    void add(Song song) {
        Validation.require(song, "song");
        if (songsById.containsKey(song.getId())) {
            throw new IllegalArgumentException("Duplicate song id: " + song.getId());
        }
        songsById.put(song.getId(), song);
        index(songsByArtist, song.getArtist(), song);
        index(songsByGenre, song.getGenre(), song);
    }

    private void index(Map<String, List<Song>> index, String key, Song song) {
        index.computeIfAbsent(normalize(key), ignored -> new ArrayList<>()).add(song);
    }

    Song get(String id) {
        return songsById.get(Validation.require(id, "songId"));
    }

    Song requireSong(String id) {
        Song song = get(id);
        if (song == null) {
            throw new IllegalArgumentException("Unknown song: " + id);
        }
        return song;
    }

    List<Song> byArtist(String artist) {
        return List.copyOf(songsByArtist.getOrDefault(normalize(Validation.require(artist, "artist")), List.of()));
    }

    List<Song> byGenre(String genre) {
        return List.copyOf(genreCandidates(Validation.require(genre, "genre")));
    }

    //internal traversal only | public search methods copy their indexed results
    List<Song> genreCandidates(String genre) {
        return songsByGenre.getOrDefault(normalize(genre), List.of());
    }

    List<Song> byTitle(String query) {
        String normalizedQuery = normalize(Validation.require(query, "query"));
        List<Song> matches = new ArrayList<>();
        for (Song song : songsById.values()) {
            if (normalize(song.getTitle()).contains(normalizedQuery)) {
                matches.add(song);
            }
        }
        return List.copyOf(matches);
    }

    Collection<Song> allSongs() {
        return songsById.values();
    }

    int size() {
        return songsById.size();
    }
}
