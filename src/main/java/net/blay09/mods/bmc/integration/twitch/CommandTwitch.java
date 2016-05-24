package net.blay09.mods.bmc.integration.twitch;

import com.google.common.collect.Lists;
import net.blay09.javatmi.TMIClient;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

public class CommandTwitch extends CommandBase {

	@Override
	public String getCommandName() {
		return "twitch";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/twitch <channel|user> <message>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length < 2) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		String message = StringUtils.join(args, ' ', 1, args.length);
		TMIClient twitchClient = TwitchIntegration.getTwitchClient();
		if(twitchClient != null) {
			if(args[0].startsWith("#")) {
				twitchClient.send(args[0], message);
			} else {
				twitchClient.getTwitchCommands().whisper(args[0], message);
			}
		}
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		List<String> completions = Lists.newArrayList();
		for(TwitchChannel channel : TwitchIntegration.getTwitchChannels()) {
			if(channel.isActive()) {
				completions.add("#" + channel.getName().toLowerCase());
			}
		}
		return getListOfStringsMatchingLastWord(args, completions);
	}

}
