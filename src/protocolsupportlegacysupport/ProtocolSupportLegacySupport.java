package protocolsupportlegacysupport;

import org.bukkit.plugin.java.JavaPlugin;

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

	@Override
	public void onEnable() {
		new BrewingStandFuelHandler().start();
		new EnchantingTableHandler().start();
		new HologramHandler().start();
		new BossBarHandler().start();
	}

}
