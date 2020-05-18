/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cytoscape.UFO.internal;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.cytoscape.UFO.Base.GENE;
import org.cytoscape.UFO.Base.Interaction;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;


/**
 *
 * @author SUPB
 */
public class UseCurrentNetworkTask  implements Task{
    public static boolean Error=false;
    private boolean interrupted = false;
    
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Load information from current network");
        taskMonitor.setProgress(0.1);
        /*try{
            //JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Hello");
            UserData.NormalizedNetwork = new ArrayList<Interaction>();
            UserData.NormalizedNetworkNode = new ArrayList<GENE>();

            CyNetwork curNetwork = Cytoscape.getCurrentNetwork();
            Common.readCurrentNetwork(curNetwork);

            int i,j;

            CyAttributes CyNetworkAttrs = Cytoscape.getNetworkAttributes();
            //If it is normalized network, then transfer to Normalized network directly
            
            //Add and Update All fields for UserData.NormalizedNetworkNode
            CyAttributes nodeAtt = Cytoscape.getNetworkAttributes();

            List<CyNode> NodeList = curNetwork.getNodeList();
            for(i=0;i<NodeList.size();i++){
                CyNode n = (CyNode)NodeList.get(i);
                GENE g= new GENE();
                g.NetworkID=curNetwork.getDefaultNodeTable().getRow(n.getSUID()).getRaw("name").toString();//n.getIdentifier();
                g.EntrezID=(nodeAtt.getStringAttribute(n.getIdentifier(), "EntrezID")!=null)?nodeAtt.getStringAttribute(n.getIdentifier(), "EntrezID"):"";
                g.OfficialSymbol=(nodeAtt.getStringAttribute(n.getIdentifier(), "OfficialSymbol")!=null)?nodeAtt.getStringAttribute(n.getIdentifier(), "OfficialSymbol"):"";
                String altsyms = (nodeAtt.getStringAttribute(n.getIdentifier(), "AlternateSymbols")!=null)?nodeAtt.getStringAttribute(n.getIdentifier(), "AlternateSymbols"):"";
                StringTokenizer stk = new StringTokenizer(altsyms,", ");
                while(stk.hasMoreTokens()){
                    g.AlternateSymbols.add(stk.nextToken());
                }
                g.Organism=(nodeAtt.getStringAttribute(n.getIdentifier(), "Organism")!=null)?nodeAtt.getStringAttribute(n.getIdentifier(), "Organism"):"";
                g.UniProtAC=(nodeAtt.getStringAttribute(n.getIdentifier(), "UniProtAC")!=null)?nodeAtt.getStringAttribute(n.getIdentifier(), "UniProtAC"):"";

                UserData.NormalizedNetworkNode.add(g);
            }

            //Identify NetworkNodeIdentifier

            //Load edges
            //CyAttributes edgeAtt = Cytoscape.getNetworkAttributes();

            List<CyEdge> EdgeList = curNetwork.getEdgeList();
            for(i=0;i<EdgeList.size();i++){
                CyEdge e = (CyEdge)EdgeList.get(i);
                Interaction ina= new Interaction();

                ina.NodeSrc= curNetwork.getDefaultNodeTable().getRow(e.getSource().getSUID()).getRaw("name").toString();//e.getSource().getIdentifier();
                ina.NodeDst= curNetwork.getDefaultNodeTable().getRow(e.getTarget().getSUID()).getRaw("name").toString();//e.getTarget().getIdentifier();
                ina.WeightOriginal=1.0;
                UserData.NormalizedNetwork.add(ina);
            }
        }catch(Exception e){
            //JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Error while reading current network: " + e.toString());
            //JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Error: The values of second column that you enter as Weight are not a numberic value...!","Notice",JOptionPane.WARNING_MESSAGE);
            //this.interrupted=true;
            e.printStackTrace();
            this.Error=true;
        }*/
        taskMonitor.setProgress(0.1);
    }

    @Override
    public void cancel() {
        this.interrupted = true;
    }
    
    

}
