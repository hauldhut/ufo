/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cytoscape.UFO.internal;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import org.cytoscape.UFO.Base.Interaction;
import org.cytoscape.UFO.Base.NodeInteraction;
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
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualPropertyDependency;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author "MinhDA"
 */
class VisualizeSubOntologyGraphTask implements Task {

    private volatile boolean interrupted = false;
    private CyNetworkManager netMgr;
    private CyNetworkFactory cnf;
    private CyNetworkNaming namingUtil;
    private CyNetworkViewFactory cyNetworkViewFactory;
    private CyNetworkViewManager cyNetworkViewManager;
    private CyLayoutAlgorithmManager layoutManager;
    private TaskManager taskManager;
    private VisualMappingManager vmmManager;
    private VisualStyle vs;
    private Set<String> selTermIDSet;

    public VisualizeSubOntologyGraphTask(CyNetworkManager netMgr, CyNetworkFactory cnf, CyNetworkNaming namingUtil,
            CyNetworkViewFactory cyNetworkViewFactory, CyNetworkViewManager cyNetworkViewManager, CyLayoutAlgorithmManager layoutManager,
            TaskManager taskManager, VisualMappingManager vmmManager, VisualStyle vs, Set<String> selTermIDSet) {
        this.netMgr = netMgr;
        this.cnf = cnf;
        this.namingUtil = namingUtil;
        this.cyNetworkViewFactory = cyNetworkViewFactory;
        this.cyNetworkViewManager = cyNetworkViewManager;
        this.layoutManager = layoutManager;
        this.taskManager = taskManager;
        this.vmmManager = vmmManager;
        this.vs = vs;
        this.selTermIDSet = selTermIDSet;
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

    private void addNodeToNetwork(CyNetwork subNetwork, String n) {
        CyNode aNode = subNetwork.addNode();
        CyRow aNodeAtt = subNetwork.getDefaultNodeTable().getRow(aNode.getSUID());
        aNodeAtt.set("ID", n);
        System.out.println("aNode = " + aNode);
        System.out.println("x = " + aNodeAtt);
        if (BasicData.TermID2NameMap.containsKey(n)) {//Term node

            aNodeAtt.set("Name", BasicData.TermID2NameMap.get(n));
            aNodeAtt.set("Information Content (IC)", BasicData.Term2ICMap.get(n));

            String AnnotatedOnjects = "";
            int NodeSize = BasicData.TermID2InfoMap.get(n).Name.length();
//                    subNetwork.getDefaultNodeTable().getRow(aNode.getSUID()).set("node.shape", "ellipse");
        } else {
            aNodeAtt.set("Name", BasicData.ObjectID2NameMap.get(n));

//                    Cytoscape.getNodeAttributes().setAttribute(aNode.getIdentifier(), "Name", BasicData.ObjectID2NameMap.get(n));
//                    Cytoscape.getNodeAttributes().setAttribute(aNode.getIdentifier(),"node.fillColor","222,222,54");
//                    Cytoscape.getNodeAttributes().setAttribute(aNode.getIdentifier(),"node.shape","rectangle");
        }
    }

    private void setSelectedNodeState(CyNetwork network, List<CyNode> nodeList, boolean isSelected) {
        for (CyNode node : nodeList) {
            network.getDefaultNodeTable().getRow(node.getSUID()).set("selected", isSelected);
        }
    }

    private void setSelectedEdgeState(CyNetwork network, List<CyEdge> edgeList, boolean isSelected) {
        for (CyEdge edge : edgeList) {
            network.getDefaultEdgeTable().getRow(edge.getSUID()).set("selected", isSelected);
        }
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
            ArrayList<Interaction> SubGraph = new ArrayList<Interaction>();
            Set<String> SubGraphNodeSet = new TreeSet<String>();
            SubGraphNodeSet.addAll(selTermIDSet);

            if (MainPanel.chkAnnotatedObject.isSelected()) {
                for (String TermID : selTermIDSet) {
                    if (!BasicData.Term2ObjectMap.containsKey(TermID)) {
                        continue;
                    }
                    for (String ObjectID : BasicData.Term2ObjectMap.get(TermID)) {
                        Interaction ina = new Interaction();
                        ina.NodeSrc = TermID;
                        ina.NodeDst = ObjectID;
                        ina.TypeOriginal = "Entity-Term";
                        SubGraph.add(ina);
                    }
                }
            }

            if (MainPanel.chkTermAncestor.isSelected()) {
                SubGraph = new ArrayList<Interaction>();
                Set<String> subgraph = new TreeSet<String>();
                for (String TermID : selTermIDSet) {
                    ArrayList<Interaction> SubDAG = new ArrayList<Interaction>();
                    Set<String> InaSet = new TreeSet<String>();
                    Common.extractSubDAG(TermID, BasicData.ParentNodeMap, SubDAG, InaSet);
                    for (i = 0; i < SubDAG.size(); i++) {
                        String ina = SubDAG.get(i).NodeSrc + "_" + SubDAG.get(i).Type + "_" + SubDAG.get(i).NodeDst;
                        if (!subgraph.contains(ina)) {
                            SubGraph.add(SubDAG.get(i));
                            subgraph.add(ina);
                        }
                    }

                }
            }

            if (MainPanel.chkTermDescendant.isSelected()) {
                for (String TermID : selTermIDSet) {
                    ArrayList<Interaction> SubTree = new ArrayList<Interaction>();
                    Set<String> InaSet = new TreeSet<String>();
                    Common.extractSubTree(TermID, BasicData.ChildNodeMap, SubTree, InaSet);
                    SubGraph.addAll(SubTree);
                }
            }

            for (i = 0; i < SubGraph.size(); i++) {
                SubGraphNodeSet.add(SubGraph.get(i).NodeSrc);
                SubGraphNodeSet.add(SubGraph.get(i).NodeDst);
            }
            StringBuilder subNetworkTitle = new StringBuilder("Sub-Graph_of");
            for (String TermID : selTermIDSet) {
                subNetworkTitle.append("_" + TermID);
            }

            //CyNetwork subNetwork = Cytoscape.createNetwork(subNetworkTitle.toString(), true);
            CyNetwork subNetwork = cnf.createNetwork();
            subNetwork.getRow(subNetwork).set(CyNetwork.NAME,
                    namingUtil.getSuggestedNetworkTitle(subNetworkTitle.toString()));
            
            subNetwork.getDefaultNodeTable().createColumn("Type", String.class, false);
            subNetwork.getDefaultNodeTable().createColumn("Information Content (IC)", Double.class, false);
            subNetwork.getDefaultNodeTable().createColumn("Term/Entity Name", String.class, false);
            subNetwork.getDefaultNodeTable().createColumn("ID", String.class, false);
            
            System.out.println("Node Set size:" + SubGraphNodeSet.size());
            taskMonitor.setStatusMessage("Creating nodes of Sub-Graph...!");
            if (this.interrupted == true) {
                return;
            }
            ArrayList<CyNode> entityNodes = new ArrayList<>();
            for (String n : SubGraphNodeSet) {
//                addNodeToNetwork(subNetwork, n);
                CyNode aNode = subNetwork.addNode();
                CyRow aNodeAtt = subNetwork.getDefaultNodeTable().getRow(aNode.getSUID());
                aNodeAtt.set("ID", n);
                System.out.println("aNode = " + aNode);
                System.out.println("x = " + aNodeAtt);
                if (BasicData.TermID2NameMap.containsKey(n)) {//Term node

                    aNodeAtt.set("Type", "Term");
                    aNodeAtt.set("Term/Entity Name", BasicData.TermID2NameMap.get(n));
                    aNodeAtt.set("name", n);
                    aNodeAtt.set("Information Content (IC)", BasicData.Term2ICMap.get(n));
                    aNodeAtt.set("ID", n);

                    String AnnotatedOnjects = "";
                    int NodeSize = BasicData.TermID2InfoMap.get(n).Name.length();
//                    subNetwork.getDefaultNodeTable().getRow(aNode.getSUID()).set("node.shape", "ellipse");
                } else {
                    entityNodes.add(aNode);
                    aNodeAtt.set("Type", "Entity");
                    aNodeAtt.set("name", n);
                    aNodeAtt.set("Term/Entity Name", BasicData.ObjectID2NameMap.get(n));
                    aNodeAtt.set("ID", n);
                    
//                    Cytoscape.getNodeAttributes().setAttribute(aNode.getIdentifier(), "Name", BasicData.ObjectID2NameMap.get(n));
//                    Cytoscape.getNodeAttributes().setAttribute(aNode.getIdentifier(),"node.fillColor","222,222,54");
//                    Cytoscape.getNodeAttributes().setAttribute(aNode.getIdentifier(),"node.shape","rectangle");
                }
            }
            taskMonitor.setStatusMessage("Creating edges of Sub-Graph...!");
            if (this.interrupted == true) {
                return;
            }
            System.out.println("size = " + SubGraph.size());
            for (i = 0; i < SubGraph.size(); i++) {
                CyNode nodeSource = findNodeById(subNetwork, SubGraph.get(i).NodeSrc);
                CyNode nodeDestination = findNodeById(subNetwork, SubGraph.get(i).NodeDst);
                String EdgeName = SubGraph.get(i).NodeSrc + "_" + SubGraph.get(i).NodeDst;
                
                CyEdge aEdge = subNetwork.addEdge(nodeSource, nodeDestination, true);
                
                subNetwork.getDefaultEdgeTable().getRow(aEdge.getSUID()).set("name", EdgeName);
                subNetwork.getDefaultEdgeTable().getRow(aEdge.getSUID()).set("interaction", SubGraph.get(i).TypeOriginal);
                System.out.println("Node source:" + SubGraph.get(i).NodeSrc + " - " + SubGraph.get(i).NodeDst);
            }

            // Add network properties
            subNetwork.getDefaultNetworkTable().createColumn("Type", String.class, false);
//            subNetwork.getDefaultNetworkTable().getAllRows().get(0).set("Type", "Ontology");
            subNetwork.getRow(subNetwork).set("Type", "Ontology");
            // Inform others via property change event.
//            Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
            taskMonitor.setStatusMessage("Applying hierarchical layout for Sub-Graph...!");
            if (this.interrupted == true) {
                return;
            }
            netMgr.addNetwork(subNetwork);
            System.out.println("Added network");
            CyLayoutAlgorithm layout = layoutManager.getLayout("hierarchical");//force-directed

            final Collection<CyNetworkView> views = cyNetworkViewManager.getNetworkViews(subNetwork);
            CyNetworkView myView = null;
            if (views.size() != 0) {
                myView = views.iterator().next();
                myView.setVisualProperty(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ELLIPSE);
                this.taskManager.execute(layout.createTaskIterator(myView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, null));

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
                this.taskManager.execute(layout.createTaskIterator(myView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, null));
                cyNetworkViewManager.addNetworkView(myView);

            } else {
                System.out.println("networkView already existed.");
            }
            //Apply Visual Style
            taskMonitor.setStatusMessage("Applying visual style for Sub-Graph...!");

            if (this.interrupted == true) {
                return;
            }

            List<CyNode> subNetworkNodeList = subNetwork.getNodeList();
            setSelectedNodeState(subNetwork, subNetworkNodeList, false);
//            for (CyNode cyNode : subNetworkNodeList) {
//                subNetwork.getDefaultNodeTable().getRow(cyNode.getSUID()).set("selected", false);
//            }

            List<CyNode> SelectedNodeList = new ArrayList<CyNode>();
            for (CyNode n : subNetworkNodeList) {
                if (selTermIDSet.contains(subNetwork.getDefaultNodeTable().getRow(n.getSUID()).getRaw("ID").toString())) {
                    SelectedNodeList.add(n);
                }
            }
            setSelectedNodeState(subNetwork, subNetworkNodeList, true);
//            subNetwork.setSelectedNodeState(SelectedNodeList, true);
//            for (CyNode cyNode : subNetworkNodeList) {
//                subNetwork.getDefaultNodeTable().getRow(cyNode.getSUID()).set("selected", true);
//            }

            //Visualize Common DAG
            if (MainPanel.chkTermAncestor.isSelected() && MainPanel.chkCommonAncestor.isEnabled() && MainPanel.chkCommonAncestor.isSelected()) {
                Set<String> CommonAncestorSet = new TreeSet<String>();
                ArrayList<String> selTermIDList = new ArrayList<String>();
                selTermIDList.addAll(selTermIDSet);

                CommonAncestorSet.addAll(Common.extractAncestorTerms(selTermIDList.get(0), BasicData.ParentNodeMap));
                for (i = 1; i < selTermIDList.size(); i++) {
                    CommonAncestorSet.retainAll(Common.extractAncestorTerms(selTermIDList.get(i), BasicData.ParentNodeMap));
                }

//                CyNetwork curNetwork = netMgr.getCurrentNetwork();
                CyNetwork curNetwork = netMgr.getNetwork(subNetwork.getSUID());

                subNetworkNodeList = curNetwork.getNodeList();
                List<CyNode> CommonDAGNodeList = new ArrayList<CyNode>();
                for (CyNode n : subNetworkNodeList) {
                    if (CommonAncestorSet.contains(curNetwork.getDefaultNodeTable().getRow(n.getSUID()).getRaw("ID").toString())) {
                        CommonDAGNodeList.add(n);
                    }
                }

                List<CyEdge> subNetworkEdgeList = curNetwork.getEdgeList();
                List<CyEdge> CommonDAGEdgeList = new ArrayList<CyEdge>();
                for (CyEdge e : subNetworkEdgeList) {
                    if (CommonAncestorSet.contains(curNetwork.getDefaultNodeTable().getRow(e.getSource().getSUID()).toString())
                            && CommonAncestorSet.contains(curNetwork.getDefaultNodeTable().getRow(e.getTarget().getSUID().toString()))) {
                        CommonDAGEdgeList.add(e);
                    }
                }
                setSelectedNodeState(curNetwork, subNetworkNodeList, false);
                setSelectedEdgeState(curNetwork, subNetworkEdgeList, false);
//                curNetwork.setSelectedNodeState(subNetworkNodeList, false);
//                curNetwork.setSelectedEdgeState(subNetworkEdgeList, false);

                setSelectedNodeState(curNetwork, CommonDAGNodeList, true);
                setSelectedEdgeState(curNetwork, CommonDAGEdgeList, true);
//                curNetwork.setSelectedNodeState(CommonDAGNodeList, true);
//                curNetwork.setSelectedEdgeState(CommonDAGEdgeList, true);
            }
            //Visualize MICA
            if (MainPanel.chkTermAncestor.isSelected() && MainPanel.chkMICA.isEnabled() && MainPanel.chkMICA.isSelected()) {
                Set<String> CommonAncestorSet = new TreeSet<String>();
                ArrayList<String> selTermIDList = new ArrayList<String>();
                selTermIDList.addAll(selTermIDSet);

                CommonAncestorSet.addAll(Common.extractAncestorTerms(selTermIDList.get(0), BasicData.ParentNodeMap));
                for (i = 1; i < selTermIDList.size(); i++) {
                    CommonAncestorSet.retainAll(Common.extractAncestorTerms(selTermIDList.get(i), BasicData.ParentNodeMap));
                }

                String MICA = "";
                double maxIC = 0.0;
                for (String ca : CommonAncestorSet) {
                    if (BasicData.Term2ICMap.get(ca) > maxIC) {
                        maxIC = BasicData.Term2ICMap.get(ca);
                        MICA = ca;
                    }
                }

                CyNetwork curNetwork = netMgr.getNetwork(subNetwork.getSUID());//.getCurrentNetwork();

                subNetworkNodeList = curNetwork.getNodeList();//getNodeList();
                List<CyNode> MICANode = new ArrayList<CyNode>();
                for (CyNode n : subNetworkNodeList) {
                    if (MICA.compareTo(curNetwork.getDefaultNodeTable().getRow(n.getSUID()).getRaw("ID").toString()) == 0) {
                        MICANode.add(n);
                    }
                }
                setSelectedNodeState(curNetwork, subNetworkNodeList, false);
                setSelectedNodeState(curNetwork, MICANode, true);
//                curNetwork.setSelectedNodeState(subNetworkNodeList, false);
//                curNetwork.setSelectedNodeState(MICANode, true);
            }

            //Visualize DCA
            if (MainPanel.chkTermAncestor.isSelected() && MainPanel.chkCDA.isEnabled() && MainPanel.chkCDA.isSelected()) {

                ArrayList<String> selTermIDList = new ArrayList<String>();
                selTermIDList.addAll(selTermIDSet);

                String c1 = "";
                String c2 = "";
                c1 = selTermIDList.get(0);
                c2 = selTermIDList.get(1);

                Set<String> DCASet = Common.calculateDCA(c1, c2, BasicData.Term2ICMap, BasicData.ParentNodeMap);

                CyNetwork curNetwork = netMgr.getNetwork(subNetwork.getSUID());//Cytoscape.getCurrentNetwork();

                subNetworkNodeList = curNetwork.getNodeList();
                List<CyNode> DCANodeList = new ArrayList<CyNode>();
                for (CyNode n : subNetworkNodeList) {
                    if (DCASet.contains(curNetwork.getDefaultNodeTable().getRow(n.getSUID()).getRaw("ID").toString())) {
                        DCANodeList.add(n);
                    }
                }

                List<CyEdge> subNetworkEdgeList = curNetwork.getEdgeList();
                List<CyEdge> DCAEdgeList = new ArrayList<CyEdge>();
                for (CyEdge e : subNetworkEdgeList) {
                    if (DCASet.contains(curNetwork.getDefaultNodeTable().getRow(e.getSource().getSUID()).getRaw("ID").toString())
                            && DCASet.contains(curNetwork.getDefaultNodeTable().getRow(e.getTarget().getSUID()).getRaw("ID").toString())) {
                        DCAEdgeList.add(e);
                    }
                }
                setSelectedNodeState(curNetwork, subNetworkNodeList, false);
                setSelectedEdgeState(curNetwork, subNetworkEdgeList, false);
//                curNetwork.setSelectedNodeState(subNetworkNodeList, false);
//                curNetwork.setSelectedEdgeState(subNetworkEdgeList, false);
                setSelectedNodeState(curNetwork, DCANodeList, true);
                setSelectedEdgeState(curNetwork, DCAEdgeList, true);
//                curNetwork.setSelectedNodeState(DCANodeList, true);
//                curNetwork.setSelectedEdgeState(DCAEdgeList, true);
            }

            //Visualize LCA
            if (MainPanel.chkTermAncestor.isSelected() && MainPanel.chkLCA.isEnabled() && MainPanel.chkLCA.isSelected()) {
                Set<String> CommonAncestorSet = new TreeSet<String>();
                ArrayList<String> selTermIDList = new ArrayList<String>();
                selTermIDList.addAll(selTermIDSet);

                CommonAncestorSet.addAll(Common.extractAncestorTerms(selTermIDList.get(0), BasicData.ParentNodeMap));
                for (i = 1; i < selTermIDList.size(); i++) {
                    CommonAncestorSet.retainAll(Common.extractAncestorTerms(selTermIDList.get(i), BasicData.ParentNodeMap));
                }

                Map<String, Set<String>> ConjAncestorsDAG_OutgoingNodeMap = new TreeMap<String, Set<String>>();
                for (String t : CommonAncestorSet) {
                    if (BasicData.ParentNodeMap.containsKey(t)) {
                        Set<String> OutgoingNodeSet = new TreeSet<String>();
                        for (NodeInteraction ni : BasicData.ParentNodeMap.get(t)) {
                            if (CommonAncestorSet.contains(ni.Node)) {//Dieu kien nay thua, vi Ancestors da chua Parent
                                OutgoingNodeSet.add(ni.Node);
                            }
                        }
                        ConjAncestorsDAG_OutgoingNodeMap.put(t, OutgoingNodeSet);
                    }
                }

                String LowestCommonAncestor = "";//LCA

                //System.out.println("ConjAncestorsDAG_OutgoingNodeMap.size(): " + ConjAncestorsDAG_OutgoingNodeMap.size() + "\t" + ConjAncestorsDAG_OutgoingNodeMap.keySet().toString());
                //ArrayList<Stack> PathList = new ArrayList<Stack>();
                int LongestPathFromLCA2Root = 0;
                Stack<String> LongestPath = new Stack();
                for (String t : CommonAncestorSet) {
                    if (t.compareTo(BasicData.RootTermID) != 0) {
                        Common.connectionPath = new Stack();
                        Common.connectionPaths = new ArrayList<Stack>();

                        Common.findAllPaths(t, BasicData.RootTermID, ConjAncestorsDAG_OutgoingNodeMap);
                        //PathList.addAll(connectionPaths);
                        for (Stack p : Common.connectionPaths) {
                            if (p.size() + 1 > LongestPathFromLCA2Root) {
                                LongestPathFromLCA2Root = p.size() + 1;
                                LowestCommonAncestor = t;
                                LongestPath = p;
                            }
                        }
                    }
                }
                Set<String> LongestPathNodeSet = new TreeSet<String>();
                LongestPathNodeSet.add(BasicData.RootTermID);
                LongestPathNodeSet.add(LowestCommonAncestor);
                LongestPathNodeSet.addAll(LongestPath);

                CyNetwork curNetwork = netMgr.getNetwork(subNetwork.getSUID());//Cytoscape.getCurrentNetwork();
                subNetworkNodeList = curNetwork.getNodeList();
                SelectedNodeList = new ArrayList<CyNode>();
                for (CyNode n : subNetworkNodeList) {
                    if (LongestPathNodeSet.contains(curNetwork.getDefaultNodeTable().getRow(n.getSUID()).getRaw("ID").toString())) {
                        SelectedNodeList.add(n);
                    }
                }

                List<CyEdge> subNetworkEdgeList = curNetwork.getEdgeList();
                List<CyEdge> SelectedEdgeList = new ArrayList<CyEdge>();
                for (CyEdge e : subNetworkEdgeList) {
                    if (LongestPathNodeSet.contains(curNetwork.getDefaultNodeTable().getRow(e.getSource().getSUID()).getRaw("ID").toString())
                            && LongestPathNodeSet.contains(curNetwork.getDefaultNodeTable().getRow(e.getTarget().getSUID()).getRaw("ID").toString())) {
                        SelectedEdgeList.add(e);
                    }
                }
                setSelectedNodeState(curNetwork, subNetworkNodeList, false);
                setSelectedEdgeState(curNetwork, subNetworkEdgeList, false);
//                curNetwork.setSelectedNodeState(subNetworkNodeList, false);
//                curNetwork.setSelectedEdgeState(subNetworkEdgeList, false);

                setSelectedNodeState(curNetwork, SelectedNodeList, true);
                setSelectedEdgeState(curNetwork, SelectedEdgeList, true);
//                curNetwork.setSelectedNodeState(SelectedNodeList, true);
//                curNetwork.setSelectedEdgeState(SelectedEdgeList, true);
            }

            //Visualize Common Tree
            if (MainPanel.chkTermDescendant.isSelected() && MainPanel.chkCommonDescendant.isEnabled() && MainPanel.chkCommonDescendant.isSelected()) {
                Set<String> CommonDescendantSet = new TreeSet<String>();
                ArrayList<String> selTermIDList = new ArrayList<String>();
                selTermIDList.addAll(selTermIDSet);

                CommonDescendantSet.addAll(Common.extractDescendantTerm(selTermIDList.get(0), BasicData.ChildNodeMap));
                for (i = 1; i < selTermIDList.size(); i++) {
                    CommonDescendantSet.retainAll(Common.extractAncestorTerms(selTermIDList.get(i), BasicData.ParentNodeMap));
                }
                CyNetwork curNetwork = netMgr.getNetwork(subNetwork.getSUID());//Cytoscape.getCurrentNetwork();
                subNetworkNodeList = curNetwork.getNodeList();
                List<CyNode> CommonTreeNodeList = new ArrayList<CyNode>();
                for (CyNode n : subNetworkNodeList) {
                    if (CommonDescendantSet.contains(curNetwork.getDefaultNodeTable().getRow(n.getSUID()).getRaw("ID").toString())) {
                        CommonTreeNodeList.add(n);
                    }
                }

                List<CyEdge> subNetworkEdgeList = curNetwork.getEdgeList();
                List<CyEdge> CommonTreeEdgeList = new ArrayList<CyEdge>();
                for (CyEdge e : subNetworkEdgeList) {
                    if (CommonDescendantSet.contains(curNetwork.getDefaultNodeTable().getRow(e.getSource().getSUID()).getRaw("ID").toString())
                            && CommonDescendantSet.contains(curNetwork.getDefaultNodeTable().getRow(e.getTarget().getSUID()).getRaw("ID").toString())) {
                        CommonTreeEdgeList.add(e);
                    }
                }

                setSelectedNodeState(curNetwork, subNetworkNodeList, false);
                setSelectedEdgeState(curNetwork, subNetworkEdgeList, false);

                setSelectedNodeState(curNetwork, CommonTreeNodeList, true);
                setSelectedEdgeState(curNetwork, CommonTreeEdgeList, true);
            }

        } catch (Exception e) {
            this.interrupted = true;
            JOptionPane.showMessageDialog(null, "exception : " + e.getMessage());
        }

        taskMonitor.setProgress(1.0);
    }

    @Override
    public void cancel() {
        this.interrupted = true;
    }

}
