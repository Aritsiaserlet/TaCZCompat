package com.taczcompat.taczruntimecompat.mixin;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Intercepts DataFixerUpper schema lookup directly to prevent IllegalArgumentException
 * when it encounters "minecraft:dummy" or "dummy".
 *
 * The exception happens deep inside DFU when evaluating NBT tags. Instead of guessing
 * where "dummy" is located in the NBT tree (chunks, players, items, structures),
 * we intercept the exact point where DFU asks for the schema type.
 */
@Mixin(value = Schema.class, remap = false)
public class SchemaSanitizerMixin {

    @Inject(
        method = "getChoiceType",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void safeFallbackForDummyChoice(DSL.TypeReference type, String choiceName, CallbackInfoReturnable<Type<?>> cir) {
        if ("minecraft:dummy".equals(choiceName) || "dummy".equals(choiceName)) {
            // Return an empty part type instead of letting it throw an IllegalArgumentException.
            // This safely bypasses DataFixer processing for this specific invalid type.
            System.out.println("[TaCZCompat] Schema.getChoiceType intercepted: Returning empty type for '" + choiceName + "' to prevent DFU crash.");
            cir.setReturnValue(DSL.emptyPartType());
        }
    }
}
