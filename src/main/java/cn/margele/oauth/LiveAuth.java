package cn.margele.oauth;

public class LiveAuth {
    private final String refresh_token, access_token;

    public LiveAuth(String refresh_token, String access_token) {
        this.refresh_token = refresh_token;
        this.access_token = access_token;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public String getAccessToken() {
        return access_token;
    }
}
