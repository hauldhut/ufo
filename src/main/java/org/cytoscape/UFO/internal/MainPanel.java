package org.cytoscape.UFO.internal;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.cytoscape.UFO.Base.Interaction;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;

/**
 *
 * @author minh
 */
public class MainPanel extends javax.swing.JPanel implements CytoPanelComponent {

    private TaskManager taskManager;
    private VisualMappingManager vmmManager;
    private VisualStyleFactory vsfManager;
    private VisualMappingFunctionFactory vmfFactoryP;
    private VisualMappingFunctionFactory vmfFactoryC;
    private VisualMappingFunctionFactory vmfFactoryD;
    private CyNetworkManager cyNetworkManager;
    private CyNetworkFactory cyNetworkFactory;
    private CyNetworkNaming cyNetworkNaming;
    private CyNetworkViewFactory cyNetworkViewFactory;
    private CyNetworkViewManager cyNetworkViewManager;
    private CyLayoutAlgorithmManager layoutManager;
    private VisualStyle vs;
    private Set<String> selTermIDSet;
    private List<CyEdge> arrCyEdge;
    
    private TaskManager cyTaskManager;
   
    private CyNetworkReaderManager cyNetworkReaderManager;
    
    private CyNetworkNaming namingUtil;
    private SynchronousTaskManager cySynchronousTaskManager; 

    public static int Species_TaxonID = 9606;
    public static String Corpus = "UniProtKB";
    public static String GeneIdentifier = "EntrezID";
    public static Set<String> TermSimSubMetSet = new LinkedHashSet<String>();
    public static Set<String> ObjSimSubMetSet = new LinkedHashSet<String>();

