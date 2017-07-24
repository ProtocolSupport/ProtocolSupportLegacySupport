package protocolsupportlegacysupport.hologram.legacyhologram;

import java.util.ArrayList;

import org.bukkit.util.Vector;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import protocolsupport.api.Connection;
import protocolsupportlegacysupport.utils.Constants;
import protocolsupportlegacysupport.utils.IdGenerator;
import protocolsupportlegacysupport.utils.PacketUtils;

public class HorseLegacyHologram implements LegacyHologram {

	private final int horseId = IdGenerator.generateId();
	private final int witherSkullId = IdGenerator.generateId();

	@Override
	public void spawn(Connection connection, Vector location, String name) {
		PacketContainer spawnSkull = PacketUtils.createEntityObjectSpawnPacket(witherSkullId, Constants.WITHER_SKULL_TYPE_ID);
		WrappedDataWatcher dwHorse = new WrappedDataWatcher();
		dwHorse.setObject(30, Constants.DW_INTEGER_SERIALIZER, -1700000);
		PacketContainer spawnHorse = PacketUtils.createEntityLivingSpawnPacket(horseId, Constants.HORSE_TYPE_ID, dwHorse);
		PacketUtils.sendPacket(connection, spawnSkull);
		PacketUtils.sendPacket(connection, spawnHorse);
		updateName(connection, name);
		updateLocation(connection, location);
	}

	@Override
	public void updateLocation(Connection connection, Vector location) {
		PacketContainer detach = PacketUtils.createEntitySetPassengersPacket(witherSkullId);
		Vector flocation = location.clone().add(new Vector(0, 55, 0));
		PacketContainer teleportSkull = PacketUtils.createEntityTeleportPacket(witherSkullId, flocation);
		PacketContainer teleportHorse = PacketUtils.createEntityTeleportPacket(horseId, flocation);
		PacketContainer attach = PacketUtils.createEntitySetPassengersPacket(witherSkullId, horseId);
		PacketUtils.sendPacket(connection, detach);
		PacketUtils.sendPacket(connection, teleportSkull);
		PacketUtils.sendPacket(connection, teleportHorse);
		PacketUtils.sendPacket(connection, attach);
	}

	@Override
	public void updateName(Connection connection, String name) {
		ArrayList<WrappedWatchableObject> objects = new ArrayList<>();
		objects.add(new WrappedWatchableObject(new WrappedDataWatcherObject(Constants.DW_NAME_INDEX, Constants.DW_STRING_SERIALIZER), name));
		objects.add(new WrappedWatchableObject(new WrappedDataWatcherObject(Constants.DW_NAME_VISIBLE_INDEX, Constants.DW_BOOLEAN_SERIALIZER), true));
		PacketUtils.sendPacket(connection, PacketUtils.createEntityMetadataPacket(horseId, objects));
	}

	@Override
	public void despawn(Connection connection) {
		PacketUtils.sendPacket(connection, PacketUtils.createEntityDestroyPacket(horseId, witherSkullId));
	}

}
