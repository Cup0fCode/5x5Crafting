package fiveByFiveCrafting.recipes;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fiveByFiveCrafting.Crafting5x5;

public class RecipeManager {
	private final Crafting5x5 plugin = Crafting5x5.getInstance();
	private ArrayList<Recipe> recipes;

	public void storeRecipes() {
		File folder = new File(plugin.getDataFolder() + "/recipes");
		File folder2 = new File(plugin.getDataFolder() + "/recipes/customRecipes/");
		
		File[] listOfFiles = (File[]) ArrayUtils.addAll((File[]) folder.listFiles(),(File[]) folder2.listFiles());

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		recipes = new ArrayList<Recipe>();

		for (File file : listOfFiles) {
			if (file.isFile()) {
				try {
					plugin.getLogger().info("Loading " + file.getAbsolutePath());
					String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

					plugin.getLogger().info("Loaded recipe " + gson.fromJson(content, Recipe.class));
					recipes.add(gson.fromJson(content, Recipe.class));
				} catch (IOException err) {
					plugin.getLogger().info(err.getMessage());
				}
			}
		}
	}

	public ArrayList<Recipe> getRecipes() {
		return recipes;
	}

	public Recipe getRecipeByPattern(RecipeItem[][] pattern) {
		// compress TablePattern
		RecipeItem[][] tablePattern = compress(pattern);

		if (tablePattern == null) {
			return null;
		}

		recipeLoop: for (Recipe recipe : recipes) {
			if (recipe.getType().contains("crafting_shaped")) {
				// determine what recipe matches the tablePattern
				RecipeItem[][] recipePattern = recipe.getPattern();

				// continue if patterns are not the same size
				if (recipePattern.length != tablePattern.length || recipePattern[0].length != tablePattern[0].length)
					continue;

				boolean equal = true;
				loop: for (int y = 0; y < recipePattern.length; y++) {
					for (int x = 0; x < recipePattern[0].length; x++) {

						if (!recipePattern[y][x].check(tablePattern[y][x])) {
							equal = false;
							break loop;
						}
					}
				}

				if (equal) {
					return recipe;
				}
			} else if (recipe.getType().contains("crafting_shapeless")) {
				//check if amount of items required is same as amount of items in table
				RecipeItem[] ingredients = recipe.getIngredients();
				RecipeItem[][] itemMatrix = deepClone(pattern);
				
				//Bukkit.getLogger().info(getAmount(itemMatrix) + ":" + ingredients.length);
				if (getAmount(itemMatrix) != ingredients.length) 
					continue;
				
				
			
				ingredientsLoop: for (RecipeItem ingredient : ingredients) {
					boolean hasItem = false;
					for (RecipeItem[] items : itemMatrix) {
						for (int i = 0; i < items.length ; i++) {
							if (items[i] == null) {
								continue;
							}
							if (ingredient.check(items[i])) {
								hasItem = true;
								items[i] = null;
								//Bukkit.getLogger().info(ingredient.getMaterial().name());
								continue ingredientsLoop;
							}
						}
					}
					if (!hasItem) {
						continue recipeLoop;
					}
				}
				Bukkit.getLogger().info(recipe.toString());
				return recipe;
			}
		}

		return null;
	}
	
	public static int getAmount(RecipeItem[][] matrix) {
		int amount = 0;
		for (RecipeItem[] array : matrix) {
			for (RecipeItem item : array) {
				if (!item.getMaterial().equals(Material.AIR)) {
					amount++;
				}
			}
		}
		return amount;
	}

	public static RecipeItem[][] compress(RecipeItem[][] pattern) {
		// return pattern removing empty rows and columns

		// If everything is 0 return null

		boolean check = true;
		check: for (int y = 0; y < pattern.length; y++) {
			for (int x = 0; x < pattern[0].length; x++) {
				if (!pattern[y][x].getMaterial().equals(Material.AIR)) {
					check = false;
					break check;
				}
			}
		}
		if (check)
			return null;

		// compress top row
		while (pattern.length != 0) {

			RecipeItem[][] patternTest = deepClone(pattern);
			boolean shrink = true;
			for (RecipeItem item : patternTest[0]) {
				// check if all items in row are air
				if (!item.getMaterial().equals(Material.AIR)) {
					// if material isn't air don't shrink
					shrink = false;
					break;
				}
			}
			if (shrink) {
				// remove row
				patternTest = new RecipeItem[pattern.length - 1][pattern[0].length];
				for (int y = 1; y < pattern.length; y++) {
					for (int x = 0; x < pattern[0].length; x++) {
						patternTest[y - 1][x] = pattern[y][x];
					}
				}
				pattern = deepClone(patternTest);
			} else {
				// nothing happened, break
				break;
			}
		}

		// compress bottom row
		while (pattern.length != 0) {

			RecipeItem[][] patternTest = deepClone(pattern);
			boolean shrink = true;
			for (RecipeItem item : patternTest[patternTest.length - 1]) {
				// check if all items in row are air
				if (!item.getMaterial().equals(Material.AIR)) {
					// if material isn't air don't shrink
					shrink = false;
				}
			}
			if (shrink) {
				// remove row
				patternTest = new RecipeItem[pattern.length - 1][pattern[0].length];
				for (int y = 0; y < pattern.length - 1; y++) {
					for (int x = 0; x < pattern[0].length; x++) {
						patternTest[y][x] = pattern[y][x];
					}
				}
				pattern = deepClone(patternTest);
			} else {
				// nothing happened, break
				break;
			}
		}
		// compress left column
		while (pattern.length != 0) {

			RecipeItem[][] patternTest = deepClone(pattern);
			boolean shrink = true;
			for (int i = 0; i < pattern.length; i++) {
				// check if all items in row are air
				if (!patternTest[i][0].getMaterial().equals(Material.AIR)) {
					// if material isn't air don't shrink
					shrink = false;
					break;
				}
			}
			if (shrink) {
				// remove row
				patternTest = new RecipeItem[pattern.length][pattern[0].length - 1];
				for (int y = 0; y < pattern.length; y++) {
					for (int x = 1; x < pattern[0].length; x++) {
						patternTest[y][x - 1] = pattern[y][x];
					}
				}
				pattern = deepClone(patternTest);
			} else {
				// nothing happened, break
				break;
			}
		}

		// compress right column
		while (pattern.length != 0) {

			RecipeItem[][] patternTest = deepClone(pattern);
			boolean shrink = true;
			for (int i = 0; i < pattern.length; i++) {
				// check if all items in row are air
				if (!patternTest[i][pattern[0].length - 1].getMaterial().equals(Material.AIR)) {
					// if material isn't air don't shrink
					shrink = false;
					break;
				}
			}
			if (shrink) {
				// remove row
				patternTest = new RecipeItem[pattern.length][pattern[0].length - 1];
				for (int y = 0; y < pattern.length; y++) {
					for (int x = 0; x < pattern[0].length - 1; x++) {
						patternTest[y][x] = pattern[y][x];
					}
				}
				pattern = deepClone(patternTest);
			} else {
				// nothing happened, break
				break;
			}
		}

		// Bukkit.getLogger().info(pattern.toString());
		return pattern;
	}

	public static RecipeItem[][] deepClone(RecipeItem[][] first) {
		RecipeItem[][] second = new RecipeItem[first.length][first[0].length];
		int i = 0;
		for (RecipeItem[] line : first) {
			second[i] = line.clone();
			i++;
		}
		return second;
	}

}
