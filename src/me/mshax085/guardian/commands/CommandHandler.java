package me.mshax085.guardian.commands;

import java.util.ArrayList;
import me.mshax085.guardian.PlotGuardian;
import me.mshax085.guardian.events.SelectionHandler;
import me.mshax085.guardian.protection.Plot;
import me.mshax085.guardian.protection.PlotDatabase;
import me.mshax085.guardian.protection.PlotManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    private final PlotGuardian guardian;

    public CommandHandler(PlotGuardian g) {
	this.guardian = g;
    }

    public boolean onCommand(CommandSender Sender, Command cmd, String label, String[] arg) {
	String command = cmd.getName();
	if (command.equalsIgnoreCase("plot")) {
	    if (arg.length > 0) {
		if (arg[0].equalsIgnoreCase("create")) {
		    if (!(Sender instanceof Player)) {
			Sender.sendMessage(ChatColor.RED + "[PlotGuardian] This command may only be used by players!");
			return true;
		    }
		    if (Sender.hasPermission("plotguardian.create")) {
			if ((arg.length == 2) || (arg.length == 4)) {
			    SelectionHandler selection = this.guardian.getSelectionHandler();
			    if (selection.isFirstSelectionSet(Sender.getName())) {
				if (selection.isSecondSelectionSet(Sender.getName())) {
				    PlotDatabase pd = this.guardian.getPlotDatabase();
				    if (!pd.getPlotManager().isExistingPlot(arg[1])) {
					Location minimumSelection = selection.getMinimumSelection(Sender.getName());
					Location maximumSelection = selection.getMaximumSelection(Sender.getName());
					if (arg.length == 4) {
					    try {
						double minY = Double.parseDouble(arg[2]);
						double maxY = Double.parseDouble(arg[3]);
						if ((minY <= -1.0D) || (maxY >= 256.0D)) {
							Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Only values between 0 and 255 is valid!");
						    return true;
						}
						if (maxY < minY) {
							Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Max Y-position is lower than minium Y-position!");
						    return true;
						}
						minimumSelection.setY(minY);
						maximumSelection.setY(maxY);
					    } catch (NumberFormatException ex) {
					    	Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Invalid Y-positions supplied!");
						return true;
					    }
					}
					if (pd.addPlotToDatabase(minimumSelection.getWorld().getName(), arg[1], "noowner", minimumSelection, maximumSelection, -1)) {
					    Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Plot " + arg[1] + " added to database!");
					    Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Minimum Location: (" + minimumSelection.getBlockX() + "," + minimumSelection.getBlockY() + "," + minimumSelection.getBlockZ() + ") (X,Y,Z)");
					    Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Maximum Location: (" + maximumSelection.getBlockX() + "," + maximumSelection.getBlockY() + "," + maximumSelection.getBlockZ() + ") (X,Y,Z)");
					} else {
					    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Failed to save " + arg[1] + " to database!");
					}
				    } else {
					Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " already exist!");
				    }
				} else {
				    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Use the diamondselector to select the second location!");
				}
			    } else {
				Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Use the diamondselector to select the first location!");
			    }
			} else {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Syntax: /plot create <plotname> [min y] [max y]");
			}
		    } else {
			Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You are lacking permission: 'plotguardian.create'");
		    }
		} else if (arg[0].equalsIgnoreCase("protect")) {
		    if (!(Sender instanceof Player)) {
			Sender.sendMessage(ChatColor.RED + "[PlotGuardian] This command may only be used by players!");
			return true;
		    }
		    if (Sender.hasPermission("plotguardian.create")) {
			if ((arg.length == 2) || (arg.length == 4)) {
			    SelectionHandler selection = this.guardian.getSelectionHandler();
			    if (selection.isFirstSelectionSet(Sender.getName())) {
				if (selection.isSecondSelectionSet(Sender.getName())) {
				    PlotDatabase pd = this.guardian.getPlotDatabase();
				    if (!pd.isExistingProtective(arg[1])) {
					Location minimumSelection = selection.getMinimumSelection(Sender.getName());
					Location maximumSelection = selection.getMaximumSelection(Sender.getName());
					if (arg.length == 4) {
					    try {
						double minY = Double.parseDouble(arg[2]);
						double maxY = Double.parseDouble(arg[3]);
						if ((minY <= -1.0D) || (maxY >= 256.0D)) {
						    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Only values between 0 and 255 is valid!");
						    return true;
						}
						if (maxY < minY) {
						    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Max Y-position is lower than minium Y-position!");
						    return true;
						}
						minimumSelection.setY(minY);
						maximumSelection.setY(maxY);
					    } catch (NumberFormatException ex) {
						Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Invalid Y-positions supplied!");
						return true;
					    }
					}
					if (pd.addProtectivePlotToDatabase(minimumSelection.getWorld().getName(), arg[1], minimumSelection, maximumSelection)) {
					    Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Protective " + arg[1] + " added to database!");
					    Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Minimum Location: (" + minimumSelection.getBlockX() + "," + minimumSelection.getBlockY() + "," + minimumSelection.getBlockZ() + ") (X,Y,Z)");
					    Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Maximum Location: (" + maximumSelection.getBlockX() + "," + maximumSelection.getBlockY() + "," + maximumSelection.getBlockZ() + ") (X,Y,Z)");
					} else {
					    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Failed to save Protective " + arg[1] + " to database!");
					}
				    } else {
					Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Protective " + arg[1] + " already exist!");
				    }
				} else {
				    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Use the diamondselector to select the second location!");
				}
			    } else {
				Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Use the diamondselector to select the first location!");
			    }
			} else {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Syntax: /plot protect <plotname> [min y] [max y]");
			}
		    } else {
			Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You are lacking permission: 'plotguardian.create'");
		    }
		} else if (arg[0].equalsIgnoreCase("deprotect")) {
		    if (Sender.hasPermission("plotguardian.delete")) {
			if (arg.length == 2) {
			    PlotDatabase pd = this.guardian.getPlotDatabase();
			    if (pd.isExistingProtective(arg[1])) {
				pd.removeProtectiveFromDatabase(arg[1]);
				Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Successfully deleted protective " + arg[1] + "!");
			    } else {
				Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Could not find any protective plot by name: " + arg[1] + "!");
			    }
			} else {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Syntax: /plot deprotect <protectiveplotname>");
			}
		    } else {
			Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You are lacking permission: 'plotguardian.delete'");
		    }
		} else if (arg[0].equalsIgnoreCase("delete")) {
		    if (Sender.hasPermission("plotguardian.delete")) {
			if (arg.length == 2) {
			    PlotDatabase pd = this.guardian.getPlotDatabase();
			    if (pd.getPlotManager().isExistingPlot(arg[1])) {
				pd.removePlotFromDatabase(arg[1]);
				Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Successfully deleted " + arg[1] + "!");
			    } else {
				Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Could not find any plot by name: " + arg[1] + "!");
			    }
			} else {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Syntax: /plot delete <plotname>");
			}
		    } else {
			Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You are lacking permission: 'plotguardian.delete'");
		    }
		} else if (arg[0].equalsIgnoreCase("forsale")) {
		    if (this.guardian.useEconomy) {
			if (Sender.hasPermission("plotguardian.sale")) {
			    if (arg.length == 3) {
				if (arg[2].length() < 15) {
				    int price = -1;
				    try {
					price = Integer.parseInt(arg[2]);
				    } catch (NumberFormatException ex) {
					Sender.sendMessage(ChatColor.RED + "[PlotGuardian] The price may only contain numbers!");
					return true;
				    }
				    if (price > 0) {
					PlotManager pm = this.guardian.getPlotDatabase().getPlotManager();
					if (pm.isExistingPlot(arg[1])) {
					    if (pm.isFreePlot(arg[1])) {
						if (!pm.isPlotForSale(arg[1])) {
						    PlotDatabase pd = this.guardian.getPlotDatabase();
						    Plot plot = pd.getPlotByName(arg[1]);
						    int middleX = plot.minX + (plot.maxX - plot.minX) / 2;
						    int middleZ = plot.minZ + (plot.maxZ - plot.minZ) / 2;
						    World world = Sender.getServer().getWorld(plot.world);
						    int y = world.getHighestBlockYAt(middleX, middleZ);
						    if ((y + 1 >= plot.minY) && (y + 1 <= plot.maxY)) {
							world.getBlockAt(middleX, y, middleZ).setType(Material.FENCE);
							Block sign = world.getBlockAt(middleX, y + 1, middleZ);
							sign.setType(Material.SIGN_POST);
							if ((sign.getState() instanceof Sign)) {
							    Sign signPost = (Sign) sign.getState();
							    signPost.setLine(0, "[Plot]");
							    signPost.setLine(1, "$" + price);
							    signPost.setLine(2, "noowner");
							    String plotSize = plot.maxX - plot.minX + ", " + (plot.maxY - plot.minY) + ", " + (plot.maxZ - plot.minZ);
							    if (plotSize.length() > 15) {
								plotSize = "Too big values";
							    }
							    signPost.setLine(3, plotSize);
							    signPost.update();
							}
							pm.addPlotForSale(arg[1], price);
							Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] " + arg[1] + " is now for sale for " + price + " dollars!");
						    } else {
							Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Could not generate sign at plots middleposition!");
							Sender.sendMessage(ChatColor.RED + "[PlotGuardian] The ground is either too low or too high!");
						    }
						} else {
						    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " is already for sale!");
						}
					    } else {
						Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " is occupied!");
					    }
					} else {
					    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " does not exist!");
					}
				    } else {
					Sender.sendMessage(ChatColor.RED + "[PlotGuardian] The price must be greater than 0!");
				    }
				} else {
				    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] The max length for the price is 14 characters!");
				}
			    } else {
				Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Syntax: /plot forsale <plotname> <price>");
			    }
			} else {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You are lacking permission: 'plotguardian.sale'");
			}
		    } else {
			Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Economy has to be enabled in the config!");
		    }
		} else if (arg[0].equalsIgnoreCase("offsale")) {
		    if (this.guardian.useEconomy) {
			if (Sender.hasPermission("plotguardian.sale")) {
			    if (arg.length == 2) {
				PlotManager pm = this.guardian.getPlotDatabase().getPlotManager();
				if (pm.isExistingPlot(arg[1])) {
				    if (pm.isFreePlot(arg[1])) {
					if (pm.isPlotForSale(arg[1])) {
					    pm.removePlotFromSale(arg[1]);
					    Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] " + arg[1] + " is no longer for sale!");
					} else {
					    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " is not for sale!");
					}
				    } else {
					Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " is occupied!");
				    }
				} else {
				    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " does not exist!");
				}
			    } else {
				Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Syntax: /plot offsale <plotname>");
			    }
			} else {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You are lacking permission: 'plotguardian.sale'");
			}
		    } else {
			Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Economy has to be enabled in the config!");
		    }
		} else if (arg[0].equalsIgnoreCase("clear")) {
		    if (Sender.hasPermission("plotguardian.clear")) {
			if (arg.length == 2) {
			    PlotManager pm = this.guardian.getPlotDatabase().getPlotManager();
			    if (pm.isExistingPlot(arg[1])) {
				pm.setOwner(arg[1], "noowner");
				pm.removeMember(arg[1], null);
				Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] All values reset for " + arg[1] + "!");
			    } else {
				Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Could not find any plot by name: " + arg[1] + "!");
			    }
			} else {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Syntax: /plot clear <plotname>");
			}
		    } else {
			Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You are lacking permission: 'plotguardian.clear'");
		    }
		} else if (arg[0].equalsIgnoreCase("claim"))
			 //plotsCanClaim = GetNumPlotsCanClaim(cs);
		{
		    if (Sender.hasPermission("plotguardian.claim")) {
			if (!(Sender instanceof Player)) {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] This command may only be used by players!");
			    return true;
			}
			if (arg.length == 2) {
			    PlotManager pm = this.guardian.getPlotDatabase().getPlotManager();
			    if (pm.isExistingPlot(arg[1])) {
				if (pm.isFreePlot(arg[1])) {
				    if (!pm.isPlotForSale(arg[1])) {
					if (pm.getPlotAmountOwnedByPlayer(Sender.getName()) < PlotGuardian.GetNumPlotsCanClaim(Sender)) {
					    pm.setOwner(arg[1], Sender.getName());
					    Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] You are now the owner of " + arg[1] + "!");
					} else {
					    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You may only claim " + PlotGuardian.GetNumPlotsCanClaim(Sender) + " plots!");
					}
				    } else {
					Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " is for sale and may not be claimed!");
				    }
				} else {
				    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " is occupied!");
				}
			    } else {
				Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Could not find any plot by name: " + arg[1]);
			    }
			} else {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Syntax: /plot claim <plotname>");
			}
		    } else {
			Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You don't have enough permission!");
		    }
		} else if (arg[0].equalsIgnoreCase("declaim")) {
		    if (Sender.hasPermission("plotguardian.declaim")) {
			if (!(Sender instanceof Player)) {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] This command may only be used by players!");
			    return true;
			}
			if (this.guardian.canDeclaim) {
			    if (arg.length == 2) {
				PlotManager pm = this.guardian.getPlotDatabase().getPlotManager();
				if (pm.isExistingPlot(arg[1])) {
				    if (pm.isOwnerOfPlot(arg[1], Sender.getName())) {
					pm.setOwner(arg[1], "noowner");
					pm.removeMember(arg[1], null);
					Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] " + arg[1] + " was unclaimed!");
				    } else {
					Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You do not own " + arg[1] + "!");
				    }
				} else {
				    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Could not find any plot by name: " + arg[1]);
				}
			    } else if (arg.length == 1) {
				PlotManager pm = this.guardian.getPlotDatabase().getPlotManager();
				int plots = pm.getPlotAmountOwnedByPlayer(Sender.getName());
				if (plots > 1) {
				    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You own more than one plot, please use /plot declaim <plotname> to declaim a specific!");
				    return true;
				}
				if (plots == 0) {
				    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Could not find any plot owned by you!");
				    return true;
				}
				String plot = pm.getPlotsByOwner(Sender.getName()).get(0);
				pm.setOwner(plot, "noowner");
				pm.removeMember(plot, null);
				Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] " + plot + " was unclaimed!");
			    } else {
				Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Syntax: /plot declaim");
			    }
			} else {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Plot declaiming has been disabled by the admins!");
			}
		    } else {
			Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You don't have enough permission!");
		    }
		} else if (arg[0].equalsIgnoreCase("addmember")) {
		    if (Sender.hasPermission("plotguardian.addmember")) {
			if (!(Sender instanceof Player)) {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] This command may only be used by players!");
			    return true;
			}
			if (this.guardian.canAddRemoveUsers) {
			    if (arg.length == 2) {
				if ((Sender.getServer().getPlayer(arg[1]) != null) && (Sender.getServer().getPlayer(arg[1]).isOnline())) {
				    PlotManager pm = this.guardian.getPlotDatabase().getPlotManager();
				    int plots = pm.getPlotAmountOwnedByPlayer(Sender.getName());
				    if (plots == 0) {
					Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You do not own any plot!");
					return true;
				    }
				    if (plots > 1) {
					Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You own more than one plot, please use /plot addmember <playername> <plotname>!");
					return true;
				    }
				    String plot = pm.getPlotsByOwner(Sender.getName()).get(0);
				    if ((!pm.isMemberOfPlot(plot, arg[1])) && (!Sender.getName().equalsIgnoreCase(arg[1]))) {
					if (pm.getPlotMembers(plot) < this.guardian.membersPerPlot) {
					    pm.addPlotMember(pm.getPlotsByOwner(Sender.getName()).get(0), arg[1]);
					    Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Member " + arg[1] + " was added to " + plot + "!");
					} else {
					    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You may only add " + this.guardian.membersPerPlot + " plot members!");
					}
				    } else {
					Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " is already a member " + plot + "!");
				    }
				} else {
				    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " is not online or could not be found!");
				}
			    } else if (arg.length == 3) {
				if ((Sender.getServer().getPlayer(arg[1]) != null) && (Sender.getServer().getPlayer(arg[1]).isOnline())) {
				    PlotManager pm = this.guardian.getPlotDatabase().getPlotManager();
				    if (pm.isOwnerOfPlot(arg[2], Sender.getName())) {
					if ((!pm.isMemberOfPlot(arg[2], arg[1])) && (!Sender.getName().equalsIgnoreCase(arg[1]))) {
					    if (pm.getPlotMembers(arg[2]) < this.guardian.membersPerPlot) {
						pm.addPlotMember(arg[2], arg[1]);
						Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Member " + arg[1] + " was added to " + arg[2] + "!");
					    } else {
						Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You may only add " + this.guardian.membersPerPlot + " plot members!");
					    }
					} else {
					    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " is already a member of " + arg[2] + "!");
					}
				    } else {
					Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You do not own " + arg[2] + "!");
				    }
				} else {
				    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " is not online or could not be found!");
				}
			    } else {
				Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Syntax: /plot addmember <playername>");
			    }
			} else {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Plot members has been disabled by the admins!");
			}
		    } else {
			Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You don't have enough permission!");
		    }
		} else if (arg[0].equalsIgnoreCase("removemember")) {
		    if (Sender.hasPermission("plotguardian.removemember")) {
			if (!(Sender instanceof Player)) {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] This command may only be used by players!");
			    return true;
			}
			if (this.guardian.canAddRemoveUsers) {
			    if (arg.length == 2) {
				PlotManager pm = this.guardian.getPlotDatabase().getPlotManager();
				int plots = pm.getPlotAmountOwnedByPlayer(Sender.getName());
				if (plots == 0) {
				    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You do not own any plot!");
				    return true;
				}
				if (plots > 1) {
				    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You own more than one plot, please use /plot removemember <playername> <plotname>!");
				    return true;
				}
				if (!Sender.getName().equalsIgnoreCase(arg[1])) {
				    String plot = pm.getPlotsByOwner(Sender.getName()).get(0);
				    if (pm.isMemberOfPlot(plot, arg[1])) {
					pm.removeMember(plot, arg[1]);
					Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Member " + arg[1] + " was removed from " + plot + "!");
				    } else {
					Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " is not a member of " + plot + "!");
				    }
				} else {
				    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You may not remove yourself!");
				}
			    } else if (arg.length == 3) {
				PlotManager pm = this.guardian.getPlotDatabase().getPlotManager();
				if (pm.isOwnerOfPlot(arg[2], Sender.getName())) {
				    if (!Sender.getName().equalsIgnoreCase(arg[1])) {
					if (pm.isMemberOfPlot(arg[2], arg[1])) {
					    pm.removeMember(arg[2], arg[1]);
					    Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Member " + arg[1] + " was removed from " + arg[2] + "!");
					} else {
					    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] " + arg[1] + " is not a member of " + arg[2] + "!");
					}
				    } else {
					Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You may not remove yourself!");
				    }
				} else {
				    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You do not own " + arg[2] + "!");
				}
			    } else {
				Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Syntax: /plot addmember <playername>");
			    }
			} else {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] Plot members has been disabled by the admins!");
			}
		    } else {
			Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You don't have enough permission!");
		    }
		} else if (arg[0].equalsIgnoreCase("version")) {
		    Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Plugin Version: " + this.guardian.getDescription().getVersion() + "!");
		} else if (arg[0].equalsIgnoreCase("list")) {
		    if (Sender.hasPermission("plotguardian.list")) {
			if (!(Sender instanceof Player)) {
			    Sender.sendMessage(ChatColor.RED + "[PlotGuardian] This command may only be used by players!");
			    return true;
			}
			ArrayList<String> plots = this.guardian.getPlotDatabase().getPlotManager().getPlotsByOwner(Sender.getName());
			String names = "No plots found ...";
			if ((plots != null) && (plots.size() > 0)) {
			    names = plots.toString().substring(1, plots.toString().length() - 1);
			}
			Sender.sendMessage(ChatColor.DARK_GREEN + "[PlotGuardian] Plots owned by your character: " + names);
		    } else {
			Sender.sendMessage(ChatColor.RED + "[PlotGuardian] You don't have enough permission!");
		    }
		} else {
		    sendCommandList(Sender);
		}
	    } else {
		sendCommandList(Sender);
	    }

	    return true;
	}
	return false;
    }

    private void sendCommandList(CommandSender Sender) {
	Sender.sendMessage(ChatColor.DARK_GREEN + "---------------" + ChatColor.GOLD + "PlotGuardian Commands" + ChatColor.DARK_GREEN + "-----------------");
	//Sender.sendMessage(ChatColor.DARK_GREEN + "/plot claim <plotname> " + ChatColor.GOLD + " - Claim a plot with name <plotname>");
	//Sender.sendMessage(ChatColor.DARK_GREEN + "/plot declaim <plotname> " + ChatColor.GOLD + " - Declaim a plot with name <plotname>");
	Sender.sendMessage(ChatColor.DARK_GREEN + "/plot addmember <name> " + ChatColor.GOLD + " - Add member <name> to your plot");
	Sender.sendMessage(ChatColor.DARK_GREEN + "/plot removemember <name> " + ChatColor.GOLD + " - Remove a member from plot");
	Sender.sendMessage(ChatColor.DARK_GREEN + "/plot list " + ChatColor.GOLD + " - List the plots owned by you");
	//Sender.sendMessage(ChatColor.DARK_GREEN + "/plot version " + ChatColor.GOLD + " - View the plugin version");
	Sender.sendMessage(ChatColor.DARK_GREEN + "/plot sales " + ChatColor.GOLD + " - Lists the Plots For Sale");
	Sender.sendMessage(ChatColor.DARK_GREEN + "/plot warp " + ChatColor.GOLD + " - Warp To a plot for sale or one you own");
	if ((!(Sender instanceof Player)) || (Sender.isOp())) {
	    Sender.sendMessage(ChatColor.DARK_GREEN + "/plot create <plotname> " + ChatColor.GOLD + " - Create a plot");
	    Sender.sendMessage(ChatColor.DARK_GREEN + "/plot delete <plotname> " + ChatColor.GOLD + " - Delete a plot");
	    Sender.sendMessage(ChatColor.DARK_GREEN + "/plot protect <plotname> " + ChatColor.GOLD + " - Create a protective plot");
	    Sender.sendMessage(ChatColor.DARK_GREEN + "/plot deprotect <plotname> " + ChatColor.GOLD + " - Delete a protective plot");
	    Sender.sendMessage(ChatColor.DARK_GREEN + "/plot forsale <plotname> <price> " + ChatColor.GOLD + " - Put a plot for sale");
	    Sender.sendMessage(ChatColor.DARK_GREEN + "/plot offsale <plotname> " + ChatColor.GOLD + " - Put a plot off sale");
	    Sender.sendMessage(ChatColor.DARK_GREEN + "/plot clear <plotname> " + ChatColor.GOLD + " - Reset members/owners of a plot");
	}
	String bottomPart = "--------------------------";
	String version = this.guardian.getDescription().getVersion();
	if (!this.guardian.isLatestVersion()) {
	    version = version + " (Update Available)";
	}
	bottomPart = bottomPart.substring(0, bottomPart.length() - version.length() / 2);
	Sender.sendMessage(ChatColor.DARK_GREEN + bottomPart + ChatColor.GOLD + version + ChatColor.DARK_GREEN + bottomPart);
    }
}