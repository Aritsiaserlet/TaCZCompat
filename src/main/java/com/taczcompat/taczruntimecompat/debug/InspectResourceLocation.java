package com.taczcompat.taczruntimecompat.debug;

import net.minecraft.resources.ResourceLocation;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

public class InspectResourceLocation {
    public static void main(String[] args) {
        System.out.println("=== ResourceLocation Constructors ===");
        for (Constructor<?> c : ResourceLocation.class.getDeclaredConstructors()) {
            System.out.println(c.toString());
        }
        System.out.println("=== ResourceLocation Methods ===");
        for (Method m : ResourceLocation.class.getDeclaredMethods()) {
            System.out.println(m.toString());
        }
    }
}
