package java.me.mshax085.guardian.events;

import java.me.mshax085.guardian.PlotGuardian;
import java.me.mshax085.guardian.protection.PlotManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;


public static int GetNumPlotsCanClaim(CommandSender cs)
{
        int plotsCanClaim = 2;
        if (p.hasPermission("plotguardian.resident")) plotsCanClaim = 3;
        if (p.hasPermission("plotguardian.iron")) plotsCanClaim = 4;
        if (p.hasPermission("plotguardian.silver")) plotsCanClaim = 5;
        if (p.hasPermission("plotguardian.gold")) plotsCanClaim = 6;
        if (p.hasPermission("plotguardian.diamond")) plotsCanClaim = 7;
        if (p.hasPermission("plotguardian.wizard")) plotsCanClaim = 8;
        if (p.hasPermission("plotguardian.sorceror")) plotsCanClaim = 10;
        if (p.isOp()) plotsCanClaim = 999;

        return plotsCanClaim;
}

public class BlockListener implements Listener {

    private final PlotGuardian guardian;

    public BlockListener(PlotGuardian pg) {
	this.guardian = pg;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e) {
	Player p = e.getPlayer();
	if ((this.guardian.useEconomy)
		&& ((e.getBlock().getState() instanceof Sign))) {
	    Sign sign = (Sign) e.getBlock().getState();
	    if (sign.getLine(0).equalsIgnoreCase("[plot]")) {
		PlotManager manager = this.guardian.getPlotDatabase().getPlotManager();
		 // Verify player has a spot available...
        PlotManager pm = p.getPlotManager();
        int plotsCanClaim = guardian.GetNumPlotsCanClaim(p);
        int plotsOwned = pm.getPlotAmountOwnedByPlayer(p.getName());
        if (plotsOwned >= plotsCanClaim) {
                p.sendMessage(ChatColor.RED + "[PlotGuardian] You may only claim " + plotsCanClaim + " plots! You currently own " + plotsOwned + ".");
                return true;
        }
		if (manager.getPlotAmountOwnedByPlayer(p.getName()) < plotsCanClaim) {
		    String plotname = manager.isInsidePlot(e.getBlock().getLocation());
		    if (plotname != null) {
			if (manager.isPlotForSale(plotname)) {
			    if (this.guardian.economy.getBalance(p.getName()) >= manager.getPrice(plotname)) {
				this.guardian.economy.depositPlayer(manager.getPlotOwner(plotname), manager.getPrice(plotname));
				if ((p.getServer().getPlayer(manager.getPlotOwner(plotname)) != null) && (p.getServer().getPlayer(manager.getPlotOwner(plotname)).isOnline())) {
				    p.getServer().getPlayer(manager.getPlotOwner(plotname)).sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] " + p.getName() + " bought " + plotname + " for " + manager.getPrice(plotname) + " dollars!");
				}
				this.guardian.economy.withdrawPlayer(p.getName(), manager.getPrice(plotname));
				p.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] You bought " + plotname + " for " + manager.getPrice(plotname) + " dollars!");
				manager.removePlotFromSale(plotname);
				manager.setOwner(plotname, p.getName());
			    } else {
				p.sendMessage(ChatColor.RED + "[PlotGuardian] This plot costs " + manager.getPrice(plotname) + " dollars, you only have " + this.guardian.economy.getBalance(p.getName()) + " dollars!");
			    }
			} else {
			    e.getBlock().setTypeId(0);
			    p.sendMessage(ChatColor.RED + "[PlotGuardian] This plot is not for sale!");
			}
		    } else {
			e.getBlock().setTypeId(0);
			p.sendMessage(ChatColor.RED + "[PlotGuardian] Invalid sign!");
		    }
		} else {
		    p.sendMessage(ChatColor.RED + "[PlotGuardian] You may only claim " + plotsCanClaim + " plots!");
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
