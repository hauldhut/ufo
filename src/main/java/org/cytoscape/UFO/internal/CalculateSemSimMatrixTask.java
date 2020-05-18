/*
 * MinhDA - 20160404 - ADD
 */
package org.cytoscape.UFO.internal;

import java.util.Map;
import java.util.TreeMap;
import static org.cytoscape.UFO.internal.MainPanel.tblSemSimMatrix;
import static org.cytoscape.UFO.internal.MainPanel.fillSimTable;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author "MinhDA"
 */
class CalculateSemSimMatrixTask implements Task {
    
    public static boolean Error=false;
    private volatile boolean interrupted = false;
    
    public static Map<String,Map<String, Double>> SimMatrix = new TreeMap<String, Map<String, Double>>(); 
    
    public CalculateSemSimMatrixTask() {

    }
    
    public String getTitle() {
        return "Calculate Semantic Similarity Matrix";
    }
    
    public void run(TaskMonitor taskMonitor) {
        taskMonitor.setTitle("Calculate Semantic Similarity Matrix");
        taskMonitor.setProgress(0.1);
        
        try{
            taskMonitor.setStatusMessage("Calculating Semantic Similarity Matrix...!");
            
            double TermSim = 0.0;
            int i,j;
            
            String TermSimSubMet = (String) MainPanel.cboTermSimSubMet.getSelectedItem();
            System.out.println("TermSimSubMet: " + TermSimSubMet);

            SimMatrix = new TreeMap<String, Map<String, Double>>();       
            for(i=0;i<BasicData.validSelTermIDList.size();i++){
                String Term1ID = BasicData.validSelTermIDList.get(i);
                Map<String, Double> SimMap = new TreeMap<String, Double>();
                for(j=0;j<BasicData.validSelTermIDList.size();j++){

                    String Term2ID = BasicData.validSelTermIDList.get(j);

                    taskMonitor.setStatusMessage("Calculating Semantic Similarity between " + Term1ID + " and " + Term2ID + " ...!");
                    if(this.interrupted==true) return;
                    
                    if (TermSimSubMet.contains("Resnik")) {
                        if (TermSimSubMet.contains("GraSM")) {
                            TermSim = Common.calculateTermSimilarity_ResnikGraSM_Couto2005(Term1ID, Term2ID, BasicData.Term2ICMap, BasicData.ParentNodeMap);
                        } else {
                            TermSim = Common.calculateTermSimilarity_Resnik_Resnik1995(Term1ID, Term2ID, BasicData.Term2ICMap, BasicData.ParentNodeMap);
                        }
                    } else if (TermSimSubMet.contains("Lin")) {
                        if (TermSimSubMet.contains("GraSM")) {
                            TermSim = Common.calculateTermSimilarity_LinGraSM_Couto2005(Term1ID, Term2ID, BasicData.Term2ICMap, BasicData.ParentNodeMap);
                        } else {
                            TermSim = Common.calculateTermSimilarity_Lin_Lin1998(Term1ID, Term2ID, BasicData.Term2ICMap, BasicData.ParentNodeMap);
                        }
                    } else if (TermSimSubMet.contains("JC")) {
                        if (TermSimSubMet.contains("GraSM")) {
                            TermSim = Common.calculateTermSimilarity_JCGraSM_Couto2005(Term1ID, Term2ID, BasicData.Term2ICMap, BasicData.ParentNodeMap);
                        } else {
                            TermSim = Common.calculateTermSimilarity_JC_JiangNConrath1997(Term1ID, Term2ID, BasicData.Term2ICMap, BasicData.ParentNodeMap);
                        }
                    } else if (TermSimSubMet.contains("Rel")) {
                        if (TermSimSubMet.contains("GraSM")) {
                            TermSim = Common.calculateTermSimilarity_RelGraSM_Couto2005(Term1ID, Term2ID, BasicData.Term2ICMap, BasicData.ParentNodeMap);
                        } else {
                            TermSim = Common.calculateTermSimilarity_Rel_Schlicker2006(Term1ID, Term2ID, BasicData.Term2ICMap, BasicData.ParentNodeMap);
                        }
                    } else if (TermSimSubMet.contains("Wang")) {
                        TermSim = Common.calculateTermSimilarity_Wang2007(Term1ID, Term2ID, BasicData.RootTermID, BasicData.ParentNodeMap, BasicData.ChildNodeMap);
                    } else if (TermSimSubMet.contains("Yu")) {
                        TermSim = Common.calculateTermSimilarity_simSP_Yu2005(Term1ID, Term2ID, BasicData.RootTermID, BasicData.ParentNodeMap);
                    } else if (TermSimSubMet.contains("Wu")) {
                        TermSim = Common.calculateTermSimilarity_Wu2005(Term1ID, Term2ID, BasicData.RootTermID, BasicData.ParentNodeMap);
                    } else {
                    }
                    SimMap.put(Term2ID, TermSim);

                }
                SimMatrix.put(Term1ID, SimMap);
            }

            fillSimTable(SimMatrix, tblSemSimMatrix);

        }catch(Exception e){
            //JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Error while reading current network: " + e.toString());
            //JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Error: The values of second column that you enter as Weight are not a numberic value...!","Notice",JOptionPane.WARNING_MESSAGE);
            //this.interrupted=true;
            this.interrupted = true;
            this.Error=true;
        }
        
        taskMonitor.setProgress(1.0);

    }

    @Override
    public void cancel() {
        System.out.println("Task cancel called");
        this.interrupted=true;
        this.interrupted = true;
    }
}
