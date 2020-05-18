
package org.cytoscape.UFO.Base;

/**
 *
 * @author "MinhDA"
 */
public class Interaction {
    public String Index;//Common field to store field by which Interaction list will be sorted
    public String NodeSrc;
    public String NodeDst;
    public String TypeOriginal;
    public double PearsonCC;
    public double Weight;
    public double WeightOriginal;
    public int Type;
    public Interaction(){
        this.TypeOriginal="";
        this.NodeSrc="";
        this.NodeDst="";
        this.Weight=0;
        this.PearsonCC=0.0;
        this.WeightOriginal=0;
        this.Type =0;
    }
    public Interaction(String nodesrc, String nodedst){
        this.NodeSrc=nodesrc;
        this.NodeDst=nodedst;
        this.TypeOriginal="";
        this.Weight=1.0;
        this.PearsonCC=0.0;
        this.WeightOriginal=1.0;
    }
    public Interaction(String nodesrc, String nodedst, double Weight){
        this.NodeSrc=nodesrc;
        this.NodeDst=nodedst;
        this.TypeOriginal="";
        this.Weight=Weight;
        this.PearsonCC=0.0;
        this.WeightOriginal=Weight;
    }
}
