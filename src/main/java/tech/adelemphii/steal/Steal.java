package tech.adelemphii.steal;

import org.bukkit.plugin.java.JavaPlugin;
import tech.adelemphii.steal.commands.StealCommand;
import tech.adelemphii.steal.listeners.BlockClickListener;

public final class Steal extends JavaPlugin {

    private StealManager stealManager;

    @Override
    public void onEnable() {
        this.stealManager = new StealManager();

        getServer().getPluginManager().registerEvents(new BlockClickListener(this), this);
        getCommand("steal").setExecutor(new StealCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public StealManager getStealManager() {
        return stealManager;
    }
}
