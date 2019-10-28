
package org.cytoscape.UFO.internal;

import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.TaskManager;


/**
 * A sample plugin to show how to add a tabbed Panel to Cytoscape
 * Control panel. Deploy this plugin (tutorial01.jar) to the plugins
 * directory. A new tabbed panel "MyPanel" will appear at the
 * control panel of Cytoscape.
 */
public class WeighEntityNetworkMenuAction extends AbstractCyAction {
    public TaskManager taskManagerServiceRef;
    public CyNetworkManager cyNetworkManagerServiceRef;
    public WeighEntityNetworkMenuAction(TaskManager taskManagerServiceRef, CyNetworkManager cyNetworkManagerServiceRef) {
        super("Weigh Entity Network...");
        this.taskManagerServiceRef = taskManagerServiceRef;
        this.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
    }
    public void actionPerformed(ActionEvent ae){
        WeighEntityNetworkDialog dlg = new WeighEntityNetworkDialog(null, true, taskManagerServiceRef, cyNetworkManagerServiceRef);//Cytoscape.getDesktop(), true);

        dlg.setLocationRelativeTo(null); //should center on screen

        dlg.setVisible(true);
    }

}
