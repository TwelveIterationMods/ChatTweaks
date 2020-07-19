//package net.blay09.mods.chattweaks.gui.config;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiListExtended;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.client.resources.I18n;
//import net.minecraft.util.text.TextFormatting;
//
//import java.util.List;
//
//public class GuiChatViewChannelsList extends GuiListExtended {
//
//	private final Minecraft mc;
//	private final String header;
//	private final List<GuiChannelListEntry> channelEntries;
//
//	public GuiChatViewChannelsList(Minecraft mc, int width, int height, List<GuiChannelListEntry> channelEntries, String header) {
//		super(mc, width, height, 32, height - 55 + 4, 36);
//		this.mc = mc;
//		this.channelEntries = channelEntries;
//		this.header = I18n.format(header);
//		centerListVertically = false;
//		setHasListHeader(true, (int) ((float) mc.fontRenderer.FONT_HEIGHT * 1.5f));
//	}
//
//	@Override
//	protected void drawListHeader(int insideLeft, int insideTop, Tessellator tesselator) {
//		String s = TextFormatting.UNDERLINE.toString() + TextFormatting.BOLD + header;
//		mc.fontRenderer.drawString(s, insideLeft + width / 2 - mc.fontRenderer.getStringWidth(s) / 2, Math.min(top + 3, insideTop), 0xFFFFFFFF);
//	}
//
//	public List<GuiChannelListEntry> getList() {
//		return channelEntries;
//	}
//
//	@Override
//	protected int getSize() {
//		return channelEntries.size();
//	}
//
//	@Override
//	public GuiChannelListEntry getListEntry(int index) {
//		return channelEntries.get(index);
//	}
//
//	@Override
//	public int getListWidth() {
//		return width;
//	}
//
//	@Override
//	protected int getScrollBarX() {
//		return right - 6;
//	}
//
//}
