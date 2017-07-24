package protocolsupportlegacysupport.enchantingtable;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

import protocolsupportlegacysupport.ProtocolSupportLegacySupport;

public class EnchantingTableHandler implements Listener {

	public void start() {
		Bukkit.getPluginManager().registerEvents(this, ProtocolSupportLegacySupport.getInstance());
	}

	private static final ItemStack lapis = new Dye(DyeColor.BLUE).toItemStack(1);

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onInvOpen(InventoryClickEvent event) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(ProtocolSupportLegacySupport.getInstance(), () -> {
			Inventory topinv = event.getWhoClicked().getOpenInventory().getTopInventory();
			if (!(topinv instanceof EnchantingInventory)) {
				return;
			}
			EnchantingInventory enchinv = (EnchantingInventory) topinv;
			Player player = (Player) event.getWhoClicked();
			int lapisEnchAmount = enchinv.getSecondary() != null ? enchinv.getSecondary().getAmount() : 0;
			ItemStack[] contents = player.getInventory().getStorageContents();
			for (int i = 0; i < contents.length; i++) {
				ItemStack itemstack = contents[i];
				if (lapis.isSimilar(itemstack)) {
					int lapisPlayerAmount = Math.min(itemstack.getAmount(), Material.INK_SACK.getMaxStackSize() - lapisEnchAmount);
					itemstack.setAmount(itemstack.getAmount() - lapisPlayerAmount);
					if (itemstack.getAmount() == 0) {
						contents[i] = null;
					}
					lapisEnchAmount += lapisPlayerAmount;
					if (lapisEnchAmount == Material.INK_SACK.getMaxStackSize()) {
						break;
					}
				}
			}
			player.getInventory().setStorageContents(contents);
			if (lapisEnchAmount != 0) {
				enchinv.setSecondary(new Dye(DyeColor.BLUE).toItemStack(lapisEnchAmount));
			}
			player.updateInventory();
		});
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onInvClose(InventoryCloseEvent event) {
		Inventory topinv = event.getView().getTopInventory();
		if (!(topinv instanceof EnchantingInventory)) {
			return;
		}
		EnchantingInventory enchinv = (EnchantingInventory) topinv;
		ItemStack lapisEnch = enchinv.getSecondary();
		enchinv.setSecondary(null);
		if (lapisEnch != null) {
			Player player = (Player) event.getPlayer();
			player.getInventory().addItem(lapisEnch).values().forEach(rem -> player.getWorld().dropItem(player.getLocation(), rem));
		}
	}

}
