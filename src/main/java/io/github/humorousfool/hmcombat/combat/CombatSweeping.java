package io.github.humorousfool.hmcombat.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import io.github.humorousfool.hmcombat.HMCombat;
import io.github.humorousfool.hmcombat.config.Config;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class CombatSweeping implements Listener
{
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event)
    {
        if(event.getDamager().getType() != EntityType.PLAYER || event.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
            return;

        if(Config.DisableSweeping)
        {
            event.setCancelled(true);
        }

        else if(Config.SweepingEnchantmentOnly)
        {
            Player attacker = (Player) event.getDamager();
            int level = attacker.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SWEEPING_EDGE);
            if(level == 0)
            {
                event.setCancelled(true);
            }
        }
    }

    public static void registerListener()
    {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(HMCombat.getInstance(), PacketType.Play.Server.WORLD_PARTICLES)
                {
                    @Override
                    public void onPacketSending(PacketEvent event)
                    {
                        if(event.getPacket().getNewParticles().getValues().get(0).getParticle().name().equals(EnumWrappers.Particle.SWEEP_ATTACK.name()))
                        {
                            event.setCancelled(true);
                        }
                    }
                }
        );
    }
}
