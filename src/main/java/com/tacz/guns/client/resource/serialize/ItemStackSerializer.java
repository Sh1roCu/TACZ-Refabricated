package com.tacz.guns.client.resource.serialize;

import cn.sh1rocu.tacz.util.forge.CraftingHelper;
import com.google.gson.*;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;

public class ItemStackSerializer implements JsonDeserializer<ItemStack> {
    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            return CraftingHelper.getItemStack(jsonObject, true, false);
        } else {
            throw new JsonSyntaxException("Expected " + json + " to be a ItemStack because it's not an object");
        }
    }
}