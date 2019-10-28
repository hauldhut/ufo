/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cytoscape.UFO.Base;

import java.util.ArrayList;

/**
 *
 * @author "MinhDA"
 */
public class GO {
    public String EntrezID;
    public String UniProtAC;
    public String OfficialSymbol;
    public String Taxon;
    public String GOID;
    public String GOName;
    public String Category;
    public String Evidence;
    public String Index;
    public int Distance;//Positive: ancestors, Negative: descendants
    public ArrayList<GO> RelatedGOs;

    public GO(){
        this.Index="";
        this.EntrezID="";
        this.UniProtAC="";
        this.OfficialSymbol="";
        this.Taxon="";
        this.GOID="";
        this.GOName="";
        this.Category="";
        this.Evidence="";
        RelatedGOs= new ArrayList<GO>();
        this.Distance=0;
    }

    public GO(String EntrezID,String UniProtAC,String OfficialSymbol,String Taxon,String GOID, String GOName,String Category,String Evidence){
        this.Index="";
        this.EntrezID=EntrezID;
        this.UniProtAC=UniProtAC;
        this.OfficialSymbol=OfficialSymbol;
        this.Taxon=Taxon;
        this.GOID=GOID;
        this.GOName=GOName;
        this.Category=Category;
        this.Evidence=Evidence;
        RelatedGOs= new ArrayList<GO>();
        this.Distance=0;
    }
}
