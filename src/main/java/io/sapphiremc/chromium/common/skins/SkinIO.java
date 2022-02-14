/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.common.skins;

import com.mojang.authlib.properties.Property;
import io.sapphiremc.chromium.common.util.skins.FileUtils;
import io.sapphiremc.chromium.common.util.skins.JsonUtils;
import java.nio.file.Path;
import java.util.UUID;

public class SkinIO {

    private static final String FILE_EXTENSION = ".json";

    private final Path savePath;

    public SkinIO(Path savePath) {
        this.savePath = savePath;
    }

    public Property loadSkin(UUID uuid) {
        return JsonUtils.fromJson(FileUtils.readFile(savePath.resolve(uuid + FILE_EXTENSION).toFile()), Property.class);
    }

    public void saveSkin(UUID uuid, Property skin) {
        FileUtils.writeFile(savePath.toFile(), uuid + FILE_EXTENSION, JsonUtils.toJson(skin));
    }
}
