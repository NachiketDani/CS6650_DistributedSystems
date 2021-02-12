import java.util.HashMap;

/**
 * Class to set and store Command Line data
 */
public class CmdLineData {
  private HashMap<String, Integer> commandLineData;
  private String serverPath;
  private String ipAddress;

  /**
   * Constructor to set values to defaults recommended
   */
  public CmdLineData() {
    this.commandLineData = new HashMap<>();
    this.commandLineData.put("maxStores", 10);
    this.commandLineData.put("customersPerStore", 1000);
    this.commandLineData.put("maxItemId",100000);
    this.commandLineData.put("numPurchases", 60);
    this.commandLineData.put("itemsPerPurchase", 5);
    this.commandLineData.put("date", 20210101);
    this.commandLineData.put("portNumber", 8080);
    this.ipAddress = "localhost";
    this.serverPath = "http://"+ this.ipAddress + ":" +
        String.valueOf(this.commandLineData.get("portNumber")) +
        "/CS6650_a1Servlet_war";
  }

  public HashMap<String, Integer> getCommandLineData() {
    return commandLineData;
  }

  public String getServerPath() {
    return serverPath;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  /**
   * Setter method for program settings within Hashmap
   */
  public void setCommandLineDataSetting(String targetSetting, String value) {
    Integer setValue = Integer.valueOf(value);
    this.commandLineData.put(targetSetting, setValue);
  }

  /**
   * Setter method for the server path
   */
  public void setServerPath(String ipAddress) {
    this.serverPath = "http://" + ipAddress + ":" +
        String.valueOf(this.commandLineData.get("portNumber")) + "/CS6650_a1Servlet_war";
  }

  /**
   * Method to validate if command line argument tag is correct
   */
  public boolean checkValidSetting(String targetSetting) {
    return this.commandLineData.keySet().contains(targetSetting)
        || targetSetting.equals("ip");
  }
}
