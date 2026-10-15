package edu.utexas.ece422c.minispotify;

public final class MusicServices {
    private MusicServices() {
    }

    //preserve the grading entry point | each call creates independent service state
    public static MusicService create() {
        return new LegacyMusicService();
    }
}
