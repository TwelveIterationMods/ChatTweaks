package net.blay09.mods.bmc.gui.config;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.ChatViewManager;
import net.blay09.mods.bmc.chat.ChatView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

public class GuiChatViews extends GuiScreen {

	private final GuiScreen parentScreen;
	private GuiListChatViews viewList;
	private GuiButton btnAddView;
	private GuiButton btnEditView;
	private GuiButton btnDeleteView;
	private GuiButton btnCancel;

	public GuiChatViews(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;
	}

	@Override
	public void initGui() {
		super.initGui();

		viewList = new GuiListChatViews(mc, width, height, 32, height - 32, 36);

		btnAddView = addButton(new GuiButton(0, width / 2 + 84, height - 28, 80, 20, "Add View"));

		btnEditView = addButton(new GuiButton(1, width / 2 - 84, height - 28, 80, 20, "Edit"));
		btnEditView.enabled = false;

		btnDeleteView = addButton(new GuiButton(2, width / 2, height - 28, 80, 20,"Delete"));
		btnDeleteView.enabled = false;

		btnCancel = addButton(new GuiButton(3, width / 2 - 168, height - 28, 80, 20, "Done"));
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		this.viewList.handleMouseInput();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.viewList.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button == btnAddView) {
			mc.displayGuiScreen(new GuiChatView(this, new ChatView("<new>")));
		} else if(button == btnDeleteView) {
			mc.displayGuiScreen(new GuiYesNo(this, "Do you really want to delete this view?", "This cannot be undone.", 2));
		} else if(button == btnEditView) {
			GuiListChatViews.GuiListChatViewEntry entry = viewList.getSelectedEntry();
			if(entry != null) {
				mc.displayGuiScreen(new GuiChatView(this, entry.chatView));
			}
		} else if(button == btnCancel) {
			mc.displayGuiScreen(parentScreen);
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if(result && id == 2) {
			GuiListChatViews.GuiListChatViewEntry entry = viewList.getSelectedEntry();
			if(entry != null) {
				ChatViewManager.removeChatView(entry.chatView);
			}
		}
		mc.displayGuiScreen(this);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		this.viewList.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void updateScreen() {
		boolean hasSelection = viewList.hasSelection();
		btnEditView.enabled = hasSelection;
		btnDeleteView.enabled = hasSelection;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		viewList.drawScreen(mouseX, mouseY, partialTicks);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public static class GuiListChatViews extends GuiListExtended {

		private final List<GuiListChatViewEntry> entries = Lists.newArrayList();
		private int selectedIndex = -1;

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

		public boolean hasSelection() {
			return selectedIndex != -1;
		}

		@Nullable
		public GuiListChatViewEntry getSelectedEntry() {
			return selectedIndex != - 1 ? entries.get(selectedIndex) : null;
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
				mc.fontRenderer.drawString(chatView.getName(), x, y, 0xFFFFFF);
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
