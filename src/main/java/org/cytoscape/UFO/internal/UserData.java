

package org.cytoscape.UFO.internal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import org.cytoscape.UFO.Base.GENE;
import org.cytoscape.UFO.Base.Interaction;

/**
 *
 * @author SUPB
 */
public class UserData {
    public static ArrayList<Interaction> OriginalNetwork;
    public static ArrayList<Interaction> NormalizedNetwork;

    public static ArrayList<String> OriginalNetworkNode;
    public static ArrayList<GENE> NormalizedNetworkNode;
    public static ArrayList<GENE> NormalizedNetworkNode_EntrezIDIndex;
    public static ArrayList<GENE> NormalizedNetworkNode_UniProtACIndex;
    public static ArrayList<GENE> NormalizedNetworkNode_OfficialSymbolIndex;

    public static ArrayList<String> KDGUserInput;
    public static ArrayList<GENE> KDGUserInputNormalized;

    public static ArrayList<String> TGUserInput;
    public static ArrayList<GENE> TGUserInputNormalized;

    public static String Network_FileNameFullPath;//="BIND_HPRD_BioGRID_EntrezID_UnWeighted_UnDirected_Human_Distinct.txt";//BIND_HPRD_BioGRID_EntrezID_UnWeighted_UnDirected_Human_Distinct.txt
    public static String Network_FileName;

    public static String term="";

    public static ArrayList<String> MissingNetworkGenes;
    
    public static ArrayList<String> MissingKnownDiseaseGenes;
    public static ArrayList<String> MissingTestGenes;

    public static ArrayList<String> MissingGenes;
    public static String MissingGeneIdentifier;

    public static void loadNormalizedGraph(){
        try{
            BufferedReader br= new BufferedReader(new FileReader("Directed_Normalized_Network1.txt"));
            String str=null;

            MainData.NormalizedGraph=new ArrayList<Interaction>();
            Interaction inatemp;
            String srcnode="";
            String dstnode="";
            double weight=0.0;
            System.out.println("Normalized Graph data file is being loaded...!");

            while((str=br.readLine())!=null){
                //System.out.println(numofina + ": " + str);
                StringTokenizer st = new StringTokenizer(str,"\t");
                //System.out.println(st.nextToken());
                if(st.countTokens()==3){
                    srcnode=st.nextToken();
                    weight=Double.parseDouble(st.nextToken());
                    dstnode=st.nextToken();

                    inatemp= new Interaction();

                    inatemp.NodeSrc=srcnode;
                    inatemp.NodeDst=dstnode;
                    inatemp.Weight=weight;
                    MainData.NormalizedGraph.add(inatemp);
                }
            }
            br.close();
            JOptionPane.showMessageDialog(null, "Total interaction of " + MainData.NormalizedGraph.size());
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error in loading Normalized Graph: " + e.toString());
        }
    }

    public static void loadAllSeedGenes(){
        try{
            BufferedReader br= new BufferedReader(new FileReader("AllSeedGenes.txt"));
            String str=null;
            int i;
            MainData.TrainingGene= new ArrayList<GENE>();
            MainData.AllSeedGenes= new ArrayList<ArrayList<GENE>>();

            GENE gene;
            
            System.out.println("Normalized Graph data file is being loaded...!");

            while((str=br.readLine())!=null){
                //System.out.println(numofina + ": " + str);
                StringTokenizer st = new StringTokenizer(str,"\t");
                //System.out.println(st.nextToken());
                if(st.countTokens()==76){
                    gene=new GENE();
                    gene.OfficialSymbol=st.nextToken();
                    MainData.TrainingGene.add(gene);
                    ArrayList<GENE> SeedGenes = new ArrayList<GENE>();
                    for(i=0;i<75;i++){
                        gene=new GENE();
                        gene.OfficialSymbol=st.nextToken();
                        SeedGenes.add(gene);
                    }
                    MainData.AllSeedGenes.add(SeedGenes);
                }
            }
            br.close();
            JOptionPane.showMessageDialog(null, "Total seed set of " + MainData.AllSeedGenes.size());
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error in loading Traing & Seed Genes: " + e.toString());
        }
    }
}
