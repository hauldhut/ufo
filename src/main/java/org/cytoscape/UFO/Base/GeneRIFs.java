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
public class GeneRIFs {
    public int TaxID;
    public String EntrezID;
    public String PubMedID;
    public String OfficialSymbol;
    public String GeneRIFsText;

    public GeneRIFs(){
        this.TaxID=-1;
        this.EntrezID="";
        this.PubMedID="";
        this.OfficialSymbol="";
        this.GeneRIFsText="";
    }
}
