/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

    public static String readFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return StringUtils.readString(reader);
        } catch (IOException e) {
            return null;
        }
    }

    public static void writeFile(File path, String fileName, String content) {
        try {
            if (!path.exists())
                path.mkdirs();

            File file = new File(path, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            }
        } catch (IOException ignored) {
        }
    }
}
