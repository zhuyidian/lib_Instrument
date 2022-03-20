# buildSrc


## 1.插件monitorimage监听图片加载告警
* 1，插件名
```xml
com.dunn.instrument.monitorimage
```
* 2，插件引用
```xml
app的build.gradle中

apply plugin: 'com.dunn.instrument.monitorimage'
```
* 3，传参到插件
```xml
app的build.gradle中

// 配置参数
tinkerPatch {
    oldApk = "oldApk"
}
```
* 4，使用MonitorImageView
```xml
MonitorImageView中做了监听告警
```
* 5, 原理说明
```xml
Gradle Plugin插件插入一个Transform，Transform中使用MonitorImageClassVisitor监听和拦截所有的ImageView，
将ImageView设置图片的方法全部进入到自定义的ImageView中，再在自定义ImageView中处理图片加载报警提示。
注：Transform是在合成dex前修改Class字节码
```
