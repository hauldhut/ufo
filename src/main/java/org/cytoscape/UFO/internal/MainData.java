/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cytoscape.UFO.internal;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.cytoscape.UFO.Base.Corpus;
import org.cytoscape.UFO.Base.GENE;
import org.cytoscape.UFO.Base.Interaction;
import org.cytoscape.UFO.Base.OMIM;
import org.cytoscape.UFO.Base.Species;
import org.cytoscape.UFO.Statistic.ROC;
import org.cytoscape.model.CyNode;

/**
 *
 * @author "MinhDA"
 */
public class MainData {
    public static Map<String,Species> Species = new TreeMap<>();
    public static Map<String,Corpus> Corpus = new TreeMap<>();
    public static Map<String,String> BGCats = new TreeMap<>();
    public static Map<String,String> BTCats = new TreeMap<>();
    public static Map<String,String> BGMets = new TreeMap<>();
    
    public static Map<String,String> BGCatsName2ID = new TreeMap<>();
    public static Map<String,String> BTCatsName2ID = new TreeMap<>();
    public static Map<String,String> BGMetsName2ID = new TreeMap<>();
    
    public static Map<String,Set<String>> BGCat2BGMets = new TreeMap<>();
    public static Map<String,Set<String>> BGMet2BTCats = new TreeMap<>();
    
    public static ArrayList<GENE> AllKnownGenes;
    public static ArrayList<OMIM> AllOMIMRecords;
    public static ArrayList<CyNode> MatchedGenes;
    
    public static ArrayList<GENE> TrainingGene;//All Known Disease Genes in Network
    public static ArrayList<ArrayList<GENE>> LinkageIntervalGenes;

    //Find Seed Genes for each run (In each run, one training gene is held out and the remaining set is seed genes
    public static ArrayList<ArrayList<GENE>> AllSeedGenes;
    public static ArrayList<GENE> SeedGenes;
    //To store for all run (number of runs equal to number of seed genes)
    public static ArrayList<ArrayList<GENE>> AllRuns;

    public static boolean isDirected;
    public static ArrayList<Interaction> ConvertedGraph;

    public static boolean isWeighted=true;
    
    public static ArrayList<Interaction> NormalizedGraph;

    //Store all genes in normalized network before and after each run of validation
    public static ArrayList<GENE> ValidationScore;
    //Store all genes in normalized network before and after network prioritization (only 1 time)
    public static ArrayList<GENE> PrioritizationScore;

    public static int TestGeneType;

    public static ArrayList<GENE> AllTestGenes;//Store All Test Genes for each run (each held out)

    public static double alpha;

    public static ROC myROC;

    public static ArrayList<ArrayList<Double>> OutEdgeWeights;     //Weights of link from current node to target nodes
    public static ArrayList<ArrayList<Integer>> OutNodeIndices;   //Target node indices of current node
    public static ArrayList<ArrayList<String>> OutNodes;           //Target nodes of current node

    public static int NumOfNeighbors;

    public static String vsNetworkName="Scored Network Visual Style";

    public static String GeneFormat;

    public static String NetworkGeneIdentifier;

    public static boolean AnalysisOK=false;
    public static String AnalysisErrorMsg="";

    public static GENE assignNormalizedNetworkNode(GENE NormalizedNetworkNode){
                
        GENE g = new GENE();
        g.AlternateSymbols = NormalizedNetworkNode.AlternateSymbols;
        g.Band = NormalizedNetworkNode.Band;
        g.Chromosome = NormalizedNetworkNode.Chromosome;
        g.DistanceToSeed=NormalizedNetworkNode.DistanceToSeed;
        g.EntrezID=NormalizedNetworkNode.EntrezID;
        g.GeneEnd=NormalizedNetworkNode.GeneEnd;
        g.GeneStart = NormalizedNetworkNode.GeneStart;
        g.InNetwork = NormalizedNetworkNode.InNetwork;
        g.Index = NormalizedNetworkNode.Index;
        g.NetworkID = NormalizedNetworkNode.NetworkID;
        g.IsHeldout=NormalizedNetworkNode.IsHeldout;
        g.IsSeed=NormalizedNetworkNode.IsSeed;
        g.IsTest=NormalizedNetworkNode.IsTest;
        g.Name=NormalizedNetworkNode.Name;
        g.OfficialSymbol=NormalizedNetworkNode.OfficialSymbol;
        g.Organism=NormalizedNetworkNode.Organism;
        g.Rank=NormalizedNetworkNode.Rank;
        g.Score=NormalizedNetworkNode.Score;
        g.UniProtAC=NormalizedNetworkNode.UniProtAC;
        g.Tag=NormalizedNetworkNode.Tag;
        return g;
    }

}
