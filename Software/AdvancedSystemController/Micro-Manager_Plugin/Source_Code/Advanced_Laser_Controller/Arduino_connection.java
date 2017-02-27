/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Advanced_Laser_Controller;

/*import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.OutputStream;
import java.util.Enumeration;*/
import javax.swing.JOptionPane;
import org.micromanager.internal.MMStudio;

/**
 *
 * @author James_Walsh
 */
class Arduino_connection {
   //  SerialPort serialPort;
   // OutputStream output;
    MMStudio si_in;
    String arduinoname;
    
    public Arduino_connection(String comportstr,MMStudio si){
        si_in=si;
        arduinoname=comportstr;
        try{
           
          //  for(int i=0;i<devicesfound.size();i++) try{if(comportstr.equals(si_in.core().getProperty(devicesfound.get(i), "ShowPort")))arduinoname=devicesfound.get(i);}catch(Exception e){};
            si_in.core().setSerialPortCommand(comportstr, "R", "\n");
            //JOptionPane.showMessageDialog(null, si_in.core().getSerialPortAnswer(comportstr,"\n"));
        }catch (Exception e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(null, "Can't open arduino port");
        }

    }
    
    void Update_arduino(loopclass[] loops) {
         try{
            //output.write("R\n".getBytes());
            // si_in.core().setSerialPortCommand(arduinoname, "R", "\n");
            Thread.sleep(200);
            si_in.core().setSerialPortCommand(arduinoname, "M1", "\n");
            Thread.sleep(200);
            String stringout="N";
            stringout+=Integer.toString(loops.length);
            for(int i = 0;i<loops.length;i++){
                stringout+=" "+Integer.toString(loops[i].numofsteps);
                stringout+=" "+Integer.toString(loops[i].numofrepeats);
                stringout+=" "+Integer.toString(loops[i].runtime);
            }
            si_in.core().setSerialPortCommand(arduinoname, stringout, "\n");
            System.out.println(stringout);
            Thread.sleep(100);
            
            
            for(int i=0;i<loops.length;i++){
                for(int j=0;j<loops[i].laserseqs.length;j++){
                    stringout="A"+Integer.toString(i+1);
                    stringout+=" "+Integer.toString(loops[i].laserseqs[j]);
                    stringout+=" "+Integer.toString(loops[i].pauses[j]);
                    si_in.core().setSerialPortCommand(arduinoname, stringout, "\n");
                    System.out.println(stringout);
                    Thread.sleep(100);
                }    
            }
         } catch (Exception e) {
             JOptionPane.showMessageDialog(null, "Can't write arduino");
            System.out.println("Can't write arduino");
            //System.exit(1);
        }
    }
    
    
       void Start_arduino(){
       try{
       si_in.core().setSerialPortCommand(arduinoname, "S", "\n");
       } catch (Exception e) {
             JOptionPane.showMessageDialog(null, "Can't start arduino");
            System.out.println("Can't start arduino");
            //System.exit(1);
        }
   }
   
   void Reset_arduino(){
       try{
      // si_in.core().setSerialPortCommand(arduinoname, "r", "\n");
       } catch (Exception e) {
             JOptionPane.showMessageDialog(null, "Can't reset arduino");
            System.out.println("Can't reset arduino");
            //System.exit(1);
        }
   }
        

}
