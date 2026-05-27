package com.taczcompat.taczruntimecompat.util;

/**
 * Scoped ResourceLocation sanitizer.
 * Only fixes paths that belong to TaCZ and contain "gun".
 */
public final class ResourceLocationSanitizer {

    private ResourceLocationSanitizer() {}

    /**
     * Sanitizes the path ONLY if it matches TaCZ gun rules.
     */
    public static String sanitizeTaCZGunPath(String namespace, String path) {
        // Only apply if namespace == "tacz" and path contains "gun"
        if (!"tacz".equals(namespace) || path == null || !path.contains("gun")) {
            return path;
        }

        // Replace invalid characters using: [^a-z0-9/._-] -> "_"
        // Also lowercase any uppercase characters first, as they are often meant to be lowercase,
        // or just directly replace anything not in the allowed set with '_'. 
        // We will strictly replace any character not in [a-z0-9/._-] with "_".
        boolean needsFix = false;
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '/' || c == '.' || c == '_' || c == '-')) {
                needsFix = true;
                break;
            }
        }

        if (!needsFix) return path;

        StringBuilder sb = new StringBuilder(path.length());
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            
            // Lowercase uppercase letters, otherwise replace invalid with '_'
            if (c >= 'A' && c <= 'Z') {
                c = (char) (c + 32);
            } else if (!((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '/' || c == '.' || c == '_' || c == '-')) {
                c = '_';
            }
            
            sb.append(c);
        }
        
        return sb.toString();
    }
}


