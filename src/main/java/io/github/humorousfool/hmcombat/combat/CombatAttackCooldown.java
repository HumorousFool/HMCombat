package io.github.humorousfool.hmcombat.combat;

import io.github.humorousfool.hmcombat.config.Config;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * @author Kernitus
 */

public class CombatAttackCooldown implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event)
    {
        event.getPlayer().setMaximumNoDamageTicks(Config.PlayerNoDamageTicks);
        setAttackSpeed(event.getPlayer(), Config.AttackSpeedOverride);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(PlayerChangedWorldEvent event)
    {
        event.getPlayer().setMaximumNoDamageTicks(Config.PlayerNoDamageTicks);
        setAttackSpeed(event.getPlayer(), Config.AttackSpeedOverride);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        event.getPlayer().setMaximumNoDamageTicks(Config.PlayerNoDamageTicks);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        event.getPlayer().setMaximumNoDamageTicks(20);
        setAttackSpeed(event.getPlayer(), 4);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntitySpawn(EntitySpawnEvent event)
    {
        if(!(event.getEntity() instanceof LivingEntity))
            return;

        ((LivingEntity) event.getEntity()).setMaximumNoDamageTicks(Config.EntityNoDamageTicks);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTeleport(EntityTeleportEvent event)
    {
        if(!(event.getEntity() instanceof LivingEntity))
            return;

        if(event.getFrom().getWorld().getUID() != event.getTo().getWorld().getUID())
            ((LivingEntity) event.getEntity()).setMaximumNoDamageTicks(Config.EntityNoDamageTicks);
    }

    private void setAttackSpeed(Player player, double attackSpeed)
    {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if(attribute == null)
        {
            return;
        }

        double baseValue = attribute.getBaseValue();

        if(baseValue != attackSpeed)
        {
            attribute.setBaseValue(attackSpeed);
            player.saveData();
        }
    }
}
