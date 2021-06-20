package protocolsupportlegacysupport.features.hologram.armorstand;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.util.Vector;

import com.comphenix.protocol.events.PacketContainer;

import protocolsupport.api.Connection;

public class ArmorStandTracker {

	protected final Connection connection;
	protected final HashMap<Integer, ArmorStandData> collection = new HashMap<>();

	public ArmorStandTracker(Connection connection) {
		this.connection = connection;
	}

	public void destroyAll(Collection<PacketContainer> packets) {
		if (packets != null) {
			collection.values().forEach(adata -> adata.destroy(packets));
		}
		collection.clear();
	}

	public void add(int entityId, Vector location) {
		collection.put(entityId, new ArmorStandData(connection, entityId, location));
	}

	public boolean destroy(Collection<PacketContainer> packets, int entityId) {
		ArmorStandData armorstand = collection.remove(entityId);
		if (armorstand != null) {
			armorstand.destroy(packets);
			return true;
		}
		return false;
	}

	public boolean has(int entityId) {
		return collection.containsKey(entityId);
	}

	public ArmorStandData get(int entityId) {
		return collection.get(entityId);
	}

}
