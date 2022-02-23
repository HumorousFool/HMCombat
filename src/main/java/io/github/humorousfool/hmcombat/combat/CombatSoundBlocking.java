package io.github.humorousfool.hmcombat.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.github.humorousfool.hmcombat.HMCombat;
import io.github.humorousfool.hmcombat.config.Config;
import org.bukkit.Sound;

public class CombatSoundBlocking
{
    public static void registerListener()
    {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(HMCombat.getInstance(), PacketType.Play.Server.NAMED_SOUND_EFFECT)
                {
                    @Override
                    public void onPacketSending(PacketEvent event)
                    {
                        for(Sound sound : Config.BlockedSounds)
                        {
                            if(event.getPacket().getSoundEffects().getValues().contains(sound))
                            {
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }
                }
        );
    }
}
