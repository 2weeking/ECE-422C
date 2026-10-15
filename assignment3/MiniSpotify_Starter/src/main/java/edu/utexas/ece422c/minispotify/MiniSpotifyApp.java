package edu.utexas.ece422c.minispotify;

import java.io.InputStream;

public class MiniSpotifyApp {
    public static void main(String[] args) throws Exception {
        MusicService service = MusicServices.create();
        try (InputStream input = MiniSpotifyApp.class.getResourceAsStream("/songs.csv")) {
            for (Song song : CatalogLoader.load(input)) {
                service.addSong(song);
            }
        }
        service.createUser("alice");
        service.createUser("bob");
        service.createPlaylist("alice", "Running");
        service.addToPlaylist("alice", "alice", "Running", "s1");
        service.addCollaborator("alice", "alice", "Running", "bob");
        service.addToPlaylist("bob", "alice", "Running", "s2");
        service.followPlaylist("bob", "alice", "Running");
        service.addFavorite("alice", "s1");
        service.playSong("alice", "s1");
        System.out.println("Running: " + service.getPlaylist("alice", "Running"));
        System.out.println("Recently played: " + service.getRecentlyPlayed("alice", 5));
        System.out.println("Recommendations: " + service.getRecommendations("alice", 5));
        System.out.println("Top songs: " + service.getTopSongs(3));

        //search titles by partial text | ignores capitalization
        System.out.println(service.searchByTitle("midnight"));

        //search artists by exact name | ignores capitalization
        System.out.println(service.searchByArtist("The Signals"));

        //search genres by exact name | ignores capitalization
        System.out.println(service.searchByGenre("rock"));
    }
}
