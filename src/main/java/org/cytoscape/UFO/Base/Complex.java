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
public class Complex {
    public String ComplexID;
    public String ComplexName;
    public String Synonyms;
    public String Organism;
    public String Subunits_UniProtACs;
    public String Subunits_EntrezIDs;
    public ArrayList<String> Genes;//EntrezID
    public String PurificationMethod;
    public String PubMedIDs;
    public String FunCategories;
    public String FunctionalComment;
    public String DiseaseComment;
    public String SubunitComment;

    public Complex(){
        this.ComplexID = "";
        this.ComplexName = "";
        this.Synonyms = "";
        this.Organism = "";
        this.Subunits_UniProtACs = "";
        this.Subunits_EntrezIDs = "";
        this.PurificationMethod = "";
        this.PubMedIDs = "";
        this.FunCategories = "";
        this.FunctionalComment ="";
        this.DiseaseComment = "";
        this.SubunitComment = "";
        this.Genes = new ArrayList<String>();
    }
}
