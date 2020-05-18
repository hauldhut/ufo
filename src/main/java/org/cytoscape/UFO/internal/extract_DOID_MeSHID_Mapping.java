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
import org.cytoscape.UFO.Base.Term;

/**
 *
 * @author Administrator
 */
public class extract_DOID_MeSHID_Mapping {
    public static void main(String[] args) {
        try{
            int i,j;
            
            
            String DOFileName = "/Users/admin/Data/Ontology/DO/doid.obo.txt";
            String RootTerm = "DOID:4";
            //ArrayList<Interaction> DODAG = Common.loadDAG(DODAGFileName);
            BasicData.TermID2InfoMap = Common.loadOntologyData_ForMapping(DOFileName);
            System.out.println("BasicData.TermID2InfoMap.size(): " + BasicData.TermID2InfoMap.size());
    
            PrintWriter pw = new PrintWriter(new FileOutputStream(DOFileName + "_IDMapping.txt"),true);
            int c=0;
            Map<String, Set<String>> MeSHID2DOIDMap = new TreeMap<>();
            for(Entry<String, Term> e: BasicData.TermID2InfoMap.entrySet()){
                if(e.getValue().MappedIDSet.size()>0){
                    System.out.println(e.getKey() + "\t" + e.getValue().MappedIDSet.toString());
                    pw.println(e.getKey() + "\t" + e.getKey() + "\t" + e.getValue().MappedIDSet.toString().substring(1,e.getValue().MappedIDSet.toString().length()-1));
                    c++;
                    
                    for(String meshid: e.getValue().MappedIDSet){
                        if(MeSHID2DOIDMap.containsKey(meshid)){
                            MeSHID2DOIDMap.get(meshid).add(e.getKey());
                        }else{
                            Set<String> doidset = new TreeSet<>();
                            doidset.add(e.getKey());
                            MeSHID2DOIDMap.put(meshid, doidset);
                        }
                    }
                }
            }
            pw.close();
            System.out.println("c: " + c);
            System.out.println("MeSHID2DOIDMap.size(): " + MeSHID2DOIDMap.size());
            
            
            //Map with GWAS MeSH-to-SNP
            String BiNet_FileName = "/Users/admin/Data/GWAS/CAUSALdb/EUR_Assoc.txt"; 
            BufferedReader br = new BufferedReader(new FileReader(BiNet_FileName));
            String str = null;
            Map<String, Set<String>> MeSHID2SNPMap = new TreeMap<>();
            
            
            while ((str = br.readLine()) != null) {
                String[] s = str.split("\t");
                String meshid = s[0].trim();
                String snpstr = s[2].trim();
                String[] snparr = snpstr.split(", ");
                Set<String> snpset = new TreeSet<>();
                for(i=0;i<snparr.length;i++){
                    snpset.add(snparr[i].trim());
                }
                MeSHID2SNPMap.put(meshid, snpset);
            }
            br.close();
            
            System.out.println("MeSHID2SNPMap.size() :" + MeSHID2SNPMap.size());
            
            Set<String> CommonMeSHIDSet = new TreeSet<>();
            CommonMeSHIDSet.addAll(MeSHID2DOIDMap.keySet());
            CommonMeSHIDSet.retainAll(MeSHID2SNPMap.keySet());
            
            System.out.println("CommonMeSHIDSet.size() :" + CommonMeSHIDSet.size() + "\t" + CommonMeSHIDSet.toString());
            
            //Map with DOID-2-Enh
            Set<String> CommonDOIDSet = new TreeSet<>();
            for(String meshid: CommonMeSHIDSet){
                if(MeSHID2DOIDMap.containsKey(meshid)){
                    CommonDOIDSet.addAll(MeSHID2DOIDMap.get(meshid));
                }
            }
            System.out.println("CommonDOIDSet.size() :" + CommonDOIDSet.size() + "\t" + CommonDOIDSet.toString());
            
            //read DOID-2-Enh
            String BiNet_FileName2 = "/Users/admin/Java/DTI/Data/BipartiteNets/Disease2Enhancers.txt"; 
            br = new BufferedReader(new FileReader(BiNet_FileName2));
            
            Map<String, Set<String>> DOID2EnhMap = new TreeMap<>();

            while ((str = br.readLine()) != null) {
                String[] s = str.split("\t");
                String doid = s[0].trim();
                String enhstr = s[2].trim();
                String[] enharr = enhstr.split(", ");
                Set<String> enhset = new TreeSet<>();
                for(i=0;i<enharr.length;i++){
                    enhset.add(enharr[i].trim());
                }
                DOID2EnhMap.put(doid, enhset);
            }
            br.close();
            
            System.out.println("DOID2EnhMap.size() :" + DOID2EnhMap.size());
            
            CommonDOIDSet.retainAll(DOID2EnhMap.keySet());
            
            System.out.println("CommonDOIDSet.size() :" + CommonDOIDSet.size() + "\t" + CommonDOIDSet.toString());
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
