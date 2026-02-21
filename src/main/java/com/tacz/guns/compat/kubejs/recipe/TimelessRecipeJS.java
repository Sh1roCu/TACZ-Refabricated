package com.tacz.guns.compat.kubejs.recipe;

public class TimelessRecipeJS /*extends RecipeJS*/ {
//    private String outputGroup = "";
//    private GunSmithTableResultInfo info;
//
//    public GunSmithTableResultInfo getResultInfo() {
//        return info;
//    }
//
//    public void setResultInfo(GunSmithTableResultInfo info) {
//        this.info = info;
//    }
//
//    /**
//     * 设置后对配方结果的影响改至{@link GunSmithTableResultInfo}
//     */
//    public TimelessRecipeJS outputGroupName(String group) {
//        getResultInfo().setGroupName(group);
//        return this;
//    }
//
//    /**
//     * 设置后对配方结果的影响改至{@link GunSmithTableResultInfo}
//     */
//    public TimelessRecipeJS outputGroup(GunSmithTableResultInfo.OutputGroupName group) {
//        return outputGroupName(group.getName());
//    }
//
//    @Override
//    public InputItem readInputItem(Object from) {
//        if (from instanceof JsonObject jsonObject) {
//            if (!jsonObject.has("item")) {
//                throw new RecipeExceptionJS("Expected " + jsonObject + " must has a item member");
//            }
//            Ingredient ingredient = Ingredient.fromJson(jsonObject.get("item"));
//            int count = 1;
//            if (jsonObject.has("count")) {
//                count = Math.max(GsonHelper.getAsInt(jsonObject, "count"), 1);
//            }
//            return InputItem.of(ingredient, count);
//        } else {
//            return super.readInputItem(from);
//        }
//    }
//
//    @Override
//    public JsonElement writeInputItem(InputItem value) {
//        JsonObject jsonObject = new JsonObject();
//        if (value.count > 1) {
//            jsonObject.add("item", value.ingredient.toJson());
//            jsonObject.addProperty("count", value.count);
//        } else {
//            jsonObject.add("item", value.ingredient.toJson());
//        }
//        return jsonObject;
//    }
//
//    @Deprecated
//    @Override
//    public OutputItem readOutputItem(Object from) {
//        if (from instanceof JsonObject jsonObject) {
//            String groupName = "";
//            String typeName = GsonHelper.getAsString(jsonObject, "type");
//            int count = 1;
//            CompoundTag extraTag = null;
//            if (jsonObject.has("count")) {
//                count = Math.max(GsonHelper.getAsInt(jsonObject, "count"), 1);
//            }
//            if (jsonObject.has("nbt")) {
//                extraTag = CraftingHelper.getNBT(jsonObject.get("nbt"));
//            }
//            ItemStack resultItemStack = ItemStack.EMPTY;
//            switch (typeName) {
//                case GunSmithTableResult.GUN -> {
//                    groupName = getGunTypeFromJson(jsonObject);
//                    resultItemStack = getGunItemFromJson(jsonObject);
//                }
//                case GunSmithTableResult.AMMO -> {
//                    groupName = GunSmithTableResultInfo.OutputGroupName.AMMO.getName();
//                    resultItemStack = getAmmoItemFromJson(jsonObject);
//                }
//                case GunSmithTableResult.ATTACHMENT -> {
//                    groupName = getAttachmentTypeFromJson(jsonObject);
//                    resultItemStack = getAttachmentItemFromJson(jsonObject);
//                }
//                case GunSmithTableResult.CUSTOM -> {
//                    JsonObject resultObject = GsonHelper.getAsJsonObject(jsonObject, "item");
//                    ItemStack itemStack = CraftingHelper.getItemStack(resultObject, true);
//                    if (extraTag != null) {
//                        itemStack.setTag(extraTag);
//                    }
//                    resultItemStack = itemStack;
//                }
//            }
//            if (jsonObject.has("group")) {
//                groupName = GsonHelper.getAsString(jsonObject, "group");
//            }
//            if (!groupName.isEmpty()) {
//                outputGroupName(groupName);
//            }
//            return OutputItem.of(resultItemStack).withCount(count);
//        }
//        return super.readOutputItem(from);
//    }
//
//    @Deprecated
//    @Override
//    public JsonElement writeOutputItem(OutputItem value) {
//        JsonObject jsonObject = new JsonObject();
//        JsonObject itemJson = new JsonObject();
//        itemJson.addProperty("item", RegistryInfo.ITEM.getId(value.item.getItem()).toString());
//        itemJson.addProperty("count", value.getCount());
//        if (JsonIO.of(value.getNbt()) != null) {
//            itemJson.addProperty("nbt", value.getNbt().toString());
//        }
//        jsonObject.addProperty("type", "custom");
//        jsonObject.add("item", itemJson);
//        if (!outputGroup.isEmpty()) {
//            jsonObject.addProperty("group", outputGroup);
//        }
//        return jsonObject;
//    }
//
//    private Identifier getIdFromJson(JsonObject jsonObject) {
//        return Identifier.fromNamespaceAndPath(GsonHelper.getAsString(jsonObject, "id"));
//    }
//
//    private String getGunTypeFromJson(JsonObject jsonObject) {
//        Identifier gunId = getIdFromJson(jsonObject);
//        return TimelessAPI.getCommonGunIndex(gunId).map(CommonGunIndex::getType).orElse("");
//    }
//
//    private ItemStack getGunItemFromJson(JsonObject jsonObject) {
//        Identifier gunId = getIdFromJson(jsonObject);
//        int ammoCount;
//        EnumMap<AttachmentType, Identifier> attachments;
//        GunResult gunResult = CommonAssetsManager.GSON.fromJson(jsonObject, GunResult.class);
//        if (gunResult != null) {
//            ammoCount = Math.max(0, gunResult.getAmmoCount());
//            attachments = gunResult.getAttachments();
//        } else {
//            ammoCount = 0;
//            attachments = new EnumMap<>(AttachmentType.class);
//        }
//        return TimelessAPI.getCommonGunIndex(gunId).map(gunIndex ->
//                GunItemBuilder.create()
//                        .setId(gunId)
//                        .setAmmoCount(ammoCount)
//                        .setAmmoInBarrel(false)
//                        .putAllAttachment(attachments)
//                        .setFireMode(gunIndex.getGunData().getFireModeSet().get(0))
//                        .build()
//        ).orElse(ItemStack.EMPTY);
//    }
//
//    private String getAttachmentTypeFromJson(JsonObject jsonObject) {
//        Identifier attachmentId = getIdFromJson(jsonObject);
//        return TimelessAPI.getCommonAttachmentIndex(attachmentId).map(attachmentIndex ->
//                attachmentIndex.getType()
//                        .name()
//                        .toLowerCase(Locale.US)
//        ).orElse("");
//    }
//
//    private ItemStack getAttachmentItemFromJson(JsonObject jsonObject) {
//        Identifier attachmentId = getIdFromJson(jsonObject);
//        return AttachmentItemBuilder.create().setId(attachmentId).build();
//    }
//
//    private ItemStack getAmmoItemFromJson(JsonObject jsonObject) {
//        Identifier ammoId = getIdFromJson(jsonObject);
//        return AmmoItemBuilder.create().setId(ammoId).build();
//    }
}