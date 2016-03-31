package net.blay09.mods.bmc.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

// TODO submit event to Forge once prototype is done
public class ClassTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String className, String transformedName, byte[] bytes) {
        if(transformedName.equals("net.minecraft.client.gui.GuiNewChat")) {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(bytes);
            classReader.accept(classNode, 0);
            for(MethodNode method : classNode.methods) {
                if(method.name.equals("drawChat") || method.name.equals("func_146230_a")) {
                    // Insert drawChatMessagePre event call
                    MethodNode mn = new MethodNode();
                    mn.visitVarInsn(Opcodes.ALOAD, 10); // push chatline
                    mn.visitVarInsn(Opcodes.ALOAD, 17); // push s (formattedText)
                    mn.visitVarInsn(Opcodes.ILOAD, 15); // push i2 (x)
                    mn.visitVarInsn(Opcodes.ILOAD, 16); // push j2 (y)
                    mn.visitVarInsn(Opcodes.ILOAD, 14); // push l1 (alpha)
                    mn.visitMethodInsn(Opcodes.INVOKESTATIC, "net/blay09/mods/bmc/coremod/CoremodHelper", "drawChatMessagePre", "(Lnet/minecraft/client/gui/ChatLine;Ljava/lang/String;III)Z", false);
                    Label afterDraw = new Label();
                    mn.visitJumpInsn(Opcodes.IFNE, afterDraw);
                    AbstractInsnNode insertAfter = null;
                    for(int i = 0; i < method.instructions.size(); i++) {
                        AbstractInsnNode node = method.instructions.get(i);
                        if(node instanceof MethodInsnNode) {
                            if(node.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) node).name.equals("enableBlend")) {
                                insertAfter = node;
                                break;
                            }
                        }
                    }
                    if(insertAfter != null) {
                        method.instructions.insert(insertAfter, mn.instructions);
                    }

                    // Insert drawChatMessagePost event call
                    mn = new MethodNode();
                    mn.visitVarInsn(Opcodes.ALOAD, 10); // push chatline
                    mn.visitVarInsn(Opcodes.ALOAD, 17); // push s (formattedText)
                    mn.visitVarInsn(Opcodes.ILOAD, 15); // push i2 (x)
                    mn.visitVarInsn(Opcodes.ILOAD, 16); // push j2 (y)
                    mn.visitVarInsn(Opcodes.ILOAD, 14); // push l1 (alpha)
                    mn.visitMethodInsn(Opcodes.INVOKESTATIC, "net/blay09/mods/bmc/coremod/CoremodHelper", "drawChatMessagePost", "(Lnet/minecraft/client/gui/ChatLine;Ljava/lang/String;III)V", false);
                    mn.visitLabel(afterDraw);
                    AbstractInsnNode insertBefore = null;
                    for(int i = 0; i < method.instructions.size(); i++) {
                        AbstractInsnNode node = method.instructions.get(i);
                        if(node instanceof MethodInsnNode) {
                            if(node.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) node).name.equals("disableAlpha")) {
                                insertBefore = node;
                                break;
                            }
                        }
                    }
                    if(insertBefore != null) {
                        method.instructions.insertBefore(insertBefore, mn.instructions);
                    }
                } else if(method.name.equals("printChatMessageWithOptionalDeletion") || method.name.equals("func_146234_a")) {
                    // Insert printChatMessage event call
                    MethodNode mn = new MethodNode();
                    mn.visitVarInsn(Opcodes.ALOAD, 1); // push chatComponent
                    mn.visitVarInsn(Opcodes.ILOAD, 2); // push chatLineId
                    mn.visitMethodInsn(Opcodes.INVOKESTATIC, "net/blay09/mods/bmc/coremod/CoremodHelper", "printChatMessage", "(Lnet/minecraft/util/text/ITextComponent;I)Lnet/blay09/mods/bmc/api/event/PrintChatMessageEvent;", false);
                    mn.visitVarInsn(Opcodes.ASTORE, 3); // store event
                    mn.visitVarInsn(Opcodes.ALOAD, 3); // push event
                    mn.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/blay09/mods/bmc/api/event/PrintChatMessageEvent", "getMessage", "()Lnet/minecraft/util/text/ITextComponent;", false);
                    mn.visitVarInsn(Opcodes.ASTORE, 1);
                    mn.visitVarInsn(Opcodes.ALOAD, 3); // push event
                    mn.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/blay09/mods/bmc/api/event/PrintChatMessageEvent", "getChatLineId", "()I", false);
                    mn.visitVarInsn(Opcodes.ISTORE, 2);
                    mn.visitVarInsn(Opcodes.ALOAD, 3); // push event
                    mn.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/blay09/mods/bmc/api/event/PrintChatMessageEvent", "isCanceled", "()Z", false);
                    Label afterReturn = new Label();
                    mn.visitJumpInsn(Opcodes.IFEQ, afterReturn);
                    mn.visitInsn(Opcodes.RETURN);
                    mn.visitLabel(afterReturn);
                    method.instructions.insertBefore(method.instructions.getFirst(), mn.instructions);
                }
            }
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();
        } else if(transformedName.equals("net.minecraft.client.gui.FontRenderer")) {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(bytes);
            classReader.accept(classNode, 0);
            for(MethodNode method : classNode.methods) {
                if(method.name.equals("renderStringAtPos") || method.name.equals("func_78255_a")) {
					AbstractInsnNode replaceNode = null;
					for(int i = 0; i < method.instructions.size(); i++) {
						AbstractInsnNode node = method.instructions.get(i);
						if(node instanceof LdcInsnNode) {
							if(((LdcInsnNode) node).cst instanceof String) {
								if(((LdcInsnNode) node).cst.equals("0123456789abcdefklmnor")) {
									replaceNode = node;
									break;
								}
							}
						}
					}
					if(replaceNode != null) {
						method.instructions.set(replaceNode, new LdcInsnNode("0123456789abcdefklmnor#"));
						AbstractInsnNode insertBefore = null;
						for(int i = 0; i < method.instructions.size(); i++) {
							AbstractInsnNode node = method.instructions.get(i);
							if(node instanceof IincInsnNode) {
								if(((IincInsnNode) node).var == 3 && ((IincInsnNode) node).incr == 1) {
									insertBefore = node;
									break;
								}
							}
						}
						if(insertBefore != null) {
							MethodNode mn = new MethodNode();
							Label afterRGB = new Label();
							mn.visitVarInsn(Opcodes.ILOAD, 5); // push i1
							mn.visitVarInsn(Opcodes.BIPUSH, 22); // push "22"
							mn.visitJumpInsn(Opcodes.IF_ICMPNE, afterRGB); // i1 != 22 -> afterRGB
							mn.visitVarInsn(Opcodes.ALOAD, 0); // push this
							mn.visitVarInsn(Opcodes.ILOAD, 2); // push shadow
							mn.visitMethodInsn(Opcodes.INVOKESTATIC, "net/blay09/mods/bmc/coremod/RGBFontRenderer", "popColor", "(Lnet/minecraft/client/gui/FontRenderer;Z)V", false);
							mn.visitLabel(afterRGB);
							method.instructions.insertBefore(insertBefore, mn.instructions);
						}
					}
				}
            }
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();
        }
        return bytes;
    }

}
