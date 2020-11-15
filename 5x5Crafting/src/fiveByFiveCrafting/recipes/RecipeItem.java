package fiveByFiveCrafting.recipes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.gson.annotations.Expose;

public class RecipeItem {
	// TODO: Expose other item attributes
	@Expose
	private String item;
	@Expose
	private int count;
	@Expose
	private RecipeItemMeta recipeItemMeta;

	public RecipeItem(ItemStack item) {
		if (item == null) {
			this.item = "AIR";
		} else {
			this.item = item.getType().name();
		}
		if (item.hasItemMeta())
			recipeItemMeta = new RecipeItemMeta(item.getItemMeta());
			
		count = item.getAmount();
	}

	public Material getMaterial() {
		if (item == null) {
			return Material.AIR;
		}
		Material m = Material.getMaterial(item.substring(item.indexOf(":") + 1).toUpperCase());
		if (m == null) {
			return Material.AIR;
		}

		return Material.getMaterial(item.substring(item.indexOf(":") + 1).toUpperCase());
	}

	public boolean check(RecipeItem recipeItem) {
		if (recipeItem == null) {
			return false;
		}
		if (!recipeItem.getMaterial().equals(getMaterial()))
			return false;
		if (recipeItemMeta != null) {
			if (!recipeItem.hasRecipeItemMeta()) {
				return false;
			}
			if (!recipeItemMeta.check(recipeItem.getRecipeItemMeta())) {
				return false;
			}
		}
		return true;
	}

	private RecipeItemMeta getRecipeItemMeta() {
		return recipeItemMeta;
	}

	private boolean hasRecipeItemMeta() {
		if (recipeItemMeta == null) {
			return false;
		}
		return true;
	}

	public ItemStack getItemStack() {
		ItemStack itemStack = new ItemStack(getMaterial(), count);
		if (recipeItemMeta != null)
			itemStack.setItemMeta(recipeItemMeta.getItemMeta(getMaterial()));
		return itemStack;
	}

	public int getCount() {
		if (count == 0)
			return 1;
		return count;
	}
}
