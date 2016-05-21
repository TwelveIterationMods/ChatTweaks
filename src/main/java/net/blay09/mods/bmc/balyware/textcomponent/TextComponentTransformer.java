package net.blay09.mods.bmc.balyware.textcomponent;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TextComponentTransformer {

	private static final Pattern PATTERN_ARGUMENT = Pattern.compile("(?:%([0-9])\\$[sdf]|%[sdf])");
	private static final Matcher MATCHER_ARGUMENT = PATTERN_ARGUMENT.matcher("");

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
		return null;
	}

	private ITextComponent walkTextComponentString(TextComponentString chatComponent) {
		String newText = transformText(chatComponent, chatComponent.getText());
		TextComponentString transformedComponent = new TextComponentString(newText);
		transformedComponent.setStyle(chatComponent.getStyle());
		transformStyle(transformedComponent);
		for(Object object : chatComponent.getSiblings()) {
			ITextComponent adjustedComponent = walkTextComponentInternal((ITextComponent) object);
			if(adjustedComponent != null) {
				transformedComponent.appendSibling(adjustedComponent);
			}
		}
		return transformedComponent;
	}

	private ITextComponent walkTextComponentTranslation(TextComponentTranslation chatComponent) {
		return walkTextComponentString(convertTranslationComponent(chatComponent));
	}

	private static TextComponentString convertTranslationComponent(TextComponentTranslation chatComponent) {
		Object[] args = chatComponent.getFormatArgs();
		String[] splitKey = I18n.translateToLocal(chatComponent.getKey()).split("(?<=" + PATTERN_ARGUMENT + ")|(?=" + PATTERN_ARGUMENT + ")");
		TextComponentString root = null;
		int currentArg = 0;
		for(String key : splitKey) {
			MATCHER_ARGUMENT.reset(key);
			if(MATCHER_ARGUMENT.matches()) {
				if(root == null) {
					root = new TextComponentString("");
					root.setStyle(chatComponent.getStyle().createShallowCopy());
				}
				int thisArg = currentArg;
				if(MATCHER_ARGUMENT.group(1) != null) {
					thisArg = Integer.parseInt(MATCHER_ARGUMENT.group(1)) - 1;
				}
				if(args.length > thisArg) {
					if(args[thisArg] instanceof TextComponentString) {
						root.appendSibling(((TextComponentString) args[thisArg]).createCopy());
					} else if(args[thisArg] instanceof TextComponentTranslation) {
						root.appendSibling(convertTranslationComponent((TextComponentTranslation) args[thisArg]));
					} else {
						root.appendText(args[thisArg] == null ? "null" : String.valueOf(args[thisArg]));
					}
					if(thisArg == currentArg) {
						currentArg++;
					}
				}
			} else {
				if(root == null) {
					root = new TextComponentString(key);
					root.setStyle(chatComponent.getStyle().createShallowCopy());
				} else {
					root.appendSibling(new TextComponentString(key));
				}
			}
		}
		return root;
	}
}
