package com.thevoxelbox.bukkit.border.util;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.thevoxelbox.bukkit.border.VoxelBorder;

/**
 *
 * @author Voxel
 */
public class Admin {

    private static TreeMap<Integer, TreeSet<String>> lists = new TreeMap<Integer, TreeSet<String>>();
    private static TreeMap<Integer, vList> infos = new TreeMap<Integer, vList>();
    private static TreeMap<String, Integer> ids = new TreeMap<String, Integer>();
    private static int lastID = 0;

    public static void addList(String filePath, String listName) {
        vList newList = new vList(listName, filePath);
        newList.ID = lastID++;
        lists.put(newList.ID, new TreeSet<String>());
        infos.put(newList.ID, newList);
        ids.put(newList.name, newList.ID);
    }

    public static boolean hasList(String listName) {
        if(ids.containsKey(listName)) {
            return lists.containsKey(ids.get(listName));
        }
        return false;
    }

    public static int getListID(String listName) {
        return (ids.containsKey(listName) ? ids.get(listName) : -1);
    }

    public static String getListName(Integer listID) {
        return infos.get(listID).name;
    }

    public static String getListPath(Integer listID) {
        return infos.get(listID).path;
    }

    public static Map<String, String> getNamePath() {
        TreeMap<String, String> temp = new TreeMap<String, String>();
        for(vList vl : infos.values()) {
            temp.put(vl.name, vl.path);
        }
        return temp;
    }

    public static boolean contains(Integer listID, String name) {
        return lists.containsKey(listID) ? lists.get(listID).contains(name) : false;
    }

    public static void add(Integer listID, String name) {
        if (lists.containsKey(listID)) {
            lists.get(listID).add(name);
        }
    }

    public static void remove(Integer listID, String name) {
        if (lists.containsKey(listID)) {
            lists.get(listID).remove(name);
        }
    }

    public static void saveList(String listName) {
        if (!hasList(listName)) {
            return;
        }
        String path = infos.get(ids.get(listName)).path;
        try {
            File f = new File(path);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
                VoxelBorder.log.info("[VoxelBorder] \"" + path + "\" was missing and thus created.");
            }
            PrintWriter pw = new PrintWriter(f);
            for (String name : lists.get(ids.get(listName))) {
                pw.println(name);
            }
            pw.close();
        } catch (Exception e) {
            VoxelBorder.log.warning("[VoxelBorder] Error while saving: " + path);
            return;
        }
        VoxelBorder.log.info("[VoxelBorder] List \"" + listName + "\" has been saved succesfully to \"" + path + "\"");
    }

    public static void readList(String path, String listName) {
        if (hasList(listName)) {
            return;
        }
        TreeSet<String> tree = new TreeSet<String>();
        try {
            File f = new File(path);
            if (!f.exists()) {
                f.createNewFile();
                VoxelBorder.log.info("[VoxelBorder] " + path + " was missing and thus created.");
            }
            Scanner snr = new Scanner(f);
            while (snr.hasNext()) {
                String st = snr.nextLine();
                tree.add(st);
            }
            vList newList = new vList(listName, path);
            newList.ID = lastID++;
            lists.put(newList.ID, tree);
            infos.put(newList.ID, newList);
            ids.put(newList.name, newList.ID);
            snr.close();
        } catch (Exception e) {
            VoxelBorder.log.warning("[VoxelBorder] Error while loading " + path);
            e.printStackTrace();
            return;
        }
        VoxelBorder.log.info("[VoxelBorder] List \"" + listName + "\" has been loaded succesfully from \"" + path + "\"");
    }

    public static void printLists(CommandSender sender) {
        if (lists.isEmpty()) {
            sender.sendMessage(ChatColor.GOLD + "Sorry, there are no lists.");
            return;
        }
        for (vList listInfo : infos.values()) {
            sender.sendMessage(ChatColor.AQUA + listInfo.name + " (ID:" + listInfo.ID + ") - \"" + listInfo.path + "\"");
        }
    }

    private static class vList {

        public String name;
        public String path;
        public int ID;

        public vList(String listname, String listpath) {
            name = listname;
            path = listpath;
        }
    }
}
