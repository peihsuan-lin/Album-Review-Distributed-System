import com.google.gson.Gson;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class AlbumReviewConsumer implements Runnable {

  private final Gson gson = new Gson();
  private final DBHandler dbHandler;

  public AlbumReviewConsumer() {
    this.dbHandler = new DBHandler();
  }

  @Override
  public void run() {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "album-review-consumer-group");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
      consumer.subscribe(Arrays.asList("likes-topic", "dislikes-topic"));

      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(100);
        for (ConsumerRecord<String, String> record : records) {
          ReviewMessage reviewMessage = gson.fromJson(record.value(), ReviewMessage.class);
          dbHandler.storeReviewInDb(reviewMessage);
        }
      }
    } catch (Exception e) {
      System.out.println("Kafka Error in Consumer: " + e.getMessage());
      e.printStackTrace();
    }
  }
}