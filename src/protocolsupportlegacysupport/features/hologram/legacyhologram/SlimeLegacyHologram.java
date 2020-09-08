package protocolsupportlegacysupport.features.hologram.legacyhologram;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

import protocolsupport.api.Connection;
import protocolsupportlegacysupport.utils.EntityIdGenerator;
import protocolsupportlegacysupport.utils.PacketUtils;

//Not invisible, but entities on wither skulls stay still, so text is somewhat readable when not multiline
public class SlimeLegacyHologram implements LegacyHologram {

	private static final Integer SLIME_SIZE = Integer.valueOf(1);

	private final int slimeId;
	private final int witherSkullId = EntityIdGenerator.INSTANCE.nextId();

	public SlimeLegacyHologram(int entityId) {
		this.slimeId = entityId;
	}

	@Override
	public void spawn(Connection connection, Vector location, Optional<WrappedChatComponent> name) {
		PacketUtils.sendPacket(connection, PacketUtils.createEntityObjectSpawnPacket(witherSkullId, EntityType.WITHER_SKULL));
		PacketUtils.sendPacket(connection, PacketUtils.createEntityLivingSpawnPacket(slimeId, PacketUtils.SLIME_TYPE_ID));
		PacketUtils.sendPacket(connection, PacketUtils.createEntityMetadataPacket(slimeId, Collections.singletonList(
			PacketUtils.createDataWatcherObject(PacketUtils.DW_SLINE_SIZE_INDEX, PacketUtils.DW_INTEGER_SERIALIZER, SLIME_SIZE)
		)));
		updateName(connection, name);
		updateLocation(connection, location);
	}

	@Override
	public void updateLocation(Connection connection, Vector location) {
		PacketUtils.sendPacket(connection, PacketUtils.createEntitySetPassengersPacket(witherSkullId));
		PacketUtils.sendPacket(connection, PacketUtils.createEntityTeleportPacket(witherSkullId, location));
		PacketUtils.sendPacket(connection, PacketUtils.createEntityTeleportPacket(slimeId, location));
		PacketUtils.sendPacket(connection, PacketUtils.createEntitySetPassengersPacket(witherSkullId, slimeId));
	}

	@Override
	public void updateName(Connection connection, Optional<WrappedChatComponent> name) {
		PacketUtils.sendPacket(connection, PacketUtils.createEntityMetadataPacket(slimeId, Arrays.asList(
			PacketUtils.createDataWatcherObject(PacketUtils.DW_BASE_NAME_INDEX, PacketUtils.DW_OPTIONAL_CHAT_SERIALIZER, name.map(WrappedChatComponent::getHandle)),
			PacketUtils.createDataWatcherObject(PacketUtils.DW_BASE_NAME_VISIBLE_INDEX, PacketUtils.DW_BOOLEAN_SERIALIZER, Boolean.TRUE)
		)));
	}

	@Override
	public void despawn(Connection connection) {
		PacketUtils.sendPacket(connection, PacketUtils.createEntityDestroyPacket(slimeId, witherSkullId));
	}

}
