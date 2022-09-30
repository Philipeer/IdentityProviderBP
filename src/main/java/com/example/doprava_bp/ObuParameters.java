package com.example.doprava_bp;

import java.io.Serializable;

public class ObuParameters implements Serializable {
    public String message;
    private String driverKey;
    private int idr;

    public ObuParameters() {
    }

    public ObuParameters(String message) {
        this.message = message;
    }

    public String getDriverKey() {
        return driverKey;
    }

    public void setDriverKey(String driverKey) {
        this.driverKey = driverKey;
    }

    public int getIdr() {
        return idr;
    }

    public void setIdr(int idr) {
        this.idr = idr;
    }
}
