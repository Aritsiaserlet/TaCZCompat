package com.taczcompat.taczruntimecompat.api;

/**
 * Functional interface for custom sanitization rules.
 */
@FunctionalInterface
public interface SanitizerRule {
    /**
     * Applies a sanitization rule to a single character.
     *
     * @param c The original character.
     * @param isNamespace Whether the character is in the namespace (true) or path (false) part.
     * @return The replacement string (can be a single char or empty to drop), or null to indicate no change.
     */
    String apply(char c, boolean isNamespace);
}
