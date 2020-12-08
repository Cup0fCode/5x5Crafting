package fiveByFiveCrafting.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import fiveByFiveCrafting.GUIs.CraftingGUI;

public class TableClick implements Listener {
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.CRAFTING_TABLE) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (event.getPlayer() != null) {
				event.setCancelled(true);
				new CraftingGUI().display(event.getPlayer());
			}
		}
	}
}
