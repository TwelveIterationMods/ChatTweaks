package net.blay09.mods.chattweaks;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.text.SimpleDateFormat;
import java.util.List;

@Mod.EventBusSubscriber(modid = ChatTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChatTweaksConfig {

    private static final SimpleDateFormat DEFAULT_TIMESTAMP_FORMAT = new SimpleDateFormat("[HH:mm]");
    public static SimpleDateFormat cachedTimestampFormat;
    public static int backgroundColorHighlight;

    public static class Client {
        public final ForgeConfigSpec.BooleanValue smallerEmotes;
        public final ForgeConfigSpec.BooleanValue alternateBackground;
        public final ForgeConfigSpec.ConfigValue<String> backgroundColor1;
        public final ForgeConfigSpec.ConfigValue<String> backgroundColor2;
        public final ForgeConfigSpec.ConfigValue<String> backgroundColorHighlight;
        public final ForgeConfigSpec.BooleanValue highlightName;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> highlightWords;
        public final ForgeConfigSpec.BooleanValue smartViewNavigation;
        public final ForgeConfigSpec.BooleanValue showNewMessageOverlay;
        public final ForgeConfigSpec.BooleanValue chatTextOpacity;
        public final ForgeConfigSpec.BooleanValue disableUnderlines;
        public final ForgeConfigSpec.ConfigValue<String> timestampFormat;
        public final ForgeConfigSpec.IntValue messageHistory;

        Client(ForgeConfigSpec.Builder builder) {
            smallerEmotes = builder
                    .comment("Should emotes be scaled down to perfectly fit into one line?")
                    .translation("config.chattweaks.smallerEmotes")
                    .define("smallerEmotes", true);

            alternateBackground = builder
                    .comment("Should uneven lines alternate their background color for easier reading?")
                    .translation("config.chattweaks.alternateBackground")
                    .define("alternateBackground", true);

            highlightName = builder
                    .comment("If set to true, mentions of your Minecraft IGN will be highlighted in chat.")
                    .translation("config.chattweaks.highlightName")
                    .define("highlightName", true);

            disableUnderlines = builder
                    .comment("Set to true to disable underlines in all chat messages, as they usually don't look very good.")
                    .translation("config.chattweaks.disableUnderlines")
                    .define("disableUnderlines", true);

            chatTextOpacity = builder
                    .comment("Vanilla Minecraft makes the text in chat transparent too when opacity is set. Set this to false to restore that behaviour.")
                    .translation("config.chattweaks.chatTextOpacity")
                    .define("chatTextOpacity", true);

            backgroundColor1 = builder
                    .comment("The background color to use for even line numbers as a hex color.")
                    .translation("config.chattweaks.backgroundColor1")
                    .define("backgroundColor1", "000000");

            backgroundColor2 = builder
                    .comment("The background color to use for even line numbers as a hex color.")
                    .translation("config.chattweaks.backgroundColor2")
                    .define("backgroundColor2", "111111");

            backgroundColorHighlight = builder
                    .comment("The background color to use for highlighted lines as a hex color.")
                    .translation("config.chattweaks.backgroundColorHighlight")
                    .define("backgroundColorHighlight", "550000");

            smartViewNavigation = builder
                    .comment("When navigating between views, prefer views with new messages.")
                    .translation("config.chattweaks.smartViewNavigation")
                    .define("smartViewNavigation", true);

            showNewMessageOverlay = builder
                    .comment("Highlights views with new messages red even when chat is closed.")
                    .translation("config.chattweaks.showNewMessageOverlay")
                    .define("showNewMessageOverlay", true);

            timestampFormat = builder
                    .comment("The format for the timestamp to be displayed in.")
                    .translation("config.chattweaks.timestampFormat")
                    .define("timestampFormat", "[HH:mm]");

            highlightWords = builder
                    .comment("List of words that are highlighted in chat.")
                    .translation("config.chattweaks.highlightWords")
                    .defineList("highlightWords", Lists.newArrayList(), it -> it instanceof String);

            messageHistory = builder
                    .comment("The amount of messages to keep available in a view.")
                    .translation("config.chattweaks.messageHistory")
                    .defineInRange("messageHistory", 100, 1, Integer.MAX_VALUE);
        }
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfig.ModConfigEvent event) {
        backgroundColorHighlight = colorFromHex(CLIENT.backgroundColorHighlight.get());

        try {
            cachedTimestampFormat = new SimpleDateFormat(CLIENT.timestampFormat.get());
        } catch (IllegalArgumentException e) {
            ChatTweaks.logger.error("Invalid timestamp format - reverting to default: ", e);
            cachedTimestampFormat = DEFAULT_TIMESTAMP_FORMAT;
        }
    }

    static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    private static int colorFromHex(String hex) {
        return Integer.parseInt(hex.startsWith("#") ? hex.substring(1) : hex, 16);
    }
}
