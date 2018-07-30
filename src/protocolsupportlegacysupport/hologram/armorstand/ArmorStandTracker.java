package protocolsupportlegacysupport.hologram.armorstand;

import java.util.HashMap;

import org.bukkit.util.Vector;

import protocolsupport.api.Connection;

public class ArmorStandTracker {

	private final HashMap<Integer, ArmorStandData> armorStandMeta = new HashMap<>();

	public void addArmorStand(Connection connection, int entityId, Vector location) {
		armorStandMeta.put(entityId, new ArmorStandData(connection, location));
	}

	public void removeArmorStand(int entityId) {
		armorStandMeta.remove(entityId);
	}

	public ArmorStandData getArmorStand(int entityId) {
		return armorStandMeta.get(entityId);
	}

}
