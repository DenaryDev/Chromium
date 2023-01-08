/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.compat.ldl;

import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class LdlCompatMixinPlugin implements IMixinConfigPlugin {
    private final List<AllowedVersion> allowedVersions = List.of(
            new AllowedVersion("2.2.0", true)
    );
    private boolean isVersionValid = false;

    @Override
    public void onLoad(String mixinPackage) {
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)) return;
        isVersionValid = FabricLoader.getInstance().getModContainer("lambdynlights").map(mod -> {
            String version = mod.getMetadata().getVersion().getFriendlyString();

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
        for (AllowedVersion allowed : allowedVersions) {
            if (allowed.matches(version)) {
                return true;
            }
        }

        return false;
    }

    private record AllowedVersion(String version, boolean prefix) {

        private boolean matches(String candidate) {
            if (prefix) {
                return candidate.startsWith(version);
            } else {
                return candidate.equals(version);
            }
        }
    }
}
