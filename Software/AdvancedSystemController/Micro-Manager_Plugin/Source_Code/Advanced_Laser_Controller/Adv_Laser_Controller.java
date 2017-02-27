/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Advanced_Laser_Controller;

import org.micromanager.MenuPlugin;
import org.micromanager.Studio;
import org.micromanager.internal.MMStudio;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;

/**
 *
 * @author James_Walsh
 */
@Plugin(type = MenuPlugin.class)
public class Adv_Laser_Controller implements MenuPlugin, SciJavaPlugin {
    private arduino_gui dialog_;
    MMStudio si_in;


    @Override
    public void onPluginSelected() {
        

    //JFrame frame = new JFrame("InputDialog Example #1");

       // int firstcomport = Integer.parseInt(
       //         JOptionPane.showInputDialog(frame, "First Com port Number:","5"));
   
       // int numofcomports = Integer.parseInt(
        //        JOptionPane.showInputDialog(frame, "Number of com ports to search:","8"));
        
   
        dialog_ = new arduino_gui(si_in);
        
        

        
    }

    @Override
    public String getVersion() {
      //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    return "Version 1.1";
    }

    @Override
    public String getCopyright() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    return "no copyright";
    }

    @Override
    public String getSubMenu() {
      //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    return "";
    }


    @Override
    public void setContext(Studio studio) {
        si_in=(MMStudio) studio; 
    }

    @Override
    public String getName() {
       return "NicoLase";
    }

    @Override
    public String getHelpText() {
      return "See James Walsh (james.walsh@phys.unsw.edu.au)";//  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

