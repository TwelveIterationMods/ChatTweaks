package net.blay09.mods.bmc.gui.settings;

import net.blay09.mods.bmc.balyware.gui.FormattedFontRenderer;
import net.blay09.mods.bmc.balyware.gui.GuiFormattedTextField;
import net.blay09.mods.bmc.chat.ChatChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class GuiRegExField extends GuiFormattedTextField {

	public static GuiRegExField create(int id, FontRenderer fontRenderer, int x, int y, int width, int height) {
		return new GuiRegExField(id, new FormattedFontRenderer(Minecraft.getMinecraft(), fontRenderer, new RegExStringFormatter()), x, y, width, height);
	}

	private GuiRegExField(int id, FormattedFontRenderer fontRenderer, int x, int y, int width, int height) {
		super(id, fontRenderer, x, y, width, height);
		setDisplayTextWhenEmpty(TextFormatting.GRAY+ "(all messages)");
		setEmptyText(ChatChannel.DEFAULT_PATTERN.pattern());
	}

	@Override
	public void drawTextBox() {
		if (!getVisible()) {
			return;
		}
		super.drawTextBox();

		PatternSyntaxException error = null;
		try {
			//noinspection ResultOfMethodCallIgnored
			Pattern.compile(getText());
		} catch (PatternSyntaxException e) {
			error = e;
		}

		if (error != null) {
			int endOfError = error.getMessage().indexOf('\r');
			if(endOfError == -1) {
				endOfError = error.getMessage().indexOf('\n');
				if(endOfError == -1) {
					endOfError = error.getMessage().length();
				}
			}
			String message = error.getMessage().substring(0, endOfError);
			fontRenderer.getBaseFontRenderer().drawStringWithShadow(message, xPosition, yPosition + height + 6, 0xFFFF0000);

			int index = Math.min(error.getIndex(), getText().length() - 1);
			if(index != -1) {
				int errorX = xPosition + 4;
				if (index - lineScrollOffset > 0) {
					errorX += fontRenderer.getStringWidth(getText().substring(lineScrollOffset, index));
				}
				int errorY = yPosition + (height - 8) / 2;
				int errorWidth = fontRenderer.getCharWidth(getText().charAt(index));
				if(index - lineScrollOffset >= 0 && errorX + errorWidth < xPosition + width) {
					Gui.drawRect(errorX - 1, errorY - 1, errorX + errorWidth, errorY + 1 + fontRenderer.FONT_HEIGHT, 0x44FF0000);
				}
			}
		} else if(!getText().isEmpty()) {
			if(!getText().contains("(?<s>")) {
				fontRenderer.getBaseFontRenderer().drawStringWithShadow("Pattern is missing (?<s> ... ) sender group.", xPosition, yPosition + height + 6, 0xFFFFFF00);
			} else if(!getText().contains("(?<m>")) {
				fontRenderer.getBaseFontRenderer().drawStringWithShadow("Pattern is missing (?<m> ... ) message group.", xPosition, yPosition + height + 6, 0xFFFFFF00);
			} else {
				fontRenderer.getBaseFontRenderer().drawStringWithShadow("Pattern is valid.", xPosition, yPosition + height + 6, 0xFF00FF00);
			}
		} else {
			fontRenderer.getBaseFontRenderer().drawStringWithShadow("Messages will not be filtered.", xPosition, yPosition + height + 6, 0xFFAAAAAA);
		}
	}

}
