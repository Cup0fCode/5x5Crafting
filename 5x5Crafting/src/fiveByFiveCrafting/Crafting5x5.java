package fiveByFiveCrafting;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import fiveByFiveCrafting.listeners.InventoryClick;
import fiveByFiveCrafting.listeners.InventoryClose;
import fiveByFiveCrafting.listeners.TableClick;
import fiveByFiveCrafting.recipes.RecipeManager;

public class Crafting5x5 extends JavaPlugin {
    private static Crafting5x5 instance;
    private static RecipeManager recipeManager;
    private File configFile;
	private FileConfiguration config;

    @Override
    public void onEnable() {
        instance = this;
        
        loadConfig();
        
        recipeManager = new RecipeManager();
        recipeManager.storeRecipes();
        
        getLogger().info("Loaded 5x5Crafting");
        getCommand("craft").setExecutor(new CraftingCommands());

        registerListeners(new InventoryClose(), new InventoryClick());
        if (config.getBoolean("settings.craftingTableEnabled"))
        	registerListeners(new TableClick());
        	
    }

    private void registerListeners(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }
    
    public static RecipeManager getRecipeManager() {
    	return recipeManager;
    }

    public static Crafting5x5 getInstance() {
        return instance;
    }

	private void loadConfig() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}

		configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		config = YamlConfiguration.loadConfiguration(configFile);

		HashMap<String, Object> defaultConfig = new HashMap<>();

		defaultConfig.put("settings.craftingTableEnabled", true);
		defaultConfig.put("settings.permissions.craftCommandOp", true);
		defaultConfig.put("settings.permissions.craftNewCommandOp", true);

		for (String key : defaultConfig.keySet()) {
			if(!config.contains(key)) {
				config.set(key, defaultConfig.get(key));
			}
		}

		this.saveConfig();
	}
	
	@Override
	public void saveConfig() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public FileConfiguration getConfig() {
		return config;
	}

}
