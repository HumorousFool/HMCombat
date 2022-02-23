package io.github.humorousfool.hmcombat.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import io.github.humorousfool.hmcombat.HMCombat;
import io.github.humorousfool.hmcombat.api.AttackSpeed;
import io.github.humorousfool.hmcombat.api.StatUtil;
import io.github.humorousfool.hmcombat.api.WeaponSwingEvent;
import io.github.humorousfool.hmcombat.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CombatAttackSpeed implements Listener
{
    private final HashMap<Player, AttackSpeed> fatiguedPlayers = new HashMap<>();
    private final HashMap<UUID, Long> lastSuccessFullSwing = new HashMap<>();
    private final ArrayList<Player> breakingBlock = new ArrayList<>();

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        fatiguedPlayers.remove(event.getPlayer());
    }

    @EventHandler
    public void onSwitch(PlayerItemHeldEvent event)
    {
        updateFatigue(event.getPlayer(), event.getPlayer().getInventory().getItem(event.getNewSlot()));
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event)
    {
        updateFatigue(event.getPlayer(), event.getMainHandItem());
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent event)
    {
        if(event.getEntity().getType() != EntityType.TRIDENT || !(event.getEntity().getShooter() instanceof Player))
            return;

        Bukkit.getScheduler().runTaskLater(HMCombat.getInstance(), () -> {
            Player player = (Player) event.getEntity().getShooter();
            updateFatigue(player, player.getInventory().getItemInMainHand());
        }, 1);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            if(event.getClickedBlock() != null && fatiguedPlayers.containsKey(event.getPlayer()) && !breakingBlock.contains(event.getPlayer()))
            {
                breakingBlock.add(event.getPlayer());
                removeFatigue(event.getPlayer());
            }
            else if(event.getClickedBlock() == null && breakingBlock.contains(event.getPlayer()))
            {
                breakingBlock.remove(event.getPlayer());
                fatiguedPlayers.remove(event.getPlayer());
                updateFatigue(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
            }

            onSwing(event.getPlayer(), event);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onHit(EntityDamageByEntityEvent event)
    {
        if(event.getDamager().getType() != EntityType.PLAYER) return;

        Player player = (Player) event.getDamager();
        if(breakingBlock.contains(player))
        {
            breakingBlock.remove(player);
            fatiguedPlayers.remove(player);
            updateFatigue(player, player.getInventory().getItemInMainHand());
        }

        if(event.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
        {
            event.setCancelled(onSwing(player, event));
        }
    }

    public boolean onSwing(Player player, Event event)
    {
        if(!fatiguedPlayers.containsKey(player))
        {
            updateFatigue(player, player.getInventory().getItemInMainHand());
            return false;
        }

        if(!lastSuccessFullSwing.containsKey(player.getUniqueId()))
        {
            lastSuccessFullSwing.put(player.getUniqueId(), System.currentTimeMillis());
            final WeaponSwingEvent swingEvent = new WeaponSwingEvent(player, event);
            Bukkit.getPluginManager().callEvent(swingEvent);
            return false;
        }

        if(fatiguedPlayers.get(player) != AttackSpeed.FAST)
        {
            if(lastSuccessFullSwing.get(player.getUniqueId()) + fatiguedPlayers.get(player).attackCooldown > System.currentTimeMillis())
            {
                return event instanceof EntityDamageByEntityEvent;
            }
        }

        lastSuccessFullSwing.put(player.getUniqueId(), System.currentTimeMillis());
        final WeaponSwingEvent swingEvent = new WeaponSwingEvent(player, event);
        Bukkit.getPluginManager().callEvent(swingEvent);

        return false;
    }

    public void updateFatigue(Player player, ItemStack newItem)
    {
        AttackSpeed speed = AttackSpeed.FAST;
        if(newItem != null)
        {
            speed = StatUtil.getSpeed(newItem);
            if(speed == null)
            {
                speed = Config.MaterialAttackSpeeds.getOrDefault(newItem.getType(), AttackSpeed.FAST);
            }
        }

        if(fatiguedPlayers.containsKey(player))
        {
            if (newItem == null || !Config.MaterialAttackSpeeds.containsKey(newItem.getType()) || speed == AttackSpeed.FAST)
            {
                removeFatigue(player);
                fatiguedPlayers.remove(player);
            }

            else if(fatiguedPlayers.get(player).id != speed.id)
            {
                removeFatigue(player);

                PacketContainer effectPacket = HMCombat.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EFFECT);

                effectPacket.getIntegers().write(0, player.getEntityId())
                        .write(1, 32767);
                effectPacket.getBytes().write(0, (byte) 4)
                        .write(1, (byte) speed.effectLevel)
                        .write(2, (byte) 0x0);

                try {
                    HMCombat.getProtocolManager().sendServerPacket(player, effectPacket);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(
                            "Cannot send packet " + effectPacket, e);
                }

                fatiguedPlayers.replace(player, speed);
            }
        }
        else if (speed != AttackSpeed.FAST)
        {
            PacketContainer effectPacket = HMCombat.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EFFECT);

            effectPacket.getIntegers().write(0, player.getEntityId())
                    .write(1, 32767);
            effectPacket.getBytes().write(0, (byte) 4)
                    .write(1, (byte) speed.effectLevel)
                    .write(2, (byte) 0x0);

            try {
                HMCombat.getProtocolManager().sendServerPacket(player, effectPacket);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(
                        "Cannot send packet " + effectPacket, e);
            }

            fatiguedPlayers.put(player, speed);
        }
    }

    private void removeFatigue(Player player)
    {
        PacketContainer removePacket = HMCombat.getProtocolManager().createPacket(PacketType.Play.Server.REMOVE_ENTITY_EFFECT);

        removePacket.getIntegers().write(0, player.getEntityId());
        removePacket.getEffectTypes().write(0, PotionEffectType.SLOW_DIGGING);

        try {
            HMCombat.getProtocolManager().sendServerPacket(player, removePacket);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(
                    "Cannot send packet " + removePacket, e);
        }
    }
}
