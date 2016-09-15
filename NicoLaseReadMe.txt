NicoLase - An open-source diode laser combiner, fiber launch, and sequencing controller for fluorescence microscopy

Philip R Nicovich, James Walsh, Till B\{:}ocking, and Katharina Gaus

\textbf{Abstract}
Modern fluorescence microscopy requires illumination sources with high power and software control across a wide range of wavelengths. Diode lasers meet the power requirements and combining multiple units into a single fiber launch expands their capability across the required spectral range.  We present the NicoLase, an open-source diode laser combiner, fiber launch, and software sequence controller for fluorescence microscopy and super-resolution microscopy applications.  Two configurations are described, giving four or six output wavelengths, with all CAD files, machinist drawings, and controller source code openly available.

\textbf{Introduction}
Multiple laser lines are almost are a requirement for a modern fluorescence microscope.  Rarely can a system produce publication-appropriate results without at least a second laser wavelength.  Diode laser units are convenient for fluorescence microscopy and super-resolution microscopy due to their high power and ease of use.  While a bit more expensive than the classic noble gas mix laser plus an AOTF, the long life, ease of use, and expanded wavelength repertoire of diode lasers have pushed the other options strongly out of favor. Multiple makers provide CW diode laser product lines with a dozen or two wavelengths from the  UV through mid-IR with powers from tens to a few hundred milliwatts.  With the addition of direct modulation avaliable on nearly all modern units there's no need for an acousto-optical tunable filter or other tuning or power modulation control in addition to the diodes themselves.    

Software and trigger control of the lasers is strongly desired.  Microscopy cameras often have a dead time between frames when laser illumination is (almost always) undesired, and at the very least it's convenient to have a laser that shuts off when the acquisition isn't actively running. As a further step, it's often necessary to alternate or otherwise sequence lasers for certain experiments (\textit{i.e.} ALEX FRET, photoactivation and imaging experiments, or pseudo-simultaneous multicolor imaging).  With digital control of laser emission this needs to be controlled at the laser head and with a controller than can be programmed in a straightforward manner from the instrument PC. 

Imaging systems can utilize free-space coupling, but coupling into a single-mode fiber provides extra flexibility in system configuration, enhanced beam quality, and makes precise positioning of the beam, such as is what is required for TIRF microscopy, much more straightforward.  Coupling to a single-mode fiber sets a high bar in terms of alignment of the multiple lasers into a single output and the precision for free-space or multi-mode fiber coupling is relatively relaxed.  A system that can effectively align into a single-mode fiber can do all three should the application require it. 

There are commercial options to achieve nearly all of these criteria.  The presented system does not do anything not available on the market already, but the final cost here is 2/3 to 1/2 of the cost of commerical instruments and can be easily customized in terms of size, number of lines, and final configuration.  The system presented here is flexible, compact, relatively inexpensive, and fully open-source, allowing end users to configure the system to their exact application. 

Lab-built assemblies for laser combining are also common.  By sharing these implementations the hope is that downstream users can take advantage of these materials without having to reinvent the fiber launch, so to speak. In the same vein as projects such as OpenSPIM, this provides a starting point for others who are encouraged to share their own improvements and modifications.

Two generations of this project are presented.  The first, referred to as teh NicoLase 1500, is a single-mirror coupling system coupling five (expandable to six) diode lasers into a single-mode fiber output.  The latter, the NicoLase 2400, is a two-mirror system with four diode laser lines with output power tunably shared between two single-mode fiber outputs.  These share the same footprint, many of the same components, and the same control software and PC interface.  

All design files, bill of materials, software source code, and machine shop diagrams are included in the author's GitHub repo, available at https://github.com/PRNicovich/NicoLase.  This repository is continually maintained including updates for new iterations of the NicoLase project.  Users are encouraged to generate a fork of this repository for their use and upload any of their own updates and modifications.

### Hardware
#### Lasers
The NicoLase is built primarily around Vortran Stradus laser diode units.  These are a standardized size for each line, reasonably priced, and produce a large amount of power for their size and cost.  Nearly all produce enough power for dSTORM imaging.  These units can be digitally modulated at a high rate (usually up to 2 MHz) and potentially analog modulated if desired.  

