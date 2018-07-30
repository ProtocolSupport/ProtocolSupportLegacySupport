package protocolsupportlegacysupport.hologram.legacyhologram;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.util.Vector;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import protocolsupport.api.Connection;
import protocolsupportlegacysupport.utils.Constants;
import protocolsupportlegacysupport.utils.IdGenerator;
import protocolsupportlegacysupport.utils.PacketUtils;

//Not invisible, but entities on wither skulls stay still, so text is somewhat readable when not multiline
public class SlimeLegacyHologram implements LegacyHologram {

	private final int slimeId = IdGenerator.generateId();
	private final int witherSkullId = IdGenerator.generateId();

	@Override
	public void spawn(Connection connection, Vector location, Optional<WrappedChatComponent> name) {
		PacketContainer spawnSkull = PacketUtils.createEntityObjectSpawnPacket(witherSkullId, Constants.WITHER_SKULL_TYPE_ID);
		WrappedDataWatcher dwSlime = new WrappedDataWatcher();
		dwSlime.setObject(12, Constants.DW_INTEGER_SERIALIZER, 1);
		PacketContainer spawnSlime = PacketUtils.createEntityLivingSpawnPacket(slimeId, Constants.SLIME_TYPE_ID, dwSlime);
		PacketUtils.sendPacket(connection, spawnSkull);
		PacketUtils.sendPacket(connection, spawnSlime);
		updateName(connection, name);
		updateLocation(connection, location);
	}

	@Override
	public void updateLocation(Connection connection, Vector location) {
		PacketContainer detach = PacketUtils.createEntitySetPassengersPacket(witherSkullId);
		PacketContainer teleportSkull = PacketUtils.createEntityTeleportPacket(witherSkullId, location);
		PacketContainer teleportSlime = PacketUtils.createEntityTeleportPacket(slimeId, location);
		PacketContainer attach = PacketUtils.createEntitySetPassengersPacket(witherSkullId, slimeId);
		PacketUtils.sendPacket(connection, detach);
		PacketUtils.sendPacket(connection, teleportSkull);
		PacketUtils.sendPacket(connection, teleportSlime);
		PacketUtils.sendPacket(connection, attach);
	}

	@Override
	public void updateName(Connection connection, Optional<WrappedChatComponent> name) {
		ArrayList<WrappedWatchableObject> objects = new ArrayList<>();
		objects.add(new WrappedWatchableObject(new WrappedDataWatcherObject(Constants.DW_NAME_INDEX, Constants.DW_OPTIONAL_CHAT_SERIALIZER), name));
		objects.add(new WrappedWatchableObject(new WrappedDataWatcherObject(Constants.DW_NAME_VISIBLE_INDEX, Constants.DW_BOOLEAN_SERIALIZER), true));
		PacketUtils.sendPacket(connection, PacketUtils.createEntityMetadataPacket(slimeId, objects));
	}

	@Override
	public void despawn(Connection connection) {
		PacketUtils.sendPacket(connection, PacketUtils.createEntityDestroyPacket(slimeId, witherSkullId));
	}

}
