package fr.jojos38.AfkPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

public class Plugin extends JavaPlugin implements Listener {



	private int time = 300;
	private boolean move = true, sendMessage = true, moveCamera = false, interactWithBlock = true, interactWithAir = false, interactWithEntity = true;
	private Map<UUID, Integer> timeMap = new HashMap<>();
	private Map<UUID, Location> lastLocation = new HashMap<>();
	private Map<UUID, String> lastNickname = new HashMap<>();
	public ArrayList<UUID> afkList = new ArrayList<UUID>();



	public String formatMessage(String m, String player) {
		String message = ChatColor.translateAlternateColorCodes('&', getConfig().getString(m)); // Get afk message
		message = message.replace("{player}", player); // Replace {player} by the player name	
		return message;
	}



	public void setAfk(Player player) {
		if (player.hasPermission("afkplus.beingafk")) { // If the player have permission to be afk
			UUID playerUniqueID = player.getUniqueId();
			afkList.add(playerUniqueID); // Add the player to the list of Afk players
			if (getConfig().getBoolean("show-afk-in-tab-list") == true) { // If afk in tab list is enabled
				String message = ChatColor.translateAlternateColorCodes('&', getConfig().getString("afk-tab-list-display")); // Get afk in tab list message
				String playerListName = player.getPlayerListName();
				lastNickname.put(playerUniqueID, playerListName);
				message = message.replace("{player}", player.getPlayerListName()); // Replace {player} by the player name
				player.setPlayerListName(message);
				;
			}		
			if (getConfig().getBoolean("enable-afk-messages") == true) { // If afk message is enabled
				String message = formatMessage("afk-message", player.getName()); // Get message
				Bukkit.broadcastMessage(message); // Send it
			}
		}
	}



	public void resetPlayerTimer(Player player) {
		UUID playerUniqueID = player.getUniqueId();
		timeMap.put(playerUniqueID, 0); // Reset the player timer to 0
		if (afkList.contains(playerUniqueID)) { // If player is afk
			afkList.remove(playerUniqueID); // Remove him from afk list		
			if (getConfig().getBoolean("show-afk-in-tab-list") == true) { // If afk in tab list is enabled
				String message = ChatColor.translateAlternateColorCodes('&', getConfig().getString("no-longer-afk-tab-list-display")); // Get no longer afk in tab list message
				message = message.replace("{player}", lastNickname.get(playerUniqueID)); // Replace {player} by the player name			
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
            	for (Map.Entry<UUID, Integer> entry : timeMap.entrySet()) { // Get all players in timeMap             		
            		UUID playerUniqueID = entry.getKey(); // Get player
            		int timer = entry.getValue(); // Get his timer
            		if (!afkList.contains(playerUniqueID)) { // If the player is not already AFK
            			if (timer >= time) { // If the time is out
            				setAfk(Bukkit.getPlayer(playerUniqueID)); // Set him AFK
            			} else { // Else
            				timeMap.put(playerUniqueID, timer + 1); // Increase their timer
            			}
            		}
            	}    	
            }
        }, 0, 20); // In ticks
	}


	
	public void loadConfig() {
		getConfig().options().copyDefaults(true); // Get config file
		saveDefaultConfig(); // Create config file
		// Afk settings list
		time               = getConfig().getInt    ("time");
		move               = getConfig().getBoolean("move");
		interactWithBlock  = getConfig().getBoolean("interact-with-blocks");
		interactWithAir    = getConfig().getBoolean("interact-with-air");
		interactWithEntity = getConfig().getBoolean("interact-with-entity");
		moveCamera         = getConfig().getBoolean("move-camera");
		sendMessage        = getConfig().getBoolean("send-message");
	}
	
	

	public void onEnable() { //Lorsque le plugin est activé	
		this.getServer().getPluginManager().registerEvents(this, this);	// Register all events
		getCommand("afk").setExecutor(new AfkCommand(this)); // Create afk command
		getCommand("afkplusreload").setExecutor(new AfkCommand(this)); // Create afk command
		loadConfig();
		schedule(); // Start the timer
		System.out.println("[AfkPlus] Loaded successfully");
	}



	public void onDisable(){
		System.out.println("[AfkPlus] Disabled successfully");
	}	



	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer(); // Get player
		UUID playerUniqueID = player.getUniqueId();
		Location lastPlayerLocation = lastLocation.get(playerUniqueID);
		if (move) { // If movement enabled in settings
			if (!lastPlayerLocation.getBlock().equals(player.getLocation().getBlock())) { // If player location has changed
				resetPlayerTimer(player); // Reset afk timer
				lastLocation.put(playerUniqueID, lastPlayerLocation);
			}
		}
		if (moveCamera) { // If camera enabled in the settings
			if (!lastPlayerLocation.getDirection().equals(player.getEyeLocation().getDirection())) { // If player camera has moved
				resetPlayerTimer(player); // Reset afk timer
				lastLocation.put(playerUniqueID, lastPlayerLocation);
			}
		}	
	}	



	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		UUID playerUniqueID = e.getPlayer().getUniqueId();
		timeMap.put(playerUniqueID, 0); // If player connect add him to the player time list
		lastLocation.put(playerUniqueID, e.getPlayer().getLocation());
	}



	@EventHandler(priority=EventPriority.NORMAL)
	public void OnPlayerQuit(PlayerQuitEvent e) {
		UUID playerUniqueID = e.getPlayer().getUniqueId();
		timeMap.remove(playerUniqueID); // If player disconnect remove him from the time list
		afkList.remove(playerUniqueID);
	}



	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (interactWithAir) { // If interact with air is enabled in settings
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
    	if (interactWithEntity) { // If interact with entity enabled in settings
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
		if (sendMessage) { // If send messages enabled in settings
			resetPlayerTimer(e.getPlayer()); // Reset afk timer
		}
	}
}