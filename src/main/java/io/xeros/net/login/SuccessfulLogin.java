package io.xeros.net.login;

public class SuccessfulLogin {
    private final String ip;
    private final String mac;
    private final String uuid;

    public SuccessfulLogin(String ip, String mac, String uuid) {
        this.ip = ip;
        this.mac = mac;
        this.uuid = uuid;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    public String getUuid() {
        return uuid;
    }
}
