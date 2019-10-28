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
public class PubMed {
    public String ID;
    public String PubDate;
    public String FullJournalName;
    public String Title;
    public String DOI;
    public String Abstract;

    public PubMed(){
        this.ID="";
        this.PubDate="";
        this.FullJournalName="";
        this.Title="";
        this.DOI="";
        this.Abstract="";
    }
}
