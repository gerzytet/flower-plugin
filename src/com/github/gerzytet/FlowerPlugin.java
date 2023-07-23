package com.github.gerzytet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class FlowerPlugin extends JavaPlugin implements Listener {
    public static FlowerPlugin instance;
    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getLogger().info(ChatColor.GREEN + "Enabled " + this.getName());
        FlowerCommands flowerCommands = new FlowerCommands();
        getCommand("createflower").setExecutor(flowerCommands);
        getCommand("removeflower").setExecutor(flowerCommands);
        saveDefaultConfig();
        FlowerDatabase.initDatabase();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        FlowerDatabaseEntry entry = FlowerDatabase.getEntryAtLocaiton(event.getClickedBlock().getLocation());
        if (entry == null) {
            return;
        }
        if (event.getClickedBlock().getType() != entry.flower) {
            return;
        }
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.SHEARS) {
            return;
        }

        event.getPlayer().getInventory().addItem(new ItemStack(FlowerDatabase.getFlowerDrop(entry.flower)));
        entry.location.getBlock().setType(Material.AIR);
        FlowerDatabase.putEntryOnCooldown(entry);
    }
}