package protocolsupportlegacysupport.utils;

import java.util.HashMap;
import java.util.Map;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.collection.BiFunction;

import protocolsupport.api.Connection;
import protocolsupport.api.Connection.PacketListener;

public class ClientBoundPacketListener extends PacketListener {

	protected final Connection connection;
	protected ClientBoundPacketListener(Connection connection) {
		this.connection = connection;
	}

	private final Map<PacketType, BiFunction<Connection, PacketContainer, Boolean>> handlers = new HashMap<>();

	protected void registerHandler(PacketType type, BiFunction<Connection, PacketContainer, Boolean> handler) {
		handlers.put(type, handler);
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		PacketContainer packet = PacketContainer.fromPacket(event.getPacket());
		BiFunction<Connection, PacketContainer, Boolean> handler = handlers.get(packet.getType());
		if (handler == null) {
			return;
		}
		if (handler.apply(connection, packet)) {
			event.setCancelled(true);
		}
	}

}
