package com.alejozss.aplanar;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;

public final class AplanarArea {
	private AplanarArea() {
	}

	public static boolean isHorizontal(Direction face) {
		return face.getAxis() == Direction.Axis.Y;
	}

	public static int minOffset(int size) {
		return -(size / 2);
	}

	public static int maxOffset(int size) {
		return size - size / 2 - 1;
	}

	/**
	 * Devuelve todas las posiciones del cuadrado centrado en {@code center}.
	 * Si la cara es superior/inferior el cuadrado es un plano horizontal (Y fija).
	 * Si la cara es lateral el cuadrado es un plano vertical:
	 * caras este/oeste usan X fija y caras norte/sur usan Z fija.
	 */
	public static List<BlockPos> positions(BlockPos center, Direction face, int size) {
		int start = minOffset(size);
		int end = maxOffset(size);
		List<BlockPos> result = new ArrayList<>(size * size);
		Direction.Axis axis = face.getAxis();

		for (int a = start; a <= end; a++) {
			for (int b = start; b <= end; b++) {
				if (axis == Direction.Axis.Y) {
					result.add(center.offset(a, 0, b));
				} else if (axis == Direction.Axis.X) {
					result.add(center.offset(0, a, b));
				} else {
					result.add(center.offset(a, b, 0));
				}
			}
		}

		return result;
	}

	public static int countNonAir(BlockGetter level, BlockPos center, Direction face, int size) {
		int start = minOffset(size);
		int end = maxOffset(size);
		Direction.Axis axis = face.getAxis();
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		int count = 0;

		for (int a = start; a <= end; a++) {
			for (int b = start; b <= end; b++) {
				if (axis == Direction.Axis.Y) {
					cursor.setWithOffset(center, a, 0, b);
				} else if (axis == Direction.Axis.X) {
					cursor.setWithOffset(center, 0, a, b);
				} else {
					cursor.setWithOffset(center, a, b, 0);
				}
				if (!level.getBlockState(cursor).isAir()) {
					count++;
				}
			}
		}

		return count;
	}
}
