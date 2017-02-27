/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Advanced_Laser_Controller;

/**
 *
 * @author James_Walsh
 */
class loopclass {
    
    int numofrepeats;
    int numofsteps;
    int runtime;
    int laserseqs[];
    int pauses[];
    boolean camera1 [];
    boolean camera2 [];
    
    public loopclass(){
        numofrepeats=100;
        numofsteps=1;
        runtime=100;
        laserseqs=new int[1];
        laserseqs[0]=111111;
        pauses=new int[1];
        pauses[0]=100;
        camera1= new boolean[1];
        camera1[0]=true;
        camera2=new boolean[1];
        camera2[0]=true;
    }
}
