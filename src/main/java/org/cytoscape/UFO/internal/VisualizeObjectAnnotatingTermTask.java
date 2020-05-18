package org.cytoscape.UFO.internal;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import org.cytoscape.UFO.Base.Interaction;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author suvl_000
 */
class VisualizeObjectAnnotatingTermTask implements Task {

    private TaskManager taskManager;
    private volatile boolean interrupted = false;
    private CyNetworkFactory cnf;
    private CyNetworkNaming namingUtil;
    private CyNetworkManager netMgr;
    private CyLayoutAlgorithmManager layoutManager;
    private CyNetworkViewManager cyNetworkViewManager;
    private CyNetworkViewFactory cyNetworkViewFactory;
    private VisualStyle vs;

    public VisualizeObjectAnnotatingTermTask(TaskManager taskManager, CyNetworkFactory cnf, CyNetworkNaming namingUtil, CyNetworkManager netMgr,
            CyLayoutAlgorithmManager layoutManager, CyNetworkViewManager cyNetworkViewManager, CyNetworkViewFactory cyNetworkViewFactory, VisualStyle vs) {
        this.taskManager = taskManager;
        this.cnf = cnf;
        this.namingUtil = namingUtil;
        this.netMgr = netMgr;
        this.layoutManager = layoutManager;
        this.cyNetworkViewManager = cyNetworkViewManager;
        this.cyNetworkViewFactory = cyNetworkViewFactory;
        this.vs = vs;
    }

