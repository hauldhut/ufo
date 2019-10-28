/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cytoscape.UFO.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import org.cytoscape.UFO.Base.GENE;
import org.cytoscape.UFO.Base.GO;
import org.cytoscape.UFO.Base.GeneRIFs;
import org.cytoscape.UFO.Base.Interaction;
import org.cytoscape.UFO.Base.NodeInteraction;
import org.cytoscape.UFO.Base.Term;

/**
 *
 * @author "MinhDA"
 */
public class BasicData {
    public static ArrayList<GENE> AllGene = new ArrayList<>();
    public static ArrayList<GENE> AllGene_EntrezIDIndex = new ArrayList<>();
    public static ArrayList<GENE> AllGene_UniProtACIndex = new ArrayList<>();
    public static ArrayList<GENE> AllGene_OfficialSymbolIndex = new ArrayList<>();
    public static ArrayList<GENE> AllGene_EntrezID = new ArrayList<>();
    public static ArrayList<GENE> AllGene_UniProtAC = new ArrayList<>();

    public static String AllGene_FileName="Data\\Genes\\AllGene_EntrezID_UniProt.txt";
    public static String AllGene_EntrezID_FileName="Data\\Genes\\AllGene_EntrezID.txt";
    public static String AllGene_UniProtAC_FileName="Data\\Genes\\AllGene_UniProtAC.txt";
    
    public static String AllGene_Mammalia_FileName="Data\\Genes\\All_Mammalia.gene_info";

    public static ArrayList<GENE> AllGeneChromosome = new ArrayList<>();
    public static String Gene_Chromosome_FileName="Data\\Genes\\Gene_Chromosome2.txt";

    public static ArrayList<GeneRIFs> AllGeneRIFs = new ArrayList<>();
    public static String GeneRIFs_FileName="Data\\Genes\\generifs_basic";

    //FTP. Download GeneRIFs via FTP
    public static String hostname = "ftp.ncbi.nih.gov";//reader.readLine();
    public static String username = "anonymous";//reader.readLine();
    public static String password = "hauldhut@yahoo.com";//reader.readLine();
    public static String remotedirectory="/gene/GeneRIF/";
    public static String localdirectory="Data\\Genes\\";
    public static String filename="generifs_basic.gz";
    
    public static ArrayList<GO> AllGO = new ArrayList<>();
    public static ArrayList<GO> AllGO_EntrezID = new ArrayList<>();
    public static ArrayList<GO> AllGO_UniProtAC = new ArrayList<>();

    public static String AllGO_FileName="";
    
    public static String AllGO_EntrezID_FileName="Data\\GO\\gene2go";//GO_EntrezID
    public static String AllDO_EntrezID_FileName="";
    public static String DiseaseID2HPOMapping_FileName="";
    
    public static String Ontology_FileName="";
    public static String Annotation_FileName="";
    public static Map<String, Term> TermID2InfoMap = new TreeMap<>();
    public static Map<String, String> TermID2NameMap = new TreeMap<>();
    public static Map<String, String> ObjectID2NameMap = new TreeMap<>();
    public static ArrayList<String> validSelTermIDList = new ArrayList<>();
    public static ArrayList<String> validSelObjectIDList = new ArrayList<>();
    public static ArrayList<String> validSelObject1IDList = new ArrayList<>();
    public static ArrayList<String> validSelObject2IDList = new ArrayList<>();
    
    public static Map<String, Double> Term2PvalueMap = new TreeMap<>();
    public static Map<String, Double> Term2AdjustedPvalueMap = new TreeMap<>();
            
    public static ArrayList<Interaction> GODAG = new ArrayList<>();
    public static ArrayList<Interaction> DAG = new ArrayList<>();
    public static Set<String> DAGNodeSet = new TreeSet<>();
    
    public static String RootTermID = "";
    public static Map<String, ArrayList<NodeInteraction>> ChildNodeMap = new TreeMap<>();
    public static Map<String, ArrayList<NodeInteraction>> ParentNodeMap = new TreeMap<>();
    public static Map<String, Double> Term2ICMap = new TreeMap<>();
    public static double MaxIC;
    public static double MinIC;
    
    public static Map<String, Set<String>> Object2TermMap = new TreeMap<>();
    public static Map<String, Set<String>> Term2ObjectMap = new TreeMap<>();
    public static boolean IgnoreIEA = true;
    public static String SubOntology = "";
       
