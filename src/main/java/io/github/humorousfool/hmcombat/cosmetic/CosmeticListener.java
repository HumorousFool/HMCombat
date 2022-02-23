package io.github.humorousfool.hmcombat.cosmetic;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import io.github.humorousfool.hmcombat.HMCombat;
import io.github.humorousfool.hmcombat.config.Config;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class CosmeticListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onHit(EntityDamageEvent event)
    {
        if(event.isCancelled() || !(event.getEntity() instanceof LivingEntity) || !Config.BloodDamageCauses.contains(event.getCause())) return;

        for(Entity entity : event.getEntity().getNearbyEntities(Config.CosmeticRange, Config.CosmeticRange, Config.CosmeticRange))
        {
            if(entity.getType() != EntityType.PLAYER) continue;

            ((Player) entity).spawnParticle(Particle.BLOCK_DUST, ((LivingEntity) event.getEntity()).getEyeLocation(), 15, 0.25D, 0.5D, 0.25D, Material.REDSTONE_WIRE.createBlockData());
        }
    }
}
