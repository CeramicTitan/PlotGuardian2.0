package java.me.mshax085.guardian.protection;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;

public class PlotManager {

    private final PlotDatabase plotDatabase;

    public PlotManager(PlotDatabase pd) {
	this.plotDatabase = pd;
    }

    public boolean addPlotForSale(String plot, int price) {
	Plot p = this.plotDatabase.getPlotByName(plot);
	p.price = price;
	this.plotDatabase.savePlotToFile(plot);
	return true;
    }

    public boolean addPlotMember(String plot, String member) {
	Plot p = this.plotDatabase.getPlotByName(plot);
	String members = p.members;
	if (members == null) {
	    members = "";
	}
	p.members = (members + member + ",");
	this.plotDatabase.savePlotToFile(plot);
	return true;
    }

    public int getPlotAmountOwnedByPlayer(String owner) {
	int plots;
	try {
	    return plots = getPlotsByOwner(owner).size();
	} catch (NullPointerException ex) {
	    return 0;
	}
    }

    public int getPlotMembers(String plotname) {
	return StringUtils.countMatches(
		this.plotDatabase.getPlotByName(plotname).members, ",");
    }

    public int getPrice(String plotname) {
	return this.plotDatabase.getPlotByName(plotname).price;
    }

    public String getPlotOwner(String plotname) {
	return this.plotDatabase.getPlotByName(plotname).owner;
    }

    // TODO - Use linked list for iterating plot names
    // TODO - Use HashMap to store players linked list of plot names?
    public ArrayList<String> getPlotsByOwner(String owner) {
	ArrayList<String> plots = null;
	Object[] plotNames = this.plotDatabase.getAllPlotNames();
	if (plotNames != null) {
	    for (Object plotName : plotNames) {
		if (this.plotDatabase.getPlotByName(plotName.toString()).owner.equalsIgnoreCase(owner)) {
		    if (plots == null) {
			plots = new ArrayList<String>();
		    }
		    plots.add(plotName.toString());
		}
	    }
	}
	return plots;
    }

    public boolean isExistingPlot(String plotname) {
	return this.plotDatabase.getPlotByName(plotname) != null;
    }

    public boolean isFreePlot(String plotname) {
	return this.plotDatabase.getPlotByName(plotname).owner
		.contains("noowner");
    }

    public boolean isMemberOfPlot(String plotname, String user) {
	String members = this.plotDatabase.getPlotByName(plotname).members;
	if (members == null) {
	    return false;
	}
	return members.contains(user);
    }

    public boolean isOwnerOfPlot(String plotname, String user) {
	Plot plot = this.plotDatabase.getPlotByName(plotname);
	if (plot == null) {
	    return false;
	}
	String owner = plot.owner;
	return owner.contains(user);
    }

    public boolean isPlotForSale(String plotname) {
	return this.plotDatabase.getPlotByName(plotname).price > -1;
    }

    public boolean isSameWorld(String plotname, String world) {
	return this.plotDatabase.getPlotByName(plotname).world
		.equalsIgnoreCase(world);
    }

    public String isInsidePlot(Location location) {
	String plotname = null;
	Object[] plots = this.plotDatabase.getAllPlotNames();
	if ((plots != null) && (plots.length > 0)) {
	    int x = location.getBlockX();
	    int y = location.getBlockY();
	    int z = location.getBlockZ();
	    for (Object plot : plots) {
		Plot currentPlot = this.plotDatabase.getPlotByName(plot.toString());
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

    public boolean removeMember(String plot, String member) {
	Plot p = this.plotDatabase.getPlotByName(plot);
	if (member != null) {
	    String members = p.members.replace(member + ",", "");
	    p.members = members;
	} else {
	    p.members = null;
	}
	this.plotDatabase.savePlotToFile(plot);
	return true;
    }

    public boolean removePlotFromSale(String plot) {
	Plot p = this.plotDatabase.getPlotByName(plot);
	p.price = -1;
	this.plotDatabase.savePlotToFile(plot);
	return true;
    }

    public boolean setOwner(String plot, String owner) {
	this.plotDatabase.getPlotByName(plot).owner = owner;
	this.plotDatabase.savePlotToFile(plot);
	return true;
    }
}
