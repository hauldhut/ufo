/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cytoscape.UFO.internal;

import java.io.File;
import java.io.InputStream;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author "MinhDA"
 */
class LoadDataResourcesTask implements Task{

    private volatile boolean interrupted;
    private CyNetworkManager cyNetworkManager;
    private CyNetworkReaderManager cyNetworkReaderManager;
    private CyNetworkFactory cyNetworkFactory;
    private CyNetworkNaming namingUtil;

    public LoadDataResourcesTask(CyNetworkManager cyNetworkManager, CyNetworkReaderManager cyNetworkReaderManager, CyNetworkFactory cyNetworkFactory, CyNetworkNaming namingUtil) {
        this.cyNetworkManager = cyNetworkManager;
        this.cyNetworkReaderManager = cyNetworkReaderManager;
        this.cyNetworkFactory = cyNetworkFactory;
        this.namingUtil = namingUtil;
    }
    
    
    
    @Override
    public void run(TaskMonitor taskMonitor){
        taskMonitor.setTitle("Loading Resources");
        try{
            taskMonitor.setStatusMessage("Loading Default Data...!");
            if(MainData.Species==null || MainData.Species.size()==0){
                taskMonitor.setStatusMessage("Loading Default Data...!");
                MainData.Species = new Resource().readSpeciesList("/Species.txt");
            }
            
//            taskMonitor.setStatusMessage("Loading Default Gene/Protein Networks...!");
//            for(CyNetwork net : cyNetworkManager.getNetworkSet()){
//                if(net.getRow(net).getRaw("name").toString().equals(BasicData.InputNetworkFileName)){
//                    cyNetworkManager.destroyNetwork(net);
//                }
//            }
//            InputStream in = getClass().getResourceAsStream("/" + BasicData.InputNetworkFileName);
//            Common.loadNetworkFromFile(cyNetworkFactory, namingUtil, cyNetworkManager, in, "Default_Human_PPI_Network");
//
//            if(BasicData.AllGene_EntrezID ==null || BasicData.AllGene_EntrezID.size()==0){
//                taskMonitor.setStatusMessage("Loading Gene Information Database...!");
//                new Resource().loadAllGenes("EntrezID", "/EntrezGeneInfo.txt");
//            }
//            if(BasicData.Phenotype2Genes_Full ==null || BasicData.Phenotype2Genes_Full.size()==0){
//                taskMonitor.setStatusMessage("Loading Disease Information Database...!");
//                BasicData.Phenotype2Genes_Full = new Resource().loadPhenotypeInfo("/Phenotype2Genes_Full_UMLS.txt");
//            }       
//            if(BasicData.AllGeneChromosome == null || BasicData.AllGeneChromosome.size()==0){
//                taskMonitor.setStatusMessage("Loading Gene-Chromosome Database...!");
//                new Resource().loadAllGenes_Chromosomes("/GeneChromosome.txt");
//            }
//            taskMonitor.setStatusMessage("Loading Case studies...!");
//            new Resource().loadCaseStudies("/Hypertension.txt");
//            
//            
//            
//            //Load Disease 2 gene Network
//            if(BasicData.Phenotype2Genes_MIM==null || BasicData.Phenotype2Genes_MIM.size()==0){
//                taskMonitor.setStatusMessage("Loading MIM - Gene Association Database...!");
//                BasicData.Phenotype2Genes_MIM = new Resource().loadPhenotype2GeneNetwork("/Phenotype2Genes.txt");
//            }
//            if (BasicData.Phenotype2Genes_DisGenNET == null || BasicData.Phenotype2Genes_DisGenNET.size() == 0) {
//                taskMonitor.setStatusMessage("Loading DisGenNET - Gene Association Database...!");
//                BasicData.Phenotype2Genes_DisGenNET = new Resource().loadPhenotype2GeneNetwork("/Disease2Genes_UMLS_all.txt");
//            }
//
//            //Load Disease Similarity Network
//            taskMonitor.setStatusMessage("Loading Disease Similarity Database...!");
//            BasicData.PhenotypeNetwork_MIM = new Resource().loadPhenotypeNetwork("/Disease_Similarity_Network_5.sif");
//            BasicData.NetworkPhenotypeSet_MIM = BasicData.NetworkPhenotypeSet;
//
//            if(BasicData.Complex2Genes==null || BasicData.Complex2Genes.size()==0){
//                taskMonitor.setStatusMessage("Loading Complex - Gene Association Database...!");
//                BasicData.Complex2Genes = new Resource().loadComplex2Gene("Human","/allComplexes.csv");
//            }
//            
//            if(BasicData.DO2Genes==null || BasicData.DO2Genes.size()==0){
//                taskMonitor.setStatusMessage("Loading Disease Ontology - Gene Association Database...!");
//                BasicData.DO2Genes = new Resource().loadDO2Gene("Human","/DO2Genes.txt");
//            }

        }catch(Exception e){
            System.out.println("Error while loading data resources" + e.toString());
        }
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
