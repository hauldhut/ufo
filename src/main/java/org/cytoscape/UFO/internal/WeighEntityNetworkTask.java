package org.cytoscape.UFO.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.cytoscape.UFO.Base.Interaction;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author suvl_000
 */
class WeighEntityNetworkTask implements Task {

    private boolean interrupted = false;
    public static ArrayList<Interaction> WeightedInteractionList = new ArrayList<Interaction>();

    public static boolean Error = false;
    private CyNetworkManager cyNetworkManager;
    private List<CyEdge> arrCyEdge;

    public WeighEntityNetworkTask(CyNetworkManager cyNetworkManager, List<CyEdge> arrCyEdge) {
        this.cyNetworkManager = cyNetworkManager;
        this.arrCyEdge = arrCyEdge;
        System.out.println("this.arrCyEdge.size(): " + this.arrCyEdge.size());
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Weighting interactions in the network");
        taskMonitor.setProgress(0.1);
        int i;

        try {
            //Collecting selected edges
            Iterator<CyEdge> it = null;

            //String selNetTitle = pnlOntology.cboNetwork.getSelectedItem().toString();
            //String selNetID = selNetTitle.substring(selNetTitle.indexOf("(")+1, selNetTitle.length()-1);
            String selNetID = WeighEntityNetworkDialog.cboNetwork.getSelectedItem().toString();
            CyNetwork selNet = null;
            for (CyNetwork cyNetwork : cyNetworkManager.getNetworkSet()) {
                if (cyNetwork.getDefaultNetworkTable().getRow(cyNetwork.getSUID()).getRaw("name").toString().equals(selNetID)) {
                    selNet = cyNetwork;
                }
            }
//            CyNetwork selNet = cyNetworkManager.getNetwork(selNetID);
            int TotalEdge = 0;

            if (WeighEntityNetworkDialog.cboInteraction.getSelectedIndex() == 0) {
//                it = selNet.getSelectedEdges().iterator();
//                TotalEdge = selNet.getSelectedEdges().size();
                it = this.arrCyEdge.iterator();
                TotalEdge = this.arrCyEdge.size();
            } else {
//                it = selNet.edgesList().iterator();
//                TotalEdge = selNet.edgesList().size();
                it = selNet.getEdgeList().iterator();
                TotalEdge = selNet.getEdgeCount();
            }

            //Weighting
            taskMonitor.setStatusMessage("Weighting Interactions...!");

            String TermSimSubMet = (String) MainPanel.cboTermSimSubMet.getSelectedItem();
            String ObjectSimSubMet = (String) MainPanel.cboObjSimSubMet.getSelectedItem();
            System.out.println("TermSimSubMet: " + TermSimSubMet);
            System.out.println("ObjectSimSubMet: " + ObjectSimSubMet);
            if (null == selNet.getDefaultEdgeTable().getColumn(ObjectSimSubMet + "_" + TermSimSubMet)) {
                selNet.getDefaultEdgeTable().createColumn(ObjectSimSubMet + "_" + TermSimSubMet, Double.class, false);
            }
            if (null == selNet.getDefaultEdgeTable().getColumn(ObjectSimSubMet)) {
                selNet.getDefaultEdgeTable().createColumn(ObjectSimSubMet, Double.class, false);
            }
            i = 0;
            int InvalidIna = 0;
            WeightedInteractionList = new ArrayList<Interaction>();
            
            //System.out.println(it.);
            while (it.hasNext()) {
                i++;
                CyEdge e = (CyEdge) it.next();
                String Object1ID = selNet.getDefaultNodeTable().getRow(e.getSource().getSUID()).getRaw("name").toString();
                String Object2ID = selNet.getDefaultNodeTable().getRow(e.getTarget().getSUID()).getRaw("name").toString();
//                String Object1ID=e.getSource().getIdentifier();
//                String Object2ID=e.getTarget().getIdentifier();
                //System.out.println(Object1ID + "\t" + Object2ID);
                taskMonitor.setStatusMessage(i + "/" + TotalEdge + " Weighting Interaction: " + Object1ID + " --- " + Object2ID);
                if (this.interrupted == true) {
                    break;
                }

                double ObjectSim = 0.0;
                String Note = "";
                if (Object1ID.compareTo(Object2ID) == 0) {
                    ObjectSim = 1;
                    Note = "Same entity";
                } else if (!BasicData.Object2TermMap.containsKey(Object1ID) || !BasicData.Object2TermMap.containsKey(Object2ID)) {
                    ObjectSim = Double.NaN;
                    if (!BasicData.Object2TermMap.containsKey(Object1ID)) {
                        Note = "Missing " + Object1ID + " in the annotation data";
                    } else if (!BasicData.Object2TermMap.containsKey(Object2ID)) {
                        Note = "Missing " + Object2ID + " in the annotation data";
                    } else {
                        Note = "Missing " + Object1ID + " and " + Object2ID + " in the annotation data";
                    }

                    InvalidIna++;
                } else if (ObjectSimSubMet.contains("Pairwise-Based: Avg") || ObjectSimSubMet.contains("Pairwise-Based: Max") || ObjectSimSubMet.contains("Pairwise-Based: BMA") || ObjectSimSubMet.contains("Pairwise-Based: RCmax")) {
                    String ObjSimMet = "";
                    if (ObjectSimSubMet.contains("Pairwise-Based: Avg")) {
                        ObjSimMet = "Avg";
                    } else if (ObjectSimSubMet.contains("Pairwise-Based: Max")) {
                        ObjSimMet = "Max";
                    } else if (ObjectSimSubMet.contains("Pairwise-Based: BMA")) {
                        ObjSimMet = "BMA";
                    } else {
                        ObjSimMet = "RCmax";
                    }
                    ObjectSim = Common.calculateObjectSimilarity_Pairwise(Object1ID, Object2ID, BasicData.RootTermID, BasicData.Object2TermMap, BasicData.Term2ICMap, BasicData.ParentNodeMap, BasicData.ChildNodeMap, TermSimSubMet, ObjSimMet);
                } else if (ObjectSimSubMet.contains("Term Overlap (TO)")) {
                    ObjectSim = Common.calculateObjectSimilarity_Groupwise_TermOverlap_Lee2004_Mistry2008(Object1ID, Object2ID, BasicData.Object2TermMap, BasicData.ParentNodeMap, false);
                } else if (ObjectSimSubMet.contains("Normalized Term Overlap (NTO)")) {
                    ObjectSim = Common.calculateObjectSimilarity_Groupwise_TermOverlap_Lee2004_Mistry2008(Object1ID, Object2ID, BasicData.Object2TermMap, BasicData.ParentNodeMap, true);
                } else if (ObjectSimSubMet.contains("UI")) {
                    ObjectSim = Common.calculateObjectSimilarity_Groupwise_simUI_Gentleman2005(Object1ID, Object2ID, BasicData.Object2TermMap, BasicData.ParentNodeMap);
                } else if (ObjectSimSubMet.contains("GIC")) {
                    ObjectSim = Common.calculateObjectSimilarity_Groupwise_simGIC_Pesquita2007(Object1ID, Object2ID, BasicData.Object2TermMap, BasicData.Term2ICMap, BasicData.ParentNodeMap);
                } else if (ObjectSimSubMet.contains("LP")) {
                    ObjectSim = Common.calculateObjectSimilarity_Groupwise_simLP_Gentleman2005(Object1ID, Object2ID, BasicData.RootTermID, BasicData.Object2TermMap, BasicData.ParentNodeMap);
                } else if (ObjectSimSubMet.contains("Huang")) {
                    ObjectSim = Common.calculateObjectSimilarity_Groupwise_Kappa_Huang2007(Object1ID, Object2ID, BasicData.Object2TermMap);
                } else if (ObjectSimSubMet.contains("Chabalier")) {
                    ObjectSim = Common.calculateObjectSimilarity_Groupwise_CoSine_Chabalier2007(Object1ID, Object2ID, BasicData.Object2TermMap);
                }

                if (ObjectSimSubMet.contains("Pairwise-Based")) {
                    selNet.getDefaultEdgeTable().getRow(e.getSUID()).set(ObjectSimSubMet + "_" + TermSimSubMet, ObjectSim);
//                    Cytoscape.getEdgeAttributes().setAttribute(e.getIdentifier(), ObjectSimSubMet + "_" + TermSimSubMet, ObjectSim);
                } else {
                    selNet.getDefaultEdgeTable().getRow(e.getSUID()).set(ObjectSimSubMet, ObjectSim);
//                    Cytoscape.getEdgeAttributes().setAttribute(e.getIdentifier(), ObjectSimSubMet, ObjectSim);
                }
                Interaction ina = new Interaction();
                ina.NodeSrc = Object1ID;
                ina.NodeDst = Object2ID;
                ina.Index = Note;
                ina.Weight = ObjectSim;
                WeightedInteractionList.add(ina);

            }
            MainPanel.fillWeightedInteractionTable(WeightedInteractionList, WeighEntityNetworkDialog.tblWeightedInteraction);

        } catch (Exception e) {
            e.printStackTrace();
        }
        taskMonitor.setProgress(0.1);
    }

    @Override
    public void cancel() {
        this.interrupted = true;
    }

}
