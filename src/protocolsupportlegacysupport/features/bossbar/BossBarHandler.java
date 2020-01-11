package protocolsupportlegacysupport.features.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import protocolsupport.api.Connection;
import protocolsupport.api.Connection.PacketListener;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolType;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.api.events.ConnectionOpenEvent;
import protocolsupportlegacysupport.ProtocolSupportLegacySupport;
import protocolsupportlegacysupport.features.AbstractFeature;
import protocolsupportlegacysupport.features.bossbar.legacybossbar.LegacyBossBar;

public class BossBarHandler extends AbstractFeature<Void> implements Listener {

	private BukkitTask task;

	@Override
	protected void enable0(Void config) {
		Bukkit.getPluginManager().registerEvents(this, ProtocolSupportLegacySupport.getInstance());
		ProtocolSupportAPI.getConnections().forEach(this::initConnection);
		task = Bukkit.getScheduler().runTaskTimer(ProtocolSupportLegacySupport.getInstance(), () -> {
			ProtocolSupportAPI.getConnections().forEach(connection -> {
				Player player = connection.getPlayer();
				if (player == null) {
					return;
				}
				LegacyBossBar bossbar = connection.getMetadata(metadata_key);
				if (bossbar == null) {
					return;
				}
				bossbar.handlePlayerTick(connection, player);
			});
		}, 0, 1);
	}

	@Override
	protected void disable0() {
		HandlerList.unregisterAll();
		task.cancel();
		for (Connection connection : ProtocolSupportAPI.getConnections()) {
			LegacyBossBar bossbar = connection.removeMetadata(metadata_key);
			if (bossbar != null) {
				bossbar.despawn(connection);
			}
		}
	}

	private static final String metadata_key = "PSLS_BOSSBAR";

	@EventHandler(priority = EventPriority.LOWEST)
	protected void onConnectionOpen(ConnectionOpenEvent event) {
		initConnection(event.getConnection());
	}

	private void initConnection(Connection connection) {
		connection.addPacketListener(new PacketListener() {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (
					(connection.getVersion().getProtocolType() != ProtocolType.PC) ||
					connection.getVersion().isAfter(ProtocolVersion.MINECRAFT_1_8)
				) {
					return;
				}
				Player player = connection.getPlayer();
				if (player == null) {
					return;
				}
				PacketContainer packet = PacketContainer.fromPacket(event.getPacket());
				if (packet.getType() != PacketType.Play.Server.BOSS) {
					return;
				}
				int action = packet.getSpecificModifier(Enum.class).read(0).ordinal();
				switch (action) {
					case 0: {
						LegacyBossBar bossbar = LegacyBossBar.create(connection.getVersion());
						bossbar.spawn(connection, player, packet.getChatComponents().read(0), packet.getFloat().read(0) * 100);
						connection.addMetadata(metadata_key, bossbar);
						break;
					}
					case 1: {
						LegacyBossBar bossbar = connection.removeMetadata(metadata_key);
						if (bossbar != null) {
							bossbar.despawn(connection);
						}
						break;
					}
					case 2: {
						LegacyBossBar bossbar = connection.getMetadata(metadata_key);
						if (bossbar != null) {
							bossbar.updatePercent(connection, player, packet.getFloat().read(0) * 100);
						}
						break;
					}
					case 3: {
						LegacyBossBar bossbar = connection.getMetadata(metadata_key);
						if (bossbar != null) {
							bossbar.updateName(connection, player, packet.getChatComponents().read(0));
						}
						break;
					}
				}
			}
		});
	}

}
