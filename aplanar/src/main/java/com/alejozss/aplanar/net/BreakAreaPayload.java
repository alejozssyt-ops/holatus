package com.alejozss.aplanar.net;

import com.alejozss.aplanar.AplanarMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record BreakAreaPayload(BlockPos center, Direction face, int size) implements CustomPacketPayload {
	public static final Type<BreakAreaPayload> ID =
			new Type<>(Identifier.fromNamespaceAndPath(AplanarMod.MOD_ID, "break_area"));

	public static final StreamCodec<RegistryFriendlyByteBuf, BreakAreaPayload> CODEC = StreamCodec.of(
			(buf, payload) -> {
				buf.writeBlockPos(payload.center());
				buf.writeEnum(payload.face());
				buf.writeVarInt(payload.size());
			},
			buf -> new BreakAreaPayload(buf.readBlockPos(), buf.readEnum(Direction.class), buf.readVarInt())
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
