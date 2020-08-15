package net.blay09.mods.chattweaks.chat;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

public class VanillaNewChatGui extends ExtendedNewChatGui {

    public VanillaNewChatGui(Minecraft minecraft) {
        super(minecraft);
    }

    @Override // renderChat
    public void func_238492_a_(MatrixStack matrixStack, int updateCounter) {
        if (!this.func_238496_i_()) { // isChatHidden
            this.func_238498_k_(); // handleMessageQueue
            int lineCount = this.getLineCount();
            int drawnChatLinesCount = this.drawnChatLines.size();
            if (drawnChatLinesCount > 0) {
                final boolean isChatOpen = this.mc.currentScreen instanceof ChatScreen;

                double scale = this.getScale();
                int scaledWidth = MathHelper.ceil((double) this.getChatWidth() / scale);
                RenderSystem.pushMatrix();
                RenderSystem.translatef(2.0F, 8.0F, 0.0F);
                RenderSystem.scaled(scale, scale, 1.0D);
                double chatOpacity = this.mc.gameSettings.chatOpacity * (double) 0.9F + (double) 0.1F;
                double accessibilityTextBackgroundOpacity = this.mc.gameSettings.accessibilityTextBackgroundOpacity;
                final double field_238331_l_ = this.mc.gameSettings.field_238331_l_;
                double d3 = 9.0D * (field_238331_l_ + 1.0D);
                double d4 = -8.0D * (field_238331_l_ + 1.0D) + 4.0D * field_238331_l_;
                int l = 0;

                for (int i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < lineCount; ++i1) {
                    ChatLine<IReorderingProcessor> chatLine = this.drawnChatLines.get(i1 + this.scrollPos);
                    if (chatLine != null) {
                        int chatLineTicks = updateCounter - chatLine.getUpdatedCounter();
                        if (chatLineTicks < 200 || isChatOpen) {
                            double brightness = isChatOpen ? 1 : getLineBrightness(chatLineTicks);
                            int l1 = (int) (255.0D * brightness * chatOpacity);
                            int i2 = (int) (255.0D * brightness * accessibilityTextBackgroundOpacity);
                            ++l;
                            if (l1 > 3) {
                                double d6 = (double) (-i1) * d3;
                                matrixStack.push();
                                matrixStack.translate(0.0D, 0.0D, 50.0D);
                                fill(matrixStack, -2, (int) (d6 - d3), scaledWidth + 4, (int) d6, i2 << 24);
                                RenderSystem.enableBlend();
                                matrixStack.translate(0.0D, 0.0D, 50.0D);
                                this.mc.fontRenderer.func_238407_a_(matrixStack, chatLine.func_238169_a_(), 0.0F, (float) ((int) (d6 + d4)), 16777215 + (l1 << 24));
                                RenderSystem.disableAlphaTest();
                                RenderSystem.disableBlend();
                                matrixStack.pop();
                            }
                        }
                    }
                }

                if (!this.field_238489_i_.isEmpty()) {
                    int k2 = (int) (128.0D * chatOpacity);
                    int i3 = (int) (255.0D * accessibilityTextBackgroundOpacity);
                    matrixStack.push();
                    matrixStack.translate(0.0D, 0.0D, 50.0D);
                    fill(matrixStack, -2, 0, scaledWidth + 4, 9, i3 << 24);
                    RenderSystem.enableBlend();
                    matrixStack.translate(0.0D, 0.0D, 50.0D);
                    this.mc.fontRenderer.func_243246_a(matrixStack, new TranslationTextComponent("chat.queue", this.field_238489_i_.size()), 0.0F, 1.0F, 16777215 + (k2 << 24));
                    matrixStack.pop();
                    RenderSystem.disableAlphaTest();
                    RenderSystem.disableBlend();
                }

                if (isChatOpen) {
                    int l2 = 9;
                    RenderSystem.translatef(-3f, 0f, 0f);
                    int j3 = drawnChatLinesCount * l2 + drawnChatLinesCount;
                    int k3 = l * l2 + l;
                    int l3 = this.scrollPos * k3 / drawnChatLinesCount;
                    int k1 = k3 * k3 / j3;
                    if (j3 != k3) {
                        int i4 = l3 > 0 ? 170 : 96;
                        int j4 = this.isScrolled ? 13382451 : 3355562;
                        fill(matrixStack, 0, -l3, 2, -l3 - k1, j4 + (i4 << 24));
                        fill(matrixStack, 2, -l3, 1, -l3 - k1, 13421772 + (i4 << 24));
                    }
                }

                RenderSystem.popMatrix();
            }
        }
    }

}
