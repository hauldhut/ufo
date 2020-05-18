package org.cytoscape.UFO.internal;

import java.awt.event.ActionEvent;
import java.util.Properties;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;


/**
 * Creates a new menu item under Apps menu section.
 *
 */
public class MainFunctionMenuAction extends AbstractCyAction {

    private final CySwingApplication desktopApp;
    public static CytoPanel cytoPanelWest;
    public static MainPanel mainPanel;
    public static CyActivator cyActivator;
    public static BundleContext context;
    public static boolean isOpen = false;
    private final TaskManager taskManagerServiceRef;
    private final VisualMappingManager vmmServiceRef;
    private final VisualStyleFactory visualStyleFactoryServiceRef;
    private final VisualMappingFunctionFactory vmfFactoryC;
    private final VisualMappingFunctionFactory vmfFactoryP;
    private final VisualMappingFunctionFactory vmfFactoryD;
    private final CyNetworkFactory cyNetworkFactoryServiceRef;
    private final CyNetworkManager cyNetworkManagerServiceRef;
    private final CyNetworkNaming cyNetworkNamingServiceRef;
    private final CyNetworkViewFactory cyNetworkViewFactory;
    private final CyNetworkViewManager cyNetworkViewManager;
    private final CyLayoutAlgorithmManager layoutManager;
    
    
    public MainFunctionMenuAction(CySwingApplication desktopApp, TaskManager taskManagerServiceRef, VisualMappingManager vmmServiceRef, VisualStyleFactory visualStyleFactoryServiceRef, VisualMappingFunctionFactory vmfFactoryC, VisualMappingFunctionFactory vmfFactoryP, VisualMappingFunctionFactory vmfFactoryD, CyNetworkFactory cyNetworkFactoryServiceRef, CyNetworkManager cyNetworkManagerServiceRef, CyNetworkNaming cyNetworkNamingServiceRef,
            CyNetworkViewFactory cyNetworkViewFactory, CyNetworkViewManager cyNetworkViewManager, CyLayoutAlgorithmManager layoutManager, BundleContext context){
        super("Main Functions...");
        
        this.desktopApp = desktopApp;
        
        this.cytoPanelWest = this.desktopApp.getCytoPanel(CytoPanelName.WEST);
        
        this.taskManagerServiceRef = taskManagerServiceRef;
        this.vmmServiceRef = vmmServiceRef;
        this.visualStyleFactoryServiceRef = visualStyleFactoryServiceRef;
        this.vmfFactoryC = vmfFactoryC;
        this.vmfFactoryP = vmfFactoryP;
        this.vmfFactoryD = vmfFactoryD;
        this.cyNetworkFactoryServiceRef = cyNetworkFactoryServiceRef;
        this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
        this.cyNetworkNamingServiceRef = cyNetworkNamingServiceRef;
        this.cyNetworkViewFactory = cyNetworkViewFactory;
        this.cyNetworkViewManager = cyNetworkViewManager;
        this.layoutManager = layoutManager;
        MainFunctionMenuAction.context =  context;
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if (cytoPanelWest.getState() == CytoPanelState.HIDE){
            cytoPanelWest.setState(CytoPanelState.DOCK);
        }
        if (false == isOpen) {
            mainPanel = new MainPanel(taskManagerServiceRef, vmmServiceRef, visualStyleFactoryServiceRef, vmfFactoryC, vmfFactoryP, vmfFactoryD, 
                    cyNetworkFactoryServiceRef, cyNetworkManagerServiceRef, cyNetworkNamingServiceRef, cyNetworkViewFactory, cyNetworkViewManager, layoutManager);
            cyActivator = new CyActivator();
            cyActivator.start(context);
        }
        int index = cytoPanelWest.indexOfComponent(mainPanel);
        if (index == -1){
            return;
        }
        cytoPanelWest.setSelectedIndex(index);
    }
    
    public class CyActivator extends AbstractCyActivator {

        @Override
        public void start(BundleContext context) {
            isOpen = true;
            registerService(context, mainPanel, CytoPanelComponent.class, new Properties());

        }

    }
}
