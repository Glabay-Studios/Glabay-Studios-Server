package io.xeros.model.entity.player;

public interface PacketType {
	void processPacket(Player c, int packetType, int packetSize);
}
