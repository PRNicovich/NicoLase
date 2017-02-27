#ifndef MyHeaders_h
#define MyHeaders_h

#include "Arduino.h"


using namespace std;

class subloop{
  public:
    uint8_t* laserseq;
    unsigned long* pauses;
    unsigned long subnextcutoff;
    unsigned long subloopcount;
    bool subrunbool;
    unsigned int numofseqsin;
    unsigned int totnumofseqs;

    unsigned int sublooprepeatnum; 
    unsigned int sublooptime;

    ~subloop();
    void runsub(unsigned long timein );
    void updatesub(unsigned long timein );
};

class mainloop{
  public:
    subloop* loops;
    
    unsigned long loopcount;
    unsigned long subloopcount;
    unsigned long nextcutoff;
    bool runbool;
    unsigned int numofloops;

    void clearall();
    void addlasersequence(String& input);
    void setloops(String& input);
    void runmain(unsigned long timein );
    void updatemain(unsigned long timein  );
};

#endif
