package me.slaps.iCoLand;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.iConomy.iConomy;
import com.iConomy.system.Account;


public class TaskLandTaxes implements Runnable {
    
    public void run() {
        Player[] players = iCoLand.server.getOnlinePlayers();
        int timeOffset = Config.taxTimeMinutes*60*1000;
        Timestamp now = new Timestamp(System.currentTimeMillis());

        if ( Config.debugMode1 ) 
            iCoLand.info("Time offset ms: "+timeOffset);

        
        ArrayList<Land> lands = iCoLand.landMgr.listLandPastTaxTime(now);

        if ( Config.debugMode1 )
            iCoLand.info("Starting tax task...  "+now);
        
        for(Land land : lands) {
            double tax = Double.valueOf(iCoLand.df.format(land.location.volume()*Config.pricePerBlock.get("raw")*Config.taxRate));
            
            Account acc = iConomy.getAccount(land.owner);
            Account bank = iConomy.getAccount(Config.bankName);
            
            if ( Config.debugMode1 ) 
                iCoLand.info("Last tax date for ID#"+land.getID()+" : "+land.dateTax);
            
            Timestamp nextTaxDate = new Timestamp(land.dateTax.getTime());
            
            while ( nextTaxDate.before(now) ) {
                nextTaxDate.setTime(nextTaxDate.getTime()+timeOffset);
                if ( acc == null ) {
                    iCoLand.info("Land ID# "+land.getID()+ " belongs to "+land.owner+", but he does not have an iConomy account!");
                } else if  ( !iCoLand.hasPermission(land.location.setLoc1.getWorld().getName(), land.owner, "admin.notax") ) {
                    if ( acc.getHoldings().hasEnough(tax) ) {
    
                        // subtract tax out
                        acc.getHoldings().subtract(tax);
                        bank.getHoldings().add(tax);
                        
                        int i = playerInList(players, land.owner);
                        if ( i > -1 ) {
                            Messaging mess = new Messaging(players[i]);
                            mess.send("{}Land ID# {PRM}"+land.getID()+" {}taxed for {PRM}"+iCoLand.df.format(tax));
                        } 
                        
                        if ( Config.debugMode ) 
                            iCoLand.info(land.owner+ " - Land ID# "+land.getID()+" taxed for "+iCoLand.df.format(tax));
                    } else {
                        // not enough for taxes, delete zone/mark inactive !
                        if (!iCoLand.landMgr.updateActive(land.getID(), false)) {
                            iCoLand.severe("Error setting land ID# "+land.getID()+" inactive, due to unpaid taxes");
                        }
                        
                        int i = playerInList(players, land.owner);
                        if ( i >= 0 ) {
                            Messaging mess = new Messaging(players[i]);
                            mess.send("{ERR}Not enough money to pay tax of {PRM}"+tax+" on land ID# {PRM}"+land.getID());
                            mess.send("{}Land ID# {PRM}"+land.getID()+"{} marked inactive");
                        }
    
                        iCoLand.info(land.owner+" didn't have enough money to pay tax of "+tax+" on land ID# "+land.getID());
                        iCoLand.info("Land ID# "+land.getID()+" marked inactive");
                    }
                    
                } else {
                    // notax perm
                    int i = playerInList(players, land.owner);
                    if ( i > -1 ) {
                        Messaging mess = new Messaging(players[i]);
                        mess.send("{}Land ID# {PRM}"+land.getID()+" {}taxed for {PRM}0 {BKT}({PRM}"+iCoLand.df.format(tax)+"{BKT})");
                    }
                    
                    iCoLand.info(land.owner+ " - Land ID# "+land.getID()+" taxed for 0 ("+iCoLand.df.format(tax)+") - notax perm");
                }
                

            }
            
            if (!iCoLand.landMgr.updateTaxTime(land.getID(), nextTaxDate)) {
                iCoLand.severe("Error updating tax timestamp on land ID# "+land.getID());
            }
            
            if ( Config.debugMode1 ) 
                iCoLand.info("New tax date for ID#"+land.getID()+" : "+nextTaxDate);

        }
        


        Timestamp timeThreshold = new Timestamp(now.getTime()-Config.inactiveDeleteTime*60*1000);
        lands = iCoLand.landMgr.listLandPastInactiveTime(timeThreshold);

        if ( Config.debugMode1 )
            iCoLand.info("Starting inactive deletion task...  "+now);
        
        for(Land land : lands) {
            Account acc = iConomy.getAccount(land.owner);
            Account bank = iConomy.getAccount(Config.bankName);
            double tax = Double.valueOf(iCoLand.df.format(land.location.volume()*Config.pricePerBlock.get("raw")*Config.taxRate));

            if ( iCoLand.landMgr.removeLandById(land.getID()) ) {

                // find if player is online
                int i = playerInList(players, land.owner);
                if ( i > -1 ) {
                    Messaging mess = new Messaging(players[i]);
                    mess.send("{}Land ID# {PRM}"+land.getID()+" {}deleted due to inactivity");
                } 
                
                Double price = Double.valueOf(iCoLand.df.format(land.getTotalPrice()));
                if ( !iCoLand.hasPermission(land.location.setLoc1.getWorld().getName(), land.owner, "admin.notax" ) ) {
                    acc.getHoldings().add(price-tax);
                    bank.getHoldings().subtract(price-tax);
                }

                i = playerInList(players, land.owner);
                if ( i >= 0 ) {
                    Messaging mess = new Messaging(players[i]);
                    mess.send("{}Land ID# {PRM}"+land.getID()+"{} sold for {PRM}"+(price-tax));
                } 
                
//                if ( Config.debugMode )
                    iCoLand.info("Land ID# "+land.getID()+" ("+land.owner+") sold for "+(price-tax));
                
            } else {
                iCoLand.info("Problem removing Land ID# "+land.getID());
            }
            
            

        }
                
                    
    }
    
    public int playerInList(Player[] players, String playerName) {
        for(int i=0;i<players.length;i++) {
            if ( players[i].getName().equals(playerName) ) {
                return i;
            }
        }
        return -1;
    }
}
