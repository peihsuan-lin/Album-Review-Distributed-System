package part1;

public class Main {

  public static void main(String[] args) throws InterruptedException {
    System.out.println("Java Server");
    Parser parser = new Parser();
    parser.parse(args);
    MultiThreadClient client = new MultiThreadClient(Parser.getThreadGroupSize(),
        Parser.getNumThreadGroups(), Parser.getDelay(), Parser.getUrl());
    client.execute();

  }

}
