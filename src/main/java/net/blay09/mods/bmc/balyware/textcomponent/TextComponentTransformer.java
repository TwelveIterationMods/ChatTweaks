package net.blay09.mods.bmc.balyware.textcomponent;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;

public abstract class TextComponentTransformer {

	private static final String PATTERN_ARGUMENT = "%[sdf]";

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
		String newText = transformText(chatComponent, chatComponent.getChatComponentText_TextValue());
		TextComponentString transformedComponent = new TextComponentString(newText);
		transformedComponent.setChatStyle(chatComponent.getChatStyle());
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

	public static TextComponentString convertTranslationComponent(TextComponentTranslation chatComponent) {
		Object[] args = chatComponent.getFormatArgs();
		String[] splitKey = I18n.translateToLocal(chatComponent.getKey()).split("(?<=" + PATTERN_ARGUMENT + ")|(?=" + PATTERN_ARGUMENT + ")");
		TextComponentString root = null;
		int currentArg = 0;
		for(String key : splitKey) {
			if(key.matches(PATTERN_ARGUMENT)) {
				if(root == null) {
					root = new TextComponentString("");
					root.setChatStyle(chatComponent.getChatStyle().createShallowCopy());
				}
				if(args.length > currentArg) {
					if(args[currentArg] instanceof TextComponentString) {
						root.appendSibling(((TextComponentString) args[currentArg]).createCopy());
					} else if(args[currentArg] instanceof TextComponentTranslation) {
						root.appendSibling(convertTranslationComponent((TextComponentTranslation) args[currentArg]));
					} else {
						root.appendText(args[currentArg] == null ? "null" : String.valueOf(args[currentArg]));
					}
					currentArg++;
				}
			} else {
				if(root == null) {
					root = new TextComponentString(key);
					root.setChatStyle(chatComponent.getChatStyle().createShallowCopy());
				} else {
					root.appendSibling(new TextComponentString(key));
				}
			}
		}
		return root;
	}
}