A straightforward modification allows you to use Coherent OBIS heads in place of some/all of the Vortran units.  Control is nearly identical and aside from rearrangment of the mounting screw holes, these are drop-in replacements for the Vortran units.  Though not tested specifically for this application, a survey of products from major manufacturers confirms that those from Spectra-Physics (Excelsior), Oxxius (Laser Boxx), and Omikron (LuxX) are also compatible. 

Any chosen laser diode unit would need to have the following characteristics: a 100 mm x 50 mm footprint or smaller; 19.00 mm or 3/4" (19.05 mm) beam height, or can be brought to this height with spacers/lifts; digital modulation via TTL pulse; and software control of laser power via USB or RS-232 (for add-on plug-in control).  These qualifcations have become the standard across the laser diode industry, so finding alternative products to those presented should not be difficult.

#### Heatsinks and Mounting

All optics are chosen for a 1.5" (39.0 mm) center height.  Mirrors and dichroic mirrors are mounted in Thorlabs Polaris 1" mounts.  The single-mirror system requires 3-adjuster mounts, but either 2- or 3- adjuster mounts can be used for the two-mirror system.  Hex adjuster are nice for squeezing the mounts closer together, but are a bit more of a pain to adjust.  Clearances are just barely there for thumb adjusters and for your fingers. Those more rotundly-digited of us may prefer the hex adjuster mirrors. 

Mirrors and other optics atop standard Thorlabs pedestals except for the fiber coupler itself.  This needs a custom pedestal to be machined, but this is a straightforward piece of turning.  The original 1500 design includes a flat block for this mount but the 2400 pedestal is a much better design.  Pedestal clamps are standard Thorlabs parts, though lower-profile clamps are advantageous for some positions.

Excitation filters can be mounted in standard lens mounts, or options for 3D printed mounts for 1/2" filters designed for the NicoLase is given in author's 3D printing repo [here](https://github.com/PRNicovich/3D-Printed-Optics-Lab-Parts).  A mount to tie the Arduino to the optical table is or to a spare slot in the heat sink block is given in the same repo. 

Diode laser heads require a heat sink for proper operation, which has to at least be a contact with a block of aluminum.  The laser outputs need to be at 39.0 mm to intersect with the steering and coupling optics.  And of course lasers have to actually bolt down to something.  

To solve these issues, a heat sink + mounting block was machined.  Three dimensions must be held to strict tolerances for this part.  The top must be as flat as possible (0.1 mm specified) with no scratches or machine marks to hinder interfacing with the bottom of the laser diode for optimal heat transfer.  The top and bottom faces must be parallel (0.1 mm specified) so the beams are at the same height.  And the total height must be the specified thickness (0.2 mm specified).  

Keeping the top flat can be done by using a piece of ground stock and machining other faces.  Keeping the top and bottom flat to that tolerance should be straightforward for a decent mill.  And a bit of a trick can be employed for the total height.  This is because there are much greater tolerances for the bottom of the part and can therefore add shims of sheet stock (with clearance holes cut) to bring the total assembly to the desired height.  The block can be machined on the bottom face until the thickness is a bit short of the target, then a piece (or pieces) of sheet stock selected to bring it up to the final thickness.  Sheet stock being available in nearly a continuous range of thicknesses makes this much simpler and easier to meet the necessary tolerance than achieving the right thickness with machining only.  

The heat sink block must be made of either aluminum or copper.  Steel, especially stainless, does not have a sufficient heat transfer rate to keep the diodes from overheating.  The presented top blocks were made from aluminum and the shims from copper sheet, which works very well. Do not add heat sink paste or thermal grease to the interface between the laser and the heat sink block.  Those products can offensively outgas, depositing onto critical optics.  The flat surface interface between the diode unit and heat sink does an excellent job of heat transfer already. 

