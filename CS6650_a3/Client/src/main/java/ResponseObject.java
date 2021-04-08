import java.util.Objects;

public class ResponseObject {
  private Long tStart;
  private Long tEnd;
  private Integer responseCode;
  private String responseType;
  private Long latency;

  public ResponseObject(Long tStart, Long tEnd, Integer responseCode, String responseType) {
    this.tStart = tStart;
    this.tEnd = tEnd;
    this.responseCode = responseCode;
    this.responseType = responseType;
    this.latency = tEnd - tStart;
  }

  /**
   * Private constructor
   */
  protected ResponseObject(){
    this.tStart = 0L;
    this.tEnd = 0L;
    this.responseCode = 0;
    this.responseType = "POST";
  }

  public Long getTStart() {
    return tStart;
  }

  public void setTStart(Long tStart) {
    this.tStart = tStart;
  }

  public Long getTEnd() {
    return tEnd;
  }

  public void setTEnd(Long tEnd) {
    this.tEnd = tEnd;
  }

  public Integer getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(Integer responseCode) {
    this.responseCode = responseCode;
  }

  public String getResponseType() {
    return responseType;
  }

  public Long getLatency() {
    return latency;
  }

  public void setLatency(Long latency) {
    this.latency = latency;
  }

  public void setResponseType(String responseType) {
    this.responseType = responseType;
  }

  /**
   * To String method
   * @return string representation of the Response object
   */
  @Override
  public String toString() {
    return tStart + "," + responseType + "," + latency + "," + responseCode;
  }

  /**
   * Equals method for Response Object
   * @param o Object to be compared to
   * @return Boolean depending if objects are equal or not
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ResponseObject)) {
      return false;
    }
    ResponseObject that = (ResponseObject) o;
    return Objects.equals(tStart, that.tStart) && Objects.equals(tEnd, that.tEnd)
        && Objects.equals(getResponseCode(), that.getResponseCode()) && Objects
        .equals(getResponseType(), that.getResponseType()) && Objects
        .equals(getLatency(), that.getLatency()) ;
  }

  /**
   * Hashcode method
   * @return hashcode for the response object
   */
  @Override
  public int hashCode() {
    return Objects
        .hash(tStart, tEnd, getResponseCode(), getResponseType(), getLatency());
  }
}
