package org.localmc.tools.hardcodepatcher.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.localmc.tools.hardcodepatcher.HardcodeTextPatcher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class HardcodeTextPatcherConfig {
    private static final Gson GSON = new Gson();
    private static final Path configFile = HardcodeTextPatcher.configPath.resolve("config.json");
    private static List<String> mods = new ArrayList<>();
    private static final DebugMode debug = new DebugMode();

    public static List<String> getMods() {
        return mods;
    }

    public static DebugMode getDebugMode() {
        return debug;
    }

    private static void writeConfig(JsonWriter jw) throws IOException {
        debug.writeJson(jw);
        jw.name("mods").beginArray();
        jw.name("mods").endArray();
    }

    public static void readConfig() throws IOException {
        File f = configFile.toFile();
        if (Files.notExists(configFile)) {
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            Files.createFile(configFile);
            JsonWriter jw = GSON.newJsonWriter(Files.newBufferedWriter(configFile));
            writeConfig(jw);
        }

        JsonReader jr = GSON.newJsonReader(new InputStreamReader(new FileInputStream(f)));

        jr.beginObject();
        while (jr.peek() != JsonToken.END_OBJECT) {
            switch (jr.nextName()) {
                case "debug_mode":
                    if (jr.peek() == JsonToken.BEGIN_OBJECT) {
                        debug.readJson(jr);
                    }
                    break;
                case "mods":
                    if (jr.peek() == JsonToken.BEGIN_ARRAY) {
                        mods = GSON.fromJson(jr, new TypeToken<List<String>>() {
                        }.getType());
                    }
                    break;
                default:
                    jr.skipValue();
                    break;
            }
        }
        jr.endObject();
    }
}
