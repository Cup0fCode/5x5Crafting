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
import org.bukkit.inventory.meta.ItemMeta;

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
			if (e.getRawSlot() == 26 && e.getClickedInventory().getItem(26) != null) {
				// Crafted items, remove items from other side.

				if (!e.getCursor().getType().equals(Material.AIR)) {
					e.setCancelled(true);
					return;
				}

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
					@Override
					public void run() {
						InventoryView inv = p.getOpenInventory();
						int count = getLowestCount(getCraftItems(inv.getTopInventory()));
						for (int y = 0; y < 37; y += 9) {
							for (int x = 0; x < 5; x++) {
								if (inv.getItem(x + y) == null)
									continue;
								ItemStack item = inv.getItem(x + y);
								item.setAmount(item.getAmount() - count);
								if (item.getAmount() <= 0) {
									inv.getTopInventory().setItem(x + y, new ItemStack(Material.AIR, 1));
								}
								inv.getTopInventory().setItem(x + y, item);
							}
						}
					}
				});
			} else if (e.getRawSlot() < e.getView().getTopInventory().getSize()
					&& !((e.getRawSlot() % 9 == 0 || e.getRawSlot() % 9 == 1 || e.getRawSlot() % 9 == 2
							|| e.getRawSlot() % 9 == 3 || e.getRawSlot() % 9 == 4) && e.getRawSlot() <= 41)) {
				e.setCancelled(true);
			}
		} else if (p.getOpenInventory().getTitle().contains("New Recipe")
				&& p.getOpenInventory().getType().equals(InventoryType.CHEST)) {
			Inventory inv = p.getOpenInventory().getTopInventory();
			if (!(e.getRawSlot() % 9 == 0 || e.getRawSlot() % 9 == 1 || e.getRawSlot() % 9 == 2
					|| e.getRawSlot() % 9 == 3 || e.getRawSlot() % 9 == 4 || e.getRawSlot() == 26)
					&& e.getRawSlot() <= e.getView().getTopInventory().getSize()) {
				e.setCancelled(true);
			}
			if (e.getRawSlot() == 16) {
				// toggle Shaped Recipe
				if (inv.getItem(16).getItemMeta().getDisplayName().equals("Shaped Recipe")) {
					// toggle Unshaped
					ItemStack shapedRecipeToggle = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
					ItemMeta shapedRecipeToggleMeta = shapedRecipeToggle.getItemMeta();
					shapedRecipeToggleMeta.setDisplayName("Unshaped Recipe");
					shapedRecipeToggle.setItemMeta(shapedRecipeToggleMeta);
					inv.setItem(16, shapedRecipeToggle);
				} else {
					// toggle Shaped
					ItemStack shapedRecipeToggle = new ItemStack(Material.BLUE_STAINED_GLASS_PANE, 1);
					ItemMeta shapedRecipeToggleMeta = shapedRecipeToggle.getItemMeta();
					shapedRecipeToggleMeta.setDisplayName("Shaped Recipe");
					shapedRecipeToggle.setItemMeta(shapedRecipeToggleMeta);
					inv.setItem(16, shapedRecipeToggle);
				}
			}
			if (e.getRawSlot() == 42) {
				// Create new recipe file
				RecipeItem result = new RecipeItem(e.getClickedInventory().getItem(26));
				Recipe recipe = new Recipe(getCraftItems(inv), result,
						inv.getItem(16).getItemMeta().getDisplayName().equals("Shaped Recipe"));

				// write file
				if (!result.getMaterial().equals(Material.AIR)) {
					try (Writer writer = new FileWriter(
							instance.getDataFolder() + "/recipes/customRecipes/" + result.getMaterial() + ".json")) {
						Gson gson = new GsonBuilder().create();
						gson.toJson(recipe, writer);
						p.sendMessage("Recipe successfully created.");
						recipeManager.addRecipe(recipe);
					} catch (IOException e1) {
						p.sendMessage("Recipe creation failed.");
					}
					p.closeInventory();
				} else {
					p.sendMessage("Your recipe must have an output");
				}
			}
		}
	}

	public static int getLowestCount(RecipeItem[][] lines) {
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
		return Math.min(lowestCount,
				64 / Crafting5x5.getRecipeManager().getRecipeByPattern(lines).getResult().getCount());
	}

	public static RecipeItem[][] getCraftItems(Inventory inv) {

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
}
