package net.blay09.mods.bmc.chat.badges;

import com.google.common.base.Strings;
import net.blay09.mods.bmc.balyware.textcomponent.TextComponentTransformer;
import net.blay09.mods.bmc.image.ChatImage;
import net.blay09.mods.bmc.image.ChatImageDefault;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class NameTransformer extends TextComponentTransformer {

	protected String senderName;
	protected NameBadge nameBadge;
	protected TextFormatting nameColor;
	private ChatImage image;
	private int offset;

	private boolean isNameComponent;

	@Override
	public void begin(ITextComponent chatComponent) {
		image = null;
		nameColor = null;
		isNameComponent = false;
		offset = 0;
	}

	@Override
	public ITextComponent transformStyle(ITextComponent component) {
		if(isNameComponent && nameColor != null) {
			component.getStyle().setColor(nameColor);
			nameColor = null;
			isNameComponent = false;
		}
		offset += component.getFormattedText().length();
		return component;
	}

	@Override
	public String transformText(ITextComponent component, String text) {
		if(senderName == null || text == null || text.trim().length() <= 1) {
			return text;
		}
		int index = text.indexOf(senderName);
		isNameComponent = index != -1;
		if(image == null && nameBadge != null && isNameComponent) {
			image = new ChatImageDefault(offset + index, nameBadge.getImage(), nameBadge);
			return (index > 0 ? text.substring(0, index) : "") + Strings.repeat(" ", nameBadge.getImage().getWidthInSpaces()) + (index < text.length() ? text.substring(index) : "");
		} else {
			return text;
		}
	}

	public ChatImage getImage() {
		return image;
	}

}
