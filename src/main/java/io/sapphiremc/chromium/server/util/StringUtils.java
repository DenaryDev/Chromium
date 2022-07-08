/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.server.util;

import java.io.BufferedReader;
import java.io.IOException;

public class StringUtils {

    public static String readString(BufferedReader reader) throws IOException {
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        return response.toString();
    }
}
