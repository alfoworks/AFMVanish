package ru.alfomine.afmvanish.hooks;

import gloomyfolken.hooklib.minecraft.HookLoader;
import gloomyfolken.hooklib.minecraft.PrimaryClassTransformer;

public class VanishHookLoader extends HookLoader {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{PrimaryClassTransformer.class.getName()};
    }

    @Override
    protected void registerHooks() {
        registerHookContainer("ru.alfomine.afmvanish.hooks.VanishHookContainer");
    }
}
