package cn.margele.oauth;

import cn.margele.hmclInserter.HMCLInserter;
import cn.margele.hmclInserter.LoggingPanel;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.OkHttps;

public class OAuth {
    private static final HTTP http = HTTP.builder().config(builder -> builder.proxy(HMCLInserter.proxy)).build();
    private static final String APP_ID = "6a3728d6-27a3-4180-99bb-479895b8f88e";

    private final MinecraftProfile profile = new MinecraftProfile();;
    private final String refreshToken;

    public MinecraftProfile getProfile() {
        return profile;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public OAuth(String refreshToken, LoggingPanel panel) throws AuthException {
        try {
            LiveAuth liveAuth = liveAuth(refreshToken);
            panel.log("登录Live成功");
            this.refreshToken = liveAuth.getRefreshToken();
            String xBoxToken = xBoxAuth(liveAuth.getAccessToken());
            panel.log("登录XBox成功");
            XSTSAuth xstsAuth = XSTSAuth(xBoxToken);
            panel.log("登录XSTS成功");
            this.minecraftAuth(xstsAuth.getToken(), xstsAuth.getUhs());
            panel.log("登录Minecraft成功");
            this.obtainUUID();
            panel.log("获取UUID成功");
        } catch (Exception e) {
            throw new AuthException(e.getMessage());
        }
    }

    public void obtainUUID() throws AuthException {
        HttpResult.Body authorization = http.sync("https://api.minecraftservices.com/minecraft/profile")
                .addHeader("Authorization", "Bearer " + profile.getAccessToken())
                .get()
                .getBody();

        JSONObject result = JSONObject.parseObject(authorization.toString());

        if (result != null && result.containsKey("id") && result.containsKey("name")) {
            this.profile.setUuid(result.getString("id"));
            this.profile.setUsername(result.getString("name"));
        } else {
            throw new AuthException("获取UUID失败！");
        }
    }

    public void minecraftAuth(String xbl_token, String uhs) throws AuthException {
        JSONObject obj = new JSONObject();
        obj.put("identityToken", "XBL3.0 x=" + uhs + ";" + xbl_token);

        HttpResult.Body body = http.sync("https://api.minecraftservices.com/authentication/login_with_xbox")
                .bodyType(OkHttps.JSON)
                .setBodyPara(obj.toString())
                .post()
                .getBody();

        JSONObject result = JSONObject.parseObject(body.toString());

        if (result != null && result.containsKey("access_token")) {
            this.profile.setAccessToken(result.getString("access_token"));
        } else {
            throw new AuthException("登录Minecraft失败！");
        }
    }

    public XSTSAuth XSTSAuth(String xbl_token) throws AuthException {
        JSONObject obj = new JSONObject();
        JSONObject properties = new JSONObject();
        JSONArray arr = new JSONArray();

        properties.put("SandboxId", "RETAIL");
        arr.add(xbl_token);
        properties.put("UserTokens", arr);

        obj.put("Properties", properties);
        obj.put("RelyingParty", "rp://api.minecraftservices.com/");
        obj.put("TokenType", "JWT");

        HttpResult.Body body = http.sync("https://xsts.auth.xboxlive.com/xsts/authorize")
                .bodyType(OkHttps.JSON)
                .setBodyPara(obj.toString())
                .post()
                .getBody();

        JSONObject result = JSONObject.parseObject(body.toString());

        if (result != null) {
            return new XSTSAuth(result.getString("Token"), result.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs"));
        } else {
            throw new AuthException("登录XSTS失败！");
        }
    }

    public LiveAuth liveAuth(String refresh_token) throws AuthException {
        HttpResult.Body body = http.sync("https://login.live.com/oauth20_token.srf")
                .addBodyPara("client_id", APP_ID)
                .addBodyPara("refresh_token", refresh_token)
                .addBodyPara("grant_type", "refresh_token")
                .addBodyPara("scope", "XboxLive.signin offline_access")
                .post()
                .getBody();

        JSONObject obj = JSONObject.parseObject(body.toString());

        if (obj != null && obj.containsKey("access_token") && obj.containsKey("refresh_token")) {
            return new LiveAuth(obj.getString("refresh_token"), obj.getString("access_token"));
        } else {
            throw new AuthException("登录Live失败！");
        }
    }

    public String xBoxAuth(String accessToken) throws AuthException {
        JSONObject obj = new JSONObject();
        JSONObject properties = new JSONObject();
        properties.put("AuthMethod", "RPS");
        properties.put("SiteName", "user.auth.xboxlive.com");
        properties.put("RpsTicket", "d=" + accessToken);
        obj.put("Properties", properties);
        obj.put("RelyingParty", "http://auth.xboxlive.com");
        obj.put("TokenType", "JWT");

        HttpResult.Body body = http.sync("https://user.auth.xboxlive.com/user/authenticate")
                .bodyType(OkHttps.JSON)
                .setBodyPara(obj.toString())
                .post()
                .getBody();

        JSONObject result = JSONObject.parseObject(body.toString());

        if (result != null && result.containsKey("Token")) {
            return result.getString("Token");
        } else {
            throw new AuthException("登录XBox失败！");
        }
    }
}