    private CyNode findNodeById(CyNetwork network, String id) {
        for (CyRow node : network.getDefaultNodeTable().getAllRows()) {
            if (id.equals(node.getRaw("ID"))) {
                Long x = (Long) node.getRaw("SUID");
                return network.getNode(x);
            }
        }
        return null;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Visualize Sub-Graph");
        taskMonitor.setProgress(0.1);
        try {
            taskMonitor.setStatusMessage("Creating Sub-Graph...!");
            if (this.interrupted == true) {
                return;
            }

            int i, j;
            //int[] selIdx = pnlOntology.lstTermID.getSelectedIndices();
            Set<String> SubGraphNodeSet = new TreeSet<String>();
            SubGraphNodeSet.addAll(BasicData.validSelObjectIDList);

            ArrayList<Interaction> SubGraph = new ArrayList<Interaction>();
            Set<String> DirectAnnTermIDSet = new TreeSet<String>();

            for (String ObjectID : BasicData.validSelObjectIDList) {
                if (BasicData.Object2TermMap.containsKey(ObjectID)) {
                    DirectAnnTermIDSet.addAll(BasicData.Object2TermMap.get(ObjectID));
                }
            }

            //Link Object to direct annotating terms
            if (MainPanel.chkDirectTerm.isSelected()) {
                for (String ObjectID : BasicData.validSelObjectIDList) {
                    if (BasicData.Object2TermMap.containsKey(ObjectID)) {
                        for (String TermID : BasicData.Object2TermMap.get(ObjectID)) {
                            Interaction ina = new Interaction();
                            ina.NodeSrc = TermID;
                            ina.NodeDst = ObjectID;
                            ina.TypeOriginal = "Entity-Term";

                            SubGraph.add(ina);
                        }
                    }
                }
            }

            if (MainPanel.chkDirectAncestorTerm.isSelected()) {
                for (String TermID : DirectAnnTermIDSet) {
                    ArrayList<Interaction> SubDAG = new ArrayList<Interaction>();
                    Set<String> InaSet = new TreeSet<String>();
                    Common.extractSubDAG(TermID, BasicData.ParentNodeMap, SubDAG, InaSet);
                    SubGraph.addAll(SubDAG);
                }
            }

            if (MainPanel.chkDirectDescendantTerm.isSelected()) {
                for (String TermID : DirectAnnTermIDSet) {
                    ArrayList<Interaction> SubTree = new ArrayList<Interaction>();
                    Set<String> InaSet = new TreeSet<String>();
                    Common.extractSubTree(TermID, BasicData.ParentNodeMap, SubTree, InaSet);
                    SubGraph.addAll(SubTree);
                }
            }

            double min = Double.parseDouble(MainPanel.txtMinFunSim.getText());
            double max = Double.parseDouble(MainPanel.txtMaxFunSim.getText());
            Set<String> InaSet = new TreeSet<String>();
            if (MainPanel.chkFunSimInteraction.isSelected()) {
                for (Map.Entry<String, Map<String, Double>> e1 : CalculateFunSimMatrixTask.SimMatrix.entrySet()) {
                    for (Map.Entry<String, Double> e2 : e1.getValue().entrySet()) {
                        if (e1.getKey().compareTo(e2.getKey()) == 0) {
                            continue;
                        }
                        if (InaSet.contains(e1.getKey() + "_" + e2.getKey()) || InaSet.contains(e2.getKey() + "_" + e1.getKey())) {
                            continue;
                        }
                        if (e2.getValue() >= min && e2.getValue() <= max) {
                            Interaction ina = new Interaction();
                            ina.NodeSrc = e1.getKey();
                            ina.NodeDst = e2.getKey();
                            ina.TypeOriginal = "Entity-Entity";
                            ina.Weight = e2.getValue();
                            SubGraph.add(ina);
                            InaSet.add(e1.getKey() + "_" + e2.getKey());
                            InaSet.add(e2.getKey() + "_" + e1.getKey());
                        }
                    }
                }
            }

            for (i = 0; i < SubGraph.size(); i++) {
                SubGraphNodeSet.add(SubGraph.get(i).NodeSrc);
                SubGraphNodeSet.add(SubGraph.get(i).NodeDst);
            }
            StringBuilder subNetworkTitle = new StringBuilder("Annotating-Graph_of");
            for (String ObjectID : BasicData.validSelObjectIDList) {
                subNetworkTitle.append("_" + ObjectID);
            }

            CyNetwork subNetwork = cnf.createNetwork();
            subNetwork.getRow(subNetwork).set(CyNetwork.NAME, namingUtil.getSuggestedNetworkTitle(subNetworkTitle.toString()));
            final Collection<CyNetworkView> views = cyNetworkViewManager.getNetworkViews(subNetwork);

            ArrayList<CyNode> entityNodes = new ArrayList<>();

            taskMonitor.setStatusMessage("Creating nodes of Sub-Graph...!");
            if (this.interrupted == true) {
                return;
            }
            subNetwork.getDefaultNodeTable().createColumn("Type", String.class, false);
            subNetwork.getDefaultNodeTable().createColumn("Information Content (IC)", Double.class, false);
            subNetwork.getDefaultNodeTable().createColumn("Term/Entity Name", String.class, false);
            subNetwork.getDefaultNodeTable().createColumn("ID", String.class, false);
            for (String n : SubGraphNodeSet) {
                CyNode aNode = subNetwork.addNode();
                CyRow aNodeAtt = subNetwork.getDefaultNodeTable().getRow(aNode.getSUID());

                if (BasicData.TermID2NameMap.containsKey(n)) {//Term node

                    aNodeAtt.set("Type", "Term");
                    aNodeAtt.set("Term/Entity Name", BasicData.TermID2NameMap.get(n));
                    aNodeAtt.set("name", n);
                    aNodeAtt.set("Information Content (IC)", BasicData.Term2ICMap.get(n));
                    aNodeAtt.set("ID", n);

                    String AnnotatedOnjects = "";
                    int NodeSize = BasicData.TermID2InfoMap.get(n).Name.length();
//                    aNodeAtt.set("node.shape", "ellipse");

                } else {
                    entityNodes.add(aNode);
                    aNodeAtt.set("Type", "Entity");
                    aNodeAtt.set("name", n);
                    aNodeAtt.set("Term/Entity Name", BasicData.ObjectID2NameMap.get(n));
                    aNodeAtt.set("ID", n);
//                    aNodeAtt.set("node.fillColor","222,222,54");
//                    aNodeAtt.set("node.shape","rectangle");
                }
            }

            taskMonitor.setStatusMessage("Creating edges of Sub-Graph...!");
            subNetwork.getDefaultEdgeTable().createColumn("Type", String.class, false);

            if (this.interrupted == true) {
                return;
            }
            for (i = 0; i < SubGraph.size(); i++) {
                CyEdge aEdge = null;
                String EdgeName = SubGraph.get(i).NodeSrc + "_" + SubGraph.get(i).NodeDst;
                CyNode nodeSource = findNodeById(subNetwork, SubGraph.get(i).NodeSrc);
                CyNode nodeDestination = findNodeById(subNetwork, SubGraph.get(i).NodeDst);
                if (SubGraph.get(i).TypeOriginal.compareTo("Entity-Term") == 0) {
                    aEdge = subNetwork.addEdge(nodeSource, nodeDestination, true);
                    subNetwork.getDefaultEdgeTable().getRow(aEdge.getSUID()).set("name", EdgeName);
                    subNetwork.getDefaultEdgeTable().getRow(aEdge.getSUID()).set("interaction", "Annotation");

                } else if (SubGraph.get(i).TypeOriginal.compareTo("Entity-Entity") == 0) {
                    DecimalFormat df = new DecimalFormat("0.00000");
                    aEdge = subNetwork.addEdge(nodeSource, nodeDestination, true);
                    subNetwork.getDefaultEdgeTable().getRow(aEdge.getSUID()).set("name", EdgeName);
                    subNetwork.getDefaultEdgeTable().getRow(aEdge.getSUID()).set("interaction", df.format(SubGraph.get(i).Weight));
                } else {
                    aEdge = subNetwork.addEdge(nodeSource, nodeDestination, true);
                    subNetwork.getDefaultEdgeTable().getRow(aEdge.getSUID()).set("name", EdgeName);
                    subNetwork.getDefaultEdgeTable().getRow(aEdge.getSUID()).set("interaction", SubGraph.get(i).TypeOriginal);
                }
//                Node src = null;
//                Node dst = null;
//                for (giny.model.Node n : (List<giny.model.Node>)subNetwork.nodesList()) {
//                    if(n.getIdentifier().compareTo(SubGraph.get(i).NodeSrc)==0){
//                        src = n;
//                    }
//                    if(n.getIdentifier().compareTo(SubGraph.get(i).NodeDst)==0){
//                        dst = n;
//                    }
//                }
//                CyEdge aEdge = Cytoscape.getCyEdge(src, dst, "Type", SubGraph.get(i).TypeOriginal, true, true);
                //Cytoscape.getEdgeAttributes().setAttribute(aEdge.getIdentifier(), "Type", SubGraph.get(i).TypeOriginal);

                if (SubGraph.get(i).TypeOriginal.compareTo("Entity-Term") == 0) {
                    subNetwork.getDefaultEdgeTable().getRow(aEdge.getSUID()).set("Type", "Entity-Term");
                } else if (SubGraph.get(i).TypeOriginal.compareTo("Entity-Entity") == 0) {
                    subNetwork.getDefaultEdgeTable().getRow(aEdge.getSUID()).set("Type", "Entity-Entity");
                } else {
                    subNetwork.getDefaultEdgeTable().getRow(aEdge.getSUID()).set("Type", "Term-Term");
                }
            }
            taskMonitor.setStatusMessage("Applying circular layout for Sub-Graph...!");
            if (this.interrupted == true) {
                return;
            }

            netMgr.addNetwork(subNetwork);

            CyLayoutAlgorithm layout = layoutManager.getLayout("circular");//force-directed
            layout = layoutManager.getLayout("attributes-layout");

            CyNetworkView myView = null;
            if (views.size() != 0) {
                myView = views.iterator().next();
                this.taskManager.execute(layout.createTaskIterator(myView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, "Type"));
            }
            if (myView == null) {
                // create a new view for my network
                myView = cyNetworkViewFactory.createNetworkView(subNetwork);
                for (CyNode node : entityNodes) {
                    View<CyNode> nodeView = myView.getNodeView(node);
                    nodeView.setLockedValue(BasicVisualLexicon.NODE_FILL_COLOR, Color.CYAN);
                    nodeView.setLockedValue(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.RECTANGLE);
                }
                // Apply the visual style to a NetwokView
                this.vs.apply(myView);
                myView.updateView();
                this.taskManager.execute(layout.createTaskIterator(myView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, "Type"));
                cyNetworkViewManager.addNetworkView(myView);

            } else {
                System.out.println("networkView already existed.");
            }
            if (this.interrupted == true) {
                return;
            }

        } catch (Exception e) {
            System.out.println("exception : " + e);
        }
        taskMonitor.setProgress(1.0);
    }

    @Override
    public void cancel() {
        this.interrupted = true;
    }

}
