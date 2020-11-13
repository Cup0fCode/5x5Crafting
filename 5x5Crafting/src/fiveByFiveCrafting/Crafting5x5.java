package fiveByFiveCrafting;

import java.util.Arrays;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import fiveByFiveCrafting.listeners.InventoryClick;
import fiveByFiveCrafting.listeners.InventoryClose;
import fiveByFiveCrafting.recipes.RecipeManager;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Crafting5x5 extends JavaPlugin {
    private static Crafting5x5 instance;
    private static RecipeManager recipeManager;

    @Override
    public void onEnable() {
        instance = this;
        
        recipeManager = new RecipeManager();
        
        recipeManager.storeRecipes();
        
        getLogger().info("Loaded 5x5Crafting");
        getCommand("craft").setExecutor(new CraftingCommands());
        //getCommand("new recipe").setExecutor(new CraftingCommands());


        registerListeners(new InventoryClose(), new InventoryClick());
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

}
