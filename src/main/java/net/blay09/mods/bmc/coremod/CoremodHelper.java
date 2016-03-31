package net.blay09.mods.bmc.coremod;

import net.blay09.mods.bmc.api.event.ClientChatEvent;
import net.blay09.mods.bmc.api.event.DrawChatMessageEvent;
import net.blay09.mods.bmc.api.event.PrintChatMessageEvent;
import net.blay09.mods.bmc.api.event.TabCompletionEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.TabCompleter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class CoremodHelper {

    public static boolean drawChatMessagePre(ChatLine chatLine, String formattedText, int x, int y, int alpha) {
        DrawChatMessageEvent event = new DrawChatMessageEvent.Pre(chatLine, formattedText, x, y, alpha);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static void drawChatMessagePost(ChatLine chatLine, String formattedText, int x, int y, int alpha) {
        MinecraftForge.EVENT_BUS.post(new DrawChatMessageEvent.Post(chatLine, formattedText, x, y, alpha));
    }

    public static PrintChatMessageEvent printChatMessage(ITextComponent chatComponent, int chatLineId) {
        PrintChatMessageEvent event = new PrintChatMessageEvent(chatComponent, chatLineId);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static String[] addTabCompletions(GuiTextField textField, TabCompleter tabCompleter, String[] completions) {
        String input = textField.getText().substring(0, textField.getCursorPosition());
        BlockPos pos = tabCompleter.getTargetBlockPos();
        List<String> list = new ArrayList<>();
        Collections.addAll(list, completions);
        MinecraftForge.EVENT_BUS.post(new TabCompletionEvent(Side.CLIENT, Minecraft.getMinecraft().thePlayer, input.split(" ")[0], pos, pos != null, list));
        return list.toArray(new String[list.size()]);
    }

	public static String onClientChat(String message) {
		ClientChatEvent event = new ClientChatEvent(message);
		if(MinecraftForge.EVENT_BUS.post(event)) {
			message = null;
		} else {
			message = event.getMessage();
		}
		return message;
	}

}
