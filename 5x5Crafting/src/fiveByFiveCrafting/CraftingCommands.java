package fiveByFiveCrafting;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fiveByFiveCrafting.GUIs.CraftingGUI;
import fiveByFiveCrafting.GUIs.RecipeCreationGUI;

public class CraftingCommands implements CommandExecutor {

	private final Crafting5x5 gameInstance = Crafting5x5.getInstance();

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		boolean CraftCommandOp = gameInstance.getConfig().getBoolean("settings.permissions.craftCommandOp");
		boolean CraftNewCommandOp = gameInstance.getConfig().getBoolean("settings.permissions.craftNewCommandOp");
		
		if (!(sender instanceof Player)) {
			return true;
		}
		
		Player p = (Player) sender;
		if (args.length < 1 && (!CraftCommandOp || (CraftCommandOp && p.isOp()))) {
			new CraftingGUI().display(p);	
		} else 
		if (args[0].equalsIgnoreCase("new" )&& (!CraftNewCommandOp || (CraftNewCommandOp && p.isOp()))) {
			new RecipeCreationGUI().display(p);	
		}
		return true;
	}
}
