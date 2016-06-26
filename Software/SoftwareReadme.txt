Software associated with NicoLase Arduino laser sequencer

- Arduino:
Arduino skech (SerialSequenceUploader.ino) should be uploaded to Arduino Uno.  Keep USB connected for serial communication.

See header of sketch file for list and syntax of allowed serial strings.

- MATLAB GUI:

MATLAB GUI (SequenceUploader.m) can be run in MATLAB R2014b or later.  The COM port associated with Arduino should be specified as a string as an input argument on GUI call or specified in function file on line 17.

- Micro-ManagerDemo: 

Example Beanshell script for interfacing with Arduino running above sketch through Micro-manager.  This requires the Arduino to be set up on the correct COM port as a FreeSerialPort device in the Micro-Manager config.  

On boot the Arduino sketch defaults to an 'all on' sequence, so if you only wish to toggle lasers on and off in sync with the camera exposures, it is not necessary to re-program the default sequence.  Only if you wish to take advantage of more complex features is any communication over USB necessary prior to an acquisition.
