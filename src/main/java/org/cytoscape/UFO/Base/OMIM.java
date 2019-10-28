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
public class OMIM {
    public String Prefix;
    public String OMIMID;
    public String Title;
    public String GeneLoci;
    public String AltTitle;
    //public ArrayList<String> GeneID;
    public OMIM(){
        this.Prefix="";
        this.OMIMID="";
        this.Title="";
        this.GeneLoci="";
        this.AltTitle ="";
        //GeneID=new ArrayList<String>();
    }

    public OMIM(String Prefix, String OMIMID, String Title, String GeneLoci){
        this.Prefix=Prefix;
        this.OMIMID=OMIMID;
        this.Title=Title;
        this.GeneLoci=GeneLoci;
        //GeneID=new ArrayList<String>();
    }
}
