package com.taczcompat.taczruntimecompat.api;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Public API for TaCZ Runtime Compat.
 * Allows addons or modpack scripts to register custom sanitization logic.
 */
public final class TaCZRuntimeCompatAPI {

    private static final List<SanitizerRule> CUSTOM_RULES = new CopyOnWriteArrayList<>();
    private static final List<String> DYNAMIC_ALLOWED_NAMESPACES = new CopyOnWriteArrayList<>();

    private TaCZRuntimeCompatAPI() {}

    /**
     * Registers a custom rule for modifying invalid characters during sanitization.
     * Custom rules run after config-based rules.
     *
     * @param rule The sanitization rule to register.
     */
    public static void registerSanitizerRule(SanitizerRule rule) {
        if (rule != null) {
            CUSTOM_RULES.add(rule);
        }
    }

    /**
     * Adds a namespace dynamically to the allowed sanitization list, bypassing config limits.
     *
     * @param namespace The namespace to allow.
     */
    public static void addAllowedNamespace(String namespace) {
        if (namespace != null && !namespace.isEmpty() && !DYNAMIC_ALLOWED_NAMESPACES.contains(namespace)) {
            DYNAMIC_ALLOWED_NAMESPACES.add(namespace);
        }
    }

    /**
     * INTERNAL USE: Returns the current registered custom rules.
     */
    public static List<SanitizerRule> getCustomRules() {
        return CUSTOM_RULES;
    }

    /**
     * INTERNAL USE: Returns dynamically allowed namespaces.
     */
    public static List<String> getDynamicAllowedNamespaces() {
        return DYNAMIC_ALLOWED_NAMESPACES;
    }
}
