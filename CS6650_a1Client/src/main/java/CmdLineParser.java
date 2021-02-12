
/**
 * Command Line Parser class
 */
public class CmdLineParser {
  private CmdLineData cmdLineData;

  /**
   * Constructor for the command line parser
   */
  public CmdLineParser() {
    this.cmdLineData = new CmdLineData();
  }

  /**
   * Method that calls validation and setting of command line arguments
   */
  public CmdLineData parseInput(String[] args) throws InvalidArgumentsException {
    int i = 0;
    while (i < args.length) {
      if (args[i].charAt(0) == '-' && cmdLineData.checkValidSetting(args[i].substring(1))) {
        if (args[i].equals("-ip")) {
          this.cmdLineData.setServerPath(args[i + 1]);
        } else {
          this.cmdLineData.setCommandLineDataSetting(args[i].substring(1), args[i + 1]);
        }
          i += 2;
      } else {
        i++;
      }
    }
    return this.cmdLineData;
  }
}
