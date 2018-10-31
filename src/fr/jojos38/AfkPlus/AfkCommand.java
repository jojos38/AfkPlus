package fr.jojos38.AfkPlus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AfkCommand implements CommandExecutor {
	
	private final Plugin plugin;
	
	public AfkCommand(Plugin plugin) {
		this.plugin = plugin;
	}
	
	
	
	
	
	public void toggleAfk(Player player) { // Toggle Afk for specific player
    	if (plugin.afkList.contains(player)) { // If player is Afk
    		plugin.resetPlayerTimer(player); // Disable Afk
    	} else { // If player if not Afk
    		plugin.setAfk(player); // Enable Afk
    	}
	}
	
	
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command commande, String msg, String[] args) {
        if (sender instanceof Player) {      	
    		Player player = ((Player) sender).getPlayer(); // Player entered the command
        	if (args.length > 0) { // If command have argument
        		
        		if (sender.hasPermission("afkplus.afkplayers")) { 		
	        		try {
	        			Player targetPlayer = plugin.getServer().getPlayer(args[0]); // Search if a player have this nickname on the server
	        			toggleAfk(targetPlayer); // If yes enable Afk
	        		} catch (NullPointerException e) { // If no player have the specified nickname
	        			String message = plugin.formatMessage("player-not-found-message", args[0]);		
	        			player.sendMessage(message); // Send no player found message
	        		}             		
        		} else {
        			String message = plugin.formatMessage("permission-message", "");		
        			player.sendMessage(message); // Send no player found message
        		}
        		
        	} else { // If no specified arguments
        		toggleAfk(player); // Toggle Afk for the player that entered the command
        	}
        }
        return true;
	}
}
