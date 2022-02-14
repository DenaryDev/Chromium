/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.common.skins;

public enum SkinVariant {
    CLASSIC,
    SLIM;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
