标题：Windows 10 网络打印机连接失败常见原因及解决方案

摘要：
当用户在 Windows 10 上无法连接网络打印机时，可能由以下原因导致：

1. **IP 地址变更**  
   打印机 DHCP 分配的 IP 发生变化，旧地址失效。建议设置静态 IP 或使用 DNS 名称。

2. **防火墙阻止打印端口**  
   Windows 防火墙默认阻止 TCP 9100 端口（JetDirect）。需手动放行该端口。

3. **驱动不兼容或损坏**  
   尝试卸载后重新安装最新官方驱动。避免使用通用驱动。

4. **Spooler 服务异常**  
   可通过重启 Print Spooler 服务恢复：
   ```cmd
   net stop spooler
   net start spooler