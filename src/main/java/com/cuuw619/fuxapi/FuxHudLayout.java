package com.cuuw619.fuxapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FuxHudLayout {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("fuxapi-hud.json");
    private static Data data = new Data();

    private FuxHudLayout() {}

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                try (Reader reader = Files.newBufferedReader(FILE)) {
                    Data loaded = GSON.fromJson(reader, Data.class);
                    if (loaded != null) data = loaded;
                }
            }
        } catch (Exception ignored) {
            data = new Data();
        }
        clamp();
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception ignored) {}
    }

    public static Data data() { return data; }

    public static void reset() {
        data = new Data();
        save();
    }

    public static void clamp() {
        data.compassX = clamp01(data.compassX);
        data.compassY = clamp01(data.compassY);
        data.infoX = clamp01(data.infoX);
        data.infoY = clamp01(data.infoY);
        data.targetEspX = clamp01(data.targetEspX);
        data.targetEspY = clamp01(data.targetEspY);
    }

    private static float clamp01(float v) {
        return Math.max(0.0F, Math.min(1.0F, v));
    }

    public static final class Data {
        public float compassX = 0.50F;
        public float compassY = 0.035F;
        public float infoX = 0.955F;
        public float infoY = 0.035F;
        public float targetEspX = 0.50F;
        public float targetEspY = 0.68F;
    }
}
