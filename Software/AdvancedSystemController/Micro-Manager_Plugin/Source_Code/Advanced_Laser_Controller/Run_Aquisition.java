/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Advanced_Laser_Controller;

import java.awt.Frame;
import javax.swing.JOptionPane;
import mmcorej.DeviceType;
import mmcorej.StrVector;
import org.micromanager.acquisition.SequenceSettings;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.display.DisplayWindow;
import org.micromanager.internal.MMStudio;

/**
 *
 * @author James_Walsh
 */
class Run_Aquisition implements Runnable {

    MMStudio si;
    SequenceSettings aqset;
    Arduino_connection arduino;
    loopclass[] loops;
    String[] triggerinfo;
    
    public Run_Aquisition(MMStudio si_in, SequenceSettings aqsettings, loopclass[] loopsin, Arduino_connection arduinoin,String[]triggerinfoin) {
        si=si_in;
        aqset=aqsettings;
        loops = loopsin;
        arduino=arduinoin;
        triggerinfo = triggerinfoin;
    }
    
    @Override
    public void run(){
        int errorin2=0;
                                try{
                                   StrVector cameras = si.getCMMCore().getLoadedDevicesOfType(DeviceType.CameraDevice);
                                   //for(int i=0;i<cameras.size();i++)if(cameras.get(i).equals("Multi Camera")==false)si.getCMMCore().setProperty(cameras.get(i), "Trigger", "External");
                                    for(int i=0;i<cameras.size();i++)if(cameras.get(i).equals("Multi Camera")==false)si.getCMMCore().setProperty(cameras.get(i), triggerinfo[0], triggerinfo[2]);
                                           
   
                                    
                                    
                                    errorin2=1;
                                    //JOptionPane.showMessageDialog(null,"in new thread");
                                 Datastore imagestack;
                                 
                                 imagestack=si.getAcquisitionManager().runAcquisitionWithSettings(aqset,false);
                                 Thread.sleep(2000);
                                 arduino.Start_arduino();
                                 //JOptionPane.showMessageDialog(null,"run aquisition");
                                 errorin2=2;
                                 while(si.getAcquisitionManager().isAcquisitionRunning()){}
                                 errorin2=3;
                                 Thread.sleep(100);
                                 arduino.Reset_arduino();
                                 //si.getCMMCore().setProperty(cameras.get(0), "Trigger", "Software");
                                 si.getCMMCore().setProperty(cameras.get(0), triggerinfo[0], triggerinfo[1]);
                                 Datastore imagestackout = si.data().createRAMDatastore();
                                 org.micromanager.data.Image image;
                                 Coords.CoordsBuilder builder = si.data().getCoordsBuilder();
                                 Coords maxcoords=imagestack.getMaxIndices();
                                 
                                // JOptionPane.showMessageDialog(null,"imagetokeep[0] : "+image2keep[0]+" [1] : "+image2keep[1]);
                                 
                                 errorin2=4;     
                                 Coords coords;
  
                                int channelnum=0,channelnumin=0,framenum=0;
                                
                                for(int i=0;i<loops.length;i++)for(int k=0;k<loops[i].numofrepeats;k++){channelnumin=channelnum;
                                    for(int j=0;j<loops[i].numofsteps;j++){
                                        
                                    if(loops[i].camera1[j]){
                                            builder = builder.time(framenum).channel(0).stagePosition(0).z(0);
                                            coords= builder.build();
                                           // JOptionPane.showMessageDialog(null,"pos in t:"+coords.getTime()+" channel : "+coords.getChannel());
                                            image = imagestack.getImage(coords);
                                            builder=builder.time(k).channel(channelnumin);
                                            coords= builder.build();
                                           // JOptionPane.showMessageDialog(null,"pos in t:"+coords.getTime()+" channel : "+coords.getChannel());
                                            image=image.copyAtCoords(coords);
                                            imagestackout.putImage(image);
                                        
                                        channelnumin++;
                                    }
                                    
                                    if(loops[i].camera2[j]){
                                            builder = builder.time(framenum).channel(1).stagePosition(0).z(0);
                                            coords= builder.build();
                                           // JOptionPane.showMessageDialog(null,"pos in t:"+coords.getTime()+" channel : "+coords.getChannel());
                                            image = imagestack.getImage(coords);
                                            builder=builder.time(k).channel(channelnumin);
                                            coords= builder.build();
                                           // JOptionPane.showMessageDialog(null,"pos in t:"+coords.getTime()+" channel : "+coords.getChannel());
                                            image=image.copyAtCoords(coords);
                                            imagestackout.putImage(image);
                                        
                                        channelnumin++;
                                    }
                                        
                                        
                                    if(k==loops[i].numofrepeats-1&&j==loops[i].numofsteps-1)channelnum=channelnumin;
                                    framenum++;
                                }}
                                 imagestack.close();
                                    errorin2=5;
                                 
                                 
                                 
                                 
                                 
                                 
                               /*  errorin2=4;     
                                 Coords coords;
                                    int maxtime = maxcoords.getTime();
                                    for (int i=0;i<=maxtime;i++)for(int chan=0;chan<2;chan++){
                                        if(image2keep[(i%numsteps)*2+chan]>=0){
                                            builder = builder.time(i).channel(chan).stagePosition(0).z(0);
                                            coords= builder.build();
                                           // JOptionPane.showMessageDialog(null,"pos in t:"+coords.getTime()+" channel : "+coords.getChannel());
                                            image = imagestack.getImage(coords);
                                            builder=builder.time((int) i/numsteps).channel(image2keep[(i%numsteps)*2+chan]);
                                            coords= builder.build();
                                           // JOptionPane.showMessageDialog(null,"pos in t:"+coords.getTime()+" channel : "+coords.getChannel());
                                            image=image.copyAtCoords(coords);
                                            imagestackout.putImage(image);
                                            }
                                    }
                                    imagestack.close();
                                    errorin2=5;*/
                                    
                                    Frame frame=new Frame();
                                    java.awt.Window window=new java.awt.Window(frame);
                                    window.setVisible(true);
                                    imagestackout.save(window);
                                    
                                    DisplayWindow imagedisplay=si.displays().createDisplay(imagestackout);
                                    while(imagedisplay.getIsClosed()==false){};
                                    imagestackout.close();
                                    
                                    

                             } catch (Exception e) {
                             System.out.println(e);
                             JOptionPane.showMessageDialog(null,"Error in Image Capture : "+Integer.toString(errorin2));
                             //System.exit(1);
                             }      
         
     }   
}
