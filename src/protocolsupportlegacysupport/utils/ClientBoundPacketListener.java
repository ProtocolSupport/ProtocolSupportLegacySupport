package protocolsupportlegacysupport.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import protocolsupport.api.Connection;
import protocolsupport.api.Connection.PacketListener;

public class ClientBoundPacketListener extends PacketListener {

	protected final Connection connection;

	protected ClientBoundPacketListener(Connection connection) {
		this.connection = connection;
	}

	private final Map<PacketType, BiPredicate<Connection, PacketContainer>> handlers = new HashMap<>();

	protected void registerHandler(PacketType type, BiPredicate<Connection, PacketContainer> handler) {
		handlers.put(type, handler);
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		PacketContainer packet = PacketContainer.fromPacket(event.getPacket());
		BiPredicate<Connection, PacketContainer> handler = handlers.get(packet.getType());
		if (handler == null) {
			return;
		}
		if (handler.test(connection, packet)) {
			event.setCancelled(true);
		}
	}

}
