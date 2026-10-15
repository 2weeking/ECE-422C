package edu.utexas.ece422c.minispotify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class CatalogLoader {
    private CatalogLoader() {
    }

    //read the starter's five-column catalog format | close the input after loading
    public static List<Song> load(InputStream input) throws IOException {
        Validation.require(input, "input");
        List<Song> songs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                if (fields.length != 5) {
                    throw new IllegalArgumentException("Expected five columns at line " + lineNumber);
                }
                try {
                    songs.add(new Song(fields[0], fields[1], fields[2], fields[3], Integer.parseInt(fields[4])));
                } catch (IllegalArgumentException exception) {
                    throw new IllegalArgumentException("Invalid song at line " + lineNumber, exception);
                }
            }
        }
        return songs;
    }
}
