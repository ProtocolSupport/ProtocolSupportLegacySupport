package protocolsupportlegacysupport.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import protocolsupport.api.Connection;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolType;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.api.chat.ChatAPI;
import protocolsupport.api.events.ConnectionOpenEvent;
import protocolsupportlegacysupport.ProtocolSupportLegacySupport;
import protocolsupportlegacysupport.bossbar.legacybossbar.LegacyBossBar;

public class BossBarHandler implements Listener {

	private static final String metadata_key = "PSLS_BOSSBAR";

	public void start() {
		Bukkit.getPluginManager().registerEvents(this, ProtocolSupportLegacySupport.getInstance());
		ProtocolSupportAPI.getConnections().forEach(this::initConnection);
		Bukkit.getScheduler().runTaskTimer(ProtocolSupportLegacySupport.getInstance(), () -> {
			ProtocolSupportAPI.getConnections().forEach(connection -> {
				Player player = connection.getPlayer();
				if (player == null) {
					return;
				}
				LegacyBossBar bossbar = getBossBar(connection);
				if (bossbar == null) {
					return;
				}
				bossbar.handlePlayerTick(connection, player);
			});
		}, 0, 1);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onConnectionOpen(ConnectionOpenEvent event) {
		initConnection(event.getConnection());
	}

	private void initConnection(Connection connection) {
		connection.addPacketSendListener(packetObj -> {
			if (connection.getVersion().getProtocolType() != ProtocolType.PC) {
				return true;
			}
			if (connection.getVersion().isAfter(ProtocolVersion.MINECRAFT_1_8)) {
				return true;
			}
			Player player = connection.getPlayer();
			if (player == null) {
				return true;
			}
			PacketContainer packet = PacketContainer.fromPacket(packetObj);
			if (packet.getType() != PacketType.Play.Server.BOSS) {
				return true;
			}
			int action = packet.getSpecificModifier(Enum.class).read(0).ordinal();
			switch (action) {
				case 0: {
					LegacyBossBar bossbar = LegacyBossBar.create(connection.getVersion());
					bossbar.spawn(
						connection, player,
						ChatAPI.fromJSON(packet.getChatComponents().read(0).getJson()).toLegacyText(),
						packet.getFloat().read(0) * 100
					);
					connection.addMetadata(metadata_key, bossbar);
					break;
				}
				case 1: {
					LegacyBossBar bossbar = getBossBar(connection);
					if (bossbar != null) {
						bossbar.despawn(connection);
						connection.removeMetadata(metadata_key);
					}
					break;
				}
				case 2: {
					LegacyBossBar bossbar = getBossBar(connection);
					if (bossbar != null) {
						bossbar.updatePercent(connection, player, packet.getFloat().read(0) * 100);
					}
					break;
				}
				case 3: {
					LegacyBossBar bossbar = getBossBar(connection);
					if (bossbar != null) {
						bossbar.updateName(connection, player, ChatAPI.fromJSON(packet.getChatComponents().read(0).getJson()).toLegacyText());
					}
					break;
				}
			}
			return false;
		});
	}

	private LegacyBossBar getBossBar(Connection connection) {
		return ((LegacyBossBar) connection.getMetadata(metadata_key));
	}

}
