/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cytoscape.UFO.internal;

import org.cytoscape.UFO.Base.Interaction;
import org.cytoscape.UFO.Base.NodeInteraction;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author Administrator
 */
public class CalculateDOTermSimilarity_NonICBased_SingleMode {
    public static void main(String[] args) {
        try{
            int i,j;
        
            String DODAGFileName = "C:\\Java\\Ontology_TextVersion\\Data\\DO\\Peng2012\\HumanDO.obo_DODAG.txt";
            String DOFileName = "C:\\Java\\Ontology_TextVersion\\Data\\DO\\Peng2012\\HumanDO.obo";
            String RootTerm = "DOID:4";
            //ArrayList<Interaction> DODAG = Common.loadDAG(DODAGFileName);
            ArrayList<Interaction> DODAG = Common.loadOntologyData(DOFileName);
            
            
            System.out.println(DODAG.size());
    //        for(i=0;i<DODAG.size();i++){
    //            System.out.println(DODAG.get(i).NodeSrc + "\t" + DODAG.get(i).Weight + "\t" + DODAG.get(i).NodeDst);
    //        }

            Map<String, ArrayList<NodeInteraction>> ParentNodeMap = Common.calculateOutgoingNeighbors(DODAG);
            System.out.println(ParentNodeMap.size());
            
            Map<String, ArrayList<NodeInteraction>> ChildNodeMap = Common.calculateIncomingNeighbors(DODAG);
            System.out.println("ChildNodeMap.keySet(): " + ChildNodeMap.keySet().toString());
            System.out.println("ChildNodeMap.size(): " + ChildNodeMap.size());
    
            System.out.println(ParentNodeMap.containsKey(RootTerm) + "\t" + ChildNodeMap.containsKey(RootTerm));

            
            String t1 ="DOID:0050770";
            String t2 ="DOID:11665";
                        
            double TermSimWu2005=0.0;
            double TermSimYu2005=0.0;
            double TermSimWang2007=0.0;
            TermSimWu2005 = Common.calculateTermSimilarity_Wu2005(t1, t2, RootTerm, ParentNodeMap);
            TermSimYu2005 = Common.calculateTermSimilarity_simSP_Yu2005(t1, t2, RootTerm, ParentNodeMap);
            TermSimWang2007 = Common.calculateTermSimilarity_Wang2007(t1, t2, RootTerm, ParentNodeMap, ChildNodeMap);

            System.out.println(t1 + "\t" + TermSimWu2005 + "\t" + TermSimYu2005 + "\t" + TermSimWang2007 + "\t" + t2);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
