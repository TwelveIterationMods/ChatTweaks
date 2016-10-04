package net.blay09.mods.bmc.text;

import com.google.common.collect.Lists;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

public class StyledString {

	private final List<StyledStringSection> sections;
	private String string;

	public StyledString(String string) {
		this(string, Lists.newArrayList());
	}

	public StyledString(String string, List<StyledStringSection> sections) {
		this.string = string;
		this.sections = sections;
	}

	public StyledString(ITextComponent textComponent) {
		sections = Lists.newArrayList();
		StringBuilder sb = new StringBuilder();
		for(ITextComponent component : textComponent) {
			if(component instanceof TextComponentString) {
				String text = ((TextComponentString) component).getText();
				if(text.length() > 0) {
					int start = sb.length();
					sections.add(new StyledStringSection(start, start + text.length(), component.getStyle()));
					sb.append(text);
				}
			}
		}
		string = sb.toString();
	}

	public String getString() {
		return string;
	}

	public ITextComponent toTextComponent() {
		ITextComponent root = null;
		int last = 0;
		for(StyledStringSection section : sections) {
			if(section.getStart() > last) {
				String text = string.substring(last, section.getStart());
				if(root == null) {
					root = new TextComponentString(text);
				} else {
					root.appendText(text);
				}
			}
			last = section.getEnd();
			ITextComponent component = new TextComponentString(string.substring(section.getStart(), section.getEnd()));
			component.setStyle(section.getStyle().createDeepCopy());
			if(root == null) {
				root = component;
			} else {
				root.appendSibling(component);
			}
		}
		if(root == null) {
			root = new TextComponentString("");
		}
		return root;
	}

	public List<StyledStringSection> getStyleSections(int srcStart, int srcEnd, int dstStart, int dstEnd) {
		List<StyledStringSection> result = Lists.newArrayList();
		for(StyledStringSection section : sections) {
			if(section.getStart() >= srcStart && section.getStart() < srcEnd) {
				result.add(new StyledStringSection(Math.max(dstStart, dstStart + (section.getStart() - srcStart)), Math.min(dstEnd, dstEnd + (section.getEnd() - srcEnd)), section.getStyle()));
			}
		}
		return result;
	}
}
