#include "MyHeaders.h"

    subloop::~subloop(){
          delete laserseq;
          delete pauses;
      
    }
    
    void mainloop::clearall(){
     /* for(int i=0;i<numofloops;i++){
            delete loops[i].laserseq;
            delete loops[i].pauses;
            loops[i].numofseqsin=0;
             loops[i].totnumofseqs=0;
      }*/
          delete loops;
          loopcount=0;
          nextcutoff=0;
          runbool=false;
    }
    
    void mainloop::addlasersequence(String& input){
        int subtoaddto = 0;
        int count=0;
        while(input[count]>47&&input[count]<58)
        {
          subtoaddto=10*subtoaddto+input[count]-48;
          count++;
        }
        subtoaddto--;
        count++;

        uint8_t laserseqin=0;
        while(input[count]>47&&input[count]<50)
        {
          laserseqin=2*laserseqin+input[count]-48;
          count++;
        }
        count++;

        loops[subtoaddto].laserseq[loops[subtoaddto].numofseqsin]=laserseqin;

        unsigned long pausein=0;
        while(input[count]>47&&input[count]<58)
        {
          pausein=10*pausein+input[count]-48;
          count++;
        }
        
        loops[subtoaddto].pauses[loops[subtoaddto].numofseqsin]=pausein;

        loops[subtoaddto].numofseqsin++;

        Serial.print("Added laser sequence to loop ");Serial.print(subtoaddto+1);Serial.print(". It now contains ");Serial.print(loops[subtoaddto].numofseqsin);Serial.print(" of ");Serial.print(loops[subtoaddto].totnumofseqs);Serial.println(" steps");
        for(int i=0;i< loops[subtoaddto].numofseqsin;i++){Serial.print(i+1);Serial.print(" has laser code ");Serial.print(loops[subtoaddto].laserseq[i]);Serial.print(" and pauses for ");Serial.print(loops[subtoaddto].pauses[i]);Serial.println(" ms");}
    }
    
    void mainloop::setloops(String& input){
      
        numofloops = 0;
        int count=0;
        while(input[count]>46&&input[count]<58)
        {
          numofloops=10*numofloops+input[count]-48;
          count++;
        }
        
        count++;
        loops = new subloop[numofloops];//.resize(numofloops);


        for(int i=0;i<numofloops;i++){
            loops[i].numofseqsin=0;
            loops[i].totnumofseqs=0;
            while(input[count]>47&&input[count]<58)
            {
              loops[i].totnumofseqs=10*(loops[i].totnumofseqs)+input[count]-48;
              count++;
            }
            count++;

            loops[i].laserseq = new uint8_t[loops[i].totnumofseqs];
            loops[i].pauses = new unsigned long[loops[i].totnumofseqs];
            
            
            loops[i].sublooprepeatnum=0;
            while(input[count]>47&&input[count]<58)
            {
              loops[i].sublooprepeatnum=10*(loops[i].sublooprepeatnum)+input[count]-48;
              count++;
            }
            count++;
            loops[i].sublooptime=0;
            while(input[count]>47&&input[count]<58)
            {
              loops[i].sublooptime=10*(loops[i].sublooptime)+input[count]-48;
              count++;
            }
            count++;
        }
        
        Serial.print("Total number of loops set to ");Serial.println(numofloops);
        for (int i=0;i<numofloops;i++){Serial.print(i+1);Serial.print(" has ");Serial.print(loops[i].totnumofseqs);Serial.print(" steps and is repeated ");Serial.print(loops[i].sublooprepeatnum);Serial.print(" times and runs over ");Serial.print(loops[i].sublooptime);Serial.println(" ms");}
    }
    
    void mainloop::runmain(unsigned long timein ){
      loopcount = 0;
      subloopcount=0;
      loops[loopcount].runsub(timein);
      nextcutoff = timein+loops[loopcount].sublooptime;
      runbool=true;
    }
    
    void mainloop::updatemain(unsigned long timein ){
      if(timein<nextcutoff)loops[loopcount].updatesub(timein);
      else{
          subloopcount++;
          if(subloopcount>=loops[loopcount].sublooprepeatnum){loopcount++;subloopcount=0;}

          if(loopcount<numofloops){
          loops[loopcount].runsub(timein);
          nextcutoff += loops[loopcount].sublooptime;
          }
          else runbool = false;
      }
    }

    
    void subloop::runsub(unsigned long timein){
      subloopcount=0;
      subnextcutoff = timein+pauses[subloopcount];
      subrunbool=true;
      PORTC=laserseq[subloopcount];
      digitalWrite(outPin, HIGH);
      debouncestarttime=millis();
      currentlyfiring=true;
      
    }
    
    void subloop::updatesub(unsigned long timein){
      if(subrunbool && timein>subnextcutoff){
        subloopcount++;
        if(subloopcount<numofseqsin){
             PORTC=laserseq[subloopcount];
             subnextcutoff+=pauses[subloopcount];
             digitalWrite(outPin, HIGH);
             debouncestarttime=millis();
             currentlyfiring=true;    
        }
        else subrunbool = false;
      }
    }

