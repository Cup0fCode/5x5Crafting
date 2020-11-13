package fiveByFiveCrafting.recipes;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.gson.annotations.Expose;

import fiveByFiveCrafting.Utils;

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

	public Recipe(RecipeItem[][] patternLayout, RecipeItem result, boolean shapedRecipe) {
		if (shapedRecipe) {
			type = "minecraft:crafting_shaped";
		} else {
			type = "minecraft:crafting_shapeless";
		}
		this.result = result;

		// create patternArray
		RecipeItem[][] compressedPatternLayout = RecipeManager.compress(patternLayout);
		RecipeItem[] patternArray = new RecipeItem[compressedPatternLayout.length * compressedPatternLayout[0].length];
		Bukkit.getLogger().info(patternArray.length + "");
		int i = 0;
		for (RecipeItem[] line : compressedPatternLayout) {
			for (RecipeItem item : line) {
				patternArray[i] = item;
				Bukkit.getLogger().info(line[i % line.length].getMaterial().toString());
				i++;
			}
		}

		// set keyArray
		RecipeItem[] keyArray = new RecipeItem[patternArray.length];
		for (RecipeItem item : patternArray) {
			for (int x = 0; x <= keyArray.length; x++) {
				if (keyArray[x] == null) {
					keyArray[x] = item;
					break;
				}
				if (keyArray[x].check(item)) {
					break;
				}
			}
		}

		// set key
		key = new HashMap<String, RecipeItem>();
		// HashMap<String, RecipeItem> reverseKey = new HashMap<String, RecipeItem>();
		for (int x = 0; x < keyArray.length; x++) {
			key.put(Utils.ALPHABET[x], keyArray[x]);
			if (keyArray[x] == null)
				break;
		}

		// set Pattern
		pattern = new String[compressedPatternLayout.length];
		i = 0;
		for (int x = 0; x < pattern.length; x++) {
			pattern[x] = "";
			for (RecipeItem item : compressedPatternLayout[x]) {
				int y = 0;
				while (keyArray[y] != null) {
					if (keyArray[y].check(item)) {
						pattern[x] += Utils.ALPHABET[y];
						break;
					}
					y++;
				}
			}
		}
	}

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
