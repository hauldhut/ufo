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
public class CoExpression {
    public String EntrezID;
    public double MutualRank;
    public double PearsonCC;

    public CoExpression(){
        this.EntrezID="";
        this.MutualRank=-1000000.0;
        this.PearsonCC=-2000000.0;
    }

    public CoExpression(String EntrezID, double MutualRank, double PearsonCC){
        this.EntrezID=EntrezID;
        this.MutualRank=MutualRank;
        this.PearsonCC=PearsonCC;
    }
}
