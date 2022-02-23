package io.github.humorousfool.hmcombat.api;

import io.github.humorousfool.hmcombat.HMCombat;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class StatUtil
{
    public static final NamespacedKey powerKey = new NamespacedKey(HMCombat.getInstance(), "attackPower");
    public static final NamespacedKey speedKey = new NamespacedKey(HMCombat.getInstance(), "attackSpeed");

    public static int getPower(@NotNull ItemStack item)
    {
        if(!item.hasItemMeta() || !item.getItemMeta().getPersistentDataContainer().has(powerKey, PersistentDataType.INTEGER))
            return  0;
        return item.getItemMeta().getPersistentDataContainer().get(powerKey, PersistentDataType.INTEGER);
    }

    public static AttackSpeed getSpeed(@NotNull ItemStack item)
    {
        if(!item.hasItemMeta() || !item.getItemMeta().getPersistentDataContainer().has(speedKey, PersistentDataType.INTEGER))
            return null;
        return AttackSpeed.fromInteger(item.getItemMeta().getPersistentDataContainer().get(speedKey, PersistentDataType.INTEGER));
    }
}
