package com.taczcompat.taczruntimecompat.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(ChunkStorage.class)
public class ChunkStorageSanitizerMixin {

    @Inject(
        method = "upgradeChunkTag",
        at = @At("HEAD"),
        require = 0,
        remap = false
    )
    private void sanitizeChunkNbtBeforeDataFixer(
            ResourceKey<Level> levelKey,
            Supplier<?> storage,
            CompoundTag chunkData,
            Optional<?> chunkGeneratorKey,
            CallbackInfoReturnable<CompoundTag> cir) {

        if (chunkData == null) return;

        // Modern format (1.18+): block entities stored in "block_entities" list at root
        removeInvalidBlockEntities(chunkData, "block_entities");

        // Legacy format (pre-1.18): block entities under "Level" -> "TileEntities"
        if (chunkData.contains("Level", Tag.TAG_COMPOUND)) {
            CompoundTag level = chunkData.getCompound("Level");
            removeInvalidBlockEntities(level, "TileEntities");
        }
    }

    private static void removeInvalidBlockEntities(CompoundTag parent, String listKey) {
        if (!parent.contains(listKey, Tag.TAG_LIST)) return;

        ListTag list = parent.getList(listKey, Tag.TAG_COMPOUND);
        for (int i = list.size() - 1; i >= 0; i--) {
            Tag entry = list.get(i);
            if (entry instanceof CompoundTag compound) {
                String id = compound.getString("id");

                // 1. Explicitly remove "minecraft:dummy"
                if ("minecraft:dummy".equals(id)) {
                    list.remove(i);
                    continue;
                }

                // 2. Catch invalid ResourceLocation formats and remove the entry silently
                if (id != null && !id.isEmpty()) {
                    try {
                        // This will throw if the ID format is invalid
                        ResourceLocation.parse(id);
                    } catch (Exception e) {
                        // Silently clean the data by removing the corrupted block entity
                        list.remove(i);
                    }
                }
            }
        }
    }
}
