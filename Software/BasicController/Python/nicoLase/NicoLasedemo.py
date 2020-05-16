# -*- coding: utf-8 -*-
"""
Created on Tue Apr 14 13:59:02 2020

@author: Rusty Nicovich
"""

import serial
import time
from NicoLaseDriver import NicoLaseDriver

comPort = 'COM4'
baud = 115200
timeOut = 1

serObj = serial.Serial(comPort, baud, timeout = timeOut)
time.sleep(3)
serObj.flushInput()
serObj.reset_input_buffer()

#%%
# Initialize octoDAC object
nL = NicoLaseDriver(serObj, verbose = True)

# Clear existing patterns
nL.clearSeqs()

# Program in a few patterns
nL.appendToSeq(0b010101)
nL.appendToSeq(0b101010)
nL.appendToSeq(0b111000)
nL.appendToSeq(0b000111)

# Echo existing sequences
nL.echoAllSeq()

# Query shutter state
shutterState = nL.queryShutter()
if (shutterState):
    print("Shutter open")
elif (not shutterState):
    print("Shutter closed")
    

# Open shutter
nL.firstOnInSeq()

# Close shutter
nL.allOff()


#%%
serObj.close()