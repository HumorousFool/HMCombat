package io.github.humorousfool.hmcombat.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WeaponSwingEvent extends PlayerEvent
{
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Event originalEvent;

    public WeaponSwingEvent(@NotNull Player who, @Nullable Event originalEvent)
    {
        super(who);
        this.originalEvent = originalEvent;
    }

    @Nullable
    public Event getOriginalEvent()
    {
        return originalEvent;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
