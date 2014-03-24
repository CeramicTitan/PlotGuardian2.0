package com.daddyeric.guardian.protection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.daddyeric.guardian.PlotGuardian;

public class PlotDatabase {

    private final PlotGuardian guardian;
    private final PlotManager plotManager;
    private final Map<String, Plot> protectedPlots = new HashMap<String, Plot>();
    private final Map<String, ProtectivePlot> protectivePlots = new HashMap<String, ProtectivePlot>();
    private FileConfiguration plots = null;
    private File plotsFile = null;

    public PlotDatabase(PlotGuardian g) {
	this.guardian = g;
	this.plotManager = new PlotManager(this);
	loadPlotsFile();
	loadPlotsFromFile();
    }

    public boolean addPlotToDatabase(String world, String name, String owner, Location min, Location max, int price) {
	ConfigurationSection category = this.plots.getConfigurationSection("plots");
	if (category == null) {
	    category = this.plots.createSection("plots");
	}
	ConfigurationSection Sender = category.createSection(name);
	Sender.set("world", world);
	Sender.set("min", min.getBlockX() + ", " + min.getBlockY() + ", " + min.getBlockZ());
	Sender.set("max", max.getBlockX() + ", " + max.getBlockY() + ", " + max.getBlockZ());
	Sender.set("owner", owner);
	Sender.set("members", null);
	Sender.set("price", Integer.valueOf(price));
	Plot newPlot = new Plot(world, owner, null, min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ(), price);
	this.protectedPlots.put(name, newPlot);
	savePlotsFile();
	return true;
    }

    public boolean addProtectivePlotToDatabase(String world, String name, Location min, Location max) {
	ConfigurationSection category = this.plots.getConfigurationSection("protectivePlots");
	if (category == null) {
	    category = this.plots.createSection("protectivePlots");
	}
	ConfigurationSection Sender = category.createSection(name);
	Sender.set("world", world);
	Sender.set("min", min.getBlockX() + ", " + min.getBlockY() + ", " + min.getBlockZ());
	Sender.set("max", max.getBlockX() + ", " + max.getBlockY() + ", " + max.getBlockZ());
	ProtectivePlot newPlot = new ProtectivePlot(world, min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ());
	this.protectivePlots.put(name, newPlot);
	savePlotsFile();
	return true;
    }

    public boolean removePlotFromDatabase(String name) {
	this.protectedPlots.remove(name);
	ConfigurationSection category = this.plots.getConfigurationSection("plots");
	category.set(name, null);
	savePlotsFile();
	return true;
    }

    public boolean removeProtectiveFromDatabase(String name) {
	this.protectivePlots.remove(name);
	ConfigurationSection category = this.plots.getConfigurationSection("protectivePlots");
	category.set(name, null);
	savePlotsFile();
	return true;
    }

    public Object[] getAllPlotNames() {
	if ((this.plots == null) || (this.plots.getConfigurationSection("plots") == null) || (this.plots.getConfigurationSection("plots").getKeys(false) == null)) {
	    return null;
	}
	return this.plots.getConfigurationSection("plots").getKeys(false).toArray();
    }

    public Object[] getAllProtectivePlotNames() {
	if ((this.plots == null) || (this.plots.getConfigurationSection("protectivePlots") == null) || (this.plots.getConfigurationSection("protectivePlots").getKeys(false) == null)) {
	    return null;
	}
	return this.plots.getConfigurationSection("protectivePlots").getKeys(false).toArray();
    }

    public FileConfiguration getPlotsConfig() {
	if (this.plots == null) {
	    loadPlotsFile();
	}
	return this.plots;
    }

    public Plot getPlotByName(String plotname) {
	return this.protectedPlots.get(plotname);
    }

    public PlotManager getPlotManager() {
	return this.plotManager;
    }

    public boolean isExistingProtective(String plotname) {
	return this.protectivePlots.get(plotname) != null;
    }

    public String isInsideProtectivePlot(Location location) {
	String plotname = null;
	Object[] plots = getAllProtectivePlotNames();
	if ((plots != null)
		&& (plots.length > 0)) {
	    int x = location.getBlockX();
	    int y = location.getBlockY();
	    int z = location.getBlockZ();
	    for (Object plot : plots) {
		ProtectivePlot currentPlot = this.protectivePlots.get(plot.toString());
		if ((x >= currentPlot.minX) && (x <= currentPlot.maxX)
			&& (y >= currentPlot.minY) && (y <= currentPlot.maxY)
			&& (z >= currentPlot.minZ) && (z <= currentPlot.maxZ)) {
		    plotname = plot.toString();
		    break;
		}
	    }

	}

	return plotname;
    }

