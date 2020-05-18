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
public class Species {
    public int Tax_id;
    public String ScientificName;
    public String CommonName;
    public Species(){
        this.Tax_id=-1;
        this.ScientificName="";
        this.CommonName="";
    }

    public Species(int Tax_id, String ScientificName){
        this.Tax_id=Tax_id;
        this.ScientificName=ScientificName;
    }
}
