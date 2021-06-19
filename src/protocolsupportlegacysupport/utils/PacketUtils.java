package protocolsupportlegacysupport.utils;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import protocolsupport.api.Connection;
import protocolsupport.api.MaterialAPI;

public class PacketUtils {

	public static WrappedWatchableObject createDataWatcherObject(int index, Serializer serializer, Object value) {
		return new WrappedWatchableObject(new WrappedDataWatcherObject(index, serializer), value);
	}

	private static final ProtocolManager manager = ProtocolLibrary.getProtocolManager();

	public static void sendPacket(Connection connection, PacketContainer packet) {
		connection.sendPacket(packet.getHandle());
	}

	public static PacketContainer createPacket(PacketType type) {
		return manager.createPacket(type);
	}

	public static PacketContainer createEntityTeleportPacket(int entityId, Vector location) {
		PacketContainer teleport = createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
		teleport.getIntegers().write(0, entityId);
		teleport.getDoubles().write(0, location.getX());
		teleport.getDoubles().write(1, location.getY());
		teleport.getDoubles().write(2, location.getZ());
		return teleport;
	}

	public static PacketContainer createEntityMetadataPacket(int entityId, List<WrappedWatchableObject> objects) {
		PacketContainer metadata = createPacket(PacketType.Play.Server.ENTITY_METADATA);
		metadata.getIntegers().write(0, entityId);
		metadata.getWatchableCollectionModifier().write(0, objects);
		return metadata;
	}

	public static PacketContainer createEntityDestroyPacket(int entityId) {
		PacketContainer destroy = createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		destroy.getIntegers().write(0, entityId);
		return destroy;
	}

	public static PacketContainer createEntityLivingSpawnPacket(int entityId, int type) {
		PacketContainer spawn = createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		spawn.getIntegers().write(0, entityId);
		spawn.getIntegers().write(1, type);
		spawn.getSpecificModifier(UUID.class).write(0, UUID.randomUUID());
		return spawn;
	}

	public static PacketContainer createEntityObjectSpawnPacket(int entityId, EntityType type) {
		PacketContainer spawn = createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		spawn.getIntegers().write(0, entityId);
		spawn.getEntityTypeModifier().write(0, type);
		spawn.getSpecificModifier(UUID.class).write(0, UUID.randomUUID());
		return spawn;
	}

	public static PacketContainer createEntitySetPassengersPacket(int vehicleId, int... passengers) {
		PacketContainer setpassengers = createPacket(PacketType.Play.Server.MOUNT);
		setpassengers.getIntegers().write(0, vehicleId);
		setpassengers.getIntegerArrays().write(0, passengers);
		return setpassengers;
	}

	public static final int SLIME_TYPE_ID = MaterialAPI.getEntityLivingTypeNetworkId(EntityType.SLIME);
	public static final int HORSE_TYPE_ID = MaterialAPI.getEntityLivingTypeNetworkId(EntityType.HORSE);
	public static final int WITHER_TYPE_ID = MaterialAPI.getEntityLivingTypeNetworkId(EntityType.WITHER);
	public static final int ARMORSTAND_TYPE_ID = MaterialAPI.getEntityLivingTypeNetworkId(EntityType.ARMOR_STAND);

	public static final Serializer DW_FLOAT_SERIALIZER = Registry.get(Float.class, false);
	public static final Serializer DW_BYTE_SERIALIZER = Registry.get(Byte.class, false);
	public static final Serializer DW_OPTIONAL_CHAT_SERIALIZER = Registry.getChatComponentSerializer(true);
	public static final Serializer DW_BOOLEAN_SERIALIZER = Registry.get(Boolean.class, false);
	public static final Serializer DW_INTEGER_SERIALIZER = Registry.get(Integer.class, false);
	public static final int DW_BASE_FLAGS_INDEX = 0;
	public static final int DW_BASE_FLAGS_INVISIBLE_OFFSET = 0x20;
	public static final int DW_BASE_NAME_INDEX = 2;
	public static final int DW_BASE_NAME_VISIBLE_INDEX = 3;
	public static final int DW_LIVING_HEALTH_INDEX = 9;
	public static final int DW_ARMORSTANDDATA_INDEX = 15;
	public static final int DW_ARMORSTANDDATA_MARKER_OFFSET = 0x10;
	public static final int DW_WITHER_INVULNERABLE_TIME_INDEX = 19;
	public static final int DW_SLINE_SIZE_INDEX = 16;

}