package protocolsupportlegacysupport.features.hologram.legacyhologram;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

import protocolsupport.api.Connection;
import protocolsupportlegacysupport.utils.IdGenerator;
import protocolsupportlegacysupport.utils.PacketUtils;

public class HorseLegacyHologram implements LegacyHologram {

	private static final Integer AGE_HACK_INDEX = 30;
	private static final Integer AGE_HACK_VALUE = Integer.valueOf(-1700000);

	private final int horseId = IdGenerator.generateId();
	private final int witherSkullId = IdGenerator.generateId();

	@Override
	public void spawn(Connection connection, Vector location, Optional<WrappedChatComponent> name) {
		PacketUtils.sendPacket(connection, PacketUtils.createEntityObjectSpawnPacket(witherSkullId, EntityType.WITHER_SKULL));
		PacketUtils.sendPacket(connection, PacketUtils.createEntityLivingSpawnPacket(horseId, PacketUtils.HORSE_TYPE_ID));
		PacketUtils.sendPacket(connection, PacketUtils.createEntityMetadataPacket(horseId, Collections.singletonList(
			PacketUtils.createDataWatcherObject(AGE_HACK_INDEX, PacketUtils.DW_INTEGER_SERIALIZER, AGE_HACK_VALUE)
		)));
		updateName(connection, name);
		updateLocation(connection, location);
	}

	@Override
	public void updateLocation(Connection connection, Vector location) {
		Vector flocation = location.clone().add(new Vector(0, 55, 0));
		PacketUtils.sendPacket(connection, PacketUtils.createEntitySetPassengersPacket(witherSkullId));
		PacketUtils.sendPacket(connection, PacketUtils.createEntityTeleportPacket(witherSkullId, flocation));
		PacketUtils.sendPacket(connection, PacketUtils.createEntityTeleportPacket(horseId, flocation));
		PacketUtils.sendPacket(connection, PacketUtils.createEntitySetPassengersPacket(witherSkullId, horseId));
	}

	@Override
	public void updateName(Connection connection, Optional<WrappedChatComponent> name) {
		PacketUtils.sendPacket(connection, PacketUtils.createEntityMetadataPacket(horseId, Arrays.asList(
			PacketUtils.createDataWatcherObject(PacketUtils.DW_BASE_NAME_INDEX, PacketUtils.DW_OPTIONAL_CHAT_SERIALIZER, name.map(WrappedChatComponent::getHandle)),
			PacketUtils.createDataWatcherObject(PacketUtils.DW_BASE_NAME_VISIBLE_INDEX, PacketUtils.DW_BOOLEAN_SERIALIZER, Boolean.TRUE)
		)));
	}

	@Override
	public void despawn(Connection connection) {
		PacketUtils.sendPacket(connection, PacketUtils.createEntityDestroyPacket(horseId, witherSkullId));
	}

}
