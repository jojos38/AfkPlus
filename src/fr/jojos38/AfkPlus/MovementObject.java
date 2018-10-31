package fr.jojos38.AfkPlus;

import org.bukkit.Location;

public class MovementObject {
	 
    private String playerName;
    private Location location;
    private long timeLastMoved;
 
    public MovementObject(String name, Location loc, long time){
        setPlayerName(name);
        setLocation(loc);
        setTimeLastMoved(time);
    }
 
    public String getPlayerName() {
        return playerName;
    }
 
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
 
    public Location getLocation() {
        return location;
    }
 
    public void setLocation(Location location) {
        this.location = location;
    }
 
    public long getTimeLastMoved() {
        return timeLastMoved;
    }
 
    public void setTimeLastMoved(long timeLastMoved) {
        this.timeLastMoved = timeLastMoved;
    }
}
