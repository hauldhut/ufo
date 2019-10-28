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
public class Corpus {
    public String Name;
    public String FullName;
    public String Website;

    public Corpus(){
        this.Name="";
        this.FullName="";
        this.Website="";
    }

    public Corpus(String Name, String FullName, String Website){
        this.Name=Name;
        this.FullName=FullName;
        this.Website=Website;
    }
}
