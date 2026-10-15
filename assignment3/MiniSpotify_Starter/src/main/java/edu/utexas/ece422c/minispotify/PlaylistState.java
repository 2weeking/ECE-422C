package edu.utexas.ece422c.minispotify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class PlaylistState {
    private final String name;
    private final String owner;
    private final List<Song> songs = new ArrayList<>();
    private final Set<String> collaborators = new HashSet<>();

    PlaylistState(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }

    String owner() {
        return owner;
    }

    void requireEditor(String actingUser) {
        if (!owner.equals(actingUser) && !collaborators.contains(actingUser)) {
            throw new IllegalArgumentException("User cannot edit this playlist: " + actingUser);
        }
    }

    void addCollaborator(String actingUser, String collaborator) {
        if (!owner.equals(actingUser)) {
            throw new IllegalArgumentException("Only the owner may add collaborators");
        }
        if (owner.equals(collaborator)) {
            throw new IllegalArgumentException("Owner cannot collaborate on their own playlist");
        }
        collaborators.add(collaborator);
    }

    void addSong(Song song) {
        songs.add(song);
    }

    void removeSong(Song song) {
        //list removal uses song identity | only the first occurrence is removed
        if (!songs.remove(song)) {
            throw new IllegalArgumentException("Song not in playlist: " + song.getId());
        }
    }

    List<Song> songsSnapshot() {
        return List.copyOf(songs);
    }

    Playlist snapshot() {
        return new Playlist(name, owner, songs);
    }
}
