package protocolsupportlegacysupport.features.hologram.legacyhologram;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

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
	public void spawn(Collection<PacketContainer> packets, Vector location, Optional<WrappedChatComponent> name) {
		packets.add(PacketUtils.createEntityObjectSpawnPacket(witherSkullId, EntityType.WITHER_SKULL));
		packets.add(PacketUtils.createEntityLivingSpawnPacket(slimeId, PacketUtils.SLIME_TYPE_ID));
		packets.add(PacketUtils.createEntityMetadataPacket(slimeId, Collections.singletonList(
			PacketUtils.createDataWatcherObject(PacketUtils.DW_SLINE_SIZE_INDEX, PacketUtils.DW_INTEGER_SERIALIZER, SLIME_SIZE)
		)));
		updateName(packets, name);
		updateLocation(packets, location);
	}

	@Override
	public void updateLocation(Collection<PacketContainer> packets, Vector location) {
		packets.add(PacketUtils.createEntitySetPassengersPacket(witherSkullId));
		packets.add(PacketUtils.createEntityTeleportPacket(witherSkullId, location));
		packets.add(PacketUtils.createEntityTeleportPacket(slimeId, location));
		packets.add(PacketUtils.createEntitySetPassengersPacket(witherSkullId, slimeId));
	}

	@Override
	public void updateName(Collection<PacketContainer> packets, Optional<WrappedChatComponent> name) {
		packets.add(PacketUtils.createEntityMetadataPacket(slimeId, Arrays.asList(
			PacketUtils.createDataWatcherObject(PacketUtils.DW_BASE_NAME_INDEX, PacketUtils.DW_OPTIONAL_CHAT_SERIALIZER, name.map(WrappedChatComponent::getHandle)),
			PacketUtils.createDataWatcherObject(PacketUtils.DW_BASE_NAME_VISIBLE_INDEX, PacketUtils.DW_BOOLEAN_SERIALIZER, Boolean.TRUE)
		)));
	}

	@Override
	public void despawn(Collection<PacketContainer> packets) {
		packets.add(PacketUtils.createEntityDestroyPacket(Arrays.asList(slimeId, witherSkullId)));
	}

}
