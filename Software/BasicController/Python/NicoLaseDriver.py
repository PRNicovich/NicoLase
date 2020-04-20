"""
Created on Wed Mar 11 16:35:16 2020

@author: Rusty Nicovich


NicoLase Python class

Supports functionality of NicoLase Basic Controller for laser selection and communication.

From Arduino sketch (.\NicoLase\Software\BasicController\ArduinoSketch\SerialSequenceUploader\SerialSequenceUploader.ino):

/* Serial comands :
 *  A BXXXXXX - Append pattern to main sequence.  BXXXXXX is binary pattern, from Red-Blue
 *  B BXXXXXX - Set pre-collection sequence.  BXXXXXX is binary pattern, from Red-Blue.  
 *              There can only be one of these and is used, for example, to allow 405 laser to expose for M ms 
 *  C - Clear programmed sequence
 *  D BXXXXXX - Set post-collection sequence.  As in B.
 *  E - Echo sequence of pattern (in Hex)
 *  F - Set first value in sequence to B00111111, or all 6 on
 *  G long X - set exposure time for pre-acquisition sequence B
 *  H long X - set exposure time for post-acquisition sequence D
 *  I Bool X - True/false for pre-acquisition sequence to be followed by camera trigger out
 *  J long X - set delay between pre-acquisition sequence and camera trigger out
 *  K - Call pre-acquisition sequence B for time set in G
 *  L - Call post-acquisition sequence D for time set in H  
 *  M BXXXXXX - Set pattern as only in sequence. Equivalent to 'C\nA BXXXXXX'
 *  N long,long,long..., - Set counter numbers for each A sequence. 
 *                        Each will be called N[i] times before moving to next, then back to start
 *  O - Turn off all outputs, ignoring input shutter state. Call 'X' to reset shutter state.
 *  P - Add pre-programmed sequence of Blue-Red individually
 *  Q - Turn on first pattern in programmed sequence, ignoring input shutter state.  Do not iterate counter.
 *  R - Reset sequence counter to 0 and cycleNum to 0 (reset)
 *  S XX - Set sequence counter to XX
 *  T - Test main sequence programmed
 *  U - Echo only first array sequence as byte. Terse output version of 'E'.
 *  V - Return version string.
 *  W BXXXXXX - Turn on array BXXXXXX directly.  No sequencing or triggering.
 *  X - All off and reset to trigger-enabled setting.
 *  Y - Return device ID ('NicoLaseSequencer')
 *  ? X - Query state of device X.  Supported devices : S = shutter open (0 = closed, 1 = open)
 *  
 *  
 *  Button with pull-down resistor into pin 3
 *  LEDs with pins A0-A5 (Red-Blue, C6-C1) as sources
 *  
 *  Serial requires 9600 baud, newline characters only for line feed
*/

"""
import time

returnMessages = {
    'CMD_NOT_DEFINED': 0x15,
    'DeviceID' : 'NicoLaseSequencer'
    }

