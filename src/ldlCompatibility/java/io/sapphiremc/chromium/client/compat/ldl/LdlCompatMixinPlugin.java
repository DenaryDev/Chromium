/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.compat.ldl;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class LdlCompatMixinPlugin implements IMixinConfigPlugin {
    private final List<AllowedVersion> allowedVersions = List.of(
            new AllowedVersion("2.3.1"),
            new AllowedVersion("2.3.2"),
            new AllowedVersion("2.3.3")
    );
    private boolean isVersionValid = false;

    @Override
    public void onLoad(String mixinPackage) {
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)) return;
        isVersionValid = FabricLoader.getInstance().getModContainer("lambdynlights").map(mod -> {
            final var version = mod.getMetadata().getVersion().getFriendlyString();

            return isAllowedVersion(version);
        }).orElse(false);

        if (!isVersionValid) {
            System.err.println("[Chromium] Invalid/missing version of LambDynamicLights detected, disabling compatibility mixins!");
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return isVersionValid;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    private boolean isAllowedVersion(String version) {
        for (final var allowed : allowedVersions) {
            if (allowed.matches(version)) {
                return true;
            }
        }

        return false;
    }

    private record AllowedVersion(String version) {

        private boolean matches(String candidate) {
            return candidate.startsWith(version);
        }
    }
}
