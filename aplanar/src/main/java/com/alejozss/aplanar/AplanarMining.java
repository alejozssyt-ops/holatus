package com.alejozss.aplanar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class AplanarMining {
	private static final int INSTANT_BATCH = 50_000;
	private static final Map<UUID, Job> JOBS = new HashMap<>();

	private AplanarMining() {
	}

	public static void start(ServerPlayer player, BlockPos center, Direction face, int size) {
		Job previous = JOBS.remove(player.getUUID());
		if (previous != null) {
			previous.cancel(player);
		}

		List<BlockPos> positions = AplanarArea.positions(center, face, size);
		JOBS.put(player.getUUID(), new Job(player.level(), center, face, positions, size));
	}

	public static void tick(MinecraftServer server) {
		if (JOBS.isEmpty()) {
			return;
		}

		JOBS.entrySet().removeIf(entry -> {
			Job job = entry.getValue();
			ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
			if (player == null) {
				return true;
			}
			if (!player.isAlive() || player.isSpectator() || player.level() != job.level) {
				job.cancel(player);
				return true;
			}

			job.tick(player);
			return job.finished;
		});
	}

	private static final class Job {
		private final ServerLevel level;
		private final BlockPos center;
		private final List<BlockPos> positions;
		private final AABB dropArea;
		private final Vec3 dropTarget;
		private float progress;
		private int shownStage = -1;
		private Item lastTool;
		private int instantCursor;
		private boolean centerDone;
		private boolean finished;

		private Job(ServerLevel level, BlockPos center, Direction face, List<BlockPos> positions, int size) {
			this.level = level;
			this.center = center;
			this.positions = positions;
			this.dropTarget = new Vec3(center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5);

			int start = AplanarArea.minOffset(size);
			int end = AplanarArea.maxOffset(size) + 1;
			double minX = center.getX();
			double minY = center.getY();
			double minZ = center.getZ();
			double maxX = center.getX() + 1;
			double maxY = center.getY() + 1;
			double maxZ = center.getZ() + 1;

			if (face.getAxis() == Direction.Axis.Y) {
				minX = center.getX() + start;
				maxX = center.getX() + end;
				minZ = center.getZ() + start;
				maxZ = center.getZ() + end;
			} else if (face.getAxis() == Direction.Axis.X) {
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

			this.dropArea = new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(1.0);
		}

		private void tick(ServerPlayer player) {
			if (!centerDone) {
				mineCenter(player);
			}
			if (centerDone && !finished) {
				breakRemaining(player);
			}
		}

		private void mineCenter(ServerPlayer player) {
			BlockState state = level.getBlockState(center);
			if (state.isAir()) {
				clearProgress(player);
				centerDone = true;
				return;
			}

			if (player.isCreative()) {
				player.gameMode.destroyBlock(center);
				clearProgress(player);
				centerDone = true;
				return;
			}

			ItemStack heldItem = player.getMainHandItem();
			Item tool = heldItem.getItem();
			if (tool != lastTool) {
				lastTool = tool;
				clearProgress(player);
			}

			float delta = state.getDestroyProgress(player, level, center);
			if (delta <= 0.0F) {
				clearProgress(player);
				centerDone = true;
				return;
			}

			progress += delta;
			int stage = Math.min(9, (int) (progress * 10.0F));
			if (stage != shownStage) {
				level.destroyBlockProgress(player.getId(), center, stage);
				shownStage = stage;
			}

			if (progress >= 1.0F) {
				level.destroyBlockProgress(player.getId(), center, -1);
				player.gameMode.destroyBlock(center);
				clearProgress(player);
				centerDone = true;
			}
		}

		private void breakRemaining(ServerPlayer player) {
			boolean creative = player.isCreative();
			int processed = 0;

			while (instantCursor < positions.size() && processed < INSTANT_BATCH) {
				BlockPos pos = positions.get(instantCursor++);
				processed++;

				if (pos.equals(center)) {
					continue;
				}

				BlockState state = level.getBlockState(pos);
				if (state.isAir()) {
					continue;
				}
				if (!creative && state.getDestroySpeed(level, pos) < 0.0F) {
					continue;
				}

				player.gameMode.destroyBlock(pos);
			}

			if (processed > 0) {
				moveDropsToCenter();
			}

			if (instantCursor >= positions.size()) {
				finished = true;
			}
		}

		private void moveDropsToCenter() {
			for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, dropArea)) {
				if (item.getAge() != 0) {
					continue;
				}
				item.setPos(dropTarget.x, dropTarget.y, dropTarget.z);
				item.setDeltaMovement(Vec3.ZERO);
			}
		}

		private void clearProgress(ServerPlayer player) {
			if (shownStage >= 0) {
				level.destroyBlockProgress(player.getId(), center, -1);
			}
			progress = 0.0F;
			shownStage = -1;
		}

		private void cancel(ServerPlayer player) {
			if (player != null) {
				clearProgress(player);
			}
			finished = true;
		}
	}
}