    public static String AllGO_UniProtAC_FileName="Data\\GO\\AllGO_UniProtAC.txt";
    public static String GO_FileName="Data\\GO\\go.obo";
    public static String DO_FileName="";
    public static String HPO_FileName="";
    public static String GODAG_FileName="";
    public static Map<String, String> GORoot = new TreeMap<>();
    public static ArrayList<Interaction> GODAG_BP = new ArrayList<>();
    public static ArrayList<Interaction> GODAG_CC = new ArrayList<>();
    public static ArrayList<Interaction> GODAG_MF = new ArrayList<>();
    
    public static Set<String> SignificantTermSet1 = new TreeSet<String>();
    public static Set<String> SignificantTermSet2 = new TreeSet<String>();
    public static double ObjectSetSim = 0.0;
    
    public static void loadAllGenes(String GeneIdentifier){
        try{
            int i;
            if(GeneIdentifier.compareTo("EntrezID")==0){
                BasicData.AllGene_FileName=BasicData.AllGene_EntrezID_FileName;
                BasicData.AllGene_EntrezID = new ArrayList<>();
            }else{
                BasicData.AllGene_FileName=BasicData.AllGene_UniProtAC_FileName;
                BasicData.AllGene_UniProtAC = new ArrayList<>();
            }
            File f=new File(BasicData.AllGene_FileName);
            if(f.exists()==false){
                f.createNewFile();
            }
            BufferedReader br= new BufferedReader(new FileReader(BasicData.AllGene_FileName));
            String str=null;
            int geneindex=0;
            GENE gene;
            String ensemblid="";
            String geneid="";
            String officialsymbol="";
            String organism="";
            String uniprotid="";
            System.out.println("Human Gene data file is being loaded...!");
            while((str=br.readLine())!=null){
                StringTokenizer st = new StringTokenizer(str,"\t");
                if(st.countTokens()==4){
                    gene= new GENE();
                    geneid=st.nextToken();
                    officialsymbol=st.nextToken();
                    organism=st.nextToken();
                    StringTokenizer alternatesymbols=new StringTokenizer(st.nextToken(),", ");
                    gene.Organism=organism;
                    gene.OfficialSymbol=officialsymbol;
                    while(alternatesymbols.hasMoreTokens()){
                        gene.AlternateSymbols.add(alternatesymbols.nextToken());
                    }
                    gene.Tag=Integer.toString(geneindex);
                    if(GeneIdentifier.compareTo("EntrezID")==0){
                        gene.EntrezID=geneid;
                        BasicData.AllGene_EntrezID.add(gene);
                    }else{
                        gene.UniProtAC=geneid;
                        BasicData.AllGene_UniProtAC.add(gene);
                    }
                    geneindex++;
                }
            }
            br.close();
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error while loading AllGene Database: " + e.toString());
        }
    }
    
    public static void loadGOs(String GeneIdentifier){
        try{
            int i;
            if(GeneIdentifier.compareTo("EntrezID")==0){
                BasicData.AllGO_FileName=BasicData.AllGO_EntrezID_FileName;
                BasicData.AllGO_EntrezID = new ArrayList<>();
            }else{
                BasicData.AllGO_FileName=BasicData.AllGO_UniProtAC_FileName;
                BasicData.AllGO_UniProtAC = new ArrayList<>();
            }
            BufferedReader br= new BufferedReader(new FileReader(BasicData.AllGO_FileName));
            //PrintWriter pw = new PrintWriter(new FileOutputStream(filename.replace(".txt", "_Output.txt")),false);
            String str=null;
            int goindex=0;
            String entrezid="";
            String uniprotac="";
            String officialsymbol="";
            String taxon="";
            String goid="";
            String goname="";
            String category="";
            String evidence="";
            System.out.println("Gene Ontology data file is being loaded...!");
            if(GeneIdentifier.compareTo("EntrezID")==0){
                br.readLine();//Ignore first line
                while((str=br.readLine())!=null){
                    StringTokenizer st = new StringTokenizer(str,"\t");
                    if(st.countTokens()==8){
                        taxon=st.nextToken();
                        entrezid=st.nextToken();
                        goid=st.nextToken();
                        evidence=st.nextToken();
                        st.nextToken();
                        goname=st.nextToken();
                        st.nextToken();
                        category=st.nextToken();
                        uniprotac="";
                        officialsymbol="";
                        GO go = new GO(entrezid,uniprotac,officialsymbol,taxon,goid,goname,category,evidence);
                        goindex++;
                        BasicData.AllGO_EntrezID.add(go);
                    }
                }
            }else{
                while((str=br.readLine())!=null){
                    StringTokenizer st = new StringTokenizer(str,"\t");
                    if(st.countTokens()==7){

                        entrezid="";
                        uniprotac=st.nextToken();
                        officialsymbol=st.nextToken();
                        taxon=st.nextToken();
                        goid=st.nextToken();
                        goname=st.nextToken();
                        category=st.nextToken();
                        evidence=st.nextToken();

                        GO go = new GO(entrezid,uniprotac,officialsymbol,taxon,goid,goname,category,evidence);
                        goindex++;
                        BasicData.AllGO_UniProtAC.add(go);
                    }
                }
            }
            br.close();

        }catch(Exception e){
        }
    }
    
