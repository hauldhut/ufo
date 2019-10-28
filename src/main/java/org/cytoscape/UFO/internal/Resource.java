/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cytoscape.UFO.internal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import org.cytoscape.UFO.Base.Species;

/**
 *
 * @author "MinhDA"
 */
class Resource {

    public Resource() {
    }

    //Load Gene ID Mapping database
    public Map<String, String> loadGeneInfo(String filename){
        Map<String, String> ObjectID2NameMap = new TreeMap<>();
        try{
            
            //Load from Resource (this is fixed)
            InputStream is = getClass().getResourceAsStream(filename);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String str=null;
            
            String geneid="";
            String officialsymbol="";
            
            ObjectID2NameMap = new TreeMap<>();
            while((str=br.readLine())!=null){
                String[] st = str.split("\t");
                
                geneid=st[0].trim();
                officialsymbol=st[1].trim();
                    
                ObjectID2NameMap.put(geneid, officialsymbol);
            }
            br.close();

        }catch(Exception e){
            System.out.println("Error while loading Gene/Protein Information Database: " + e.toString());
            JOptionPane.showMessageDialog(null, "Error while loading Gene/Protein Information Database: " + e.toString());
        }
        return ObjectID2NameMap;
    }

    public Map<String, String> loadPhenotypeInfo(String FileName) {
        Map<String, String> ObjectID2NameMap = new TreeMap<String, String>();
        try {
            InputStream is = getClass().getResourceAsStream(FileName);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String str = "";
            String mimid="";
            String title="";
            ObjectID2NameMap = new TreeMap<String, String>();
            while((str=br.readLine())!=null){
                String[] st = str.split("\t");
                
                mimid=st[0].trim();
                title=st[3].trim();
                mimid=mimid.substring(3, mimid.length());
                //System.out.println("mimid: " + mimid + "\t" + title);
                ObjectID2NameMap.put(mimid, title);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error while loading Phenotype Information Database: " + e.toString());
            JOptionPane.showMessageDialog(null, "Error while loading Phenotype Information Database: " + e.toString());
            e.printStackTrace();
        }
        return ObjectID2NameMap;
    }
    
    public Map<String, Species> readSpeciesList(String FileName){

        Map<String, Species> sl = new TreeMap<String, Species>();
        try{
            InputStream is = getClass().getResourceAsStream(FileName);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
            String str=null;
            while((str=br.readLine())!=null){
                String[] s = str.split("\t");
                String sn = s[0];
                Species so = new Species();
                so.ScientificName=sn;
                if(s[1].compareTo("null")!=0){
                    so.CommonName=s[1];
                }
                so.Tax_id=Integer.parseInt(s[2]);
                sl.put(sn, so);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return sl;
    }
}
