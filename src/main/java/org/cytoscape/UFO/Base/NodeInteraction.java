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
public class NodeInteraction {
    public String Node;
    public int InaType;
    public String InaTypeOriginal;
    public int State;
    public double Weight;

    public NodeInteraction(){
        this.Node="";
        this.InaType=0;
        this.Weight=0.0;
        InaTypeOriginal="";
    }

    public NodeInteraction(String Node, int InaType, double Weight, String InaTypeOriginal){
        this.Node=Node;
        this.InaType=InaType;
        this.Weight=Weight;
        this.InaTypeOriginal = InaTypeOriginal;
    }

    public NodeInteraction(String Node, int State, int InaType){
        this.Node=Node;
        this.State=State;
        this.InaType=InaType;
        this.Weight=0.0;
    }
}
