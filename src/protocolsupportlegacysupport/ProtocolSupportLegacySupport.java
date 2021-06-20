package protocolsupportlegacysupport;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import protocolsupport.api.ProtocolSupportAPI;
import protocolsupportlegacysupport.features.FeaturesConfiguration;
import protocolsupportlegacysupport.features.bossbar.BossBarHandler;
import protocolsupportlegacysupport.features.brewingstandfuel.BrewingStandFuelHandler;
import protocolsupportlegacysupport.features.enchantingtable.EnchantingTableHandler;
import protocolsupportlegacysupport.features.hologram.HologramHandler;
import protocolsupportlegacysupport.utils.EntityIdGenerator;

public class ProtocolSupportLegacySupport extends JavaPlugin {

	private static ProtocolSupportLegacySupport instance;

	public static ProtocolSupportLegacySupport getInstance() {
		return instance;
	}

	public ProtocolSupportLegacySupport() {
		instance = this;
	}

	private static final BigInteger requiredAPIversion = BigInteger.valueOf(15);

	private final FeaturesConfiguration configuration = new FeaturesConfiguration();

	private final BrewingStandFuelHandler brewingstandHandler = new BrewingStandFuelHandler();

	private final EnchantingTableHandler enchantmenttableHandler = new EnchantingTableHandler();

	private final BossBarHandler bossbarHandler = new BossBarHandler();

	private final HologramHandler hologramHandler = new HologramHandler();

	@Override
	public void onEnable() {
		try {
			if (ProtocolSupportAPI.getAPIVersion().compareTo(requiredAPIversion) < 0) {
				getLogger().severe(MessageFormat.format("Too low ProtocolSupport API version, required at least {0}, got {1}", requiredAPIversion, ProtocolSupportAPI.getAPIVersion()));
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		} catch (Throwable t) {
			getLogger().severe("Unable to detect ProtocolSupport API version");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getLogger().log(Level.INFO, "Using entity id generator " + EntityIdGenerator.INSTANCE.getClass().getSimpleName());
		configuration.reload();
		enableHandlers();
	}

	@Override
	public void onDisable() {
		disableHandlers();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("protocolsupportlegacysupport.admin")) {
			sender.sendMessage(ChatColor.RED + "No permissions");
			return true;
		}
		if (args.length > 0) {
			switch (args[0].toLowerCase()) {
				case "reload": {
					configuration.reload();
					disableHandlers();
					enableHandlers();
					sender.sendMessage(ChatColor.GREEN + "Reload complete");
					return true;
				}
			}
		}
		return false;
	}

	private void enableHandlers() {
		if (configuration.isBrewingStrandEnabled()) {
			brewingstandHandler.enable(null);
		}
		if (configuration.isEnchantmentTableEnabled()) {
			enchantmenttableHandler.enable(null);
		}
		if (configuration.isBossbarEnabled()) {
			bossbarHandler.enable(null);
		}
		if (configuration.isHologramEnabled()) {
			hologramHandler.enable(null);
		}
	}

	private void disableHandlers() {
		brewingstandHandler.disable();
		enchantmenttableHandler.disable();
		bossbarHandler.disable();
		hologramHandler.disable();
	}

}