    private void loadPlotsFile() {
	if (this.plotsFile == null) {
	    String path = "plugins" + File.separator + "PlotGuardian";
	    this.plotsFile = new File(path, "plots.yml");
	}
	this.plots = YamlConfiguration.loadConfiguration(this.plotsFile);
    }

    private boolean loadPlotsFromFile() {
	Object[] keys = getAllPlotNames();
	if ((keys != null)
		&& (keys.length > 0)) {
	    ConfigurationSection category = this.plots.getConfigurationSection("plots");
	    for (Object key : keys) {
		ConfigurationSection Sender = category.getConfigurationSection(key.toString());
		String world = Sender.getString("world");
		String[] min = Sender.getString("min").split(", ");
		String[] max = Sender.getString("max").split(", ");
		String owner = Sender.getString("owner");
		String members = Sender.getString("members");
		int price = Sender.getInt("price");
		try {
		    Plot newPlot = new Plot(world, owner, members, Integer.parseInt(min[0]), Integer.parseInt(min[1]), Integer.parseInt(min[2]), Integer.parseInt(max[0]), Integer.parseInt(max[1]), Integer.parseInt(max[2]), price);
		    this.protectedPlots.put(key.toString(), newPlot);
		} catch (NumberFormatException ex) {
		    this.guardian.sendLog("Failed to load " + key.toString() + "!");
		    break;
		}
	    }
	}

	keys = getAllProtectivePlotNames();
	if ((keys != null)
		&& (keys.length > 0)) {
	    ConfigurationSection category = this.plots.getConfigurationSection("protectivePlots");
	    for (Object key : keys) {
		ConfigurationSection Sender = category.getConfigurationSection(key.toString());
		String world = Sender.getString("world");
		String[] min = Sender.getString("min").split(", ");
		String[] max = Sender.getString("max").split(", ");
		try {
		    ProtectivePlot plot = new ProtectivePlot(world, Integer.parseInt(min[0]), Integer.parseInt(min[1]), Integer.parseInt(min[2]), Integer.parseInt(max[0]), Integer.parseInt(max[1]), Integer.parseInt(max[2]));
		    this.protectivePlots.put(key.toString(), plot);
		} catch (NumberFormatException ex) {
		    this.guardian.sendLog("Failed to load " + key.toString() + "!");
		    break;
		}
	    }
	}

	return true;
    }

    public void savePlotsFile() {
	if ((this.plots == null) || (this.plotsFile == null)) {
	    return;
	}
	try {
	    getPlotsConfig().save(this.plotsFile);
	} catch (IOException ex) {
	    this.guardian.sendLog("Could not save plotsFile to " + this.plotsFile);
	}
    }

    public boolean savePlotToFile(String plotName) {
	Plot plot = getPlotByName(plotName);
	ConfigurationSection category;
	if (this.plots.isConfigurationSection("plots")) {
	    category = this.plots.getConfigurationSection("plots");
	} else {
	    category = this.plots.createSection("plots");
	}
	ConfigurationSection Sender;
	if (category.contains(plotName)) {
	    Sender = category.getConfigurationSection(plotName);
	} else {
	    Sender = category.createSection(plotName);
	}
	Sender.set("world", plot.world);
	Sender.set("min", plot.minX + ", " + plot.minY + ", " + plot.minZ);
	Sender.set("max", plot.maxX + ", " + plot.maxY + ", " + plot.maxZ);
	Sender.set("owner", plot.owner);
	String members = plot.members;
	if ((members != null) && (members.length() <= 0)) {
	    members = null;
	}
	Sender.set("members", members);
	Sender.set("price", Integer.valueOf(plot.price));
	savePlotsFile();
	return true;
    }

    public boolean saveProtectivePlotToFile(String plotName) {
	ProtectivePlot plot = this.protectivePlots.get(plotName);
	ConfigurationSection category;
	if (this.plots.isConfigurationSection("protectivePlots")) {
	    category = this.plots.getConfigurationSection("protectivePlots");
	} else {
	    category = this.plots.createSection("protectivePlots");
	}
	ConfigurationSection Sender;
	if (category.contains(plotName)) {
	    Sender = category.getConfigurationSection(plotName);
	} else {
	    Sender = category.createSection(plotName);
	}
	Sender.set("world", plot.world);
	Sender.set("min", plot.minX + ", " + plot.minY + ", " + plot.minZ);
	Sender.set("max", plot.maxX + ", " + plot.maxY + ", " + plot.maxZ);
	savePlotsFile();
	return true;
    }
}
