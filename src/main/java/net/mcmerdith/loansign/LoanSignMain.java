package net.mcmerdith.loansign;

import net.mcmerdith.loansign.command.LoanCommand;
import net.mcmerdith.loansign.operations.loanPlayerMoney;
import net.mcmerdith.loansign.storage.FlatFileDataStore;
import net.mcmerdith.loansign.storage.LoanData;
import net.mcmerdith.mcmpluginlib.McmPluginLogger;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.configuration.configUtil;

import java.util.HashMap;
import java.util.Map;

public class LoanSignMain extends JavaPlugin {
    private static final McmPluginLogger logger = McmPluginLogger.classInstance(LoanSignMain.class);

    public static LoanSignMain instance;

    public static Economy economy;

    private static final int B_STATS_ID = 18595;

    @Override
    public void onEnable() {
        instance = this;

        // Check if signshop is loaded
        PluginManager pm = Bukkit.getServer().getPluginManager();
        if (!pm.isPluginEnabled("SignShop")) {
            logger.error("SignShop is not loaded, can not continue.");
            pm.disablePlugin(this);
            return;
        }

        // Create our working directory if it doesn't already exist
        createDir();

        String filename = "config.yml";
        FileConfiguration ymlThing = configUtil.loadYMLFromPluginFolder(this, filename);
        configUtil.loadYMLFromJar(this, LoanSignMain.class, ymlThing, filename);

        SignShopConfig.registerExternalOperation(new loanPlayerMoney());
        SignShopConfig.setupOperations(configUtil.fetchStringStringHashMap("signs", ymlThing), "net.mcmerdith.loansign.operations");
        SignShopConfig.registerErrorMessages(configUtil.fetchStringStringHashMap("errors", ymlThing));
        for (Map.Entry<String, HashMap<String, String>> entry : configUtil.fetchHasmapInHashmap("messages", ymlThing).entrySet()) {
            SignShopConfig.registerMessages(entry.getKey(), entry.getValue());
        }

        if (SignShopConfig.metricsEnabled()) {
//            Metrics metrics =
            new Metrics(this, B_STATS_ID);
            logger.info("Thank you for enabling metrics!");
        }

        LoanData.instance().enable(new FlatFileDataStore());

        new LoanCommand().setExecutorFor(getCommand("loan"));
        getLogger();
        logger.info("Enabled");
    }

    @Override
    public void onDisable() {
        super.onDisable();

        LoanData.instance().disable();
    }

    /***
     * Ensure the plugin directory exists
     */
    private void createDir() {
        if (!this.getDataFolder().exists()) {
            if (!this.getDataFolder().mkdir()) {
                logger.error("Could not create plugin folder!");
            }
        }
    }
}