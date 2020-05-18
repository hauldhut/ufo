
package org.cytoscape.UFO.internal;

import java.awt.event.ActionEvent;
import static org.cytoscape.UFO.internal.MainFunctionMenuAction.context;
import static org.cytoscape.UFO.internal.MainFunctionMenuAction.cyActivator;
import static org.cytoscape.UFO.internal.MainFunctionMenuAction.cytoPanelWest;
import static org.cytoscape.UFO.internal.MainFunctionMenuAction.mainPanel;
import static org.cytoscape.UFO.internal.MainFunctionMenuAction.isOpen;
import org.cytoscape.application.swing.AbstractCyAction;


public class ExitAppMenuAction extends AbstractCyAction {
    public ExitAppMenuAction() {
        super("Exit App");
    }
    @Override
    public void actionPerformed(ActionEvent ae){
        int index = cytoPanelWest.indexOfComponent(mainPanel);
        if (-1 != index) {
            cyActivator.stop(context);
            cyActivator.shutDown();
            isOpen = false;
        }
    }

}
