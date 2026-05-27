package com.taczcompat.taczruntimecompat.mixin;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ResourceLocation.class, priority = 2000)
public class ResourceLocationSanitizerMixin {

    // ── char validators ───────────────────────────────────────────────────────

    private static boolean isValidNsChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')
                || c == '_' || c == '-' || c == '.';
    }

    private static boolean isValidPathChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')
                || c == '_' || c == '-' || c == '.' || c == '/';
    }

    private static String scrub(String segment, boolean isPath) {
        char[] buf = null;
        for (int i = 0; i < segment.length(); i++) {
            char c = segment.charAt(i);
            if (!(isPath ? isValidPathChar(c) : isValidNsChar(c))) {
                if (buf == null) buf = segment.toCharArray();
                buf[i] = '_';
            }
        }
        return buf == null ? segment : new String(buf);
    }

    // ── sanitize helpers ──────────────────────────────────────────────────────

    private static String sanitizeFull(String input) {
        if (input == null) return null;
        
        // DO NOT sanitize the placeholder "DUMMY" into a valid lowercase "dummy".
        // This prevents the game from accidentally registering it as a valid ID and
        // crashing later in DataFixer schema lookups.
        if (input.equals("DUMMY") || input.equalsIgnoreCase("dummy")) {
            return input; // Leave it as is. It will fail validation if parsed strictly.
        }

        String s = input.toLowerCase(java.util.Locale.ROOT);
        int colon = s.indexOf(':');
        String ns, path;
        if (colon == -1) {
            ns   = null;
            path = s;
        } else {
            ns   = scrub(s.substring(0, colon), false);
            path = scrub(s.substring(colon + 1).replace(':', '_'), true);
        }
        String result = (ns == null) ? path : ns + ":" + path;
        if (!result.equals(input))
            System.out.println("[RL FIX] \"" + input + "\" -> \"" + result + "\"");
        return result;
    }

    private static String sanitizeNs(String ns) {
        if (ns == null) return null;
        if (ns.equals("DUMMY") || ns.equalsIgnoreCase("dummy")) return ns;
        
        String fixed = scrub(ns.toLowerCase(java.util.Locale.ROOT), false);
        if (!fixed.equals(ns))
            System.out.println("[RL FIX] namespace \"" + ns + "\" -> \"" + fixed + "\"");
        return fixed;
    }

    private static String sanitizePath(String path) {
        if (path == null) return null;
        if (path.equals("DUMMY") || path.equalsIgnoreCase("dummy")) return path;

        String fixed = scrub(
            path.toLowerCase(java.util.Locale.ROOT).replace(':', '_'), true);
        if (!fixed.equals(path))
            System.out.println("[RL FIX] path \"" + path + "\" -> \"" + fixed + "\"");
        return fixed;
    }

    // ── @Inject handlers to safely return null for tryParse on DUMMY ──────────
    
    @Inject(
        method = "tryParse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
        at = @At("HEAD"),
        cancellable = true,
        require = 0,
        remap = false
    )
    private static void skipDummyTryParse(String input, CallbackInfoReturnable<ResourceLocation> cir) {
        if (input != null && (input.equals("DUMMY") || input.equalsIgnoreCase("dummy") || input.contains("dummy"))) {
            System.out.println("[RL FIX] Intercepted tryParse for placeholder '" + input + "'. Returning null.");
            cir.setReturnValue(null);
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // STATIC method injections (ModifyVariable)
    // ════════════════════════════════════════════════════════════════════════════

    @Dynamic("parse is Mojmap-only; no SRG entry → remap=false required")
    @ModifyVariable(
        method   = "parse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
        at       = @At("HEAD"),
        argsOnly = true, ordinal = 0, require = 0, remap = false
    )
    private static String fixParse(String input) {
        return sanitizeFull(input);
    }

    @Dynamic("tryParse is Mojmap-only; no SRG entry → remap=false required")
    @ModifyVariable(
        method   = "tryParse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
        at       = @At("HEAD"),
        argsOnly = true, ordinal = 0, require = 0, remap = false
    )
    private static String fixTryParseVar(String input) {
        return sanitizeFull(input);
    }

    @Dynamic("fromNamespaceAndPath is Mojmap-only; no SRG entry → remap=false required")
    @ModifyVariable(
        method   = "fromNamespaceAndPath(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
        at       = @At("HEAD"),
        argsOnly = true, ordinal = 0, require = 0, remap = false
    )
    private static String fixFromNsAndPathNs(String namespace) {
        return sanitizeNs(namespace);
    }

    @Dynamic("fromNamespaceAndPath path arg; Mojmap-only → remap=false required")
    @ModifyVariable(
        method   = "fromNamespaceAndPath(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
        at       = @At("HEAD"),
        argsOnly = true, ordinal = 1, require = 0, remap = false
    )
    private static String fixFromNsAndPathPath(String path) {
        return sanitizePath(path);
    }

    @Dynamic("tryBuild ns arg; Mojmap-only → remap=false required")
    @ModifyVariable(
        method   = "tryBuild(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
        at       = @At("HEAD"),
        argsOnly = true, ordinal = 0, require = 0, remap = false
    )
    private static String fixTryBuildNs(String namespace) {
        return sanitizeNs(namespace);
    }

    @Dynamic("tryBuild path arg; Mojmap-only → remap=false required")
    @ModifyVariable(
        method   = "tryBuild(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
        at       = @At("HEAD"),
        argsOnly = true, ordinal = 1, require = 0, remap = false
    )
    private static String fixTryBuildPath(String path) {
        return sanitizePath(path);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // CONSTRUCTOR injections
    // ════════════════════════════════════════════════════════════════════════════

    @Dynamic("constructor namespace arg; <init> at HEAD requires static handler")
    @ModifyVariable(
        method   = "<init>(Ljava/lang/String;Ljava/lang/String;)V",
        at       = @At("HEAD"),
        argsOnly = true, ordinal = 0, require = 0, remap = false
    )
    private static String fixCtorNamespace(String namespace) { 
        return sanitizeNs(namespace);
    }

    @Dynamic("constructor path arg; <init> at HEAD requires static handler")
    @ModifyVariable(
        method   = "<init>(Ljava/lang/String;Ljava/lang/String;)V",
        at       = @At("HEAD"),
        argsOnly = true, ordinal = 1, require = 0, remap = false
    )
    private static String fixCtorPath(String path) { 
        return sanitizePath(path);
    }
}
