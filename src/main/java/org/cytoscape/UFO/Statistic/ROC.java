/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cytoscape.UFO.Statistic;

import java.util.ArrayList;

/**
 *
 * @author "MinhDA"
 */
public class ROC {
    public ArrayList<Double> TPFs; //True Positive Fraction (Vertical axis)
    public ArrayList<Double> FPFs; //False Positive Fraction (Horizontal axis)
    public double AUC;
    public int MaxRank;
    public ArrayList<Integer> HoldoutRanks;

    public ROC(){
        HoldoutRanks=new ArrayList<Integer>();
        TPFs = new ArrayList<Double>();
        FPFs = new ArrayList<Double>();
    }
    public void calTPFs_FPFs(){
        
        int threshold,i;
        TPFs = new ArrayList<Double>();
        FPFs = new ArrayList<Double>();
        for(threshold=1;threshold<=MaxRank;threshold++){
            int numTP=0;
            int numFN=0;
            int numFP=0;
            for(i=0;i<HoldoutRanks.size();i++){
                if(HoldoutRanks.get(i)<=threshold){
                    numTP++;
                }

            }
            TPFs.add((double)numTP/HoldoutRanks.size());

            FPFs.add((double)(threshold*HoldoutRanks.size()-numTP)/((MaxRank-1)*HoldoutRanks.size()));//MaxRank-1 equal to Number of genes in Common.AllRankedTestGene
        }
        
    }
    public void calcAUC(){
        double temp=0;
        int i;
        for(i=0;i<MaxRank-1;i++){
            temp+=(FPFs.get(i+1)-FPFs.get(i))*(TPFs.get(i+1)+TPFs.get(i));
        }
        AUC=temp/2;
    }
}
