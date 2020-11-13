package fiveByFiveCrafting.recipes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.gson.annotations.Expose;

public class RecipeItem {
	//TODO: Expose other item attributes
	@Expose
	private String item;
	@Expose
	private int count;


	public RecipeItem(ItemStack item) {
		if (item == null) {
			this.item = "AIR";
		} else {
			this.item = item.getType().name();
		}
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
		boolean check = true;
		if (recipeItem == null) {
			return false;
		}
		if (!recipeItem.getMaterial().equals(getMaterial()))
			check = false;

		return check;
	}

	public ItemStack getItemStack() {
		ItemStack itemStack = new ItemStack(getMaterial(), count);
		return itemStack;
	}

	public int getCount() {
		if (count == 0)
			return 1;
		return count;
	}
}
