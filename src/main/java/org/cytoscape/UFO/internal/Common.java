/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cytoscape.UFO.internal;

import java.awt.Color;
import java.awt.Paint;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JOptionPane;
//import org.apache.commons.math3.stat.inference.AlternativeHypothesis;
//import org.apache.commons.math3.stat.inference.BinomialTest;
import org.cytoscape.UFO.Base.GENE;
import org.cytoscape.UFO.Base.GO;
import org.cytoscape.UFO.Base.Interaction;
import org.cytoscape.UFO.Base.NodeInteraction;
import org.cytoscape.UFO.Base.Term;
import org.cytoscape.UFO.Base.TermIDEvidenceID;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.ArrowShape;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualPropertyDependency;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

/**
 *
 * @author "MinhDA"
 */
public class Common {

    public static void preprocessGeneList(ArrayList<GENE> Genes, String By){
        int i;
        for(i=0;i<Genes.size();i++){
            if(By.compareTo("OfficialSymbol")==0){
                Genes.get(i).Index=Genes.get(i).OfficialSymbol;
            }else if(By.compareTo("EntrezID")==0){
                Genes.get(i).Index=Genes.get(i).EntrezID;
            }else if(By.compareTo("UniProtAC")==0){
                Genes.get(i).Index=Genes.get(i).UniProtAC;
            }else if(By.compareTo("Organism")==0){
                Genes.get(i).Index=Genes.get(i).Organism;
            }else if(By.compareTo("Tag")==0){
                Genes.get(i).Index=Genes.get(i).Tag;
            }
        }
    }

    public static void sortQuickGOListInAsc(ArrayList<GO> GOs){
        Common.quickSortGO(GOs, 0, GOs.size()-1);
    }
    
    public static void quickSortGO(ArrayList<GO> A, int lower, int upper){
        int i, j;
        String x;
        x = A.get((lower + upper) / 2).Index;
        i = lower;
        j = upper;
        while(i <= j){
            while(A.get(i).Index.compareTo(x)<0) i++;
            while(A.get(j).Index.compareTo(x)>0) j--;
            if (i <= j){
                GO temp=new GO();
                temp=A.get(i);
                A.set(i,A.get(j));
                A.set(j,temp);

                i++;
                j--;
            }
        }
        if (j > lower) quickSortGO(A, lower, j);
        if (i < upper) quickSortGO(A, i, upper);
    }

    public static ArrayList<Integer> searchUsingBinaryGENEArray(String searchterm, ArrayList<GENE> List){
        ArrayList<Integer> posarr= new ArrayList<Integer>();
        try{
            int lo, high;
            lo=0;
            high=List.size();
            int pos = Common.searchUsingBinaryGENEDetail(searchterm, List, lo, high);
            if(pos==-1) return posarr;
            
            posarr.add(pos);
            int postemp1=pos;
            int postemp2=pos;
            boolean exist1, exist2;
            while(true){
                exist1=false;
                postemp1++;
                if(postemp1<List.size() && List.get(postemp1).Index.compareTo(searchterm)==0){
                   posarr.add(postemp1);
                   exist1=true;
                }
                if(exist1==false) break;
            }
            while(true){
                exist2=false;
                postemp2--;
                if(postemp2>=0 && List.get(postemp2).Index.compareTo(searchterm)==0){
                   posarr.add(postemp2);
                   exist2=true;
                }
                if(exist2==false) break;
            }
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error while searching in ID Mapping database: " + e.toString());
        }
        return posarr;
    }
    
    public static int searchUsingBinaryGENEDetail(String key, ArrayList<GENE> a, int lo, int hi) {
        // possible key indices in [lo, hi)
        if (hi <= lo) return -1;
        int mid = lo + (hi - lo) / 2;
        int cmp = a.get(mid).Index.compareTo(key);
        if      (cmp > 0) return searchUsingBinaryGENEDetail(key, a, lo, mid);
        else if (cmp < 0) return searchUsingBinaryGENEDetail(key, a, mid+1, hi);
        else              return mid;
    }

