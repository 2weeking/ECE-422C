package edu.utexas.ece422c.minispotify;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Predicate;

final class SongRanking {
    private final Map<String, Long> playCounts = new HashMap<>();
    private final Comparator<Song> bestFirst = Comparator
            .<Song>comparingLong(song -> playCounts.getOrDefault(song.getId(), 0L))
            .reversed()
            .thenComparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(Song::getId);

    void recordPlay(Song song) {
        playCounts.merge(song.getId(), 1L, Long::sum);
    }

    List<Song> select(Iterable<Song> candidates, int n, Predicate<Song> include) {
        if (n == 0) {
            return List.of();
        }
        //keep only the best n candidates | heap head is the worst retained song
        PriorityQueue<Song> selected = new PriorityQueue<>(bestFirst.reversed());
        for (Song song : candidates) {
            if (!include.test(song)) {
                continue;
            }
            if (selected.size() < n) {
                selected.add(song);
            } else if (bestFirst.compare(song, selected.peek()) < 0) {
                selected.remove();
                selected.add(song);
            }
        }
        List<Song> result = new ArrayList<>(selected);
        result.sort(bestFirst);
        return List.copyOf(result);
    }
}
