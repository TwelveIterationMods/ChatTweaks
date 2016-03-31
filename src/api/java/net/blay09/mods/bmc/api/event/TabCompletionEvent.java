package net.blay09.mods.bmc.api.event;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class TabCompletionEvent extends Event {

	private final Side side;
	private final ICommandSender sender;
	private final String input;
	private final BlockPos pos;
	private final boolean hasTargetBlock;
	private final List<String> completions;

	public TabCompletionEvent(Side side, ICommandSender sender, String input, BlockPos pos, boolean hasTargetBlock, List<String> completions) {
		this.side = side;
		this.sender = sender;
		this.input = input;
		this.pos = pos;
		this.hasTargetBlock = hasTargetBlock;
		this.completions = completions;
	}

	public Side getSide() {
		return side;
	}

	public ICommandSender getSender() {
		return sender;
	}

	public String getInput() {
		return input;
	}

	public BlockPos getPos() {
		return pos;
	}

	public boolean isHasTargetBlock() {
		return hasTargetBlock;
	}

	public List<String> getCompletions() {
		return completions;
	}

	public void addCompletion(String completion) {
		completions.add(completion);
	}

}
