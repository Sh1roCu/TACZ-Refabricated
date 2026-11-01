package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.util.forge.PartialNBTIngredient;
import cn.sh1rocu.tacz.util.forge.StrictNBTIngredient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Ingredient.class)
public abstract class IngredientMixin {
    @Shadow
    public abstract JsonElement toJson();

    @Inject(method = "toJson", at = @At("HEAD"), cancellable = true)
    private void tacz$injectToJson(CallbackInfoReturnable<JsonElement> cir) {
        CustomIngredient customIngredient = ((Ingredient) (Object) this).getCustomIngredient();
        if (customIngredient != null) {
            if (customIngredient instanceof StrictNBTIngredient strictNBTIngredient) {
                JsonObject obj = new JsonObject();
                strictNBTIngredient.getSerializer().write(obj, strictNBTIngredient);
                cir.setReturnValue(obj);
            } else if (customIngredient instanceof PartialNBTIngredient partialNBTIngredient) {
                JsonObject obj = new JsonObject();
                partialNBTIngredient.getSerializer().write(obj, partialNBTIngredient);
                cir.setReturnValue(obj);
            }
        }
    }

    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/Ingredient;valueFromJson(Lcom/google/gson/JsonObject;)Lnet/minecraft/world/item/crafting/Ingredient$Value;",
                    ordinal = 0
            ),
            method = "fromJson(Lcom/google/gson/JsonElement;Z)Lnet/minecraft/world/item/crafting/Ingredient;",
            cancellable = true
    )
    private static void tacz$injectFromJson(JsonElement json, boolean requireNotEmpty, CallbackInfoReturnable<Ingredient> cir) {
        JsonObject obj = json.getAsJsonObject();

        if (obj.has("type")) {
            ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(obj, "type"));
            CustomIngredientSerializer<?> serializer = null;

            if (id.equals(StrictNBTIngredient.ID)) serializer = StrictNBTIngredient.Serializer.INSTANCE;
            else if (id.equals(PartialNBTIngredient.ID)) serializer = PartialNBTIngredient.Serializer.INSTANCE;

            if (serializer != null) {
                cir.setReturnValue(serializer.read(obj).toVanilla());
            }
        }
    }

    @Inject(
            at = @At("HEAD"),
            method = "fromNetwork",
            cancellable = true
    )
    private static void tacz$injectFromNetwork(FriendlyByteBuf buf, CallbackInfoReturnable<Ingredient> cir) {
        int index = buf.readerIndex();
        try {
            if (buf.readUtf().equals("tacz_ingredient")) {
                ResourceLocation id = buf.readResourceLocation();
                CustomIngredientSerializer<?> serializer = null;

                if (id.equals(StrictNBTIngredient.ID)) serializer = StrictNBTIngredient.Serializer.INSTANCE;
                else if (id.equals(PartialNBTIngredient.ID)) serializer = PartialNBTIngredient.Serializer.INSTANCE;

                if (serializer != null) {
                    cir.setReturnValue(serializer.read(buf).toVanilla());
                }
            } else {
                buf.readerIndex(index);
            }
        } catch (Exception e) {
            buf.readerIndex(index);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "toNetwork", at = @At("HEAD"), cancellable = true)
    private void tacz$injectToNetwork(FriendlyByteBuf buffer, CallbackInfo ci) {
        JsonElement element = this.toJson();
        if (element != null && !element.isJsonNull() && element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("type")) {
                ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(obj, "type"));
                CustomIngredientSerializer serializer = null;

                if (id.equals(StrictNBTIngredient.ID)) serializer = StrictNBTIngredient.Serializer.INSTANCE;
                else if (id.equals(PartialNBTIngredient.ID)) serializer = PartialNBTIngredient.Serializer.INSTANCE;

                if (serializer != null) {
                    buffer.writeUtf("tacz_ingredient");
                    buffer.writeResourceLocation(id);
                    serializer.write(buffer, serializer.read(obj));
                    ci.cancel();
                }
            }
        }
    }
}
