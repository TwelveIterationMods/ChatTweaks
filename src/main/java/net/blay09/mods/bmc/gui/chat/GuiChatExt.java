package net.blay09.mods.bmc.gui.chat;

import com.google.common.base.Strings;
import net.blay09.mods.bmc.coremod.CoremodHelper;
import net.blay09.mods.bmc.api.event.ChatComponentClickEvent;
import net.blay09.mods.bmc.api.event.ChatComponentHoverEvent;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;

public class GuiChatExt extends GuiChat {

	public GuiChatExt(String defaultText) {
		super(defaultText);
	}

	@Override
	public void initGui() {
		String oldText = inputField != null ? inputField.getText() : null;
		super.initGui();
		inputField.width = inputField.width - 36;
		if(!Strings.isNullOrEmpty(oldText)) {
			inputField.setText(oldText);
		}
	}

	@Override
	public void sendChatMessage(String message, boolean addToSentMessages) {
		String newMessage = CoremodHelper.onClientChat(message);
		if(!Strings.isNullOrEmpty(newMessage)) {
			if(addToSentMessages) {
				// Store the originally typed message, not the potentially prefixed one.
				mc.ingameGUI.getChatGUI().addToSentMessages(message);
			}
			super.sendChatMessage(newMessage, false);
		}
	}

	@Override
	public void setCompletions(String... newCompletions) {
		super.setCompletions(CoremodHelper.addTabCompletions(inputField, tabCompleter, newCompletions));
	}

	@Override
	protected void handleComponentHover(ITextComponent component, int x, int y) {
		if(!MinecraftForge.EVENT_BUS.post(new ChatComponentHoverEvent(component, x, y))) {
			super.handleComponentHover(component, x, y);
		}
	}

	@Override
	protected boolean handleComponentClick(ITextComponent component) {
		return (component != null && MinecraftForge.EVENT_BUS.post(new ChatComponentClickEvent(component))) || super.handleComponentClick(component);
	}
}
