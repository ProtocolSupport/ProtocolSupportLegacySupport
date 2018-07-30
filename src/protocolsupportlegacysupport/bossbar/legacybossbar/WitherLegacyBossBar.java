package protocolsupportlegacysupport.bossbar.legacybossbar;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import protocolsupport.api.Connection;
import protocolsupportlegacysupport.utils.Constants;
import protocolsupportlegacysupport.utils.IdGenerator;
import protocolsupportlegacysupport.utils.PacketUtils;

public class WitherLegacyBossBar implements LegacyBossBar {

	private final int id = IdGenerator.generateId();

	private Location lastPlayerLocation;
	private WrappedChatComponent lastName = WrappedChatComponent.fromText("");
	private float lastPercent = 100.0F;

	@SuppressWarnings("deprecation")
	@Override
	public void spawn(Connection connection, Player player, WrappedChatComponent name, float percent) {
		lastName = name;
		lastPercent = percent;
		lastPlayerLocation = player.getLocation();
		WrappedDataWatcher watcher = new WrappedDataWatcher();
		watcher.setObject(new WrappedDataWatcherObject(0, Constants.DW_BYTE_SERIALIZER), Byte.valueOf((byte) 0x20));
		watcher.setObject(new WrappedDataWatcherObject(2, Constants.DW_OPTIONAL_CHAT_SERIALIZER), Optional.of(lastName));
		watcher.setObject(new WrappedDataWatcherObject(3, Constants.DW_BOOLEAN_SERIALIZER), Boolean.TRUE);
		watcher.setObject(new WrappedDataWatcherObject(6, Constants.DW_FLOAT_SERIALIZER), Float.valueOf(lastPercent * 3F));
		watcher.setObject(new WrappedDataWatcherObject(14, Constants.DW_INTEGER_SERIALIZER), Integer.valueOf(881));
		PacketUtils.sendPacket(connection, PacketUtils.createEntityLivingSpawnPacket(id, EntityType.WITHER.getTypeId(), watcher));
		updateEntityLocation(connection);
	}

	@Override
	public void handlePlayerTick(Connection connection, Player player) {
		if (lastPlayerLocation == null) {
			return;
		}
		Location currectLocation = player.getLocation();
		if (!lastPlayerLocation.getWorld().equals(currectLocation.getWorld())) {
			despawn(connection);
			spawn(connection, player, lastName, lastPercent);
		} else {
			lastPlayerLocation = currectLocation;
			updateEntityLocation(connection);
		}
	}

	private void updateEntityLocation(Connection connection) {
		Vector entityLocation = lastPlayerLocation.clone().add(lastPlayerLocation.getDirection().normalize().multiply(48)).toVector();
		PacketUtils.sendPacket(connection, PacketUtils.createEntityTeleportPacket(id, entityLocation));
	}

	@Override
	public void updateName(Connection connection, Player player, WrappedChatComponent name) {
		despawn(connection);
		spawn(connection, player, name, lastPercent);
	}

	@Override
	public void updatePercent(Connection connection, Player player, float percent) {
		despawn(connection);
		spawn(connection, player, lastName, percent);
	}

	@Override
	public void despawn(Connection connection) {
		PacketUtils.sendPacket(connection, PacketUtils.createEntityDestroyPacket(id));
		lastPlayerLocation = null;
	}

}
