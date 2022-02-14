/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.compat.bobby;

import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class BobbyCompatMixinPlugin implements IMixinConfigPlugin {
    private final List<String> allowedBobbyVersions = List.of("3.1.0");
    private boolean validBobbyVersion = false;

    @Override
    public void onLoad(String mixinPackage) {
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)) return;
        validBobbyVersion = FabricLoader.getInstance().getModContainer("bobby").map(iris -> {
            String version = iris.getMetadata().getVersion().getFriendlyString();

            return allowedBobbyVersions.contains(version);
        }).orElse(false);

        if (!validBobbyVersion) {
            System.err.println("[Chromium] Invalid/missing version of Bobby detected, disabling compatibility mixins!");
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return validBobbyVersion;
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
}
