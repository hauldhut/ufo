
package org.cytoscape.UFO.internal;

import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.AbstractCyAction;


public class ExtractAnnotationAction extends AbstractCyAction {
    public ExtractAnnotationAction() {
        super("Extract Annotation Data...");
    }
    public void actionPerformed(ActionEvent ae){
        ExtractAnnotationDialog dlg = new ExtractAnnotationDialog(null, true);

        dlg.setLocationRelativeTo(null); //should center on screen

        dlg.setVisible(true);

    }

}
