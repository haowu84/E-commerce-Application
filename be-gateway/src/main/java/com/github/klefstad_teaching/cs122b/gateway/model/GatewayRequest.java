package com.github.klefstad_teaching.cs122b.gateway.model;

import java.time.Instant;

public class GatewayRequest {
    private Integer id;
    private String ipAddress;
    private Instant callTime;
    private String path;

    public Integer getId() {
        return id;
    }

    public GatewayRequest setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public GatewayRequest setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public Instant getCallTime() {
        return callTime;
    }

    public GatewayRequest setCallTime(Instant callTime) {
        this.callTime = callTime;
        return this;
    }

    public String getPath() {
        return path;
    }

    public GatewayRequest setPath(String path) {
        this.path = path;
        return this;
    }
}
