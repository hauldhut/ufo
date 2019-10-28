/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cytoscape.UFO.Base;

import java.util.ArrayList;

/**
 *
 * @author "MinhDA"
 */
public class GENE {
    public String Index; //Common field to store field by which gene list will be sorted
    public String OfficialSymbol;
    public String Organism;
    public String EntrezID;
    public String UniProtAC;
    public ArrayList<String> AlternateSymbols;
    public String EnsemblID;
    public String NetworkID; //Contain Network gene identifier
    
    public String Name;

    public String Chromosome;
    public long GeneStart;
    public long GeneEnd;
    public String Band;


    public int DistanceToSeed;
    
    
    public boolean InNetwork;

    public String Tag;

    public double Score;
    public boolean IsSeed;
    public boolean IsTest;
    public boolean IsHeldout;
    public int Rank;

    public ArrayList<GO> GOs;
    public ArrayList<CoExpression> CoExs;
    public GENE(){
        this.OfficialSymbol="";
        this.Organism="";
        this.EnsemblID="";
        this.UniProtAC="";
        this.EntrezID="";
        this.AlternateSymbols= new ArrayList<String>();
        this.Name="";
        
        this.InNetwork=false;

        this.Score=0.0;
        this.IsSeed=false;
        this.IsTest=false;
        this.IsHeldout=false;
        this.Rank=0;
        GOs=new ArrayList<GO>();
        this.CoExs=new ArrayList<CoExpression>();
    }

    public GENE(String OfficialSymbol){
        this.OfficialSymbol=OfficialSymbol;
        this.Organism="";
        this.EnsemblID="";
        this.UniProtAC="";
        this.EntrezID="";
        this.AlternateSymbols= new ArrayList<String>();
        this.Name="";
        
        this.InNetwork=false;

        this.Score=0.0;
        this.IsSeed=false;
        this.IsTest=false;
        this.IsHeldout=false;
        this.Rank=0;
        GOs=new ArrayList<GO>();
        this.CoExs=new ArrayList<CoExpression>();
    }

    public void CopyFrom(GENE g){//Use for assignment, ex: b.copyFrom(a) means b=a;
        this.AlternateSymbols = g.AlternateSymbols;
        this.Band = g.Band;
        this.Chromosome = g.Chromosome;
        this.DistanceToSeed=g.DistanceToSeed;
        this.EntrezID=g.EntrezID;
        this.GeneEnd=g.GeneEnd;
        this.GeneStart = g.GeneStart;
        this.InNetwork = g.InNetwork;
        this.Index = g.Index;
        this.NetworkID = g.NetworkID;
        this.IsHeldout=g.IsHeldout;
        this.IsSeed=g.IsSeed;
        this.IsTest=g.IsTest;
        this.Name=g.Name;
        this.OfficialSymbol=g.OfficialSymbol;
        this.Organism=g.Organism;
        this.Rank=g.Rank;
        this.Score=g.Score;
        this.UniProtAC=g.UniProtAC;
        this.Tag=g.Tag;
        
    }

    public GENE Copy(){//Use for copying, ex: b=a.copy();
        GENE g=new GENE();
        g.AlternateSymbols=this.AlternateSymbols;
        g.Band=this.Band;
        g.Chromosome=this.Chromosome;
        g.DistanceToSeed=this.DistanceToSeed;
        g.EntrezID=this.EntrezID;
        g.GeneEnd=this.GeneEnd;
        g.GeneStart=this.GeneStart;
        g.InNetwork=this.InNetwork;
        g.Index=this.Index;
        g.NetworkID=this.NetworkID;
        g.IsHeldout=this.IsHeldout;
        g.IsSeed=this.IsSeed;
        g.IsTest=this.IsTest;
        g.Name=this.Name;
        g.OfficialSymbol=this.OfficialSymbol;
        g.Organism=this.Organism;
        g.Rank=this.Rank;
        g.Score=this.Score;
        g.UniProtAC=this.UniProtAC;
        g.Tag=this.Tag;

        return g;
    }
}
