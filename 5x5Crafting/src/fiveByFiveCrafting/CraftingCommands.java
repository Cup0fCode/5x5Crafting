package fiveByFiveCrafting;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CraftingCommands implements CommandExecutor {

	private final Crafting5x5 gameInstance = Crafting5x5.getInstance();

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("craft")) {
			new CraftingGUI().display(p);
			
		}
		return true;
	}
}