class NicoLaseDriver():
    """
    Class for communicating with NicoLase Arduino Shield
    
    """
    def __init__(self, serialDevice, verbose = False):
        
        self.serial = serialDevice
        self.verbose = verbose

        # Once connected, check that port actually has NicoLase on the receiving end
        devID = self.getIdentification()
        if devID == returnMessages['DeviceID']:
            if self.verbose:
                print('Connected to NicoLase on port ' + self.serial.port)
        else:
            print("Initialization error! Disconnecting!\n")
            self.serial.close()
            
    def numToBinaryString(self, seq):
        """
        Utility function to convert input sequence from numerical form to
        string format to send to NicoLase
        """
        decRep = int(seq) # Convert to decimal format
        # If number is greater than 63 (0b111111 or 0x3f)
        # then leading digits will get dropped. If so, warn user and 
        # take only remainder
        if decRep > 63:
            
            decRepTrim = decRep % 64
            
            if self.verbose:
                print("Input value %d for sequence too large. Truncating to %d.", decRep, decRepTrim)
        else:
            decRepTrim = decRep
        
        # Convert back to binary with the right number of leading zeros
        retString = 'B{0:06b}'.format(decRepTrim)
        
        return retString
    
    def numToLongInteger(self, exTime):
        """
        Utility function to convert input time to long integer
        """
        longTime = str(int(exTime))
        return longTime

    def writeAndRead(self, sendString):
        """
        Helper function for sending to serial port, returning line
        """
        self.serial.reset_input_buffer()
        self.serial.reset_output_buffer()
        self.serial.write(sendString + '\n')
        ret = self.serial.readline().strip()
        
        if self.verbose:
            print(ret)
            
        return

    # A
    def appendToSeq(self, seq):
        """ 
        Append input seq to sequence in Arduino
        Input seq can be any numerical form (binary, hex, or decimal)
        """
        fmtStr = self.numToBinaryString(seq)
        self.writeAndRead('A ' + fmtStr)
        time.sleep(0.1)
        
    # B
    def setPreCollectionSeq(self, seq):
        """ 
        Set pre-collection sequence
        Input seq can be any numerical form (binary, hex, or decimal)
        """
        fmtStr = self.numToBinaryString(seq)
        self.writeAndRead('B ' + fmtStr)  
        
    # C
    def clearSeqs(self):
        """ 
        Clear all sequences from buffer
        """
        self.writeAndRead('C') 
        
    # D
    def setPostSeq(self, seq):
        """ 
        Set post-collection sequence
        Input seq can be any numerical form (binary, hex, or decimal)
        """
        fmtStr = self.numToBinaryString(seq)
        self.writeAndRead('D' + fmtStr)    
        
    # E
    def echoAllSeq(self):
        """ 
        Echo current sequence
        """
        self.serial.reset_input_buffer()
        self.serial.reset_output_buffer()
        self.serial.write('E\n')
        rd = self.serial.read(1000)
        print(rd)
        
    # F
    def allOnSeq(self, seq):
        """ 
        Set sequence to 'all on'
        """
        self.writeAndRead('F') 
        
    # G
    def setPreSeqExTime(self, exTime):
        """ 
        Set pre-sequenc time
        """      
        filtNum = self.numToLongIntegerStr(exTime)
        self.writeAndRead('G' + filtNum)
        
    # H
    def setPostSeqExTime(self, exTime):
        """ 
        Set post-sequenc time
        """      
        filtNum = self.numToLongIntegerStr(exTime)
        self.writeAndRead('H' + filtNum)
        
    def triggerAfterPreAcq(self, boolX):
        """ 
        True/false for pre-acquisition sequence to be followed by camera trigger out
        boolX should be boolean
        """
        self.writeAndRead('I ' + boolX)
         
        
    # J
    def setPreToTriggerDelayTime(self, exTime):
        """ 
        Set pre-acquisition to cam trigger delay
        """      
        filtNum = self.numToLongIntegerStr(exTime)
        self.writeAndRead('J ' + filtNum)
        
        
    # K
    def callPreSeq(self):
        """ 
        Call pre-acquisition sequence
        """
        self.writeAndRead('K')
         
    # L
    def callPostSeq(self):
        """ 
        Call post-acquisition sequence
        """
        self.writeAndRead('L')
        
    # M
    def setSeq(self, seq):
        """ 
        Clear existing and set singular sequence
        Command when switching 'channels' with shutter support
        """
        fmtStr = self.numToBinaryString(seq)
        self.writeAndRead('M ' + fmtStr)      
    
    # M
    def setIterations(self, iterList):
        """ 
        Set iterations for each pattern in sequence.
        Input is list of integers.
        """
        cleanList = [self.numToLongIntegerStr for x in iterList]
        self.writeAndRead('N ' + ','.join(cleanList))
        
    # O
    def allOff(self):
        """ 
        Set all outputs to LOW
        Effectively 'shutter closed'
        """
        self.writeAndRead('O')
        
    # P
    def setPreProgrammedSeq(self):
        """ 
        Set sequence to pre-programmed test pattern
        """
        self.writeAndRead('P')

    # Q
    def firstOnInSeq(self):
        """ 
        Turn on first pattern in sequence, ignoring shutter state.
        Do not iterate counter.
        Effectively 'shutter open'.
        """
        self.writeAndRead('Q')
        
    # R
    def resetSeqCounter(self):
        """ 
        Reset sequence counter.
        """
        self.writeAndRead('R')
        
    # S
    def setSeqCounter(self, seqCount):
        """ 
        Set sequence counter to seqCount
        """      
        filtNum = self.numToLongIntegerStr(seqCount)
        self.writeAndRead('S ' + filtNum) 
    
    # T
    def testSeq(self):
        """ 
        Cycle outputs through main sequence
        One count per pattern
        """
        self.writeAndRead('T')
        
    # U
    def echoFirstSeq(self):
        """ 
        Echo first pattern in sequence
        """
        self.writeAndRead('U')
      
    # V
    def getVersion(self):
        """ 
        Return version in sketch
        """
        self.writeAndRead('V')
        
    # W
    def setSeqAndTurnOn(self, seq):
        """ 
        Clear existing and set singular sequence, then turn on
        Command when switching 'channels' directly over USB
        """
        fmtStr = self.numToBinaryString(seq)
        self.writeAndRead('W ' + fmtStr)
        
    # X
    def allOffAndResetTriggering(self):
        """ 
        Turn off all outputs and set to camera triggering
        """
        self.writeAndRead('X')
        
    # Y 
    def getIdentification(self):
        """ identification query """
        self.serial.write('Y\n')
        idn = self.serial.readline().strip()
        return idn
        
    # ?  
    def queryShutter(self):
        """ 
        Query state of shutter
        """
        self.serial.write('? S\n')
        idn = self.serial.readline().strip()
        return idn 

if __name__ == '__main__':
    print("Testing NicoLase sequencer shield...")
    nL = NicoLaseDriver(3)
    
    nL.setSeq(0b010101)
    nL.echoFirstSeq()
    
    nL.appendToSeq(0b101010)
    nL.echoAllSeq()
    
    nL.clearSeqs()
    

