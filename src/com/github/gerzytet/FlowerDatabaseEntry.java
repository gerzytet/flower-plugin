package com.github.gerzytet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class FlowerDatabaseEntry implements ConfigurationSerializable {
    public Location location;
    public Material flower;

    public FlowerDatabaseEntry(Location location, Material flower) {
        this.location = location;
        this.flower = flower;
    }
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("location", location);
            put("flower", flower.name());
        }};
    }

    public static FlowerDatabaseEntry deserialize(Map<String, Object> map) {
        return new FlowerDatabaseEntry(
                (Location) map.get("location"),
                Material.valueOf((String) map.get("flower"))
        );
    }
}
