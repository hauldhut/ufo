
package org.cytoscape.UFO.internal;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.cytoscape.application.swing.AbstractCyAction;



public class HelpMenuAction extends AbstractCyAction {
    public HelpMenuAction() {
        super("Help...");
    }
    public void actionPerformed(ActionEvent ae){
        Desktop desktop = Desktop.getDesktop();
        try{
            desktop.browse(new URI("http://sites.google.com/site/duchaule2011/bioinformatics-tools/ufo"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

}
