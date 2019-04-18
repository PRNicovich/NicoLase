Software associated with NicoLase Arduino laser sequencer.  

The Basic Controller allows for sequencing of the TTL outputs with an external system clock (ie a camera fire signal) or as a laser select and shutter.   The Advanced System Controller allows the Arduino to be the master system clock to control both laser and camera triggering, in addition to an optional external clock mode.  

The Basic Controller is simpler to operate and is sufficient for most applications.  Example configurations are included for Micro-Manager with controller as a FreeSerialPort or as a UserDefinedStateDevice + UserDefinedShutter.  The former allows lower-level control of the sequencing operations through the script interface.  The UserDefined configuration allows 'channel' selection and on/off control through the GUI interface.  Once configured, the UserDefinedStateDevice behaves very similar to a filter wheel but with laser channel output(s) as the state parameter.  The UserDefinedShutter can toggle on and off a single laser configuration as if it were a shutter device. Together these act as straightforward Micro-manager devices for a user-friendly setup. 

The Advanced System Controller is a great option if you wish to have a master clock that isn't reliant on the camera signal.  Additionally, the Advanced System Controller solves the timelapse issue in Micromanager for acquisitions between ~3 Hz and the free-run rate of your camera.  

Both use the same hardware and share many of the same serial commands. Switching between the two options is a matter of uploading the appropriate sketch to the Arduino. 

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Basic Controller *

- Arduino:
Arduino skech (SerialSequenceUploader.ino) should be uploaded to Arduino Uno.  Keep USB connected for serial communication.

See header of sketch file for list and syntax of allowed serial strings.

- MATLAB GUI:

MATLAB GUI (SequenceUploader.m) can be run in MATLAB R2014b or later.  The COM port associated with Arduino should be specified as a string as an input argument on GUI call or specified in function file on line 17.

- Micro-ManagerDemo: 

-- FreeSerialPort:

Example Beanshell script for interfacing with Arduino running above sketch through Micro-manager.  This requires the Arduino to be set up on the correct COM port as a FreeSerialPort device in the Micro-Manager config.  

On boot the Arduino sketch defaults to an 'all on' sequence, so if you only wish to toggle lasers on and off in sync with the camera exposures, it is not necessary to re-program the default sequence.  Only if you wish to take advantage of more complex features is any communication over USB necessary prior to an acquisition.

.\BasicController\Micro-MangerDemo\FreeSerial\ includes a .bsh script file demonstrating this functionality.

-- StateDevices:

Configuration of controller as a UserDefinedStateDevice and/or UserDefinedShutter.  These are both useful for GUI-centric interaction with the controller for typical fluorescence microscopy use. 

The UserDefinedStateDevice allows a single channel (not sequence) to be selected using USB serial communication with MicroManager.  The Hardware Configuration in MicroManager must include a specification for the number of device states at configuration.  There are 2^6 possible configs with this hardware, but likely the single-channel configurations will be of most use.  In that configuration, the 'Number of positions' parameter should be 7 (6 each with a single laser on plus one all-off state) and the command string set to 'M BXXXXXX' where 'XXXXXX' is the binary output configuration for that channel.  These can be then designated in the Micro-manager Configuration Settings groups as a part of a preset along with any other desired devices.  For example, one may set up a channel with a GFP filter cube plus the UserDefinedStateDevice 'SetPosition-command-1' set to 'M B000010', given a 488 nm laser plugged into shield output C2.  The Micro-manager documentation has some additional tips for the configuration of this device.

The UserDefinedShutter device turns on the first output in the sequence buffer (with 'Q' string) or all outputs off ('O').  This also configures the controller to ignore any input signal from the FIRE inputs (this can be reset by restarting Arduino, restarting serial communication to board - including loading a new Hardware Configuration w/o shutter in Micro-manager, or sending 'X' string over serial).  With the shutter control, outputs can be toggled directly through the Micro-manager GUI shutter buttons or with the Auto-shutter.  This is particuarly useful if your system does not use or require a clock signal from the camera.  

Files .\BasicController\Micro-MangerDemo\StateDevices\*.cfg include demo configurations for the Basic Controller as a UserDefinedStateDevice, UserDefinedShutter, or both. These will hopefully be useful in getting started with these features.

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Advanced System Controller *

Arduino sketch and corresponding Micro-Manager GUI allowing for further use of the Arduino's features to control the microscope system.  In addition to the laser sequencing while being triggered by the camera, there is also a Arduino Master mode, where the Arduino can act as the system controller.  The camera and lasers are driven by the Arduino as the master system clock.  

In this configuration all cameras are set to External Trigger mode.  The primary camera is then triggered by the Arduino.  If a secondary camera is present, it is triggered by the Fire signal from the primary camera.  

The primary motivation for the Arduino as the master clock is solving the no-mans-land of timelapse rates between the camera's free-run rate (delay < [exposure time + dead time]) and ~3 Hz.  In Micromanager, the timelapse delays in this range seem to change from a continuous camera acquisition to a series of single acquisitions at a given rate.  This comes with much more communication overhead, ultimately limiting the capture rate to accomodate the ~250 ms of added delay. Such a delay is not immediately obvious in the software without checking the timestamps of acquired images and may not necessarily affect your system or measurements. We like to work in the affected range for our system, however, so a solution is needed.  

By having all cameras set to External Trigger mode, the communication overhead comes once at the beginning of the acquisition.  Between frame captures the camera does not have to communicate with the PC except to transfer image data.  The cameras wait for the next incoming external trigger pulse to take the next image and are able to capture without additional communication overhead.  This means timelapse images can be captured at any rate from the free-run rate of the camera or slower.  

It is essential that these external pulses come when the camera is listening, so the external delay must be greater than the set exposure time plus the camera dead time (50-65 ms on an Andor 888; check the Minumum Cycle Time on the Micromanager Device Property Browser for this value).  The current version of this software does not check for a 'Ready' signal from the camera before sending the next pulse, so this timing is the responsibility of the user. 

This software has been tested with 2 x Andor 888 cameras and with a Prime 95B with either 4 or 5 diode lasers. 

Software consists of:

- Arduino 
In .\Arduino_controller.  Compile and upload Arduino_controller.ino to the Arduino Uno.  Additional files LoopFiles.ino and MyHeaders.h are required dependencies. 

- MicroManager Plugin
NicoLase.jar in .\Micro-Manager_Plugin.  Installation instructions are included in .\Miro-Manager_Plugin\Installation_Guide.txt.  

This plug-in for MicroManager provides a GUI for configuring the laser sequencing and timing as well as which cameras are utilized for a given acquisition channel.  The acquisition settings are uploaded to the Arduino, images acquired, and the stack with appropriate channels re-assembled by the plug-in.  

** Note ** 
With the Arduino as the master clock, you don't need another clock signal!  If you simply want to trigger TTL devices at a given rate, then this software may work for you!  See .\Arduino_controller documentation for details on how to control sequencing and serial communication. 