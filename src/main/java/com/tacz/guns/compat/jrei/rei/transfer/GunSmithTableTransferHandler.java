package com.tacz.guns.compat.jrei.rei.transfer;

import com.tacz.guns.client.gui.GunSmithTableScreen;
import com.tacz.guns.compat.jrei.rei.display.GunSmithTableDisplay;
import com.tacz.guns.inventory.GunSmithTableMenu;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class GunSmithTableTransferHandler implements TransferHandler {

    @Override
    public ApplicabilityResult checkApplicable(Context context) {
        if (context.getContainerScreen() instanceof GunSmithTableScreen && context.getDisplay() instanceof GunSmithTableDisplay) {
            return ApplicabilityResult.createApplicable();
        }
        return ApplicabilityResult.createNotApplicable();
    }

    @Override
    public Result handle(Context context) {
        if (!(context.getMenu() instanceof GunSmithTableMenu container))
            return Result.createNotApplicable();

        GunSmithTableDisplay display = (GunSmithTableDisplay) context.getDisplay();
        if (!container.canSelectRecipe(display.getRecipeHolder().id(), context.getMinecraft().player.level().getRecipeManager())) {
            return Result.createFailed(Component.translatable("jei.tacz.gun_smith_table.recipe_unavailable"));
        }

        if (context.isActuallyCrafting()) {
            // The table crafts from the player's inventory; transferring only selects the recipe.
            // REI doesn't restore the parent screen after this call which consumes the selection request, so need to call setScreen.
            container.requestRecipeSelection(display.getRecipeHolder().id());
            Minecraft.getInstance().setScreen(context.getContainerScreen());
        }

        return Result.createSuccessful();
    }
}
