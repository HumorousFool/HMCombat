package io.github.humorousfool.hmcombat;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.humorousfool.hmcombat.combat.*;
import io.github.humorousfool.hmcombat.config.Config;
import io.github.humorousfool.hmcombat.cosmetic.CosmeticListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class HMCombat extends JavaPlugin
{
    private static HMCombat instance;
    private static ProtocolManager protocolManager;

    @Override
    public void onEnable()
    {
        instance = this;

        saveDefaultConfig();
        Config.update(getConfig());

        if(getServer().getPluginManager().getPlugin("ProtocolLib") == null || !getServer().getPluginManager().getPlugin("ProtocolLib").isEnabled())
        {
            getLogger().log(Level.SEVERE, "Could not find ProtocolLib! Disabling plugin!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        protocolManager = ProtocolLibrary.getProtocolManager();

        if(Config.EnableAttackCooldown)
            getServer().getPluginManager().registerEvents(new CombatAttackCooldown(), this);
        if(Config.EnableAttackSpeed)
            getServer().getPluginManager().registerEvents(new CombatAttackSpeed(), this);
        if(Config.EnableKnockbackMechanics)
            getServer().getPluginManager().registerEvents(new CombatKnockbackMechanics(), this);
        if(Config.EnableProjectileMechanics)
            getServer().getPluginManager().registerEvents(new CombatProjectileMechanics(), this);
        if(Config.EnableShieldCooldown)
            getServer().getPluginManager().registerEvents(new CombatShieldCooldown(), this);
        if(Config.EnableSoundBlocking)
            CombatSoundBlocking.registerListener();
        if(Config.DisableSweeping || Config.SweepingEnchantmentOnly)
            getServer().getPluginManager().registerEvents(new CombatSweeping(), this);
        if(Config.DisableSweepingParticles)
            CombatSweeping.registerListener();

        if(Config.EnableBlood)
            getServer().getPluginManager().registerEvents(new CosmeticListener(), this);
    }

    @Override
    public void onDisable()
    {
        protocolManager.removePacketListeners(this);
        instance = null;
    }

    public static HMCombat getInstance()
    {
        return instance;
    }

    public static ProtocolManager getProtocolManager()
    {
        return protocolManager;
    }
}
