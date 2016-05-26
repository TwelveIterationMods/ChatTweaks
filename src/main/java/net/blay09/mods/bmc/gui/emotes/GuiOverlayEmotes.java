package net.blay09.mods.bmc.gui.emotes;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.api.gui.IGuiOverlay;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.api.emote.IEmoteGroup;
import net.blay09.mods.bmc.api.image.IChatRenderable;
import net.blay09.mods.bmc.chat.emotes.EmoteRegistry;
import net.blay09.mods.bmc.gui.GuiOverlay;
import net.blay09.mods.bmc.image.renderable.ImageLoader;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Iterator;
import java.util.List;

public class GuiOverlayEmotes extends GuiOverlay {

	/**
	 * Twitch Emotes with white backgrounds or white borders that they refuse to fix for "Nostalgia"'s sake. They make everything look horrible, no need to have them in our beautiful menu.
	 * No one uses these anyways (apart from the DansGame one)
	 */
	private static final String[] BANNED_EMOTES = new String[] {
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

	private static String currentGroup = "Default";

	private static IChatRenderable iconDefault;
	private static IChatRenderable iconPatreon;
	private static IChatRenderable iconTwitch;
	private static IChatRenderable iconBTTV;

	private final List<GuiButtonEmote> emoteButtons = Lists.newArrayList();

	private int scrollOffset;

	public GuiOverlayEmotes(GuiScreen parentScreen) {
		super(parentScreen);
		this.width = 100;
		this.height = 60;
		this.x = parentScreen.width - width - 2;
		this.y = parentScreen.height - height - 14;
		if(iconDefault == null) {
			iconDefault = ImageLoader.loadImage(new ResourceLocation(BetterMinecraftChat.MOD_ID, "groups/default.png"));
		}
		if(iconPatreon == null) {
			iconPatreon = ImageLoader.loadImage(new ResourceLocation(BetterMinecraftChat.MOD_ID, "groups/patreon.png"));
		}
		if(iconTwitch == null) {
			iconTwitch = ImageLoader.loadImage(new ResourceLocation(BetterMinecraftChat.MOD_ID, "groups/twitch.png"));
		}
		if(iconBTTV == null) {
			iconBTTV = ImageLoader.loadImage(new ResourceLocation(BetterMinecraftChat.MOD_ID, "groups/bttv.png"));
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		int groupY = y + 2;
		if(EmoteRegistry.hasGroup("Default")) {
			addButton(new GuiButtonEmoteGroup(-1, x + 2, groupY, iconDefault, EmoteRegistry.getGroup("Default")));
			groupY += 14;
		}
		if(EmoteRegistry.hasGroup("Patreon")) {
			addButton(new GuiButtonEmoteGroup(-1, x + 2, groupY, iconPatreon, EmoteRegistry.getGroup("Patreon")));
			groupY += 14;
		}
		if(EmoteRegistry.hasGroup("TwitchGlobal")) {
			addButton(new GuiButtonEmoteGroup(-1, x + 2, groupY, iconTwitch, EmoteRegistry.getGroup("TwitchGlobal")));
			groupY += 14;
		}
		if(EmoteRegistry.hasGroup("BTTV")) {
			addButton(new GuiButtonEmoteGroup(-1, x + 2, groupY, iconBTTV, EmoteRegistry.getGroup("BTTV")));
		}
		displayGroup(EmoteRegistry.getGroup(currentGroup));
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if(button instanceof GuiButtonEmoteGroup) {
			displayGroup(((GuiButtonEmoteGroup) button).getEmoteGroup());
		} else if(button instanceof GuiButtonEmote) {
			((GuiChat) parentScreen).inputField.writeText(" " + ((GuiButtonEmote) button).getEmote().getCode() + " ");
		}
	}

	public void displayGroup(IEmoteGroup group) {
		clearEmotes();
		for(IEmote emote : group.getEmotes()) {
			if(!emote.isRegex() && !ArrayUtils.contains(BANNED_EMOTES, emote.getCode())) {
				GuiButtonEmote button = new GuiButtonEmote(-1, x, y, emote);
				emoteButtons.add(button);
				addButton(button);
			}
		}
		currentGroup = group.getName();
	}

	@Override
	public void drawOverlayBackground(int mouseX, int mouseY) {
		int index = 0;
		int buttonX = x + 16;
		int buttonY = y + 2;
		for(GuiButtonEmote button : emoteButtons) {
			index++;
			if(index >= scrollOffset) {
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
	}

	@Override
	public void clear() {
		scrollOffset = 0;
		clearEmotes();
		super.clear();
	}

	private void clearEmotes() {
		Iterator<GuiButton> it = parentScreen.buttonList.iterator();
		while(it.hasNext()) {
			if(it.next() instanceof GuiButtonEmote) {
				it.remove();
			}
		}
		emoteButtons.clear();
	}

	@Override
	public void mouseScrolled(int delta) {
		if(delta > 0) {
			scrollOffset = Math.max(0, scrollOffset - 4);
		} else {
			scrollOffset = Math.min(emoteButtons.size() - 16, scrollOffset + 4);
		}
	}

	@Override
	public IGuiOverlay recreateFor(IGuiOverlay overlay, GuiScreen guiScreen) {
		return new GuiOverlayEmotes(guiScreen);
	}

}
