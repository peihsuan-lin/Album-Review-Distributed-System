package part2;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Parser {
  @Option(name = "-threadGroupSize", required = true, usage = "Size of each thread group")
  private static int threadGroupSize;

  @Option(name = "-numThreadGroups", required = true, usage = "Number of thread groups")
  private static int numThreadGroups;

  @Option(name = "-delay", required = true, usage = "Delay in milliseconds")
  private static int delay;

  @Option(name = "-url", required = true, usage = "Server URL")
  private static String url;


  public void parse(String[] args){
      CmdLineParser parser = new CmdLineParser(this);
      try {
        parser.parseArgument(args);
      } catch (CmdLineException e) {
        System.err.println(e.getMessage());
        parser.printUsage(System.err);
      }
    }

  public static int getThreadGroupSize() {
    return threadGroupSize;
  }

  public static int getNumThreadGroups() {
    return numThreadGroups;
  }

  public static int getDelay() {
    return delay;
  }

  public static String getUrl() {
    return url;
  }
}