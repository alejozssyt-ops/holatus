package com.alejozss.aplanar.client;

import com.alejozss.aplanar.AplanarArea;
import com.alejozss.aplanar.AplanarConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.phys.Vec3;

/**
 * 26.x ya no expone MultiBufferSource/RenderTypes para dibujar lineas en el mundo,
 * asi que el recuadro se dibuja con la API de Gizmos de vanilla. Un Gizmo de linea solo
 * admite UN color, por lo que cada arista se trocea en tramos cortos para conservar
 * el degradado/animacion de colores que tenia la version 1.21.11.
 */
public final class SelectionRenderer {
	private static final float EXPAND = 0.002F;
	private static final int MAX_SEGMENTS_PER_EDGE = 48;

	private SelectionRenderer() {
	}

	/** Se llama una vez por frame desde LevelRenderEvents.BEFORE_GIZMOS. */
	public static void render() {
		Selection selection = AplanarClient.getSelection();
		if (selection == null || !AplanarClient.isActive()) {
			return;
		}

		BlockPos center = selection.center();
		int start = AplanarArea.minOffset(selection.size());
		int end = AplanarArea.maxOffset(selection.size()) + 1;
		Direction.Axis axis = selection.face().getAxis();

		int minX = center.getX();
		int minY = center.getY();
		int minZ = center.getZ();
		int maxX = center.getX() + 1;
		int maxY = center.getY() + 1;
		int maxZ = center.getZ() + 1;

		if (axis == Direction.Axis.Y) {
			minX = center.getX() + start;
			maxX = center.getX() + end;
			minZ = center.getZ() + start;
			maxZ = center.getZ() + end;
		} else if (axis == Direction.Axis.X) {
			minY = center.getY() + start;
			maxY = center.getY() + end;
			minZ = center.getZ() + start;
			maxZ = center.getZ() + end;
		} else {
			minX = center.getX() + start;
			maxX = center.getX() + end;
			minY = center.getY() + start;
			maxY = center.getY() + end;
		}

		AplanarConfig config = AplanarConfig.get();
		double time = AplanarColors.animationTime();
		Bounds bounds = new Bounds(minX, minY, minZ, maxX, maxY, maxZ, selection.face().getAxis());

		line(bounds, config, time, minX, minY, minZ, maxX, minY, minZ);
		line(bounds, config, time, maxX, minY, minZ, maxX, minY, maxZ);
		line(bounds, config, time, maxX, minY, maxZ, minX, minY, maxZ);
		line(bounds, config, time, minX, minY, maxZ, minX, minY, minZ);

		line(bounds, config, time, minX, maxY, minZ, maxX, maxY, minZ);
		line(bounds, config, time, maxX, maxY, minZ, maxX, maxY, maxZ);
		line(bounds, config, time, maxX, maxY, maxZ, minX, maxY, maxZ);
		line(bounds, config, time, minX, maxY, maxZ, minX, maxY, minZ);

		line(bounds, config, time, minX, minY, minZ, minX, maxY, minZ);
		line(bounds, config, time, maxX, minY, minZ, maxX, maxY, minZ);
		line(bounds, config, time, maxX, minY, maxZ, maxX, maxY, maxZ);
		line(bounds, config, time, minX, minY, maxZ, minX, maxY, maxZ);
	}

	private static void line(Bounds bounds, AplanarConfig config, double time,
			int x1, int y1, int z1, int x2, int y2, int z2) {
		float lineWidth = config.lineWidth;
		int length = Math.abs(x2 - x1) + Math.abs(y2 - y1) + Math.abs(z2 - z1);
		int segments = Math.max(1, Math.min(MAX_SEGMENTS_PER_EDGE, length));

		double ax = x1 + bounds.offsetX(x1);
		double ay = y1 + bounds.offsetY(y1);
		double az = z1 + bounds.offsetZ(z1);
		double bx = x2 + bounds.offsetX(x2);
		double by = y2 + bounds.offsetY(y2);
		double bz = z2 + bounds.offsetZ(z2);

		for (int i = 0; i < segments; i++) {
			double t0 = (double) i / segments;
			double t1 = (double) (i + 1) / segments;
			// Color del tramo = color en su punto medio, sobre el perimetro del recuadro.
			double tm = (t0 + t1) / 2.0;
			double px = x1 + (x2 - x1) * tm;
			double py = y1 + (y2 - y1) * tm;
			double pz = z1 + (z2 - z1) * tm;
			int color = AplanarColors.colorAt(config, bounds.perimeterT(px, py, pz), time);

			Vec3 from = new Vec3(ax + (bx - ax) * t0, ay + (by - ay) * t0, az + (bz - az) * t0);
			Vec3 to = new Vec3(ax + (bx - ax) * t1, ay + (by - ay) * t1, az + (bz - az) * t1);
			Gizmos.line(from, to, color, lineWidth);
		}
	}

	private record Bounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Direction.Axis normalAxis) {
		private double perimeterT(double x, double y, double z) {
			double a;
			double b;
			int minA;
			int minB;
			int maxA;
			int maxB;

			if (normalAxis == Direction.Axis.Y) {
				a = x;
				b = z;
				minA = minX;
				minB = minZ;
				maxA = maxX;
				maxB = maxZ;
			} else if (normalAxis == Direction.Axis.X) {
				a = z;
				b = y;
				minA = minZ;
				minB = minY;
				maxA = maxZ;
				maxB = maxY;
			} else {
				a = x;
				b = y;
				minA = minX;
				minB = minY;
				maxA = maxX;
				maxB = maxY;
			}

			double width = maxA - minA;
			double height = maxB - minB;
			double perimeter = 2.0 * (width + height);
			if (perimeter <= 0.0) {
				return 0.0;
			}

			double u = a - minA;
			double v = b - minB;
			double distance;
			if (v <= 0.0) {
				distance = u;
			} else if (u >= width) {
				distance = width + v;
			} else if (v >= height) {
				distance = width + height + (width - u);
			} else {
				distance = 2.0 * width + height + (height - v);
			}
			return distance / perimeter;
		}

		private float offsetX(int x) {
			return offset(x, minX, maxX);
		}

		private float offsetY(int y) {
			return offset(y, minY, maxY);
		}

		private float offsetZ(int z) {
			return offset(z, minZ, maxZ);
		}

		private static float offset(int value, int min, int max) {
			if (value == min) {
				return -EXPAND;
			}
			if (value == max) {
				return EXPAND;
			}
			return 0.0F;
		}
	}
}
