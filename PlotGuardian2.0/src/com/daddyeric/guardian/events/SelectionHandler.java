package com.daddyeric.guardian.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

public class SelectionHandler {

    private Map<String, Location> firstSelection = new HashMap<String, Location>();
    private Map<String, Location> secondSelection = new HashMap<String, Location>();

    public Location getMaximumSelection(String playername) {
	Location first = this.firstSelection.get(playername);
	Location second = this.secondSelection.get(playername);
	int maxX = first.getBlockX() > second.getBlockX() ? first.getBlockX() : second.getBlockX();
	int maxY = first.getBlockY() > second.getBlockY() ? first.getBlockY() : second.getBlockY();
	int maxZ = first.getBlockZ() > second.getBlockZ() ? first.getBlockZ() : second.getBlockZ();
	return new Location(first.getWorld(), maxX, maxY, maxZ);
    }

    public Location getMinimumSelection(String playername) {
	Location first = this.firstSelection.get(playername);
	Location second = this.secondSelection.get(playername);
	int minX = first.getBlockX() > second.getBlockX() ? second.getBlockX() : first.getBlockX();
	int minY = first.getBlockY() > second.getBlockY() ? second.getBlockY() : first.getBlockY();
	int minZ = first.getBlockZ() > second.getBlockZ() ? second.getBlockZ() : first.getBlockZ();
	return new Location(first.getWorld(), minX, minY, minZ);
    }

    public boolean isFirstSelectionSet(String playername) {
	return (this.firstSelection.containsKey(playername)) && (this.firstSelection.get(playername) != null);
    }

    public boolean isSecondSelectionSet(String playername) {
	return (this.secondSelection.containsKey(playername)) && (this.secondSelection.get(playername) != null);
    }

    public void setFirstSelection(String playername, Location newSelection) {
	this.firstSelection.put(playername, newSelection);
    }

    public void setSecondSelection(String playername, Location newSelection) {
	this.secondSelection.put(playername, newSelection);
    }
}
