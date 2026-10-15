package edu.utexas.ece422c.minispotify;

public final class Song {
    private final String id;
    private final String title;
    private final String artist;
    private final String genre;
    private final int durationSeconds;

    public Song(String id, String title, String artist, String genre, int durationSeconds) {
        Validation.require(id, "id");
        if (id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (durationSeconds <= 0) {
            throw new IllegalArgumentException("duration must be positive");
        }
        this.id = id;
        this.title = Validation.require(title, "title");
        this.artist = Validation.require(artist, "artist");
        this.genre = Validation.require(genre, "genre");
        this.durationSeconds = durationSeconds;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    //identity depends only on id | immutable fields keep hash membership stable
    @Override
    public boolean equals(Object other) {
        return other instanceof Song song && id.equals(song.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return title + " - " + artist + " [" + id + "]";
    }
}
