package fr.jojos38.AfkPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Plugin extends JavaPlugin implements Listener {

	
	
	
	
	private int time = 300;
	private Vector eyesLocationTemp;
	private Block playerLocationTemp;
	private boolean move = true, sendMessage = true, moveCamera = false, interactWithBlock = true, interactWithAir = false, interactWithEntity = true;
	private Map<Player, Integer> timeMap = new HashMap<>();
	public ArrayList<Player> afkList = new ArrayList<Player>();
	
	
	
	
	
	public String formatMessage(String m, String player) {
		String message = ChatColor.translateAlternateColorCodes('&', getConfig().getString(m)); // Get afk message
		message = message.replace("{player}", player); // Replace {player} by the player name	
		return message;
	}
	
	
	
	
	
	public void setAfk(Player player) {
		if (player.hasPermission("afkplus.beingafk")) { // If the player have permission to be afk
			afkList.add(player); // Add the player to the list of Afk players
			if (getConfig().getBoolean("show-afk-in-tab-list") == true) { // If afk in tab list is enabled
				String message = ChatColor.translateAlternateColorCodes('&', getConfig().getString("afk-tab-list-display")); // Get afk in tab list message
				message = message.replace("{player}", player.getName()); // Replace {player} by the player name			
				player.setPlayerListName(message);		
			}		
			if (getConfig().getBoolean("enable-afk-messages") == true) { // If afk message is enabled
				String message = formatMessage("afk-message", player.getName()); // Get message
				Bukkit.broadcastMessage(message); // Send it
			}
		}
	}
	
	public void resetPlayerTimer(Player player) {
		timeMap.put(player, 0); // Reset the player timer to 0
		if (afkList.contains(player)) { // If player is afk
			afkList.remove(player); // Remove him from afk list		
			if (getConfig().getBoolean("show-afk-in-tab-list") == true) { // If afk in tab list is enabled
				String message = ChatColor.translateAlternateColorCodes('&', getConfig().getString("no-longer-afk-tab-list-display")); // Get no longer afk in tab list message
				message = message.replace("{player}", player.getName()); // Replace {player} by the player name			
				player.setPlayerListName(message); // Remove [afk] in tab list	
			}
			if (getConfig().getBoolean("enable-no-longer-afk-messages") == true) { // If no longer afk message is enabled
				String message = formatMessage("no-longer-afk-message", player.getName()); // Get message
				Bukkit.broadcastMessage(message); // Send it
			}
		}
	}
	
	
	
	
	
	private void schedule() {		
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
            	for (Map.Entry<Player, Integer> entry : timeMap.entrySet()) { // Get all players in timeMap             		
            		Player player = entry.getKey(); // Get player
            		int timer = entry.getValue(); // Get his timer	
            	    if (timer >= time && !afkList.contains(player)) { // If time have been exceeded and player is not already afk
            	    	setAfk(player);
            	    }           
            	    if (!afkList.contains(player) && timer <= time) { // If player is not afk
                	    timeMap.put(player, timer + 1); // Add 1 to their timer 
            	    }      	    
            	    if (timer == 1 && moveCamera == true) { // Avoid a small bug with camera move
            	    	eyesLocationTemp = player.getEyeLocation().getDirection();
            	    }           	       	        
            	}    	
            }
        }, 0, 20);
	}

	
	
	

	public void onEnable() { //Lorsque le plugin est activé	
		this.getServer().getPluginManager().registerEvents(this, this);	// Register all events
		getConfig().options().copyDefaults(true); // Get config file
		saveDefaultConfig(); // Create config file
		getCommand("afk").setExecutor(new AfkCommand(this)); // Create afk command
		
		// Afk settings list
		time               = getConfig().getInt    ("time");
		move               = getConfig().getBoolean("move");
		interactWithBlock  = getConfig().getBoolean("interact-with-blocks");
		interactWithAir    = getConfig().getBoolean("interact-with-air");
		interactWithEntity = getConfig().getBoolean("interact-with-entity");
		moveCamera         = getConfig().getBoolean("move-camera");
		sendMessage        = getConfig().getBoolean("send-message");
		
		schedule(); // Start the timer
		System.out.println("[AfkPlus] Loaded successfully");
	}
	
	
	
	
	
	public void onDisable(){
		System.out.println("[AfkPlus] Disabled successfully");
	}	
	
	
	
	
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {	
		Player player = e.getPlayer(); // Get player
		if (move == true) { // If movement enabled in settings
			if (playerLocationTemp != null) { // To avoid error at the first execution
				if (!playerLocationTemp.equals(player.getLocation().getBlock())) { // If player location has changed
					resetPlayerTimer(player); // Reset afk timer
				}
			}			
			playerLocationTemp = player.getLocation().getBlock();
		}
		if (moveCamera == true) { // If camera enabled in the settings	
			if (eyesLocationTemp != null) { // To avoid error at the first execution
				if (!eyesLocationTemp.equals(player.getEyeLocation().getDirection())) { // If player camera has moved
					resetPlayerTimer(player); // Reset afk timer
				}
			}			
			eyesLocationTemp = player.getEyeLocation().getDirection();
		}	
	}	
	
	
	
	
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {		
		timeMap.put(e.getPlayer(), 0); // If player connect add him to the player time list   
	}
	
	

	
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void OnPlayerQuit(PlayerQuitEvent e) {
		timeMap.remove(e.getPlayer()); // If player disconnect remove him from the time list
	}
	
	
	
	
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {

		if (interactWithAir == true) { // If interact with air is enabled in settings
			if (e.getClickedBlock() == null) { // If interacted block is air
				resetPlayerTimer(e.getPlayer()); // Reset afk timer
			}	
		}
		if (interactWithBlock == true) { // If interact with blocks is enabled in settings
			if (e.getClickedBlock() != null) { // If interacted block anyelse than air
				resetPlayerTimer(e.getPlayer()); // Reset afk timer
			}	
		}		
	}	    
	
	
	
	
	
    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
    	if (interactWithEntity == true) { // If interact with entity enabled in settings
            if (e.getDamager() instanceof Player) { // If the damager is a player
            	resetPlayerTimer((Player) e.getDamager()); // Reset his timer
            }
            
            if (e.getEntity() instanceof Projectile) { // If the player got hit by arrow
            	Projectile p = (Projectile) e.getEntity(); // Convert entity to arrow
            	resetPlayerTimer((Player) p.getShooter()); // Get the shooter
            } else if (e.getEntity() instanceof Player) { // If the shooter is a player
            	resetPlayerTimer((Player) e.getEntity()); // Reset his timer
            }
    	}  
    }
	
	
    
    
    
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (sendMessage == true) { // If send messages enabled in settings
			resetPlayerTimer(e.getPlayer()); // Reset afk timer
		}
	}
}
	

