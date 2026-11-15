package com.tacz.guns.client.event;

import cn.sh1rocu.tacz.api.event.PlayerEvent;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.tacz.guns.command.RootCommand;
import com.tacz.guns.config.common.OtherConfig;
import com.tacz.guns.event.GunPackCompatibilityWarning;
import com.tacz.guns.resource.PackConvertor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.io.File;

@Environment(EnvType.CLIENT)
public class PlayerEnterWorld {

    public static void registerClientCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal(RootCommand.ROOT_NAME)
                .then(Commands.literal(GunPackCompatibilityWarning.COMMAND_GROUP)
                        .then(Commands.literal(GunPackCompatibilityWarning.GUIDE_COMMAND)
                                .executes(PlayerEnterWorld::showConversionGuide))
                        .then(Commands.literal(GunPackCompatibilityWarning.DISMISS_COMMAND)
                                .executes(PlayerEnterWorld::dismissCompatibilityWarning))));
    }

    private static int showConversionGuide(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(pre(Component.translatable("message.tacz.pack_compatibility.help",
                suggestCommand("/tacz convert universal all check"),
                suggestCommand("/tacz convert universal all export"))));
        return Command.SINGLE_SUCCESS;
    }

    private static int dismissCompatibilityWarning(CommandContext<CommandSourceStack> context) {
        OtherConfig.HIDE_GUN_PACK_COMPATIBILITY_WARNING.set(true);
        OtherConfig.HIDE_GUN_PACK_COMPATIBILITY_WARNING.save();
        context.getSource().sendSystemMessage(pre(Component.translatable("message.tacz.pack_compatibility.dismissed")));
        return Command.SINGLE_SUCCESS;
    }

    public static boolean onSystemChatReceived(Component message, boolean overlay) {
        boolean canceled = false;
        if (OtherConfig.HIDE_GUN_PACK_COMPATIBILITY_WARNING.get() && !overlay
                && message.getSiblings().stream().anyMatch(part ->
                part.getContents() instanceof TranslatableContents contents
                        && GunPackCompatibilityWarning.WARNING_KEY.equals(contents.getKey()))) {
            // event.setCanceled(true);
            canceled = true;
        }
        return !canceled;
    }

    public static void onClientPlayerEnterWorld(ClientPacketListener handler, PacketSender sender, Minecraft client) {
        // Integrated servers already send the warning through PlayerLoggedInEvent.
        if (!handler.getConnection().isMemoryConnection()) {
            GunPackCompatibilityWarning.warn(client.player);
        }
    }

    public static void onPlayerEnterWorld(PlayerEvent.PlayerLoggedInEvent event) {
        File[] files = PackConvertor.FOLDER.toFile().listFiles();
        if (files != null && files.length > 0) {
            event.getEntity().sendSystemMessage(pre(Component.translatable("message.tacz.convert_from_legacy.intro")));
            event.getEntity().sendSystemMessage(pre(Component.translatable("message.tacz.convert_from_legacy.intro2")));
            Component component = Component.translatable("message.tacz.convert_from_legacy")
                    .append(Component.translatable("message.tacz.convert_from_legacy.button")
                            .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tacz convert"))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("message.tacz.convert_from_legacy.hover"))
                                    )));
            event.getEntity().sendSystemMessage(pre(component));
            event.getEntity().sendSystemMessage(pre(Component.translatable("message.tacz.convert_from_legacy.hint")));
            event.getEntity().sendSystemMessage(pre(Component.translatable("message.tacz.convert_from_legacy.hide")));
        }
    }

    private static Component suggestCommand(String command) {
        return Component.literal(command).withStyle(style -> style.withColor(ChatFormatting.AQUA).withUnderlined(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("message.tacz.pack_compatibility.command.hover"))));
    }

    private static Component pre(Component component) {
        return Component.translatable("message.tacz.pre").append(component);
    }
}
