package org.cytoscape.UFO.internal;

import java.util.Map;
import java.util.TreeMap;
import static org.cytoscape.UFO.internal.MainPanel.fillSimTable;
import static org.cytoscape.UFO.internal.MainPanel.tblFunSimMatrix;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author suvl_000
 */
public class CalculateFunSimMatrixTask implements Task{
    private volatile boolean interrupted = false;
    public static boolean Error=false;
    public static Map<String,Map<String, Double>> SimMatrix = new TreeMap<String, Map<String, Double>>();
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Calculate Functional Similarity Matrix");
        taskMonitor.setProgress(0.1);
        try{
            taskMonitor.setStatusMessage("Calculating Functional Similarity Matrix...!");
            int i,j;
            String TermSimSubMet = (String) MainPanel.cboTermSimSubMet.getSelectedItem();
            String ObjectSimSubMet = (String) MainPanel.cboObjSimSubMet.getSelectedItem();
            System.out.println("TermSimSubMet: " + TermSimSubMet);
            System.out.println("ObjectSimSubMet: " + ObjectSimSubMet); 
            
            double max=0.0;
            double min=1.0;
            SimMatrix = new TreeMap<String, Map<String, Double>>();       
            for(i=0;i<BasicData.validSelObjectIDList.size();i++){
                String Object1ID = BasicData.validSelObjectIDList.get(i);
                Map<String, Double> SimMap = new TreeMap<String, Double>();
                for(j=0;j<BasicData.validSelObjectIDList.size();j++){
                    
                    double ObjectSim = 0.0;
                    String Object2ID = BasicData.validSelObjectIDList.get(j);
                    
                    taskMonitor.setStatusMessage("Calculating Functional Similarity between " + Object1ID + " and " + Object2ID + " ...!");
                    if(this.interrupted==true) return;
                    
                    if(Object1ID.compareTo(Object2ID)==0){
                        ObjectSim =1;
                    }else if(SimMatrix.containsKey(Object2ID)){
                        ObjectSim = SimMatrix.get(Object2ID).get(Object1ID);
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
                            ObjectSim = Common.calculateObjectSimilarity_Pairwise(Object1ID, Object2ID, BasicData.RootTermID, BasicData.Object2TermMap, BasicData.Term2ICMap, BasicData.ParentNodeMap,BasicData.ChildNodeMap, TermSimSubMet, ObjSimMet);
                        } else if (ObjectSimSubMet.contains("Term Overlap (TO)")) {
                            ObjectSim = Common.calculateObjectSimilarity_Groupwise_TermOverlap_Lee2004_Mistry2008(Object1ID, Object2ID, BasicData.Object2TermMap, BasicData.ParentNodeMap, false);
                        } else if (ObjectSimSubMet.contains("Normalized Term Overlap (NTO)")) {
                            ObjectSim = Common.calculateObjectSimilarity_Groupwise_TermOverlap_Lee2004_Mistry2008(Object1ID, Object2ID, BasicData.Object2TermMap, BasicData.ParentNodeMap, true);
                        } else if (ObjectSimSubMet.contains("UI")) {
                            ObjectSim = Common.calculateObjectSimilarity_Groupwise_simUI_Gentleman2005(Object1ID, Object2ID, BasicData.Object2TermMap, BasicData.ParentNodeMap);
                        } else if (ObjectSimSubMet.contains("GIC")) {
                            ObjectSim = Common.calculateObjectSimilarity_Groupwise_simGIC_Pesquita2007(Object1ID, Object2ID, BasicData.Object2TermMap, BasicData.Term2ICMap, BasicData.ParentNodeMap);
                        } else if (ObjectSimSubMet.contains("LP")) {
                            ObjectSim = Common.calculateObjectSimilarity_Groupwise_simLP_Gentleman2005(Object1ID, Object2ID, BasicData.RootTermID, BasicData.Object2TermMap, BasicData.ParentNodeMap);
                        } else if (ObjectSimSubMet.contains("Huang")) {
                            ObjectSim = Common.calculateObjectSimilarity_Groupwise_Kappa_Huang2007(Object1ID, Object2ID, BasicData.Object2TermMap);
                        } else if (ObjectSimSubMet.contains("Chabalier")) {
                            ObjectSim = Common.calculateObjectSimilarity_Groupwise_CoSine_Chabalier2007(Object1ID, Object2ID, BasicData.Object2TermMap);
                        }
                    }
                    SimMap.put(Object2ID, ObjectSim);
                    if(ObjectSim<min) min = ObjectSim;
                    if(ObjectSim>max) max = ObjectSim;
                }
                SimMatrix.put(Object1ID, SimMap);
            }
            fillSimTable(SimMatrix, tblFunSimMatrix);
            MainPanel.txtMaxFunSim.setText(String.valueOf(max));
            MainPanel.txtMinFunSim.setText(String.valueOf(min));
            
            MainPanel.txtMaxFunSim.setToolTipText("Set a value not greater than " + String.valueOf(max));
            MainPanel.txtMinFunSim.setToolTipText("Set a value not less than " + String.valueOf(min));
        }catch(Exception e){
            //JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Error while reading current network: " + e.toString());
            //JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Error: The values of second column that you enter as Weight are not a numberic value...!","Notice",JOptionPane.WARNING_MESSAGE);
            //this.interrupted=true;
            e.printStackTrace();
            this.Error=true;
            this.interrupted = true;
        }
        taskMonitor.setProgress(1.0);
    }

    @Override
    public void cancel() {
        System.out.println("Task cancel called");
        this.interrupted = true;
    }
    
}
