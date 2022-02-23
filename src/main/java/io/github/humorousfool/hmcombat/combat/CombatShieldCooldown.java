package io.github.humorousfool.hmcombat.combat;

import io.github.humorousfool.hmcombat.HMCombat;
import io.github.humorousfool.hmcombat.api.StatUtil;
import io.github.humorousfool.hmcombat.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class CombatShieldCooldown implements Listener
{
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event)
    {
        if(event.getEntity().getType() != EntityType.PLAYER || event.isCancelled())
            return;

        Player player = (Player) event.getEntity();

        if(!player.isBlocking() || event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) >= 0)
            return;

        if(event.getDamager() instanceof LivingEntity attacker)
        {

            ItemStack item = attacker.getEquipment().getItemInMainHand();
            int power = StatUtil.getPower(item);
            if(item.getType().name().endsWith("AXE"))
            {
                if(event.getDamager().getType() != EntityType.PLAYER) return;
                else if(((Player) event.getDamager()).isSprinting()) return;
                else power = 5;
            }

            if(power > 0)
                player.setCooldown(Material.SHIELD, power);
            else if(event.getDamager().getType() == EntityType.PLAYER && Config.DefaultShieldCooldownTimePlayer > 0)
                player.setCooldown(Material.SHIELD, Config.DefaultShieldCooldownTimePlayer);
            else if(Config.DefaultShieldCooldownTimeEntity > 0)
                player.setCooldown(Material.SHIELD, Config.DefaultShieldCooldownTimeEntity);
        }
        else if(Config.DefaultShieldCooldownTimeEntity > 0)
        {
            player.setCooldown(Material.SHIELD, Config.DefaultShieldCooldownTimeEntity);
        }

        stopBlocking(player);
        if(Config.NoDamageTicksOnBlock > 0)
            player.setNoDamageTicks(Config.NoDamageTicksOnBlock);
    }

    private void stopBlocking(Player player)
    {
        if(player.getInventory().getItemInMainHand().getType() == Material.SHIELD)
        {
            ItemStack item = player.getInventory().getItemInMainHand();

            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

            Bukkit.getScheduler().runTaskLaterAsynchronously(HMCombat.getInstance(), ()->
                    player.getInventory().setItemInMainHand(item), 2L);
        }

        else if(player.getInventory().getItemInOffHand().getType() == Material.SHIELD)
        {
            ItemStack item = player.getInventory().getItemInOffHand();

            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));

            Bukkit.getScheduler().runTaskLaterAsynchronously(HMCombat.getInstance(), ()->
                    player.getInventory().setItemInOffHand(item), 2L);
        }
    }
}