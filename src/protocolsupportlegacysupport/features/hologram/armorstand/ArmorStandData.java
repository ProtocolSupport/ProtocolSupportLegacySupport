package protocolsupportlegacysupport.features.hologram.armorstand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import org.bukkit.util.Vector;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import protocolsupport.api.Connection;
import protocolsupportlegacysupport.features.hologram.legacyhologram.LegacyHologram;
import protocolsupportlegacysupport.utils.PacketUtils;

public class ArmorStandData {

	private final Connection connection;
	private final int entityId;

	private Vector location;
	private HashMap<Integer, Object> meta = new HashMap<>();

	private LegacyHologram hologram;

	public ArmorStandData(Connection connection, int entityId, Vector location) {
		this.connection = connection;
		this.entityId = entityId;
		this.location = location.clone();
	}

	public void setLocation(Collection<PacketContainer> packets, Vector vector) {
		this.location = vector.clone();
		if (hologram != null) {
			hologram.updateLocation(packets, location.clone());
		}
	}

	public void addMeta(Collection<PacketContainer> packets, Collection<WrappedWatchableObject> objects) {
		for (WrappedWatchableObject obj : objects) {
			meta.put(obj.getIndex(), obj.getRawValue());
		}
		if (hologram == null) {
			if (isHologram()) {
				hologram = LegacyHologram.create(connection.getVersion(), entityId);
				hologram.spawn(packets, location.clone(), getName());
			}
		} else {
			hologram.updateName(packets, getName());
		}
	}

	public void destroy(Collection<PacketContainer> packets) {
		if (hologram != null) {
			hologram.despawn(packets);
		}
	}



	private static final boolean isOffsetSet(int value, int offset) {
		return (value & offset) == offset;
	}

	@SuppressWarnings("unchecked")
	private Optional<WrappedChatComponent> getName() {
		return ((Optional<Object>) meta.get(PacketUtils.DW_BASE_NAME_INDEX)).map(WrappedChatComponent::fromHandle);
	}

	private boolean isHologram() {
		Object basicData = meta.get(PacketUtils.DW_BASE_FLAGS_INDEX);
		if (basicData == null) {
			return false;
		}
		int basicDataI = ((Number) basicData).intValue();
		if (!isOffsetSet(basicDataI, PacketUtils.DW_BASE_FLAGS_INVISIBLE_OFFSET)) {
			return false;
		}
		Object armorStandData = meta.get(PacketUtils.DW_ARMORSTANDDATA_INDEX);
		if (armorStandData == null) {
			return false;
		}
		return isOffsetSet(((Number) armorStandData).intValue(), PacketUtils.DW_ARMORSTANDDATA_MARKER_OFFSET);
	}

}