    public static void mergeMainAllGenes(){
        int i,j;
        BasicData.AllGene = new ArrayList<>();
        ArrayList<GENE> Difference = new ArrayList<>();

        if(BasicData.AllGene_EntrezID.size()>0 && BasicData.AllGene_UniProtAC.isEmpty()){
            for(i=0;i<BasicData.AllGene_EntrezID.size();i++){
                BasicData.AllGene.add(MainData.assignNormalizedNetworkNode(BasicData.AllGene_EntrezID.get(i)));
            }
            return;
        }else if(BasicData.AllGene_EntrezID.isEmpty() && BasicData.AllGene_UniProtAC.size()>0){
            for(i=0;i<BasicData.AllGene_UniProtAC.size();i++){
                BasicData.AllGene.add(MainData.assignNormalizedNetworkNode(BasicData.AllGene_UniProtAC.get(i)));
            }
            return;
        }else if(BasicData.AllGene_EntrezID.isEmpty() && BasicData.AllGene_UniProtAC.isEmpty()){
            return;
        }

        Common.preprocessGeneList(BasicData.AllGene_EntrezID, "OfficialSymbol");
        Common.sortQuickGeneListInAsc(BasicData.AllGene_EntrezID);
        Common.preprocessGeneList(BasicData.AllGene_UniProtAC, "OfficialSymbol");
        Common.sortQuickGeneListInAsc(BasicData.AllGene_UniProtAC);
        for(i=0;i<BasicData.AllGene_UniProtAC.size();i++){
            GENE g = BasicData.AllGene_UniProtAC.get(i);
            BasicData.AllGene.add(MainData.assignNormalizedNetworkNode(g));
        }
        
        for(i=0;i<BasicData.AllGene_EntrezID.size();i++){
            GENE g=new GENE();
            ArrayList<Integer> posarr = Common.searchUsingBinaryGENEArray(BasicData.AllGene_EntrezID.get(i).OfficialSymbol, BasicData.AllGene);
            if(posarr.size()>0){
                boolean exist=false;
                for(j=0;j<posarr.size();j++){//Check if identical
                    if(BasicData.AllGene_EntrezID.get(i).Organism.compareToIgnoreCase(BasicData.AllGene.get(posarr.get(j)).Organism)==0){
                        if(BasicData.AllGene.get(posarr.get(j)).EntrezID.trim().compareTo("")==0){
                            BasicData.AllGene.get(posarr.get(j)).EntrezID=BasicData.AllGene_EntrezID.get(i).EntrezID;
                        }else{//There are more than one Entrez ID for the same Official Symbol and Organism (One is new, one is old)
                            if(BasicData.AllGene.get(posarr.get(j)).EntrezID.compareTo(BasicData.AllGene_EntrezID.get(i).EntrezID)!=0){
                                g=MainData.assignNormalizedNetworkNode(BasicData.AllGene.get(posarr.get(j)));
                                g.EntrezID=BasicData.AllGene_EntrezID.get(i).EntrezID;
                                BasicData.AllGene.add(g);
                            }
                        }
                        exist=true;
                    }
                }
                if(exist==false){//not identical --> difference
                    g=BasicData.AllGene_EntrezID.get(i);
                    Difference.add(MainData.assignNormalizedNetworkNode(g));
                }
            }else{//not found --> difference
                g=BasicData.AllGene_EntrezID.get(i);
                Difference.add(MainData.assignNormalizedNetworkNode(g));
            }
        }
        //Add differences to 
        for(i=0;i<Difference.size();i++){
            BasicData.AllGene.add(MainData.assignNormalizedNetworkNode(Difference.get(i)));
        }
    }
}
