package com.thevoxelbox.voxelborder;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import com.thevoxelbox.voxelborder.util.RangeBlockHelper;

/**
 * 
 * @author Voxel
 */
public class BorderListener implements Listener
{

    private final ZoneManager zoneManager;

    public BorderListener(final ZoneManager zoneManager)
    {
        this.zoneManager = zoneManager;
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event)
    {
        try
        {
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                if (event.getItem().getType().equals(Material.ENDER_PEARL))
                {
                    event.setCancelled(true);
                    event.setUseItemInHand(Result.ALLOW);
                    final RangeBlockHelper rangeHelper = new RangeBlockHelper(event.getPlayer(), event.getPlayer().getWorld());
                    event.getPlayer().teleport(rangeHelper.getCurBlock().getLocation().add(0, 1, 0), TeleportCause.PLUGIN);
                }
            }
        }
        catch (final Exception e)
        {
        }
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event)
    {
        if (!this.zoneManager.canMoveTo(event.getPlayer(), event.getFrom(), event.getTo()))
        {
            event.getPlayer().teleport(event.getFrom(), TeleportCause.PLUGIN);
        }
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event)
    {
        if (event.getCause().equals(TeleportCause.ENDER_PEARL))
        {
            return;
        }
        if (event.getFrom().getWorld().equals(event.getTo().getWorld()))
        {
            if (!this.zoneManager.canMoveTo(event.getPlayer(), event.getFrom(), event.getTo()))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleEnter(final VehicleEnterEvent event)
    {
        if (event.getEntered() instanceof Player)
        {
            final Player _player = ((Player) event.getEntered());
            if (!this.zoneManager.canMoveTo(_player, event.getEntered().getLocation(), event.getVehicle().getLocation()))
            {
                event.getVehicle().eject();
                event.getEntered().teleport(event.getEntered().getLocation(), TeleportCause.PLUGIN);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleMove(final VehicleMoveEvent event)
    {
        if (event.getVehicle().getPassenger() instanceof Player)
        {
            final Player _player = ((Player) event.getVehicle().getPassenger());
            if (!this.zoneManager.canMoveTo(_player, event.getFrom(), event.getTo()))
            {
                final Entity entity = event.getVehicle().getPassenger();
                event.getVehicle().eject();
                entity.teleport(event.getFrom(), TeleportCause.PLUGIN);
            }
        }
    }
}
