package com.alejozss.aplanar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class AplanarConfig {
	public enum ColorMode {
		SOLID,
		ANIMATED
	}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("aplanar.json");
	public static final int MAX_SIZE = 333;
	private static final int PALETTE_SIZE = 6;
	private static final int[] DEFAULT_RED = {0, 255, 255, 80, 180, 255};
	private static final int[] DEFAULT_GREEN = {200, 60, 215, 255, 80, 255};
	private static final int[] DEFAULT_BLUE = {255, 60, 0, 120, 255, 255};
	private static AplanarConfig instance;

	public int red = 0;
	public int green = 200;
	public int blue = 255;
	public ColorMode colorMode = ColorMode.ANIMATED;
	public int colorCount = 3;
	public int[] paletteRed = DEFAULT_RED.clone();
	public int[] paletteGreen = DEFAULT_GREEN.clone();
	public int[] paletteBlue = DEFAULT_BLUE.clone();
	public float animationSpeed = 1.0F;
	public float brightness = 1.0F;
	public float lineWidth = 2.0F;
	public float opacity = 1.0F;
	public int configVersion = 1;
	public int defaultSize = 3;
	public int maxSize = MAX_SIZE;

	public static AplanarConfig get() {
		if (instance == null) {
			load();
		}
		return instance;
	}

	public static void load() {
		instance = null;
		if (Files.exists(PATH)) {
			try (Reader reader = Files.newBufferedReader(PATH)) {
				instance = GSON.fromJson(reader, AplanarConfig.class);
			} catch (Exception e) {
				AplanarMod.LOGGER.warn("No se pudo leer la configuracion de Aplanar, se usaran valores por defecto", e);
			}
		}
		if (instance == null) {
			instance = new AplanarConfig();
		}
		instance.clamp();
	}

	public static void save() {
		if (instance == null) {
			return;
		}
		instance.clamp();
		try {
			Files.createDirectories(PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(PATH)) {
				GSON.toJson(instance, writer);
			}
		} catch (Exception e) {
			AplanarMod.LOGGER.warn("No se pudo guardar la configuracion de Aplanar", e);
		}
	}

	public void clamp() {
		red = clampChannel(red);
		green = clampChannel(green);
		blue = clampChannel(blue);
		if (colorMode == null) {
			colorMode = ColorMode.ANIMATED;
		}
		colorCount = clampNumber(colorCount, 2, PALETTE_SIZE);
		paletteRed = clampPalette(paletteRed, DEFAULT_RED);
		paletteGreen = clampPalette(paletteGreen, DEFAULT_GREEN);
		paletteBlue = clampPalette(paletteBlue, DEFAULT_BLUE);
		animationSpeed = clampFloat(animationSpeed, 0.0F, 5.0F);
		brightness = clampFloat(brightness, 0.1F, 2.0F);
		lineWidth = clampFloat(lineWidth, 1.0F, 6.0F);
		opacity = clampFloat(opacity, 0.1F, 1.0F);
		if (configVersion < 3) {
			maxSize = MAX_SIZE;
			configVersion = 3;
		}
		maxSize = clampNumber(maxSize, 1, MAX_SIZE);
		if (maxSize % 2 == 0) {
			maxSize--;
		}
		defaultSize = clampNumber(defaultSize, 1, maxSize);
		if (defaultSize % 2 == 0) {
			defaultSize--;
		}
	}

	public int solidArgb() {
		return 0xFF000000 | (red << 16) | (green << 8) | blue;
	}

	public int paletteArgb(int index) {
		int i = clampNumber(index, 0, PALETTE_SIZE - 1);
		return 0xFF000000 | (paletteRed[i] << 16) | (paletteGreen[i] << 8) | paletteBlue[i];
	}

	private static int[] clampPalette(int[] values, int[] defaults) {
		int[] result = new int[PALETTE_SIZE];
		for (int i = 0; i < PALETTE_SIZE; i++) {
			int value = values != null && i < values.length ? values[i] : defaults[i];
			result[i] = clampChannel(value);
		}
		return result;
	}

	private static int clampChannel(int value) {
		return clampNumber(value, 0, 255);
	}

	private static int clampNumber(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	private static float clampFloat(float value, float min, float max) {
		if (!Float.isFinite(value)) {
			return min;
		}
		return Math.max(min, Math.min(max, value));
	}
}
