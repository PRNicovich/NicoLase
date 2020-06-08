"""
Created on Wed Mar 11 16:35:16 2020

@author: Rusty Nicovich


NicoLase Python class

Supports functionality of NicoLase Basic Controller for laser selection and communication.

From Arduino sketch (.\NicoLase\Software\BasicController\ArduinoSketch\SerialSequenceUploader\SerialSequenceUploader.ino):

"""
import time

returnMessages = {
    'CMD_NOT_DEFINED': 0x15,
    'DeviceID' : 'NicoLaseSequencer'
    }

def nicoLaseStart(comPort, baud, timeOut):
    
    """
    Helper function to start a serial object like NicoLase or octoDAC
    
    Inputs : comPort - string; com port to use for this object
             baud - int; baud rate for serial port.  NicoLase is set to 115200
             timeOut - int; timeout time for port in seconds.  Default is 1    
             
    Returns : n1 - initialized NicoLaseDriver object on provided serial port parameters.
    """
    
    import serial 
    
    ser1 = serial.Serial(comPort, baud, timeout = timeOut)
    time.sleep(3)
    ser1.flushInput()
    ser1.reset_input_buffer()
    
    return ser1

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
    
    def numToLongIntegerStr(self, exTime):
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
        self.writeAndRead('D ' + fmtStr)    
        
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
    def allOnSeq(self):
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
        self.writeAndRead('G ' + filtNum)
        
    # H
    def setPostSeqExTime(self, exTime):
        """ 
        Set post-sequenc time
        """      
        filtNum = self.numToLongIntegerStr(exTime)
        self.writeAndRead('H ' + filtNum)
        
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
    

