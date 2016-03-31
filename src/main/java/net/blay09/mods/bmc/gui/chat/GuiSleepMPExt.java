package net.blay09.mods.bmc.gui.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CPacketEntityAction;

import java.io.IOException;

public class GuiSleepMPExt extends GuiChatExt {

	public GuiSleepMPExt() {
		super("");
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButton(1, width / 2 - 100, height - 40, I18n.format("multiplayer.stopSleeping")));
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1) {
			wakeFromSleep();
		} else {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 1) {
			wakeFromSleep();
		} else {
			super.actionPerformed(button);
		}
	}

	private void wakeFromSleep() {
		NetHandlerPlayClient netHandler = mc.thePlayer.sendQueue;
		netHandler.addToSendQueue(new CPacketEntityAction(mc.thePlayer, CPacketEntityAction.Action.STOP_SLEEPING));
		Minecraft.getMinecraft().displayGuiScreen(null);
	}
}
