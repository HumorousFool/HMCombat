package io.github.humorousfool.hmcombat.config;

import io.github.humorousfool.hmcombat.api.AttackSpeed;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config
{
    public static boolean EnableAttackCooldown = true;
    public static double AttackSpeedOverride = 16;
    public static int PlayerNoDamageTicks = 18;
    public static int EntityNoDamageTicks = 16;

    public static boolean EnableAttackSpeed = true;
    public static boolean EnableMaterialAttackSpeed = false;
    public static HashMap<Material, AttackSpeed> MaterialAttackSpeeds = new HashMap<>();

    public static boolean EnableKnockbackMechanics = true;
    public static boolean EnableSprintCrits = true;
    public static double KnockbackHorizontal = 0.4D;
    public static double KnockbackVertical = 0.4D;
    public static double KnockbackVerticalLimit = 0.4D;
    public static double KnockbackExtraHorizontal = 0.5D;
    public static double KnockbackExtraVertical = 0.1D;

    public static boolean EnableProjectileMechanics = true;

    public static boolean EnableShieldCooldown = true;
    public static int DefaultShieldCooldownTimePlayer = 30;
    public static int DefaultShieldCooldownTimeEntity = 30;
    public static int NoDamageTicksOnBlock = 18;

    public static boolean EnableSoundBlocking = true;
    public static List<Sound> BlockedSounds = new ArrayList<>();

    public static boolean DisableSweeping = false;
    public static boolean DisableSweepingParticles = true;
    public static boolean SweepingEnchantmentOnly = true;

    public static double CosmeticRange = 20D;

    public static boolean EnableBlood = true;
    public static List<EntityDamageEvent.DamageCause> BloodDamageCauses = new ArrayList<>();
    
    public static void update(FileConfiguration file)
    {
        EnableAttackCooldown = file.getBoolean("EnableAttackCooldown", true);
        AttackSpeedOverride = file.getDouble("AttackSpeedOverride", 16D);
        PlayerNoDamageTicks = file.getInt("PlayerNoDamageTicks", 18);
        EntityNoDamageTicks = file.getInt("EntityNoDamageTicks", 16);

        EnableAttackSpeed = file.getBoolean("EnableAttackSpeed", true);
        EnableMaterialAttackSpeed = file.getBoolean("EnableAttackMaterialAttackSpeed", true);
        MaterialAttackSpeeds = getMap((MemorySection) file.get("MaterialAttackSpeeds"));

        EnableKnockbackMechanics = file.getBoolean("EnableKnockbackMechanics", true);
        EnableSprintCrits = file.getBoolean("EnableSprintCrits", true);
        KnockbackHorizontal = file.getDouble("KnockbackHorizontal", 0.4D);
        KnockbackVertical = file.getDouble("KnockbackVertical", 0.4D);
        KnockbackVerticalLimit = file.getDouble("KnockbackVerticalLimit", 0.4D);
        KnockbackExtraHorizontal = file.getDouble("KnockbackExtraHorizontal", 0.5D);
        KnockbackExtraVertical = file.getDouble("KnockbackExtraVertical", 0.1D);

        EnableProjectileMechanics = file.getBoolean("EnableProjectileMechanics", true);

        EnableShieldCooldown = file.getBoolean("EnableShieldCooldown", true);
        DefaultShieldCooldownTimePlayer = file.getInt("DefaultShieldCooldownTimePlayer", 30);
        DefaultShieldCooldownTimeEntity = file.getInt("DefaultShieldCooldownTimeEntity", 30);
        NoDamageTicksOnBlock = file.getInt("NoDamageTicksOnBlock", 18);

        EnableSoundBlocking = file.getBoolean("EnableSoundBlocking", true);
        if(!file.getStringList("BlockedSounds").isEmpty())
        {
            for(String sound : file.getStringList("BlockedSounds"))
            {
                BlockedSounds.add(Sound.valueOf(sound));
            }
        }

        DisableSweeping = file.getBoolean("DisableSweeping", false);
        DisableSweepingParticles = file.getBoolean("DisableSweepingParticles", true);
        SweepingEnchantmentOnly = file.getBoolean("SweepingEnchantmentOnly", true);

        CosmeticRange = file.getDouble("CosmeticRange", 20D);

        EnableBlood = file.getBoolean("EnableBlood", true);
        if(!file.getStringList("BloodDamageCauses").isEmpty())
        {
            for(String cause : file.getStringList("BloodDamageCauses"))
            {
                BloodDamageCauses.add(EntityDamageEvent.DamageCause.valueOf(cause));
            }
        }
    }

    private static HashMap<Material, AttackSpeed> getMap(MemorySection memorySection)
    {
        HashMap<Material, AttackSpeed> map = new HashMap<>();

        if(memorySection == null)
            return map;

        for(String key : memorySection.getKeys(false))
        {
            Material material = Material.matchMaterial(key);
            AttackSpeed speed = AttackSpeed.fromInteger(memorySection.getInt(key));
            map.put(material, speed);
        }

        return map;
    }
}
