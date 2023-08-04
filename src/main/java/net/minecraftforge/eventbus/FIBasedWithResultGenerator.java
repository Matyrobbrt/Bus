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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FIBasedWithResultGenerator implements Opcodes {

    public static ClassNode generate(Class<? extends IFunctionalEvent<?>> type, Method method, IEventListenerFactory factory) throws Exception {
        ClassNode classWriter = new ClassNode();
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;

        final String eventInternal = Type.getInternalName(type);
        final String eventDesc = Type.getType(type).getDescriptor();
        final String funcListenerDesc = Type.getType(FunctionalListener.class).getDescriptor();
        final String funcEventDesc = Type.getType(IFunctionalEvent.class).getDescriptor();
        final String funcListenerInternal = Type.getType(FunctionalListener.class).getInternalName();
        final String resultDesc = Type.getType(method.getReturnType()).getDescriptor();
        final String genName = factory.getUniqueName(type).replace('.', '/');

        classWriter.visitSource("hi.dynamic", null);
        classWriter.visit(60, ACC_PUBLIC | ACC_SUPER, genName, null, "java/lang/Object", new String[] {Type.getInternalName(type)});
        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL, "test", "Ljava/util/function/Predicate;", "Ljava/util/function/Predicate<" + resultDesc + ">;", null);
            fieldVisitor.visitEnd();
        }
        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL, "listeners", "[" + funcListenerDesc, "[" + funcListenerDesc + "<" + eventDesc + ">;", null);
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/util/function/Predicate;[" + funcListenerDesc + ")V", "(Ljava/util/function/Predicate<" + resultDesc + ">;[" + funcListenerDesc + "<" + eventDesc + ">;)V", null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(9, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(10, label1);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitFieldInsn(PUTFIELD, genName, "test", "Ljava/util/function/Predicate;");
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(11, label2);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitFieldInsn(PUTFIELD, genName, "listeners", "[" + funcListenerDesc);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(12, label3);
            methodVisitor.visitInsn(RETURN);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLocalVariable("this", "L" + genName + ";", null, label0, label4, 0);
            methodVisitor.visitLocalVariable("test", "Ljava/util/function/Predicate;", "Ljava/util/function/Predicate<" + resultDesc + ">;", label0, label4, 1);
            methodVisitor.visitLocalVariable("listeners", "[" + funcListenerDesc, "[" + funcListenerDesc + "<" + eventDesc + ">;", label0, label4, 2);
            methodVisitor.visitMaxs(2, 3);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, null);
            int varAmount = 1 + Arrays.stream(method.getParameterTypes()).mapToInt(it -> Type.getType(it).getSize()).sum();
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(15, label0);
            methodVisitor.visitInsn(ACONST_NULL);
            methodVisitor.visitVarInsn(ASTORE, varAmount);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(16, label1);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ISTORE, varAmount + 1);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(17, label2);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, genName, "listeners", "[" + funcListenerDesc);
            methodVisitor.visitVarInsn(ASTORE, varAmount + 2);
            methodVisitor.visitVarInsn(ALOAD, varAmount + 2);
            methodVisitor.visitInsn(ARRAYLENGTH);
            methodVisitor.visitVarInsn(ISTORE, varAmount + 3);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ISTORE, varAmount + 4);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);

            final List<Object> frames = new ArrayList<>();
            frames.add(genName);
            for (Class<?> parameterType : method.getParameterTypes()) {
                if (parameterType == long.class) {
                    frames.add(LONG);
                } else {
                    frames.add(Type.getType(parameterType).getInternalName());
                }
            }
            frames.add(Type.getInternalName(method.getReturnType()));
            frames.add(Opcodes.INTEGER);
            frames.add("[" + funcListenerDesc);
            frames.add(Opcodes.INTEGER);
            frames.add(Opcodes.INTEGER);

            methodVisitor.visitFrame(Opcodes.F_FULL, 1 + method.getParameterCount() + 5, frames.toArray(), 0, new Object[] {});
            methodVisitor.visitVarInsn(ILOAD, varAmount + 4);
            methodVisitor.visitVarInsn(ILOAD, varAmount + 3);
            Label label4 = new Label();
            methodVisitor.visitJumpInsn(IF_ICMPGE, label4);
            methodVisitor.visitVarInsn(ALOAD, varAmount + 2);
            methodVisitor.visitVarInsn(ILOAD, varAmount + 4);
            methodVisitor.visitInsn(AALOAD);
            methodVisitor.visitVarInsn(ASTORE, varAmount + 5);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLineNumber(18, label5);
            methodVisitor.visitVarInsn(ILOAD, varAmount + 1);
            Label label6 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label6);
            Label label7 = new Label();
            methodVisitor.visitLabel(label7);
            methodVisitor.visitLineNumber(19, label7);
            methodVisitor.visitVarInsn(ALOAD, varAmount + 5);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, funcListenerInternal, "receiveCancelled", "()Z", false);
            Label label8 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label8);
            Label label9 = new Label();
            methodVisitor.visitLabel(label9);
            methodVisitor.visitLineNumber(20, label9);
            methodVisitor.visitVarInsn(ALOAD, varAmount + 5);
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
            methodVisitor.visitInsn(POP);
            methodVisitor.visitJumpInsn(GOTO, label8);
            methodVisitor.visitLabel(label6);
            methodVisitor.visitLineNumber(23, label6);
            methodVisitor.visitFrame(Opcodes.F_APPEND, 1, new Object[] {funcListenerInternal}, 0, null);
            methodVisitor.visitVarInsn(ALOAD, varAmount + 5);
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
            methodVisitor.visitVarInsn(ASTORE, varAmount);
            Label label10 = new Label();
            methodVisitor.visitLabel(label10);
            methodVisitor.visitLineNumber(24, label10);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, genName, "test", "Ljava/util/function/Predicate;");
            methodVisitor.visitVarInsn(ALOAD, varAmount);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Predicate", "test", "(Ljava/lang/Object;)Z", true);
            methodVisitor.visitVarInsn(ISTORE, varAmount + 1);
            methodVisitor.visitLabel(label8);
            methodVisitor.visitLineNumber(17, label8);
            methodVisitor.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
            methodVisitor.visitIincInsn(varAmount + 4, 1);
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(27, label4);
            methodVisitor.visitFrame(Opcodes.F_CHOP, 3, null, 0, null);
            methodVisitor.visitVarInsn(ALOAD, varAmount);
            methodVisitor.visitInsn(ARETURN);
            Label label11 = new Label();
            methodVisitor.visitLabel(label11);
            methodVisitor.visitLocalVariable("listener", funcListenerDesc, funcListenerDesc + "<" + eventDesc + ">;", label5, label8, varAmount + 5);
            methodVisitor.visitLocalVariable("this", "L" + genName + ";", null, label0, label11, 0);
            int start = 1;
            for (int i = 0; i < method.getParameterCount(); i++) {
                start++;
                methodVisitor.visitLocalVariable("arg" + start, Type.getDescriptor(method.getParameterTypes()[i]), null, label0, label11, start);
            }

            methodVisitor.visitLocalVariable("result", resultDesc, null, label1, label11, start);
            methodVisitor.visitLocalVariable("isCancelled", "Z", null, label2, label11, start + 1);
            methodVisitor.visitMaxs(method.getParameterCount() + 1, varAmount + 6);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter;
    }
}