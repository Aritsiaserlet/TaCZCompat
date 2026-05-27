package com.taczcompat.taczruntimecompat.mixin;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Intercepts Util.fetchChoiceType BEFORE it calls doFetchChoiceType.
 *
 * Root cause of bootstrap crash:
 *   TaCZ registers a placeholder BlockEntity using the string "DUMMY".
 *   Our ResourceLocationSanitizerMixin "fixes" this into "dummy" (making it a valid ResourceLocation).
 *   When the game bootstraps, BlockEntityType.register calls Util.fetchChoiceType(..., "minecraft:dummy").
 *   Since "minecraft:dummy" is not in the DFU schema, it throws an IllegalArgumentException.
 *
 * Fix: 
 *   If choiceName contains "dummy", we return null early. 
 *   The game handles null Type<?> gracefully — it just means no DataFixer migration is applied.
 */
@Mixin(Util.class)
public class UtilFetchChoiceTypeMixin {

    @Inject(
        method = "fetchChoiceType",
        at = @At("HEAD"),
        cancellable = true,
        require = 0,
        remap = false
    )
    private static void skipDummyBlockEntityType(
            DSL.TypeReference type,
            String choiceName,
            CallbackInfoReturnable<Type<?>> cir) {
        
        if (choiceName != null && choiceName.toLowerCase(java.util.Locale.ROOT).contains("dummy")) {
            System.out.println("[TaCZCompat] Util.fetchChoiceType: returning null for unregistered placeholder '" + choiceName + "' to prevent bootstrap crash.");
            cir.setReturnValue(null);
        }
    }
}
