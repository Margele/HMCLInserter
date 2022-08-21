package cn.margele.oauth;

public class XSTSAuth {
    private final String token, uhs;

    public XSTSAuth(String token, String uhs) {
        this.token = token;
        this.uhs = uhs;
    }

    public String getToken() {
        return token;
    }

    public String getUhs() {
        return uhs;
    }
}
