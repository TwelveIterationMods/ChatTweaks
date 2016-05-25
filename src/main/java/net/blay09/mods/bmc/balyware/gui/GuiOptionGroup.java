package net.blay09.mods.bmc.balyware.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class GuiOptionGroup {

	private final List<GuiCheckBox> options = Lists.newArrayList();

	public GuiOptionGroup(GuiCheckBox... options) {
		Collections.addAll(this.options, options);
	}

	public void actionPerformed(@Nullable GuiButton button) {
		if(button instanceof GuiCheckBox && options.contains(button)) {
			for(GuiCheckBox option : options) {
				if(option != button) {
					option.setIsChecked(false);
				}
			}
		}
	}

}
