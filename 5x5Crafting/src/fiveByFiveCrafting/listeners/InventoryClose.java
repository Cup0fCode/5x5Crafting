package fiveByFiveCrafting.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fiveByFiveCrafting.recipes.RecipeItem;

public class InventoryClose implements Listener {
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		Inventory inv = e.getInventory();
		if (p.getOpenInventory().getTitle().contains("Crafting") && inv.getType().equals(InventoryType.CHEST)) {
			for (int y = 0; y < 37; y += 9) {
				for (int x = 0; x < 5; x++) {
					if (inv.getItem(y + x) != null) {
						ItemStack item = inv.getItem(y + x);
						p.getInventory().addItem(item);
					}
				}
			}
		}
	}
}
