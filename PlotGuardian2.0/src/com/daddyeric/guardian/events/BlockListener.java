package com.daddyeric.guardian.events;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.daddyeric.guardian.PlotGuardian;
import com.daddyeric.guardian.protection.PlotManager;

public class BlockListener implements Listener {

   
	private List<String> list = null;
	
	private final PlotGuardian guardian;

    public BlockListener(PlotGuardian pg) {
	this.guardian = pg;
	list = pg.getBlacklist();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent  e) {
	Player p = e.getPlayer();
	if ((this.guardian.useEconomy)
		&& ((e.getBlock().getState() instanceof Sign))) {
	    Sign sign = (Sign) e.getBlock().getState();
	    if (sign.getLine(0).equalsIgnoreCase("[plot]")) {
		PlotManager manager = this.guardian.getPlotDatabase().getPlotManager();
		if (manager.getPlotAmountOwnedByPlayer(p.getName()) <= PlotGuardian.GetNumPlotsCanClaim(p)) {
		    String plotname = manager.isInsidePlot(e.getBlock().getLocation());
		    if (plotname != null) {
			if (manager.isPlotForSale(plotname)) {
			    if (PlotGuardian.economy.getBalance(p.getName()) >= manager.getPrice(plotname)) {
				PlotGuardian.economy.depositPlayer(manager.getPlotOwner(plotname), manager.getPrice(plotname));
				if ((p.getServer().getPlayer(manager.getPlotOwner(plotname)) != null) && (p.getServer().getPlayer(manager.getPlotOwner(plotname)).isOnline())) {
				    p.getServer().getPlayer(manager.getPlotOwner(plotname)).sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] " + p.getName() + " bought " + plotname + " for " + manager.getPrice(plotname) + " dollars!");
				}
				PlotGuardian.economy.withdrawPlayer(p.getName(), manager.getPrice(plotname));
				p.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] You bought " + plotname + " for " + manager.getPrice(plotname) + " dollars!");
				manager.removePlotFromSale(plotname);
				manager.setOwner(plotname, p.getName());
			    } else {
			    	
				p.sendMessage(ChatColor.RED + "[PlotGuardian] This plot costs " + manager.getPrice(plotname) + " dollars, you only have " + PlotGuardian.economy.getBalance(p.getName()) + " dollars!");
			    e.setCancelled(true);
			    }
			} else {
			    e.getBlock().setTypeId(0);
			    p.sendMessage(ChatColor.RED + "[PlotGuardian] This plot is not for sale!");
			    e.setCancelled(false);
			}
		    } else {
			e.getBlock().setTypeId(0);
			p.sendMessage(ChatColor.RED + "[PlotGuardian] Invalid sign!");
			e.setCancelled(true);
		    }
		} else {
			
		    p.sendMessage(ChatColor.RED + "[PlotGuardian] You may only claim " + PlotGuardian.GetNumPlotsCanClaim(p) + " plots!");
		e.setCancelled(true);
		}
		sign.update();
		e.setCancelled(true);
	    }
	}

	if ((!p.hasPermission("plotguardian.build.anywhere"))
		&& (!canBuild(p, e.getBlock().getLocation()))) {
	    p.sendMessage(ChatColor.RED + "[PlotGuardian] You cannot build at this location!");
	    e.setCancelled(true);
	}
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent e) {
	Player p = e.getPlayer();
	if ((!p.hasPermission("plotguardian.build.anywhere"))
		&& (!canBuild(p, e.getBlock().getLocation()))) {
	    p.sendMessage(ChatColor.RED + "[PlotGuardian] You cannot build at this location!");
	    e.setBuild(false);
	    e.setCancelled(true);
	}
    }
    //Chest Right Click
    @EventHandler (priority = EventPriority.HIGH)
    public void  onInteract(PlayerInteractEvent event){
    	
    	Player p = event.getPlayer();// we get the player
    	PlotManager manager = this.guardian.getPlotDatabase().getPlotManager();//We get the plot manager
    	if(manager.getAllPlots().size() == 0){//wont get called if not true
    		p.sendMessage(p.getName() + " has no plots!");
    		return;
    	}
    	for(String plots : manager.getAllPlots()){//we iterate over all that plots owned by player(our variable p).
    		
			
			
			if(list.contains(event.getClickedBlock().getType().toString()) && manager.isInsidePlot(event.getClickedBlock().getLocation()).equals(plots)){//checks if the block is a chest and checks if the chest is in the region
    			if(!(manager.isOwnerOfPlot(plots, p.getName()) || p.hasPermission("plotguardian.build.anywhere"))){ //checks if the player is the owner or if the player has the appropriate permissions.
    				event.setCancelled(true);//if not, it cancels the event so the chest doesn't open.
    				p.sendMessage("You cannot do that!");//if not, it sends the player a message.
    		}
    	}
    }
  } 
 /*   //Chest Right Click
    @EventHandler (priority = EventPriority.HIGH)
    public void  onInteract(PlayerInteractEvent event){
    	Player p = event.getPlayer();// we get the player
    	PlotManager manager = this.guardian.getPlotDatabase().getPlotManager();//We get the plot manager
    	if(manager.getAllPlots().size() == 0){//wont get called if not true
    		p.sendMessage(p.getName() + " has no plots!");
    		return;
    	}
    	for(String plots : manager.getAllPlots()){//we iterate over all that plots owned by player(our variable p).
    		
			if(event.getClickedBlock().getType() == Material.CHEST && manager.isInsidePlot(event.getClickedBlock().getLocation()).equals(plots)){//checks if the block is a chest and checks if the chest is in the region
    			if(!(manager.isOwnerOfPlot(plots, p.getName()) || p.hasPermission("plotguardian.build.anywhere"))){ //checks if the player is the owner or if the player has the appropriate permissions.
    				event.setCancelled(true);//if not, it cancels the event so the chest doesn't open.
    				p.sendMessage("You cannot do that!");//if not, it sends the player a message.
    		}
    	}
    }
  }*/ 
    private boolean canBuild(Player player, Location location) {
	PlotManager manager = this.guardian.getPlotDatabase().getPlotManager();
	String plot = manager.isInsidePlot(location);
	if (plot != null) {
	    if ((manager.isSameWorld(plot, player.getWorld().getName())) && ((manager.isMemberOfPlot(plot, player.getName())) || ((manager.isOwnerOfPlot(plot, player.getName())) && (!manager.isPlotForSale(plot))))) {
		return true;
	    }
	} else if (this.guardian.getPlotDatabase().isInsideProtectivePlot(location) != null) {
	    if (player.hasPermission("plotguardian.build.anywhere")) {
		return true;
	    }
	} else {
	    if (!this.guardian.protectAreaOutsidePlots) {
		return true;
	    }
	    String worldName = player.getWorld().getName().toLowerCase();
	    String exempt = this.guardian.exemptWorldFromProtectionOutsidePlots;
	    if ((exempt != null) && (exempt.contains(","))) {
		String[] exemptions = this.guardian.exemptWorldFromProtectionOutsidePlots.toLowerCase().split(",");
		for (int i = 0; i < exemptions.length; i++) {
		    if (exemptions[i].equals(worldName)) {
			return true;
		    }
		}
	    }
	}

	return false;
    }
}
