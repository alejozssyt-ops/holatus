package com.alejozss.aplanar.client;

import com.alejozss.aplanar.AplanarConfig;
import java.util.Locale;
import java.util.function.DoubleConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AplanarConfigScreen extends Screen {
	private static final int PAGE_GENERAL = 0;
	private static final int PAGE_COLORS = 1;
	private static final int CONTROL_WIDTH = 220;
	private static final int ROW_HEIGHT = 22;

	private final Screen parent;
	private final AplanarConfig config;
	private int page = PAGE_GENERAL;
	private int selectedColor;

	public AplanarConfigScreen(Screen parent) {
		super(Component.translatable("aplanar.config.title"));
		this.parent = parent;
		this.config = AplanarConfig.get();
	}

	@Override
	protected void init() {
		int cx = width / 2;
		int left = cx - CONTROL_WIDTH / 2;
		selectedColor = Math.max(0, Math.min(selectedColor, config.colorCount - 1));

		if (page == PAGE_GENERAL) {
			initGeneral(left);
		} else {
			initColors(left);
		}
	}

	private void initGeneral(int left) {
		boolean animated = config.colorMode == AplanarConfig.ColorMode.ANIMATED;
		int rows = animated ? 7 : 5;
		int top = Math.max(24, (height - rows * ROW_HEIGHT - 32) / 2);
		int y = top;

		addRenderableWidget(Button.builder(modeLabel(), button -> {
			config.colorMode = animated
					? AplanarConfig.ColorMode.SOLID
					: AplanarConfig.ColorMode.ANIMATED;
			refresh();
		}).bounds(left, y, CONTROL_WIDTH, 20).build());
		y += ROW_HEIGHT;

		if (animated) {
			addRenderableWidget(Button.builder(Component.translatable("aplanar.config.colors", config.colorCount),
					button -> {
						config.colorCount = config.colorCount >= 6 ? 2 : config.colorCount + 1;
						selectedColor = Math.min(selectedColor, config.colorCount - 1);
						refresh();
					}).bounds(left, y, CONTROL_WIDTH, 20).build());
			y += ROW_HEIGHT;

			addRenderableWidget(new ValueSlider(left, y, CONTROL_WIDTH, 20, "aplanar.config.speed",
					config.animationSpeed, 0.0, 5.0, false, value -> config.animationSpeed = (float) value));
			y += ROW_HEIGHT;
		}

		addRenderableWidget(new ValueSlider(left, y, CONTROL_WIDTH, 20, "aplanar.config.brightness",
				config.brightness, 0.1, 2.0, false, value -> config.brightness = (float) value));
		y += ROW_HEIGHT;

		addRenderableWidget(new ValueSlider(left, y, CONTROL_WIDTH, 20, "aplanar.config.line_width",
				config.lineWidth, 1.0, 6.0, false, value -> config.lineWidth = (float) value));
		y += ROW_HEIGHT;

		addRenderableWidget(new ValueSlider(left, y, CONTROL_WIDTH, 20, "aplanar.config.opacity",
				config.opacity, 0.1, 1.0, false, value -> config.opacity = (float) value));
		y += ROW_HEIGHT;

		addRenderableWidget(Button.builder(Component.translatable("aplanar.config.edit_colors"), button -> {
			page = PAGE_COLORS;
			refresh();
		}).bounds(left, y, CONTROL_WIDTH, 20).build());

		int doneY = Math.min(top + rows * ROW_HEIGHT + 4, height - 28);
		addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
				.bounds(left, doneY, CONTROL_WIDTH, 20)
				.build());
	}

	private void initColors(int left) {
		boolean animated = config.colorMode == AplanarConfig.ColorMode.ANIMATED;
		int rows = animated ? 6 : 4;
		int top = Math.max(24, (height - rows * ROW_HEIGHT - 48) / 2);
		int y = top;

		if (animated) {
			addRenderableWidget(Button.builder(Component.translatable("aplanar.config.colors", config.colorCount),
					button -> {
						config.colorCount = config.colorCount >= 6 ? 2 : config.colorCount + 1;
						selectedColor = Math.min(selectedColor, config.colorCount - 1);
						refresh();
					}).bounds(left, y, CONTROL_WIDTH, 20).build());
			y += ROW_HEIGHT;

			int slotWidth = 34;
			int gap = 2;
			int startX = width / 2 - (config.colorCount * slotWidth + (config.colorCount - 1) * gap) / 2;
			for (int i = 0; i < config.colorCount; i++) {
				int index = i;
				String label = selectedColor == i ? "[" + (i + 1) + "]" : Integer.toString(i + 1);
				addRenderableWidget(Button.builder(Component.literal(label), button -> {
					selectedColor = index;
					refresh();
				}).bounds(startX + i * (slotWidth + gap), y, slotWidth, 20).build());
			}
			y += ROW_HEIGHT;
		}

		if (animated) {
			int index = selectedColor;
			addRenderableWidget(new ValueSlider(left, y, CONTROL_WIDTH, 20, "aplanar.config.red",
					config.paletteRed[index], 0, 255, true,
					value -> config.paletteRed[index] = (int) Math.round(value)));
			y += ROW_HEIGHT;
			addRenderableWidget(new ValueSlider(left, y, CONTROL_WIDTH, 20, "aplanar.config.green",
					config.paletteGreen[index], 0, 255, true,
					value -> config.paletteGreen[index] = (int) Math.round(value)));
			y += ROW_HEIGHT;
			addRenderableWidget(new ValueSlider(left, y, CONTROL_WIDTH, 20, "aplanar.config.blue",
					config.paletteBlue[index], 0, 255, true,
					value -> config.paletteBlue[index] = (int) Math.round(value)));
		} else {
			addRenderableWidget(new ValueSlider(left, y, CONTROL_WIDTH, 20, "aplanar.config.red",
					config.red, 0, 255, true, value -> config.red = (int) Math.round(value)));
			y += ROW_HEIGHT;
			addRenderableWidget(new ValueSlider(left, y, CONTROL_WIDTH, 20, "aplanar.config.green",
					config.green, 0, 255, true, value -> config.green = (int) Math.round(value)));
			y += ROW_HEIGHT;
			addRenderableWidget(new ValueSlider(left, y, CONTROL_WIDTH, 20, "aplanar.config.blue",
					config.blue, 0, 255, true, value -> config.blue = (int) Math.round(value)));
		}

		int buttonWidth = CONTROL_WIDTH / 2 - 2;
		addRenderableWidget(Button.builder(Component.translatable("gui.back"), button -> {
			page = PAGE_GENERAL;
			refresh();
		}).bounds(left, height - 28, buttonWidth, 20).build());
		addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
				.bounds(left + buttonWidth + 4, height - 28, buttonWidth, 20).build());
	}

	private Component modeLabel() {
		return Component.translatable("aplanar.config.mode")
				.append(": ")
				.append(Component.translatable(config.colorMode == AplanarConfig.ColorMode.SOLID
						? "aplanar.config.mode.solid"
						: "aplanar.config.mode.animated"));
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(graphics, mouseX, mouseY, delta);
		int cx = width / 2;

		if (page == PAGE_GENERAL) {
			int rows = config.colorMode == AplanarConfig.ColorMode.ANIMATED ? 7 : 5;
			int top = Math.max(24, (height - rows * ROW_HEIGHT - 32) / 2);
			graphics.centeredText(font, title, cx, top - 18, 0xFFFFFF);
		} else {
			graphics.centeredText(font, Component.translatable("aplanar.config.colors.title"),
					cx, Math.max(24, (height - (config.colorMode == AplanarConfig.ColorMode.ANIMATED ? 6 : 4)
							* ROW_HEIGHT - 48) / 2) - 18, 0xFFFFFF);
			renderPreview(graphics, cx - CONTROL_WIDTH / 2, height - 56);
		}
	}

	private void renderPreview(GuiGraphicsExtractor graphics, int x, int y) {
		int width = CONTROL_WIDTH;
		double time = AplanarColors.animationTime();
		for (int i = 0; i < width; i++) {
			double t = width <= 1 ? 0.0 : (double) i / (width - 1);
			graphics.fill(x + i, y, x + i + 1, y + 20, AplanarColors.colorAt(config, t, time));
		}
		graphics.outline(x, y, width, 20, 0xFFFFFFFF);
	}

	private void refresh() {
		if (minecraft != null) {
			minecraft.execute(() -> {
				if (minecraft.gui.screen() == this) {
					rebuildWidgets();
				}
			});
		}
	}

	@Override
	public void onClose() {
		AplanarConfig.save();
		minecraft.gui.setScreen(parent);
	}

	private static class ValueSlider extends AbstractSliderButton {
		private final String translationKey;
		private final double min;
		private final double max;
		private final boolean integer;
		private final DoubleConsumer setter;

		private ValueSlider(int x, int y, int width, int height, String translationKey,
				double initial, double min, double max, boolean integer, DoubleConsumer setter) {
			super(x, y, width, height, Component.empty(), (initial - min) / (max - min));
			this.translationKey = translationKey;
			this.min = min;
			this.max = max;
			this.integer = integer;
			this.setter = setter;
			updateMessage();
		}

		@Override
		protected void updateMessage() {
			double current = min + value * (max - min);
			String formatted = integer
					? Integer.toString((int) Math.round(current))
					: String.format(Locale.ROOT, "%.2f", current);
			setMessage(Component.translatable(translationKey).append(": ").append(formatted));
		}

		@Override
		protected void applyValue() {
			setter.accept(min + value * (max - min));
		}
	}
}
