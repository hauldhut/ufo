
package org.cytoscape.UFO.internal;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author suvl_000
 */
class CalculateFunSimBetweenObjectSetTask implements Task{
    private boolean interrupted = false;

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Calculate Functional Similarity between Entity Sets");
        try{
            taskMonitor.setStatusMessage("Analyzing term enrichment of two Entity Sets...!");
            if(this.interrupted==true) return;
            
            int i,j;
            String TermSimSubMet = (String) MainPanel.cboTermSimSubMet.getSelectedItem();
            String ObjectSimSubMet = (String) MainPanel.cboObjSimSubMet.getSelectedItem();
            System.out.println("TermSimSubMet: " + TermSimSubMet);
            System.out.println("ObjectSimSubMet: " + ObjectSimSubMet); 
            
            
            String Object1ID = "1";//MetaObject 1
            String Object2ID = "2";//MetaObject 2
                        
            Map<String, Set<String>> Object2TermMap = new TreeMap<String, Set<String>>();//MetaObject
            if(BasicData.SignificantTermSet1.size()>0 && BasicData.SignificantTermSet2.size()>0){
                Object2TermMap.put(Object1ID, BasicData.SignificantTermSet1);
                Object2TermMap.put(Object2ID, BasicData.SignificantTermSet2);
            }
            
            

            taskMonitor.setStatusMessage("Calculating Functional Similarity between two Entity Sets ...!");
            if(this.interrupted==true) return;

            if(BasicData.validSelObject1IDList.containsAll(BasicData.validSelObject2IDList) && BasicData.validSelObject2IDList.containsAll(BasicData.validSelObject1IDList)){
                BasicData.ObjectSetSim =1;
            }else{

                if (ObjectSimSubMet.contains("Pairwise-Based: Avg") || ObjectSimSubMet.contains("Pairwise-Based: Max") || ObjectSimSubMet.contains("Pairwise-Based: BMA") || ObjectSimSubMet.contains("Pairwise-Based: RCmax")) {
                    String ObjSimMet = "";
                    if (ObjectSimSubMet.contains("Pairwise-Based: Avg")) {
                        ObjSimMet = "Avg";
                    } else if (ObjectSimSubMet.contains("Pairwise-Based: Max")) {
                        ObjSimMet = "Max";
                    } else if (ObjectSimSubMet.contains("Pairwise-Based: BMA")) {
                        ObjSimMet = "BMA";
                    } else {
                        ObjSimMet = "RCmax";
                    }
                    BasicData.ObjectSetSim = Common.calculateObjectSimilarity_Pairwise(Object1ID, Object2ID, BasicData.RootTermID, Object2TermMap, BasicData.Term2ICMap, BasicData.ParentNodeMap,BasicData.ChildNodeMap, TermSimSubMet, ObjSimMet);
                } else if (ObjectSimSubMet.contains("Term Overlap (TO)")) {
                    BasicData.ObjectSetSim = Common.calculateObjectSimilarity_Groupwise_TermOverlap_Lee2004_Mistry2008(Object1ID, Object2ID, Object2TermMap, BasicData.ParentNodeMap, false);
                } else if (ObjectSimSubMet.contains("Normalized Term Overlap (NTO)")) {
                    BasicData.ObjectSetSim = Common.calculateObjectSimilarity_Groupwise_TermOverlap_Lee2004_Mistry2008(Object1ID, Object2ID, Object2TermMap, BasicData.ParentNodeMap, true);
                } else if (ObjectSimSubMet.contains("UI")) {
                    BasicData.ObjectSetSim = Common.calculateObjectSimilarity_Groupwise_simUI_Gentleman2005(Object1ID, Object2ID, Object2TermMap, BasicData.ParentNodeMap);
                } else if (ObjectSimSubMet.contains("GIC")) {
                    BasicData.ObjectSetSim = Common.calculateObjectSimilarity_Groupwise_simGIC_Pesquita2007(Object1ID, Object2ID, Object2TermMap, BasicData.Term2ICMap, BasicData.ParentNodeMap);
                } else if (ObjectSimSubMet.contains("LP")) {
                    BasicData.ObjectSetSim = Common.calculateObjectSimilarity_Groupwise_simLP_Gentleman2005(Object1ID, Object2ID, BasicData.RootTermID, Object2TermMap, BasicData.ParentNodeMap);
                } else if (ObjectSimSubMet.contains("Huang")) {
                    BasicData.ObjectSetSim = Common.calculateObjectSimilarity_Groupwise_Kappa_Huang2007(Object1ID, Object2ID, Object2TermMap);
                } else if (ObjectSimSubMet.contains("Chabalier")) {
                    BasicData.ObjectSetSim = Common.calculateObjectSimilarity_Groupwise_CoSine_Chabalier2007(Object1ID, Object2ID, Object2TermMap);
                }
            }
                        
            

        }catch(Exception e){
            e.printStackTrace();
            
        }
    }

    @Override
    public void cancel() {
        this.interrupted = true;
    }
    
}
