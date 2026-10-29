package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.GunTabType;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.AmmoItem;
import com.tacz.guns.item.AttachmentItem;
import com.tacz.guns.item.GunSmithTableItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import static cn.sh1rocu.tacz.TaCZFabric.VANILLA_ACCESS;

@SuppressWarnings("all")
public class ModCreativeTabs {
    public static void init() {

    }

    public static CreativeModeTab OTHER_TAB = register("other", FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.tab.tacz.other"))
            .icon(() -> ModItems.GUN_SMITH_TABLE.getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.acceptAll(GunSmithTableItem.fillItemCategory());
                output.accept(ModItems.TARGET);
                output.accept(ModItems.STATUE);
                output.accept(ModItems.GUN_PEGBOARD);
                output.accept(ModItems.GLOW_GUN_PEGBOARD);
                output.accept(ModItems.AMMO_STORAGE);
                output.accept(ModItems.AMMO_STORAGE_GOLD);
                output.accept(ModItems.AMMO_STORAGE_DIAMOND);
                output.accept(ModItems.GUN_DISPLAY_CASE);
                output.accept(ModItems.TARGET_MINECART);
                AmmoBoxItem.fillItemCategory(output);
            }).build());

    public static CreativeModeTab AMMO_TAB = register("ammo", FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.tab.tacz.ammo"))
            .icon(() -> AmmoItemBuilder.create().setId(DefaultAssets.DEFAULT_AMMO_ID).build())
            .displayItems((parameters, output) -> output.acceptAll(AmmoItem.fillItemCategory())).build());

    public static CreativeModeTab ATTACHMENT_SCOPE_TAB = register("scope", FabricItemGroup.builder()
            .title(Component.translatable("tacz.type.scope.name"))
            .icon(() -> AttachmentItemBuilder.create().setId(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "scope_acog_ta31")).build())
            .displayItems((parameters, output) -> output.acceptAll(AttachmentItem.fillItemCategory(AttachmentType.SCOPE))).build());

    public static CreativeModeTab ATTACHMENT_MUZZLE_TAB = register("muzzle", FabricItemGroup.builder()
            .title(Component.translatable("tacz.type.muzzle.name"))
            .icon(() -> AttachmentItemBuilder.create().setId(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "muzzle_compensator_trident")).build())
            .displayItems((parameters, output) -> output.acceptAll(AttachmentItem.fillItemCategory(AttachmentType.MUZZLE))).build());

    public static CreativeModeTab ATTACHMENT_STOCK_TAB = register("stock", FabricItemGroup.builder()
            .title(Component.translatable("tacz.type.stock.name"))
            .icon(() -> AttachmentItemBuilder.create().setId(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "stock_militech_b5")).build())
            .displayItems((parameters, output) -> output.acceptAll(AttachmentItem.fillItemCategory(AttachmentType.STOCK))).build());

    public static CreativeModeTab ATTACHMENT_GRIP_TAB = register("grip", FabricItemGroup.builder()
            .title(Component.translatable("tacz.type.grip.name"))
            .icon(() -> AttachmentItemBuilder.create().setId(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "grip_magpul_afg_2")).build())
            .displayItems((parameters, output) -> output.acceptAll(AttachmentItem.fillItemCategory(AttachmentType.GRIP))).build());

    public static CreativeModeTab ATTACHMENT_EXTENDED_MAG_TAB = register("extended_mag", FabricItemGroup.builder()
            .title(Component.translatable("tacz.type.extended_mag.name"))
            .icon(() -> AttachmentItemBuilder.create().setId(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "extended_mag_3")).build())
            .displayItems((parameters, output) -> output.acceptAll(AttachmentItem.fillItemCategory(AttachmentType.EXTENDED_MAG))).build());

    public static CreativeModeTab ATTACHMENT_LASER_TAB = register("laser", FabricItemGroup.builder()
            .title(Component.translatable("tacz.type.laser.name"))
            .icon(() -> AttachmentItemBuilder.create().setId(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "laser_compact")).build())
            .displayItems((parameters, output) -> output.acceptAll(AttachmentItem.fillItemCategory(AttachmentType.LASER))).build());

    public static CreativeModeTab GUN_PISTOL_TAB = register("pistol", FabricItemGroup.builder()
            .title(Component.translatable("tacz.type.pistol.name"))
            .icon(() -> GunItemBuilder.create().setId(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "glock_17")).build(VANILLA_ACCESS))
            .displayItems((parameters, output) -> output.acceptAll(AbstractGunItem.fillItemCategory(GunTabType.PISTOL, parameters.holders()))).build());

    public static CreativeModeTab GUN_SNIPER_TAB = register("sniper", FabricItemGroup.builder()
            .title(Component.translatable("tacz.type.sniper.name"))
            .icon(() -> GunItemBuilder.create().setId(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "ai_awp")).build(VANILLA_ACCESS))
            .displayItems((parameters, output) -> output.acceptAll(AbstractGunItem.fillItemCategory(GunTabType.SNIPER, parameters.holders()))).build());

    public static CreativeModeTab GUN_DMR_TAB = register("dmr", FabricItemGroup.builder()
            .title(Component.translatable("tacz.type.dmr.name"))
            .icon(() -> GunItemBuilder.create().setId(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "mk14")).build(VANILLA_ACCESS))
            .displayItems((parameters, output) -> output.acceptAll(AbstractGunItem.fillItemCategory(GunTabType.DMR, parameters.holders()))).build());

    public static CreativeModeTab GUN_RIFLE_TAB = register("rifle", FabricItemGroup.builder()
            .title(Component.translatable("tacz.type.rifle.name"))
            .icon(() -> GunItemBuilder.create().setId(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "ak47")).build(VANILLA_ACCESS))
            .displayItems((parameters, output) -> output.acceptAll(AbstractGunItem.fillItemCategory(GunTabType.RIFLE, parameters.holders()))).build());

    public static CreativeModeTab GUN_SHOTGUN_TAB = register("shotgun", FabricItemGroup.builder()
            .title(Component.translatable("tacz.type.shotgun.name"))
            .icon(() -> GunItemBuilder.create().setId(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "db_short")).build(VANILLA_ACCESS))
            .displayItems((parameters, output) -> output.acceptAll(AbstractGunItem.fillItemCategory(GunTabType.SHOTGUN, parameters.holders()))).build());

    public static CreativeModeTab GUN_SMG_TAB = register("smg", FabricItemGroup.builder()
            .title(Component.translatable("tacz.type.smg.name"))
            .icon(() -> GunItemBuilder.create().setId(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "hk_mp5a5")).build(VANILLA_ACCESS))
            .displayItems((parameters, output) -> output.acceptAll(AbstractGunItem.fillItemCategory(GunTabType.SMG, parameters.holders()))).build());

    public static CreativeModeTab GUN_RPG_TAB = register("rpg", FabricItemGroup.builder()
            .title(Component.translatable("tacz.type.rpg.name"))
            .icon(() -> GunItemBuilder.create().setId(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "rpg7")).build(VANILLA_ACCESS))
            .displayItems((parameters, output) -> output.acceptAll(AbstractGunItem.fillItemCategory(GunTabType.RPG, parameters.holders()))).build());

    public static CreativeModeTab GUN_MG_TAB = register("mg", FabricItemGroup.builder()
            .title(Component.translatable("tacz.type.mg.name"))
            .icon(() -> GunItemBuilder.create().setId(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "m249")).build(VANILLA_ACCESS))
            .displayItems((parameters, output) -> output.acceptAll(AbstractGunItem.fillItemCategory(GunTabType.MG, parameters.holders()))).build());

    private static CreativeModeTab register(String name, CreativeModeTab tab) {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, name), tab);
    }
}
