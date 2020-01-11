package protocolsupportlegacysupport.features;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import protocolsupportlegacysupport.ProtocolSupportLegacySupport;

public class FeaturesConfiguration {

	protected boolean brewingstandEnabled = true;

	protected boolean enchantmenttableEnabled = true;

	protected boolean bossbarEnabled = true;

	protected boolean hologramEnabled = true;

	public boolean isBrewingStrandEnabled() {
		return brewingstandEnabled;
	}

	public boolean isEnchantmentTableEnabled() {
		return enchantmenttableEnabled;
	}

	public boolean isBossbarEnabled() {
		return bossbarEnabled;
	}

	public boolean isHologramEnabled() {
		return hologramEnabled;
	}


	protected static final String brewingstand_enabled_path = "brewingstand.enabled";

	protected static final String enchantment_enabled_path = "enchantment.enabled";

	protected static final String bossbar_enabled_path = "bossbar.enabled";

	protected static final String hologram_enabled_path = "hologram.enabled";

	public void reload() {
		File file = new File(ProtocolSupportLegacySupport.getInstance().getDataFolder(), "config.yml");

		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

		brewingstandEnabled = configuration.getBoolean(brewingstand_enabled_path, brewingstandEnabled);
		enchantmenttableEnabled = configuration.getBoolean(enchantment_enabled_path, enchantmenttableEnabled);
		bossbarEnabled = configuration.getBoolean(bossbar_enabled_path, bossbarEnabled);
		hologramEnabled = configuration.getBoolean(hologram_enabled_path, hologramEnabled);

		configuration = new YamlConfiguration();

		configuration.set(brewingstand_enabled_path, brewingstandEnabled);
		configuration.set(enchantment_enabled_path, enchantmenttableEnabled);
		configuration.set(bossbar_enabled_path, bossbarEnabled);
		configuration.set(hologram_enabled_path, hologramEnabled);

		try {
			configuration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
