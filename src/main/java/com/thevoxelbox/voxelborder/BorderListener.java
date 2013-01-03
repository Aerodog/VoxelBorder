package com.thevoxelbox.voxelborder;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(final PlayerInteractEvent event)
    {
        try
        {
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                if (event.getItem().getType().equals(Material.ENDER_PEARL))
                {
                    if (!event.isCancelled()) //For compatibility with other plugins
                    {
                        event.setCancelled(true);
                    	if (event.getPlayer().isOp() ? true : event.getPlayer().hasPermission("voxelborder.enderpearl"))
                        {
                            this.useItem(event.getPlayer());
    
                            final RangeBlockHelper rangeHelper = new RangeBlockHelper(event.getPlayer(), event.getPlayer().getWorld());
                            final Location curLoc = rangeHelper.getTargetBlock().getLocation();

                            if (curLoc == null) {
                                return;
                            }

                            while (!this.isValidJump(curLoc))
                            {
                                curLoc.add(0, 1, 0);
                            }
                            event.getPlayer().teleport(curLoc, TeleportCause.COMMAND);
                        }
                    }
                }
            }
        }
        catch (final Exception e)
        {
        }
    }

    /**
     * Helps handling item use by removing one item from the stack the player is holding, and clearing the stack if only 1 remains.
     * 
     * @param player The player using a material
     */
    private void useItem(Player player)
    {
        final ItemStack hand = player.getItemInHand();
        if (hand != null)
        {
            if (hand.getAmount() > 1) {
                hand.setAmount(hand.getAmount() - 1);
            }
            else
            {
                final PlayerInventory inv = player.getInventory();
                if (inv != null)
                {
                    inv.clear(inv.getHeldItemSlot());
                }
            }
        }
    }

    /**
     * Calculates whether of not a point is a valid jump destination
     * 
     * @param jumpLoc The destination of the jump
     * @return True if valid
     */
    private boolean isValidJump(final Location jumpLoc)
    {
        final Location locA = jumpLoc.clone(), locB = jumpLoc.clone().add(0, 1, 0);
        return locA.getBlock().isEmpty() && locB.getBlock().isEmpty();
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event)
    {
        if (!this.zoneManager.canMoveTo(event.getPlayer(), event.getFrom(), event.getTo()))
        {
            event.getPlayer().teleport(event.getFrom(), TeleportCause.ENDER_PEARL);
        }
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event)
    {
        if (event.getTo() == null)
        {
            event.setCancelled(true);
            return;
        }
        if (event.getCause().equals(TeleportCause.ENDER_PEARL))
        {
            return;
        }
        if (!this.zoneManager.canMoveTo(event.getPlayer(), event.getFrom(), event.getTo()))
        {
            event.setCancelled(true);
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
        if (event.getVehicle() == null) {
            return;
        }
        if (event.getVehicle().getPassenger() instanceof Player)
        {
            final Player _player = ((Player) event.getVehicle().getPassenger());
            if (!this.zoneManager.canMoveTo(_player, event.getFrom(), event.getTo()))
            {
                final Entity entity = event.getVehicle().getPassenger();
                event.getVehicle().eject();
                entity.teleport(event.getFrom(), TeleportCause.ENDER_PEARL);
            }
        }
    }
}
