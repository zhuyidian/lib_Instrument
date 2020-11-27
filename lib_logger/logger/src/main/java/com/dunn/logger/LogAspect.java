package com.dunn.logger;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.File;
import java.lang.reflect.Modifier;

/**
 * Created by dunn
 * 切面
 */
@Aspect
public class LogAspect {
    /**
     * 找到处理的切点
     * * *(..)  可以处理所有的方法
     */
    @Pointcut("execution(@com.dunn.logger.InitJointPoint * *(..))")
    public void pointcutInitMethod() {

    }

    /**
     * 找到处理的切点
     * * *(..)  可以处理所有的方法
     */
    @Pointcut("execution(@com.dunn.logger.ReleaseJointPoint * *(..))")
    public void pointcutReleaseMethod() {

    }

    /**
     * 找到处理的切点
     * * *(..)  可以处理所有的方法
     */
    @Pointcut("execution(@com.dunn.logger.UploadJointPoint * *(..))")
    public void pointcutUploadMethod() {

    }

    /**
     * 找到处理的切点
     * * *(..)  可以处理所有的方法
     */
    @Pointcut("execution(@com.dunn.logger.LogJointPoint * *(..))")
    public void pointcutLogMethod() {

    }

    /**
     * 完全替代
     */
    @Around("pointcutInitMethod()")
    public Object aroundInitMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        //切入的目标方法名
        String methodName = joinPoint.getSignature().getName();
        //切入的目标类名(简称)
        String simpleClassName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        //切入的目标类名(全路径)
        String className = joinPoint.getSignature().getDeclaringTypeName();
        //切入的目标方法生命类型
        String modifiers = Modifier.toString(joinPoint.getSignature().getModifiers());
        //获取注解类
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        InitJointPoint initJointPoint = methodSignature.getMethod().getAnnotation(InitJointPoint.class);
        LogConfig.DEBUG = initJointPoint.isDebug();
        if (LogConfig.DEBUG) {
            Log.v("logger[", "LogAspect ----init before----methodName=" + methodName + "\n" +
                    ", simpleClassName=" + simpleClassName + "\n" +
                    ", className=" + className + "\n" +
                    ", modifiers=" + modifiers + "\n" +
                    ", 注解属性mFilePath=" + initJointPoint.mFilePath() + "\n" +
                    ", 注解属性mFileName=" + initJointPoint.mFileName() + "\n" +
                    ", 注解属性isDebug=" + initJointPoint.isDebug());
        }

        String mPath = initJointPoint.mFilePath();
        String mName = initJointPoint.mFileName();
        LoggerManager.L().init(Environment.getExternalStorageDirectory().getAbsolutePath() + mPath, mName);

