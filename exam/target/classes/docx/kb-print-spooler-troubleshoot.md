标题：Print Spooler 服务卡住或崩溃的处理方法

症状：
- 打印任务堆积在队列中
- 提示“正在打印”但无输出
- 添加新打印机时报错 0x000006ba

解决流程：
1. 清除打印队列缓存：
    - 停止 Print Spooler 服务
    - 删除 `C:\Windows\System32\spool\PRINTERS\*` 所有文件
    - 重启服务

2. 注册表修复（仅限高级管理员）：
    - 打开 regedit
    - 导航至 `HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Services\Spooler`
    - 确保 `ImagePath` 为 `%systemroot%\System32\spoolsv.exe`

3. 替代方案：启用客户端点打印（Client-Side Rendering）
    - 组策略开启：`Computer Configuration > Administrative Templates > Printers > Enable Client Side Rendering`
    - 可绕过服务器端 Spooler 故障