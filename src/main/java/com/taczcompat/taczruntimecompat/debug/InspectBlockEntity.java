package com.taczcompat.taczruntimecompat.debug;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

public class InspectBlockEntity {
    public static void main(String[] args) {
        try {
            System.out.println("=== BlockEntity Constructors ===");
            for (Constructor<?> c : BlockEntity.class.getDeclaredConstructors()) {
                System.out.println(c);
            }
            System.out.println("=== BlockEntity Methods ===");
            for (Method m : BlockEntity.class.getDeclaredMethods()) {
                System.out.println(m);
            }

            System.out.println("=== BlockEntityType Constructors ===");
            for (Constructor<?> c : BlockEntityType.class.getDeclaredConstructors()) {
                System.out.println(c);
            }
            System.out.println("=== BlockEntityType Methods ===");
            for (Method m : BlockEntityType.class.getDeclaredMethods()) {
                System.out.println(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
