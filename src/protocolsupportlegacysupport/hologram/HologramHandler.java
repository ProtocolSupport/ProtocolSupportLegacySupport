package protocolsupportlegacysupport.hologram;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;

import protocolsupport.api.Connection;
import protocolsupport.api.Connection.PacketListener;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolType;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.api.events.ConnectionOpenEvent;
import protocolsupportlegacysupport.ProtocolSupportLegacySupport;
import protocolsupportlegacysupport.hologram.armorstand.ArmorStandData;
import protocolsupportlegacysupport.hologram.armorstand.ArmorStandTracker;
import protocolsupportlegacysupport.utils.Constants;

public class HologramHandler implements Listener {

	private static final String metadata_key = "PSLS_HOLOGRAM";

	public void start() {
		Bukkit.getPluginManager().registerEvents(this, ProtocolSupportLegacySupport.getInstance());
		ProtocolSupportAPI.getConnections().forEach(this::initConnection);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onConnectionOpen(ConnectionOpenEvent event) {
		initConnection(event.getConnection());
	}

	private void initConnection(Connection connection) {
		connection.addMetadata(metadata_key, new ArmorStandTracker());
		connection.addPacketListener(new PacketListener() {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (
					(connection.getVersion().getProtocolType() != ProtocolType.PC) ||
					connection.getVersion().isAfter(ProtocolVersion.MINECRAFT_1_7_10)
				) {
					return;
				}
				PacketContainer packet = PacketContainer.fromPacket(event.getPacket());
				if (packet.getType() == PacketType.Play.Server.SPAWN_ENTITY) {
					if (packet.getIntegers().read(6) != Constants.ARMORSTAND_OBJECT_TYPE_ID) {
						return;
					}
					event.setCancelled(true);
					initArmorStand(connection, packet.getIntegers().read(0), packet.getDoubles());
				} else if (packet.getType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
					if (packet.getIntegers().read(1) != Constants.ARMORSTAND_LIVING_TYPE_ID) {
						return;
					}
					event.setCancelled(true);
					int entityId = packet.getIntegers().read(0);
					initArmorStand(connection, entityId, packet.getDoubles());
					ArmorStandData data = getArmorStand(connection, entityId);
					data.addMeta(packet.getDataWatcherModifier().read(0));
				} else if (packet.getType() == PacketType.Play.Server.ENTITY_DESTROY) {
					int[] entityIds = packet.getIntegerArrays().read(0);
					for (int entityId : entityIds) {
						ArmorStandData data = getArmorStand(connection, entityId);
						if (data == null) {
							continue;
						}
						data.destroy();
						getTracker(connection).removeArmorStand(entityId);
					}
				} else if (packet.getType() == PacketType.Play.Server.ENTITY_METADATA) {
					ArmorStandData data = getArmorStand(connection, packet.getIntegers().read(0));
					if (data == null) {
						return;
					}
					event.setCancelled(true);
					data.addMeta(packet.getWatchableCollectionModifier().read(0));
				} else if (packet.getType() == PacketType.Play.Server.ENTITY_TELEPORT) {
					ArmorStandData data = getArmorStand(connection, packet.getIntegers().read(0));
					if (data == null) {
						return;
					}
					event.setCancelled(true);
					StructureModifier<Double> doubles = packet.getDoubles();
					data.setLocation(new Vector(doubles.read(0), doubles.read(1), doubles.read(2)));
				}
			}
		});
	}

	protected ArmorStandTracker getTracker(Connection connection) {
		return ((ArmorStandTracker) connection.getMetadata(metadata_key));
	}

	protected ArmorStandData getArmorStand(Connection connection, int entityId) {
		return getTracker(connection).getArmorStand(entityId);
	}

	protected void initArmorStand(Connection connection, int entityId, StructureModifier<Double> doubles) {
		ArmorStandData existing = getArmorStand(connection, entityId);
		if (existing != null) {
			existing.destroy();
		}
		double x = doubles.read(0);
		double y = doubles.read(1);
		double z = doubles.read(2);
		getTracker(connection).addArmorStand(connection, entityId, new Vector(x, y, z));
	}

}
