package net.minecraftforge.eventbus;

import net.minecraftforge.eventbus.api.FunctionalListener;
import net.minecraftforge.eventbus.api.IFunctionalEvent;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FIBasedWithoutResultGenerator implements Opcodes {

    public static ClassNode generate(Class<? extends IFunctionalEvent<?>> type, Method method, IEventListenerFactory factory) throws Exception {
        System.out.println("Without hi!");
        ClassNode classWriter = new ClassNode();
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;

        final String eventInternal = Type.getInternalName(type);
        final String eventDesc = Type.getType(type).getDescriptor();
        final String funcListenerDesc = Type.getType(FunctionalListener.class).getDescriptor();
        final String funcEventDesc = Type.getType(IFunctionalEvent.class).getDescriptor();
        final String funcListenerInternal = Type.getType(FunctionalListener.class).getInternalName();
        final String genName = factory.getUniqueName(type).replace('.', '/');

        classWriter.visitSource("hi.dynamic", null);
        classWriter.visit(60, ACC_PUBLIC | ACC_SUPER, genName, null, "java/lang/Object", new String[] {Type.getInternalName(type)});
        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL, "listeners", "[" + funcListenerDesc, "[" + funcListenerDesc + "<" + eventDesc + ">;", null);
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "([" + funcListenerDesc + ")V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(9, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(11, label2);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitFieldInsn(PUTFIELD, genName, "listeners", "[" + funcListenerDesc);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(12, label3);
            methodVisitor.visitInsn(RETURN);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLocalVariable("this", "L" + genName + ";", null, label0, label4, 0);
            methodVisitor.visitLocalVariable("listeners", "[" + funcListenerDesc, "[" + funcListenerDesc + "<" + eventDesc + ">;", label0, label4, 2);
            methodVisitor.visitMaxs(1, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, null);
            methodVisitor.visitCode();
            int varAmount = 1 + Arrays.stream(method.getParameterTypes()).mapToInt(it -> Type.getType(it).getSize()).sum();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(11, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, genName, "listeners", "[" + funcListenerDesc);
            methodVisitor.visitVarInsn(ASTORE, varAmount);
            methodVisitor.visitVarInsn(ALOAD, varAmount);
            methodVisitor.visitInsn(ARRAYLENGTH);
            methodVisitor.visitVarInsn(ISTORE, varAmount + 1);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ISTORE, varAmount + 2);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitFrame(Opcodes.F_APPEND,3, new Object[] {"[" + funcListenerDesc, Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
            methodVisitor.visitVarInsn(ILOAD, varAmount + 2);
            methodVisitor.visitVarInsn(ILOAD, varAmount + 1);
            Label label2 = new Label();
            methodVisitor.visitJumpInsn(IF_ICMPGE, label2);
            methodVisitor.visitVarInsn(ALOAD, varAmount);
            methodVisitor.visitVarInsn(ILOAD, varAmount + 2);
            methodVisitor.visitInsn(AALOAD);
            methodVisitor.visitVarInsn(ASTORE, varAmount + 3);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(12, label3);
            methodVisitor.visitVarInsn(ALOAD, varAmount + 3);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, funcListenerInternal, "listener", "()" + funcEventDesc, false);
            methodVisitor.visitTypeInsn(CHECKCAST, eventInternal);
            {
                int invokeidx = 1;
                for (int i = 0; i < method.getParameterCount(); i++) {
                    final Type t1 = Type.getType(method.getParameterTypes()[i]);
                    methodVisitor.visitVarInsn(t1.getOpcode(ILOAD), invokeidx);
                    invokeidx += t1.getSize();
                }
            }
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, eventInternal, method.getName(), Type.getMethodDescriptor(method), true);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(11, label4);
            methodVisitor.visitIincInsn(varAmount + 2, 1);
            methodVisitor.visitJumpInsn(GOTO, label1);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(14, label2);
            methodVisitor.visitFrame(Opcodes.F_CHOP,3, null, 0, null);
            methodVisitor.visitInsn(RETURN);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLocalVariable("listener", funcListenerDesc, null, label3, label4, varAmount + 3);
            methodVisitor.visitLocalVariable("this", "L" + genName + ";", null, label0, label5, 0);
            int start = 1;
            for (int i = 0; i < method.getParameterCount(); i++) {
                start++;
                methodVisitor.visitLocalVariable("arg" + start, Type.getDescriptor(method.getParameterTypes()[i]), null, label0, label5, start);
            }
            methodVisitor.visitMaxs(6, 10);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter;
    }
}