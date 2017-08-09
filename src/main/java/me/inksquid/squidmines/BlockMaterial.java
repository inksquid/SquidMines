package me.inksquid.squidmines;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
public class BlockMaterial {
    @Getter private Material material;
    @Getter private short damage;
}
