package com.dunn.instrument.monitorimage;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class MonitorImageClassVisitor extends ClassVisitor {
    public MonitorImageClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    /**
     * 访问类会进入这里
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println("MonitorImageClass ---> visit"+", name="+name+", superName="+superName);
        //这里将系统的ImageView拦截到自定的ImageView，所有的ImageView设置图片的方法都会在自定义的ImageView中执行
        if(superName.equals("android/widget/ImageView")
                && !name.equals("com/darren/optimize/day04/MonitorImageView")){
            superName = "com/darren/optimize/day04/MonitorImageView";
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }
}
