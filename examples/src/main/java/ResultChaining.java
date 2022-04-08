import dev.kylesilver.result.Result;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * This example outlines some hypothetical utility functions for a Kafka library
 */
public class ResultChaining {

    public static Result<Map<TopicPartition, Long>, Exception> lag(Consumer<?, ?> consumer) {
        return Result.tryOr(consumer::assignment, Exception.class)
                .andThen(assignment -> Result.tryOr(() -> consumer.committed(assignment), Exception.class))
                .andThen(committed -> {
                    var assignment = committed.keySet();
                    return Result.tryOr(() -> consumer.endOffsets(assignment), Exception.class).map(ends -> calculateLag(committed, ends));
                });
    }

    private static Map<TopicPartition, Long> calculateLag(
            Map<TopicPartition, OffsetAndMetadata> committed,
            Map<TopicPartition, Long> ends
    ) {
        return committed.entrySet()
                .stream()
                .map(entry -> {
                    var key = entry.getKey();
                    var offset = entry.getValue().offset();
                    var end = ends.getOrDefault(key, offset);
                    var lag = Math.max(end - offset, 0);
                    return Map.entry(key, lag);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
