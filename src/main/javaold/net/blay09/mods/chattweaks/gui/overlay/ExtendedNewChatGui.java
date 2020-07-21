package net.blay09.mods.chattweaks.gui.overlay;

import com.google.common.collect.Lists;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatTweaksConfig;
import net.blay09.mods.chattweaks.ChatViewManager;
import net.blay09.mods.chattweaks.chat.*;
import net.blay09.mods.chattweaks.event.PrintChatMessageEvent;
import net.blay09.mods.chattweaks.chatimage.ChatImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtendedNewChatGui extends NewChatGui {

    public static class WrappedChatLine {
        private final int timeCreated;
        private final ChatMessage message;
        private final ITextComponent component;
        private final String cleanText;
        private final List<TextRenderRegion> regions;
        private final List<ChatImage> images;
        private final boolean alternateBackground;

        public WrappedChatLine(int timeCreated, ChatMessage message, ITextComponent component, String cleanText, List<TextRenderRegion> regions, @Nullable List<ChatImage> images, boolean alternateBackground) {
            this.timeCreated = timeCreated;
            this.message = message;
            this.component = component;
            this.cleanText = cleanText;
            this.regions = regions;
            this.images = images;
            this.alternateBackground = alternateBackground;
        }
    }

    private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR#]");
    private static final Pattern UNDERLINE_CODE_PATTERN = Pattern.compile("(?i)\u00a7n");
    private static final Pattern EMOTE_PATTERN = Pattern.compile("\u00a7\\*");
    private static final Pattern CUSTOM_FORMATTING_CODE_PATTERN = Pattern.compile("\u00a7([#*])");

    private final Minecraft mc;
    private final List<WrappedChatLine> wrappedChatLines = Lists.newArrayList();

    @Override
    public void drawChat(int updateCounter) {
        EmoteRegistry.runDisposal();
        boolean isChatOpen = this.getChatOpen();
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int lineSpacing = ChatTweaksConfig.lineSpacing;
            float chatOpacity = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f;
            int wrappedChatLinesCount = wrappedChatLines.size();
            if (wrappedChatLinesCount > 0) {
                float chatScale = this.getChatScale();
                int chatWidth = MathHelper.ceil((float) this.getChatWidth() / chatScale);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2f, 8f, 0f);
                GlStateManager.scale(chatScale, chatScale, 1f);

                int maxVisibleLines = this.getLineCount();
                int drawnLinesCount = 0;
                for (int lineIdx = 0; lineIdx + this.scrollPos < this.wrappedChatLines.size() && lineIdx < maxVisibleLines; lineIdx++) {
                    WrappedChatLine chatLine = this.wrappedChatLines.get(lineIdx + this.scrollPos);
                    int lifeTime = updateCounter - chatLine.timeCreated;
                    if (lifeTime < 200 || isChatOpen) {
                        int alpha = 255;
                        if (!isChatOpen) {
                            float percentage = (1f - (float) lifeTime / 200f) * 10f;
                            percentage = MathHelper.clamp(percentage, 0f, 1f);
                            percentage = percentage * percentage;
                            alpha = (int) (255f * percentage);
                        }
                        int scaledAlpha = (int) ((float) alpha * chatOpacity);
                        if (scaledAlpha > 3) {
                            int x = 0;
                            int y = -lineIdx * (fontRenderer.FONT_HEIGHT + lineSpacing);
                            if (chatLine.message.hasBackgroundColor()) {
                                drawRect(-2, y - fontRenderer.FONT_HEIGHT + lineSpacing / 2, chatWidth + 4, y + (int) Math.ceil((float) lineSpacing / 2f), (chatLine.message.getBackgroundColor() & 0x00FFFFFF) + ((scaledAlpha / 2) << 24));
                            } else {
                                drawRect(-2, y - fontRenderer.FONT_HEIGHT - lineSpacing / 2, chatWidth + 4, y + (int) Math.ceil((float) lineSpacing / 2f), (((!ChatTweaksConfig.alternateBackground || !chatLine.alternateBackground) ? ChatTweaksConfig.backgroundColor1 : ChatTweaksConfig.backgroundColor2) & 0x00FFFFFF) + ((scaledAlpha / 2) << 24));
                            }
                            GlStateManager.enableBlend();
                            for (TextRenderRegion region : chatLine.regions) {
                                x = fontRenderer.drawString(region.getText(), x, y - fontRenderer.FONT_HEIGHT + 1, (region.getColor() & 0x00FFFFFF) + (((ChatTweaksConfig.chatTextOpacity) ? alpha : scaledAlpha) << 24), true);
                            }
                            if (chatLine.images != null) {
                                for (ChatImage image : chatLine.images) {
                                    int spaceWidth = Minecraft.getMinecraft().fontRenderer.getCharWidth(' ') * image.getSpaces();
                                    float scale = image.getScale();
                                    int renderOffset = fontRenderer.getStringWidth(chatLine.cleanText.substring(0, image.getIndex()));
                                    int renderWidth = (int) (image.getWidth() * scale);
                                    int renderHeight = (int) (image.getHeight() * scale);
                                    int renderX = renderOffset + spaceWidth / 2 - renderWidth / 2;
                                    int renderY = y - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2 - renderHeight / 2;
                                    GlStateManager.pushMatrix();
                                    GlStateManager.scale(scale, scale, 1f);
                                    image.draw((int) (renderX / scale), (int) (renderY / scale), scaledAlpha);
                                    GlStateManager.popMatrix();
                                }
                            }
                            GlStateManager.disableAlpha();
                            GlStateManager.disableBlend();
                        }
                        drawnLinesCount++;
                    }
                }

                // Render the scroll bar, if necessary
                if (isChatOpen) {
                    GlStateManager.translate(-3f, 0f, 0f);
                    int fullHeight = wrappedChatLinesCount * fontRenderer.FONT_HEIGHT + wrappedChatLinesCount;
                    int drawnHeight = drawnLinesCount * fontRenderer.FONT_HEIGHT + drawnLinesCount;
                    int scrollY = this.scrollPos * drawnHeight / wrappedChatLinesCount;
                    int scrollHeight = drawnHeight * drawnHeight / fullHeight;
                    if (fullHeight != drawnHeight) {
                        int alpha = scrollY > 0 ? 0xAA : 0x60;
                        int color = this.isScrolled ? 0xCC3333 : 0x3333AA;
                        drawRect(0, -scrollY, 2, -scrollY - scrollHeight, color + (alpha << 24));
                        drawRect(2, -scrollY, 1, -scrollY - scrollHeight, 0xCCCCCC + (alpha << 24));
                    }
                }

                GlStateManager.popMatrix();
            }
        }

        if (!isChatOpen && ChatTweaksConfig.showNewMessageOverlay && ChatViewManager.getViews().size() > 1) {
            int x = 2;
            int y = 24;
            for (ChatView chatView : ChatViewManager.getViews()) {
                if (chatView.getMessageStyle() != MessageStyle.Chat) {
                    continue;
                }
                String label = "[" + chatView.getName() + "]";
                if (chatView.hasUnreadMessages() && !chatView.isMuted()) {
                    mc.fontRenderer.drawStringWithShadow(label, x, y, 0xFFFF0000);
                }
                x += mc.fontRenderer.getStringWidth(label) + 2;
            }
        }
    }

    @Nullable
    public ITextComponent getChatComponent(int mouseX, int mouseY) {
        if (!this.getChatOpen()) {
            return null;
        }
        ScaledResolution resolution = new ScaledResolution(this.mc);
        int scaleFactor = resolution.getScaleFactor();
        float chatScale = this.getChatScale();
        int x = mouseX / scaleFactor - 2;
        int y = mouseY / scaleFactor - 40;
        x = MathHelper.floor((float) x / chatScale);
        y = MathHelper.floor((float) y / chatScale);
        if (x >= 0 && y >= 0) {
            int lineCount = Math.min(this.getLineCount(), this.wrappedChatLines.size());
            if (x <= MathHelper.floor((float) this.getChatWidth() / this.getChatScale()) && y < (fontRenderer.FONT_HEIGHT + ChatTweaksConfig.lineSpacing) * lineCount + lineCount) {
                int clickedIndex = y / (fontRenderer.FONT_HEIGHT + ChatTweaksConfig.lineSpacing) + this.scrollPos;
                if (clickedIndex >= 0 && clickedIndex < this.wrappedChatLines.size()) {
                    WrappedChatLine chatLine = this.wrappedChatLines.get(clickedIndex);
                    int width = 0;
                    for (ITextComponent component : chatLine.component) {
                        if (component instanceof TextComponentString) {
                            width += fontRenderer.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(((TextComponentString) component).getText(), false));
                            if (width > x) {
                                return component;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void scroll(int amount) {
        scrollPos = Math.max(Math.min(scrollPos + amount, wrappedChatLines.size() - getLineCount()), 0);
        if (scrollPos == 0) {
            isScrolled = false;
        }
    }
}
