package com.tacz.guns.client.input;

import cn.sh1rocu.tacz.api.event.InputEvent;
import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.client.gui.compat.ClothConfigScreen;
import com.tacz.guns.compat.cloth.MenuIntegration;
import com.tacz.guns.init.CompatRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Environment(EnvType.CLIENT)
public class ConfigKey {
    public static final KeyMapping OPEN_CONFIG_KEY = new KeyMapping("key.tacz.open_config.desc",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_T,
            "key.category.tacz");

    public static void onOpenConfig(InputEvent.Key event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS
                && OPEN_CONFIG_KEY.matches(event.getKey(), event.getScanCode())
            /*&& ((IKeyBinding) OPEN_CONFIG_KEY).getKeyModifier().equals(KeyModifier.getActiveModifier())*/) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) {
                return;
            }
            if (!FabricLoader.getInstance().isModLoaded(CompatRegistry.CLOTH_CONFIG)) {
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, ClothConfigScreen.CLOTH_CONFIG_URL);
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("gui.tacz.cloth_config_warning.download"));
                MutableComponent component = Component.translatable("gui.tacz.cloth_config_warning.tips").withStyle(style ->
                        style.applyFormat(ChatFormatting.BLUE).applyFormat(ChatFormatting.UNDERLINE).withClickEvent(clickEvent).withHoverEvent(hoverEvent));
                player.sendSystemMessage(component);
            } else {
                CompatRegistry.checkModLoad(CompatRegistry.CLOTH_CONFIG, () -> Minecraft.getInstance().setScreen(MenuIntegration.getConfigScreen(null)));
            }
        }
    }
}
