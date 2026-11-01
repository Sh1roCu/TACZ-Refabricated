package cn.sh1rocu.tacz.api.mixin;

import net.minecraft.server.packs.repository.RepositorySource;

public interface PackRepositoryExtension {
    default void tacz$addPackFinder(RepositorySource packFinder) {
        throw new RuntimeException("PackRepository implementation does not support adding sources!");
    }
}