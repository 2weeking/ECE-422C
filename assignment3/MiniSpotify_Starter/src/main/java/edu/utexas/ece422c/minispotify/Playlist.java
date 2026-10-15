package edu.utexas.ece422c.minispotify;

import java.util.List;

//immutable client snapshot | editable contents and permissions stay inside PlaylistState
public final class Playlist {
    private final String name;
    private final String owner;
    private final List<Song> songs;

    public Playlist(String name, String owner) {
        this(name, owner, List.of());
    }

    Playlist(String name, String owner, List<Song> songs) {
        this.name = Validation.require(name, "playlistName");
        this.owner = Validation.require(owner, "owner");
        this.songs = List.copyOf(songs);
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public List<Song> getSongs() {
        return songs;
    }
}
