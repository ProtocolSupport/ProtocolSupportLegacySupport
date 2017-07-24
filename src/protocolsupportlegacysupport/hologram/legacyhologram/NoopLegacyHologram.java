package protocolsupportlegacysupport.hologram.legacyhologram;

import org.bukkit.util.Vector;

import protocolsupport.api.Connection;

//Some clients do not support custom entity display names at all, so don't even spawn hologram for them
public class NoopLegacyHologram implements LegacyHologram {

	@Override
	public void spawn(Connection player, Vector location, String name) {
	}

	@Override
	public void updateLocation(Connection player, Vector location) {
	}

	@Override
	public void updateName(Connection player, String name) {
	}

	@Override
	public void despawn(Connection player) {
	}

}
