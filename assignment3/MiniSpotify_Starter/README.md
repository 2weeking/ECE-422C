# MiniSpotify (Assignment #3)

This project refactors and extends the supplied MiniSpotify 0.8 starter. It implements the assignment's catalog, indexed search, users, ordered playlists, collaboration, follows, unique favorites, bounded history, global rankings, and genre-based recommendations. The supplied package and frozen public API are preserved.

Read `Design.pdf` for collection choices, complexities, invariants, and the implementation's main changes.

## Layout

```
src/main/java/edu/utexas/ece422c/minispotify/
  MusicService.java        the public API (frozen; do not change signatures)
  MusicServices.java       grading entry point: MusicServices.create() returns your implementation
  LegacyMusicService.java  the refactored service and validation coordinator
  SongCatalog.java        indexed catalog and case-insensitive search
  SongRanking.java        global counts and bounded-heap ranking
  PlaylistState.java      internal editable playlist and permissions
  Validation.java         shared argument checks
  Song.java, User.java, Playlist.java  immutable songs, account state, playlist snapshots
  CatalogLoader.java       loads songs from a CSV stream
  MiniSpotifyApp.java      command-line demo including collaboration and recommendations
src/main/resources/songs.csv                          8-song sample catalog used by the demo
src/test/java/.../MusicServicePublicTest.java          public example tests (not a complete specification)
src/test/java/.../MusicServiceRequirementsTest.java    functional, edge, error, and performance tests
src/test/java/.../CatalogLoaderTest.java               supplied CSV and loader validation tests
src/test/java/.../MusicServiceAuditTest.java           cross-feature and independent model checks
src/test/resources/minispotify_catalog_100000.csv     100,000-song catalog for your own large-catalog tests
Design.pdf                                             required design document
```

## Building and running

The project is a Maven project targeting Java 17 and JUnit 5. The grader builds your submission with `mvn test` from a clean checkout, so that command must succeed.

```
mvn test                      # compile and run all tests under src/test
mvn -q compile exec:java      # run the MiniSpotifyApp demo
```

IDE: import the folder as a Maven project (IntelliJ: Open the folder; Eclipse: File > Import > Maven > Existing Maven Projects). Use JDK 17.

The suite contains 100 JUnit test cases, including null and unknown-reference errors, playlist permissions, history boundaries, lifetime recommendation exclusions, immutable snapshots, ranking ties, and interleaved multi-user activity checked against an independent model.

The test suite also creates a 500,000-song catalog, performs 100,000 ID lookups and 10,000 artist searches, and prints the elapsed time. It uses a 15-second timeout to allow for slower machines. Returned collections and followed playlist objects are immutable snapshots; fetch again to see later changes.

Comments use the requested `//lowercase explanation | extra detail` format. Each statement occupies its own line, and control-flow braces follow the requested style.

## What is frozen

Hidden tests obtain your implementation only through `MusicServices.create()` and use it only through:

- the `MusicService` interface (do not change any method signature),
- the constructor `Song(String id, String title, String artist, String genre, int durationSeconds)` and the `Song` getters,
- `Playlist.getName()` and `Playlist.getOwner()`.

Everything else, including fields and other methods of `Playlist` and `User`, was eligible for refactoring. `Playlist` has no mutators: service edits operate on internal `PlaylistState` instances and never on objects returned to clients.

## Submission

Submit one ZIP of the complete project (all source files, your JUnit tests, and `Design.pdf` at the project root). Do not include `target/`, IDE folders, or other generated files; the supplied `.gitignore` lists what to leave out.
