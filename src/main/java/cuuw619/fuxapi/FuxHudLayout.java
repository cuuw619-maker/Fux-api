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
            if (Files.exists(FILE)) try (Reader r = Files.newBufferedReader(FILE)) {
                Data d = GSON.fromJson(r, Data.class);
                if (d != null) data = d;
            }
        } catch (Exception ignored) {}
        clamp();
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer w = Files.newBufferedWriter(FILE)) { GSON.toJson(data, w); }
        } catch (Exception ignored) {}
    }

    public static Data data() { return data; }
    public static void reset() { data = new Data(); save(); }

    public static void clamp() {
        data.compassX = c(data.compassX); data.compassY = c(data.compassY);
        data.infoX = c(data.infoX); data.infoY = c(data.infoY);
        data.targetEspX = c(data.targetEspX); data.targetEspY = c(data.targetEspY);
        data.watermarkX = c(data.watermarkX); data.watermarkY = c(data.watermarkY);
        data.moduleListX = c(data.moduleListX); data.moduleListY = c(data.moduleListY);
        data.coordsX = c(data.coordsX); data.coordsY = c(data.coordsY);
        data.keystrokesX = c(data.keystrokesX); data.keystrokesY = c(data.keystrokesY);
        data.compassScale = s(data.compassScale); data.infoScale = s(data.infoScale);
        data.targetEspScale = s(data.targetEspScale); data.watermarkScale = s(data.watermarkScale);
        data.moduleListScale = s(data.moduleListScale); data.coordsScale = s(data.coordsScale);
        data.keystrokesScale = s(data.keystrokesScale);
    }

    private static float c(float v) { return Math.max(0, Math.min(1, v)); }
    private static float s(float v) { return Math.max(.5F, Math.min(2F, v)); }

    public static final class Data {
        public float compassX=.50F, compassY=.035F, infoX=.955F, infoY=.035F;
        public float targetEspX=.50F, targetEspY=.68F;
        public float watermarkX=.035F, watermarkY=.035F;
        public float moduleListX=.035F, moduleListY=.18F;
        public float coordsX=.035F, coordsY=.90F;
        public float keystrokesX=.82F, keystrokesY=.78F;
        public float compassScale=1F, infoScale=1F, targetEspScale=1F;
        public float watermarkScale=1F, moduleListScale=1F, coordsScale=1F, keystrokesScale=1F;
    }
}
