package net.blay09.mods.chattweaks.chat;

import net.blay09.mods.chattweaks.api.ChatDisplay;
import net.blay09.mods.chattweaks.api.ChatMessage;
import net.blay09.mods.chattweaks.api.ChatView;
import net.blay09.mods.chattweaks.api.ClearChatEvent;
import net.blay09.mods.chattweaks.api.event.PrintChatMessageEvent;
import net.blay09.mods.chattweaks.core.ChatManager;
import net.blay09.mods.chattweaks.core.ChatViewManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;

public class ExtendedNewChatGui extends NewChatGui implements ChatDisplay {

    public static final String DISPLAY_NAME = "chat";

    protected final Minecraft mc;

    private boolean alternateBackground;

    public ExtendedNewChatGui(Minecraft minecraft) {
        super(minecraft);
        this.mc = minecraft;
    }

    @Override
    public void printChatMessage(ITextComponent chatComponent) {
        PrintChatMessageEvent event = new PrintChatMessageEvent(chatComponent, 0);
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            super.printChatMessage(event.getChatComponent());
        }
    }

    @Override
    public void clearChatMessages(boolean clearSentMsgHistory) {
        ClearChatEvent event = new ClearChatEvent(clearSentMsgHistory);
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            super.clearChatMessages(event.isClearSentMessageHistory());
        }
    }

    @Override
    public void refreshChat() {
        this.drawnChatLines.clear();
        this.resetScroll();

        ChatView activeView = ChatViewManager.getActiveView();
        for (ChatMessage chatMessage : activeView.getChatMessages()) {
            ChatManager.addChatMessageForDisplay(chatMessage, activeView);
        }
    }

    @Override
    public String getName() {
        return DISPLAY_NAME;
    }

    @Override
    public void addChatMessage(ChatMessage chatMessage, ChatView view) {
        if (view != ChatViewManager.getActiveView()) {
            return;
        }

        this.func_238493_a_(chatMessage.getTextComponent(), chatMessage.getChatLineId(), mc.ingameGUI.getTicks(), false);
        alternateBackground = !alternateBackground;
        /*
        int chatWidth = MathHelper.floor((float) this.getChatWidth() / this.getChatScale());
        List<ITextComponent> wrappedList = GuiUtilRenderComponents.splitText(chatMessage.getTextComponent(), chatWidth, this.mc.fontRenderer, false, false);
        boolean isChatOpen = this.getChatOpen();
        int colorIndex = -1;
        int emoteIndex = 0;
        for (ITextComponent chatLine : wrappedList) {
            if (isChatOpen && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }

            String formattedText = chatLine.getFormattedText();
            if (ChatTweaksConfig.CLIENT.disableUnderlines.get()) {
                formattedText = UNDERLINE_CODE_PATTERN.matcher(formattedText).replaceAll("");
            }

            Matcher splitMatcher = CUSTOM_FORMATTING_CODE_PATTERN.matcher(formattedText);
            List<TextRenderRegion> regions = Lists.newArrayList();
            int lastIdx = 0;
            while (splitMatcher.find()) {
                String code = splitMatcher.group(1);
                regions.add(new TextRenderRegion(formattedText.substring(lastIdx, splitMatcher.start()), chatMessage.getRGBColor(colorIndex)));
                if (code.equals("#")) {
                    colorIndex++;
                }

                lastIdx = splitMatcher.end();
            }

            if (lastIdx < formattedText.length()) {
                regions.add(new TextRenderRegion(formattedText.substring(lastIdx), chatMessage.getRGBColor(colorIndex)));
            }

            String cleanText = FORMATTING_CODE_PATTERN.matcher(chatLine.getUnformattedText()).replaceAll("");
            Matcher matcher = EMOTE_PATTERN.matcher(cleanText);
            List<ChatImage> images = null;
            if (chatMessage.hasImages()) {
                images = Lists.newArrayList();
                while (matcher.find()) {
                    ChatImage image = chatMessage.getImage(emoteIndex);
                    if (image != null) {
                        image.setIndex(matcher.start());
                        images.add(image);
                    }
                    emoteIndex++;
                }
            }

            this.wrappedChatLines.add(0, new WrappedChatLine(mc.ingameGUI.getUpdateCounter(), chatMessage, chatLine, cleanText, regions, images, alternateBackground));
        }

        while (this.wrappedChatLines.size() > ChatTweaks.MAX_MESSAGES) {
            this.wrappedChatLines.remove(this.wrappedChatLines.size() - 1);
        }

        alternateBackground = !alternateBackground;*/
    }
}