    public static ArrayList<Interaction> loadOntologyData(String FileName) {
        ArrayList<Interaction> DAG = new ArrayList<Interaction>();
        try{
            int i,j;
                        
            BasicData.TermID2InfoMap = new TreeMap<String, Term>();
            BasicData.DAGNodeSet = new TreeSet<String>();
            
            BufferedReader br = new BufferedReader(new FileReader(FileName));
            String str = null;

            ArrayList<String> TermBlockList = new ArrayList<String>();
            while ((str = br.readLine()) != null) {
                if(str.trim().compareTo("[Term]")==0){
                    
                    String TermBlock ="";
                    while(true){
                        str = br.readLine().trim();
                        if(str.compareTo("[Term]")!=0 && !str.isEmpty()){
                            TermBlock = TermBlock.concat(str);
                            TermBlock = TermBlock.concat("\n");
                        }else{
                            break;
                        }
                    }
                        
                    TermBlockList.add(TermBlock);
                    
                }
            }
            
            System.out.println("TermBlockList.size() :" + TermBlockList.size());
            
            ArrayList<String> RelationType = new ArrayList<String>();
            RelationType.add("is_a:");
            RelationType.add("relationship: part_of");
            RelationType.add("relationship: regulates");
            RelationType.add("relationship: positively_regulates");
            RelationType.add("relationship: negatively_regulates");
            
            PrintWriter pw = new PrintWriter(new FileOutputStream(FileName + "_DAG.txt"),true);
            
            PrintWriter pwTerm = new PrintWriter(new FileOutputStream(FileName + "_Term.txt"),true);
            
            for(i=0;i<TermBlockList.size();i++){
                String[] TermLine = TermBlockList.get(i).split("\n");
                String id = TermLine[0].substring("id:".length()+1, TermLine[0].length());
                String NodeSrc = id;
                String TermName = "";
                String Type = "";
                for(j=1;j<TermLine.length;j++){
                    if(TermLine[j].contains("name:")){
                        TermName = TermLine[j].substring("name:".length()+1, TermLine[j].length());
                        break;
                    }
                }
                
                for(j=1;j<TermLine.length;j++){
                    if(TermLine[j].contains("namespace:")){
                        Type = TermLine[j].substring("namespace:".length()+1, TermLine[j].length());
                        break;
                    }
                }
                pwTerm.println(id + "\t" + TermName + "\t" + Type);
                BasicData.TermID2InfoMap.put(id, new Term(id, TermName, false, Type));
                
                for(j=1;j<TermLine.length;j++){
                    int t;
                    String NodeDst = "";
                    boolean found = false;
                    for(t=0;t<RelationType.size();t++){
                        if(TermLine[j].contains(RelationType.get(t))){
                            NodeDst = TermLine[j].substring(RelationType.get(t).length()+1, TermLine[j].indexOf("!")-1);
                            found=true;
                            break;
                        }
                    }
                    if(found){
                        Interaction ina = new Interaction();
                        ina.NodeSrc = NodeSrc;
                        ina.NodeDst = NodeDst;
                        
                        if(RelationType.get(t).equalsIgnoreCase("is_a:")){
                            ina.TypeOriginal = "is_a";
                            ina.Weight = 0.8;
                        }else if(RelationType.get(t).equalsIgnoreCase("relationship: part_of")){
                            ina.TypeOriginal = "part_of";
                            ina.Weight = 0.6;
                        }else if(RelationType.get(t).equalsIgnoreCase("relationship: regulates")){
                            ina.TypeOriginal = "regulates";
                            ina.Weight = 0.4;
                        }else if(RelationType.get(t).equalsIgnoreCase("relationship: positively_regulates")){
                            ina.TypeOriginal = "positively_regulates";
                            ina.Weight = 0.4;
                        }else if(RelationType.get(t).equalsIgnoreCase("relationship: negatively_regulates")){
                            ina.TypeOriginal = "negatively_regulates";
                            ina.Weight = 0.4;
                        }else{
                            ina.Weight = 0.0;
                        }
                                                
                        pw.println(NodeSrc + "\t" + ina.TypeOriginal + "\t" + NodeDst);
                        DAG.add(ina);
                        BasicData.DAGNodeSet.add(ina.NodeSrc);
                        BasicData.DAGNodeSet.add(ina.NodeDst);
                    }
                }
            }
            pwTerm.close();
            pw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return DAG;
    }
    
    public static Map<String, Term> loadOntologyData_ForMapping(String FileName) {
        Map<String, Term> TermID2InfoMap = new TreeMap<String, Term>();
        try{
            int i,j;
            
            
            BufferedReader br = new BufferedReader(new FileReader(FileName));
            String str = null;

            ArrayList<String> TermBlockList = new ArrayList<String>();
            while ((str = br.readLine()) != null) {
                if(str.trim().compareTo("[Term]")==0){
                    
                    String TermBlock ="";
                    while(true){
                        str = br.readLine().trim();
                        if(str.compareTo("[Term]")!=0 && !str.isEmpty()){
                            TermBlock = TermBlock.concat(str);
                            TermBlock = TermBlock.concat("\n");
                        }else{
                            break;
                        }
                    }
                        
                    TermBlockList.add(TermBlock);
                    
                }
            }
            
            System.out.println("TermBlockList.size() :" + TermBlockList.size());
            
            
            //PrintWriter pw = new PrintWriter(new FileOutputStream(FileName + "_DAG.txt"),true);

            
            for(i=0;i<TermBlockList.size();i++){
                String[] TermLine = TermBlockList.get(i).split("\n");
                String id = TermLine[0].substring("id:".length()+1, TermLine[0].length());
                String NodeSrc = id;
                String TermName = "";
                String Type = "";
                boolean is_obsolete = false;
                
                for(j=1;j<TermLine.length;j++){
                    if(TermLine[j].contains("name:")){
                        TermName = TermLine[j].substring("name:".length()+1, TermLine[j].length());
                        break;
                    }
                }
                
                for(j=1;j<TermLine.length;j++){
                    if(TermLine[j].contains("namespace:")){
                        Type = TermLine[j].substring("namespace:".length()+1, TermLine[j].length());
                        break;
                    }
                }
                
                for(j=1;j<TermLine.length;j++){
                    if(TermLine[j].contains("is_obsolete:")){
                        is_obsolete = true;
                        break;
                    }
                }
                
                Set<String> MeSHIDSet = new TreeSet<>();
                for(j=1;j<TermLine.length;j++){
                    if(TermLine[j].contains("xref: MESH:")){
                        String MeSHID = TermLine[j].substring("xref: MESH:".length(), TermLine[j].length());
                        MeSHIDSet.add(MeSHID);
                    }
                }
                Term t = new Term();
                t.ID = id;
                t.Name = TermName;
                t.Type = Type;
                t.Obsolete = is_obsolete;
                t.MappedIDSet.addAll(MeSHIDSet);
                
                TermID2InfoMap.put(id, t);
                
            }
            
            
            //pw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return TermID2InfoMap;
    }

    public static Map<String,ArrayList<TermIDEvidenceID>> loadAnnotationData(String FileName){
        
        Map<String,ArrayList<TermIDEvidenceID>> Object2TermMap = new TreeMap<String,ArrayList<TermIDEvidenceID>>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(FileName));
            String str="";
            //str = br.readLine();//Ignore first line
            while ((str = br.readLine()) != null) {
                String[] s = str.split("\t");
                String ObjectID = s[0].trim();
                String TermID = s[1].trim();
                String EvidenceID = "";
                if(s.length>=3){
                    EvidenceID = s[2].trim();
                }
                                
                if(Object2TermMap.containsKey(ObjectID)){
                    Object2TermMap.get(ObjectID).add(new TermIDEvidenceID(TermID, EvidenceID));
                }else{
                    ArrayList<TermIDEvidenceID> TermIDEvidenceIDSet= new ArrayList<TermIDEvidenceID>();
                    TermIDEvidenceIDSet.add(new TermIDEvidenceID(TermID, EvidenceID));
                    Object2TermMap.put(ObjectID, TermIDEvidenceIDSet);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return Object2TermMap;
    }

    public static Map<String,ArrayList<NodeInteraction>> calculateIncomingNeighbors(ArrayList<Interaction> Network){
        Map<String,ArrayList<NodeInteraction>> IncomingNeighbors = new TreeMap<String,ArrayList<NodeInteraction>>();

        int i;
        Set<String> NetworkNodeSet = new TreeSet<String>();
        for(i=0;i<Network.size();i++){
            NetworkNodeSet.add(Network.get(i).NodeSrc);
            NetworkNodeSet.add(Network.get(i).NodeDst);
        }
        
        Common.preprocessInteractionList(Network, "NodeDst");
        Common.sortQuickInteractionListInAsc(Network);
        
        for(Iterator<String> it=NetworkNodeSet.iterator();it.hasNext();){
            String Gene = it.next();
            ArrayList<Integer> posarr = Common.searchUsingBinaryInteraction(Gene, Network);
            ArrayList<NodeInteraction> neighbors=new ArrayList<NodeInteraction>();
            if(posarr.size()>0){
                
                for(i=0;i<posarr.size();i++){
                    Interaction ina = Network.get(posarr.get(i));
                    neighbors.add(new NodeInteraction(ina.NodeSrc, ina.Type, ina.Weight, ina.TypeOriginal));
                }
                IncomingNeighbors.put(Gene, neighbors);
            }else{
                IncomingNeighbors.put(Gene, neighbors);
            }
        }

        
        return IncomingNeighbors;
    }

    public static Map<String,ArrayList<NodeInteraction>> calculateOutgoingNeighbors(ArrayList<Interaction> Network){
        Map<String,ArrayList<NodeInteraction>> OutgoingNeighbors = new TreeMap<String,ArrayList<NodeInteraction>>();

        int i;
        Set<String> NetworkNodeSet = new TreeSet<String>();
        for(i=0;i<Network.size();i++){
            NetworkNodeSet.add(Network.get(i).NodeSrc);
            
            NetworkNodeSet.add(Network.get(i).NodeDst);
        }
        
        Common.preprocessInteractionList(Network, "NodeSrc");
        Common.sortQuickInteractionListInAsc(Network);
        
        for(Iterator<String> it=NetworkNodeSet.iterator();it.hasNext();){
            String Gene = it.next();
            ArrayList<Integer> posarr = Common.searchUsingBinaryInteraction(Gene, Network);
            ArrayList<NodeInteraction> neighbors=new ArrayList<NodeInteraction>();
            if(posarr.size()>0){
                
                for(i=0;i<posarr.size();i++){
                    Interaction ina = Network.get(posarr.get(i));
                    neighbors.add(new NodeInteraction(ina.NodeDst, ina.Type, ina.Weight, ina.TypeOriginal));
                }
                OutgoingNeighbors.put(Gene, neighbors);
            }else{
                OutgoingNeighbors.put(Gene, neighbors);
            }
        }
        return OutgoingNeighbors;
    }

    public static Map<String, Double> calculateICofTerm(ArrayList<Interaction> DAG, Map<String, Set<String>> Term2GeneMap, String RootTerm) {
        Map<String, Double> Term2ICMap = new TreeMap<String, Double>();
        Set<String> DAGNodeSet = new TreeSet<String>();
        for(Interaction ina: DAG){
            DAGNodeSet.add(ina.NodeSrc);
            DAGNodeSet.add(ina.NodeDst);
        }
        System.out.println("Number of Terms: " + DAGNodeSet.size());
        System.out.println("Number of Term Links: " + DAG.size());
        
        Map<String, ArrayList<NodeInteraction>> ChildNodeMap = Common.calculateIncomingNeighbors(DAG);
        Map<String, ArrayList<NodeInteraction>> ParentNodeMap = Common.calculateOutgoingNeighbors(DAG);
        Map<String,Set<String>> Term2DescendantMap = new TreeMap<String, Set<String>>();
        Map<String,Set<String>> Term2AncestorMap = new TreeMap<String, Set<String>>();
        
        int c=0;
        for(String term: DAGNodeSet){
            
            Set<String> DescendantSet = Common.extractDescendantTerm(term, ChildNodeMap);
            Set<String> AncestorSet = Common.extractAncestorTerms(term, ParentNodeMap);
            Term2DescendantMap.put(term, DescendantSet);
            Term2AncestorMap.put(term, AncestorSet);
            c++;
        }
        System.out.println("Term2DescendantMap.size(): " + Term2DescendantMap.size());
        System.out.println("Term2AncestorMap.size(): " + Term2AncestorMap.size());
        
        Set<String> AllAnnGeneOfRootSet = Common.getAllAnnotatedObjects(RootTerm, Term2DescendantMap, Term2GeneMap);
        int totalroot = AllAnnGeneOfRootSet.size();
        System.out.println("totalroot: " + totalroot);
        int NuInf=0;
        for(Map.Entry<String,Set<String>> e: Term2DescendantMap.entrySet()){
            Set<String> AllAnnGeneSet = Common.getAllAnnotatedObjects(e.getKey(), Term2DescendantMap, Term2GeneMap);
            int total = AllAnnGeneSet.size();
            
            double IC = -Math.log10((double)total/totalroot);
            
            Term2ICMap.put(e.getKey(), IC);
            System.out.println(e.getKey() + "\t" + e.getValue().size() + "\t" + total + "\t" + IC);
            if(total==0) NuInf++;
        }
        System.out.println("NuInf: " + NuInf + "/" + Term2DescendantMap.size());
        return Term2ICMap;
    }
    
    public static Map<String, Double> calculateICofTerm(String RootTerm, Map<String, Set<String>> Term2ObjectMap, Map<String, ArrayList<NodeInteraction>> ChildNodeMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap) {
        
        Map<String, Double> Term2ICMap = new TreeMap<String, Double>();
        
        Set<String> DAGNodeSet = new TreeSet<String>();
        DAGNodeSet = ChildNodeMap.keySet();
                
        Map<String,Set<String>> Term2DescendantMap = new TreeMap<String, Set<String>>();
        
        int c=0;
        for(String term: DAGNodeSet){
            
            Set<String> DescendantSet = Common.extractDescendantTerm(term, ChildNodeMap);
            Set<String> AncestorSet = Common.extractAncestorTerms(term, ParentNodeMap);
            Term2DescendantMap.put(term, DescendantSet);
            c++;
        }
        
        Set<String> AllAnnGeneOfRootSet = Common.getAllAnnotatedObjects(RootTerm, Term2DescendantMap, Term2ObjectMap);
        int totalroot = AllAnnGeneOfRootSet.size();
        System.out.println("totalroot: " + totalroot);
        int NuInf=0;
        for(String term: DAGNodeSet){
            Set<String> AllAnnGeneSet = Common.getAllAnnotatedObjects(term, Term2DescendantMap, Term2ObjectMap);
            int total = AllAnnGeneSet.size();
            
            double IC = -Math.log10((double)total/totalroot);
            
            Term2ICMap.put(term, IC);
            if(total==0) NuInf++;
        }
        System.out.println("NuInf: " + NuInf + "/" + Term2DescendantMap.size());
        
        return Term2ICMap;
    }

    public static void sortQuickGeneListInAsc(ArrayList<GENE> Genes){
               
        Common.quickSortGene(Genes, 0, Genes.size()-1);
    }

    public static void preprocessInteractionList(ArrayList<Interaction> Interactions, String By){
        int i;
        for(i=0;i<Interactions.size();i++){
            if(By.compareTo("NodeSrc")==0){
                Interactions.get(i).Index=Interactions.get(i).NodeSrc;
            }else if(By.compareTo("NodeDst")==0){
                Interactions.get(i).Index=Interactions.get(i).NodeDst;
            }
        }
    }

    public static void sortQuickInteractionListInAsc(ArrayList<Interaction> Interactions){
        
        Common.quickSortInteraction(Interactions, 0, Interactions.size()-1);
    }

    public static Set<String> extractDescendantTerm(String Node, Map<String, ArrayList<NodeInteraction>> ChildNodeMap){        
        int i;
        Set<String> InaSet = new TreeSet<String>();
        ArrayList<Interaction> SubTree = new ArrayList<Interaction>();
        Common.extractSubTree(Node, ChildNodeMap, SubTree, InaSet);
        
        Set<String> DescendantSet = new TreeSet<String>();
        for(i=0;i<SubTree.size();i++){
            DescendantSet.add(SubTree.get(i).NodeSrc);
            DescendantSet.add(SubTree.get(i).NodeDst);
        }
        DescendantSet.remove(Node);
        return DescendantSet;
    }

    //Extract Ancestor Nodes
    public static Set<String> extractAncestorTerms(String Node, Map<String, ArrayList<NodeInteraction>> ParentNodeMap){        
        int i;
        Set<String> InaSet = new TreeSet<String>();
        ArrayList<Interaction> SubDAG = new ArrayList<Interaction>();
        Common.extractSubDAG(Node, ParentNodeMap, SubDAG, InaSet);
        
        Set<String> AncestorSet = new TreeSet<String>();
        for(i=0;i<SubDAG.size();i++){
            AncestorSet.add(SubDAG.get(i).NodeSrc);
            AncestorSet.add(SubDAG.get(i).NodeDst);
        }
        AncestorSet.remove(Node);
        return AncestorSet;
    }

    public static Set<String> getAllAnnotatedObjects(String term, Map<String, Set<String>> Term2DescendantMap, Map<String, Set<String>> Term2ObjectMap){
        Set<String> AnnObjectSet = new TreeSet<String>();
        if(Term2ObjectMap.containsKey(term)){
            AnnObjectSet = Term2ObjectMap.get(term);
        }
        for(String descendant: Term2DescendantMap.get(term)){
            if(Term2ObjectMap.containsKey(descendant)){
                AnnObjectSet.addAll(Term2ObjectMap.get(descendant));
            }
        }
        
        return AnnObjectSet;
    }

    public static void quickSortGene(ArrayList<GENE> A, int lower, int upper){
        int i, j;
        String x;
        x = A.get((lower + upper) / 2).Index;
        i = lower;
        j = upper;
        while(i <= j){
            while(A.get(i).Index.compareTo(x)<0) i++;
            while(A.get(j).Index.compareTo(x)>0) j--;
            if (i <= j){
                GENE temp=new GENE();
                temp=A.get(i);
                A.set(i,A.get(j));
                A.set(j,temp);
            
                i++;
                j--;
            }
        }
        if (j > lower) quickSortGene(A, lower, j);
        if (i < upper) quickSortGene(A, i, upper);
    }

    public static void quickSortInteraction(ArrayList<Interaction> A, int lower, int upper){
        int i, j;
        String x;
        x = A.get((lower + upper) / 2).Index;
        i = lower;
        j = upper;
        while(i <= j){
            while(A.get(i).Index.compareTo(x)<0) i++;
            while(A.get(j).Index.compareTo(x)>0) j--;
            if (i <= j){
                Interaction temp=new Interaction();
                temp=A.get(i);
                A.set(i,A.get(j));
                A.set(j,temp);

                i++;
                j--;
            }
        }
        if (j > lower) quickSortInteraction(A, lower, j);
        if (i < upper) quickSortInteraction(A, i, upper);
    }

    public static int searchUsingBinaryInteractionDetail(String key, ArrayList<Interaction> a, int lo, int hi) {
        // possible key indices in [lo, hi)
        if (hi <= lo) return -1;
        int mid = lo + (hi - lo) / 2;
        int cmp = a.get(mid).Index.compareTo(key);
        if      (cmp > 0) return searchUsingBinaryInteractionDetail(key, a, lo, mid);
        else if (cmp < 0) return searchUsingBinaryInteractionDetail(key, a, mid+1, hi);
        else              return mid;
    }

    public static void extractSubTree(String HighestNode, Map<String, ArrayList<NodeInteraction>> ChildrenNodeTable, ArrayList<Interaction> SubTree, Set<String> InaSet){
        try{
            int i,j;
            ArrayList<NodeInteraction> ChildrenNodes = ChildrenNodeTable.get(HighestNode);
            if(ChildrenNodes.size()==0) return;
            for(i=0;i<ChildrenNodes.size();i++){

                Interaction ina = new Interaction();
                ina.NodeSrc = ChildrenNodes.get(i).Node;
                ina.NodeDst = HighestNode;
                ina.Type = ChildrenNodes.get(i).InaType;
                ina.Weight = ChildrenNodes.get(i).Weight;
                ina.TypeOriginal = ChildrenNodes.get(i).InaTypeOriginal;
                if(!InaSet.contains(ina.NodeSrc + "_" + ina.NodeDst)){
                    InaSet.add(ina.NodeSrc + "_" + ina.NodeDst);
                    SubTree.add(ina);
                }
                extractSubTree(ChildrenNodes.get(i).Node, ChildrenNodeTable, SubTree, InaSet);
            }
        }catch(Exception e){
            System.out.println("HighestNode: " + HighestNode);
            e.printStackTrace();
        }
    }

    public static void extractSubDAG(String LowestNode, Map<String, ArrayList<NodeInteraction>> ParentNodeMap, ArrayList<Interaction> SubDAG, Set<String> InaSet){
        int i,j;
        ArrayList<NodeInteraction> ParentNodes = ParentNodeMap.get(LowestNode);
        if(ParentNodes==null||ParentNodes.size()==0) return;
        
        for(i=0;i<ParentNodes.size();i++){
            Interaction ina = new Interaction();
            ina.NodeSrc = LowestNode;
            ina.NodeDst = ParentNodes.get(i).Node;
            ina.Type = ParentNodes.get(i).InaType;
            ina.Weight = ParentNodes.get(i).Weight;
            ina.TypeOriginal = ParentNodes.get(i).InaTypeOriginal;
            if(!InaSet.contains(ina.NodeSrc + "_" + ina.NodeDst)){
                InaSet.add(ina.NodeSrc + "_" + ina.NodeDst);
                SubDAG.add(ina);
            }
            
            extractSubDAG(ParentNodes.get(i).Node, ParentNodeMap, SubDAG, InaSet);
        }
    }

    public static ArrayList<Integer> searchUsingBinaryInteraction(String searchterm, ArrayList<Interaction> List){
        int lo, high;
        lo=0;
        high=List.size();
        int pos= Common.searchUsingBinaryInteractionDetail(searchterm, List, lo, high);

        ArrayList<Integer> posarr= new ArrayList<Integer>();
        if(pos>=0){
            posarr.add(pos);
            int postemp1=pos;
            int postemp2=pos;
            boolean exist1, exist2;
            while(true){
                exist1=false;
                postemp1++;
                if(postemp1<List.size() && List.get(postemp1).Index.compareTo(searchterm)==0){
                   posarr.add(postemp1);
                   exist1=true;
                }
                if(exist1==false) break;
            }
            while(true){
                exist2=false;
                postemp2--;
                if(postemp2>=0 && List.get(postemp2).Index.compareTo(searchterm)==0){
                   posarr.add(postemp2);
                   exist2=true;
                }
                if(exist2==false) break;
            }
        }
        return posarr;
    }

/**
 * 
 * @param network
 * @param vsNetworkName
 * @return 
 */    
    public static VisualStyle createNetworkVisualStyle(String vsName, VisualStyleFactory vsfManager, VisualMappingFunctionFactory vmfFactoryC, VisualMappingFunctionFactory vmfFactoryP, VisualMappingFunctionFactory vmfFactoryD){

////        NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator();
////        EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator();
////        GlobalAppearanceCalculator globalAppCalc = new GlobalAppearanceCalculator();
//
//        //Global settings
////        globalAppCalc.setDefaultBackgroundColor(Color.WHITE);
//              
        VisualStyle vs = vsfManager.createVisualStyle(vsName);
        /* Disable "Lock node width and height", so we can set a custom width and height. */
        for (VisualPropertyDependency<?> visualPropertyDependency : vs.getAllVisualPropertyDependencies()) {
            if (visualPropertyDependency.getIdString().equals("nodeSizeLocked")) {
                visualPropertyDependency.setDependency(false);
                break;
            }
        }
        // 1. pass-through mapping
        // Node settings
        // Node Label
        PassthroughMapping pmNodeLabel = (PassthroughMapping) vmfFactoryP.createVisualMappingFunction("ID", String.class, BasicVisualLexicon.NODE_LABEL);
        
        //3. continous mapping.
         //Node color
        double mid = 0.0;//(BasicData.MinIC+BasicData.MaxIC)/2
        
        Color minColor = Color.green;
        Color midColor = Color.white;
        Color maxColor = Color.red;
        
        ContinuousMapping colorMapping = (ContinuousMapping) vmfFactoryC.createVisualMappingFunction("Information Content (IC)", Double.class, BasicVisualLexicon.NODE_FILL_COLOR);
        // set the points
	BoundaryRangeValues<Paint> brv1 = new BoundaryRangeValues<Paint>(minColor, minColor, minColor);
        BoundaryRangeValues<Paint> brv2 = new BoundaryRangeValues<Paint>(midColor, midColor, midColor);
	BoundaryRangeValues<Paint> brv3 = new BoundaryRangeValues<Paint>(maxColor, maxColor, maxColor);
                
        colorMapping.addPoint(BasicData.MinIC, brv1);
        colorMapping.addPoint(mid, brv2);
	colorMapping.addPoint(BasicData.MaxIC, brv3);
        System.out.println("BasicData.MinIC = " + BasicData.MinIC);
        System.out.println("BasicData.MaxIC = " + BasicData.MaxIC);
//        //Node color
//        double mid = 0.0;//(BasicData.MinIC+BasicData.MaxIC)/2
//        Color minColor = Color.green;
//        Color midColor = Color.white;
//        Color maxColor = Color.red;
//        
//        // Set node color map to attribute "Information Content (IC)"
//        ContinuousMapping colorMapping = (ContinuousMapping) vmfFactoryC.createVisualMappingFunction("Information Content (IC)", Double.class, BasicVisualLexicon.NODE_FILL_COLOR);
//        // set the points
//        colorMapping.addPoint(BasicData.MinIC, new BoundaryRangeValues(minColor, minColor, minColor));
//        colorMapping.addPoint(mid, new BoundaryRangeValues(midColor, midColor, midColor));
//        colorMapping.addPoint(BasicData.MaxIC, new BoundaryRangeValues(maxColor, maxColor, maxColor));
        
        //Edge settings
        // Edge Label
        PassthroughMapping pmEdgeLabel = (PassthroughMapping) vmfFactoryP.createVisualMappingFunction("interaction", String.class, BasicVisualLexicon.EDGE_LABEL);
        // Edge direction
        DiscreteMapping dmEdgeArrowShape = (DiscreteMapping) vmfFactoryD.createVisualMappingFunction("interaction", String.class, BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE);
        dmEdgeArrowShape.putMapValue(new String("is_a"), ArrowShapeVisualProperty.ARROW);
        dmEdgeArrowShape.putMapValue(new String("part_of"), ArrowShapeVisualProperty.CIRCLE);
        dmEdgeArrowShape.putMapValue(new String("regulates"), ArrowShapeVisualProperty.DIAMOND);
        dmEdgeArrowShape.putMapValue(new String("positively_regulates"), ArrowShapeVisualProperty.HALF_TOP);
        dmEdgeArrowShape.putMapValue(new String("negatively_regulates"), ArrowShapeVisualProperty.HALF_BOTTOM);

        //Edge Line Type
        DiscreteMapping dmEdgeLineStyle = (DiscreteMapping) vmfFactoryD.createVisualMappingFunction("Type", String.class, BasicVisualLexicon.EDGE_LINE_TYPE);
        dmEdgeLineStyle.putMapValue("Entity-Entity", LineTypeVisualProperty.SOLID);
        dmEdgeLineStyle.putMapValue("Entity-Term", LineTypeVisualProperty.DASH_DOT);
        
        vs.addVisualMappingFunction(pmNodeLabel);
        vs.addVisualMappingFunction(colorMapping);
        vs.addVisualMappingFunction(pmEdgeLabel);
        vs.addVisualMappingFunction(dmEdgeArrowShape);
        vs.addVisualMappingFunction(dmEdgeLineStyle);
//        vs.addVisualMappingFunction(dmNodeShape);
//        vs.addVisualMappingFunction(dmNodeColor);

//        PassthroughMapping pMapping = (PassthroughMapping) vmfFactoryP.createVisualMappingFunction("ID", String.class, BasicVisualLexicon.NODE_LABEL);
//        
////        PassThroughMapping pmNodeLabel = new PassThroughMapping(String.class,"ID");
////        Calculator nodeLabelCal = new BasicCalculator("BasicNodeLabelCalculator",pmNodeLabel,VisualPropertyType.NODE_LABEL);
//        
////        nodeAppCalc.setCalculator(nodeLabelCal);
//        
       
//                
////        ContinuousMapping colorMapping = new ContinuousMapping(Color.WHITE, ObjectMapping.NODE_MAPPING);
////        colorMapping.setControllingAttributeName("Information Content (IC)", Cytoscape.getCurrentNetwork(), false);
////        colorMapping.setInterpolator(new LinearNumberToColorInterpolator());
////        colorMapping.addPoint(BasicData.MinIC, new BoundaryRangeValues(minColor, minColor, minColor));
////        colorMapping.addPoint(mid, new BoundaryRangeValues(midColor, midColor, midColor));
////        colorMapping.addPoint(BasicData.MaxIC, new BoundaryRangeValues(maxColor, maxColor, maxColor));
////        Calculator nodeColorCalculator = new BasicCalculator("Vertex color calculator", colorMapping, VisualPropertyType.NODE_FILL_COLOR);
//        
////        nodeAppCalc.setCalculator(nodeColorCalculator);
//        
//        PassthroughMapping mpEdgeLabel = (PassthroughMapping) vmfFactoryP.createVisualMappingFunction("interaction", String.class, BasicVisualLexicon.EDGE_LABEL);
//
////        PassThroughMapping pmEdgeLabel = new PassThroughMapping(String.class,"interaction");
////        Calculator edgeLabelCal = new BasicCalculator("BasicEdgeLabelCalculator",pmEdgeLabel,VisualPropertyType.EDGE_LABEL);
////        edgeAppCalc.setCalculator(edgeLabelCal);
//                
//        //Edge direction
//        DiscreteMapping dmgEdgeArrowShape = new (DiscreteMapping) vmfFactoryD.createVisualMappingFunction("interaction", String.class, ArrowShapeVisualProperty.ARROW);
////        DiscreteMapping dmEdgeArrowShape = new DiscreteMapping(ArrowShape.class, "interaction");
//        dmEdgeArrowShape.putMapValue(new String("is_a"), ArrowShape.ARROW);
//        dmEdgeArrowShape.putMapValue(new String("part_of"), ArrowShape.CIRCLE);
//        dmEdgeArrowShape.putMapValue(new String("regulates"), ArrowShape.DIAMOND);
//        dmEdgeArrowShape.putMapValue(new String("positively_regulates"), ArrowShape.HALF_ARROW_TOP);
//        dmEdgeArrowShape.putMapValue(new String("negatively_regulates"), ArrowShape.HALF_ARROW_BOTTOM);
//                
//        Calculator edgeArrowShapeCal = new BasicCalculator("BasicEdgeTargetArrowShapeCalculator", dmEdgeArrowShape,VisualPropertyType.EDGE_TGTARROW_SHAPE);
//        edgeAppCalc.setCalculator(edgeArrowShapeCal);
//        
//        
//        //Edge Line Type
//        DiscreteMapping dmEdgeLineStyle = new DiscreteMapping(LineStyle.class, "Type");
//        
//        dmEdgeLineStyle.putMapValue(new String("Entity-Entity"), LineStyle.PARALLEL_LINES);
//        dmEdgeLineStyle.putMapValue(new String("Entity-Term"), LineStyle.SEPARATE_ARROW);
//        
//        Calculator edgeLineStyleCal = new BasicCalculator("BasicEdgeLineStyleCalculator", dmEdgeLineStyle,VisualPropertyType.EDGE_LINE_STYLE);
//        edgeAppCalc.setCalculator(edgeLineStyleCal);
//        
//        VisualStyle vs = new VisualStyle(vsName,nodeAppCalc, edgeAppCalc, globalAppCalc);
//        
        return vs;
    }
    // SUPB - 20160405 UPDATE - Implement algorithms for similarity calculation function - START
    static double calculateTermSimilarity_ResnikGraSM_Couto2005(String Term1ID, String Term2ID, Map<String, Double> Term2ICMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap) {
        double AvgICofCDA = calculateTermSimilarity_ShareGraSM_Couto2005(Term1ID, Term2ID,Term2ICMap, ParentNodeMap);
        double TermSimResnikGraSM = AvgICofCDA;
        return TermSimResnikGraSM;
    }
    
    static double calculateTermSimilarity_Resnik_Resnik1995(String Term1ID, String Term2ID, Map<String, Double> Term2ICMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap) {
        double ICofMICA = calculateTermSimilarity_MICA(Term1ID, Term2ID, Term2ICMap, ParentNodeMap);
        double TermSimResnik = ICofMICA;
        return TermSimResnik;
    }

    static double calculateTermSimilarity_LinGraSM_Couto2005(String Term1ID, String Term2ID, Map<String, Double> Term2ICMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap) {
        double AvgICofCDA = calculateTermSimilarity_ShareGraSM_Couto2005(Term1ID, Term2ID,Term2ICMap, ParentNodeMap);
        double TermSimLinGraSM = 2*AvgICofCDA/(Term2ICMap.get(Term1ID)+Term2ICMap.get(Term1ID));//Lin
        return TermSimLinGraSM;
    }
    
    static double calculateTermSimilarity_Lin_Lin1998(String Term1ID, String Term2ID, Map<String, Double> Term2ICMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap) {
        double ICofMICA = calculateTermSimilarity_MICA(Term1ID, Term2ID, Term2ICMap, ParentNodeMap);
        double TermSimLin = 2*ICofMICA/(Term2ICMap.get(Term1ID)+Term2ICMap.get(Term2ID));//Lin
        return TermSimLin;
    }

    static double calculateTermSimilarity_JCGraSM_Couto2005(String Term1ID, String Term2ID, Map<String, Double> Term2ICMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap) {
        double AvgICofCDA = calculateTermSimilarity_ShareGraSM_Couto2005(Term1ID, Term2ID,Term2ICMap, ParentNodeMap);
        double disJC = Term2ICMap.get(Term1ID)+Term2ICMap.get(Term1ID)-2*AvgICofCDA;
        double TermSimJCGraSM = 1/(disJC);//Lin
        return TermSimJCGraSM;
    }

    static double calculateTermSimilarity_JC_JiangNConrath1997(String Term1ID, String Term2ID, Map<String, Double> Term2ICMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap) {
        double ICofMICA = calculateTermSimilarity_MICA(Term1ID, Term2ID, Term2ICMap, ParentNodeMap);
        
        double disJC = Term2ICMap.get(Term1ID)+Term2ICMap.get(Term2ID)-2*ICofMICA;
        //System.out.println(ICofMICA + "\t" + Term2ICMap.get(Term1ID) + "\t" + Term2ICMap.get(Term2ID) + "\t" + disJC);
        double TermSimJC = 1/(disJC+1);//Lin
        return TermSimJC;
    }

    static double calculateTermSimilarity_RelGraSM_Couto2005(String Term1ID, String Term2ID, Map<String, Double> Term2ICMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap) {
        double AvgICofCDA = calculateTermSimilarity_ShareGraSM_Couto2005(Term1ID, Term2ID,Term2ICMap, ParentNodeMap);
        //IC(t) = log10(-p(c)) --> p(c) = - 10^IC(t)
        double ProbofCDA = Math.pow(10.0, -AvgICofCDA);
        double TermSimRelGraSM = 2*AvgICofCDA*(1-ProbofCDA)/(Term2ICMap.get(Term1ID)+Term2ICMap.get(Term2ID));//Lin
        return TermSimRelGraSM;
    }
    
    static double calculateTermSimilarity_Rel_Schlicker2006(String Term1ID, String Term2ID, Map<String, Double> Term2ICMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap) {
        double ICofMICA = calculateTermSimilarity_MICA(Term1ID, Term2ID, Term2ICMap, ParentNodeMap);
        //IC(t) = log10(-p(c)) --> p(c) = - 10^IC(t)
        double ProbofMICA = Math.pow(10.0, -ICofMICA);
        double TermSimRel = 2*ICofMICA*(1-ProbofMICA)/(Term2ICMap.get(Term1ID)+Term2ICMap.get(Term2ID));//Lin
        return TermSimRel;
    }
    // SUPB - 20160405 UPDATE - Implement algorithms for similarity calculation function - END
    // SUPB - 20160405 ADD - Implement some function to calculate similarity calculation - START
    public static double calculateSA_Wang2007(String LowestNode, String HighestNode, Map<String, ArrayList<NodeInteraction>> ChildNodeMap, Map<String, Double> SAtMap){
        
        double SA = 0.0;
        int i,j;
        
            double max=0.0;
            ArrayList<NodeInteraction> ChildNodes = ChildNodeMap.get(HighestNode);
            //System.out.println(ChildNodes.size());
            if(ChildNodes.size()==0){
                SA=1.0;
            }else{
                for(i=0;i<ChildNodes.size();i++){
                    String ChildNode = ChildNodes.get(i).Node;
                    //System.out.println("ChildNode: " + ChildNode);
                    double SAt = 0.0;
                    if(HighestNode.compareToIgnoreCase(LowestNode)==0){
                        SAt=1.0;
                    }else{
                        SAt = calculateSA_Wang2007(LowestNode, ChildNode, ChildNodeMap, SAtMap);
                    }
                    if(ChildNodes.get(i).Weight * SAt>max){
                        max = ChildNodes.get(i).Weight * SAt;
                    }
                }
                SA=max;
            }
            SAtMap.put(HighestNode, SA);
        return SA;
    }
    
    public static double calculateSVA_Wang2007(String Node, String RootNode, Map<String, ArrayList<NodeInteraction>> ChildNodeMap){
        //System.out.println("--->Calculating Svalue for a SubDAG of " + Node);
        double SV = 0.0;
        Map<String, Double> SAMap = new TreeMap<String, Double>();
                
        calculateSA_Wang2007(Node, RootNode, ChildNodeMap, SAMap);
        for(Map.Entry<String, Double> e: SAMap.entrySet()){
            SV+=e.getValue();
        }
        return SV;
    }
    
    static double calculateTermSimilarity_Wang2007(String Term1ID, String Term2ID, String RootTermID, Map<String, ArrayList<NodeInteraction>> ParentNodeMap, Map<String, ArrayList<NodeInteraction>> ChildNodeMap) {
        double sim=0.0;
       
        Set<String> AncestorsOfTerm1 = new TreeSet<String>();
        Set<String> AncestorsOfTerm2 = new TreeSet<String>();
        
        AncestorsOfTerm1= Common.extractAncestorTerms(Term1ID, ParentNodeMap);
        AncestorsOfTerm2= Common.extractAncestorTerms(Term2ID, ParentNodeMap);
                
        Set<String> T1 = new TreeSet<String>();
        Set<String> T2 = new TreeSet<String>();
        T1.addAll(AncestorsOfTerm1);
        T2.addAll(AncestorsOfTerm2);
        
        T1.add(Term1ID);
        T2.add(Term2ID);
        
        Set<String> ConjT1T2 = new TreeSet<String>();
        ConjT1T2.addAll(T1);
        ConjT1T2.retainAll(T2);
        
        double total = 0.0;
        for(String t: ConjT1T2){
            total+=Common.calculateSVA_Wang2007(t, RootTermID, ChildNodeMap);
        }
        double SV1 = Common.calculateSVA_Wang2007(Term1ID, RootTermID, ChildNodeMap);
        double SV2 = Common.calculateSVA_Wang2007(Term2ID, RootTermID, ChildNodeMap);
        return total/(SV1+SV2);
    }

    //Yu et al 2005
    static double calculateTermSimilarity_simSP_Yu2005(String Term1ID, String Term2ID, String RootTermID, Map<String, ArrayList<NodeInteraction>> ParentNodeMap) {
        double sim=0.0;
        
        

        Set<String> AncestorsOfTerm1 = new TreeSet<String>();
        Set<String> AncestorsOfTerm2 = new TreeSet<String>();
        
        AncestorsOfTerm1= Common.extractAncestorTerms(Term1ID, ParentNodeMap);
        AncestorsOfTerm2= Common.extractAncestorTerms(Term2ID, ParentNodeMap);
        //System.out.println(AncestorsOfTerm1.toString());
        //System.out.println(AncestorsOfTerm2.toString());
            
        Set<String> DisjDAGNodeSet = new TreeSet<String>();        
        DisjDAGNodeSet.addAll(AncestorsOfTerm1);
        DisjDAGNodeSet.addAll(AncestorsOfTerm2);
        DisjDAGNodeSet.add(Term1ID);
        DisjDAGNodeSet.add(Term2ID);
        
        Set<String> ConjAncestors = new TreeSet<String>();
        ConjAncestors.addAll(AncestorsOfTerm1);
        ConjAncestors.retainAll(AncestorsOfTerm2);
        
        Map<String, Set<String>> DisjDAG_OutgoingNodeMap = new TreeMap<String, Set<String>>();
        Map<String, Set<String>> ConjAncestorsDAG_OutgoingNodeMap = new TreeMap<String, Set<String>>();
        for(String t: DisjDAGNodeSet){
            if(ParentNodeMap.containsKey(t)){
                Set<String> OutgoingNodeSet = new TreeSet<String>();
                for(NodeInteraction ni: ParentNodeMap.get(t)){
                    if(DisjDAGNodeSet.contains(ni.Node)){//Dieu kien nay thua, vi Ancestors da chua Parent
                        OutgoingNodeSet.add(ni.Node);
                    }
                }
                DisjDAG_OutgoingNodeMap.put(t, OutgoingNodeSet);
                if(ConjAncestors.contains(t)){
                    ConjAncestorsDAG_OutgoingNodeMap.put(t, OutgoingNodeSet);
                }
                //System.out.println(t + "\t" + OutgoingNodeSet.size() + "\t" + OutgoingNodeSet.toString());
            }
            
        }
        
        String LowestCommonAncestor = "";//LCA
        
        //System.out.println("ConjAncestorsDAG_OutgoingNodeMap.size(): " + ConjAncestorsDAG_OutgoingNodeMap.size() + "\t" + ConjAncestorsDAG_OutgoingNodeMap.keySet().toString());
        
        //ArrayList<Stack> PathList = new ArrayList<Stack>();
        int LongestPathFromLCA2Root = 0;
        
        for(String t: ConjAncestors){
            if(t.compareTo(RootTermID)!=0){
                connectionPath = new Stack();
                connectionPaths = new ArrayList<Stack>();
                findAllPaths(t, RootTermID,ConjAncestorsDAG_OutgoingNodeMap);
                //PathList.addAll(connectionPaths);
                for(Stack p: connectionPaths){
                    if(p.size()+1>LongestPathFromLCA2Root){
                        LongestPathFromLCA2Root = p.size()+1;
                        LowestCommonAncestor = t;
                    }
                }
            }
        }
        if(LowestCommonAncestor.isEmpty()) LowestCommonAncestor = RootTermID;
        
        int LongestPathFromTerm12LCA = 0;
        connectionPath = new Stack();
        connectionPaths = new ArrayList<Stack>();
        //System.out.println(Term1 + "\t" + LowestCommonAncestor);
        findAllPaths(Term1ID, LowestCommonAncestor,DisjDAG_OutgoingNodeMap);
        //PathList.addAll(connectionPaths);
        for(Stack p: connectionPaths){
            if(p.size()+1>LongestPathFromTerm12LCA){
                LongestPathFromTerm12LCA = p.size()+1;
            }
        }
        
        int LongestPathFromTerm22LCA = 0;
        connectionPath = new Stack();
        connectionPaths = new ArrayList<Stack>();
        findAllPaths(Term2ID, LowestCommonAncestor,DisjDAG_OutgoingNodeMap);
        //PathList.addAll(connectionPaths);
        for(Stack p: connectionPaths){
            if(p.size()+1>LongestPathFromTerm22LCA){
                LongestPathFromTerm22LCA = p.size()+1;
            }
        }
        
        //System.out.println(LowestCommonAncestor + "\t" + LongestPathFromLCA2Root + "\t" + LongestPathFromTerm12LCA + "\t" + LongestPathFromTerm22LCA);
        return (double) LongestPathFromLCA2Root/(LongestPathFromLCA2Root + LongestPathFromTerm12LCA + LongestPathFromTerm22LCA);
    }

    static double calculateTermSimilarity_Wu2005(String Term1ID, String Term2ID, String RootTermID, Map<String, ArrayList<NodeInteraction>> ParentNodeMap) {
        double sim=0.0;
       
        Set<String> AncestorsOfTerm1 = new TreeSet<String>();
        Set<String> AncestorsOfTerm2 = new TreeSet<String>();
        
        AncestorsOfTerm1= Common.extractAncestorTerms(Term1ID, ParentNodeMap);
        AncestorsOfTerm2= Common.extractAncestorTerms(Term2ID, ParentNodeMap);
        
            
        Set<String> DisjAncestors = new TreeSet<String>();        
        DisjAncestors.addAll(AncestorsOfTerm1);
        DisjAncestors.addAll(AncestorsOfTerm2);
        
        Set<String> ConjAncestors = new TreeSet<String>();
        ConjAncestors.addAll(AncestorsOfTerm1);
        ConjAncestors.retainAll(AncestorsOfTerm2);
        
        Map<String, Set<String>> DisjAncestorsDAG_OutgoingNodeMap = new TreeMap<String, Set<String>>();
        Map<String, Set<String>> ConjAncestorsDAG_OutgoingNodeMap = new TreeMap<String, Set<String>>();
        for(String t: DisjAncestors){
            if(ParentNodeMap.containsKey(t)){
                Set<String> OutgoingNodeSet = new TreeSet<String>();
                for(NodeInteraction ni: ParentNodeMap.get(t)){
                    if(DisjAncestors.contains(ni.Node)){//Dieu kien nay thua, vi Ancestors da chua Parent
                        OutgoingNodeSet.add(ni.Node);
                    }
                }
                DisjAncestorsDAG_OutgoingNodeMap.put(t, OutgoingNodeSet);
                if(ConjAncestors.contains(t)){
                    ConjAncestorsDAG_OutgoingNodeMap.put(t, OutgoingNodeSet);
                }
                //System.out.println(t + "\t" + OutgoingNodeSet.size() + "\t" + OutgoingNodeSet.toString());
            }
            
        }
        
        String LowestCommonAncestor = "";//LCA
        
        //System.out.println("ConjAncestorsDAG_OutgoingNodeMap.size(): " + ConjAncestorsDAG_OutgoingNodeMap.size() + "\t" + ConjAncestorsDAG_OutgoingNodeMap.keySet().toString());
        
        //ArrayList<Stack> PathList = new ArrayList<Stack>();
        int LongestPathFromLCA2Root = 0;
        
        for(String t: ConjAncestors){
            if(t.compareTo(RootTermID)!=0){
                connectionPath = new Stack();
                connectionPaths = new ArrayList<Stack>();
                findAllPaths(t, RootTermID,ConjAncestorsDAG_OutgoingNodeMap);
                //PathList.addAll(connectionPaths);
                for(Stack p: connectionPaths){
                    if(p.size()+1>LongestPathFromLCA2Root){
                        LongestPathFromLCA2Root = p.size()+1;
                        LowestCommonAncestor = t;
                    }
                }
            }
        }
        if(LowestCommonAncestor.isEmpty()) LowestCommonAncestor = RootTermID;
        
        return (double) LongestPathFromLCA2Root;
    }
    
    public static ArrayList<String> sortDescByIC(Set<String> Anc, Map<String, Double> TermICMap){
        //ArrayList<String> DescByIC = new ArrayList<String>();
        ArrayList<String> AncArr = new ArrayList<String>();
        AncArr.addAll(Anc);
        int i,j;
        String temp="";
        for(i=0;i<AncArr.size()-1;i++){
            for(j=i+1;j<AncArr.size();j++){
                if(TermICMap.get(AncArr.get(i))<TermICMap.get(AncArr.get(j))){
                    temp=AncArr.get(i);
                    AncArr.set(i,AncArr.get(j));
                    AncArr.set(j,temp);
                }
            }
        }
        return AncArr;
    }
    
    public static Set<String> calculateCommonAncestors(ArrayList<Interaction> SubDAG1, ArrayList<Interaction> SubDAG2){
        //System.out.println("Calculating common ancestors of two terms");
        Set<String> SubDAG1Nodes = new TreeSet<String>();
        Set<String> SubDAG2Nodes = new TreeSet<String>();
        Set<String> CommonAnc = new TreeSet<String>();
        int i,j;
        for(i=0;i<SubDAG1.size();i++){
            SubDAG1Nodes.add(SubDAG1.get(i).NodeSrc);
            SubDAG1Nodes.add(SubDAG1.get(i).NodeDst);
        }
        
        for(i=0;i<SubDAG2.size();i++){
            SubDAG2Nodes.add(SubDAG2.get(i).NodeSrc);
            SubDAG2Nodes.add(SubDAG2.get(i).NodeDst);
        }
        
        //for(String n1: SubDAG1Nodes){
        //    for(String n2: SubDAG2Nodes){
        //        if(n1.compareTo(n2)==0){
        //            CommonAnc.add(n1);
        //        }
        //    }
        //}
        CommonAnc.addAll(SubDAG1Nodes);
        CommonAnc.retainAll(SubDAG2Nodes);
        
        return CommonAnc;
    }
    public static Stack connectionPath = new Stack();
    public static ArrayList<Stack> connectionPaths = new ArrayList<Stack>();//Each Stack contains a path, the path is a set of nodes except starting and ending nodes
    
    // Push to connectionsPath the object that would be passed as the parameter 'node' into the method below
    public static void findAllPaths(String node, Object targetNode, Map<String, Set<String>> OutgoingNodeMap) {
        for (String nextNode : OutgoingNodeMap.get(node)) {
            if (nextNode.equals(targetNode)) {
                Stack temp = new Stack();
                for (Object node1 : connectionPath)
                    temp.add(node1);
                connectionPaths.add(temp);
            } else if (!connectionPath.contains(nextNode)) {
                connectionPath.push(nextNode);
                findAllPaths(nextNode, targetNode, OutgoingNodeMap);
                connectionPath.pop();
            }
        }
    }
    
    //Check whether a1 and a2 are disjunctive ancestors of c
    public static boolean isDisjAnc(String c, String a1, String a2, ArrayList<Interaction> SubDAG){
        boolean value = false;
        int i,j;
        Map<String, ArrayList<NodeInteraction>> OutgoingNeighbors = Common.calculateOutgoingNeighbors(SubDAG);
        Map<String, Set<String>> OutgoingNodeMap = new TreeMap<String, Set<String>>();
        for(Map.Entry<String,ArrayList<NodeInteraction>> e: OutgoingNeighbors.entrySet()){
            Set<String> OutgoingNodeSet = new TreeSet<String>();
            for(i=0;i<e.getValue().size();i++){
                OutgoingNodeSet.add(e.getValue().get(i).Node);
            }
            OutgoingNodeMap.put(e.getKey(),OutgoingNodeSet);
        }
        
        //System.out.println("OutgoingNeighbors.size(): " + OutgoingNeighbors.size());
        //System.out.println("OutgoingNodeMap.size(): " + OutgoingNodeMap.size());
        
        connectionPath = new Stack();
        connectionPaths = new ArrayList<Stack>();
        findAllPaths(c, a1,OutgoingNodeMap);
        List<Stack> Pathctoa1 = connectionPaths;
        //System.out.println("NumPath " + c +  " to " + a1 + ": " + Pathctoa1.size());
        for(i=0;i<connectionPaths.size();i++){
            //System.out.print("Path " + i + ": ");
            
            //while(!connectionPaths.get(i).isEmpty()){
            //    Object node = connectionPaths.get(i).pop();
            //    System.out.print(node + " -> ");
            //}
            
            for(j=0;j<connectionPaths.get(i).size();j++){
                Object node = connectionPaths.get(i).get(j);
                //System.out.print(node + " -> ");
            }
            //System.out.println();
        }
        
        connectionPath = new Stack();
        connectionPaths = new ArrayList<Stack>();
        findAllPaths(c, a2,OutgoingNodeMap);
        List<Stack> Pathctoa2 = connectionPaths;
        //System.out.println("NumPath " + c +  " to " + a2 + ": " + Pathctoa2.size());
        
        connectionPath = new Stack();
        connectionPaths = new ArrayList<Stack>();
        findAllPaths(a2, a1,OutgoingNodeMap);
        List<Stack> Patha2toa1 = connectionPaths;
        //System.out.println("NumPath " + a2 +  " to " + a1 + ": "+ Patha2toa1.size());
                
        value = Pathctoa1.size()>(Pathctoa2.size()*Patha2toa1.size());
        return value;
    }
    
    public static double calculateTermSimilarity_ShareGraSM_Couto2005(String c1, String c2, ArrayList<Interaction> SubDAG1, ArrayList<Interaction> SubDAG2, Map<String, Double> Term2ICMap){
        //System.out.println("Calculating similarity of " + c1 + " and " + c2);
        
        Set<String> Anc = calculateCommonAncestors(SubDAG1, SubDAG2);
        //System.out.println("Anc.toString(): " + Anc.toString());
        ArrayList<String> sortedAnc = sortDescByIC(Anc, Term2ICMap);
        //System.out.println("sortedAnc.toString(): " + sortedAnc.toString());
        
        //System.out.println("SubDAG1.size(): " + SubDAG1.size());
        //System.out.println("SubDAG2.size(): " + SubDAG2.size());
        
        Set<String> CommonDisjAnc=new TreeSet<String>();
        for(int i=0;i<sortedAnc.size();i++){
            String a = sortedAnc.get(i);
            boolean isDisj=true;
            for(String cda:CommonDisjAnc){
                boolean isDisj1 = isDisjAnc(c1, a, cda, SubDAG1);
                boolean isDisj2 = isDisjAnc(c2, a, cda, SubDAG2);
                //System.out.println(isDisj1 + "\t" + isDisj2);
                isDisj = isDisj && (isDisj1||isDisj2);
            }
            if(isDisj){
                CommonDisjAnc.add(a);
                //System.out.println("CommonDisjAnc.toString(): " + CommonDisjAnc.toString());
            }
        }
        double simTerm=0.0;
        
        for(String cda:CommonDisjAnc){
            simTerm+=Term2ICMap.get(cda);
        }
        return simTerm/CommonDisjAnc.size();
    }
    
    public static double calculateTermSimilarity_ShareGraSM_Couto2005(String c1, String c2, Map<String, Double> Term2ICMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap){
        //System.out.println("Calculating similarity of " + c1 + " and " + c2);
        ArrayList<Interaction> SubDAG1 = new ArrayList<Interaction>();
        ArrayList<Interaction> SubDAG2 = new ArrayList<Interaction>();
        Set<String> InaSet1 = new TreeSet<String>();
        Set<String> InaSet2 = new TreeSet<String>();
        
        Common.extractSubDAG(c1, ParentNodeMap, SubDAG1, InaSet1);
        Common.extractSubDAG(c2, ParentNodeMap, SubDAG2, InaSet2);
        
        Set<String> Anc = calculateCommonAncestors(SubDAG1, SubDAG2);
        //System.out.println("Anc.toString(): " + Anc.toString());
        ArrayList<String> sortedAnc = sortDescByIC(Anc, Term2ICMap);
        //System.out.println("sortedAnc.toString(): " + sortedAnc.toString());
        
        //System.out.println("SubDAG1.size(): " + SubDAG1.size());
        //System.out.println("SubDAG2.size(): " + SubDAG2.size());
        
        Set<String> CommonDisjAnc=new TreeSet<String>();
        for(int i=0;i<sortedAnc.size();i++){
            String a = sortedAnc.get(i);
            boolean isDisj=true;
            for(String cda:CommonDisjAnc){
                boolean isDisj1 = isDisjAnc(c1, a, cda, SubDAG1);
                boolean isDisj2 = isDisjAnc(c2, a, cda, SubDAG2);
                //System.out.println(isDisj1 + "\t" + isDisj2);
                isDisj = isDisj && (isDisj1||isDisj2);
            }
            if(isDisj){
                CommonDisjAnc.add(a);
                //System.out.println("CommonDisjAnc.toString(): " + CommonDisjAnc.toString());
            }
        }
        double simTerm=0.0;
        
        int validCount=0;
        for(String cda:CommonDisjAnc){
            if(!Term2ICMap.get(cda).isInfinite()){
                simTerm+=Term2ICMap.get(cda);
                validCount++;
            }
        }
        //return simTerm/CommonDisjAnc.size();
        return simTerm/validCount;
    }
    
    public static double calculateTermSimilarity_MICA(String Term1ID, String Term2ID, Map<String,Double> Term2ICMap, Map<String,ArrayList<NodeInteraction>> ParentNodeMap){
        
        Set<String> Ancestor1Set = Common.extractAncestorTerms(Term1ID, ParentNodeMap);
        Set<String> Ancestor2Set = Common.extractAncestorTerms(Term2ID, ParentNodeMap);
        
        Set<String> SharedAncestorSet = new TreeSet<String>();
        SharedAncestorSet.addAll(Ancestor1Set);
        SharedAncestorSet.retainAll(Ancestor2Set);
        
        double maxIC=0.0;
        if(SharedAncestorSet.size()>0){
            for(String t: SharedAncestorSet){
                if(!Term2ICMap.get(t).isInfinite()){
                    if(Term2ICMap.get(t)>maxIC) maxIC = Term2ICMap.get(t);
                }
            }
            
        }
        return maxIC;
    }
    // SUPB - 20160405 ADD - Implement some function to calculate similarity calculation - END
    
    // SUPB - 20160405 ADD - Implement some function to calculate similarity calculation between entities - START
    public static double calculateObjectSimilarity_Pairwise(String Obj1, String Obj2, String RootTerm, Map<String, Set<String>> Obj2TermMap, Map<String, Double> Term2ICMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap, Map<String, ArrayList<NodeInteraction>> ChildNodeMap, String TermSimMethod, String AggregateMethod){
        String Term1ID="";
        String Term2ID="";
        double ObjSim = 0.0;
        try{
            
            Set<String> AnnTermSet1 = Obj2TermMap.get(Obj1);
            Set<String> AnnTermSet2 = Obj2TermMap.get(Obj2);

            for(Iterator<String> it = AnnTermSet1.iterator();it.hasNext();){
                String TermID = it.next();
                Term1ID = TermID;
                if(Term2ICMap.get(TermID).isInfinite()){
                    it.remove();
                }
            }

            for(Iterator<String> it = AnnTermSet2.iterator();it.hasNext();){
                String TermID = it.next();
                Term2ID = TermID;
                if(Term2ICMap.get(TermID).isInfinite()){
                    it.remove();
                }
            }

            if(AnnTermSet1==null||AnnTermSet2==null||AnnTermSet1.isEmpty()||AnnTermSet2.isEmpty()) return 0.0;
            double[][] TermSimMatrix = new double[AnnTermSet1.size()][AnnTermSet2.size()];
            //System.out.println(TermSimMatrix.length + "\t" + TermSimMatrix[0].length);
            double max=0.0;
            double avg=0.0;
            int i=0,j=0;
            ArrayList<Double> MaxRow = new ArrayList<Double>();
            for(String t1: AnnTermSet1){
                j=0;
                double maxrow=0.0;
                for(String t2: AnnTermSet2){
                    double TermSim=0.0;
                    if(TermSimMethod.contains("Resnik") && !TermSimMethod.contains("GraSM")){
                        TermSim = Common.calculateTermSimilarity_Resnik_Resnik1995(t1, t2, Term2ICMap, ParentNodeMap);
                    }else if(TermSimMethod.contains("Lin") && !TermSimMethod.contains("GraSM")){
                        TermSim = Common.calculateTermSimilarity_Lin_Lin1998(t1, t2, Term2ICMap, ParentNodeMap);
                    }else if(TermSimMethod.contains("JC") && !TermSimMethod.contains("GraSM")){
                        TermSim = Common.calculateTermSimilarity_JC_JiangNConrath1997(t1, t2, Term2ICMap, ParentNodeMap);
                    }else if(TermSimMethod.contains("Rel") && !TermSimMethod.contains("GraSM")){
                        TermSim = Common.calculateTermSimilarity_Rel_Schlicker2006(t1, t2, Term2ICMap, ParentNodeMap);
                    }else if(TermSimMethod.contains("Resnik") && TermSimMethod.contains("GraSM")){
                        TermSim = Common.calculateTermSimilarity_ResnikGraSM_Couto2005(t1, t2, Term2ICMap, ParentNodeMap);
                    }else if(TermSimMethod.contains("Lin") && TermSimMethod.contains("GraSM")){
                        TermSim = Common.calculateTermSimilarity_LinGraSM_Couto2005(t1, t2, Term2ICMap, ParentNodeMap);
                    }else if(TermSimMethod.contains("JC") && TermSimMethod.contains("GraSM")){
                        TermSim = Common.calculateTermSimilarity_JCGraSM_Couto2005(t1, t2, Term2ICMap, ParentNodeMap);
                    }else if(TermSimMethod.contains("Rel") && TermSimMethod.contains("GraSM")){
                        TermSim = Common.calculateTermSimilarity_RelGraSM_Couto2005(t1, t2, Term2ICMap, ParentNodeMap);
                    }else if(TermSimMethod.contains("Wu")){
                        TermSim = Common.calculateTermSimilarity_Wu2005(t1, t2, RootTerm, ParentNodeMap);
                    }else if(TermSimMethod.contains("Yu")){
                        TermSim = Common.calculateTermSimilarity_simSP_Yu2005(t1, t2, RootTerm, ParentNodeMap);
                        //System.out.println(t1 + "\t" + t2 + "\t" + TermSim);
                    }else{//Wang
                        TermSim = Common.calculateTermSimilarity_Wang2007(t1, t2, RootTerm, ParentNodeMap, ChildNodeMap);
                    }
                    TermSimMatrix[i][j]=TermSim;
                    if(max<TermSim){
                        max = TermSim;
                        maxrow = TermSim;
                    }
                    avg+=TermSim;
                    j++;
                }
                MaxRow.add(maxrow);
                i++;
            }

            ArrayList<Double> MaxCol = new ArrayList<Double>();
            for(j=0;j<TermSimMatrix[0].length;j++){
                double maxcol = 0.0;
                for(i=0;i<TermSimMatrix.length;i++){
                    if(TermSimMatrix[i][j]>maxcol) maxcol = TermSimMatrix[i][j];
                }
                MaxCol.add(maxcol);
            }
            if(AggregateMethod.contains("Max")){
                ObjSim = max;
            }else if(AggregateMethod.contains("Avg")){
                ObjSim = avg/(AnnTermSet1.size()*AnnTermSet2.size());
            }else if(AggregateMethod.contains("RCmax")){
                double avgMaxRow = 0.0;
                for(i=0;i<MaxRow.size();i++){
                    avgMaxRow+=MaxRow.get(i);
                }
                avgMaxRow=avgMaxRow/MaxRow.size();

                double avgMaxCol = 0.0;
                for(i=0;i<MaxCol.size();i++){
                    avgMaxCol+=MaxCol.get(i);
                }
                avgMaxCol=avgMaxCol/MaxCol.size();
                ObjSim = (avgMaxRow>avgMaxCol)?avgMaxRow:avgMaxCol;
            }else if(AggregateMethod.contains("BMA")){
                double avgMaxRowCol = 0.0;
                for(i=0;i<MaxRow.size();i++){
                    avgMaxRowCol+=MaxRow.get(i);
                }

                for(i=0;i<MaxCol.size();i++){
                    avgMaxRowCol+=MaxCol.get(i);
                }

                ObjSim = avgMaxRowCol/(MaxRow.size() + MaxCol.size());
            }

            
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(Term1ID + "\t" + Term2ID);
        }
        return ObjSim;
    }
    
    public static double calculateObjectSimilarity_Groupwise_TermOverlap_Lee2004_Mistry2008(String Obj1, String Obj2, Map<String, Set<String>> Obj2TermMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap, boolean normalized) {
        // TODO code application logic here
        double sim=0.0;
                
        Set<String> AnnTermSet1 = Obj2TermMap.get(Obj1);
        Set<String> AnnTermSet2 = Obj2TermMap.get(Obj2);
        
                        
        Set<String> AnnTermSet1OK = new TreeSet<String>();
        Set<String> AnnTermSet2OK = new TreeSet<String>();
        Set<String> AnnTermSetAllOK = new TreeSet<String>();
        for(String go: AnnTermSet1){
            if(ParentNodeMap.keySet().contains(go)){
                AnnTermSet1OK.add(go);
                AnnTermSetAllOK.add(go);
            }
        }
        for(String go: AnnTermSet2){
            if(ParentNodeMap.keySet().contains(go)) {
                AnnTermSet2OK.add(go);
                AnnTermSetAllOK.add(go);
            }
        }

        Set<String> AncestorsOfAnnTermSet1 = new TreeSet<String>();
        Set<String> AncestorsOfAnnTermSet2 = new TreeSet<String>();
        for(String go: AnnTermSet1OK){
            AncestorsOfAnnTermSet1.addAll(Common.extractAncestorTerms(go, ParentNodeMap));
        }
        for(String go: AnnTermSet2OK){
            AncestorsOfAnnTermSet2.addAll(Common.extractAncestorTerms(go, ParentNodeMap));
        }
        
          
        Set<String> ConjAncestorsOfAnnTermSet = new TreeSet<String>();        
        
        ConjAncestorsOfAnnTermSet.addAll(AncestorsOfAnnTermSet1);
        ConjAncestorsOfAnnTermSet.retainAll(AncestorsOfAnnTermSet2);
        
        sim = ConjAncestorsOfAnnTermSet.size();
        if(normalized){
            int min = (AncestorsOfAnnTermSet1.size()>AncestorsOfAnnTermSet2.size())?AncestorsOfAnnTermSet2.size():AncestorsOfAnnTermSet1.size();
            sim = (double)sim/min;
        }        
        return sim;
    }
    
    public static double calculateObjectSimilarity_Groupwise_simUI_Gentleman2005(String Obj1, String Obj2, Map<String, Set<String>> Obj2TermMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap) {
        // TODO code application logic here
        double sim=0.0;
        
        
        Set<String> AnnTermSet1 = Obj2TermMap.get(Obj1);
        Set<String> AnnTermSet2 = Obj2TermMap.get(Obj2);
        
                        
        Set<String> AnnTermSet1OK = new TreeSet<String>();
        Set<String> AnnTermSet2OK = new TreeSet<String>();
        Set<String> AnnTermSetAllOK = new TreeSet<String>();
        for(String go: AnnTermSet1){
            if(ParentNodeMap.keySet().contains(go)){
                AnnTermSet1OK.add(go);
                AnnTermSetAllOK.add(go);
            }
        }
        for(String go: AnnTermSet2){
            if(ParentNodeMap.keySet().contains(go)) {
                AnnTermSet2OK.add(go);
                AnnTermSetAllOK.add(go);
            }
        }

        Set<String> AncestorsOfAnnTermSet1 = new TreeSet<String>();
        Set<String> AncestorsOfAnnTermSet2 = new TreeSet<String>();
        for(String go: AnnTermSet1OK){
            AncestorsOfAnnTermSet1.addAll(Common.extractAncestorTerms(go, ParentNodeMap));
        }
        for(String go: AnnTermSet2OK){
            AncestorsOfAnnTermSet2.addAll(Common.extractAncestorTerms(go, ParentNodeMap));
        }
        
        Set<String> DisjAncestorsOfAnnTermSet = new TreeSet<String>();        
        Set<String> ConjAncestorsOfAnnTermSet = new TreeSet<String>();        
        
        DisjAncestorsOfAnnTermSet.addAll(AncestorsOfAnnTermSet1);
        DisjAncestorsOfAnnTermSet.addAll(AncestorsOfAnnTermSet2);
        
        ConjAncestorsOfAnnTermSet.addAll(AncestorsOfAnnTermSet1);
        ConjAncestorsOfAnnTermSet.retainAll(AncestorsOfAnnTermSet2);
        
        sim = (double)ConjAncestorsOfAnnTermSet.size()/DisjAncestorsOfAnnTermSet.size();
                
        return sim;
    }
    
    public static double calculateObjectSimilarity_Groupwise_simGIC_Pesquita2007(String Obj1, String Obj2, Map<String, Set<String>> Obj2TermMap, Map<String, Double> Term2ICMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap) {
        // TODO code application logic here
        double sim=0.0;
        
        Set<String> AnnTermSet1 = Obj2TermMap.get(Obj1);
        Set<String> AnnTermSet2 = Obj2TermMap.get(Obj2);
        
                        
        Set<String> AnnTermSet1OK = new TreeSet<String>();
        Set<String> AnnTermSet2OK = new TreeSet<String>();
        Set<String> AnnTermSetAllOK = new TreeSet<String>();
        for(String t: AnnTermSet1){
            if(ParentNodeMap.keySet().contains(t)){
                AnnTermSet1OK.add(t);
                AnnTermSetAllOK.add(t);
            }
        }
        for(String t: AnnTermSet2){
            if(ParentNodeMap.keySet().contains(t)) {
                AnnTermSet2OK.add(t);
                AnnTermSetAllOK.add(t);
            }
        }

        Set<String> AncestorsOfAnnTermSet1 = new TreeSet<String>();
        Set<String> AncestorsOfAnnTermSet2 = new TreeSet<String>();
        for(String go: AnnTermSet1OK){
            AncestorsOfAnnTermSet1.addAll(Common.extractAncestorTerms(go, ParentNodeMap));
        }
        for(String go: AnnTermSet2OK){
            AncestorsOfAnnTermSet2.addAll(Common.extractAncestorTerms(go, ParentNodeMap));
        }
        
        Set<String> DisjAncestorsOfAnnTermSet = new TreeSet<String>();        
        Set<String> ConjAncestorsOfAnnTermSet = new TreeSet<String>();        
        
        DisjAncestorsOfAnnTermSet.addAll(AncestorsOfAnnTermSet1);
        DisjAncestorsOfAnnTermSet.addAll(AncestorsOfAnnTermSet2);
        
        ConjAncestorsOfAnnTermSet.addAll(AncestorsOfAnnTermSet1);
        ConjAncestorsOfAnnTermSet.retainAll(AncestorsOfAnnTermSet2);
        
        double totalICofConj=0.0;
        double totalICofDisj=0.0;
        for(String t: ConjAncestorsOfAnnTermSet){
            if(!Term2ICMap.get(t).isInfinite()){
                totalICofConj+=Term2ICMap.get(t);
            }
        }
        for(String t: DisjAncestorsOfAnnTermSet){
            if(!Term2ICMap.get(t).isInfinite()){
                totalICofDisj+=Term2ICMap.get(t);
            }
        }
        
        sim = totalICofConj/totalICofDisj;
                
        return sim;
    }
    
    public static double calculateObjectSimilarity_Groupwise_simLP_Gentleman2005(String Obj1, String Obj2, String RootTerm, Map<String, Set<String>> Obj2TermMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap) {
        // TODO code application logic here
        double sim=0.0;
                
        Set<String> AnnTermSet1 = Obj2TermMap.get(Obj1);
        Set<String> AnnTermSet2 = Obj2TermMap.get(Obj2);
        
                        
        Set<String> AnnTermSet1OK = new TreeSet<String>();
        Set<String> AnnTermSet2OK = new TreeSet<String>();
        Set<String> AnnTermSetAllOK = new TreeSet<String>();
        for(String go: AnnTermSet1){
            if(ParentNodeMap.keySet().contains(go)){
                AnnTermSet1OK.add(go);
                AnnTermSetAllOK.add(go);
            }
        }
        for(String go: AnnTermSet2){
            if(ParentNodeMap.keySet().contains(go)) {
                AnnTermSet2OK.add(go);
                AnnTermSetAllOK.add(go);
            }
        }

        Set<String> AncestorsOfAnnTermSet1 = new TreeSet<String>();
        Set<String> AncestorsOfAnnTermSet2 = new TreeSet<String>();
        for(String go: AnnTermSet1OK){
            AncestorsOfAnnTermSet1.addAll(Common.extractAncestorTerms(go, ParentNodeMap));
        }
        for(String go: AnnTermSet2OK){
            AncestorsOfAnnTermSet2.addAll(Common.extractAncestorTerms(go, ParentNodeMap));
        }
        
        Set<String> DisjAncestorsOfAnnTermSet = new TreeSet<String>();        
        Set<String> ConjAncestorsOfAnnTermSet = new TreeSet<String>();        
        
        DisjAncestorsOfAnnTermSet.addAll(AncestorsOfAnnTermSet1);
        DisjAncestorsOfAnnTermSet.addAll(AncestorsOfAnnTermSet2);
        
        ConjAncestorsOfAnnTermSet.addAll(AncestorsOfAnnTermSet1);
        ConjAncestorsOfAnnTermSet.retainAll(AncestorsOfAnnTermSet2);
        
        Set<String> ConjAncestorsOfAnnTermSetDup = new TreeSet<String>();   
        ConjAncestorsOfAnnTermSetDup.addAll(ConjAncestorsOfAnnTermSet);
        
        Map<String, Set<String>> ConjAncestorsDAG_OutgoingNodeMap = new TreeMap<String, Set<String>>();
        for(String t: ConjAncestorsOfAnnTermSet){
            if(ParentNodeMap.containsKey(t)){
                Set<String> OutgoingNodeSet = new TreeSet<String>();
                for(NodeInteraction ni: ParentNodeMap.get(t)){
                    if(ConjAncestorsOfAnnTermSet.contains(ni.Node)){
                        OutgoingNodeSet.add(ni.Node);
                    }
                }
                ConjAncestorsDAG_OutgoingNodeMap.put(t, OutgoingNodeSet);
                //System.out.println(t + "\t" + OutgoingNodeSet.size() + "\t" + OutgoingNodeSet.toString());
            }
            
        }
        
        //System.out.println("ConjAncestorsDAG_OutgoingNodeMap.size(): " + ConjAncestorsDAG_OutgoingNodeMap.size() + "\t" + ConjAncestorsDAG_OutgoingNodeMap.keySet().toString());
        
        ArrayList<Stack> PathList = new ArrayList<Stack>();
        for(String t: ConjAncestorsOfAnnTermSet){
            if(t.compareTo(RootTerm)!=0){
                connectionPath = new Stack();
                connectionPaths = new ArrayList<Stack>();
                findAllPaths(t, RootTerm,ConjAncestorsDAG_OutgoingNodeMap);
                PathList.addAll(connectionPaths);
            }
        }
        int LongestPath = 0;
        for(Stack p: PathList){
            if(p.size()+1>LongestPath){
                LongestPath = p.size()+1;
            }
        }
        //for(Stack p: PathList){
        //    if(p.size()==LongestPath){
        //        for(int i=0;i<p.size();i++){
        //            System.out.print(p.get(i) + "\t");
        //        }
        //    }
        //}
                
        return (double) LongestPath;
    }
    
    public static double calculateObjectSimilarity_Groupwise_Kappa_Huang2007(String Obj1, String Obj2, Map<String, Set<String>> Obj2TermMap) {
        // TODO code application logic here
                
        Set<String> AnnTermSet1 = Obj2TermMap.get(Obj1);
        Set<String> AnnTermSet2 = Obj2TermMap.get(Obj2);
        
        Set<String> TermSet = new TreeSet<String>();
        for(Map.Entry<String, Set<String>> e: Obj2TermMap.entrySet()){
            TermSet.addAll(e.getValue());
        }
        char[] Obj1Vector = new char[TermSet.size()];
        char[] Obj2Vector = new char[TermSet.size()];
        
        int idx=0;
        int C11=0;
        int C10=0;
        int C01=0;
        int C00=0;
        for(String t: TermSet){
            if(AnnTermSet1.contains(t)){
                Obj1Vector[idx]=1;
            }else{
                Obj1Vector[idx]=0;
            }
            if(AnnTermSet2.contains(t)){
                Obj2Vector[idx]=1;
            }else{
                Obj2Vector[idx]=0;
            }
            if(Obj1Vector[idx]==1 && Obj2Vector[idx]==1){
                C11++;
            }else if(Obj1Vector[idx]==1 && Obj2Vector[idx]==0){
                C10++;
            }else if(Obj1Vector[idx]==0 && Obj2Vector[idx]==1){
                C01++;
            }else if(Obj1Vector[idx]==0 && Obj2Vector[idx]==0){
                C00++;
            }
            idx++;
        }
        int T12=C11+C10+C01+C00;
        double O12 = (double)(C11+C00)/T12;
        double A12 = (double)((C11+C10)*(C11+C01) + (C00+C10)*(C00+C01))/T12/T12;
        double K12 = (O12-A12)/(1-A12);
        
        return K12;
    }
    
    public static double calculateObjectSimilarity_Groupwise_CoSine_Chabalier2007(String Obj1, String Obj2, Map<String, Set<String>> Obj2TermMap) {
        // TODO code application logic here
                
        Set<String> AnnTermSet1 = Obj2TermMap.get(Obj1);
        Set<String> AnnTermSet2 = Obj2TermMap.get(Obj2);
        
        Set<String> TermSet = new TreeSet<String>();
        for(Map.Entry<String, Set<String>> e: Obj2TermMap.entrySet()){
            TermSet.addAll(e.getValue());
        }
        char[] Obj1Vector = new char[TermSet.size()];
        char[] Obj2Vector = new char[TermSet.size()];
        
        int idx=0;
        for(String t: TermSet){
            if(AnnTermSet1.contains(t)){
                Obj1Vector[idx]=1;
            }else{
                Obj1Vector[idx]=0;
            }
            if(AnnTermSet2.contains(t)){
                Obj2Vector[idx]=1;
            }else{
                Obj2Vector[idx]=0;
            }
            idx++;
        }
        
        double AB=0.0;
        double AA=0.0;
        double BB=0.0;
        
        int i;
        //System.out.println(Obj1Vector.toString());
        //System.out.println(Obj2Vector.toString());
        for(i=0;i<Obj1Vector.length;i++){
            AB+=Obj1Vector[i]*Obj2Vector[i];
            AA+=Obj1Vector[i]*Obj1Vector[i];
            BB+=Obj2Vector[i]*Obj2Vector[i];
        }
        //System.out.println(AB + "\t" + AA + "\t" + BB);
        double CoSine = AB/(AA + BB - AB);
        
        return CoSine;
    }
    // SUPB - 20160405 ADD - Implement some function to calculate similarity calculation between entities - END
    
    // SUPB - 20160405 ADD - Implement calculateSignificantEnrichmentTerms function - START
    
    public static double getFactorial(int n){
        if(n==0){
            return 1;
        }else{
            return n*getFactorial(n-1);
        }
    }
    
    //http://en.wikipedia.org/wiki/Combination
    public static double getCombination(int k, int n){
        double ts=1;
        for(int i=n;i>=(n-k+1);i--){
            ts*=i;
            //System.out.println(ts + "\t" + Double.MAX_VALUE);
            //if(Double.isInfinite(ts)){
            //    System.out.println(i);
            //    break;
            //}
        }
        
        double ms = getFactorial(k);
        //System.out.println("ts: " + ts);
        //System.out.println("ms: " + ms);
        return ts/ms;
    }
    
    //http://math.stackexchange.com/questions/330553/proof-that-the-hypergeometric-distribution-with-large-n-approaches-the-binomia
    public static double getProbOfHypergeometricTest(int N, int n, int K, int k){
        
        int i;
        double product1=1;
        for(i=1;i<=k;i++){
            product1*=(double)(K-k+i)/(N-k+i);
        }
        
        int j;
        double product2=1;
        for(j=1;j<=n-k;j++){
            product2*=(double)(N-K-(n-k)+j)/(N-n+j);
        }

        return getCombination(k,n)*product1*product2;
    }
    
    
    //https://en.wikipedia.org/wiki/Hypergeometric_distribution#Hypergeometric_test
    //int N=9048;//Number of gene in genome (population of size)
    //int K=114;//Number of gene in Genome which are annotated by the TermID
    //int n=16;//Number of gene in the gene set of interest (number of draws)
    //int k=1;//Number of gene in the gene set of interest which are annotated by the TermID (number of successes)
    
    //====> Can be used to test whether or not the overlap between two set of genes is statistically significant
    public static double getPvalueByHypergeometricTest_Over(int N, int n, int K, int k){
        int i;
        int min = (n>K)?K:n;
        double Pvalue = 0.0;
        for(i=k;i<=min;i++){
            Pvalue+=Common.getProbOfHypergeometricTest(N, n, K, i);
        }
        return Pvalue;
    }
    
    //Ref: http://cogsci.ucsd.edu/~dgroppe/STATZ/binomial_ztest.pdf
    
    public static double getPvalueByBinomialTest(int numberOfTrials, int numberOfSuccesses, double probability){
        int i;
        
        double Pvalue = 0.0;
        
        Pvalue=getCombination(numberOfSuccesses,numberOfTrials)*Math.pow(probability, numberOfSuccesses)*Math.pow(1-probability, numberOfTrials-numberOfSuccesses); 
        
        return Pvalue;
    }
    
    
    public static double getPvalueByBinomialTest_CommonsMath3(int numberOfTrials, int numberOfSuccesses, double probability){
        int i;
        
        double Pvalue = 0.0;
        
        
        //AlternativeHypothesis can be GREATER_THAN, LESS_THAN, TWO_SIDED
        
//        BinomialTest BT = new BinomialTest();
//        Pvalue = BT.binomialTest(numberOfTrials, numberOfSuccesses, probability, AlternativeHypothesis.TWO_SIDED);
        
        return Pvalue;
    }
    
    //http://en.wikipedia.org/wiki/Hypergeometric_distribution
    //See more in below
    //Relationship to Fisher's exact test[edit]
    //The test (see above[clarification needed]) based on the hypergeometric distribution (hypergeometric test) is identical to the corresponding one-tailed version of Fisher's exact test[2] ). Reciprocally, the p-value of a two-sided Fisher's exact test can be calculated as the sum of two appropriate hypergeometric tests (for more information see[3] ).
    //http://en.wikipedia.org/wiki/Fisher%27s_exact_test
    //https://sites.google.com/a/cidms.org/ipavs_tutorials/pathway-analysis-visualization-and-data-manipulation-tools/pathway-enrichment-algorithms
    
    public static double getPvalueByFishersExactTest(int N, int n, int K, int k){
        double Pvalue = 0.0;
        Pvalue=Common.getProbOfHypergeometricTest(N, n, K, k);
        return Pvalue;
    }
    
    public static Map<String, Double> calculateSignificantEnrichmentTerms(ArrayList<String> ObjectIDList, String Method){
        Set<String> DirectAnnTermIDSet = new TreeSet<String>();
        //Link Object to direct annotating terms
        for(String ObjectID: ObjectIDList){
            if(BasicData.Object2TermMap.containsKey(ObjectID)){
                DirectAnnTermIDSet.addAll(BasicData.Object2TermMap.get(ObjectID));
            }
        }

        Map<String, Double> Term2PvalueMap = new TreeMap<String, Double>();

        for(String TermID: DirectAnnTermIDSet){
            int N, n, K, k;
            N = BasicData.Object2TermMap.size();//Number of gene in genome
            K = BasicData.Term2ObjectMap.get(TermID).size();//Number of gene in Genome which are annotated by the TermID
            n = ObjectIDList.size();//Number of gene in the gene set of interest

            Set<String> AnnotatedObjSet = new TreeSet<>();
            AnnotatedObjSet.addAll(BasicData.Term2ObjectMap.get(TermID));
            AnnotatedObjSet.retainAll(ObjectIDList);
            k = AnnotatedObjSet.size();//Number of gene in the gene set of interest which are annotated by the TermID

            //Binomial
            double Pvalue = 0.0;
            if(Method.compareTo("Binomial")==0){
                int numberOfTrials = n;//21
                int numberOfSuccesses = k;//15
                double probability = (double)K/N;
                Pvalue=Common.getPvalueByBinomialTest(numberOfTrials, numberOfSuccesses, probability);
            }else{
                Pvalue=Common.getPvalueByFishersExactTest(N, n, K, k);
            }

            Term2PvalueMap.put(TermID, Pvalue);
        }
        return Term2PvalueMap;
    }
    // SUPB - 20160405 ADD - Implement calculateSignificantEnrichmentTerms function - END

    
    public static Set<String> calculateDCA(String c1, String c2, Map<String, Double> Term2ICMap, Map<String, ArrayList<NodeInteraction>> ParentNodeMap){
        //System.out.println("Calculating similarity of " + c1 + " and " + c2);
        ArrayList<Interaction> SubDAG1 = new ArrayList<Interaction>();
        ArrayList<Interaction> SubDAG2 = new ArrayList<Interaction>();
        Set<String> InaSet1 = new TreeSet<String>();
        Set<String> InaSet2 = new TreeSet<String>();
        
        Common.extractSubDAG(c1, ParentNodeMap, SubDAG1, InaSet1);
        Common.extractSubDAG(c2, ParentNodeMap, SubDAG2, InaSet2);
        
        Set<String> Anc = calculateCommonAncestors(SubDAG1, SubDAG2);
        //System.out.println("Anc.toString(): " + Anc.toString());
        ArrayList<String> sortedAnc = sortDescByIC(Anc, Term2ICMap);
        //System.out.println("sortedAnc.toString(): " + sortedAnc.toString());
        
        //System.out.println("SubDAG1.size(): " + SubDAG1.size());
        //System.out.println("SubDAG2.size(): " + SubDAG2.size());
        
        Set<String> CommonDisjAnc=new TreeSet<String>();
        for(int i=0;i<sortedAnc.size();i++){
            String a = sortedAnc.get(i);
            boolean isDisj=true;
            for(String cda:CommonDisjAnc){
                boolean isDisj1 = isDisjAnc(c1, a, cda, SubDAG1);
                boolean isDisj2 = isDisjAnc(c2, a, cda, SubDAG2);
                //System.out.println(isDisj1 + "\t" + isDisj2);
                isDisj = isDisj && (isDisj1||isDisj2);
            }
            if(isDisj){
                CommonDisjAnc.add(a);
                //System.out.println("CommonDisjAnc.toString(): " + CommonDisjAnc.toString());
            }
        }
    
        return CommonDisjAnc;
    }

    static void ExtractGene2GORelation(String SubOntology, int Species, String FileName, String OutputFile) {
        try{
            BufferedReader br = new BufferedReader(new FileReader(FileName));
                        
            PrintWriter pw = new PrintWriter(new FileOutputStream(OutputFile),true);
            
            String str="";
            //System.out.println(SubOntology + "\t" + Species + "\t" + IgnoreIEA);
            br.readLine();//Ignore first line
            while ((str = br.readLine()) != null) {
                
                String[] s = str.split("\t");
                int tax_id = Integer.parseInt(s[0].trim());
                String GeneID = s[1].trim();
                String GO_ID = s[2].trim();
                String Evidence = s[3].trim();
                String Qualifier = s[4].trim();
                String GO_term = s[5].trim();
                String PubMed = s[6].trim();
                String Category = s[7].trim();
                                
                if(tax_id!=Species) continue;
                
                //System.out.println(Category);
                if(SubOntology.contains("BP") && !Category.contains("Process")) continue;
                if(SubOntology.contains("CC") && !Category.contains("Component"))continue;
                if(SubOntology.contains("MF") && !Category.contains("Function"))continue;
                              
                pw.println(GeneID + "\t" + GO_ID + "\t" + Evidence);
                
            }
            pw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    static void ExtractOMIM2HPORelation(String DiseaseSource, String FileName, String OutputFile) {
        try{
            BufferedReader br = new BufferedReader(new FileReader(FileName));
                        
            PrintWriter pw = new PrintWriter(new FileOutputStream(OutputFile),true);
            
            String str="";
            //str = br.readLine();//Ignore first line
            while ((str = br.readLine()) != null) {
                String[] s = str.split("\t");
                
                if(DiseaseSource.compareTo(s[0].trim())!=0) continue;
                String ID = s[1].trim();
                String PhenotypeName = s[2].trim();
                String Unknown1 = s[3].trim();
                String HPOID = s[4].trim();
                String DiseaseID = s[5].trim();
                String Evidence = s[6].trim();
                String Unknown2 = s[7].trim();
                String Unknown3 = s[8].trim();
                String Unknown4 = s[9].trim();
                String Unknown5 = s[10].trim();
                String Unknown6 = s[11].trim();
                String Date = s[12].trim();
                String Curator = s[13].trim();
                 
                pw.println(ID + "\t" + HPOID + "\t" + Evidence);
            }
            pw.close();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    static void ExtractGene2DORelation(String FileName, String OutputFile) {
        try{
            BufferedReader br = new BufferedReader(new FileReader(FileName));
                        
            PrintWriter pw = new PrintWriter(new FileOutputStream(OutputFile),true);
            
            String str="";
            str = br.readLine();//Ignore first line
            while ((str = br.readLine()) != null) {
                String[] s = str.split("\t");
                
                String DOID = "DOID:" + s[0].trim();
                String GeneID = s[1].trim();
                String PubMedID = s[2].trim();
                String RIFtext = s[3].trim();
                String Evidence = "";
                
                pw.println(GeneID + "\t" + DOID + "\t" + Evidence);
            }
            pw.close();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