    /**
     * Creates new form MainPanel
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return "Ontology Semantic Similarity & Visualization";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.WEST;
    }

    public class LoadDataResourcesTaskFactory extends AbstractTaskFactory {

        @Override
        public TaskIterator createTaskIterator() {
            return new TaskIterator(new LoadDataResourcesTask(cyNetworkManager, cyNetworkReaderManager, cyNetworkFactory, namingUtil));
        }
    }
    
    public MainPanel(TaskManager taskManagerServiceRef, VisualMappingManager vmmServiceRef, VisualStyleFactory visualStyleFactoryServiceRef, VisualMappingFunctionFactory vmfFactoryC, VisualMappingFunctionFactory vmfFactoryP, VisualMappingFunctionFactory vmfFactoryD, CyNetworkFactory cyNetworkFactoryServiceRef, CyNetworkManager cyNetworkManagerServiceRef, CyNetworkNaming cyNetworkNamingServiceRef,
            CyNetworkViewFactory cyNetworkViewFactory, CyNetworkViewManager cyNetworkViewManager, CyLayoutAlgorithmManager layoutManager) {
        initComponents();
        this.taskManager = taskManagerServiceRef;
        this.vmmManager = vmmServiceRef;
        this.vsfManager = visualStyleFactoryServiceRef;
        this.vmfFactoryC = vmfFactoryC;
        this.vmfFactoryP = vmfFactoryP;
        this.vmfFactoryD = vmfFactoryD;
        this.cyNetworkFactory = cyNetworkFactoryServiceRef;
        this.cyNetworkManager = cyNetworkManagerServiceRef;
        this.cyNetworkNaming = cyNetworkNamingServiceRef;
        this.cyNetworkViewFactory = cyNetworkViewFactory;
        this.cyNetworkViewManager = cyNetworkViewManager;
        this.layoutManager = layoutManager;

        BasicData.Ontology_FileName = "Data" + File.separator + "go.obo";
        BasicData.Annotation_FileName = "Data" + File.separator + "gene2go";
        BasicData.SubOntology = "BP";
        BasicData.RootTermID = "GO:0008150";

        TermSimSubMetSet.add("Node-Based: Resnik (Resnik 1995)");
        TermSimSubMetSet.add("Node-Based: Lin (Lin 1998)");
        TermSimSubMetSet.add("Node-Based: JC (Jiang and Conrath 1997)");
        TermSimSubMetSet.add("Node-Based: Rel (Schlicker 2006)");
        TermSimSubMetSet.add("Node-Based: ResnikGraSM (Couto et al 2005)");
        TermSimSubMetSet.add("Node-Based: LinGraSM (Couto et al 2005)");
        TermSimSubMetSet.add("Node-Based: JCGraSM (Couto et al 2005)");
        TermSimSubMetSet.add("Node-Based: RelGraSM (Couto et al 2005)");
        TermSimSubMetSet.add("Node-Based: Bodenreider et al. [17] - YetOK");
        TermSimSubMetSet.add("Node-Based: Riensche et al. [18] - YetOK");
        TermSimSubMetSet.add("Edge-Based: Wu et al 2005");
        TermSimSubMetSet.add("Edge-Based: Wu et al 2006 - YetOK");
        TermSimSubMetSet.add("Edge-Based: Yu et al 2005");
        TermSimSubMetSet.add("Edge-Based: Cheng et al. [21] - YetOK");
        TermSimSubMetSet.add("Edge-Based: Pozo et al. [24] - YetOK");
        TermSimSubMetSet.add("Hybrid-Based: Wang et al 2007");
        TermSimSubMetSet.add("Hybrid-Based: Othman et al 2007 - YetOK");

        ObjSimSubMetSet.add("Pairwise-Based: Avg (Average)");
        ObjSimSubMetSet.add("Pairwise-Based: Max (Maximum)");
        ObjSimSubMetSet.add("Pairwise-Based: BMA (Best Match Average)");
        ObjSimSubMetSet.add("Pairwise-Based: RCmax (Row-Column Maximum) (Schlicker 2006)");
        ObjSimSubMetSet.add("Groupwise-Graph-Based: Term Overlap (TO) (Lee et al 2004)");
        ObjSimSubMetSet.add("Groupwise-Graph-Based: Normalized Term Overlap (NTO) (Mistry et al 2008)");
        ObjSimSubMetSet.add("Groupwise-Graph-Based: UI (Gentleman 2005)");
        ObjSimSubMetSet.add("Groupwise-Graph-Based: GIC (Pesquita et al 2007)");
        ObjSimSubMetSet.add("Groupwise-Graph-Based: LP (Gentleman 2005)");
        ObjSimSubMetSet.add("Groupwise-Vector-Based (Huang et al 2007)");
        ObjSimSubMetSet.add("Groupwise-Vector-Based (Chabalier et al 2007)");
        
        pnlEntityNet.setVisible(false);
        
        chkCDA.setVisible(false);
        

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        StatisticalTestGroup = new javax.swing.ButtonGroup();
        AdjustPvalueMethodGroup = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        cboOntologyType = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        txtOntologyFile = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cboAnnotationType = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        txtAnnotationFile = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblEvidence = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        btnLoadData = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        tPnlSimVizEA = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        tPnlTerm = new javax.swing.JTabbedPane();
        pnlObjSim = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        tblSelectedTerm = new javax.swing.JTable();
        jLabel19 = new javax.swing.JLabel();
        lblTermInputStatus = new javax.swing.JLabel();
        chkSelAllTerm = new javax.swing.JCheckBox();
        pnlOntSim = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        cboTermSimSubMet = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        cboTermSimMet = new javax.swing.JComboBox();
        btnCalTermSim = new javax.swing.JButton();
        jScrollPane14 = new javax.swing.JScrollPane();
        tblSemSimMatrix = new javax.swing.JTable();
        btnExportSemSimMat = new javax.swing.JButton();
        chkTermNameView = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        chkTermAncestor = new javax.swing.JCheckBox();
        chkTermDescendant = new javax.swing.JCheckBox();
        btnVisualizeSubGraph = new javax.swing.JButton();
        chkAnnotatedObject = new javax.swing.JCheckBox();
        chkCommonAncestor = new javax.swing.JCheckBox();
        chkCommonDescendant = new javax.swing.JCheckBox();
        chkMICA = new javax.swing.JCheckBox();
        chkCDA = new javax.swing.JCheckBox();
        chkLCA = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        tPnlEntity = new javax.swing.JTabbedPane();
        pnlOntSim1 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        tblSelectedObject = new javax.swing.JTable();
        lblObjectInputStatus = new javax.swing.JLabel();
        chkSelAllEntity = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        cboObjSimMet = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        cboObjSimSubMet = new javax.swing.JComboBox();
        btnCalObjSim = new javax.swing.JButton();
        jScrollPane15 = new javax.swing.JScrollPane();
        tblFunSimMatrix = new javax.swing.JTable();
        btnExportFunSimMat = new javax.swing.JButton();
        chkObjectNameView = new javax.swing.JCheckBox();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        optHypergeometric = new javax.swing.JRadioButton();
        optFishersExact = new javax.swing.JRadioButton();
        jPanel12 = new javax.swing.JPanel();
        optBonferroni = new javax.swing.JRadioButton();
        optBenjaminiHochberg = new javax.swing.JRadioButton();
        btnEnrichmentAnalysis = new javax.swing.JButton();
        jScrollPane16 = new javax.swing.JScrollPane();
        tblTermEnrichment = new javax.swing.JTable();
        pnlObjSim1 = new javax.swing.JPanel();
        chkDirectTerm = new javax.swing.JCheckBox();
        chkDirectAncestorTerm = new javax.swing.JCheckBox();
        btnVisualizeAnnotatingGraph = new javax.swing.JButton();
        chkDirectDescendantTerm = new javax.swing.JCheckBox();
        chkFunSimInteraction = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        txtMinFunSim = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtMaxFunSim = new javax.swing.JTextField();
        pnlEntityNet = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cboNetwork = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        cboInteraction = new javax.swing.JComboBox();
        btnOK = new javax.swing.JButton();
        btnLoadNetworkList = new javax.swing.JButton();
        btnExportWeightedInteraction = new javax.swing.JButton();
        chkObjectInteractionNameView = new javax.swing.JCheckBox();
        jScrollPane17 = new javax.swing.JScrollPane();
        tblWeightedInteraction = new javax.swing.JTable();
        pnlEntitySets = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane18 = new javax.swing.JScrollPane();
        tblObjectSet1 = new javax.swing.JTable();
        jScrollPane19 = new javax.swing.JScrollPane();
        tblObjectSet2 = new javax.swing.JTable();
        jLabel22 = new javax.swing.JLabel();
        btnObjectSetFunctionalSimiliarity = new javax.swing.JButton();
        lblEntitySetSim = new javax.swing.JLabel();

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ontology & Annotation Data", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13))); // NOI18N

        jLabel8.setText("Ontology");

        cboOntologyType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Gene Ontology (GO) - Biological Process", "Gene Ontology (GO) - Cellular Component", "Gene Ontology (GO) - Molecular Function", "Human Phenotype Ontology (HPO)", "Human Disease Ontology (DO)", "Select other Ontology data..." }));
        cboOntologyType.setSelectedIndex(3);
        cboOntologyType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboOntologyTypeActionPerformed(evt);
            }
        });

        jLabel9.setText("File (*.obo)");

        txtOntologyFile.setText("Data" + File.separator + "hp.obo");

        jLabel10.setText("Annotation");

        cboAnnotationType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Gene Ontology (GO) To Gene", "Human Phenotype Ontology (HPO) To Phenotype", "Disease Ontology (DO) To Gene", "Select other Annotation data..." }));
        cboAnnotationType.setSelectedIndex(1);
        cboAnnotationType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboAnnotationTypeActionPerformed(evt);
            }
        });

        jLabel11.setText("File (tab)");

        txtAnnotationFile.setText("Data" + File.separator + "Annotation_OMIM2HPO_OMIM.txt");

        jLabel2.setText("Evidence");

        tblEvidence.setAutoCreateRowSorter(true);
        tblEvidence.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                { new Boolean(true), "All",  new Boolean(true), "User-defined",  new Boolean(true), "ICE"},
                { new Boolean(true), "IEA",  new Boolean(true), "ISS",  new Boolean(true), "TAS"},
                { new Boolean(true), "ND",  new Boolean(true), "IDA",  new Boolean(true), "IMP"},
                { new Boolean(true), "IPI",  new Boolean(true), "NAS",  new Boolean(true), "IEP"},
                { new Boolean(true), "IGI",  new Boolean(true), "IC",  new Boolean(true), "RCA"},
                { new Boolean(true), "ISO",  new Boolean(true), "ISA",  new Boolean(true), "ISM"},
                { new Boolean(true), "EXP",  new Boolean(true), "IGC",  new Boolean(true), "PCS"}
            },
            new String [] {
                "Sel", "Code", "Sel", "Code", "Sel", "Code"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblEvidence.setShowVerticalLines(false);
        tblEvidence.setTableHeader(null);
        tblEvidence.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEvidenceMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(tblEvidence);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel2))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cboOntologyType, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOntologyFile, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboAnnotationType, 0, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtAnnotationFile)))
            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel8))
                    .addComponent(cboOntologyType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel9))
                    .addComponent(txtOntologyFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(cboAnnotationType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel11))
                    .addComponent(txtAnnotationFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        btnLoadData.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btnLoadData.setForeground(new java.awt.Color(255, 51, 51));
        btnLoadData.setText("Load & Prepare Data");
        btnLoadData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadDataActionPerformed(evt);
            }
        });

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Similarity Calculation, Visualization & Enrichment Analysis", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13))); // NOI18N

        tPnlTerm.setName(""); // NOI18N

        tblSelectedTerm.setAutoCreateRowSorter(true);
        tblSelectedTerm.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sel", "ID", "Name", "IC", "Annotated Entities"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblSelectedTerm.setToolTipText("");
        tblSelectedTerm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSelectedTermMouseClicked(evt);
            }
        });
        jScrollPane12.setViewportView(tblSelectedTerm);

        jLabel19.setText("Select a Term/Term Set");

        lblTermInputStatus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTermInputStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTermInputStatus.setText("...");

        chkSelAllTerm.setText("All");
        chkSelAllTerm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSelAllTermActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlObjSimLayout = new javax.swing.GroupLayout(pnlObjSim);
        pnlObjSim.setLayout(pnlObjSimLayout);
        pnlObjSimLayout.setHorizontalGroup(
            pnlObjSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlObjSimLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlObjSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlObjSimLayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkSelAllTerm)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTermInputStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(77, 77, 77))
        );
        pnlObjSimLayout.setVerticalGroup(
            pnlObjSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlObjSimLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlObjSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(lblTermInputStatus)
                    .addComponent(chkSelAllTerm))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                .addContainerGap())
        );

        tPnlTerm.addTab("Input", pnlObjSim);

        jLabel13.setText("Method");

        cboTermSimSubMet.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Node-Based: Resnik (Resnik 1995)", "Node-Based: Lin (Lin 1998)", "Node-Based: JC (Jiang and Conrath 1997)", "Node-Based: Rel (Schlicker 2006)", "Node-Based: ResnikGraSM (Couto et al 2005)", "Node-Based: LinGraSM (Couto et al 2005)", "Node-Based: JCGraSM (Couto et al 2005)", "Node-Based: RelGraSM (Couto et al 2005)", "Node-Based: Bodenreider et al. [17] - YetOK", "Node-Based: Riensche et al. [18] - YetOK", "Edge-Based: Wu et al 2005", "Edge-Based: Wu et al 2006 - YetOK", "Edge-Based: Yu et al 2005", "Edge-Based: Cheng et al. [21] - YetOK", "Edge-Based: Pozo et al. [24] - YetOK", "Hybrid-Based: Wang et al 2007", "Hybrid-Based: Othman et al 2007 - YetOK", " " }));

        jLabel12.setText("Category");

        cboTermSimMet.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Node-Based", "Edge-Based", "Hybrid-Based" }));
        cboTermSimMet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTermSimMetActionPerformed(evt);
            }
        });

        btnCalTermSim.setText("Calculate");
        btnCalTermSim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalTermSimActionPerformed(evt);
            }
        });

        tblSemSimMatrix.setAutoCreateRowSorter(true);
        tblSemSimMatrix.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Similarity Matrix"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblSemSimMatrix.setToolTipText("");
        tblSemSimMatrix.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSemSimMatrixMouseClicked(evt);
            }
        });
        jScrollPane14.setViewportView(tblSemSimMatrix);

        btnExportSemSimMat.setText("Export...");
        btnExportSemSimMat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportSemSimMatActionPerformed(evt);
            }
        });

        chkTermNameView.setText("Name View");
        chkTermNameView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTermNameViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlOntSimLayout = new javax.swing.GroupLayout(pnlOntSim);
        pnlOntSim.setLayout(pnlOntSimLayout);
        pnlOntSimLayout.setHorizontalGroup(
            pnlOntSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOntSimLayout.createSequentialGroup()
                .addGroup(pnlOntSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOntSimLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlOntSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlOntSimLayout.createSequentialGroup()
                                .addGroup(pnlOntSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlOntSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboTermSimMet, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cboTermSimSubMet, 0, 1, Short.MAX_VALUE)))
                            .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOntSimLayout.createSequentialGroup()
                        .addComponent(btnCalTermSim, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(chkTermNameView)
                        .addGap(62, 62, 62)
                        .addComponent(btnExportSemSimMat)))
                .addGap(11, 11, 11))
        );
        pnlOntSimLayout.setVerticalGroup(
            pnlOntSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOntSimLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOntSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cboTermSimMet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOntSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(cboTermSimSubMet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOntSimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCalTermSim)
                    .addComponent(btnExportSemSimMat)
                    .addComponent(chkTermNameView))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                .addContainerGap())
        );

        tPnlTerm.addTab("Semantic Similarity", pnlOntSim);

        chkTermAncestor.setSelected(true);
        chkTermAncestor.setText("Terms and Their Ancestors (Sub-DAG)");
        chkTermAncestor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTermAncestorActionPerformed(evt);
            }
        });

        chkTermDescendant.setText("Terms and Their Descendants (Sub-Tree)");
        chkTermDescendant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTermDescendantActionPerformed(evt);
            }
        });

        btnVisualizeSubGraph.setText("Visualize");
        btnVisualizeSubGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVisualizeSubGraphActionPerformed(evt);
            }
        });

        chkAnnotatedObject.setText("Annotated Entities");

        chkCommonAncestor.setText("Highlight Common Ancestors");
        chkCommonAncestor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCommonAncestorActionPerformed(evt);
            }
        });

        chkCommonDescendant.setText("Highlight Common Descendants");
        chkCommonDescendant.setEnabled(false);

        chkMICA.setText("Highlight Most Informative Common Ancestor (MICA)");

        chkCDA.setText("Highlight Common Disjunctive Ancestor (CDA)");
        chkCDA.setToolTipText("Only enabled when two terms are selected");

        chkLCA.setText("Highlight Longest Path of Common Ancestor (LCA)");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(chkTermAncestor)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkTermDescendant, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkAnnotatedObject)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkCommonDescendant)
                                    .addComponent(chkCommonAncestor)
                                    .addComponent(chkMICA)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(chkLCA)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(chkCDA)))))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnVisualizeSubGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkTermAncestor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCommonAncestor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMICA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCDA)
                    .addComponent(chkLCA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkTermDescendant)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCommonDescendant)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkAnnotatedObject)
                .addGap(49, 49, 49)
                .addComponent(btnVisualizeSubGraph)
                .addContainerGap(153, Short.MAX_VALUE))
        );

        tPnlTerm.addTab("Visualization", jPanel8);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tPnlTerm, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(tPnlTerm)
                .addGap(1, 1, 1))
        );

        tPnlSimVizEA.addTab("Term (Set)", jPanel5);

        jLabel20.setText("Select an Entity/Entity Set");
        jLabel20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel20MouseClicked(evt);
            }
        });

        tblSelectedObject.setAutoCreateRowSorter(true);
        tblSelectedObject.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sel", "ID", "Name", "Annotating Terms"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblSelectedObject.setToolTipText("");
        tblSelectedObject.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSelectedObjectMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tblSelectedObjectMouseEntered(evt);
            }
        });
        jScrollPane13.setViewportView(tblSelectedObject);

        lblObjectInputStatus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblObjectInputStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblObjectInputStatus.setText("...");

        chkSelAllEntity.setText("All");
        chkSelAllEntity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSelAllEntityActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlOntSim1Layout = new javax.swing.GroupLayout(pnlOntSim1);
        pnlOntSim1.setLayout(pnlOntSim1Layout);
        pnlOntSim1Layout.setHorizontalGroup(
            pnlOntSim1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOntSim1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOntSim1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlOntSim1Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkSelAllEntity)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblObjectInputStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        pnlOntSim1Layout.setVerticalGroup(
            pnlOntSim1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOntSim1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOntSim1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(lblObjectInputStatus)
                    .addComponent(chkSelAllEntity))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                .addContainerGap())
        );

        tPnlEntity.addTab("Input", pnlOntSim1);

        jLabel14.setText("Category");

        cboObjSimMet.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pairwise", "Groupwise" }));
        cboObjSimMet.setToolTipText("Between-term Similarity Method is defined in Term (Set) --> Semantic Similarity tab");
        cboObjSimMet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboObjSimMetActionPerformed(evt);
            }
        });

        jLabel15.setText("Method");

        cboObjSimSubMet.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pairwise-Based: Avg (Average)", "Pairwise-Based: Max (Maximum)", "Pairwise-Based: BMA (Best Match Average)", "Pairwise-Based: RCmax (Row-Column Maximum) (Schlicker 2006)", "Groupwise-Graph-Based: Term Overlap (TO) (Lee et al 2004)", "Groupwise-Graph-Based: Normalized Term Overlap (NTO) (Mistry et al 2008)", "Groupwise-Graph-Based: UI (Gentleman 2005)", "Groupwise-Graph-Based: GIC (Pesquita et al 2007)", "Groupwise-Graph-Based: LP (Gentleman 2005)", "Groupwise-Vector-Based (Huang et al 2007)", "Groupwise-Vector-Based (Chabalier et al 2007)" }));

        btnCalObjSim.setText("Calculate");
        btnCalObjSim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalObjSimActionPerformed(evt);
            }
        });

        tblFunSimMatrix.setAutoCreateRowSorter(true);
        tblFunSimMatrix.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Similarity Matrix"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblFunSimMatrix.setToolTipText("");
        tblFunSimMatrix.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblFunSimMatrixMouseClicked(evt);
            }
        });
        jScrollPane15.setViewportView(tblFunSimMatrix);

        btnExportFunSimMat.setText("Export...");
        btnExportFunSimMat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportFunSimMatActionPerformed(evt);
            }
        });

        chkObjectNameView.setText("Name View");
        chkObjectNameView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkObjectNameViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                        .addComponent(btnCalObjSim, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkObjectNameView)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                        .addComponent(btnExportFunSimMat))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboObjSimMet, 0, 315, Short.MAX_VALUE)
                            .addComponent(cboObjSimSubMet, 0, 1, Short.MAX_VALUE)))
                    .addComponent(jScrollPane15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(cboObjSimMet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(cboObjSimSubMet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCalObjSim)
                    .addComponent(btnExportFunSimMat)
                    .addComponent(chkObjectNameView))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                .addContainerGap())
        );

        tPnlEntity.addTab("Functional Similarity", jPanel9);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Statistical Test")));

        StatisticalTestGroup.add(optHypergeometric);
        optHypergeometric.setSelected(true);
        optHypergeometric.setText("Hypergeometric");

        StatisticalTestGroup.add(optFishersExact);
        optFishersExact.setText("Fisher's Exact");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(optHypergeometric)
                    .addComponent(optFishersExact)))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optHypergeometric)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(optFishersExact)
                .addGap(50, 50, 50))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Adjusted P-value"));

        AdjustPvalueMethodGroup.add(optBonferroni);
        optBonferroni.setSelected(true);
        optBonferroni.setText("Bonferroni");

        AdjustPvalueMethodGroup.add(optBenjaminiHochberg);
        optBenjaminiHochberg.setText("Benjamini and Hochberg");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(optBonferroni)
                    .addComponent(optBenjaminiHochberg))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optBonferroni)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optBenjaminiHochberg, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnEnrichmentAnalysis.setText("Analyze");
        btnEnrichmentAnalysis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnrichmentAnalysisActionPerformed(evt);
            }
        });

        tblTermEnrichment.setAutoCreateRowSorter(true);
        tblTermEnrichment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "TermID", "Name", "P-value", "Adjusted P-value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTermEnrichment.setToolTipText("");
        tblTermEnrichment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTermEnrichmentMouseClicked(evt);
            }
        });
        jScrollPane16.setViewportView(tblTermEnrichment);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEnrichmentAnalysis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                .addGap(14, 14, 14)
                .addComponent(btnEnrichmentAnalysis)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane16, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                .addContainerGap())
        );

        tPnlEntity.addTab("Enrichment", jPanel10);

        chkDirectTerm.setSelected(true);
        chkDirectTerm.setText("Annotating Terms");

        chkDirectAncestorTerm.setText("Annotating Terms and their Ancestors");

        btnVisualizeAnnotatingGraph.setText("Visualize");
        btnVisualizeAnnotatingGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVisualizeAnnotatingGraphActionPerformed(evt);
            }
        });

        chkDirectDescendantTerm.setText("Annotating Terms and their Descendants");

        chkFunSimInteraction.setText("Functional Similarity Interactions");
        chkFunSimInteraction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFunSimInteractionActionPerformed(evt);
            }
        });

        jLabel4.setText("Min");

        txtMinFunSim.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setText("Max");

        txtMaxFunSim.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setText("Network");

        cboNetwork.setToolTipText("Load and Select an Interaction Network of Objects");
        cboNetwork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboNetworkActionPerformed(evt);
            }
        });

        jLabel1.setText("Interaction");

        cboInteraction.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Selected Interactions", "All network Interactions" }));

        btnOK.setText("Weight");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnLoadNetworkList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Refresh.jpg"))); // NOI18N
        btnLoadNetworkList.setToolTipText("Load Object Interaction Network List");
        btnLoadNetworkList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadNetworkListActionPerformed(evt);
            }
        });

        btnExportWeightedInteraction.setText("Export...");
        btnExportWeightedInteraction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportWeightedInteractionActionPerformed(evt);
            }
        });

        chkObjectInteractionNameView.setText("Name View");
        chkObjectInteractionNameView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkObjectInteractionNameViewActionPerformed(evt);
            }
        });

        tblWeightedInteraction.setAutoCreateRowSorter(true);
        tblWeightedInteraction.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Entity1 ID", "Weight", "Entity2 ID", "Note"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblWeightedInteraction.setToolTipText("");
        tblWeightedInteraction.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblWeightedInteractionMouseClicked(evt);
            }
        });
        jScrollPane17.setViewportView(tblWeightedInteraction);

        javax.swing.GroupLayout pnlEntityNetLayout = new javax.swing.GroupLayout(pnlEntityNet);
        pnlEntityNet.setLayout(pnlEntityNetLayout);
        pnlEntityNetLayout.setHorizontalGroup(
            pnlEntityNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEntityNetLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEntityNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlEntityNetLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(cboNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLoadNetworkList, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlEntityNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlEntityNetLayout.createSequentialGroup()
                            .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(chkObjectInteractionNameView)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnExportWeightedInteraction))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlEntityNetLayout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cboInteraction, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane17, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlEntityNetLayout.setVerticalGroup(
            pnlEntityNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlEntityNetLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEntityNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlEntityNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(cboNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnLoadNetworkList, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlEntityNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboInteraction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlEntityNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnExportWeightedInteraction)
                    .addComponent(chkObjectInteractionNameView))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane17, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlObjSim1Layout = new javax.swing.GroupLayout(pnlObjSim1);
        pnlObjSim1.setLayout(pnlObjSim1Layout);
        pnlObjSim1Layout.setHorizontalGroup(
            pnlObjSim1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlObjSim1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlObjSim1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnVisualizeAnnotatingGraph, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                    .addComponent(chkDirectDescendantTerm)
                    .addComponent(chkDirectTerm)
                    .addComponent(chkDirectAncestorTerm, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFunSimInteraction)
                    .addGroup(pnlObjSim1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMinFunSim, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMaxFunSim))
                    .addComponent(pnlEntityNet, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlObjSim1Layout.setVerticalGroup(
            pnlObjSim1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlObjSim1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkDirectTerm)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDirectAncestorTerm)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDirectDescendantTerm)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFunSimInteraction)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlObjSim1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtMinFunSim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtMaxFunSim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addComponent(btnVisualizeAnnotatingGraph)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlEntityNet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tPnlEntity.addTab("Visualization", pnlObjSim1);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tPnlEntity)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tPnlEntity, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
        );

        tPnlSimVizEA.addTab("Entity (Set)", jPanel7);

        jLabel21.setText("Select Entity Set #1");

        tblObjectSet1.setAutoCreateRowSorter(true);
        tblObjectSet1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sel", "ID", "Name", "Annotating Terms"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblObjectSet1.setToolTipText("");
        tblObjectSet1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblObjectSet1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tblObjectSet1MouseEntered(evt);
            }
        });
        jScrollPane18.setViewportView(tblObjectSet1);

        tblObjectSet2.setAutoCreateRowSorter(true);
        tblObjectSet2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sel", "ID", "Name", "Annotating Terms"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblObjectSet2.setToolTipText("");
        tblObjectSet2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblObjectSet2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tblObjectSet2MouseEntered(evt);
            }
        });
        jScrollPane19.setViewportView(tblObjectSet2);

        jLabel22.setText("Select Entity Set #2");

        btnObjectSetFunctionalSimiliarity.setText("Calculate Entity Set Functional Similarity");
        btnObjectSetFunctionalSimiliarity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnObjectSetFunctionalSimiliarityActionPerformed(evt);
            }
        });

        lblEntitySetSim.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblEntitySetSim.setText("...");

        javax.swing.GroupLayout pnlEntitySetsLayout = new javax.swing.GroupLayout(pnlEntitySets);
        pnlEntitySets.setLayout(pnlEntitySetsLayout);
        pnlEntitySetsLayout.setHorizontalGroup(
            pnlEntitySetsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEntitySetsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEntitySetsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnObjectSetFunctionalSimiliarity, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                    .addGroup(pnlEntitySetsLayout.createSequentialGroup()
                        .addGroup(pnlEntitySetsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(jScrollPane18, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlEntitySetsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlEntitySetsLayout.createSequentialGroup()
                                .addComponent(jScrollPane19, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(2, 2, 2))
                            .addGroup(pnlEntitySetsLayout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(lblEntitySetSim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlEntitySetsLayout.setVerticalGroup(
            pnlEntitySetsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlEntitySetsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEntitySetsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlEntitySetsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane19, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                    .addComponent(jScrollPane18, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnObjectSetFunctionalSimiliarity)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblEntitySetSim)
                .addContainerGap())
        );

        tPnlSimVizEA.addTab("Between Entity Sets", pnlEntitySets);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tPnlSimVizEA)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tPnlSimVizEA))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(btnLoadData, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLoadData)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleName("");
    }// </editor-fold>//GEN-END:initComponents

    private void btnObjectSetFunctionalSimiliarityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnObjectSetFunctionalSimiliarityActionPerformed
        int i, j;
        BasicData.validSelObject1IDList = new ArrayList<String>();
        for (i = 0; i < tblObjectSet1.getRowCount(); i++) {
            if (Boolean.parseBoolean(tblObjectSet1.getValueAt(i, 0).toString()) == true) {
                String ID = tblObjectSet1.getValueAt(i, 1).toString();
                BasicData.validSelObject1IDList.add(ID);
            }
        }
        if (BasicData.validSelObject1IDList.size() <= 1) {
            JOptionPane.showMessageDialog(this.getRootPane(), "Please select at least two objects for Entity Set #1!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BasicData.validSelObject2IDList = new ArrayList<String>();
        for (i = 0; i < tblObjectSet2.getRowCount(); i++) {
            if (Boolean.parseBoolean(tblObjectSet2.getValueAt(i, 0).toString()) == true) {
                String ID = tblObjectSet2.getValueAt(i, 1).toString();
                BasicData.validSelObject2IDList.add(ID);
            }
        }
        if (BasicData.validSelObject2IDList.size() <= 1) {
            JOptionPane.showMessageDialog(this.getRootPane(), "Please select at least two objects for Entity Set #2!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String Method = "";
        if (optHypergeometric.isSelected()) {
            Method = "Hypergeometric";
        }

        Map<String, Double> Term2PvalueMap1 = Common.calculateSignificantEnrichmentTerms(BasicData.validSelObject1IDList, Method);
        Map<String, Double> Term2PvalueMap2 = Common.calculateSignificantEnrichmentTerms(BasicData.validSelObject2IDList, Method);
        double threshold = 0.1;
        BasicData.SignificantTermSet1 = new TreeSet<String>();
        for (Map.Entry<String, Double> e : Term2PvalueMap1.entrySet()) {
            if (e.getValue() <= threshold) {
                BasicData.SignificantTermSet1.add(e.getKey());
            }
        }

        BasicData.SignificantTermSet2 = new TreeSet<String>();
        for (Map.Entry<String, Double> e : Term2PvalueMap2.entrySet()) {
            if (e.getValue() <= threshold) {
                BasicData.SignificantTermSet2.add(e.getKey());
            }
        }

        System.out.println("SignificantTermSet1 :" + BasicData.SignificantTermSet1.size() + "\t" + BasicData.SignificantTermSet1.toString());
        System.out.println("SignificantTermSet2 :" + BasicData.SignificantTermSet2.size() + "\t" + BasicData.SignificantTermSet2.toString());

        if (BasicData.SignificantTermSet1.size() == 0 && BasicData.SignificantTermSet2.size() == 0) {
            JOptionPane.showMessageDialog(this.getRootPane(), "No significant term is annotated to Entity Set #1 and #2", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if (BasicData.SignificantTermSet1.size() == 0 && BasicData.SignificantTermSet2.size() > 0) {
            JOptionPane.showMessageDialog(this.getRootPane(), "No significant term is annotated to Entity Set #1", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if (BasicData.SignificantTermSet1.size() > 0 && BasicData.SignificantTermSet2.size() == 0) {
            JOptionPane.showMessageDialog(this.getRootPane(), "No significant term is annotated to Entity Set #2", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        CalculateFunSimBetweenObjectSetTaskFactory calculateFunSimBetweenObjectSetTaskFactory = new CalculateFunSimBetweenObjectSetTaskFactory();
        TaskObserver obs = new TaskObserver() {
            @Override
            public void taskFinished(ObservableTask task) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void allFinished(FinishStatus finishStatus) {
                //JOptionPane.showMessageDialog(null, "Similarity is: " + BasicData.ObjectSetSim, "Notice", JOptionPane.INFORMATION_MESSAGE);
                lblEntitySetSim.setText("Similarity is: " + BasicData.ObjectSetSim);
            }
        };
        this.taskManager.execute(calculateFunSimBetweenObjectSetTaskFactory.createTaskIterator(),obs);
    }//GEN-LAST:event_btnObjectSetFunctionalSimiliarityActionPerformed

    private void tblObjectSet2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblObjectSet2MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tblObjectSet2MouseEntered

    private void tblObjectSet2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblObjectSet2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblObjectSet2MouseClicked

    private void tblObjectSet1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblObjectSet1MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tblObjectSet1MouseEntered

    private void tblObjectSet1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblObjectSet1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblObjectSet1MouseClicked

    private void chkFunSimInteractionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFunSimInteractionActionPerformed
        // TODO add your handling code here:
        if (chkFunSimInteraction.isSelected()) {
            txtMaxFunSim.setEnabled(true);
            txtMinFunSim.setEnabled(true);
        } else {
            txtMaxFunSim.setEnabled(false);
            txtMinFunSim.setEnabled(false);
        }
    }//GEN-LAST:event_chkFunSimInteractionActionPerformed

    private void btnVisualizeAnnotatingGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVisualizeAnnotatingGraphActionPerformed
        int i, j;
        BasicData.validSelObjectIDList = new ArrayList<String>();
        for (i = 0; i < tblSelectedObject.getRowCount(); i++) {
            if (Boolean.parseBoolean(tblSelectedObject.getValueAt(i, 0).toString()) == true) {
                String ID = tblSelectedObject.getValueAt(i, 1).toString();
                BasicData.validSelObjectIDList.add(ID);
            }
        }
        if (BasicData.validSelObjectIDList.size() <= 0) {
            JOptionPane.showMessageDialog(this.getRootPane(), "Please select at least one object!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtMaxFunSim.getText().equals("") || txtMinFunSim.getText().equals("")) {
            JOptionPane.showMessageDialog(this.getRootPane(), "Please fill all values of Min and Max", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        VisualizeObjectAnnotatingTermTaskFactory visualizeObjectAnnotatingTermTaskFactory = new VisualizeObjectAnnotatingTermTaskFactory();
        this.taskManager.execute(visualizeObjectAnnotatingTermTaskFactory.createTaskIterator());
        JOptionPane.showMessageDialog(this.getRootPane(), "Done!");
    }//GEN-LAST:event_btnVisualizeAnnotatingGraphActionPerformed

    private void tblTermEnrichmentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTermEnrichmentMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblTermEnrichmentMouseClicked

    private void btnEnrichmentAnalysisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnrichmentAnalysisActionPerformed

        int i, j;

        BasicData.validSelObjectIDList = new ArrayList<String>();
        for (i = 0; i < tblSelectedObject.getRowCount(); i++) {
            if (Boolean.parseBoolean(tblSelectedObject.getValueAt(i, 0).toString()) == true) {
                String ID = tblSelectedObject.getValueAt(i, 1).toString();
                BasicData.validSelObjectIDList.add(ID);
            }
        }
        if (BasicData.validSelObjectIDList.size() <= 1) {
            JOptionPane.showMessageDialog(this.getRootPane(), "Please select at least two objects!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        EnrichmentAnalysisTaskFactory enrichmentAnalysisTaskFactory = new EnrichmentAnalysisTaskFactory();
        this.taskManager.execute(enrichmentAnalysisTaskFactory.createTaskIterator());

        JOptionPane.showMessageDialog(this.getRootPane(), "Done!");
    }//GEN-LAST:event_btnEnrichmentAnalysisActionPerformed

    private void chkObjectNameViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkObjectNameViewActionPerformed
        // SUPB - 20160405 ADD - View by Name not ID of Entity- START
        if (CalculateFunSimMatrixTask.SimMatrix == null || CalculateFunSimMatrixTask.SimMatrix.size() == 0) {
            return;
        }
        Map<String, Map<String, Double>> SimMatrixName = new LinkedHashMap<String, Map<String, Double>>();
        for (Map.Entry<String, Map<String, Double>> e : CalculateFunSimMatrixTask.SimMatrix.entrySet()) {
            String Name = BasicData.ObjectID2NameMap.get(e.getKey());
            Map<String, Double> map = new LinkedHashMap<String, Double>();
            for (Map.Entry<String, Double> e1 : e.getValue().entrySet()) {
                map.put(BasicData.ObjectID2NameMap.get(e1.getKey()), e1.getValue());
            }
            SimMatrixName.put(Name, map);
        }
        if (chkObjectNameView.isSelected()) {
            fillSimTable(SimMatrixName, tblFunSimMatrix);
        } else {
            fillSimTable(CalculateFunSimMatrixTask.SimMatrix, tblFunSimMatrix);
        }
        // SUPB - 20160405 ADD - View by Name not ID of Entity- END
    }//GEN-LAST:event_chkObjectNameViewActionPerformed

    private void btnExportFunSimMatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportFunSimMatActionPerformed
        // SUPB - 20160405 ADD - Implement export function - START
        JFileChooser fc = new JFileChooser();
        //fc.setFileFilter(new FileTypeFilter());
        fc.setDialogTitle("Save Functional Similarity Matrix");
        fc.setCurrentDirectory(new File("."));

        int returnVal = fc.showSaveDialog(this.getRootPane());
        //int returnVal=fc.showSaveDialog(this);
        String fileName = "";
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileName = fc.getSelectedFile().getAbsolutePath();
            //JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"You chose to open this file: " + fc.getSelectedFile().getName());
        } else {
            return;
        }
        try {
            PrintWriter output = new PrintWriter(new FileOutputStream(fileName), true); //auto flush
            int i, j;
            output.print("ID");
            for (i = 1; i < tblFunSimMatrix.getColumnCount(); i++) {
                output.print("\t" + tblFunSimMatrix.getColumnName(i));
            }
            output.println();
            for (i = 0; i < tblFunSimMatrix.getRowCount(); i++) {
                output.print(tblFunSimMatrix.getValueAt(i, 0).toString());
                for (j = 1; j < tblFunSimMatrix.getColumnCount(); j++) {
                    output.print("\t" + ((tblFunSimMatrix.getValueAt(i, j) != null) ? tblFunSimMatrix.getValueAt(i, j).toString() : " "));
                }
                output.println();
            }

            output.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.getRootPane(), "Error while exporting Functional Similarity Matrix: " + e.toString());
            e.printStackTrace();
        }
        // SUPB - 20160405 ADD - Implement export function - END
    }//GEN-LAST:event_btnExportFunSimMatActionPerformed

    private void tblFunSimMatrixMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFunSimMatrixMouseClicked
        // SUPB - 20160405 ADD - Give more information in matrix - START
        if (tblFunSimMatrix.getSelectedColumn() == 0) {
            return;
        }

        String Object1ID = (String) tblFunSimMatrix.getValueAt(tblFunSimMatrix.getSelectedRow(), 0);
        String Object2ID = tblFunSimMatrix.getColumnName(tblFunSimMatrix.getSelectedColumn());

        Set<String> AncTerm1 = Common.extractAncestorTerms(Object1ID, BasicData.ParentNodeMap);
        Set<String> AncTerm2 = Common.extractAncestorTerms(Object2ID, BasicData.ParentNodeMap);
        Set<String> CommonAncestors = new TreeSet<String>();
        CommonAncestors.addAll(AncTerm1);
        CommonAncestors.retainAll(AncTerm2);

        System.out.println(Object1ID + " && " + Object2ID + " have ObjectSim: " + tblFunSimMatrix.getValueAt(tblFunSimMatrix.getSelectedRow(), tblFunSimMatrix.getSelectedColumn()) + "\nNuAnnTerm1: " + BasicData.Object2TermMap.get(Object1ID).size() + " " + BasicData.Object2TermMap.get(Object1ID).toString() + "\nNuAnnTerm2: " + BasicData.Object2TermMap.get(Object2ID).size() + " " + BasicData.Object2TermMap.get(Object2ID).toString());

        JOptionPane.showMessageDialog(this.getRootPane(), Object1ID + " && " + Object2ID + " have ObjectSim: " + tblFunSimMatrix.getValueAt(tblFunSimMatrix.getSelectedRow(), tblFunSimMatrix.getSelectedColumn()) + "\nNuAnnTerm1: " + BasicData.Object2TermMap.get(Object1ID).size() + " " + BasicData.Object2TermMap.get(Object1ID).toString() + "\nNuAnnTerm2: " + BasicData.Object2TermMap.get(Object2ID).size() + " " + BasicData.Object2TermMap.get(Object2ID).toString(), "Information", JOptionPane.INFORMATION_MESSAGE);
        // SUPB - 20160405 ADD - Give more information in matrix - END
    }//GEN-LAST:event_tblFunSimMatrixMouseClicked

    private void btnCalObjSimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalObjSimActionPerformed
        // SUPB - 20160405 ADD - Implement algorithms to calculate similarity between entities - START
        chkObjectNameView.setSelected(false);

        int i, j;
        BasicData.validSelObjectIDList = new ArrayList<String>();
        for (i = 0; i < tblSelectedObject.getRowCount(); i++) {
            if (Boolean.parseBoolean(tblSelectedObject.getValueAt(i, 0).toString()) == true) {
                String ID = tblSelectedObject.getValueAt(i, 1).toString();
                BasicData.validSelObjectIDList.add(ID);
            }
        }
        if (BasicData.validSelObjectIDList.size() <= 1) {
            JOptionPane.showMessageDialog(this.getRootPane(), "Please select at least two objects!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CalculateFunSimMatrixTaskFactory calculateFunSimMatrixTaskFactory = new CalculateFunSimMatrixTaskFactory();
        this.taskManager.execute(calculateFunSimMatrixTaskFactory.createTaskIterator());

        JOptionPane.showMessageDialog(this.getRootPane(), "Done!");
        // SUPB - 20160405 ADD - Implement algorithms to calculate similarity between entities - END
    }//GEN-LAST:event_btnCalObjSimActionPerformed

    private void cboObjSimMetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboObjSimMetActionPerformed
        // SUPB - 20160405 ADD - Dependent picklist (Method follow Category - END
            String ObjSimMed = (String) this.cboObjSimMet.getSelectedItem();
            this.cboObjSimSubMet.removeAllItems();
            for (String objectsimsubmet : this.ObjSimSubMetSet) {
                if (objectsimsubmet.contains(ObjSimMed)) {
                    this.cboObjSimSubMet.addItem(objectsimsubmet);
                }
            }
            // SUPB - 20160405 ADD - Dependent picklist (Method follow Category - END
    }//GEN-LAST:event_cboObjSimMetActionPerformed

    private void chkSelAllEntityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSelAllEntityActionPerformed
        // TODO add your handling code here:
        int i=0;
        if(chkSelAllEntity.isSelected()){
            for(i=0;i<tblSelectedObject.getRowCount();i++){
                tblSelectedObject.setValueAt(true, i, 0);
            }
            lblObjectInputStatus.setText("All selected/" + BasicData.ObjectID2NameMap.size() + " total");
        }else{
            for(i=0;i<tblSelectedObject.getRowCount();i++){
                tblSelectedObject.setValueAt(false, i, 0);
            }
            lblObjectInputStatus.setText("None selected/" + BasicData.ObjectID2NameMap.size() + " total");
        }

        
    }//GEN-LAST:event_chkSelAllEntityActionPerformed

    private void tblSelectedObjectMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSelectedObjectMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tblSelectedObjectMouseEntered

    private void tblSelectedObjectMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSelectedObjectMouseClicked
        // SUPB - 20160405 ADD - Show number of Item checkbox is selected (to select entity to calculate similarity) clicked event - START
        int i;
        int selCount = 0;
        for (i = 0; i < tblSelectedObject.getRowCount(); i++) {
            if (Boolean.parseBoolean(tblSelectedObject.getValueAt(i, 0).toString()) == true) {
                selCount++;
            }
        }
        lblObjectInputStatus.setText(selCount + " selected/" + BasicData.ObjectID2NameMap.size() + " total");
        // SUPB - 20160405 ADD - Show number of Item checkbox is selected (to select entity to calculate similarity) clicked event - END
    }//GEN-LAST:event_tblSelectedObjectMouseClicked

    private void chkCommonAncestorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCommonAncestorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCommonAncestorActionPerformed

    private void btnVisualizeSubGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVisualizeSubGraphActionPerformed
        // TODO add your handling code here:
        //VisualizeSubOntologyGraphTask task= new VisualizeSubOntologyGraphTask(cyNetworkManager, cyNetworkFactory, cyNetworkNaming);
        selTermIDSet = new TreeSet<String>();
        for (int i = 0; i < this.tblSelectedTerm.getRowCount(); i++) {
            if (Boolean.parseBoolean(this.tblSelectedTerm.getValueAt(i, 0).toString()) == true) {
                String ID = this.tblSelectedTerm.getValueAt(i, 1).toString();
                selTermIDSet.add(ID);
            }
        }
        if (selTermIDSet.isEmpty()) {
            JOptionPane.showMessageDialog(this.getRootPane(), "Please select at least one term!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        VisualizeSubOntologyGraphTaskFactory visualizeSubOntologyGraphTaskFactory = new VisualizeSubOntologyGraphTaskFactory();
        this.taskManager.execute(visualizeSubOntologyGraphTaskFactory.createTaskIterator());

        JOptionPane.showMessageDialog(null, "Done");
    }//GEN-LAST:event_btnVisualizeSubGraphActionPerformed

    private void chkTermDescendantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTermDescendantActionPerformed
        // TODO add your handling code here:
        if (chkTermDescendant.isSelected()) {
            chkCommonDescendant.setEnabled(true);
        } else {
            chkCommonDescendant.setEnabled(false);
        }
    }//GEN-LAST:event_chkTermDescendantActionPerformed

    private void chkTermAncestorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTermAncestorActionPerformed
        // TODO add your handling code here:
        if (chkTermAncestor.isSelected()) {
            chkCommonAncestor.setEnabled(true);
            chkMICA.setEnabled(true);
            //chkCDA.setEnabled(true);
            chkLCA.setEnabled(true);
        } else {
            chkCommonAncestor.setEnabled(false);
            chkMICA.setEnabled(false);
            //chkCDA.setEnabled(false);
            chkLCA.setEnabled(false);
        }
    }//GEN-LAST:event_chkTermAncestorActionPerformed

    private void chkTermNameViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTermNameViewActionPerformed
        // SUPB - 20160405 ADD - Handle event checkbox Name View be clicked (to view by Name not ID) - START
        if (CalculateSemSimMatrixTask.SimMatrix == null || CalculateSemSimMatrixTask.SimMatrix.size() == 0) {
            return;
        }

        Map<String, Map<String, Double>> SimMatrixName = new LinkedHashMap<String, Map<String, Double>>();
        for (Map.Entry<String, Map<String, Double>> e : CalculateSemSimMatrixTask.SimMatrix.entrySet()) {
            String Name = BasicData.TermID2NameMap.get(e.getKey());
            Map<String, Double> map = new LinkedHashMap<String, Double>();
            for (Map.Entry<String, Double> e1 : e.getValue().entrySet()) {
                map.put(BasicData.TermID2NameMap.get(e1.getKey()), e1.getValue());
            }
            SimMatrixName.put(Name, map);
        }
        if (chkTermNameView.isSelected()) {
            fillSimTable(SimMatrixName, tblSemSimMatrix);
        } else {
            fillSimTable(CalculateSemSimMatrixTask.SimMatrix, tblSemSimMatrix);
        }
        // SUPB - 20160405 ADD - Handle event checkbox Name View be clicked (to view by Name not ID) - END
    }//GEN-LAST:event_chkTermNameViewActionPerformed

    private void btnExportSemSimMatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportSemSimMatActionPerformed
        // SUPB - 20160405 ADD - Implement export file function - START
        JFileChooser fc = new JFileChooser();
        //fc.setFileFilter(new FileTypeFilter());
        fc.setDialogTitle("Save Semantic Similarity Matrix");
        fc.setCurrentDirectory(new File("."));

        int returnVal = fc.showSaveDialog(this.getRootPane());
        //int returnVal=fc.showSaveDialog(this);
        String fileName = "";
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileName = fc.getSelectedFile().getAbsolutePath();
            //JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"You chose to open this file: " + fc.getSelectedFile().getName());
        } else {
            return;
        }
        try {
            PrintWriter output = new PrintWriter(new FileOutputStream(fileName), true); //auto flush
            int i, j;
            output.print("ID");
            for (i = 1; i < tblSemSimMatrix.getColumnCount(); i++) {
                output.print("\t" + tblSemSimMatrix.getColumnName(i));
            }
            output.println();
            for (i = 0; i < tblSemSimMatrix.getRowCount(); i++) {
                output.print(tblSemSimMatrix.getValueAt(i, 0).toString());
                for (j = 1; j < tblSemSimMatrix.getColumnCount(); j++) {
                    output.print("\t" + ((tblSemSimMatrix.getValueAt(i, j) != null) ? tblSemSimMatrix.getValueAt(i, j).toString() : " "));
                }
                output.println();
            }

            output.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.getRootPane(), "Error while exporting Semantic Similarity Matrix: " + e.toString());
            e.printStackTrace();
        }
        // SUPB - 20160405 ADD - Implement export file function - START
    }//GEN-LAST:event_btnExportSemSimMatActionPerformed

    private void tblSemSimMatrixMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSemSimMatrixMouseClicked
        // SUPB - 20160405 ADD - Handle event click in each element in similarity matrix (to view more information) - START
        int i, j;
        if (tblSemSimMatrix.getSelectedColumn() == 0) {
            return;
        }
        String Term1ID = (String) tblSemSimMatrix.getValueAt(tblSemSimMatrix.getSelectedRow(), 0);
        String Term2ID = tblSemSimMatrix.getColumnName(tblSemSimMatrix.getSelectedColumn());

        Set<String> AncTerm1 = Common.extractAncestorTerms(Term1ID, BasicData.ParentNodeMap);
        Set<String> AncTerm2 = Common.extractAncestorTerms(Term2ID, BasicData.ParentNodeMap);
        Set<String> CommonAncestors = new TreeSet<String>();
        CommonAncestors.addAll(AncTerm1);
        CommonAncestors.retainAll(AncTerm2);

        System.out.println(Term1ID + " && " + Term2ID + " have TermSim: " + tblSemSimMatrix.getValueAt(tblSemSimMatrix.getSelectedRow(), tblSemSimMatrix.getSelectedColumn()) + "\nIC1: " + BasicData.Term2ICMap.get(Term1ID) + "\nIC2: " + BasicData.Term2ICMap.get(Term2ID) + "\nCommonAncestors: " + CommonAncestors.toString());

        JOptionPane.showMessageDialog(this.getRootPane(), Term1ID + " && " + Term2ID + " have TermSim: " + tblSemSimMatrix.getValueAt(tblSemSimMatrix.getSelectedRow(), tblSemSimMatrix.getSelectedColumn()) + "\nIC1: " + BasicData.Term2ICMap.get(Term1ID) + "\nIC2: " + BasicData.Term2ICMap.get(Term2ID) + "\nCommonAncestors: " + CommonAncestors.toString(), "Information", JOptionPane.INFORMATION_MESSAGE);
        // SUPB - 20160405 ADD - Handle event click in each element in similarity matrix (to view more information) - END
    }//GEN-LAST:event_tblSemSimMatrixMouseClicked

    private void btnCalTermSimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalTermSimActionPerformed
        // SUPB - 20160405 UPDATE - btnCalTermSim clicked event (to calculate similarity between terms - START
            chkTermNameView.setSelected(false);

            int i, j;
            BasicData.validSelTermIDList = new ArrayList<String>();
            Set<String> InvalidTermIDSet = new TreeSet<String>();
            for (i = 0; i < tblSelectedTerm.getRowCount(); i++) {
                if (Boolean.parseBoolean(tblSelectedTerm.getValueAt(i, 0).toString()) == true) {
                    String ID = tblSelectedTerm.getValueAt(i, 1).toString();
                    if ((cboTermSimMet.getSelectedIndex() == 0 && !BasicData.Term2ICMap.get(ID).isInfinite()) || cboTermSimMet.getSelectedIndex() != 0) {
                        BasicData.validSelTermIDList.add(ID);
                    } else {
                        InvalidTermIDSet.add(ID);
                    }
                }
            }
            System.out.println("InvalidTermIDSet: " + InvalidTermIDSet.size() + "\t" + InvalidTermIDSet.toString());

            if (BasicData.validSelTermIDList.size() <= 1) {
                JOptionPane.showMessageDialog(this.getRootPane(), "Please select at least two terms (whose ICs are finite for Node-based methods)!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            CalculateSemSimMatrixTaskFactory calculateSemSimMatrixTaskFactory = new CalculateSemSimMatrixTaskFactory();
            this.taskManager.execute(calculateSemSimMatrixTaskFactory.createTaskIterator());
            JOptionPane.showMessageDialog(this.getRootPane(), "Done");
            // SUPB - 20160405 UPDATE - btnCalTermSim clicked event (to calculate similarity between terms - END
    }//GEN-LAST:event_btnCalTermSimActionPerformed

    private void cboTermSimMetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTermSimMetActionPerformed
        // TODO add your handling code here:
        String TermSimMed = (String) this.cboTermSimMet.getSelectedItem();
        this.cboTermSimSubMet.removeAllItems();
        System.out.println(this.TermSimSubMetSet.toString());
        for (String termsimsubmet : this.TermSimSubMetSet) {
            if (termsimsubmet.contains(TermSimMed)) {
                this.cboTermSimSubMet.addItem(termsimsubmet);
            }
        }
    }//GEN-LAST:event_cboTermSimMetActionPerformed

    private void chkSelAllTermActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSelAllTermActionPerformed
        // TODO add your handling code here:
        int i=0;
        if(chkSelAllTerm.isSelected()){
            for(i=0;i<tblSelectedTerm.getRowCount();i++){
                tblSelectedTerm.setValueAt(true, i, 0);
            }
            lblTermInputStatus.setText("All selected/" + BasicData.TermID2NameMap.size() + " total");
        }else{
            for(i=0;i<tblSelectedTerm.getRowCount();i++){
                tblSelectedTerm.setValueAt(false, i, 0);
            }
            lblTermInputStatus.setText("None selected/" + BasicData.TermID2NameMap.size() + " total");
        }

    }//GEN-LAST:event_chkSelAllTermActionPerformed

    private void tblSelectedTermMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSelectedTermMouseClicked
        // SUPB - 20160405 ADD - Show number of Item is selected (to caculate similarity) - START
        int i;
        int selCount = 0;
        for (i = 0; i < tblSelectedTerm.getRowCount(); i++) {
            if (Boolean.parseBoolean(tblSelectedTerm.getValueAt(i, 0).toString()) == true) {
                selCount++;
            }
        }
        lblTermInputStatus.setText(selCount + " selected/" + BasicData.TermID2NameMap.size() + " total");

        if (selCount == 2) {
            chkCDA.setEnabled(true);
        } else {
            chkCDA.setEnabled(false);
        }
        // SUPB - 20160405 ADD - Show number of Item is selected (to caculate similarity) - END
    }//GEN-LAST:event_tblSelectedTermMouseClicked

    private void btnLoadDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadDataActionPerformed
        // SUPB - 20160313 ADD - btnLoadData clicked event - START
        pnlEntityNet.setVisible(false);

        File ontoFile = new File(txtOntologyFile.getText());
        // check ontoFile
        if (!ontoFile.exists()) {
            // Error message and do nothing
            JOptionPane.showMessageDialog(this.getRootPane(), "Ontology file " + ontoFile.getAbsolutePath() + " does not exist!", "Notice", JOptionPane.ERROR_MESSAGE);
            return;
        }
        File annoFile = new File(txtAnnotationFile.getText());
        // check annoFile
        if (!annoFile.exists()) {
            // Error message and do nothing
            JOptionPane.showMessageDialog(this.getRootPane(), "Annotation file " + annoFile.getAbsolutePath() + " does not exist!", "Notice", JOptionPane.ERROR_MESSAGE);
            return;
        }
        LoadnPrepareDataTaskFactory useTaskMonitorTaskFactory = new LoadnPrepareDataTaskFactory();
        TaskObserver obs = new TaskObserver() {
            @Override
            public void taskFinished(ObservableTask task) {
                Vector<String> ColHeader = new Vector<String>();
                ColHeader.add("");
                Vector<Vector> Data = new Vector<Vector>();
                tblSemSimMatrix.setModel(new javax.swing.table.DefaultTableModel(new Vector<String>(), null) {
                });
                tblFunSimMatrix.setModel(new javax.swing.table.DefaultTableModel(new Vector<String>(), null) {
                });
                tblTermEnrichment.setModel(new javax.swing.table.DefaultTableModel(new Vector<String>(), null) {
                });

                // MinhDA - 20160404 ADD visual style - START
                // check to see if a visual style with this name already exists
                if (null == getVisualStyleByName("Ontology")) {
                    // if not, create it and add it to the catalog
                    vs = Common.createNetworkVisualStyle("Ontology", vsfManager, vmfFactoryC, vmfFactoryP, vmfFactoryD);
                    vmmManager.addVisualStyle(vs);
                } else {
                    vmmManager.removeVisualStyle(getVisualStyleByName("Ontology"));
                    vs = Common.createNetworkVisualStyle("Ontology", vsfManager, vmfFactoryC, vmfFactoryP, vmfFactoryD);
                    vmmManager.addVisualStyle(vs);
                }
                // MinhDA - 20160404 ADD visual style - END
            }

            @Override
            public void allFinished(FinishStatus finishStatus) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        this.taskManager.execute(useTaskMonitorTaskFactory.createTaskIterator(), obs);

//        LoadDataResourcesTaskFactory loadDataResourcesTaskFactory = new LoadDataResourcesTaskFactory();
//        this.cySynchronousTaskManager.execute(loadDataResourcesTaskFactory.createTaskIterator());
        //SUPB - 20160313 ADD - btnLoadData clicked event - END
    }//GEN-LAST:event_btnLoadDataActionPerformed

    private void tblEvidenceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblEvidenceMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblEvidenceMouseClicked

    private void cboAnnotationTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboAnnotationTypeActionPerformed
        // MinhDA - 20160313 - ADD - Annotation Type Combo Box clicked event - START
        /**
        * Let the user choose an annotation data file if the Ontology type is
        * not one of the defaults.
        */
        if (this.cboAnnotationType.getItemCount()>0 && this.cboAnnotationType.getSelectedItem().toString().compareTo("Select other Annotation data...")==0) {
            txtAnnotationFile.setEnabled(true);
            tblEvidence.setEnabled(true);
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Select an Annotation file");
            fc.setCurrentDirectory(new File("."));

            int returnVal = fc.showOpenDialog(this.getRootPane());
            String fileName = "";
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = fc.getSelectedFile().getAbsolutePath();
                txtAnnotationFile.setText(fileName);
            } else {
                return;
            }
        }else{
            txtAnnotationFile.setEnabled(false);
            tblEvidence.setEnabled(false);
        }

        //MinhDA - 20160313 - ADD - Annotation Type Combo Box clicked event - END
    }//GEN-LAST:event_cboAnnotationTypeActionPerformed

    private void cboOntologyTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboOntologyTypeActionPerformed
        //MinhDA - 20160313 MODIFY - Based on comments - START
        //MinhDA - 20160312 ADD - OntologyComboBox clicked event - START
        /**
        * Change the data of other fields depend on which ontology type is
        * selected Case 0,1,2: Gene Ontology Case 3: Human Phenotype Ontology
        * Case 4: Disease Ontology
        */
        switch (this.cboOntologyType.getSelectedIndex()) {
            case 0:
            case 1:
            case 2:
            //GO
            txtOntologyFile.setText("Data" + File.separator + "go.obo");
            this.cboAnnotationType.removeAllItems();
            this.cboAnnotationType.addItem("Gene Ontology (GO) To Gene");
            this.cboAnnotationType.addItem("Select other Annotation data...");
            this.cboAnnotationType.setSelectedIndex(0);
            /**
            * Change the Annotation text field depend on which ontology
            * type is selected Case 0: Biological process Case 1: Cellular
            * components Case 2: Molecular functions
            */
            if (0 == this.cboOntologyType.getSelectedIndex()) {
                txtAnnotationFile.setText("Data" + File.separator + "Annotation_Gene2GO_9606_BP.txt");
            } else if (1 == this.cboOntologyType.getSelectedIndex()) {
                txtAnnotationFile.setText("Data" + File.separator + "Annotation_Gene2GO_9606_CC.txt");
            } else {
                txtAnnotationFile.setText("Data" + File.separator + "Annotation_Gene2GO_9606_MF.txt");
            }
            break;
            case 3:
            //HPO
            txtOntologyFile.setText("Data" + File.separator + "hp.obo");
            this.cboAnnotationType.removeAllItems();
            this.cboAnnotationType.addItem("Human Phenotype Ontology (HPO) To Phenotype");
            this.cboAnnotationType.addItem("Select other Annotation data...");
            this.cboAnnotationType.setSelectedIndex(0);
            txtAnnotationFile.setText("Data" + File.separator + "Annotation_OMIM2HPO_OMIM.txt");
            break;
            case 4:
            //DO
            txtOntologyFile.setText("Data" + File.separator + "HumanDO.obo");
            this.cboAnnotationType.removeAllItems();
            this.cboAnnotationType.addItem("Disease Ontology (DO) To Gene");
            this.cboAnnotationType.addItem("Select other Annotation data...");
            this.cboAnnotationType.setSelectedIndex(0);
            txtAnnotationFile.setText("Data" + File.separator + "Annotation_Gene2DO.txt");
            break;
            default:
            /**
            * Open a dialog and let the user choose an Ontology data file
            * Change the annotation type to allow the user to choose an
            * Annotation data file
            */
            this.cboAnnotationType.removeAllItems();
            this.cboAnnotationType.addItem("Unavailable Annotation data");
            this.cboAnnotationType.addItem("Select other Annotation data...");
            JFileChooser fc = new JFileChooser();
            //fc.setFileFilter(new FileTypeFilter());
            fc.setFileFilter(new FileNameExtensionFilter("Ontology files", "obo"));
            fc.setDialogTitle("Select an Ontology (*.obo) file");
            fc.setCurrentDirectory(new File("."));
            int returnVal = fc.showOpenDialog(this.getRootPane());
            String fileName = "";
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = fc.getSelectedFile().getAbsolutePath();
                txtOntologyFile.setText(fileName);
            } else {
            }
            break;
        }
        //MinhDA - 20160312 ADD - OntologyComboBox clicked event - END
        //MinhDA - 20160313 MODIFY - Based on comments - END
    }//GEN-LAST:event_cboOntologyTypeActionPerformed

    private void chkObjectInteractionNameViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkObjectInteractionNameViewActionPerformed
        if (WeighEntityNetworkTask.WeightedInteractionList == null || WeighEntityNetworkTask.WeightedInteractionList.size() == 0) {
            return;
        }
        ArrayList<Interaction> WeightedInteractionNameList = new ArrayList<Interaction>();
        int i;
        for (i = 0; i < WeighEntityNetworkTask.WeightedInteractionList.size(); i++) {
            Interaction ina = WeighEntityNetworkTask.WeightedInteractionList.get(i);
            Interaction inaname = new Interaction();
            inaname.NodeSrc = BasicData.ObjectID2NameMap.get(ina.NodeSrc);
            inaname.NodeDst = BasicData.ObjectID2NameMap.get(ina.NodeDst);
            inaname.Weight = ina.Weight;
            inaname.Index = ina.Index;
            WeightedInteractionNameList.add(inaname);
        }
        if (chkObjectInteractionNameView.isSelected()) {
            fillWeightedInteractionTable(WeightedInteractionNameList, tblWeightedInteraction);
        } else {
            fillWeightedInteractionTable(WeighEntityNetworkTask.WeightedInteractionList, tblWeightedInteraction);
        }
    }//GEN-LAST:event_chkObjectInteractionNameViewActionPerformed

    private void tblWeightedInteractionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblWeightedInteractionMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblWeightedInteractionMouseClicked

    private void btnExportWeightedInteractionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportWeightedInteractionActionPerformed
        JFileChooser fc = new JFileChooser();
        //fc.setFileFilter(new FileTypeFilter());
        fc.setDialogTitle("Save Weighted Network");
        fc.setCurrentDirectory(new File("."));

        int returnVal = fc.showSaveDialog(this.getRootPane());
        //int returnVal=fc.showSaveDialog(this);
        String fileName = "";
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileName = fc.getSelectedFile().getAbsolutePath();
            //JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"You chose to open this file: " + fc.getSelectedFile().getName());
        } else {
            return;
        }
        try {
            PrintWriter output = new PrintWriter(new FileOutputStream(fileName), true); //auto flush
            int i, j;
            output.println("Object1 ID\tWeight\tObject2 ID\tNote");

            for (i = 0; i < tblWeightedInteraction.getRowCount(); i++) {
                output.print(((tblWeightedInteraction.getValueAt(i, 0) != null) ? tblWeightedInteraction.getValueAt(i, 0).toString() : ""));
                for (j = 1; j < tblWeightedInteraction.getColumnCount(); j++) {
                    output.print("\t" + ((tblWeightedInteraction.getValueAt(i, j) != null) ? tblWeightedInteraction.getValueAt(i, j).toString() : ""));
                }
                output.println();
            }

            output.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.getRootPane(), "Error while exporting Weighted Interactions: " + e.toString());
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnExportWeightedInteractionActionPerformed

    private void btnLoadNetworkListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadNetworkListActionPerformed
        cboNetwork.removeAllItems();

        Set<CyNetwork> NetworkSet = cyNetworkManager.getNetworkSet();
        for (CyNetwork net : NetworkSet) {
            String NetID = net.getRow(net).getRaw("name").toString();//getIdentifier();
            String NetTitle = net.getRow(net).getRaw("name").toString();//getTitle();
            String NetType = null;
            if (null != net.getRow(net).getRaw("Type")) {
                NetType = net.getRow(net).getRaw("Type").toString();
            }
            System.out.println(NetID + "\t" + NetTitle);

            if ((NetType != null && NetType.isEmpty()) || NetType == null) {
                //cboNetwork.addItem(NetTitle + " (" + NetID + ")");
                cboNetwork.addItem(NetID);
            }
        }
    }//GEN-LAST:event_btnLoadNetworkListActionPerformed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        chkObjectInteractionNameView.setSelected(false);

        if (cboNetwork.getItemCount() == 0) {
            Vector<String> ColHeader = new Vector<String>();
            ColHeader.add("Object1 ID");
            ColHeader.add("Weight");
            ColHeader.add("Object2 ID");
            ColHeader.add("Note");

            Vector<Vector> Data = new Vector<Vector>();
            tblWeightedInteraction.setModel(new javax.swing.table.DefaultTableModel(Data, ColHeader) {
            });

            JOptionPane.showMessageDialog(this.getRootPane(), "Please select a network! \n(Click Refress button to load Interaction Networks!)", "Notice", JOptionPane.ERROR_MESSAGE);

            return;

        }
        if (cboInteraction.getSelectedIndex() == 0) {
            //            CyNetwork selNet = Cytoscape.getNetwork(cboNetwork.getSelectedItem().toString());
            CyNetwork selNet = null;
            for (CyNetwork cyNetwork : cyNetworkManager.getNetworkSet()) {
                if (cyNetwork.getDefaultNetworkTable().getRow(cyNetwork.getSUID()).getRaw("name").toString().equals(cboNetwork.getSelectedItem().toString())) {
                    selNet = cyNetwork;
                }
            }
            //            arrCyEdge = new ArrayList<CyEdge>();
            arrCyEdge = CyTableUtil.getEdgesInState(selNet, "selected", true);
            //            for (CyRow cyRow : selNet.getDefaultEdgeTable().getAllRows()) {
                //                if (cyRow.getRaw("selected").toString().equals("true")) {
                    //                    arrCyEdge.add(selNet.getEdge((Long) cyRow.getRaw("SUID")));
                    //                }
                //            }
            System.out.println("arrCyEdge = " + arrCyEdge);
            if (null == arrCyEdge || arrCyEdge.size() == 0) {
                JOptionPane.showMessageDialog(this.getRootPane(), "Select at least one interaction!", "Notice", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        //Weight Interaction Network
        WeightInteractionNetworkTaskFactory weightInteractionNetworkTaskFactory = new WeightInteractionNetworkTaskFactory();
        this.taskManager.execute(weightInteractionNetworkTaskFactory.createTaskIterator());
        JOptionPane.showMessageDialog(this.getRootPane(), "Done!");
    }//GEN-LAST:event_btnOKActionPerformed

    private void cboNetworkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboNetworkActionPerformed
        if (cboNetwork.getItemCount() > 0) {
            String selNetID = cboNetwork.getSelectedItem().toString();
            System.out.println("selNetID: " + selNetID);
            CyNetwork selNet = null;
            for (CyNetwork cyNetwork : cyNetworkManager.getNetworkSet()) {
                if (cyNetwork.getDefaultNetworkTable().getAllRows().get(0).getRaw("name").toString().equals(selNetID)) {
                    selNet = cyNetwork;
                }
            }
            final Collection<CyNetworkView> views = cyNetworkViewManager.getNetworkViews(selNet);
            CyNetworkView selNetView = null;
            if (views.size() != 0) {
                selNetView = views.iterator().next();
            }
            if (selNetView == null) {
                // create a new view for my network
                selNetView = cyNetworkViewFactory.createNetworkView(selNet);
                cyNetworkViewManager.addNetworkView(selNetView);

            } else {
                System.out.println("networkView already existed.");
            }

            //Cytoscape.setCurrentNetworkView(selNetID);
            //            List<String> selNetIDList = new ArrayList<String>();
            //            selNetIDList.add(selNetID);
            //            Cytoscape.setSelectedNetworkViews(selNetIDList);
            //
        }
    }//GEN-LAST:event_cboNetworkActionPerformed

    private void jLabel20MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel20MouseClicked
        // TODO add your handling code here:
        int i;
        
        String EntityStr = MainPanel.txtAnnotationFile.getText();
        String[] EntityArr = EntityStr.split(" ");
        Set<String> EntitySet = new TreeSet<>();
        for(i=0;i<EntityArr.length;i++){
            EntitySet.add(EntityArr[i].trim());
        }

        int c=0;
        for(i=0;i<tblSelectedObject.getRowCount();i++){
            String EnID = tblSelectedObject.getValueAt(i, 1).toString();
            if(EntitySet.contains(EnID)){
                tblSelectedObject.setValueAt(true, i, 0);
                c++;
            }else{
                tblSelectedObject.setValueAt(false, i, 0);
            }
        }
        lblObjectInputStatus.setText(c + "/" + BasicData.ObjectID2NameMap.size() + " total");

    }//GEN-LAST:event_jLabel20MouseClicked

    private class LoadnPrepareDataTaskFactory extends AbstractTaskFactory {

        @Override
        public TaskIterator createTaskIterator() {
            return new TaskIterator(new LoadnPrepareDataTask());
        }
    }

    private class CalculateSemSimMatrixTaskFactory extends AbstractTaskFactory {

        @Override
        public TaskIterator createTaskIterator() {
            return new TaskIterator(new CalculateSemSimMatrixTask());
        }

    }

    private class VisualizeSubOntologyGraphTaskFactory extends AbstractTaskFactory {

        @Override
        public TaskIterator createTaskIterator() {
            return new TaskIterator(new VisualizeSubOntologyGraphTask(cyNetworkManager, cyNetworkFactory, cyNetworkNaming, cyNetworkViewFactory, cyNetworkViewManager, layoutManager, taskManager, vmmManager, vs, selTermIDSet));

        }
    }

    private class CalculateFunSimMatrixTaskFactory extends AbstractTaskFactory {

        @Override
        public TaskIterator createTaskIterator() {
            return new TaskIterator(new CalculateFunSimMatrixTask());
        }

    }

    private class EnrichmentAnalysisTaskFactory extends AbstractTaskFactory {

        @Override
        public TaskIterator createTaskIterator() {
            return new TaskIterator(new EnrichmentAnalysisTask());
        }

    }

    private class VisualizeObjectAnnotatingTermTaskFactory extends AbstractTaskFactory {

        @Override
        public TaskIterator createTaskIterator() {
            return new TaskIterator(new VisualizeObjectAnnotatingTermTask(taskManager, cyNetworkFactory, cyNetworkNaming, cyNetworkManager, layoutManager, cyNetworkViewManager, cyNetworkViewFactory, vs));
        }
    }

    private class WeightInteractionNetworkTaskFactory extends AbstractTaskFactory {

        @Override
        public TaskIterator createTaskIterator() {
            return new TaskIterator(new WeighEntityNetworkTask(cyNetworkManager, arrCyEdge));
        }

    }

    private class CalculateFunSimBetweenObjectSetTaskFactory extends AbstractTaskFactory {

        @Override
        public TaskIterator createTaskIterator() {
            return new TaskIterator(new CalculateFunSimBetweenObjectSetTask());
        }
    }

    public static void fillSimTable(Map<String, Map<String, Double>> SimMatrix, javax.swing.JTable TableName) {
        try {
            Vector<String> ColHeader = new Vector<String>();
            Vector<Vector> Data = new Vector<Vector>();

            ColHeader = new Vector<String>();
            Data = new Vector<Vector>();

            ColHeader.add(0, "");
            for (Map.Entry<String, Map<String, Double>> e : SimMatrix.entrySet()) {
                ColHeader.add(e.getKey());

                Vector<Object> Record = new Vector<Object>();
                Record.add(e.getKey());
                for (Map.Entry<String, Double> e1 : e.getValue().entrySet()) {
                    Record.add(e1.getValue());
                }
                Data.add(Record);
            }

            TableName.setModel(new javax.swing.table.DefaultTableModel(Data, ColHeader) {
            });

            TableName.setCellSelectionEnabled(false);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while filling Semantic/Functional similarity into table: " + e.toString());
        }
    }

    static void fillTermEnrichmentTable(Map<String, String> TermID2NameMap, Map<String, Double> TermID2PvalueMap, Map<String, Double> TermID2AdjustedPvalueMap, javax.swing.JTable TableName) {
        try {
            Vector<String> ColHeader = new Vector<String>();
            Vector<Vector> Data = new Vector<Vector>();

            ColHeader = new Vector<String>();
            ColHeader.add("Term ID");
            ColHeader.add("Name");
            ColHeader.add("P-value");
            ColHeader.add("Adjusted P-value");

            Data = new Vector<Vector>();

            Vector<Object> Record = new Vector<Object>();

            for (Map.Entry<String, Double> e : TermID2PvalueMap.entrySet()) {
                Record = new Vector<Object>();
                Record.add(e.getKey());
                if (TermID2NameMap.containsKey(e.getKey())) {
                    Record.add(TermID2NameMap.get(e.getKey()));
                } else {
                    Record.add("");
                }
                Record.add(e.getValue().toString());
                Record.add(TermID2AdjustedPvalueMap.get(e.getKey()).toString());

                Data.add(Record);

            }
            TableName.setModel(new javax.swing.table.DefaultTableModel(Data, ColHeader) {
                Class[] types = new Class[]{
                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class

                };

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int colIndex) {
                    return true;   //Disallow the editing of any cell
                }

            });

            TableRowSorter<TableModel> sorter = new TableRowSorter<>(TableName.getModel());;
            TableName.setRowSorter(sorter);
            List<RowSorter.SortKey> sortKeys = new ArrayList<>();
            int ColIdxToSort=3;
            sortKeys.add(new RowSorter.SortKey(ColIdxToSort, SortOrder.ASCENDING));
            sorter.setSortKeys(sortKeys);
            sorter.sort();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while filling Enrichment result into table data: " + e.toString());

        }
    }

    private VisualStyle getVisualStyleByName(String styleName) {
        VisualMappingManager vmm = this.vmmManager;
        Set<VisualStyle> styles = vmm.getAllVisualStyles();
        for (VisualStyle style : styles) {
            if (style.getTitle().equals(styleName)) {
                System.out.println("style found in VisualStyles: " + styleName + " == " + style.getTitle());
                return style;
            }
        }
        System.out.println("style [" + styleName + "] not in VisualStyles, default style used.");
        return null;
    }

    public static void fillTermInputTable(Map<String, String> TermID2NameMap, Map<String, Double> TermID2ICMap, Map<String, Set<String>> TermID2ObjectMap, javax.swing.JTable TableName) {
        try {
            Vector<String> ColHeader = new Vector<String>();
            Vector<Vector> Data = new Vector<Vector>();

            ColHeader = new Vector<String>();
            ColHeader.add("Select");
            ColHeader.add("ID");
            ColHeader.add("Name");
            ColHeader.add("Information Content");
            ColHeader.add("Annotated Entities");

            Data = new Vector<Vector>();

            Vector<Object> Record = new Vector<Object>();

            for (Map.Entry<String, String> e : TermID2NameMap.entrySet()) {
                Record = new Vector<Object>();
                Record.add(false);
                Record.add(e.getKey());
                Record.add(e.getValue());
                if (TermID2ICMap.containsKey(e.getKey())) {
                    Record.add(TermID2ICMap.get(e.getKey()));
                } else {
                    Record.add(Double.NaN);
                }
                if (TermID2ObjectMap.containsKey(e.getKey())) {
                    Record.add(TermID2ObjectMap.get(e.getKey()).toString().substring(1, TermID2ObjectMap.get(e.getKey()).toString().length() - 1));
                } else {
                    Record.add("");
                }
                Data.add(Record);

            }
            TableName.setModel(new javax.swing.table.DefaultTableModel(Data, ColHeader) {
                Class[] types = new Class[]{
                    java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class

                };

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int colIndex) {
                    return true;   //Disallow the editing of any cell
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while filling Terms into table data: " + e.toString());
        }
    }

    public static void fillWeightedInteractionTable(ArrayList<Interaction> WeightedInaList, javax.swing.JTable TableName) {
        try {
            Vector<String> ColHeader = new Vector<String>();
            Vector<Vector> Data = new Vector<Vector>();

            ColHeader = new Vector<String>();
            ColHeader.add("Entity #1 ID");
            ColHeader.add("Weight");
            ColHeader.add("Entity #2 ID");
            ColHeader.add("Note");

            Data = new Vector<Vector>();

            Vector<Object> Record = new Vector<Object>();
            int i;
            for (i = 0; i < WeightedInaList.size(); i++) {
                Interaction ina = WeightedInaList.get(i);
                Record = new Vector<Object>();
                Record.add(ina.NodeSrc);
                Record.add(ina.Weight);
                Record.add(ina.NodeDst);
                Record.add(ina.Index);
                Data.add(Record);
            }
            TableName.setModel(new javax.swing.table.DefaultTableModel(Data, ColHeader) {
                Class[] types = new Class[]{
                    java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class
                };

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int colIndex) {
                    return true;   //Disallow the editing of any cell
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while filling Weighted Interactions into table data: " + e.toString());
        }
    }

    public static void fillObjectInputTable(Map<String, String> ObjectID2NameMap, Map<String, Set<String>> ObjectID2TermMap, javax.swing.JTable TableName) {
        try {
            Vector<String> ColHeader = new Vector<String>();
            Vector<Vector> Data = new Vector<Vector>();

            ColHeader = new Vector<String>();
            ColHeader.add("Select");
            ColHeader.add("ID");
            ColHeader.add("Name");
            ColHeader.add("Annotating Terms");

            Data = new Vector<Vector>();

            Vector<Object> Record = new Vector<Object>();

            for (Map.Entry<String, String> e : ObjectID2NameMap.entrySet()) {
                Record = new Vector<Object>();
                Record.add(false);
                Record.add(e.getKey());
                Record.add(e.getValue());

                if (ObjectID2TermMap.containsKey(e.getKey())) {
                    //System.out.println(ObjectID2TermMap.get(e.getKey()).toString());
                    Record.add(ObjectID2TermMap.get(e.getKey()).toString().substring(1, ObjectID2TermMap.get(e.getKey()).toString().length() - 1));
                } else {
                    Record.add("");
                }
                Data.add(Record);

            }
            TableName.setModel(new javax.swing.table.DefaultTableModel(Data, ColHeader) {
                Class[] types = new Class[]{
                    java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class

                };

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int colIndex) {
                    return true;   //Disallow the editing of any cell
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while filling Entities into table data: " + e.toString());
        }
    }

    public static void fillObjectSetTable(Map<String, String> ObjectID2NameMap, Map<String, Set<String>> ObjectID2TermMap, javax.swing.JTable TableName) {
        try {
            Vector<String> ColHeader = new Vector<String>();
            Vector<Vector> Data = new Vector<Vector>();

            ColHeader = new Vector<String>();
            ColHeader.add("Select");
            ColHeader.add("ID");
            ColHeader.add("Name");

            Data = new Vector<Vector>();

            Vector<Object> Record = new Vector<Object>();

            for (Map.Entry<String, String> e : ObjectID2NameMap.entrySet()) {
                Record = new Vector<Object>();
                Record.add(false);
                Record.add(e.getKey());
                Record.add(e.getValue());

                Data.add(Record);

            }
            TableName.setModel(new javax.swing.table.DefaultTableModel(Data, ColHeader) {
                Class[] types = new Class[]{
                    java.lang.Boolean.class, java.lang.String.class, java.lang.String.class

                };

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int colIndex) {
                    return true;   //Disallow the editing of any cell
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while filling Entities into table data: " + e.toString());

        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup AdjustPvalueMethodGroup;
    private javax.swing.ButtonGroup StatisticalTestGroup;
    private javax.swing.JButton btnCalObjSim;
    private javax.swing.JButton btnCalTermSim;
    private javax.swing.JButton btnEnrichmentAnalysis;
    private javax.swing.JButton btnExportFunSimMat;
    private javax.swing.JButton btnExportSemSimMat;
    private javax.swing.JButton btnExportWeightedInteraction;
    public static javax.swing.JButton btnLoadData;
    private javax.swing.JButton btnLoadNetworkList;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnObjectSetFunctionalSimiliarity;
    private javax.swing.JButton btnVisualizeAnnotatingGraph;
    private javax.swing.JButton btnVisualizeSubGraph;
    public static javax.swing.JComboBox cboAnnotationType;
    public static javax.swing.JComboBox cboInteraction;
    public static javax.swing.JComboBox cboNetwork;
    public static javax.swing.JComboBox cboObjSimMet;
    public static javax.swing.JComboBox cboObjSimSubMet;
    public static javax.swing.JComboBox cboOntologyType;
    public static javax.swing.JComboBox cboTermSimMet;
    public static javax.swing.JComboBox cboTermSimSubMet;
    public static javax.swing.JCheckBox chkAnnotatedObject;
    public static javax.swing.JCheckBox chkCDA;
    public static javax.swing.JCheckBox chkCommonAncestor;
    public static javax.swing.JCheckBox chkCommonDescendant;
    public static javax.swing.JCheckBox chkDirectAncestorTerm;
    public static javax.swing.JCheckBox chkDirectDescendantTerm;
    public static javax.swing.JCheckBox chkDirectTerm;
    public static javax.swing.JCheckBox chkFunSimInteraction;
    public static javax.swing.JCheckBox chkLCA;
    public static javax.swing.JCheckBox chkMICA;
    private javax.swing.JCheckBox chkObjectInteractionNameView;
    private javax.swing.JCheckBox chkObjectNameView;
    private javax.swing.JCheckBox chkSelAllEntity;
    private javax.swing.JCheckBox chkSelAllTerm;
    public static javax.swing.JCheckBox chkTermAncestor;
    public static javax.swing.JCheckBox chkTermDescendant;
    private javax.swing.JCheckBox chkTermNameView;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblEntitySetSim;
    public static javax.swing.JLabel lblObjectInputStatus;
    public static javax.swing.JLabel lblTermInputStatus;
    public static javax.swing.JRadioButton optBenjaminiHochberg;
    public static javax.swing.JRadioButton optBonferroni;
    public static javax.swing.JRadioButton optFishersExact;
    public static javax.swing.JRadioButton optHypergeometric;
    private javax.swing.JPanel pnlEntityNet;
    public static javax.swing.JPanel pnlEntitySets;
    private javax.swing.JPanel pnlObjSim;
    private javax.swing.JPanel pnlObjSim1;
    private javax.swing.JPanel pnlOntSim;
    private javax.swing.JPanel pnlOntSim1;
    public static javax.swing.JTabbedPane tPnlEntity;
    private javax.swing.JTabbedPane tPnlSimVizEA;
    public static javax.swing.JTabbedPane tPnlTerm;
    public static javax.swing.JTable tblEvidence;
    public static javax.swing.JTable tblFunSimMatrix;
    public static javax.swing.JTable tblObjectSet1;
    public static javax.swing.JTable tblObjectSet2;
    public static javax.swing.JTable tblSelectedObject;
    public static javax.swing.JTable tblSelectedTerm;
    public static javax.swing.JTable tblSemSimMatrix;
    public static javax.swing.JTable tblTermEnrichment;
    public static javax.swing.JTable tblWeightedInteraction;
    public static javax.swing.JTextField txtAnnotationFile;
    public static javax.swing.JTextField txtMaxFunSim;
    public static javax.swing.JTextField txtMinFunSim;
    public static javax.swing.JTextField txtOntologyFile;
    // End of variables declaration//GEN-END:variables
}
