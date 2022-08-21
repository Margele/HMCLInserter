# HMCL Inserter
使用此工具，可以将使用HMCL APPID所获取的Refresh Token直接插入到HMCL的配置文件中。

## 使用方法
建议将本工具放入HMCL同目录下，工具可以自动识别到HMCL的配置文件，则不需要手动选取。
若不放在同目录下则需要手动选取HMCL目录下的配置文件。

选取配置文件后输入由HMCL的APPID所获取的Refresh Token，
在本工具成功登录Minecraft后即可直接将账号写入HMCL启动器。

## 代理
在启动参数中加入`--proxy 代理类型 代理IP 代理端口`即可启用代理。
例如: `--proxy SOCKS 127.0.0.1 7890`

可用代理类型参考Java中`Proxy.Type`，有如下类型：
* DIRECT
* SOCKS
* HTTP