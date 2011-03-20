package me.slaps.iConomyLand;


import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class iConomyLandCommandListener implements CommandExecutor {
    
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Messaging mess = new Messaging(sender);

        if ( iConomyLand.debugMode ) {
            String debug = "iConomyLand.onCommand(): " + ((sender instanceof Player) ? "Player " + ((Player) sender).getName() : "Console") + " Command " + cmd.getName() + " args: ";
            for (int i = 0; i < args.length; i++) 
                debug += args[i] + " ";
            iConomyLand.info(debug);
        }

        // temporary
        if (!(sender instanceof Player))
        	return false;

        // is our command?
        if ( Misc.isAny(cmd.getName(), "icl", "iConomyLand", "iConomyLand:icl", "iConomyLand:iConomyLand") ) {
            if (iConomyLand.debugMode) iConomyLand.info("Is an /icl or /iConomyLand command");

            // /icl
            if ( args.length == 0 ) {
                showHelp(sender, "");
                return true;
                
            // /icl help
            }  else if ( args[0].equalsIgnoreCase("help") ) {
                if ( args.length == 1 ) {
                    showHelp(sender, "");
                } else {
                    showHelp(sender, args[1] );
                }
                return true;
                
            // /icl list
            } else if (args[0].equalsIgnoreCase("list") ) {
                if ( iConomyLand.hasPermission(sender, "list") ) { 
                
                } else {
                    mess.send("{ERR}No access for list");
                }
                return true;
                
            // /icl select
            } else if (args[0].equalsIgnoreCase("select") ) {
                if ( iConomyLand.hasPermission(sender, "select") ) { 
                    if ( sender instanceof Player ) 
                        selectArea((Player)sender);
                    else
                        mess.send("Console can't select");
                
                } else {
                    mess.send("{ERR}No access for that...");                    
                }
                return true;
                
            // /icl info
            } else if (args[0].equalsIgnoreCase("info") ) {
                if ( iConomyLand.hasPermission(sender, "info") ) {
                    if ( args.length == 1 ) {
                        if ( sender instanceof Player )
                            showLandInfo((Player)sender, "");
                        else
                            mess.send("{ERR}Console needs to supply arguments for this command");
                    } else {
                        showLandInfo(sender, args[1]);
                    }
                } else {
                    mess.send("{ERR}No access for that...");
                }
                return true;
                
            // /icl buy
            } else if (args[0].equalsIgnoreCase("buy") ) {
                if ( iConomyLand.hasPermission(sender, "buy") ) { 
                
                } else {
                    mess.send("{ERR}No access for that...");
                }
                return true;
                
            // /icl modify
            } else if (args[0].equalsIgnoreCase("modify") ) {
                if ( iConomyLand.hasPermission(sender, "modify") ) { 
                
                } else {
                    mess.send("{ERR}No access for that...");
                }
                return true;
                
            // unrecognized /icl command
            } else {
                mess.send("{}Unrecognized/invalid/malformed command!");
                mess.send("{} Please use {CMD}/icl help {BKT}[{PRM}topic{BKT}] {}for help");
                return true;
            }

        // command not recognized ( not /icl )
        } else {
            return false;
        }

    }
    
    public void showLandInfo(Player sender, String... args) {
        Messaging mess = new Messaging(sender);
        
        if ( args[0].isEmpty() ) {
            // use location search or selected search
            String playerName = sender.getName();
            if ( iConomyLand.tmpCuboidMap.containsKey(playerName) ) {
                Cuboid select = iConomyLand.tmpCuboidMap.get(playerName);
                if ( select.isValid() ) {
                    iConomyLand.landMgr.showSelectLandInfo((CommandSender)sender, select);
                } else {
                    mess.send("{ERR}Current selection invalid! Use {CMD}/lwc select{} to cancel");
                }
            } else {
                Location loc = sender.getLocation();
                Integer landid = iConomyLand.landMgr.getLandID(loc);
                if ( landid > 0 ) {
                    iConomyLand.landMgr.showSelectLandInfo((CommandSender)sender, landid);
                } else {
                    mess.send("{ERR}No current selection, not on owned land");
                }
            }
            
        } else {
            
        }
    }
    
    public void showLandInfo(CommandSender sender, String... args) {
        
    }
    
    public void showHelp(CommandSender sender, String topic) {
        Messaging mess = new Messaging(sender);
        mess.send(Misc.headerify("{CMD} " + iConomyLand.name + " {BKT}({CMD}" + iConomyLand.codename + "{BKT}) "));
    	if ( topic == null || topic.isEmpty() ) {
    	    
    	    mess.send(" {CMD}/icl {}- main command");
    	    mess.send(" {CMD}/icl {PRM}help {BKT}[{PRM}topic{BKT}] {}- help topics");
    	    String topics = "";
            if ( iConomyLand.hasPermission(sender, "list") ) topics += "list";
            if ( iConomyLand.hasPermission(sender, "select") ) topics += " select";
            if ( iConomyLand.hasPermission(sender, "info") ) topics += " info";
            if ( iConomyLand.hasPermission(sender, "buy") ) topics += " buy";
            if ( iConomyLand.hasPermission(sender, "modify") ) topics += " modify";
    	    
    	    mess.send(" {} help topics: {CMD}" + topics);
    	    
    	} else if ( topic.equalsIgnoreCase("list") ) {
            if ( iConomyLand.hasPermission(sender, "list") ) { 
                mess.send(" {CMD}/icl {PRM}list {}- list owned land");
            }
            
        } else if ( topic.equalsIgnoreCase("select") ) {
            if ( iConomyLand.hasPermission(sender, "select") ) { 
                mess.send(" {CMD}/icl {PRM}select {}- select land");
            }
        } else if ( topic.equalsIgnoreCase("info") ) {
            if ( iConomyLand.hasPermission(sender, "info") ) { 
                mess.send(" {CMD}/icl {PRM}info {}- get land info");
            }
            
        } else if ( topic.equalsIgnoreCase("buy") ) {
            if ( iConomyLand.hasPermission(sender, "buy") ) { 
                mess.send(" {CMD}/icl {PRM}buy {}- purchase selected land");
            }
            
        } else if ( topic.equalsIgnoreCase("modify") ) {
            if ( iConomyLand.hasPermission(sender, "modify") ) { 
                mess.send(" {CMD}/icl {PRM}modify {}- modify land settings");
            }
            
        }
    }
    
    public boolean selectArea(Player player) {
        String playerName = player.getName();
        Messaging mess = new Messaging((CommandSender)player);
        if ( iConomyLand.cmdMap.containsKey(playerName) ) {
            String action = iConomyLand.cmdMap.get(playerName);
            if ( action.equals("select") ) {
                mess.send("{ERR}Cancelling current selection process and starting over");
                iConomyLand.cmdMap.remove(playerName);
                iConomyLand.tmpCuboidMap.remove(playerName);
            }
        }
        
        mess.send("{}Select 1st Corner");
        iConomyLand.cmdMap.put(playerName,"select");
        return true;
    }
    

    
    
    
}
