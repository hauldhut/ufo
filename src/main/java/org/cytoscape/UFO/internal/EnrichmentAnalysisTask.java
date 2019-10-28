package org.cytoscape.UFO.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author suvl_000
 */
class EnrichmentAnalysisTask implements Task{
    private volatile boolean interrupted = false;
    public EnrichmentAnalysisTask() {
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        
        taskMonitor.setTitle("Enrichment Analysis");
        taskMonitor.setProgress(1.0);
        try {
            taskMonitor.setStatusMessage("Analyzing Term Enrichment...!");
            
            if(this.interrupted==true) return;
            
            int i,j;
            String Method="";
            if(MainPanel.optHypergeometric.isSelected()){
                Method="Hypergeometric";
            }
                                   
            BasicData.Term2PvalueMap = Common.calculateSignificantEnrichmentTerms(BasicData.validSelObjectIDList, Method);
            
            //Calculate Adjusted Pvalues
            
            BasicData.Term2AdjustedPvalueMap = new TreeMap<String, Double>();
            //Holm method: 
//            ArrayList<Double> PvalueList = new ArrayList<Double>();
//            PvalueList.addAll(Term2PvalueMap.values());
//            Collections.sort(PvalueList);
//            for(Entry<String, Double> e: Term2PvalueMap.entrySet()){
//                int rank = Collections.binarySearch(PvalueList, e.getValue())+1;
//                Term2AdjustedPvalueMap.put(e.getKey(), 0.05/(Term2PvalueMap.size()-rank+1));
//            }
            
            //BenjaminiHochberg (FDR)
            //https://code.google.com/p/clusterviz-cytoscape/source/browse/trunk/src/clusterviz/BiNGO/BiNGO/BenjaminiHochbergFDR.java?r=33
            
            if(MainPanel.optBenjaminiHochberg.isSelected()){
                ArrayList<Double> PvalueList = new ArrayList<Double>();
                PvalueList.addAll(BasicData.Term2PvalueMap.values());
                Collections.sort(PvalueList);
                Map<String, Integer> Term2PvalueRankMap = new TreeMap<String, Integer>();
                Map<Integer, Double> Rank2PvalueMap = new TreeMap<Integer, Double>();
                int MaxRank=1;
                for(Map.Entry<String, Double> e: BasicData.Term2PvalueMap.entrySet()){
                    int rank = Collections.binarySearch(PvalueList, e.getValue())+1;
                    Rank2PvalueMap.put(rank, e.getValue());
                    Term2PvalueRankMap.put(e.getKey(), rank);
                    if(MaxRank<rank) MaxRank=rank;
                }
                //for(Entry<Integer, Double> e: Rank2PvalueMap.entrySet()){
                //    System.out.println(e.getKey() + "\t" + e.getValue());
                //}
                for(Map.Entry<String, Double> e: BasicData.Term2PvalueMap.entrySet()){
                    double adjPvalue=1.0;
                    int rank = Term2PvalueRankMap.get(e.getKey());
                    
                    adjPvalue = Rank2PvalueMap.get(rank)*MaxRank/rank;
                    if(adjPvalue>1.0) adjPvalue=1.0;
                    
//                    for(int k=rank;k<=MaxRank;k++){
//                        //System.out.println(rank + "\t" + k);
//                        if(Rank2PvalueMap.containsKey(k)){//ranks are not consecutive numbers
//                            double adjPvalueTemp = ((double)MaxRank/k)*Rank2PvalueMap.get(k);
//                            if(adjPvalueTemp>1.0) adjPvalueTemp=1.0;
//                            if(adjPvalueTemp<adjPvalue)adjPvalue = adjPvalueTemp;
//                        }
//                    }
                    BasicData.Term2AdjustedPvalueMap.put(e.getKey(), adjPvalue);
                }
            }
            //Bonferroni (FWER)
            //https://code.google.com/p/clusterviz-cytoscape/source/browse/trunk/src/clusterviz/BiNGO/BiNGO/Bonferroni.java?r=33
            if(MainPanel.optBonferroni.isSelected()){
                for(Map.Entry<String, Double> e: BasicData.Term2PvalueMap.entrySet()){
                    double adjPvalue = BasicData.Term2PvalueMap.size()*e.getValue();
                    if(adjPvalue>1.0) adjPvalue=1.0;
                    BasicData.Term2AdjustedPvalueMap.put(e.getKey(), adjPvalue);
                }
            }
             
            MainPanel.fillTermEnrichmentTable(BasicData.TermID2NameMap, BasicData.Term2PvalueMap, BasicData.Term2AdjustedPvalueMap, MainPanel.tblTermEnrichment);
            
        } catch (Exception e) {
            this.interrupted = true;
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        taskMonitor.setProgress(1.0);
    }

    @Override
    public void cancel() {
        System.out.println("Task cancel called");
        this.interrupted = true;
    }
    
}
