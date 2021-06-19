package protocolsupportlegacysupport.features.hologram;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.reflect.StructureModifier;

import protocolsupport.api.Connection;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolType;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.api.events.ConnectionOpenEvent;
import protocolsupportlegacysupport.ProtocolSupportLegacySupport;
import protocolsupportlegacysupport.features.AbstractFeature;
import protocolsupportlegacysupport.features.hologram.armorstand.ArmorStandData;
import protocolsupportlegacysupport.features.hologram.armorstand.ArmorStandTracker;
import protocolsupportlegacysupport.utils.ClientBoundPacketListener;
import protocolsupportlegacysupport.utils.PacketUtils;

public class HologramHandler extends AbstractFeature<Void> implements Listener {

	@Override
	protected void enable0(Void config) {
		Bukkit.getPluginManager().registerEvents(this, ProtocolSupportLegacySupport.getInstance());
		ProtocolSupportAPI.getConnections().forEach(this::initConnection);
	}

	@Override
	protected void disable0() {
		HandlerList.unregisterAll();
		for (Connection connection : ProtocolSupportAPI.getConnections()) {
			ArmorStandTracker tracker = connection.removeMetadata(metadata_key);
			if (tracker != null) {
				tracker.destroyAll();
			}
		}
	}

	private static final String metadata_key = "PSLS_HOLOGRAM";

	@EventHandler(priority = EventPriority.LOWEST)
	public void onConnectionOpen(ConnectionOpenEvent event) {
		initConnection(event.getConnection());
	}

	private void initConnection(Connection connection) {
		connection.addMetadata(metadata_key, new ArmorStandTracker());
		connection.addPacketListener(new ClientBoundPacketListener(connection) {
			{
				registerHandler(PacketType.Play.Server.RESPAWN, (connection, packet) -> {
					ArmorStandTracker tracker = getTracker(connection);
					if (tracker != null) {
						tracker.destroyAll();
					}
					return false;
				});
				registerHandler(PacketType.Play.Server.SPAWN_ENTITY_LIVING, (connection, packet) -> {
					if (packet.getIntegers().read(1) != PacketUtils.ARMORSTAND_TYPE_ID) {
						return false;
					}
					int entityId = packet.getIntegers().read(0);
					initArmorStand(connection, entityId, packet.getDoubles());
					return true;
				});
				registerHandler(PacketType.Play.Server.SPAWN_ENTITY, (connection, packet) -> {
					if (packet.getEntityTypeModifier().read(0) != EntityType.ARMOR_STAND) {
						return false;
					}
					initArmorStand(connection, packet.getIntegers().read(0), packet.getDoubles());
					return true;
				});
				registerHandler(PacketType.Play.Server.ENTITY_DESTROY, (connection, packet) -> {
					getTracker(connection).destroy(packet.getIntegers().read(0));
					return false;
				});
				registerHandler(PacketType.Play.Server.ENTITY_METADATA, (connection, packet) -> {
					ArmorStandData data = getArmorStand(connection, packet.getIntegers().read(0));
					if (data == null) {
						return false;
					}
					data.addMeta(packet.getWatchableCollectionModifier().read(0));
					return true;
				});
				registerHandler(PacketType.Play.Server.ENTITY_TELEPORT, (connection, packet) -> {
					ArmorStandData data = getArmorStand(connection, packet.getIntegers().read(0));
					if (data == null) {
						return false;
					}
					StructureModifier<Double> doubles = packet.getDoubles();
					data.setLocation(new Vector(doubles.read(0), doubles.read(1), doubles.read(2)));
					return true;
				});
			}
			@Override
			public void onPacketSending(PacketEvent event) {
				if (
					(connection.getVersion().getProtocolType() != ProtocolType.PC) ||
					connection.getVersion().isAfter(ProtocolVersion.MINECRAFT_1_7_10)
				) {
					return;
				}
				super.onPacketSending(event);
			}
		});
	}

	protected ArmorStandTracker getTracker(Connection connection) {
		return connection.getMetadata(metadata_key);
	}

	protected ArmorStandData getArmorStand(Connection connection, int entityId) {
		return getTracker(connection).get(entityId);
	}

	protected void initArmorStand(Connection connection, int entityId, StructureModifier<Double> doubles) {
		ArmorStandData existing = getArmorStand(connection, entityId);
		if (existing != null) {
			existing.destroy();
		}
		double x = doubles.read(0);
		double y = doubles.read(1);
		double z = doubles.read(2);
		getTracker(connection).add(connection, entityId, new Vector(x, y, z));
	}

}
