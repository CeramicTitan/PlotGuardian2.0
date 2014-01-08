package java.me.mshax085.guardian;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import java.me.mshax085.guardian.commands.CommandHandler;
import java.me.mshax085.guardian.events.BlockListener;
import java.me.mshax085.guardian.events.PlayerListener;
import java.me.mshax085.guardian.events.SelectionHandler;
import java.me.mshax085.guardian.protection.PlotDatabase;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PlotGuardian extends JavaPlugin implements Runnable {

    private static final Logger log = Logger.getLogger("Minecraft");
    private final PlotDatabase plotDatabase = new PlotDatabase(this);
    private boolean latestVersion = true;
    private SelectionHandler selectionHandler;
    private FileConfiguration config;
    private File configFile;
    public Economy economy;
    public boolean checkForUpdates;
    public boolean useEconomy;
    public boolean canDeclaim;
    public boolean canAddRemoveUsers;
    public boolean protectAreaOutsidePlots;
    public String exemptWorldFromProtectionOutsidePlots = "exampleworld1,exampleworld2";
    public boolean disallowBlockInteractionsOutsidePlots;
    public List<Integer> disallowedInteractions;
    public int membersPerPlot;

    
    public static int GetNumPlotsCanClaim(p)
    {
            int plotsCanClaim = 2;
            if (p.hasPermission("plotguardian.resident")) plotsCanClaim = 3;
            if (p.hasPermission("plotguardian.iron")) plotsCanClaim = 3;
            if (p.hasPermission("plotguardian.silver")) plotsCanClaim = 3;
            if (p.hasPermission("plotguardian.gold")) plotsCanClaim = 4;
            if (p.hasPermission("plotguardian.diamond")) plotsCanClaim = 5;
            if (p.hasPermission("plotguardian.wizard")) plotsCanClaim = 6;
            if (p.hasPermission("plotguardian.sorceror")) plotsCanClaim = 7;
            if (p.isOp()) plotsCanClaim = 999;

            return plotsCanClaim;
    }

    @Override
    public void onEnable() {
	loadConfigFile();
	writeToConfig(false);
	loadConfigContent();
	if (this.useEconomy) {
	    sendLog("Loading Vault ...");
	    if (!setupEconomy()) {
		log.warning("[PlotGuardian " + getDescription().getVersion()
			+ "] Could not find any Vault dependency!");
		log.warning("[PlotGuardian " + getDescription().getVersion()
			+ "] Economy hook will remain disabled!");
		this.useEconomy = false;
	    } else {
		sendLog("Found Vault dependency!");
		sendLog("Economy hook enabled!");
	    }
	}
	PluginManager pm = getServer().getPluginManager();
	pm.registerEvents(new BlockListener(this), this);
	pm.registerEvents(new PlayerListener(this), this);
	getCommand("plot").setExecutor(new CommandHandler(this));
	sendLog("Plugin enabled!");
	if (this.checkForUpdates) {
	    new Thread(this).start();
	}
	try {
	    MetricsLite metrics = new MetricsLite(this);
	    metrics.start();
	} catch (IOException e) {
	}
    }

    @Override
    public void onDisable() {
	saveConfigFile();
	sendLog("Plugin disabled!");
    }

    public FileConfiguration getConfiguration() {
	if (this.config == null) {
	    loadConfigFile();
	}
	return this.config;
    }

    public PlotDatabase getPlotDatabase() {
	return this.plotDatabase;
    }

    public SelectionHandler getSelectionHandler() {
	if (this.selectionHandler == null) {
	    this.selectionHandler = new SelectionHandler();
	}
	return this.selectionHandler;
    }

    private void loadConfigContent() {
	if (this.config != null) {
	    this.checkForUpdates = this.config.getBoolean("checkForUpdates");
	    this.useEconomy = this.config.getBoolean("useEconomy");
	    this.canDeclaim = this.config.getBoolean("canDeclaim");
	    this.canAddRemoveUsers = this.config
		    .getBoolean("canAddRemoveUsers");
	    this.protectAreaOutsidePlots = this.config
		    .getBoolean("protectAreaOutsidePlots");
	    this.exemptWorldFromProtectionOutsidePlots = this.config
		    .getString("exemptWorldFromProtectionOutsidePlots");
	    this.disallowBlockInteractionsOutsidePlots = this.config
		    .getBoolean("disallowBlockInteractionsOutsidePlots");
	    this.disallowedInteractions = this.config
		    .getIntegerList("disallowedInteractionsWithBlockIds");
	    this.membersPerPlot = this.config.getInt("membersPerPlot");
//	    this.claimablePlotsPerUser = this.config
//		    .getInt("claimablePlotsPerUser");
	} else {
	    loadConfigFile();
	}
    }

    public boolean isLatestVersion() {
	return this.latestVersion;
    }

    private void loadConfigFile() {
	if (this.configFile == null) {
	    String path = "plugins" + File.separator + "PlotGuardian";
	    this.configFile = new File(path, "config.yml");
	}
	this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public void sendLog(String message) {
	String msg = "[PlotGuardian " + getDescription().getVersion() + "] "
		+ message;
	log.info(msg);
    }

    public void saveConfigFile() {
	if ((this.config == null) || (this.configFile == null)) {
	    return;
	}
	try {
	    getConfiguration().save(this.configFile);
	} catch (IOException ex) {
	    sendLog("Could not save plotsFile to " + this.configFile);
	}
    }

    private boolean setupEconomy() {
	if (getServer().getPluginManager().getPlugin("Vault") == null) {
	    return false;
	}
	RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager()
		.getRegistration(Economy.class);
	if (rsp == null) {
	    return false;
	}
	this.economy = rsp.getProvider();
	return this.economy != null;
    }

    private void writeToConfig(boolean renew) {
	if (this.config != null) {
	    if (renew) {
		this.config.set("checkForUpdates",
			Boolean.valueOf(this.checkForUpdates));
		this.config.set("useEconomy", Boolean.valueOf(this.useEconomy));
		this.config.set("membersPerPlot",
			Integer.valueOf(this.membersPerPlot));
		this.config.set("claimablePlotsPerUser",
			Integer.valueOf(this.claimablePlotsPerUser));
		this.config.set("canDeclaim", Boolean.valueOf(this.canDeclaim));
		this.config.set("canAddRemoveUsers",
			Boolean.valueOf(this.canAddRemoveUsers));
		this.config.set("protectAreaOutsidePlots",
			Boolean.valueOf(this.protectAreaOutsidePlots));
		this.config.set("exemptWorldFromProtectionOutsidePlots",
			this.exemptWorldFromProtectionOutsidePlots);
		this.config
			.set("disallowBlockInteractionsOutsidePlots",
				Boolean.valueOf(this.disallowBlockInteractionsOutsidePlots));
		this.config.set("disallowedInteractionsWithBlockIds",
			this.disallowedInteractions);
	    } else {
		if (!this.config.contains("checkForUpdates")) {
		    this.config.set("checkForUpdates", Boolean.valueOf(true));
		}
		if (!this.config.contains("useEconomy")) {
		    this.config.set("useEconomy", Boolean.valueOf(false));
		}
		if (!this.config.contains("membersPerPlot")) {
		    this.config.set("membersPerPlot", Integer.valueOf(5));
		}
	//	if (!this.config.contains("claimablePlotsPerUser")) {
	//	    this.config.set("claimablePlotsPerUser", Integer.valueOf(1));
	//	}
		if (!this.config.contains("canDeclaim")) {
		    this.config.set("canDeclaim", Boolean.valueOf(true));
		}
		if (!this.config.contains("canAddRemoveUsers")) {
		    this.config.set("canAddRemoveUsers", Boolean.valueOf(true));
		}
		if (!this.config.contains("protectAreaOutsidePlots")) {
		    this.config.set("protectAreaOutsidePlots",
			    Boolean.valueOf(false));
		}
		if (!this.config
			.contains("exemptWorldFromProtectionOutsidePlots")) {
		    this.config.set("exemptWorldFromProtectionOutsidePlots",
			    "exampleworld1,exampleworld2");
		}
		if (!this.config
			.contains("disallowBlockInteractionsOutsidePlots")) {
		    this.config.set("disallowBlockInteractionsOutsidePlots",
			    Boolean.valueOf(false));
		}
		List<Integer> list = new ArrayList<Integer>();
		list.add(Integer.valueOf(96));
		if (!this.config.contains("disallowedInteractionsWithBlockIds")) {
		    this.config.set("disallowedInteractionsWithBlockIds", list);
		}
	    }
	}
    }
}
   /* public void run() {
	String address = "http://consiliumcraft.com/plugins/pguard.html";
	try {
	    URL url = new URL(address);
	    URLConnection connection = url.openConnection();
	    connection.setConnectTimeout(8000);
	    connection.setReadTimeout(15000);
	    connection.setRequestProperty("User-agent", "PerformanceMonitor "
		    + getDescription().getVersion());
	    BufferedReader bufferedReader = new BufferedReader(
		    new InputStreamReader(connection.getInputStream()));
	    String version;
	    if (((version = bufferedReader.readLine()) != null)
		    && (!version.equals(getDescription().getVersion()))) {
		this.latestVersion = false;
		sendLog("There is a new version available for download!");
	    }

	    bufferedReader.close();
	    connection.getInputStream().close();
	} catch (IOException ex) {
	    sendLog("Could not check for latest version: " + ex.getMessage());
	}
    }
}*/
