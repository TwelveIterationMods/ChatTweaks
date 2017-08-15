package net.blay09.mods.chattweaks.gui.emotes;

import com.google.common.collect.Lists;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.chat.emotes.IEmote;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteGroup;
import net.blay09.mods.chattweaks.image.renderable.IChatRenderable;
import net.blay09.mods.chattweaks.chat.emotes.EmoteRegistry;
import net.blay09.mods.chattweaks.image.renderable.ImageLoader;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public class GuiOverlayEmotes {

	/**
	 * Twitch Emotes with white backgrounds or white borders that they refuse to fix for "Nostalgia"'s sake. They make everything look horrible, no need to have them in our beautiful menu.
	 * No one uses these anyways (apart from the DansGame one)
	 */
	private static final String[] BANNED_EMOTES = new String[]{
			"JKanStyle",
			"OptimizePrime",
			"StoneLightning",
			"TheRinger",
			"PazPazowitz",
			"EagleEye",
			"CougarHunt",
			"RedCoat",
			"JonCarnage",
			"PicoMause",
			"BCWarrior",
			"NoNoSpot"
	};

	private static IChatRenderable iconTwitch;
	private static IChatRenderable iconBTTV;
	private static IChatRenderable iconFFZ;

	private final GuiScreen parentScreen;
	private final int width = 120;
	private final int height = 80;
	private int x;
	private int y;

	private String currentGroup = "TwitchGlobal";
	private final List<GuiButtonEmote> emoteButtons = Lists.newArrayList();
	private int scrollOffset;
	private boolean mouseInside;

	public GuiOverlayEmotes(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;

		if (iconTwitch == null) {
			iconTwitch = ImageLoader.loadImage(new ResourceLocation(ChatTweaks.MOD_ID, "groups/twitch.png"));
		}
		if (iconBTTV == null) {
			iconBTTV = ImageLoader.loadImage(new ResourceLocation(ChatTweaks.MOD_ID, "groups/bttv.png"));
		}
		if (iconFFZ == null) {
			iconFFZ = ImageLoader.loadImage(new ResourceLocation(ChatTweaks.MOD_ID, "groups/ffz.png"));
		}
	}

	public void initGui() {
		this.x = parentScreen.width - width - 2;
		this.y = parentScreen.height - height - 14;

		clear();

		int groupX = x;
		int groupY = y + 2;
		IEmoteGroup twitchGroup = EmoteRegistry.getGroup("TwitchGlobal");
		if (twitchGroup != null) {
			parentScreen.buttonList.add(new GuiButtonEmoteGroup(-1, groupX, groupY, iconTwitch, twitchGroup));
			groupY += 14;
		}
		IEmoteGroup bttvGroup = EmoteRegistry.getGroup("BTTV");
		if (bttvGroup != null) {
			parentScreen.buttonList.add(new GuiButtonEmoteGroup(-1, groupX, groupY, iconBTTV, bttvGroup));
			groupY += 14;
		}
		IEmoteGroup ffzGroup = EmoteRegistry.getGroup("FFZ");
		if (ffzGroup != null) {
			parentScreen.buttonList.add(new GuiButtonEmoteGroup(-1, groupX, groupY, iconFFZ, ffzGroup));
			groupY += 14;
		}

		IEmoteGroup group = EmoteRegistry.getGroup(currentGroup);
		if (group != null) {
			displayGroup(group);
		} else {
			group = EmoteRegistry.getFirstGroup();
			if (group != null) {
				displayGroup(group);
			}
		}
	}

	public void actionPerformed(GuiButton button) {
		if (button instanceof GuiButtonEmoteGroup) {
			displayGroup(((GuiButtonEmoteGroup) button).getEmoteGroup());
		} else if (button instanceof GuiButtonEmote) {
			((GuiChat) parentScreen).inputField.writeText(" " + ((GuiButtonEmote) button).getEmote().getCode() + " ");
		}
	}

	public void drawOverlay(int mouseX, int mouseY) {
		int index = -1;
		int buttonX = x + 16;
		int buttonY = y + 2;
		for (GuiButtonEmote button : emoteButtons) {
			index++;
			if (index >= scrollOffset) {
				if (buttonX + button.width > x + width - 2) {
					buttonX = x + 16;
					buttonY += 18;
				}
				if (buttonY + 18 > y + height - 2) {
					button.visible = false;
					continue;
				}
				button.x = buttonX;
				button.y = buttonY;
				button.visible = true;
				buttonX += button.width + 2;
			} else {
				button.visible = false;
			}
		}

		Gui.drawRect(x + 14, y, x + width, y + height, 0xAA000000);
		mouseInside = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}

	public void displayGroup(IEmoteGroup group) {
		clear();
		for (IEmote emote : group.getEmotes()) {
			if (!emote.isRegex() && !ArrayUtils.contains(BANNED_EMOTES, emote.getCode())) {
				GuiButtonEmote button = new GuiButtonEmote(-1, x, y, emote);
				emoteButtons.add(button);
				parentScreen.buttonList.add(button);
			}
		}
		currentGroup = group.getName();
	}

	public void mouseScrolled(int delta) {
		final int emoteColumns = width / 22;
		final int emoteRows = height / 16;
		if (delta > 0) {
			scrollOffset = Math.max(0, scrollOffset - emoteColumns);
		} else {
			scrollOffset = Math.min(emoteButtons.size() - (emoteRows * emoteColumns) + emoteColumns, scrollOffset + emoteColumns);
		}
	}

	private void clear() {
		scrollOffset = 0;
		parentScreen.buttonList.removeIf(guiButton -> guiButton instanceof GuiButtonEmote);
		emoteButtons.clear();
	}

	public void close() {
		clear();
		parentScreen.buttonList.removeIf(p -> p instanceof GuiButtonEmoteGroup);
	}

	public boolean isMouseInside() {
		return mouseInside;
	}
}
