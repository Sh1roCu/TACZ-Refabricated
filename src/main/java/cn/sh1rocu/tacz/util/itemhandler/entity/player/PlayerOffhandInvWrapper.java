package cn.sh1rocu.tacz.util.itemhandler.entity.player;

import cn.sh1rocu.tacz.util.itemhandler.InvWrapper;
import cn.sh1rocu.tacz.util.itemhandler.RangedWrapper;
import net.minecraft.world.entity.player.Inventory;

public class PlayerOffhandInvWrapper extends RangedWrapper {
    public PlayerOffhandInvWrapper(Inventory inv) {
        super(new InvWrapper(inv), inv.getNonEquipmentItems().size() + 4, inv.getNonEquipmentItems().size() + 4 + 1);
    }
}