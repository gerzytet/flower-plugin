package com.github.gerzytet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

class CooldownQueueEntry {
    public FlowerDatabaseEntry entry;
    public long releaseTime;

    public CooldownQueueEntry(FlowerDatabaseEntry entry, long releaseTime) {
        this.entry = entry;
        this.releaseTime = releaseTime;
    }
}
public class FlowerDatabase {
    private static long tickCount = 0;
    private static File configFile;
    private static YamlConfiguration config;
    private static Map<Location, FlowerDatabaseEntry> flowerDatabase = new HashMap<>();

    private static Queue<CooldownQueueEntry> cooldownQueue = new ArrayDeque<>();

    private static Map<Material, Material> flowerDrops = new HashMap<>();
    static {
        flowerDrops.put(Material.POPPY, Material.RED_DYE);
        flowerDrops.put(Material.DANDELION, Material.YELLOW_DYE);
        flowerDrops.put(Material.BLUE_ORCHID, Material.LIGHT_BLUE_DYE);
        flowerDrops.put(Material.ALLIUM, Material.MAGENTA_DYE);
    }
    public static FlowerDatabaseEntry getEntryAtLocaiton(Location location) {
        return flowerDatabase.get(location);
    }
    public static void addFlowerEntry(FlowerDatabaseEntry entry) {
        List<FlowerDatabaseEntry> flowerEntries = (List<FlowerDatabaseEntry>) config.getList("flowers", new ArrayList<FlowerDatabaseEntry>());
        flowerEntries.add(entry);
        config.set("flowers", flowerEntries);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        flowerDatabase.put(entry.location, entry);
    }

    public static void removeFlowerEntry(FlowerDatabaseEntry entry) {
        cooldownQueue.removeIf(cooldownEntry -> cooldownEntry.entry == entry);
        flowerDatabase.remove(entry.location);
        List<FlowerDatabaseEntry> flowerEntries = (List<FlowerDatabaseEntry>) config.getList("flowers", new ArrayList<FlowerDatabaseEntry>());
        flowerEntries.removeIf(flowerEntry -> flowerEntry.location.equals(entry.location));
        config.set("flowers", flowerEntries);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initDatabase() {
        try {
            FlowerDatabase.class.getClassLoader().loadClass("com.github.gerzytet.FlowerDatabaseEntry");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        configFile = new File(FlowerPlugin.instance.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        List<FlowerDatabaseEntry> entries = (List<FlowerDatabaseEntry>) config.getList("flowers", new ArrayList<FlowerDatabaseEntry>());
        for (FlowerDatabaseEntry entry : entries) {
            flowerDatabase.put(entry.location, entry);
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(
                FlowerPlugin.instance,
                FlowerDatabase::tickCooldowns,
                0,
                1
        );
        placeInitialFlowers();
    }

    private static void tickCooldowns() {
        tickCount++;
        while (cooldownQueue.size() > 0 && tickCount >= cooldownQueue.peek().releaseTime) {
            CooldownQueueEntry entry = cooldownQueue.poll();
            entry.entry.location.getBlock().setType(entry.entry.flower);
        }
    }

    public static void placeInitialFlowers() {
        for (FlowerDatabaseEntry entry : flowerDatabase.values()) {
            entry.location.getBlock().setType(entry.flower);
        }
    }

    public static boolean isValidFlower(Material material) {
        return flowerDrops.containsKey(material);
    }

    public static Material getFlowerDrop(Material flower) {
        return flowerDrops.get(flower);
    }

    public static void putEntryOnCooldown(FlowerDatabaseEntry entry) {
        long releaseTime = tickCount + 100;
        cooldownQueue.add(new CooldownQueueEntry(entry, releaseTime));
    }
}
