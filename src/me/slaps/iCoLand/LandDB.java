package me.slaps.iCoLand;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Location;

public interface LandDB {

    abstract public void close();
    
    abstract public boolean createNewLand(Land newLand);
    abstract public boolean removeLandById(int id);
    
    abstract public ArrayList<Land> listAllLand();
    abstract public ArrayList<Land> listLandOwnedBy(String playerName, int limit, int offset);
    
    abstract public int countLandOwnedBy(String playerName);
    
    abstract public int getLandId(Location loc);
    
    abstract public boolean landIdExists(int id);
    
    abstract public int intersectsExistingLand(Cuboid loc);

    abstract public Land getLandById(int id);
    abstract public String getLandPerms(int id);
    abstract public String getLandAddons(int id);
    abstract public String getLandOwner(int id);
    
    abstract public boolean updateLandOwner(int id, String newOwner);
    abstract public boolean updateLandName(int id, String newName);
    abstract public boolean updateLandPerms(int id, String perms);
    abstract public boolean updateLandAddons(int id, String addons);
    
    abstract public boolean hasPermission(int id, String playerName);
    
    abstract public void importDB(File landYMLFile);
    abstract public void exportDB(File landYMLFile);
    
}
