package fiveByFiveCrafting.listeners;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fiveByFiveCrafting.Crafting5x5;
import fiveByFiveCrafting.recipes.Recipe;
import fiveByFiveCrafting.recipes.RecipeItem;
import fiveByFiveCrafting.recipes.RecipeManager;

public class InventoryClick implements Listener {
	RecipeManager recipeManager = Crafting5x5.getRecipeManager();
	Crafting5x5 instance = Crafting5x5.getInstance();

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();

		if (e.getView().getTitle().contains("Crafting")
				&& e.getView().getTopInventory().getType().equals(InventoryType.CHEST)) {

			// if player in bottom inventory return
			// if (e.getRawSlot() >= e.getView().getTopInventory().getSize())
			// return;

			if (e.getRawSlot() == 26 && e.getClickedInventory().getItem(26) != null) {
				// Crafted items, remove items from other side.

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {

					@Override
					public void run() {
						InventoryView inv = p.getOpenInventory();
						for (int y = 0; y < 37; y += 9) {
							for (int x = 0; x < 5; x++) {
								if (inv.getItem(x + y) == null)
									continue;
								ItemStack item = inv.getItem(x + y);
								item.setAmount(item.getAmount() - 1);
								if (item.getAmount() <= 0) {
									inv.getTopInventory().setItem(x + y, new ItemStack(Material.AIR, 1));
								}
								inv.getTopInventory().setItem(x + y, item);
							}
						}
					}
				});
			} else if (((e.getRawSlot() % 9 == 0 || e.getRawSlot() % 9 == 1 || e.getRawSlot() % 9 == 2
					|| e.getRawSlot() % 9 == 3 || e.getRawSlot() % 9 == 4) && e.getRawSlot() < 42)
					|| e.getRawSlot() >= e.getView().getTopInventory().getSize()) {
				// Edited crafting tiles, refresh output
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
					@Override
					public void run() {
						InventoryView inv = p.getOpenInventory();
						RecipeItem[][] craftItems = getCraftItems(inv.getTopInventory());
						Recipe recipe = recipeManager.getRecipeByPattern(craftItems);
						// if no recipe is found return
						if (recipe == null) {
							inv.getTopInventory().setItem(26, new ItemStack(Material.AIR, 1));
							return;
						}
						int count = getLowestCount(craftItems);

						RecipeItem result = recipe.getResult();
						ItemStack item = recipe.getResult().getItemStack();

						// get possible item amount
						int amount = Math.min(64 / result.getCount(), count);
						item.setAmount(amount * result.getCount());

						inv.getTopInventory().setItem(26, item);
					}
				});

			} else {
				e.setCancelled(true);
			}

		} else if (p.getOpenInventory().getTitle().contains("New Recipe")
				&& p.getOpenInventory().getType().equals(InventoryType.CHEST)) {
			if (!(e.getRawSlot() % 9 == 0 || e.getRawSlot() % 9 == 1 || e.getRawSlot() % 9 == 2
					|| e.getRawSlot() % 9 == 3 || e.getRawSlot() % 9 == 4 || e.getRawSlot() == 26)
					&& e.getRawSlot() <= e.getView().getTopInventory().getSize()) {
				e.setCancelled(true);

				// TODO: formed/unformed recipe toggle

			}
			if (e.getRawSlot() == 42) {
				// Create new recipe file
				Inventory inv = p.getOpenInventory().getTopInventory();
				RecipeItem result = new RecipeItem(e.getClickedInventory().getItem(26));
				Recipe recipe = new Recipe(getCraftItems(inv), result, true);

				// write file
				try (Writer writer = new FileWriter(instance.getDataFolder() + "/recipes/customRecipes/" + result.getMaterial() + result.getItemStack().getItemMeta().getDisplayName() + ".json")) {
					Gson gson = new GsonBuilder().create();
					gson.toJson(recipe, writer);

				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		}
	}

	public RecipeItem[][] getCraftItems(Inventory inv) {

		RecipeItem[][] craftItems = new RecipeItem[5][5];

		for (int y = 0; y < 37; y += 9) {
			for (int x = 0; x < 5; x++) {
				if (inv.getItem(y + x) == null) {
					craftItems[y / 9][x] = new RecipeItem(new ItemStack(Material.AIR, 1));
				} else {
					craftItems[y / 9][x] = new RecipeItem(inv.getItem(y + x));
				}
			}
		}
		return craftItems;
	}

	private int getLowestCount(RecipeItem[][] lines) {
		int lowestCount = 64;
		for (RecipeItem[] line : lines) {
			for (RecipeItem recipeItem : line) {
				if (!recipeItem.getMaterial().equals(Material.AIR)) {
					int count = recipeItem.getCount();
					if (count < lowestCount)
						lowestCount = count;
				}
			}
		}
		return lowestCount;
	}
}
