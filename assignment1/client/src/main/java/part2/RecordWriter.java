package part2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class RecordWriter {

  private static RecordWriter instance;
  private static BufferedWriter writer;

  private RecordWriter() {
    try {
      writer = new BufferedWriter(new FileWriter("records.csv"));
      writer.write("startTime,requestType,latency,responseCode\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static synchronized RecordWriter getInstance() {
    if (instance == null) {
      instance = new RecordWriter();
    }
    return instance;
  }

  public static void calculateOutput(ConcurrentLinkedQueue<Record> records) throws IOException {
    if (records.isEmpty()) {
      throw new IllegalArgumentException("No records to calculate");
    }
    List<Record> recordList = new ArrayList<>(records);
    Collections.sort(recordList, Comparator.comparingDouble(Record::getLatency));
    List<Record> postRecords = recordList.stream().filter(r -> "POST".equals(r.getRequestType()))
        .collect(
            Collectors.toList());
    List<Record> getRecords = recordList.stream().filter(r -> "GET".equals(r.getRequestType()))
        .collect(Collectors.toList());
    outputMetrics("GET", getRecords);
    outputMetrics("POST", postRecords);
    close();

  }

  public static void close() throws IOException {
    if (writer != null) {
      writer.close();
    }
  }

  private static void outputMetrics(String requestType, List<Record> records) throws IOException {
    if (records.isEmpty()) {
      throw new IllegalArgumentException("No records to calculate");
    }
    double min = Double.MAX_VALUE, max = Double.MIN_VALUE, sum = 0;
    double median = records.get((int) (0.5 * records.size())).getLatency();
    double p99 = records.get((int) (0.99 * records.size())).getLatency();

    for (Record r : records) {
      sum += r.getLatency();
      max = Math.max(max, r.getLatency());
      min = Math.min(min, r.getLatency());
      writer.write(r.toString() + "\n");
    }
    writer.flush();
    double mean = sum / records.size();

    System.out.println("\nMetrics for " + requestType + ":");
    System.out.println(
        "Mean response time: " + mean + " ms" + "\n"
            + "Median response time: " + median + " ms" + "\n"
            + "99th response time: " + p99 + " ms" + "\n"
            + "Min response time: " + min + " ms" + "\n"
            + "Max response time: " + max + " ms"
    );
  }
}
