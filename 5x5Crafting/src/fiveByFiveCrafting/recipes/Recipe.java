package fiveByFiveCrafting.recipes;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.gson.annotations.Expose;

public class Recipe {
	@Expose
	private String type;
	@Expose
	private String[] pattern;
	@Expose
	private HashMap<String, RecipeItem> key;
	@Expose
	private RecipeItem[] ingredients;
	@Expose
	private RecipeItem result;

	public RecipeItem[][] getPattern() {
		// Return RecipeItem[][] of pattern
		RecipeItem[][] pattern = new RecipeItem[this.pattern.length][this.pattern[1].length()];
		int y = 0;
		for (String line : this.pattern) {
			for (int x = 0; x < line.length(); x++) {
				if (x == line.length() - 1) {
					pattern[y][x] = key.get(line.substring(x));
				} else {
					pattern[y][x] = key.get(line.substring(x, x + 1));
				}
				if (pattern[y][x] == null) {
					pattern[y][x] = new RecipeItem(new ItemStack(Material.AIR, 1));
				}
			}
			y++;
		}

		return pattern;
	}

	public String getType() {
		return type;
	}
	
	public RecipeItem[] getIngredients() {
		return ingredients;
	}

	public RecipeItem getResult() {
		return result;
	}
}
