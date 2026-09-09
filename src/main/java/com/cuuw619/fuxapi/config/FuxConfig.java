package com.cuuw619.fuxapi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FuxConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("fuxapi.json");
    private static Data data = new Data();

    private FuxConfig() {}

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
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException ignored) {}
    }

    public static Data data() { return data; }

    public static final class Data {
        public boolean animations = true;
        public boolean blur = true;
        public boolean compactMode = false;
        public double uiScale = 1.0;
        public String accent = "PURPLE";
    }
}
