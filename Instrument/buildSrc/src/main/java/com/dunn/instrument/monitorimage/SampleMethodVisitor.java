package com.dunn.instrument.monitorimage;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class SampleMethodVisitor extends AdviceAdapter {
    private String mMethodName;
    public SampleMethodVisitor(MethodVisitor methodVisitor, int access, String methodName, String descriptor) {
        super(Opcodes.ASM5, methodVisitor, access, methodName , descriptor);
        this.mMethodName = methodName;
    }
    /**
     * 这个方法也比较通用
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(mMethodName.equals("onCreate")){
            System.out.println("owner -> "+owner+", name -> "+name+", descriptor -> "+descriptor);
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        super.visitVarInsn(opcode, var);

    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        if(mMethodName.equals("onCreate")){
            System.out.println("visitEnd methodName -> "+mMethodName);
            //在每个方法后面添加一句Log代码  Log.e("TAG","enterMethod");
            // 参数怎么写
            mv.visitLdcInsn("TAG");
            mv.visitLdcInsn("enterMethod");
            // 这里一定是要字节码的方法
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e",
                    "(Ljava/lang/String;Ljava/lang/String;)I", false);
        }
    }
}
