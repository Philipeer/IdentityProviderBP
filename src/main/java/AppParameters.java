import java.io.Serializable;

public class AppParameters implements Serializable {
    public String message;
    private String userKey;
    private byte[] ATU;
    private String hatu;

    public AppParameters() {
    }
    public AppParameters(String message) {
        this.message = message;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public byte[] getATU() {
        return ATU;
    }

    public void setATU(byte[] ATU) {
        this.ATU = ATU;
    }

    public String getHatu() {
        return hatu;
    }

    public void setHatu(String hatu) {
        this.hatu = hatu;
    }
}
