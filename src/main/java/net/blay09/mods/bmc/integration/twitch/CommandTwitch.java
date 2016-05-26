package net.blay09.mods.bmc.integration.twitch;

import com.google.common.collect.Lists;
import net.blay09.javatmi.TMIClient;
import net.blay09.mods.bmc.integration.twitch.handler.TwitchChannel;
import net.blay09.mods.bmc.integration.twitch.handler.TwitchChatHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
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
		TMIClient twitchClient = TwitchIntegration.getTwitchManager().getClient();
		TwitchChatHandler twitchChatHandler = TwitchIntegration.getTwitchChatHandler();
		if(twitchClient != null) {
			if(args[0].startsWith("#")) {
				twitchClient.send(args[0], message);
				twitchChatHandler.onChatMessage(twitchClient, args[0], twitchChatHandler.getThisUser(twitchClient), message);
			} else {
				twitchClient.getTwitchCommands().whisper(args[0], message);
				twitchChatHandler.onWhisperMessage(twitchClient, twitchChatHandler.getThisUser(twitchClient), twitchChatHandler.getUser(args[0]), message);
			}
		}
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		List<String> completions = Lists.newArrayList();
		for(TwitchChannel channel : TwitchIntegration.getTwitchManager().getChannels()) {
			if(channel.isActive()) {
				completions.add("#" + channel.getName().toLowerCase());
			}
		}
		return getListOfStringsMatchingLastWord(args, completions);
	}

}
