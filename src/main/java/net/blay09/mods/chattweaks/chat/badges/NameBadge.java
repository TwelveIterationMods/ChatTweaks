package net.blay09.mods.chattweaks.chat.badges;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.blay09.mods.chattweaks.image.renderable.IChatRenderable;
import net.blay09.mods.chattweaks.image.ITooltipProvider;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.Set;

public class NameBadge implements ITooltipProvider {

	private final int id;
	private final String name;
	private final IChatRenderable image;
	private final List<String> tooltip = Lists.newArrayList();
	private final Set<String> players = Sets.newHashSet();

	public NameBadge(int id, String name, IChatRenderable image) {
		this.id = id;
		this.name = name;
		this.image = image;
		tooltip.add(TextFormatting.YELLOW + name);
	}

	@Override
	public List<String> getTooltip() {
		return tooltip;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void addPlayer(String name) {
		players.add(name);
	}

	public IChatRenderable getImage() {
		return image;
	}
}
