# lib_Instrument

## 版本更新
* V1.1.0
```
支持Android10
```
* V1.1.4
```
由于近期非Android10项目中使用的缘故，logger不支持Android10了
```
* V1.1.5
```
日志上传成功后，及时清除日志文件从而抓取新的日志，保证上传的日志不会有重复的部分。
```
* V1.1.6
```
重新整合包名和项目路径。
```
* V1.1.12
```
crash信息全面捕获。
```
* V1.2.2
```
引入thread模块
```
* V1.2.4
```
引入IO模块
```
* V1.2.5
```
引入OOM模块
```
* V1.2.7
```
去掉excel模块
```

## 启动shell运行
* 方式一
```
export CLASSPATH=`pm path com.coocaa.os.perf.monitor`
export CLASSPATH=`pm path com.dunn.instrument`
app_process /system/bin  com.coocaa.os.perf.monitor.ShellMain
app_process /system/bin  com.dunn.instrument.ShellMain
app_process /system/bin  com.coocaa.os.perf.monitor.ShellMain 包名
```
* 方式二
```
在Application MainApp中启用Telnet，Telnet会执行命令运行shell
```

## 后台Service方式启动进程
```
WINDOW command:
open_window
CPU command:
cpu_close、cpu_low、cpu_middle、cpu_high
MEM command:
mem_close、mem_low、mem_middle、mem_high

注意：
使用CPU或MEM前先停止，也即先执行cpu_close或mem_close。否则无效。

单次启动：
am start-foreground-service -a com.coocaa.intent.action.RESOURCE_ACTION --es resource_command open_window
am start-foreground-service -a com.coocaa.intent.action.RESOURCE_ACTION --es resource_command cpu_low

循环10启动：
while true; do am start-foreground-service -a com.coocaa.intent.action.RESOURCE_ACTION --es resource_command cpu_low;sleep 10;done;
```

