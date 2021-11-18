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

public class HorseLegacyHologram implements LegacyHologram {

	private static final Integer AGE_HACK_INDEX = 30;
	private static final Integer AGE_HACK_VALUE = Integer.valueOf(-1700000);

	private final int horseId;
	private final int witherSkullId = EntityIdGenerator.INSTANCE.nextId();

	public HorseLegacyHologram(int entityId) {
		this.horseId = entityId;
	}

	@Override
	public void spawn(Collection<PacketContainer> packets, Vector location, Optional<WrappedChatComponent> name) {
		packets.add(PacketUtils.createEntityObjectSpawnPacket(witherSkullId, EntityType.WITHER_SKULL));
		packets.add(PacketUtils.createEntityLivingSpawnPacket(horseId, PacketUtils.HORSE_TYPE_ID));
		packets.add(PacketUtils.createEntityMetadataPacket(horseId, Collections.singletonList(
			PacketUtils.createDataWatcherObject(AGE_HACK_INDEX, PacketUtils.DW_INTEGER_SERIALIZER, AGE_HACK_VALUE)
		)));
		updateName(packets, name);
		updateLocation(packets, location);
	}

	@Override
	public void updateLocation(Collection<PacketContainer> packets, Vector location) {
		Vector flocation = location.clone().add(new Vector(0, 55, 0));
		packets.add(PacketUtils.createEntitySetPassengersPacket(witherSkullId));
		packets.add(PacketUtils.createEntityTeleportPacket(witherSkullId, flocation));
		packets.add(PacketUtils.createEntityTeleportPacket(horseId, flocation));
		packets.add(PacketUtils.createEntitySetPassengersPacket(witherSkullId, horseId));
	}

	@Override
	public void updateName(Collection<PacketContainer> packets, Optional<WrappedChatComponent> name) {
		packets.add(PacketUtils.createEntityMetadataPacket(horseId, Arrays.asList(
			PacketUtils.createDataWatcherObject(PacketUtils.DW_BASE_NAME_INDEX, PacketUtils.DW_OPTIONAL_CHAT_SERIALIZER, name.map(WrappedChatComponent::getHandle)),
			PacketUtils.createDataWatcherObject(PacketUtils.DW_BASE_NAME_VISIBLE_INDEX, PacketUtils.DW_BOOLEAN_SERIALIZER, Boolean.TRUE)
		)));
	}

	@Override
	public void despawn(Collection<PacketContainer> packets) {
		packets.add(PacketUtils.createEntityDestroyPacket(Arrays.asList(horseId, witherSkullId)));
	}

}
