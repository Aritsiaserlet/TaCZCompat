package com.taczcompat.taczruntimecompat.debug;

/**
 * Minimal stub — SanitizerDebugHelper disabled for POC build.
 * Re-enable after confirming basic mixin operation.
 */
public final class SanitizerDebugHelper {
    private SanitizerDebugHelper() {}

    public static void record(String original, String sanitized) {}
    public static void printReport() {}
    public static void exportToFile() {}
}
