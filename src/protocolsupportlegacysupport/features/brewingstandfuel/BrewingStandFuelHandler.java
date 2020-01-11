package protocolsupportlegacysupport.features.brewingstandfuel;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import protocolsupportlegacysupport.ProtocolSupportLegacySupport;
import protocolsupportlegacysupport.features.AbstractFeature;

public class BrewingStandFuelHandler extends AbstractFeature<Void> implements Listener {

	@Override
	protected void enable0(Void config) {
		Bukkit.getPluginManager().registerEvents(this, ProtocolSupportLegacySupport.getInstance());
	}

	@Override
	protected void disable0() {
		HandlerList.unregisterAll(this);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	protected void onBrewingClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		ItemStack itemstack = event.getItem();
		if ((itemstack == null) || (itemstack.getType() != Material.BLAZE_POWDER)) {
			return;
		}
		itemstack = itemstack.clone();

		Block clicked = event.getClickedBlock();
		BlockState state = clicked.getState();
		if (!(state instanceof BrewingStand)) {
			return;
		}
		BrewingStand stand = (BrewingStand) state;

		ItemStack existing = stand.getInventory().getFuel();
		if (existing != null) {
			existing = existing.clone();
		}

		PlayerInventory inventory = event.getPlayer().getInventory();
		switch (event.getHand()) {
			case HAND: {
				inventory.setItemInMainHand(existing);
				break;
			}
			case OFF_HAND: {
				inventory.setItemInOffHand(existing);
				break;
			}
			default: {
				throw new IllegalArgumentException("Unexpected hand slot in interaction: " + event.getHand());
			}
		}
		stand.getInventory().setFuel(itemstack);
		event.setCancelled(true);
	}

}
