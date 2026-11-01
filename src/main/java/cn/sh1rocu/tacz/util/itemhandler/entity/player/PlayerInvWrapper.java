package cn.sh1rocu.tacz.util.itemhandler.entity.player;

import cn.sh1rocu.tacz.util.itemhandler.CombinedInvWrapper;
import net.minecraft.world.entity.player.Inventory;

public class PlayerInvWrapper extends CombinedInvWrapper {
    public PlayerInvWrapper(Inventory inv) {
        super(new PlayerMainInvWrapper(inv), new PlayerArmorInvWrapper(inv), new PlayerOffhandInvWrapper(inv));
    }
}