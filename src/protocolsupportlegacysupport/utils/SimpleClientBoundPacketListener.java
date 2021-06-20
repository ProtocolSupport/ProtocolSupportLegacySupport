package protocolsupportlegacysupport.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import protocolsupport.api.Connection;
import protocolsupport.api.Connection.PacketListener;

public class SimpleClientBoundPacketListener extends PacketListener {

	protected final Connection connection;

	protected SimpleClientBoundPacketListener(Connection connection) {
		this.connection = connection;
	}

	private final Map<PacketType, PacketEventHandler> handlers = new HashMap<>();

	protected void registerHandler(PacketType type, PacketEventHandler handler) {
		handlers.put(type, handler);
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		ListIterator<Object> packetIterator = event.getPackets().listIterator();
		while (packetIterator.hasNext()) {
			Object packet = packetIterator.next();
			PacketContainer container = PacketContainer.fromPacket(packet);
			PacketEventHandler handler = handlers.get(container.getType());
			if (handler != null) {
				Collection<PacketContainer> addPackets = handler.handleEvent(connection, container);
				if (addPackets != null) {
					packetIterator.remove();
					if (!addPackets.isEmpty()) {
						for (PacketContainer addPacket : addPackets) {
							packetIterator.add(addPacket.getHandle());
						}
					}
				}
			}
		}
	}

	public static interface PacketEventHandler {

		public Collection<PacketContainer> handleEvent(Connection connection, PacketContainer container);

	}

}
