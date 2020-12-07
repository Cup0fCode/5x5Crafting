package fiveByFiveCrafting.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

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
			// create patternArray
			RecipeItem[][] compressedPatternLayout = RecipeManager.compress(patternLayout);
			RecipeItem[] patternArray = new RecipeItem[compressedPatternLayout.length * compressedPatternLayout[0].length];
			
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
		} else {
			type = "minecraft:crafting_shapeless";
			ArrayList<RecipeItem> items = new ArrayList<RecipeItem>();
			for (RecipeItem[] line : patternLayout) {
				for (RecipeItem item : line) {
					if (item.getMaterial().equals(Material.AIR))
						continue;
					items.add(item);
				}
			}
			ingredients = items.toArray(new RecipeItem[items.size()]);
		}
		this.result = result;
	}

	public Recipe(org.bukkit.inventory.Recipe recipe) {
		// set result
		result = new RecipeItem(recipe.getResult());

		if (recipe instanceof org.bukkit.inventory.ShapedRecipe) {
			// set type
			type = "minecraft:crafting_shaped";

			// set key and pattern
			pattern = ((org.bukkit.inventory.ShapedRecipe) recipe).getShape();
			key = new HashMap<String, RecipeItem>();
			
			((org.bukkit.inventory.ShapedRecipe) recipe).getIngredientMap();
			for (Entry<Character, ItemStack> entry : ((org.bukkit.inventory.ShapedRecipe) recipe).getIngredientMap() .entrySet()) {
				key.put(String.valueOf(entry.getKey()), new RecipeItem(entry.getValue()));
			}
			
			Map<Character, RecipeChoice> choiceMap = ((org.bukkit.inventory.ShapedRecipe) recipe).getChoiceMap();
			for (Entry<Character, RecipeChoice> entry : choiceMap.entrySet()) {
				key.put(String.valueOf(entry.getKey()), new RecipeItem(entry.getValue()));
			}
			key.put(" ", new RecipeItem(new ItemStack(Material.AIR)));

		}
		if (recipe instanceof org.bukkit.inventory.ShapelessRecipe) {
			// set type
			type = "minecraft:crafting_shapeless";

			// set ingredients
			List<ItemStack> ingredientList = ((org.bukkit.inventory.ShapelessRecipe) recipe).getIngredientList();
			ingredients = new RecipeItem[ingredientList.size()];
			for (int i = 0; i < ingredientList.size(); i++) {
				ingredients[i] = new RecipeItem(ingredientList.get(i));
			}
		}
	}

	public RecipeItem[][] getPattern() {
		// Return RecipeItem[][] of pattern
		RecipeItem[][] pattern = new RecipeItem[this.pattern.length][this.pattern[0].length()];
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
