package protocolsupportlegacysupport.bossbar.legacybossbar;

import org.bukkit.entity.Player;

import protocolsupport.api.Connection;
import protocolsupport.api.ProtocolVersion;

public interface LegacyBossBar {

	public static LegacyBossBar create(ProtocolVersion version) {
		return new WitherLegacyBossBar();
	}

	public void spawn(Connection connection, Player player, String name, float percent);

	public void handlePlayerTick(Connection connection, Player player);

	public void updateName(Connection connection, Player player, String name);

	public void updatePercent(Connection connection, Player player, float percent);

	public void despawn(Connection connection);

}
