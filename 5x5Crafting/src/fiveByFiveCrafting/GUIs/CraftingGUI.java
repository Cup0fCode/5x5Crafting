package fiveByFiveCrafting.GUIs;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import fiveByFiveCrafting.Crafting5x5;
import fiveByFiveCrafting.listeners.InventoryClick;
import fiveByFiveCrafting.recipes.Recipe;
import fiveByFiveCrafting.recipes.RecipeItem;
import fiveByFiveCrafting.recipes.RecipeManager;

public class CraftingGUI implements InventoryHolder {
	private Inventory inv;
	private RecipeManager recipeManager = Crafting5x5.getRecipeManager();
	private Crafting5x5 instance = Crafting5x5.getInstance();
	private int task;

	public CraftingGUI() {
		inv = Bukkit.createInventory(this, 54, "Crafting");

	}

	public void display(Player player) {

		// Fill in background tiles
		ItemStack backgroundTile = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
		ItemMeta backgroundTileMeta = backgroundTile.getItemMeta();
		backgroundTileMeta.setDisplayName(" ");
		backgroundTile.setItemMeta(backgroundTileMeta);

		for (int i = 5; i < 54; i++) {
			inv.setItem(i, backgroundTile);
		}

		// Set output tile
		ItemStack outputTile = new ItemStack(Material.AIR, 1);
		inv.setItem(26, outputTile);

		// Set rightArrowSkull
		ItemStack rightArrowSkull = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
		SkullMeta rightArrowSkullMeta = (SkullMeta) rightArrowSkull.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), "");
		profile.getProperties().put("textures", new Property("textures",
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJiMGMwN2ZhMGU4OTIzN2Q2NzllMTMxMTZiNWFhNzVhZWJiMzRlOWM5NjhjNmJhZGIyNTFlMTI3YmRkNWIxIn19fQ=="));
		Field profileField = null;
		try {
			profileField = rightArrowSkullMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(rightArrowSkullMeta, profile);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}

		rightArrowSkullMeta.setDisplayName(" ");
		rightArrowSkull.setItemMeta(rightArrowSkullMeta);
		inv.setItem(24, rightArrowSkull);

		// Fill in crafting tiles
		ItemStack craftingTiles = new ItemStack(Material.AIR, 1);;

		for (int i = 0; i < 41; i++) {
			inv.setItem(i, craftingTiles);

			if (i % 9 == 4)
				i += 4;
		}

		player.openInventory(inv);

		this.task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override
			public void run() {
				if (player.getOpenInventory() != null && player.getOpenInventory().getTitle().contains("Crafting")
						&& player.getOpenInventory().getTopInventory().getType().equals(InventoryType.CHEST)) {

					InventoryView inv = player.getOpenInventory();
					RecipeItem[][] craftItems = InventoryClick.getCraftItems(inv.getTopInventory());
					Recipe recipe = recipeManager.getRecipeByPattern(craftItems);
					// if no recipe is found return
					if (recipe == null) {
						inv.getTopInventory().setItem(26, new ItemStack(Material.AIR, 1));
						return;
					}

					RecipeItem result = recipe.getResult();
					int count = InventoryClick.getLowestCount(craftItems);
					
					ItemStack item = recipe.getResult().getItemStack();

					// get possible item amount
					item.setAmount(result.getCount() * count);

					inv.getTopInventory().setItem(26, item);
				} else {
					Bukkit.getServer().getScheduler().cancelTask(task);
				}
			}
		}, 1, 1);

	}

	public Inventory getInventory() {
		return inv;
	}
}