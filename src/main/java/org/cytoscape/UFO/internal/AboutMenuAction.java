
package org.cytoscape.UFO.internal;


import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.AbstractCyAction;



public class AboutMenuAction extends AbstractCyAction {
    public AboutMenuAction() {
        super("About...");
    }
    public void actionPerformed(ActionEvent ae){
        
        //AboutDialog dlg = new AboutDialog(Cytoscape.getDesktop(), true);
        AboutDialog dlg = new AboutDialog(null, true);
        
        dlg.setLocationRelativeTo(null); //should center on screen
        
        dlg.setVisible(true);
    }

}
