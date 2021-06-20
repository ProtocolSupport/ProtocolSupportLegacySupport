package protocolsupportlegacysupport.features.hologram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
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
import protocolsupportlegacysupport.utils.PacketUtils;
import protocolsupportlegacysupport.utils.SimpleClientBoundPacketListener;

public class HologramHandler extends AbstractFeature<Void> implements Listener {

	public static final int LISTENER_PRIORITY = Integer.MIN_VALUE + Short.MAX_VALUE;

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
				List<PacketContainer> packets = new ArrayList<>();
				tracker.destroyAll(packets);
				for (PacketContainer packet : packets) {
					PacketUtils.sendPacket(connection, packet);
				}
			}
		}
	}

	private static final String metadata_key = "PSLS_HOLOGRAM";

	@EventHandler(priority = EventPriority.LOWEST)
	public void onConnectionOpen(ConnectionOpenEvent event) {
		initConnection(event.getConnection());
	}

	private void initConnection(Connection connection) {
		connection.addMetadata(metadata_key, new ArmorStandTracker(connection));
		connection.addPacketListener(new SimpleClientBoundPacketListener(connection) {
			{
				registerHandler(PacketType.Play.Server.LOGIN, (connection, packet) -> {
					getTracker(connection).destroyAll(null);
					return null;
				});
				registerHandler(PacketType.Play.Server.RESPAWN, (connection, packet) -> {
					getTracker(connection).destroyAll(null);
					return null;
				});
				registerHandler(PacketType.Play.Server.SPAWN_ENTITY_LIVING, (connection, packet) -> {
					if (packet.getIntegers().read(1) != PacketUtils.ARMORSTAND_TYPE_ID) {
						return null;
					}
					return initArmorStand(getTracker(connection), packet.getIntegers().read(0), packet.getDoubles());
				});
				registerHandler(PacketType.Play.Server.SPAWN_ENTITY, (connection, packet) -> {
					if (packet.getEntityTypeModifier().read(0) != EntityType.ARMOR_STAND) {
						return null;
					}
					return initArmorStand(getTracker(connection), packet.getIntegers().read(0), packet.getDoubles());
				});
				registerHandler(PacketType.Play.Server.ENTITY_DESTROY, (connection, packet) -> {
					int entityId = packet.getIntegers().read(0);
					ArmorStandTracker tracker = getTracker(connection);
					if (!tracker.has(entityId)) {
						return null;
					}
					List<PacketContainer> packets = new ArrayList<>();
					getTracker(connection).destroy(packets, entityId);
					return packets;
				});
				registerHandler(PacketType.Play.Server.ENTITY_METADATA, (connection, packet) -> {
					ArmorStandData data = getArmorStand(connection, packet.getIntegers().read(0));
					if (data == null) {
						return null;
					}
					List<PacketContainer> packets = new ArrayList<>();
					data.addMeta(packets, packet.getWatchableCollectionModifier().read(0));
					return packets;
				});
				registerHandler(PacketType.Play.Server.ENTITY_TELEPORT, (connection, packet) -> {
					ArmorStandData data = getArmorStand(connection, packet.getIntegers().read(0));
					if (data == null) {
						return null;
					}
					StructureModifier<Double> doubles = packet.getDoubles();
					List<PacketContainer> packets = new ArrayList<>();
					data.setLocation(packets, new Vector(doubles.read(0), doubles.read(1), doubles.read(2)));
					return packets;
				});
			}
			@Override
			public void onPacketSending(PacketEvent event) {
				ProtocolVersion verison = connection.getVersion();
				if ((verison.getProtocolType() != ProtocolType.PC) || verison.isAfterOrEq(ProtocolVersion.MINECRAFT_1_8)) {
					return;
				}
				super.onPacketSending(event);
			}
		}, LISTENER_PRIORITY);
	}

	protected ArmorStandTracker getTracker(Connection connection) {
		return connection.getMetadata(metadata_key);
	}

	protected ArmorStandData getArmorStand(Connection connection, int entityId) {
		return getTracker(connection).get(entityId);
	}

	protected List<PacketContainer> initArmorStand(ArmorStandTracker tracker, int entityId, StructureModifier<Double> doubles) {
		List<PacketContainer> packets;
		if (tracker.has(entityId)) {
			packets = new ArrayList<>();
			tracker.destroy(packets, entityId);
		} else {
			packets = Collections.emptyList();
		}
		double x = doubles.read(0);
		double y = doubles.read(1);
		double z = doubles.read(2);
		tracker.add(entityId, new Vector(x, y, z));
		return packets;
	}

}
