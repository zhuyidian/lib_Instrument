# lib_logger

## 注解使用
* 1，在logger需要初始化的地方
```xml
@InitJointPoint(mFilePath = "",mFileName = "logger_cache")
mFilePath：日志缓存的相对路径
mFileName：日志缓存文件名
```
* 2，在logger需要释放的地方
```xml
@ReleaseJointPoint
```
* 3，在log日志收集的地方埋点
```xml
@LogJointPoint(type = "MSG",open = true)
type：日志类型
open：是否开启收集
```
* 4，在log文件上传的地方
```xml
@UploadJointPoint
举例：
public <T> void uploadLogger(T value,String url);
初始化两个参数：
value：true(上传使能)  false(禁止上传) 
url：上传服务器地址
参数变动：
value：从T--->File(转变成需要上传的文件)
url：不变
```
## 项目引用
```groovy
root build.gradle中
classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.8'
module build.gradle中
apply plugin: 'android-aspectjx'
implementation 'com.github.zhuyidian:lib_logger:V1.0.0'
```
