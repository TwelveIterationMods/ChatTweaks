package net.blay09.mods.bmc.gui.config;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.ChatViewManager;
import net.blay09.mods.bmc.chat.ChatView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.List;

public class GuiChatViews extends GuiScreen {

	private final GuiScreen parentScreen;
	private GuiListChatViews list;

	public GuiChatViews(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.list = new GuiListChatViews(this.mc, this.width, this.height, 32, this.height - 64, 36);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		this.list.handleMouseInput();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.list.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		this.list.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		list.drawScreen(mouseX, mouseY, partialTicks);
	}

	public static class GuiListChatViews extends GuiListExtended {

		private final List<GuiListChatViewEntry> entries = Lists.newArrayList();
		private int selectedIndex;

		public GuiListChatViews(Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
			super(mc, width, height, top, bottom, slotHeight);
			for (ChatView chatView : ChatViewManager.getViews()) {
				entries.add(new GuiListChatViewEntry(this, chatView));
			}
		}

		@Override
		public IGuiListEntry getListEntry(int index) {
			return entries.get(index);
		}

		@Override
		protected int getSize() {
			return entries.size();
		}

		@Override
		protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
			selectedIndex = slotIndex;
		}

		@Override
		protected boolean isSelected(int slotIndex) {
			return slotIndex == selectedIndex;
		}

		public class GuiListChatViewEntry implements IGuiListEntry {

			private final GuiListChatViews parentList;
			private final ChatView chatView;

			public GuiListChatViewEntry(GuiListChatViews parentList, ChatView chatView) {
				this.parentList = parentList;
				this.chatView = chatView;
			}

			@Override
			public void setSelected(int slotIndex, int x, int y) {

			}

			@Override
			public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
				mc.fontRendererObj.drawString(chatView.getName(), x, y, 0xFFFFFF);
			}

			@Override
			public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
				return true;
			}

			@Override
			public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
			}

		}
	}
}
