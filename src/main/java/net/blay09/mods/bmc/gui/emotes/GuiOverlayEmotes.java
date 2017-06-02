package net.blay09.mods.bmc.gui.emotes;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.ChatTweaks;
import net.blay09.mods.bmc.chat.emotes.IEmote;
import net.blay09.mods.bmc.chat.emotes.IEmoteGroup;
import net.blay09.mods.bmc.image.renderable.IChatRenderable;
import net.blay09.mods.bmc.chat.emotes.EmoteRegistry;
import net.blay09.mods.bmc.image.renderable.ImageLoader;
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

	private static IChatRenderable iconDefault;
	private static IChatRenderable iconPatreon;
	private static IChatRenderable iconTwitch;
	private static IChatRenderable iconBTTV;

	private final GuiScreen parentScreen;
	private final int width = 100;
	private final int height = 60;
	private int x;
	private int y;

	private String currentGroup = "Default";
	private final List<GuiButtonEmote> emoteButtons = Lists.newArrayList();
	private int scrollOffset;
	private boolean mouseInside;

	public GuiOverlayEmotes(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;

		if (iconDefault == null) {
			iconDefault = ImageLoader.loadImage(new ResourceLocation(ChatTweaks.MOD_ID, "groups/default.png"));
		}
		if (iconPatreon == null) {
			iconPatreon = ImageLoader.loadImage(new ResourceLocation(ChatTweaks.MOD_ID, "groups/patreon.png"));
		}
		if (iconTwitch == null) {
			iconTwitch = ImageLoader.loadImage(new ResourceLocation(ChatTweaks.MOD_ID, "groups/twitch.png"));
		}
		if (iconBTTV == null) {
			iconBTTV = ImageLoader.loadImage(new ResourceLocation(ChatTweaks.MOD_ID, "groups/bttv.png"));
		}
	}

	public void initGui() {
		this.x = parentScreen.width - width - 2;
		this.y = parentScreen.height - height - 14;

		clear();

		int groupY = y + 2;
		IEmoteGroup defaultGroup = EmoteRegistry.getGroup("Default");
		if (defaultGroup != null) {
			parentScreen.buttonList.add(new GuiButtonEmoteGroup(-1, x + 2, groupY, iconDefault, defaultGroup));
			groupY += 14;
		}
		IEmoteGroup patreonGroup = EmoteRegistry.getGroup("Patreon");
		if (patreonGroup != null) {
			parentScreen.buttonList.add(new GuiButtonEmoteGroup(-1, x + 2, groupY, iconPatreon, patreonGroup));
			groupY += 14;
		}
		IEmoteGroup twitchGroup = EmoteRegistry.getGroup("TwitchGlobal");
		if (twitchGroup != null) {
			parentScreen.buttonList.add(new GuiButtonEmoteGroup(-1, x + 2, groupY, iconTwitch, twitchGroup));
			groupY += 14;
		}
		IEmoteGroup bttvGroup = EmoteRegistry.getGroup("BTTV");
		if (bttvGroup != null) {
			parentScreen.buttonList.add(new GuiButtonEmoteGroup(-1, x + 2, groupY, iconBTTV, bttvGroup));
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
		int index = 0;
		int buttonX = x + 16;
		int buttonY = y + 2;
		for (GuiButtonEmote button : emoteButtons) {
			index++;
			if (index >= scrollOffset) {
				if (buttonX + button.width > x + width - 2) {
					buttonX = x + 16;
					buttonY += 14;
				}
				if (buttonY + 14 > y + height - 2) {
					button.visible = false;
					continue;
				}
				button.xPosition = buttonX;
				button.yPosition = buttonY;
				button.visible = true;
				buttonX += button.width + 2;
			} else {
				button.visible = false;
			}
		}

		Gui.drawRect(x + 14, y, x + width, y + height, 0xAA000000);
		mouseInside = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY > y + height;
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
		if (delta > 0) {
			scrollOffset = Math.max(0, scrollOffset - 4);
		} else {
			scrollOffset = Math.min(emoteButtons.size() - 16, scrollOffset + 4);
		}
	}

	public void clear() {
		scrollOffset = 0;
		parentScreen.buttonList.removeIf(guiButton -> guiButton instanceof GuiButtonEmote);
		emoteButtons.clear();
	}

	public boolean isMouseInside() {
		return mouseInside;
	}
}
