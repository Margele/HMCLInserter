package cn.margele.hmclInserter;

import cn.margele.oauth.MinecraftProfile;
import cn.margele.oauth.OAuth;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

public class HMCLInserter {
    public static Proxy proxy = Proxy.NO_PROXY;

    public static void main(String[] args) {
        try {
            // 代理启用
            if (args.length >= 4 && args[0].equals("--proxy")) {

                proxy = new Proxy(
                        Proxy.Type.valueOf(args[1].toUpperCase(Locale.ROOT)),
                        new InetSocketAddress(args[2], Integer.parseInt(args[3]))
                );

                JOptionPane.showMessageDialog(
                        null,
                        "代理已启用: " + proxy,
                        "代理启用",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            // 在同目录中寻找HMCL配置文件。
            File launcherConfigFile = new File("hmcl.json");

            // 若同目录下没有HMCL配置文件的逻辑处理。
            if (!launcherConfigFile.exists()) {
                JFileChooser chooser = new JFileChooser();

                chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());

                // 添加文件筛选器，限定文件名为hmcl.json
                chooser.addChoosableFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.getName().equals("hmcl.json");
                    }

                    @Override
                    public String getDescription() {
                        return "HMCL配置文件 (hmcl.json)";
                    }
                });

                // 打开文件选择器
                chooser.showOpenDialog(null);

                // 处理文件
                File selectedFile = chooser.getSelectedFile();
                if (selectedFile != null) {
                    launcherConfigFile = selectedFile;
                } else {
                    throw new Exception("未选中文件！");
                }
            }

            // 弹窗提示输入Refresh Token
            String originRefreshToken =
                    JOptionPane.showInputDialog(
                            null,
                            "输入HMCL RefreshToken",
                            ""
                    );

            if (originRefreshToken == null) {
                throw new Exception("未输入Refresh Token！");
            }

            // 读入启动器配置
            JSONObject launcherConfig = JSONObject.parseObject(
                    FileUtils.readFileToString(
                            launcherConfigFile,
                            StandardCharsets.UTF_8
                    )
            );

            LoggingPanel panel = new LoggingPanel();

            // 微软登录
            OAuth oAuth = new OAuth(originRefreshToken, panel);
            MinecraftProfile profile = oAuth.getProfile();

            // 初始化字段
            String uuid         = profile.getUuid();
            String displayName  = profile.getUsername();
            String accessToken  = profile.getAccessToken();
            String refreshToken = oAuth.getRefreshToken();
            String userid       = UUID.randomUUID().toString();

            // 读入账号数组
            JSONArray accounts = launcherConfig.getJSONArray("accounts");

            // 新建新账号
            JSONObject newAccount = new JSONObject();

            newAccount.put("uuid", uuid);
            newAccount.put("displayName", displayName);
            newAccount.put("tokenType", "Bearer");
            newAccount.put("accessToken", accessToken);
            newAccount.put("refreshToken", refreshToken);
            newAccount.put("notAfter", 0);
            newAccount.put("userid", userid);
            newAccount.put("type", "microsoft");

            // 将新的账号插入账号数组
            accounts.add(newAccount);

            // 写回文件
            if (launcherConfigFile.delete()) {
                panel.log("删除成功");

                FileUtils.writeStringToFile(
                        launcherConfigFile,
                        JSONObject.toJSONString(launcherConfig, true),
                        StandardCharsets.UTF_8
                );
            } else {
                throw new Exception("删除原文件失败！");
            }

            JOptionPane.showMessageDialog(
                    null,
                    "插入账号成功！\n用户名: " + displayName,
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE
            );

            System.exit(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "处理时发生错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );

            System.exit(-1);
        }
    }
}
