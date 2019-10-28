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
public class BetweenGeneMet {
    public String BGMetID;
    public String BGMetName;

    public BetweenGeneMet() {
        this.BGMetID="";
        this.BGMetName="";
    }

    public BetweenGeneMet(String BGMetID, String BGMetName) {
        this.BGMetID=BGMetID;
        this.BGMetName=BGMetName;
    }
}
