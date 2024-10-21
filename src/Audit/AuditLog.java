package Audit;

import java.io.Serializable;

public class AuditLog implements Serializable {
    private final String configName;
    private final String userId;
    private final int port;
    private final long[] hlcTimestamp;
    private final String newValue;

    public AuditLog(String configName, String userId, int port, long[] hlcTimestamp, String newValue) {
        this.configName = configName;
        this.userId = userId;
        this.port = port;
        this.hlcTimestamp = hlcTimestamp;
        this.newValue = newValue;
    }

    public int getPort() {
        return port;
    }
    public String getUserId() {
        return userId;
    }

    public void logConfigurationChange() {
        System.out.println(
                "Audit Log: Configuration Changed: " + configName +
                ", User ID: " + userId +
                ", Timestamp: [" + hlcTimestamp[0] + "," + hlcTimestamp[1] + "]" +
                ", New Value: " + newValue
        );
    }
}