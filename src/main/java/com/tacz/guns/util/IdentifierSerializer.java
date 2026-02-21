package com.tacz.guns.util;

import com.google.gson.*;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Type;

/**
 * Replacement for the removed Identifier.Serializer in 1.21.11.
 * Handles JSON serialization/deserialization of Identifier (ResourceLocation).
 */
public class IdentifierSerializer implements JsonDeserializer<Identifier>, JsonSerializer<Identifier> {
    @Override
    public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Identifier.parse(json.getAsString());
    }

    @Override
    public JsonElement serialize(Identifier src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
