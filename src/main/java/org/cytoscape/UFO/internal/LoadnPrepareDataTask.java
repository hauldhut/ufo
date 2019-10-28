package org.cytoscape.UFO.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.cytoscape.UFO.Base.Interaction;
import org.cytoscape.UFO.Base.NodeInteraction;
import org.cytoscape.UFO.Base.Term;
import org.cytoscape.UFO.Base.TermIDEvidenceID;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author suvl_000
 */
class LoadnPrepareDataTask implements ObservableTask {
    
    public static boolean Error=false;
    private volatile boolean interrupted = false;
    
    public LoadnPrepareDataTask() {
    }
    
    public String getTitle() {
        return "Load & Prepare data";
    }

    @Override
    public void run(TaskMonitor taskMonitor) {
        // Give the task a title.
        taskMonitor.setTitle("Load & Prepare data");
        taskMonitor.setProgress(0.1);
        
        try{
            BasicData.DAG = new ArrayList<>();
            BasicData.DAGNodeSet = new TreeSet<>();
            BasicData.Object2TermMap = new TreeMap<>();
            BasicData.Term2ObjectMap = new TreeMap<>();
            BasicData.TermID2InfoMap = new TreeMap<>();
            BasicData.TermID2NameMap = new TreeMap<>();
            BasicData.Term2ICMap = new TreeMap<>();
            BasicData.ObjectID2NameMap = new TreeMap<>();
            
            //Load Ontology data
            taskMonitor.setStatusMessage("Loading Ontology data...!");
            if(this.interrupted==true) return;
            BasicData.Ontology_FileName = MainPanel.txtOntologyFile.getText();
            BasicData.DAG = Common.loadOntologyData(BasicData.Ontology_FileName);
            
            System.out.println("Before: ");
            System.out.println("--------");
            System.out.println("BasicData.DAG.size(): " + BasicData.DAG.size());
            System.out.println("BasicData.DAGNodeSet.size(): " + BasicData.DAGNodeSet.size());
            System.out.println("BasicData.TermID2InfoMap.size(): " + BasicData.TermID2InfoMap.size());
            
             //Find Relationship Maps
            BasicData.ChildNodeMap = Common.calculateIncomingNeighbors(BasicData.DAG);
            BasicData.ParentNodeMap = Common.calculateOutgoingNeighbors(BasicData.DAG);
            
            System.out.println("BasicData.ParentNodeMap.size(): " + BasicData.ParentNodeMap.size());
            System.out.println("BasicData.ChildNodeMap.size(): " + BasicData.ChildNodeMap.size());
            
            //Load Annotation data
            taskMonitor.setStatusMessage("Loading Annotation data...!");
            if(this.interrupted==true) return;
            
            if(MainPanel.cboAnnotationType.getSelectedItem().toString().compareTo("Unavailable Annotation data")==0){
                for(Map.Entry<String, Term> e: BasicData.TermID2InfoMap.entrySet()){
                    BasicData.TermID2NameMap.put(e.getKey(), e.getValue().Name);
                    BasicData.Term2ICMap.put(e.getKey(), Double.NaN);
                    BasicData.MaxIC = Double.NaN;
                    BasicData.MinIC = Double.NaN;
                    //BasicData.Term2ObjectMap.put(e.getKey(), new TreeSet<String>());
                }
                MainPanel.fillTermInputTable(BasicData.TermID2NameMap, BasicData.Term2ICMap, BasicData.Term2ObjectMap, MainPanel.tblSelectedTerm);
                MainPanel.lblTermInputStatus.setText("Total of " + BasicData.TermID2NameMap.size());
                
                
                /*
                Node-Based
                Edge-Based
                Hybrid-Based
                */
                //MainPanel.cboTermSimMet.removeAllItems();
                MainPanel.cboTermSimMet.removeItemAt(0);
                //MainPanel.cboTermSimMet.addItem("Edge-Based");
                //MainPanel.cboTermSimMet.addItem("Hybrid-Based");
                MainPanel.chkAnnotatedObject.setEnabled(false);
                MainPanel.tPnlEntity.setEnabled(false);
                MainPanel.pnlEntitySets.setEnabled(false);
                
                for(Map.Entry<String, ArrayList<NodeInteraction>> e: BasicData.ParentNodeMap.entrySet()){
                    if(e.getValue().isEmpty()){
                        BasicData.RootTermID = e.getKey();
                        break;
                    }
                }
                System.out.println("BasicData.RootTermID: " + BasicData.RootTermID);
                return;
            }else{
                //MainPanel.cboTermSimMet.removeAllItems();
                MainPanel.cboTermSimMet.insertItemAt("Node-Based", 0);
                //MainPanel.cboTermSimMet.addItem("Node-Based");
                //MainPanel.cboTermSimMet.addItem("Edge-Based");
                //MainPanel.cboTermSimMet.addItem("Hybrid-Based");
                MainPanel.chkAnnotatedObject.setEnabled(true);
                MainPanel.tPnlEntity.setEnabled(true);
                MainPanel.pnlEntitySets.setEnabled(true);
            }
            BasicData.Annotation_FileName = MainPanel.txtAnnotationFile.getText();
            Map<String, ArrayList<TermIDEvidenceID>> AnnotationData = Common.loadAnnotationData(BasicData.Annotation_FileName);
            
            //Collect Evidence code
            int i,j;
            Set<String> selEvidenceSet = new TreeSet<>();
            Set<String> AllEvidenceSet = new TreeSet<>();
            
            for(i=0;i<MainPanel.tblEvidence.getRowCount();i++){
                for(j=0;j<MainPanel.tblEvidence.getColumnCount();j++){
                    if((j%2)==0 && Boolean.parseBoolean(MainPanel.tblEvidence.getValueAt(i, j).toString())==true){
                        selEvidenceSet.add(MainPanel.tblEvidence.getValueAt(i, j+1).toString());
                    }
                    if(j%2!=0){
                        AllEvidenceSet.add(MainPanel.tblEvidence.getValueAt(i, j).toString());
                    }
                }
            }
            if(selEvidenceSet.contains("All")){
                selEvidenceSet.addAll(AllEvidenceSet);
                selEvidenceSet.remove("All");
            }
            System.out.println(selEvidenceSet.size() + "\t" + selEvidenceSet.toString());
            //Extract Object2TermMap from Annotation data
            BasicData.Object2TermMap = new TreeMap<>();
            Set<String> AnnotatedTermIDSet = new TreeSet<>();
            for(Map.Entry<String, ArrayList<TermIDEvidenceID>> e: AnnotationData.entrySet()){
                Set<String> AnnTermIDSet = new TreeSet<String>();
                for(i=0;i<e.getValue().size();i++){
                    if(e.getValue().get(i).EvidenceID.isEmpty() || selEvidenceSet.contains(e.getValue().get(i).EvidenceID)){
                        AnnTermIDSet.add(e.getValue().get(i).TermID);
                    }
                }
                AnnotatedTermIDSet.addAll(AnnTermIDSet);
                BasicData.Object2TermMap.put(e.getKey(), AnnTermIDSet);
            }
            System.out.println("BasicData.Object2TermMap.size(): " + BasicData.Object2TermMap.size());
            System.out.println("AnnotatedTermIDSet.size(): " + AnnotatedTermIDSet.size());
            
            //Determine Ontoloty Type
            if(this.interrupted==true) return;
            
            
            String Type="";
            
            if(AnnotatedTermIDSet.contains("GO:0008150")){
                Type="biological_process";
            }else if(AnnotatedTermIDSet.contains("GO:0005575")){
                Type="cellular_component";
            }else if(AnnotatedTermIDSet.contains("GO:0003674")){
                Type="molecular_function";
            } 
            
            //Extract TermID2NameMap from Annotated Terms (TermID2InfoMap)
            BasicData.TermID2NameMap = new TreeMap<String, String>();
            if(!Type.isEmpty()){
                for(Map.Entry<String, Term> e: BasicData.TermID2InfoMap.entrySet()){
                    if(e.getValue().Type.compareTo(Type)==0){
                        BasicData.TermID2NameMap.put(e.getKey(), e.getValue().Name);
                    }
                }
            }else{
                for(Map.Entry<String, Term> e: BasicData.TermID2InfoMap.entrySet()){
                    BasicData.TermID2NameMap.put(e.getKey(), e.getValue().Name);
                }
            }
            
            //Recalculate DAG and DAGNodeSet, an intersection with terms in TermID2NameMap
            BasicData.DAGNodeSet = new TreeSet<String>();
            ArrayList<Interaction> InvalidInaList = new ArrayList<Interaction>();
            for(i=0; i<BasicData.DAG.size();i++){
                //System.out.println(BasicData.DAG.get(i).NodeSrc + "\t" + BasicData.DAG.get(i).TypeOriginal + "\t" + BasicData.DAG.get(i).NodeDst);
                if(BasicData.TermID2NameMap.keySet().contains(BasicData.DAG.get(i).NodeSrc) && BasicData.TermID2NameMap.keySet().contains(BasicData.DAG.get(i).NodeDst)){
                    BasicData.DAGNodeSet.add(BasicData.DAG.get(i).NodeSrc);
                    BasicData.DAGNodeSet.add(BasicData.DAG.get(i).NodeDst);
                }else{
                    InvalidInaList.add(BasicData.DAG.get(i));
                }
            }
            BasicData.DAG.removeAll(InvalidInaList);
            
            for(Iterator<Map.Entry<String, String>> it = BasicData.TermID2NameMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, String> e = it.next();
                if(!BasicData.DAGNodeSet.contains(e.getKey())) {
                    it.remove();
                }
            }
            
            //Recalculate Object2TermMap, An object is valid if its annotating terms are in TermID2NameMap
            Set<String> InvalidOjectSet = new TreeSet<String>();
            for(Map.Entry<String, Set<String>> e: BasicData.Object2TermMap.entrySet()){
                //System.out.println(ObjectID + "\t" + BasicData.Object2TermMap.get(ObjectID).toString());
                Set<String> ValidTermIDSet =e.getValue();
                ValidTermIDSet.retainAll(BasicData.TermID2NameMap.keySet());
                if(ValidTermIDSet.isEmpty()){
                    //BasicData.Object2TermMap.remove(e.getKey());
                    InvalidOjectSet.add(e.getKey());
                }else{
                    BasicData.Object2TermMap.put(e.getKey(), ValidTermIDSet);
                }
            }
            
            for(Iterator<Map.Entry<String, Set<String>>> it = BasicData.Object2TermMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Set<String>> e = it.next();
                if(InvalidOjectSet.contains(e.getKey())) {
                    it.remove();
                }
            }
            //Calculate Term2ObjectMap from Object2TermMap
            BasicData.Term2ObjectMap = new TreeMap<String, Set<String>>();
            for (Map.Entry<String, Set<String>> e : BasicData.Object2TermMap.entrySet()) {
                String ObjID = e.getKey();
                
                for (String TermID : e.getValue()) {
                    if (BasicData.Term2ObjectMap.containsKey(TermID)) {
                        BasicData.Term2ObjectMap.get(TermID).add(ObjID);
                    } else {
                        Set<String> ObjIDSet = new TreeSet<String>();
                        ObjIDSet.add(ObjID);
                        BasicData.Term2ObjectMap.put(TermID, ObjIDSet);
                    }
                }
            }
            if(this.interrupted==true) return;
            //Find Object name
            taskMonitor.setStatusMessage("Loading name for " + BasicData.Object2TermMap.size() + " object (Gene, OMIM)...!");
            
            Map<String, String> AllObjectID2NameMap = new TreeMap<String, String>();
            if (AnnotatedTermIDSet.toString().contains("GO")||AnnotatedTermIDSet.toString().contains("DOID")) {//Gene
                AllObjectID2NameMap = new Resource().loadGeneInfo("/EntrezGeneInfo.txt");
            }else if (AnnotatedTermIDSet.toString().contains("HP")) {//Phenotype
                AllObjectID2NameMap = new Resource().loadPhenotypeInfo("/PhenotypeInfo.txt");
            }
            BasicData.ObjectID2NameMap = new TreeMap<String, String>();
            for(String ObjectID: BasicData.Object2TermMap.keySet()){
                if(AllObjectID2NameMap.containsKey(ObjectID)){
                    BasicData.ObjectID2NameMap.put(ObjectID, AllObjectID2NameMap.get(ObjectID));
                }else{
                    BasicData.ObjectID2NameMap.put(ObjectID, "");
                }
            }
            
            System.out.println("After: ");
            System.out.println("--------");
            System.out.println("BasicData.DAG.size(): " + BasicData.DAG.size());
            System.out.println("BasicData.DAGNodeSet.size(): " + BasicData.DAGNodeSet.size());
            System.out.println("BasicData.TermID2NameMap.size(): " + BasicData.TermID2NameMap.size());
            System.out.println("BasicData.Object2TermMap.size(): " + BasicData.Object2TermMap.size());
            System.out.println("BasicData.Term2ObjectMap.size(): " + BasicData.Term2ObjectMap.size());
            
            if(this.interrupted==true) return;
            
           
            
            //Find root term
            BasicData.RootTermID = "";
            System.out.println("Type: " + Type);
            if(Type.compareTo("biological_process")==0){
                BasicData.RootTermID = "GO:0008150";
            }else if(Type.compareTo("cellular_component")==0){
                BasicData.RootTermID = "GO:0005575";
            }else if(Type.compareTo("molecular_function")==0){
                BasicData.RootTermID = "GO:0003674";
            }else {
                for(Map.Entry<String, ArrayList<NodeInteraction>> e: BasicData.ParentNodeMap.entrySet()){
                    if(e.getValue().isEmpty()){
                        BasicData.RootTermID = e.getKey();
                        break;
                    }
                }
            }
            
            MainPanel.tblSelectedTerm.setToolTipText("Root Term:" + BasicData.RootTermID);
            
            //Calculate IC
            
            taskMonitor.setStatusMessage("Calculating Information Content (IC) of terms...!");
            BasicData.Term2ICMap = Common.calculateICofTerm(BasicData.RootTermID, BasicData.Term2ObjectMap, BasicData.ChildNodeMap, BasicData.ParentNodeMap);
            System.out.println("BasicData.Term2ICMap.size(): " + BasicData.Term2ICMap.size());

            BasicData.MaxIC = 0.0;
            BasicData.MinIC = Double.POSITIVE_INFINITY;

            for(Map.Entry<String, Double> e: BasicData.Term2ICMap.entrySet()){
                if(e.getValue()<BasicData.MinIC) BasicData.MinIC = e.getValue();

                if(e.getValue()!= Double.POSITIVE_INFINITY && e.getValue()>BasicData.MaxIC) BasicData.MaxIC = e.getValue();
            }
           
            System.out.println("MinIC: " + BasicData.MinIC);
            System.out.println("MaxIC: " + BasicData.MaxIC);
            
            MainPanel.fillTermInputTable(BasicData.TermID2NameMap, BasicData.Term2ICMap, BasicData.Term2ObjectMap, MainPanel.tblSelectedTerm);
            MainPanel.fillObjectInputTable(BasicData.ObjectID2NameMap, BasicData.Object2TermMap, MainPanel.tblSelectedObject);
            MainPanel.fillObjectSetTable(BasicData.ObjectID2NameMap, BasicData.Object2TermMap, MainPanel.tblObjectSet1);
            MainPanel.fillObjectSetTable(BasicData.ObjectID2NameMap, BasicData.Object2TermMap, MainPanel.tblObjectSet2);

            MainPanel.lblTermInputStatus.setText("Total of " + BasicData.TermID2NameMap.size());
            MainPanel.lblObjectInputStatus.setText("Total of " + BasicData.ObjectID2NameMap.size());
            
            
            
        }catch(Exception e){
            this.interrupted = true;
            this.Error = true;
        }
            

        
        
        taskMonitor.setProgress(1.0);
    }

    @Override
    public void cancel() {
        System.out.println("Task cancel called");
        this.interrupted=true;
    }

    @Override
    public <R> R getResults(Class<? extends R> type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