        //继续执行切入的原方法
        Object object = joinPoint.proceed();
        //在切入的方法之后执行
        if (LogConfig.DEBUG) Log.v("logger[", "LogAspect ----init after----");
        return object;
    }

    /**
     * 完全替代
     */
    @Around("pointcutReleaseMethod()")
    public Object aroundReleaseMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        //切入的目标方法名
        String methodName = joinPoint.getSignature().getName();
        //切入的目标类名(简称)
        String simpleClassName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        //切入的目标类名(全路径)
        String className = joinPoint.getSignature().getDeclaringTypeName();
        //切入的目标方法生命类型
        String modifiers = Modifier.toString(joinPoint.getSignature().getModifiers());
        //获取注解类
        //MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //InitJointPoint initJointPoint = methodSignature.getMethod().getAnnotation(InitJointPoint.class);
        if(LogConfig.DEBUG) {
            Log.v("logger[", "LogAspect ----release before----methodName=" + methodName + "\n" +
                    ", simpleClassName=" + simpleClassName + "\n" +
                    ", className=" + className + "\n" +
                    ", modifiers=" + modifiers);
        }
        LoggerManager.L().release();

        //继续执行切入的原方法
        Object object = joinPoint.proceed();
        //在切入的方法之后执行
        if(LogConfig.DEBUG)Log.v("logger[", "LogAspect ----release after----");
        return object;
    }

    /**
     * 完全替代
     */
    @Around("pointcutUploadMethod()")
    public Object aroundUploadMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        //切入的目标方法名
        String methodName = joinPoint.getSignature().getName();
        //切入的目标类名(简称)
        String simpleClassName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        //切入的目标类名(全路径)
        String className = joinPoint.getSignature().getDeclaringTypeName();
        //切入的目标方法生命类型
        String modifiers = Modifier.toString(joinPoint.getSignature().getModifiers());
        //获取注解类
        //MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //InitJointPoint initJointPoint = methodSignature.getMethod().getAnnotation(InitJointPoint.class);
        if (LogConfig.DEBUG) {
            Log.v("logger[", "LogAspect ----upload before----methodName=" + methodName + "\n" +
                    ", simpleClassName=" + simpleClassName + "\n" +
                    ", className=" + className + "\n" +
                    ", modifiers=" + modifiers);
        }

        //切入的目标方法参数
        Object args[] = joinPoint.getArgs();
        if(args!=null){
            if (LogConfig.DEBUG)Log.v("logger[", "LogAspect ----upload before---- args0="+args[0]+", args1="+args[1]);
            boolean isUpload = false;
            if(args[0] instanceof Boolean){
                isUpload = (boolean)args[0];
            }
            if(isUpload==true){  //需要上传
                final File fileZip = LoggerManager.L().copyfile(true);
                if(fileZip!=null && fileZip.exists()){
                    final String url = (String)args[1];
                    if(url!=null && !url.isEmpty()){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpUtils.getInstance().uploadLog(url,fileZip);
                            }
                        }).start();
                    }
                    //继续执行切入的原方法
                    Object object = joinPoint.proceed(new Object[]{fileZip});
                    if (LogConfig.DEBUG)Log.v("logger[", "LogAspect ----upload after----");
                    return object;
                }
            }
        }

        //继续执行切入的原方法
        Object object = joinPoint.proceed();
        //在切入的方法之后执行
        if (LogConfig.DEBUG)Log.v("logger[", "LogAspect ----upload after----");
        return object;
    }

    /**
     * 完全替代
     */
    @Around("pointcutLogMethod()")
    public Object aroundLogMethod(ProceedingJoinPoint joinPoint) throws Throwable {
//        //切入的目标方法名
//        String methodName = joinPoint.getSignature().getName();
//        //切入的目标类名(简称)
//        String simpleClassName = joinPoint.getSignature().getDeclaringType().getSimpleName();
//        //切入的目标类名(全路径)
//        String className = joinPoint.getSignature().getDeclaringTypeName();
//        //切入的目标方法生命类型
//        String modifiers = Modifier.toString(joinPoint.getSignature().getModifiers());
        //获取注解类
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        LogJointPoint logJointPoint = methodSignature.getMethod().getAnnotation(LogJointPoint.class);
//        Log.v("logger[", "LogAspect ----log before----methodName="+methodName+"\n" +
//                ", simpleClassName="+simpleClassName+"\n"+
//                ", className="+className+"\n"+
//                ", modifiers="+modifiers+"\n"+
//                ", 注解属性type="+logJointPoint.type()+"\n"+
//                ", 注解属性open="+logJointPoint.open());

        if(logJointPoint.open()==true){
            //切入的目标方法参数
            Object args[] = joinPoint.getArgs();
            if(args!=null){
//                Log.v("logger[", "LogAspect ----log before---- msg="+((String) args[0]));
                LoggerManager.L().writeTo((String) args[0]);
//            for (int i=0;i<args.length;i++) {
//                Log.v("logger[", "LogAspect ----0--第"+i+"个参数为："+args[i]);
//            }
            }
        }

//        // 1.获取 CheckNet 注解
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();  //获得包名
//        String className = joinPoint.getThis().getClass().getSimpleName();
//        Log.v("logger[", "aroundLogMethod ----start----0--class="+className+", method="+signature.getName());
//        LogJointPoint logJointPoint = signature.getMethod().getAnnotation(LogJointPoint.class);
//        if (logJointPoint != null) {
//            // 2.判断有没有网络  怎么样获取 context?
//            Object object = joinPoint.getThis();// View Activity Fragment ； getThis() 当前切点方法所在的类
//            Context context = getContext(object);
//            if(true){
//                // 3.没有网络不要往下执行
//                Toast.makeText(context,"test",Toast.LENGTH_LONG).show();
//                //Log.v("logger[", "aroundLogMethod ----return null----");
//                //return null;
//            }
//        }
//        Log.v("logger[", "aroundLogMethod ----return joinPoint.proceed----0");

        //继续执行切入的原方法
        Object object = joinPoint.proceed();
        //在切入的方法之后执行
        //Log.v("logger[", "LogAspect ----log after----");
        return object;
    }

    /**
     * 前置操作
     *
     * @param point 切入点
     */
    @Before("pointcutLogMethod()")
    public void beforeLogMethod(JoinPoint point) throws Throwable{
        //Log.i("logger[", "LogAspect ----before----" + point.getSignature());
    }

    @After("pointcutLogMethod()")
    public void afterLogMethod(JoinPoint point) throws Throwable {
        //Log.i("logger[", "LogAspect ----after----" + point.getSignature());
    }

    /**
     * 后置操作
     */
    @AfterReturning("pointcutLogMethod()")
    public void afterReturnLogMethod() {
        //Log.i("logger[", "LogAspect ----after return----");
    }

    /**
     * 通过对象获取上下文
     *
     * @param object
     * @return
     */
    private Context getContext(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            return fragment.getActivity();
        } else if (object instanceof View) {
            View view = (View) object;
            return view.getContext();
        }
        return null;
    }
}
