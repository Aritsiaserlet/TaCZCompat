package com.taczcompat.taczruntimecompat.util;

import com.taczcompat.taczruntimecompat.TaCZRuntimeCompat;
import com.taczcompat.taczruntimecompat.config.CompatConfig;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationUtils {

    public static final ResourceLocation FALLBACK =
            ResourceLocation.fromNamespaceAndPath("tacz", "invalid/fallback");

    /**
     * Normalizes a path string into a valid ResourceLocation path.
     *
     * Preserves as much of the original string as possible:
     *  1. Lowercase (ResourceLocation paths must be lowercase)
     *  2. Replace ':' with '_' (colon is never valid inside a path)
     *  3. Replace spaces with '_'
     *  4. Replace any remaining invalid character with '_'
     *  5. If the result is empty → return "invalid_fallback"
     *  6. If already valid → return unchanged
     */
    public static String sanitizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "invalid_fallback";
        }

        // Fast path: if already valid, return as-is
        if (path.matches("[a-z0-9/._-]+")) {
            return path;
        }

        String sanitized = path.toLowerCase();
        sanitized = sanitized.replace(':', '_');
        sanitized = sanitized.replace(' ', '_');
        sanitized = sanitized.replaceAll("[^a-z0-9/._-]", "_");

        if (sanitized.isEmpty()) {
            return "invalid_fallback";
        }

        return sanitized;
    }

    /**
     * Normalizes a full resource location string (namespace:path).
     *
     * Splits on the FIRST ':' only.
     * Everything after the first ':' is the path — any additional ':'
     * characters in the path are replaced with '_'.
     *
     * If the string is already valid, it is returned unchanged.
     *
     * Example:
     *   "tacz:modern_kinetic_gun__halor6:br75"
     *   → "tacz:modern_kinetic_gun__halor6_br75"
     */
    public static String sanitizeId(String id) {
        if (id == null || id.isEmpty()) {
            return id;
        }

        int firstColon = id.indexOf(':');

        if (firstColon < 0) {
            // No namespace — sanitize the whole string as a path
            String safePath = sanitizePath(id);
            if (safePath.equals(id)) {
                return id; // already valid, no change
            }
            return safePath;
        }

        String namespace = id.substring(0, firstColon);
        String path = id.substring(firstColon + 1);

        // Sanitize namespace: lowercase, replace invalid chars
        String safeNamespace = namespace.toLowerCase().replaceAll("[^a-z0-9._-]", "_");
        if (safeNamespace.isEmpty()) {
            safeNamespace = "minecraft";
        }

        // Sanitize path: this handles extra colons, uppercase, etc.
        String safePath = sanitizePath(path);

        // Only return modified string if something actually changed
        if (safeNamespace.equals(namespace) && safePath.equals(path)) {
            return id; // already valid, no change
        }

        return safeNamespace + ":" + safePath;
    }

    /**
     * Safe wrapper to create a ResourceLocation for NeoForge 1.21.1.
     *
     * Normalizes the path to prevent ResourceLocationException.
     * Falls back to "tacz:invalid/fallback" only if creation still fails.
     */
    public static ResourceLocation safe(String namespace, String path) {
        String sanitizedPath = sanitizePath(path);

        if (!sanitizedPath.equals(path)) {
            if (CompatConfig.DEBUG_MODE.get()) {
                TaCZRuntimeCompat.LOGGER.info(
                        "[TaCZCompat] Normalized path: '{}' → '{}'", path, sanitizedPath);
            } else {
                TaCZRuntimeCompat.LOGGER.warn(
                        "[TaCZCompat] Normalized path: '{}' → '{}'", path, sanitizedPath);
            }
        }

        try {
            return ResourceLocation.fromNamespaceAndPath(namespace, sanitizedPath);
        } catch (Exception e) {
            TaCZRuntimeCompat.LOGGER.error(
                    "[TaCZCompat] Failed to create ResourceLocation for {}:{} (normalized: {}) — using fallback",
                    namespace, path, sanitizedPath, e);
            return FALLBACK;
        }
    }
}

