package com.tacz.guns.client.event;

import cn.sh1rocu.tacz.api.event.PlayerEvent;
import com.tacz.guns.resource.PackConvertor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.io.File;

@Environment(EnvType.CLIENT)
public class PlayerEnterWorld {
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

    private static Component pre(Component component) {
        return Component.translatable("message.tacz.pre").append(component);
    }
}
