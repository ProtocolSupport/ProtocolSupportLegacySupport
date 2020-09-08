package protocolsupportlegacysupport.features.hologram.armorstand;

import java.util.HashMap;

import org.bukkit.util.Vector;

import protocolsupport.api.Connection;

public class ArmorStandTracker {

	private final HashMap<Integer, ArmorStandData> collection = new HashMap<>();

	public void destroyAll() {
		collection.values().forEach(ArmorStandData::destroy);
		collection.clear();
	}

	public void add(Connection connection, int entityId, Vector location) {
		collection.put(entityId, new ArmorStandData(connection, entityId, location));
	}

	public void destroy(int entityId) {
		ArmorStandData armorstand = collection.remove(entityId);
		if (armorstand != null) {
			armorstand.destroy();
		}
	}

	public ArmorStandData get(int entityId) {
		return collection.get(entityId);
	}

}
