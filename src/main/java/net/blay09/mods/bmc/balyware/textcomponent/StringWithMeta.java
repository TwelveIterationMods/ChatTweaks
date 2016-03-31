package net.blay09.mods.bmc.balyware.textcomponent;

import com.google.common.collect.*;
import net.blay09.mods.bmc.balyware.textcomponent.metadata.MetaEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

public class StringWithMeta {

	private static final StyleExtractor styleExtractor = new StyleExtractor();

	private final String text;
	private final TreeMultimap<StringRegion, MetaEntry> metadata;

	public StringWithMeta(String text, TreeMultimap<StringRegion, MetaEntry> metadata) {
		this.text = text;
		this.metadata = metadata;
	}

	public List<MetaEntry> getMetaForRange(int index, int length) {
		List<MetaEntry> list = Lists.newArrayList();
		for(StringRegion region : metadata.keySet()) {
			if(index + length >= region.getIndex() && index < region.getIndex() + region.getLength()) {
				list.addAll(metadata.get(region));
			}
		}
		return list;
	}

	public static StringWithMeta fromTextComponent(ITextComponent component) {
		String text = component.getUnformattedText();
		styleExtractor.walkTextComponent(component);
		return new StringWithMeta(text, styleExtractor.getMetadata());
	}

	public String getText() {
		return text;
	}

	public ITextComponent toChatComponent() {
		ITextComponent root = null;
		ITextComponent textComponent;
		int lastIndex = 0;
		for(StringRegion region : metadata.keySet()) {
			if(lastIndex < region.getIndex()) {
				ITextComponent unformatted = new TextComponentString(text.substring(lastIndex, region.getIndex()));
				if(root != null) {
					root.appendSibling(unformatted);
				} else {
					root = unformatted;
				}
				lastIndex = region.getIndex();
			}
			textComponent = new TextComponentString(text.substring(lastIndex, Math.min(text.length(), region.getIndex() + region.getLength())));
			for(MetaEntry meta : metadata.get(region)) {
				meta.apply(textComponent.getChatStyle());
			}
			if(root == null) {
				root = new TextComponentString("");
			}
			root.appendSibling(textComponent);
			lastIndex = region.getIndex() + region.getLength();
		}
		if(root == null) {
			root = new TextComponentString("");
		}
		if(lastIndex < text.length()) {
			root.appendText(text.substring(lastIndex));
		}
		return root;
	}
}
