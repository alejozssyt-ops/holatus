package com.alejozss.aplanar.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record Selection(BlockPos center, Direction face, int size, int blockCount) {
}
