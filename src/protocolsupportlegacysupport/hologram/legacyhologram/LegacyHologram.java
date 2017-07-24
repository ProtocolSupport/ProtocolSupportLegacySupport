package protocolsupportlegacysupport.hologram.legacyhologram;

import org.bukkit.util.Vector;

import protocolsupport.api.Connection;
import protocolsupport.api.ProtocolVersion;

public interface LegacyHologram {

	public static LegacyHologram create(ProtocolVersion version) {
		if (version.isBetween(ProtocolVersion.MINECRAFT_1_6_1, ProtocolVersion.MINECRAFT_1_7_10)) {
			return new HorseLegacyHologram();
		} else if (version.isBetween(ProtocolVersion.MINECRAFT_1_5_1, ProtocolVersion.MINECRAFT_1_5_2)) {
			return new SlimeLegacyHologram();
		} else {
			return new NoopLegacyHologram();
		}
	}

	public void spawn(Connection connection, Vector location, String name);

	public void updateLocation(Connection connection, Vector location);

	public void updateName(Connection connection, String name);

	public void despawn(Connection connection);

}
