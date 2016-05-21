package net.blay09.mods.bmc.api.image;

import java.util.Collections;
import java.util.List;

public interface ITooltipProvider {
	List<String> getTooltip();

	class EmptyTooltip implements ITooltipProvider{
		@Override
		public List<String> getTooltip() {
			return Collections.emptyList();
		}
	}

	EmptyTooltip EMPTY = new EmptyTooltip();
}
