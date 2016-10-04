package net.blay09.mods.bmc.balyware.textcomponent;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TextComponentTransformer {

	private static final Pattern argumentPattern = Pattern.compile("(?:%([0-9])\\$[sdf]|%[sdf])");

	public void begin(ITextComponent chatComponent) {}

	public String transformText(ITextComponent component, String text) {
		return text;
	}

	public ITextComponent transformStyle(ITextComponent component) {
		return component;
	}

	public void finish(ITextComponent chatComponent, ITextComponent transformedComponent) {}

	public final ITextComponent walkTextComponent(ITextComponent chatComponent) {
		begin(chatComponent);
		ITextComponent transformedComponent = walkTextComponentInternal(chatComponent);
		finish(chatComponent, transformedComponent);
		return transformedComponent;
	}

	private ITextComponent walkTextComponentInternal(ITextComponent chatComponent) {
		if(chatComponent instanceof TextComponentString) {
			return walkTextComponentString((TextComponentString) chatComponent);
		} else if(chatComponent instanceof TextComponentTranslation) {
			return walkTextComponentTranslation((TextComponentTranslation) chatComponent);
		}
		return chatComponent;
	}

	private ITextComponent walkTextComponentString(TextComponentString chatComponent) {
		String newText = transformText(chatComponent, chatComponent.getText());
		TextComponentString transformedComponent = new TextComponentString(newText);
		transformedComponent.setStyle(chatComponent.getStyle());
		transformStyle(transformedComponent);
		for(Object object : chatComponent.getSiblings()) {
			transformedComponent.appendSibling(walkTextComponentInternal((ITextComponent) object));
		}
		return transformedComponent;
	}

	private ITextComponent walkTextComponentTranslation(TextComponentTranslation chatComponent) {
		return walkTextComponentString(convertTranslationComponent(chatComponent));
	}

	private static TextComponentString convertTranslationComponent(TextComponentTranslation chatComponent) {
		Object[] args = chatComponent.getFormatArgs();
		String[] splitKey = I18n.translateToLocal(chatComponent.getKey()).split("(?<=" + argumentPattern + ")|(?=" + argumentPattern + ")");
		TextComponentString root = null;
		int currentArg = 0;
		for(String key : splitKey) {
			Matcher matcher = argumentPattern.matcher(key);
			if (matcher.matches()) {
				if (root == null) {
					root = new TextComponentString("");
					root.setStyle(chatComponent.getStyle().createShallowCopy());
				}
				int thisArg = currentArg;
				if (matcher.group(1) != null) {
					thisArg = Integer.parseInt(matcher.group(1)) - 1;
				}
				if (args.length > thisArg) {
					if (args[thisArg] instanceof TextComponentString) {
						root.appendSibling(((TextComponentString) args[thisArg]).createCopy());
					} else if (args[thisArg] instanceof TextComponentTranslation) {
						root.appendSibling(convertTranslationComponent((TextComponentTranslation) args[thisArg]));
					} else {
						root.appendText(args[thisArg] == null ? "null" : String.valueOf(args[thisArg]));
					}
					if (thisArg == currentArg) {
						currentArg++;
					}
				}
			} else {
				if (root == null) {
					root = new TextComponentString(key);
					root.setStyle(chatComponent.getStyle().createShallowCopy());
				} else {
					root.appendSibling(new TextComponentString(key));
				}
			}
		}
		if(root == null) {
			root = new TextComponentString("");
		}
		for(ITextComponent sibling : chatComponent.getSiblings()) {
			root.appendSibling(sibling);
		}
		return root;
	}
}
