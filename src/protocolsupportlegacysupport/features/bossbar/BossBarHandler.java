package protocolsupportlegacysupport.features.bossbar;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;

import protocolsupport.api.Connection;
import protocolsupport.api.Connection.PacketListener;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolType;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.api.events.ConnectionOpenEvent;
import protocolsupportlegacysupport.ProtocolSupportLegacySupport;
import protocolsupportlegacysupport.features.AbstractFeature;
import protocolsupportlegacysupport.features.bossbar.legacybossbar.LegacyBossBar;
import protocolsupportlegacysupport.utils.ObjectStucture;

public class BossBarHandler extends AbstractFeature<Void> implements Listener {

	public static final int LISTENER_PRIORITY = Integer.MIN_VALUE + Short.MAX_VALUE;

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
		connection.addPacketListener(new BossBarPacketListener(connection), LISTENER_PRIORITY);
	}

	protected static class BossBarPacketListener extends PacketListener {

		protected final Class<?> bossbarActionClass = MinecraftReflection.getMinecraftClass("network.protocol.game.PacketPlayOutBoss$Action");
		protected final Map<Class<?>, Map.Entry<BossBarActionType, StructureModifier<Object>>> bossbarActionModifiers = new HashMap<>();

		protected void registerActionModifier(BossBarActionType type, String classSuffix) {
			Class<?> clazz = MinecraftReflection.getMinecraftClass("network.protocol.game.PacketPlayOutBoss$" + classSuffix);
			bossbarActionModifiers.put(clazz, Map.entry(type, new StructureModifier<>(clazz, Object.class, false, true)));
		}

		protected final Connection connection;

		public BossBarPacketListener(Connection connection) {
			this.connection = connection;
			this.registerActionModifier(BossBarActionType.ADD, "a");
			this.registerActionModifier(BossBarActionType.REMOVE, "1");
			this.registerActionModifier(BossBarActionType.UPDATE_PERCENT, "f");
			this.registerActionModifier(BossBarActionType.UPDATE_TEXT, "e");
		}

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
			Object bossbarAction = packet.getSpecificModifier(bossbarActionClass).read(0);
			Map.Entry<BossBarActionType, StructureModifier<Object>> bossbarActionTypeEntry = bossbarActionModifiers.get(bossbarAction.getClass());
			switch (bossbarActionTypeEntry.getKey()) {
				case ADD: {
					LegacyBossBar bossbar = LegacyBossBar.create(connection.getVersion());
					ObjectStucture bossbarActionAddStructure = new ObjectStucture(bossbarAction, bossbarActionTypeEntry.getValue());
					bossbar.spawn(
						connection, player,
						bossbarActionAddStructure.getChatComponents().read(0),
						bossbarActionAddStructure.getFloat().read(0) * 100
					);
					connection.addMetadata(metadata_key, bossbar);
					break;
				}
				case REMOVE: {
					LegacyBossBar bossbar = connection.removeMetadata(metadata_key);
					if (bossbar != null) {
						bossbar.despawn(connection);
					}
					break;
				}
				case UPDATE_PERCENT: {
					LegacyBossBar bossbar = connection.getMetadata(metadata_key);
					if (bossbar != null) {
						ObjectStucture bossbarActionUpdatePercentStructure = new ObjectStucture(bossbarAction, bossbarActionTypeEntry.getValue());
						bossbar.updatePercent(connection, player, bossbarActionUpdatePercentStructure.getFloat().read(0) * 100);
					}
					break;
				}
				case UPDATE_TEXT: {
					LegacyBossBar bossbar = connection.getMetadata(metadata_key);
					if (bossbar != null) {
						ObjectStucture bossbarActionUpdateTextStructure = new ObjectStucture(bossbarAction, bossbarActionTypeEntry.getValue());
						bossbar.updateName(connection, player, bossbarActionUpdateTextStructure.getChatComponents().read(0));
					}
					break;
				}
			}
		}

		protected enum BossBarActionType {
			ADD, REMOVE, UPDATE_PERCENT, UPDATE_TEXT;
		}

	}

}
