package net.blay09.mods.bmc.balyware.textcomponent;

import com.google.common.collect.Lists;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class MultiTextComponentTransformer extends TextComponentTransformer {

	private final List<TextComponentTransformer> children = Lists.newArrayList();

	public void addTransformer(TextComponentTransformer transformer) {
		children.add(transformer);
	}

	public void insertBefore(TextComponentTransformer transformer, TextComponentTransformer... before) {
		int index = children.size();
		for(TextComponentTransformer other : before) {
			if(other != null) {
				int otherIndex = children.indexOf(other);
				if (otherIndex != -1 && otherIndex < index) {
					index = otherIndex;
				}
			}
		}
		children.add(index, transformer);
	}

	public int getTransformerCount() {
		return children.size();
	}

	public boolean contains(TextComponentTransformer transformer) {
		return children.contains(transformer);
	}

	public void removeTransformer(TextComponentTransformer transformer) {
		children.remove(transformer);
	}

	@Override
	public void begin(ITextComponent chatComponent) {
		for(TextComponentTransformer child : children) {
			child.begin(chatComponent);
		}
	}

	@Override
	public String transformText(ITextComponent component, String text) {
		for(TextComponentTransformer child : children) {
			text = child.transformText(component, text);
		}
		return text;
	}

	@Override
	public ITextComponent transformStyle(ITextComponent component) {
		for(TextComponentTransformer child : children) {
			component = child.transformStyle(component);
		}
		return component;
	}

	@Override
	public void finish(ITextComponent chatComponent, ITextComponent transformedComponent) {
		for(TextComponentTransformer child : children) {
			child.finish(chatComponent, transformedComponent);
		}
	}
}
