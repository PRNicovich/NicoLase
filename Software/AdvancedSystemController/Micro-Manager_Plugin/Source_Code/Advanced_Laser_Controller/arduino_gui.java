/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Advanced_Laser_Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import mmcorej.StrVector;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.JOptionPane;
import mmcorej.DeviceType;
import org.micromanager.acquisition.SequenceSettings;
import org.micromanager.internal.MMStudio;


/**
 *
 * @author James_Walsh
 */
public class arduino_gui extends javax.swing.JFrame {

    /**
     * Creates new form arduino_gui
     */
    

    int numoflasers;
    String lasernames[];
    String arduino_com_num;
    Arduino_connection arduino;
    MMStudio si_in;
    loopclass[] loops;
    String[] triggerinfo;
    
    int todisplay;
    
    public arduino_gui(MMStudio si) {
        loops=new loopclass[1];
        loops[0]=new loopclass();
        todisplay = 0;
        
        int numofsteps=1;
        si_in=si;
        getInfoFromFile();
        
        initComponents();
        this.setVisible(true);
        
        
        
        
        Object[][] tablevals=new Object[numofsteps][numoflasers+4];
        for(int i=0;i<numofsteps;i++){
            tablevals[i][0]=i+1;
            tablevals[i][numoflasers+1]=100;
            tablevals[i][numoflasers+2]=true;
            tablevals[i][numoflasers+3]=true;
            for(int j=1;j<numoflasers+1;j++)tablevals[i][j]=true;
        }
        
        String[] headings=new String[numoflasers+4];
        headings[0]="Step";
        headings[numoflasers+1]="Pause(ms)";
        headings[numoflasers+2]="Camera 1";
        headings[numoflasers+3]="Camera 2";
        for(int j=1;j<numoflasers+1;j++)headings[j]=lasernames[j-1];
        
        final Class[] typesin =new Class[numoflasers+4];
        typesin[0]=java.lang.Integer.class;
        typesin[numoflasers+1]=java.lang.String.class;
        typesin[numoflasers+2]=java.lang.Boolean.class;
        typesin[numoflasers+3]=java.lang.Boolean.class;
        for(int j=1;j<numoflasers+1;j++)typesin[j]=java.lang.Boolean.class;
        
        final Boolean[] editin =new Boolean[numoflasers+4];
        editin[0]=false;
        for(int j=1;j<numoflasers+4;j++)editin[j]=true;
        
        jTable1.setModel(new javax.swing.table.DefaultTableModel(tablevals,headings) {
            Class[] types = typesin;
            Boolean[] canEdit = editin;

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.setSurrendersFocusOnKeystroke(true);
        
        
    arduino= new Arduino_connection("COM"+arduino_com_num,si_in);
        
        
    }

    public void getInfoFromFile()
    {
        String inputfile = null;
        try {
            File filename = new File(ij.ImageJ.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            String filenamestring=filename.toString();
            filenamestring=filenamestring.substring(0,filenamestring.lastIndexOf("\\"));
            inputfile=filenamestring+"\\NicoLase_config_file.txt";
            
            String line;
            InputStream fis = new FileInputStream(inputfile);
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);

            line = br.readLine();
            arduino_com_num=line.substring(line.lastIndexOf(":")+2);
            
            line = br.readLine();
            numoflasers=Integer.parseInt(line.substring(line.lastIndexOf(":")+2));
            
            line = br.readLine();
            line = line.substring(line.lastIndexOf(":")+2);
            lasernames = line.split(",");
            
            triggerinfo = new String[3];
            for( int i=0;i<3;i++){line = br.readLine();triggerinfo[i] = line.substring(line.lastIndexOf(":")+2);}
            
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        
        } 
        catch (URISyntaxException e){System.out.println("in getInfoFromFile");System.out.println(inputfile); }
        catch (FileNotFoundException e){JOptionPane.showMessageDialog(null, "Can't find file :"+inputfile);System.out.println("file not found");System.out.println(inputfile); }
        catch (IOException e){JOptionPane.showMessageDialog(null, "Can't read file");System.out.println("can't read file");System.out.println(inputfile); }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();

        setTitle("NicoLase");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("Number of Steps in loop");

        jLabel2.setText("Number of repeats of  loop");

        jTextField1.setText("1");

        jTextField2.setText("100");

        jButton1.setText("Update");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton1MouseReleased(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Loop", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getColumn(0).setResizable(false);
        jTable1.getColumnModel().getColumn(1).setResizable(false);
        jTable1.getColumnModel().getColumn(2).setResizable(false);
        jTable1.getColumnModel().getColumn(3).setResizable(false);

        jLabel4.setText("Loop cycle time (ms) ");

        jTextField3.setText("100");

        jLabel5.setText("Exposure Time");

        jTextField4.setText("20.0");

        jButton2.setText("Aquire");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton2MouseReleased(evt);
            }
        });

        jButton3.setText("Reset Arduino");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton3MouseReleased(evt);
            }
        });

        jLabel6.setText("Number of loops");

        jLabel7.setText("Loop to Display");

        jTextField5.setText("1");

        jTextField6.setText("1");

        jButton5.setText("Update");
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton5MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4))
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(131, 131, 131)
                                .addComponent(jLabel5)
                                .addGap(75, 75, 75)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(jButton1)
                                .addGap(35, 35, 35)
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(124, 124, 124)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jButton5)
                .addContainerGap(41, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton5)))
                .addGap(8, 8, 8)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(jLabel4))
                            .addGap(6, 6, 6))
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTextField1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseReleased
        // TODO add your handling code here:
        

        int errorin=0;
        try {
              if (jTable1.getCellEditor() != null) {
            jTable1.getCellEditor().stopCellEditing();
        }
        
        loops[todisplay].runtime = Integer.parseInt(jTextField3.getText());
        loops[todisplay].numofsteps=Integer.parseInt(jTextField1.getText());
        loops[todisplay].numofrepeats=Integer.parseInt(jTextField2.getText());
        
        loops[todisplay].laserseqs=new int[loops[todisplay].numofsteps];
        loops[todisplay].pauses=new int[loops[todisplay].numofsteps];
        loops[todisplay].camera1=new boolean[loops[todisplay].numofsteps];
        loops[todisplay].camera2=new boolean[loops[todisplay].numofsteps];
        
        int laserval;
        boolean boolout;
        int onval;
        for(int i=0;i<loops[todisplay].numofsteps;i++){
            laserval=0;
            //for(int j=1;j<numoflasers+1;j++){
            for(int j=numoflasers;j>0;j--){
                boolout= (Boolean) jTable1.getValueAt(i, j);
                onval=(boolout)? 1 : 0;
                laserval=10*laserval+onval;
            }
            loops[todisplay].laserseqs[i]=laserval;
            loops[todisplay].pauses[i] = Integer.parseInt(jTable1.getValueAt(i,numoflasers+1).toString());
            loops[todisplay].camera1[i]= (Boolean) jTable1.getValueAt(i, numoflasers+2);
            loops[todisplay].camera2[i]= (Boolean) jTable1.getValueAt(i, numoflasers+3);
        }
        
        
        errorin=2;
        arduino.Update_arduino(loops);
        errorin=3;
        
       // JOptionPane.showMessageDialog(null,"updated arduino");
            
        errorin=4;
       // JOptionPane.showMessageDialog(null,"read in params");    
        
            si_in.core().setExposure(Double.parseDouble(jTextField4.getText()));

            //JOptionPane.showMessageDialog(null,"read in params2"); 
            
            SequenceSettings aqsettings=si_in.getAcquisitionManager().getAcquisitionSettings();
            //JOptionPane.showMessageDialog(null,"read in params3"); 
            
            
            int totnumofframes=0;
            for(int i=0;i<loops.length;i++)totnumofframes+=loops[i].numofsteps*loops[i].numofrepeats;
            
            
            aqsettings.numFrames=totnumofframes;
            
            //JOptionPane.showMessageDialog(null,"got aquisition settings");
           errorin=6;
            
            aqsettings.useCustomIntervals=false;
            aqsettings.intervalMs=0.0;
            aqsettings.save=false;

            errorin=7;
            
            
            Runnable r = new Run_Aquisition(si_in,aqsettings,loops,arduino,triggerinfo);
            new Thread(r).start();
            
            } catch (Exception e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(null,"in Image Setup : "+Integer.toString(errorin));
            //System.exit(1);
            }
        //acqeng.run(aqsettings,true);
         
    }//GEN-LAST:event_jButton2MouseReleased

    private void jButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseReleased
        loops[todisplay].numofsteps=Integer.parseInt(jTextField1.getText());
        
               Object[][] tablevals=new Object[loops[todisplay].numofsteps][numoflasers+4];
        for(int i=0;i<loops[todisplay].numofsteps;i++){
            tablevals[i][0]=i+1;
            tablevals[i][numoflasers+1]=100;
            tablevals[i][numoflasers+2]=true;
            tablevals[i][numoflasers+3]=true;
            for(int j=1;j<numoflasers+1;j++)tablevals[i][j]=true;
        }
        
        String[] headings=new String[numoflasers+4];
        headings[0]="Step";
        headings[numoflasers+1]="Pause(ms)";
        headings[numoflasers+2]="Camera 1";
        headings[numoflasers+3]="Camera 2";
        for(int j=1;j<numoflasers+1;j++)headings[j]=lasernames[j-1];
        
        final Class[] typesin =new Class[numoflasers+4];
        typesin[0]=java.lang.Integer.class;
        typesin[numoflasers+1]=java.lang.String.class;
        typesin[numoflasers+2]=java.lang.Boolean.class;
        typesin[numoflasers+3]=java.lang.Boolean.class;
        for(int j=1;j<numoflasers+1;j++)typesin[j]=java.lang.Boolean.class;
        
        final Boolean[] editin =new Boolean[numoflasers+4];
        editin[0]=false;
        for(int j=1;j<numoflasers+4;j++)editin[j]=true;
        
        jTable1.setModel(new javax.swing.table.DefaultTableModel(tablevals,headings) {
            Class[] types = typesin;
            Boolean[] canEdit = editin;

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.setSurrendersFocusOnKeystroke(true);
    }//GEN-LAST:event_jButton1MouseReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        arduino.Reset_arduino();
    }//GEN-LAST:event_formWindowClosing

    private void jButton3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseReleased
        // TODO add your handling code here:
        arduino.Reset_arduino();
    }//GEN-LAST:event_jButton3MouseReleased

    private void jButton5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseReleased
        // TODO add your handling code here:
        //save results
        if (jTable1.getCellEditor() != null) {
            jTable1.getCellEditor().stopCellEditing();
        }
        
        loops[todisplay].runtime = Integer.parseInt(jTextField3.getText());
        loops[todisplay].numofsteps=Integer.parseInt(jTextField1.getText());
        loops[todisplay].numofrepeats=Integer.parseInt(jTextField2.getText());
        
        loops[todisplay].laserseqs=new int[loops[todisplay].numofsteps];
        loops[todisplay].pauses=new int[loops[todisplay].numofsteps];
        loops[todisplay].camera1=new boolean[loops[todisplay].numofsteps];
        loops[todisplay].camera2=new boolean[loops[todisplay].numofsteps];
        
        int laserval;
        boolean boolout;
        int onval;
        for(int i=0;i<loops[todisplay].numofsteps;i++){
            laserval=0;
            //for(int j=1;j<numoflasers+1;j++){
            for(int j=numoflasers;j>0;j--){
                boolout= (Boolean) jTable1.getValueAt(i, j);
                onval=(boolout)? 1 : 0;
                laserval=10*laserval+onval;
            }
            loops[todisplay].laserseqs[i]=laserval;
            loops[todisplay].pauses[i] = Integer.parseInt(jTable1.getValueAt(i,numoflasers+1).toString());
            loops[todisplay].camera1[i]= (Boolean) jTable1.getValueAt(i, numoflasers+2);
            loops[todisplay].camera2[i]= (Boolean) jTable1.getValueAt(i, numoflasers+3);
        }
        
        
        if(loops.length!=Integer.parseInt(jTextField5.getText())){loops=new loopclass[Integer.parseInt(jTextField5.getText())];
            for(int i=0;i<Integer.parseInt(jTextField5.getText());i++)loops[i]=new loopclass();
        }

        todisplay = Integer.parseInt(jTextField6.getText())-1;
        
                
        jTextField3.setText(Integer.toString(loops[todisplay].runtime));
        jTextField1.setText(Integer.toString(loops[todisplay].numofsteps));
        jTextField2.setText(Integer.toString(loops[todisplay].numofrepeats));
        
         Object[][] tablevals=new Object[loops[todisplay].numofsteps][numoflasers+4];
        for(int i=0;i<loops[todisplay].numofsteps;i++){
            tablevals[i][0]=i+1;
            tablevals[i][numoflasers+1]=loops[todisplay].pauses[i];
            tablevals[i][numoflasers+2]=loops[todisplay].camera1[i];;
            tablevals[i][numoflasers+3]=loops[todisplay].camera2[i];;
        }
        
        for(int i=0;i<loops[todisplay].numofsteps;i++){
            laserval=loops[todisplay].laserseqs[i];
           // for(int j=numoflasers;j>0;j--){
            for(int j=1;j<numoflasers+1;j++){
                onval=  laserval%10;
                laserval=laserval/10;
                tablevals[i][j]=(onval==1)?true:false;
            }
        }
        

        
        String[] headings=new String[numoflasers+4];
        headings[0]="Step";
        headings[numoflasers+1]="Pause(ms)";
        headings[numoflasers+2]="Camera 1";
        headings[numoflasers+3]="Camera 2";
        for(int j=1;j<numoflasers+1;j++)headings[j]=lasernames[j-1];
        
        final Class[] typesin =new Class[numoflasers+4];
        typesin[0]=java.lang.Integer.class;
        typesin[numoflasers+1]=java.lang.String.class;
        typesin[numoflasers+2]=java.lang.Boolean.class;
        typesin[numoflasers+3]=java.lang.Boolean.class;
        for(int j=1;j<numoflasers+1;j++)typesin[j]=java.lang.Boolean.class;
        
        final Boolean[] editin =new Boolean[numoflasers+4];
        editin[0]=false;
        for(int j=1;j<numoflasers+4;j++)editin[j]=true;
        
        jTable1.setModel(new javax.swing.table.DefaultTableModel(tablevals,headings) {
            Class[] types = typesin;
            Boolean[] canEdit = editin;

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.setSurrendersFocusOnKeystroke(true);
        
        
    }//GEN-LAST:event_jButton5MouseReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(arduino_gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(arduino_gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(arduino_gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(arduino_gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                //new arduino_gui(si_in).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration//GEN-END:variables
}
