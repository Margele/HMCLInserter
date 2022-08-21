package cn.margele.oauth;

public class MinecraftProfile {
    private String username, uuid, accessToken;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String toString() {
        return "MinecraftProfile{" +
                "username='" + username + '\'' +
                ", uuid='" + uuid + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}
