package com.thevoxelbox.voxelborder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.gson.Gson;

/**
 * Handles all currently active zones. Also handles zone serialization.
 * 
 * @author TheCryoknight
 */
public class ZoneManager
{
    private static final File ZONE_FILE = new File("plugins/voxelborder/zones.json");

    public static File getZoneFile()
    {
        return ZoneManager.ZONE_FILE;
    }

    private final String basePerm = "voxelborder.zone.";

    private final Set<UUID> activeWorlds = new HashSet<UUID>();
    private final Set<Zone> zones = new HashSet<Zone>();

    private final VoxelBorder plugin;

    public ZoneManager(final VoxelBorder plugin)
    {
        this.plugin = plugin;
        final File zoneFolder = new File("plugins/voxelborder/");
        if (!zoneFolder.exists())
        {
            zoneFolder.mkdirs();
        }
        this.readZones(ZoneManager.ZONE_FILE);
    }

    /**
     * Adds a zone to the active list
     * 
     * @param newZone
     *            Zone to add
     * @return 
     */
    public boolean addZone(final Zone newZone)
    {
        for (Zone zone : this.zones)
        {
            if (zone.getName().equalsIgnoreCase(newZone.getName()))
            {
                return false;
            }
        }
        this.activeWorlds.add(newZone.getWorldID());
        return this.zones.add(newZone);
    }

    /**
     * Checks to see if a player can move from a location to another location.
     * 
     * @param player
     *            The player moving
     * @param startLoc
     *            Originating location
     * @param endLoc
     *            Ending location
     * @return True if the specified player can move
     */
    public boolean canMoveTo(final Player player, final Location startLoc, final Location endLoc)
    {
        if (endLoc == null)
        {
            return true;
        }
        if (endLoc.getWorld() == null) 
        {
            return true;
        }
        if (endLoc.getWorld().getUID() == null) 
        {
            return true;
        }
        if (!activeWorlds.contains(endLoc.getWorld().getUID()))
        {
            return true;
        }

        boolean canEnter = true;
        boolean inZone = false;
        for (final Zone zone : this.zones)
        {
            if (zone.getWorldID().equals(endLoc.getWorld().getUID()))
            {
                if (zone.inBound(endLoc))
                {
                	inZone = true;
                    if (zone.inBound(startLoc))
                    {
                    	if (canEnter)
                    	{
                    		continue;
                    	}
                    }
                    if (player.isOp() ? true : player.hasPermission(this.basePerm + zone.getName().replaceAll(" ", "")))
                    {
                    	if (canEnter)
                    	{
                        	player.sendMessage(ChatColor.GRAY + "Now crossing border of " + ChatColor.GREEN + zone.getName().trim());
                        	continue;
                    	}
                    }
                    else
                    {
                        player.sendMessage(ChatColor.GRAY + "You can not cross the border of " + ChatColor.GREEN + zone.getName().trim());
                    	canEnter = false;
                    	break;
                    }
                }
            }
        }

        if (inZone)
        {
        	return canEnter;
        }

        player.sendMessage(ChatColor.GRAY + "You can not access area outside of the the borders");
        return false;
    }

    /**
     * Searches all active zones for one that has the same name as <code>zoneName</code>. 
     * Note: This is not case sensitive
     * 
     * @param zoneName
     *            Name of the zone to search
     * @return Zone that is currently active and name matches the <code>zoneName</code>. will return null if no such zone exists.
     */
    public Zone getZone(final String zoneName)
    {
        for (final Zone zone : this.zones)
        {
            if (zone.getName().equalsIgnoreCase(zoneName))
            {
                return zone;
            }
        }
        return null;
    }

    /**
     * Searches the names of all the active zones and returns a list of all the zones that start with the search term.
     * 
     * @param str
     *            the search term
     * @return A list of strings that start with the search term
     */
    public List<String> lookupZone(final String str)
    {
        final List<String> matches = new ArrayList<String>();
        for (final Zone zone : this.zones)
        {
            if (zone.getName().toLowerCase().startsWith(str))
            {
                matches.add(zone.getName());
            }
        }
        return matches;
    }
    /**
    * Creates an array of strings that represent the active zones.
    * 
    * @return Array of the <code>toColoredString()</code> from all the active zones
    */
    public String[] getZones() {
        final List<String> zoneTxt = new ArrayList<String>();
        for (Zone zone : this.zones)
        {
            zoneTxt.add(zone.toColoredString());
        }
        Collections.sort(zoneTxt);
        return zoneTxt.toArray(new String[0]);
    }
    
    /**
     * Refreshes all of the active worlds, this prevents a world witch has no more zones from being monitored
     */
    private void updateActiveWorlds() {
        this.activeWorlds.clear();
        for (Zone zone : this.zones) {
            this.activeWorlds.add(zone.getWorldID());
        }
    }

    /**
     * Removes a zone from the active list
     * 
     * @param oldZone
     *            Zone to remove
     * @return true if successfully removed
     */
    public boolean removeZone(final Zone oldZone)
    {
        final boolean success = this.zones.remove(oldZone);
        this.updateActiveWorlds();
        return success;
    }

    public void readZones(final File zoneFile)
    {
        final Gson gson = new Gson();
        if (zoneFile.exists())
        {
            Scanner scan;
            try
            {
                scan = new Scanner(zoneFile);
            }
            catch (final Exception e)
            {
                this.plugin.getLogger().severe("Can not open config files");
                e.printStackTrace();
                return;
            }
            try
            {
                while (scan.hasNextLine())
                {
                    final Zone zone = gson.fromJson(scan.nextLine(), Zone.class);
                    this.addZone(zone);
                }
            }
            catch (final Exception e)
            {
                this.plugin.getLogger().severe("Can not read config files");
                e.printStackTrace();
                return;
            }
            finally
            {
                scan.close();
            }
        }
        else
        {

        }
    }

    public void saveZones(final File zonefile)
    {
        final Gson gson = new Gson();
        if (!zonefile.exists())
        {
            try
            {
                zonefile.createNewFile();
            }
            catch (final IOException e)
            {
                e.printStackTrace();
                return;
            }
        }
        else
        {
            if (!zonefile.delete())
            {
                this.plugin.getLogger().severe("Can not save config files");
            }
            try
            {
                zonefile.createNewFile();
            }
            catch (final IOException e)
            {
                e.printStackTrace();
                return;
            }
        }
        PrintWriter pw;
        try
        {
            pw = new PrintWriter(zonefile);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return;
        }
        for (final Zone z : this.zones)
        {
                pw.println(gson.toJson(z));
        }
        pw.close();
    }
}
