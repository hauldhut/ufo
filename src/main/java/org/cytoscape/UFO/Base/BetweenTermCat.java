/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cytoscape.UFO.Base;

/**
 *
 * @author "MinhDA"
 */
public class BetweenTermCat {
    public String BTCatID;
    public String BTCatName;

    public BetweenTermCat(){
        this.BTCatID="";
        this.BTCatName="";
    }

    public BetweenTermCat(String BTCatID, String BTCatName){
        this.BTCatID=BTCatID;
        this.BTCatName=BTCatName;
    }
}
