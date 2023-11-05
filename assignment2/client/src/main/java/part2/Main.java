package part2;

import java.io.IOException;

public class Main {

  public static void main(String[] args) throws InterruptedException, IOException {
    Parser parser = new Parser();
    parser.parse(args);
    MultiThreadClient client = new MultiThreadClient(Parser.getThreadGroupSize(),
        Parser.getNumThreadGroups(), Parser.getDelay(), Parser.getUrl());
    client.execute();

  }


}
