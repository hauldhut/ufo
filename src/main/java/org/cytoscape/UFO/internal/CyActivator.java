package org.cytoscape.UFO.internal;

import java.util.Properties;

import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
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

public class CyActivator extends AbstractCyActivator {
        public TaskManager taskManagerServiceRef;
        public CyActivator(){
            super();
        }
        
        @Override
	public void start(BundleContext bc) {
		CySwingApplication cytoscapeDesktopService = getService(bc,CySwingApplication.class);
                
		taskManagerServiceRef  = getService(bc, TaskManager.class);
                VisualMappingManager vmmServiceRef = getService(bc,VisualMappingManager.class);
                VisualStyleFactory visualStyleFactoryServiceRef = getService(bc,VisualStyleFactory.class);
		
		VisualMappingFunctionFactory vmfFactoryC = getService(bc,VisualMappingFunctionFactory.class, "(mapping.type=continuous)");
		VisualMappingFunctionFactory vmfFactoryP = getService(bc,VisualMappingFunctionFactory.class, "(mapping.type=passthrough)");
                VisualMappingFunctionFactory vmfFactoryD = getService(bc,VisualMappingFunctionFactory.class, "(mapping.type=discrete)");
                
                CyNetworkManager cyNetworkManagerServiceRef = getService(bc,CyNetworkManager.class);
		CyNetworkNaming cyNetworkNamingServiceRef = getService(bc,CyNetworkNaming.class);
		CyNetworkFactory cyNetworkFactoryServiceRef = getService(bc,CyNetworkFactory.class);
                
                CyNetworkViewFactory cyNetworkViewFactoryServiceRef = getService(bc,CyNetworkViewFactory.class);
		CyNetworkViewManager cyNetworkViewManagerServiceRef = getService(bc,CyNetworkViewManager.class);
		CyLayoutAlgorithmManager layoutManager = getService(bc, CyLayoutAlgorithmManager.class);
		
//                MainPanel myControlPanel = new MainPanel(taskManagerServiceRef, vmmServiceRef, visualStyleFactoryServiceRef, vmfFactoryC, vmfFactoryP, vmfFactoryD, cyNetworkFactoryServiceRef, cyNetworkManagerServiceRef, cyNetworkNamingServiceRef, 
//                        cyNetworkViewFactoryServiceRef, cyNetworkViewManagerServiceRef, layoutManager);
		MainFunctionMenuAction mainfuncAction = new MainFunctionMenuAction(cytoscapeDesktopService, taskManagerServiceRef, vmmServiceRef, visualStyleFactoryServiceRef, vmfFactoryC, vmfFactoryP, vmfFactoryD, cyNetworkFactoryServiceRef, cyNetworkManagerServiceRef, cyNetworkNamingServiceRef, 
                    cyNetworkViewFactoryServiceRef, cyNetworkViewManagerServiceRef, layoutManager, bc);
                
		ExtractAnnotationAction extractAnnotationAction = new ExtractAnnotationAction();
                WeighEntityNetworkMenuAction weighEntityNetAction = new WeighEntityNetworkMenuAction(taskManagerServiceRef, cyNetworkManagerServiceRef);
                HelpMenuAction helpAction = new HelpMenuAction();
                AboutMenuAction aboutAction = new AboutMenuAction();
                ExitAppMenuAction exitAppAction = new ExitAppMenuAction();
                        
                mainfuncAction.setPreferredMenu("Apps.UFO");
                extractAnnotationAction.setPreferredMenu("Apps.UFO.Utilities");
                weighEntityNetAction.setPreferredMenu("Apps.UFO.Utilities");
                helpAction.setPreferredMenu("Apps.UFO");
                aboutAction.setPreferredMenu("Apps.UFO");
                exitAppAction.setPreferredMenu("Apps.UFO");
                
		//registerService(bc,myControlPanel,CytoPanelComponent.class, new Properties());
		registerService(bc,mainfuncAction,CyAction.class, new Properties());
                registerService(bc,extractAnnotationAction,CyAction.class, new Properties());
                registerService(bc,weighEntityNetAction,CyAction.class, new Properties());
                registerService(bc,helpAction,CyAction.class, new Properties());
                registerService(bc,aboutAction,CyAction.class, new Properties());
                registerService(bc,exitAppAction,CyAction.class, new Properties());
	}

}
