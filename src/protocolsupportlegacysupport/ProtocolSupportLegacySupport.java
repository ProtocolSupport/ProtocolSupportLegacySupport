package protocolsupportlegacysupport;

import java.math.BigInteger;
import java.text.MessageFormat;

import org.bukkit.plugin.java.JavaPlugin;

import protocolsupport.api.ProtocolSupportAPI;
import protocolsupportlegacysupport.bossbar.BossBarHandler;
import protocolsupportlegacysupport.brewingstandfuel.BrewingStandFuelHandler;
import protocolsupportlegacysupport.enchantingtable.EnchantingTableHandler;
import protocolsupportlegacysupport.hologram.HologramHandler;

public class ProtocolSupportLegacySupport extends JavaPlugin {

	private static ProtocolSupportLegacySupport instance;

	public static ProtocolSupportLegacySupport getInstance() {
		return instance;
	}

	public ProtocolSupportLegacySupport() {
		instance = this;
	}

	private static final BigInteger requiredAPIversion = BigInteger.ONE;

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
		new BrewingStandFuelHandler().start();
		new EnchantingTableHandler().start();
		new HologramHandler().start();
		new BossBarHandler().start();
	}

}