Enclosures keep the lasers away from the unsuspecting eyes of users and the optics protected from bumps and dust.  Drawings and pictures of enclosures for the assembled units are provided.  These include a 'facility-ready' enclosure built from T-slot aluminum and a low-cost version from corrugated plastic. The 2400 enclosure is made of corrugated plastic sheeting and held together with magnets and 3D printed magnet clips (available [here](https://github.com/PRNicovich/3D-Printed-Optics-Lab-Parts/tree/master/Magnet%20Clips)).  In both cases the laser power supplies are on the optical table with the breadboard and lasers above, but there's no reason the whole assembly couldn't be moved to a shelf off of the optical table if space is at a premium. 

Technical drawings for all machined components are included in the Supporting Information.  These drawings were sufficient for the University's machine shop to complete satisfactory work with no additional consultation. 

#### Optics
Optics are relatively straightforward - a few dichroic mirrors, optional filters, and a fiber coupler.  The units presented were built with Thorlabs components and these parts are provided as reference, but similar products from any other maker should be compatible.

Dichroic mirrors are used to combine lasers into a single multi-wavelength beam.  A mix of Chroma and Semrock filters were used and part numbers presented for reference.  All are standard 1" round long-pass filters costing a few hundred USD each.  Which ones are used depends on which wavelengths are being combined.  A filter must have a cut-on wavelength that reflects the beam being folded in and transmits all others upstream.  Picking a cut-on wavelength that falls between adjacent laser lines usually works well, but the provided spectra should be consulted to confirm there aren't odd peaks or dips in the filter spectrum.  

Diode lasers often require an emission clean-up filter to block odd long-wavelength emission from the laser head.  This seems especially prevelant with blue (470-490 nm) diode units.  At the same time, diode heads are ordered by nominal wavelength, but the actual wavelength of the supplied unit may vary 3-5 nm from this nomial value.  This means that when chosing emission clean-up filters, one cannot necessarily order a laser line filter for the nominal wavelength and have it work for a given diode.  For example, the shown '488' nm diodes actually emit at 490 nm, and a 488 nm laser line clean-up filter (Semrock LL01-488-12.5) blocks nearly half of the emitted power.  A 485/10 band-pass filter does a much better job and blocks the offending green emission.  This means one must double-check the actual emitted wavelength (should be specified by the vendor in the factory testing documentation) when chosing an emission filter or if emitted wavelength is critical. 

A Thorlabs PAFA-X-F-A Achromatic FiberPort FC/APC coupler is used to couple the combined laser beams into a polarization-maintaining FC/APC patch cable.  The FiberPorts are somewhat expensive (~$500 USD), but these units have proven to be stable and relatively achromatic.  30-60% coupling efficiency across all lines is achievable without too much struggle on this configuration.

The output of the 2400 was directed into two separate single-mode fibers allowing the lasers to be shared across a pair of excitation paths on the same optical table.  This approach could be particularly adventageous for generating a pair of counter-propogating light sheets to reduce shadowing artefacts in SPIM applications.  Here this is accomplished with a half-wave plate and polarizing beamsplitter immediately in front of the primary fiber coupler.  A pair of steering mirrors guides the beam reflected by the beamsplitter into a second fiber coupler. The half-wave plate is on a rotating mount meaning the division of power between the two fiber outputs can be varied by rotating a single component.  This enhancement requires that all lasers have a common polarization axis (nearly always within a few degrees of vertical for diode lasers) and corresponding quarter-wave plates or other depolarization optics at the fiber output, if the application requires.

A bill of materials for optical components is given in the Hardware folder of the repository and in the Supporting Information.  

###### One mirror or two?
Using two mirrors to steer a beam from one vector to another is taken as dogma for most applications. It is true that this is necessary for arbitrary output and input vectors, but that's not the case here.  

Because lasers with identical beam heights and angle relative to the breadboard are used (assuming proper machining tolerances on the mounting block), one degree of freedom is removed. As such one does not need four adjusters to steer the beam into the fiber coupler, but rather three.  This means a single three-adjuster mirror mount can be used and, as a result, six laser heads and steering optics can be squeezed into a single 450 mm x 300 mm footprint.  This layout is given in the NicoLase 1500 description.  

A more typical two adjuster layout is used in the NicoLase 2400.  This uses a pair of two-adjuster mirrors for each laser and fits four lasers into the 450 mm x 300 mm footprint.  Using two mirrors does have more tolerance for lasers that might have unequal beam heights, such as when mixing Vortran and OBIS diode heads in a single unit. This layout also leaves a portion of the breadboard unused, meaning space is available for the second fiber coupler output path.

Alignment of a single-mirror system is only marginally more difficult than aligning the two mirror system.  The single-mirror system is more stable. 

###### One fiber or two?

A current application demands two optical paths on the same microscope.  With products such as the Nikon stratum structure on the Ti series of microscopes having multiple excitation paths is reasonably straightforward.  In our application we combine a TIRF excitation path with a laser scanner confocal-like path for extended imaging capability on the same chassis, sharing lasers, controllers, objectives, and camera.  The light paths are not going to be used simultaneously (though, in theory, they could be) so a means to selectively divide laser power between one of two exit ports is needed.  

The method employed here uses a polarizing beamsplitter cube in conjunction with a half wave plate on a rotational mount.  Linearly polarized incident light is divided between either the transmitted (P polarized) or reflected (S polarized) light paths depending on the angle of the incident polarization relative to the plane of the beamsplitter face.  Rotating a half wave plate in front of the polarizing beamsplitter rotates the angle of polarization in the incident beam, controlling the division of laser power between the two exit light paths.  This approach has the advantage of maintaining alignment between the two light paths while switching, as opposed to a flip mirror that may not reliably return to the same alignment each time.

Because of the tight space allowed between the final laser combiner mirror and the transmitted fiber output, a custom mount was designed.  This mount holds a 12.5 mm polarizing beamsplitter cube (Thorlabs PBS122) and a 30 mm cage rotational mount (Thorlabs CRM1/M) with an achromatic half wave plate (Newport 10RP52-1).  The mount was 3D printed and set of four 6 mm stainless steel rods are press-fit into the 3D printed part to hold the cage mount.  The mount is held on the breadboard with a standard 1" pedestal mount and at the proper beam height.  A pair of half-inch mirrors on 25 mm pedestals steer the beam to a second fiber launch (due to the imprecision of the beamsplitter mount, two steering mirrors are necessary).  

An optional second rotational mount with a linear polarizer (Newport 5511) can be placed upstream of the half wave plate to add an additional layer of polarization clean-up, though it is difficult to find a polarizer with transmission sufficiently broad to cover the wavelengths used here.

Extinction of over 10,000:1 is possible through the transmitted path with rotation of the half wave plate.  This value falls to ~110:1 for the reflected path, which always has a bit of bleed-through into that port.  It's a testament to the alignment capability that this light reaches the fiber in the first place, even with the imprecision in the printed mount.  The bleed-through into the reflected output path is of little concern in the end as this emission can be hid behind a shutter in that light path.  Laser output power can still be selectively steered into one or the other (or both, if desired) light paths selectively without a large loss as would occur with, say, a 50/50 beamsplitter in the same position, and still with fitting in the same 450 mm x 300 mm footprint. 

#### Controller
Trigger translation, especially with a time-variable component, requires some form of controller.  A DAC board (such as the many options from National Instruments) is the "professional" choice, but are overkill for this application.  A pair input channels and no more than eight output channels are needed. Timing precision, rise times, and trigger jitter on the microsecond or even tens of microseconds time scale is actually more than sufficient here (10 microseconds of jitter on a 10 ms exposure is a 0.1% variation). USB or serial control is convenient to communicate with the controller, as is low cost for the unit and the I/O interface.

Enter the Arduino Uno.  Units cost $40 AUD for an official product (with generic models under $10 AUD).  Timing precision and programming space is sufficient, I/O interfaces are straightforward with an add-on shield, and a USB serial port comes standard. Bonuses are a large community of support and open-source, freely-available software for programming. 

Another Arduino variant or another microcontroller can be swapped in if the need arises (e.g. if the application requires more analog I/O ports, or a 32-bit timer, or additional serial ports) but that's beyond the scope of this project.  As long as the unit can take a master trigger in, output the appropriate pulses, and communicate over a serial port, the exact product can be chosen to meet the needs of the application.

Throughout the discussion on triggering, the catch-all term 'lasers' or 'laser outputs' for the end target for TTL outputs of the controller is used.  Of course this same controller and software can be used with any equipment receiving a TTL input pulse that needs to be synced to camera acquisitions.  For example, a LED illuminator for transmitted light is commonly used with the controller output. 

Interfacing with the trigger pulses in and out is accomplished with a custom shield.  Circuit schematic included in ShieldPCB folder of the repository as well as in the Supporting Information. The PCB includes 10 BNC ports (two camera inputs, two camera outputs, and six laser outputs), a OR gate, 3v3 to 5v level shifter, and pushbutton, plus a few pull-down and limiting resistors.  The shield mounts directly on top of the Arduino. The Arduino and shield is powered over the USB port and does not require a separate power supply.  

PRIMARYTRIGGER camera input drives the SECONDARYTRIGGER output high as well as one input of the 3v3 OR gate.  This allows the master camera to directly trigger a frame capture on the slave camera for dual-camera acquisitions (on single-camera systems the slave camera ports are left empty). The other OR gate input is SECONDARYFIRE input, meaning either camera can be used singly to trigger the Arduino.  The OR gate output connects through a 3v3->5v level shifter to ensure that the pulse adequately triggers 5v input on the Arduino.  There the 5v pulse connects to interrupt digital pin 2 of the Arduino.  Arduino digital pin 3 connects to pushbutton on PCB which can also be used to iterate laser sequence for testing in place of a camera trigger.  Digital pin 4 goes through a current-limiting resistor to the PRIMARYTRIGGER output. 

All TTL outputs occur on Analog Out register of Arduino Uno.  Sharing a register means all can be set simultaneously in the Arduino software without having to go through the digitalWrite() function.  This allows faster triggering and all outputs are set with the same delay/jitter rather than a variable delay depending on which pin on the register they lie.  Outgoing pulses are all 5v TTL levels.  Incoming signals are anticipated to be 3v3 TTL signals with the pins tolerant to 5v signals. 

A bill of materials included in the repository for the parts necessary to populate the shield.  Surface mount components are used to fit into the space available.  All should be common components available from any major electronics supplier. 

### Software
Software is split into two parts - controller software running on the Arduino and interface software for the PC.  Only the controller software is strictly necessary for triggering.  Examples of PC software are given which can be expanded to work with your particular system.

###### Controller Software
The Arduino sketch accomplishes two primary needs - external communication and sequence programming via serial port, and triggering + interating a programmed output sequence in response to a trigger pulse.  The output sequence is an N x 6 binary array, with each line corresponding to the output configuration that will be adopted on a input trigger pulse.  A change in the trigger pin state triggers an interrupt on the Arduino. A rising edge causes the outputs (CH1 through CH6) to adopt the current line of the output sequence.  A falling edge on the trigger pulse drives all outputs low and iterates the sequence program counter. Any given line of the sequence can be repeated any number of times (up to 2147483647 times per line) before proceeding to the next line if programmed by the user. Once the end of the sequence is reached the Arduino goes back to the start and repeats indefinitely. 

Serial commands are via the USB connection to the control PC. Upon startup, the Arduino adopts the default output sequence (i.e. all outputs high when the fire signal is high).  It also initializes communication with the instrument PC, listening for commands coming over the serial port.   A detailed description of the possible serial commands is given in the documentation in the Arduino Sketch folder of the repository.  Each command is a capital letter character specifying the command type, in some cases a space followed by one or more digits, and always terminated by a newline character.  For example, the command 'B 000111\n' will append an entry to the current output sequence where channels 1-3 (CH1, CH2, and CH3) are high and channels 4-6 (CH4, CH5, and CH6) low.  

Alternate functions are available for what is called a pre- or post-acquisition command.  These are single output patterns that can be triggered by a serial command independent of a trigger input.  Each has a time associated for how long the outputs will be held high.  The pre-acquisition sequence has an additional delay after which an optional pulse will be send to the PRIMARY TRIGGER pin.  This means the user can, for example, have an acquisition sequence of a 488 nm laser and 561 nm laser firing on alternating camera frames (the 'acquisition sequence').  Before that sequence is reached, however, a pre-acquisition sequence of a 405 nm laser is fired with no camera acquisition for 1000 ms, followed by a 500 ms delay, and then the actual acquisition begun by the Arduino sending a pulse to the master camera.  A more detailed description of the 
triggering and associated controller functions is given in the Triggering section.  

###### PC Software
Software on the PC side is used to alter the sequence on the Arduino controller. Any serial terminal can be used and it is straightforward to include the controller as a device in instrument control software supporting serial communication.

Example Micro-manager scripts to communicate output sequence settings are given for inclusion into the Beanshell interface.  The Arduino is added to the Micro-manager config as a FreeSerialPort device for this communication.  The sendSerialPortCommand() command passes along strings to the Arduino at the beginning of the experiment (or during, if desired) which then sets the Arduino sequence configuration and waits for trigger pulses from the camera.  

A MATLAB GUI for easily uploading arbitrary sequences is also included.  This can run in parallel with Micro-manager if the FreeSerialPort has not been added to the Micro-manager config, or in an another external application.

### Triggering
A diagram of the triggering sequence is given in Figure \TRIGGER:

[TRIGGERING DIAGRAM]

Here input signals are blue (Primary In and Secondary In), outputs to cameras in red (Primary Out and Secondary Out), and outputs to lasers in wavelength-approximate colors (Ch6 - Ch1).  Trigger event stages are separated by dashed black lines with acquisition frames in gray blocks with gray dotted borders.

Because the SECONDARYFIRE and PRIMARYFIRE inputs first pass through an OR gate, either being high results in the input to the Arduino being high.  In practice the separation between these two pulses is on the order of single microseconds, so these are treated as simultaneous.  Also, this means that even in a dual-camera system, running either camera alone is sufficient to trigger the CHX outputs.  Single-camera systems can work on either input, though using PRIMARYFIRE input is recommended.

The progression of trigger signals for this experiment of 8 imaging frames is as follows:
-  Serial command to Arduino starts Pre-acquisition sequence with Ch1 and Ch6 high (ie 405 nm laser and blue transmitted LED)
-  After defined illumination time, Pre-acquisition ends and Pre-acquisition delay begins
-  After defined pre-acquisition delay time, a pulse is sent to the PRIMARYTRIGGER output
-  The Primary camera begins acquiring frames (charcteristics defined through camera software).  Each is associated with an input pulse at the PRIMARYFIRE pin
-  Each incoming PRIMARYFIRE pulse results in an output SECONDARYTRIGGER pulse and output pulses to the user-specified CHX output pins.  Here alternating sequences of [Ch5 + Ch4] and [Ch3 + Ch2] are shown to illustrate the programmable sequencing of these pins. 
- Each SECONDARYTRIGGER pulse triggers the slave camera, which in turn results in an incoming SECONDARYFIRE pulse (ignored in 2-camera acquisitions)
- Falling edge of PRIMARYFIRE pulse drives CHX outputs low and Arduino iterates to next line of sequence
- After acquisition is complete, serial command to Arduino starts Post-acquisition sequence (here Ch6, blue transmitted LED high) for user-defined time

This experimental framework covers a large number of potential applications by expanding the triggering and sequencing options.  This should provide a scheme to control a number of imaging instrumentation devices and hopefully aids other researchers in creating their own application-specific designs. 

### Conclusion

The motivation and implementation of two generations of a diode laser combiner, fiber launch, and controller.  The designs utilize commonly-available components to yield a low-cost solution for multi-wavelength imaging.  Software for syncing multiple camera triggers with programmable laser sequencing is included.  All design files, machine shop drawings, parts lists, and software are openly available for users to clone, modify, and share through the authors' GitHub repo.  Together this provides an open platform for an aspect of fluorescence microscopy previously left to ad-hoc or closed-source commercial instruments.


FIGURES:
-  + pictures
- Optical layout + pictures
- Circuit diagram
- Triggering sequence




