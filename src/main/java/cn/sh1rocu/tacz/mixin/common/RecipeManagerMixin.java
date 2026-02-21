package cn.sh1rocu.tacz.mixin.common;

// This mixin is no longer needed in 1.21.11.
// In 1.21.1, it modified raw JSON to inject Fabric's CustomIngredient TYPE_KEY before recipe deserialization.
// In 1.21.11, RecipeManager.apply() receives RecipeMap (already deserialized) instead of Map<Identifier, JsonElement>.
// Fabric API now handles custom ingredients natively through IngredientMixin.injectCodec() which modifies the
// Ingredient Codec to support custom ingredient types.
