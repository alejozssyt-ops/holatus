package com.alejozss.aplanar.client;

import com.alejozss.aplanar.AplanarConfig;

public final class AplanarColors {
	private AplanarColors() {
	}

	public static double animationTime() {
		return System.nanoTime() / 1_000_000_000.0;
	}

	public static int colorAt(AplanarConfig config, double perimeterT, double time) {
		double red;
		double green;
		double blue;

		if (config.colorMode == AplanarConfig.ColorMode.SOLID) {
			red = config.red;
			green = config.green;
			blue = config.blue;
		} else {
			int count = config.colorCount;
			double phase = perimeterT * count + time * config.animationSpeed;
			double whole = Math.floor(phase);
			double fraction = phase - whole;
			int first = Math.floorMod((int) whole, count);
			int second = (first + 1) % count;

			red = lerp(config.paletteRed[first], config.paletteRed[second], fraction);
			green = lerp(config.paletteGreen[first], config.paletteGreen[second], fraction);
			blue = lerp(config.paletteBlue[first], config.paletteBlue[second], fraction);
		}

		double brightness = config.brightness;
		int alpha = (int) Math.round(config.opacity * 255.0);
		return (alpha << 24)
				| (clampChannel(red * brightness) << 16)
				| (clampChannel(green * brightness) << 8)
				| clampChannel(blue * brightness);
	}

	private static double lerp(int first, int second, double fraction) {
		return first + (second - first) * fraction;
	}

	private static int clampChannel(double value) {
		return (int) Math.max(0, Math.min(255, Math.round(value)));
	}
}
