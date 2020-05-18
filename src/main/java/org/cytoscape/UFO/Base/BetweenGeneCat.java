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
public class BetweenGeneCat {
    public String BGCatID;
    public String BGCatName;

    public BetweenGeneCat(){
        this.BGCatID="";
        this.BGCatName="";
    }

    public BetweenGeneCat(String BGCatID, String BGCatName){
        this.BGCatID=BGCatID;
        this.BGCatName=BGCatName;
    }
}
