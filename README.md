# HMCL Inserter
使用此工具，可以将使用HMCL APPID所获取的Refresh Token直接插入到HMCL的配置文件中。

## 代理
在启动参数中加入`--proxy 代理类型 代理IP 代理端口`即可启用代理。
例如: `--proxy SOCKS 127.0.0.1 7890`

可用代理类型参考Java中`Proxy.Type`，有如下类型：
* DIRECT
* SOCKS
* HTTP