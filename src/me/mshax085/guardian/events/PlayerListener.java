package me.mshax085.guardian.events;

import me.mshax085.guardian.PlotGuardian;
import me.mshax085.guardian.protection.Plot;
import me.mshax085.guardian.protection.PlotManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener {

    private final PlotGuardian guardian;

    public PlayerListener(PlotGuardian pg) {
	this.guardian = pg;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
	Block block = e.getClickedBlock();
	if (block != null) {
	    Player p = e.getPlayer();
	    Material itemInHand = p.getItemInHand().getType();
	    if ((this.guardian.disallowBlockInteractionsOutsidePlots)
		    && (!p.hasPermission("plotguardian.build.interactAny"))
		    && (this.guardian.disallowedInteractions.contains(Integer.valueOf(block.getTypeId())))) {
		PlotManager manager = this.guardian.getPlotDatabase().getPlotManager();
		String plot = manager.isInsidePlot(block.getLocation());
		if ((plot == null) || (!manager.getPlotOwner(plot).equalsIgnoreCase(p.getName()))) {
		    e.setUseItemInHand(Result.DENY);
		    e.setUseInteractedBlock(Result.DENY);
		    e.setCancelled(true);
		}

	    }

	    if (itemInHand.equals(Material.DIAMOND)) {
		if (p.hasPermission("plotguardian.diamondselector")) {
		    Action action = e.getAction();
		    Location blockLocation = block.getLocation();
		    if (action.equals(Action.LEFT_CLICK_BLOCK)) {
			e.setUseItemInHand(Result.DENY);
			e.setUseInteractedBlock(Result.DENY);
			e.setCancelled(true);
			this.guardian.getSelectionHandler().setFirstSelection(p.getName(), blockLocation);
			p.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] First selection equals (X: " + blockLocation.getBlockX() + ", Y: " + blockLocation.getBlockY() + ", Z: " + blockLocation.getBlockZ() + ")");
		    } else if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
			e.setUseItemInHand(Result.DENY);
			e.setUseInteractedBlock(Result.DENY);
			e.setCancelled(true);
			this.guardian.getSelectionHandler().setSecondSelection(p.getName(), blockLocation);
			p.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Second selection equals (X: " + blockLocation.getBlockX() + ", Y: " + blockLocation.getBlockY() + ", Z: " + blockLocation.getBlockZ() + ")");
		    }
		} else {
		    p.sendMessage(ChatColor.RED + "[PlotGuardian] You are lacking permission: 'plotguardian.diamondselector'");
		}
	    } else if (itemInHand.equals(Material.BLAZE_ROD)) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
		    if (p.hasPermission("plotguardian.blazerod")) {
			e.setUseItemInHand(Result.DENY);
			e.setUseInteractedBlock(Result.DENY);
			e.setCancelled(true);
			PlotManager manager = this.guardian.getPlotDatabase().getPlotManager();
			String plot = manager.isInsidePlot(block.getLocation());
			if (plot != null) {
			    p.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Found " + plot + " owned by " + manager.getPlotOwner(plot));
			} else {
			    p.sendMessage(ChatColor.RED + "[PlotGuardian] Could not find any plot at this location!");
			}
		    } else {
			p.sendMessage(ChatColor.RED + "[PlotGuardian] You are lacking permission: 'plotguardian.blazerod'");
		    }
		}
		e.getClickedBlock().isLiquid();
	    } else if ((isLiquid(itemInHand))
		    && (!p.hasPermission("plotguardian.build.anywhere")) && ((e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) || (e.getAction().equals(Action.RIGHT_CLICK_AIR)))) {
		PlotManager manager = this.guardian.getPlotDatabase().getPlotManager();
		String plot = manager.isInsidePlot(block.getLocation());
		if (plot != null) {
		    if ((!manager.isMemberOfPlot(plot, p.getName())) && (!manager.isOwnerOfPlot(plot, p.getName()))) {
			p.sendMessage(ChatColor.RED + "[PlotGuardian] You cannot build at this location!");
			e.setUseItemInHand(Result.DENY);
			e.setUseInteractedBlock(Result.DENY);
			e.setCancelled(true);
		    }
		} else if (((this.guardian.protectAreaOutsidePlots) && (!isWorldExempt(p.getWorld().getName()))) || (this.guardian.getPlotDatabase().isInsideProtectivePlot(block.getLocation()) != null)) {
		    p.sendMessage(ChatColor.RED + "[PlotGuardian] You cannot build at this location!");
		    e.setUseItemInHand(Result.DENY);
		    e.setUseInteractedBlock(Result.DENY);
		    e.setCancelled(true);
		}
	    }
	}
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent e) {
	if (e.getLine(0).equalsIgnoreCase("[plot]")) {
	    if (this.guardian.useEconomy) {
		if ((e.getLine(1).contains("$")) && (e.getLine(1).length() > 0)) {
		    int price = 0;
		    try {
			price = Integer.parseInt(e.getLine(1).replace("$", ""));
		    } catch (NumberFormatException ex) {
			e.setLine(1, "Invalid Sign");
			e.setLine(2, "");
			e.setLine(3, "");
			return;
		    }
		    if (price <= 0) {
			e.setLine(1, "Invalid Sign");
			e.setLine(2, "");
			e.setLine(3, "");
			return;
		    }
		    PlotManager pm = this.guardian.getPlotDatabase().getPlotManager();
		    String plot = pm.isInsidePlot(e.getBlock().getLocation());
		    if (plot != null) {
			if (pm.isOwnerOfPlot(plot, e.getPlayer().getName())) {
			    pm.removeMember(plot, null);
			    pm.addPlotForSale(plot, price);
			    String playername = e.getPlayer().getName();
			    if (playername.length() > 15) {
				playername.substring(0, 14);
			    }
			    e.setLine(2, playername);
			    Plot soldPlot = this.guardian.getPlotDatabase().getPlotByName(plot);
			    String plotSize = soldPlot.maxX - soldPlot.minX + ", " + (soldPlot.maxY - soldPlot.minY) + ", " + (soldPlot.maxZ - soldPlot.minZ);
			    if (plotSize.length() > 15) {
				plotSize = "Too big values";
			    }
			    e.setLine(3, plotSize);
			    e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Your plot has been put to sale for " + price + " dollars!");
			    e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] You will gain the money when a user decides to buy the plot.");
			} else {
			    e.getPlayer().sendMessage(ChatColor.RED + "[PlotGuardian] You do not own this plot!");
			    e.setLine(1, "Invalid Sign");
			    e.setLine(2, "");
			    e.setLine(3, "");
			}
		    } else {
			e.setLine(1, "Invalid Sign");
			e.setLine(2, "");
			e.setLine(3, "");
		    }
		} else {
		    e.setLine(1, "Invalid Sign");
		    e.setLine(2, "");
		    e.setLine(3, "");
		}
	    } else {
		e.setLine(1, "Invalid Sign");
		e.setLine(2, "");
		e.setLine(3, "");
		e.getPlayer().sendMessage(ChatColor.RED + "[PlotGuardian] Economy is disabled by the admins!");
	    }
	}
    }

    private boolean isWorldExempt(String world) {
	String worldName = world.toLowerCase();
	String exempt = this.guardian.exemptWorldFromProtectionOutsidePlots;
	if ((exempt != null) && (exempt.contains(","))) {
	    String[] exemptions = this.guardian.exemptWorldFromProtectionOutsidePlots.toLowerCase().split(",");
	    for (String exemption : exemptions) {
		if (exemption.equals(worldName)) {
		    return true;
		}
	    }
	}
	return false;
    }

    public boolean isLiquid(Material material) {
	if (material.equals(Material.LAVA)) {
	    return true;
	}
	if (material.equals(Material.LAVA_BUCKET)) {
	    return true;
	}
	if (material.equals(Material.WATER)) {
	    return true;
	}
	if (material.equals(Material.WATER_BUCKET)) {
	    return true;
	}
	if (material.equals(Material.BUCKET)) {
	    return true;
	}
	return false;
    }
}
