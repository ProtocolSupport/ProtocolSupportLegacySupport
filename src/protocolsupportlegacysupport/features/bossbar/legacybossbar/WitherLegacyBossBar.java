package protocolsupportlegacysupport.features.bossbar.legacybossbar;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

import protocolsupport.api.Connection;
import protocolsupportlegacysupport.utils.EntityIdGenerator;
import protocolsupportlegacysupport.utils.PacketUtils;

public class WitherLegacyBossBar implements LegacyBossBar {

	private static final Byte WITHER_BASE_FLAGS_VALUE = Byte.valueOf((byte) 0x20);
	private static final Integer WITHER_INVULNERABLE_TIME = Integer.valueOf(881);

	private final int id = EntityIdGenerator.INSTANCE.nextId();

	private Location lastPlayerLocation;
	private WrappedChatComponent lastName = WrappedChatComponent.fromText("");
	private float lastPercent = 100.0F;

	@Override
	public void spawn(Connection connection, Player player, WrappedChatComponent name, float percent) {
		lastName = name;
		lastPercent = percent;
		lastPlayerLocation = player.getLocation();

		PacketUtils.sendPacket(connection, PacketUtils.createEntityLivingSpawnPacket(id, PacketUtils.WITHER_TYPE_ID));
		PacketUtils.sendPacket(connection, PacketUtils.createEntityMetadataPacket(id, Arrays.asList(
			PacketUtils.createDataWatcherObject(PacketUtils.DW_BASE_FLAGS_INDEX, PacketUtils.DW_BYTE_SERIALIZER, WITHER_BASE_FLAGS_VALUE),
			PacketUtils.createDataWatcherObject(PacketUtils.DW_BASE_NAME_INDEX, PacketUtils.DW_OPTIONAL_CHAT_SERIALIZER, Optional.of(lastName.getHandle())),
			PacketUtils.createDataWatcherObject(PacketUtils.DW_BASE_NAME_VISIBLE_INDEX, PacketUtils.DW_BOOLEAN_SERIALIZER, Boolean.TRUE),
			PacketUtils.createDataWatcherObject(PacketUtils.DW_LIVING_HEALTH_INDEX, PacketUtils.DW_FLOAT_SERIALIZER, Float.valueOf(lastPercent * 3F)),
			PacketUtils.createDataWatcherObject(PacketUtils.DW_WITHER_INVULNERABLE_TIME_INDEX, PacketUtils.DW_INTEGER_SERIALIZER, WITHER_INVULNERABLE_TIME)
		)));
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
		lastPercent = percent;

		PacketUtils.sendPacket(connection, PacketUtils.createEntityMetadataPacket(id, Collections.singletonList(
			PacketUtils.createDataWatcherObject(PacketUtils.DW_LIVING_HEALTH_INDEX, PacketUtils.DW_FLOAT_SERIALIZER, Float.valueOf(lastPercent * 3F))
		)));
	}

	@Override
	public void despawn(Connection connection) {
		PacketUtils.sendPacket(connection, PacketUtils.createEntityDestroyPacket(id));
		lastPlayerLocation = null;
	}

}
