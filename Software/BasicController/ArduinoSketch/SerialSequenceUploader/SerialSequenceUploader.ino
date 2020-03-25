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

// Version variables
const String version = "1.2";

// Serial variables
String inputString = "";         // a string to hold incoming data
boolean stringComplete = false;  // whether the string is complete
long readVal = 0;
boolean inputOK = false;

// LED Variables
int seqArray[64] = {0x00};
long seqRepeat[64] = {0};
int cycleNum = 0;
int repeatNow = 0;
volatile int repeatAddCount = 0;
int arraySize = 0;
int preIllumSeq = {0x00};
unsigned long preIllumTime = 0;
int postIllumSeq = {0x00};
unsigned long postIllumTime = 0;
volatile int digitCount = 1;

boolean shutterState = false;

// Incoming trigger variables
const int triggerPin = 2;
boolean LEDToggle = false;

// Post-acquisition trigger variables
const int outPin = 4;
boolean followSeq = false;
long preAcqToCamDelay = 0; 

// Button Variables 
const int buttonPin = 3;
boolean lastButtonState = false;
boolean LEDState = false;
int buttonState;
long lastDebounceTime = 0;
long debounceDelay = 50;

// ------------------------------------------------------ //

void setup() {

  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial connection
  }
  inputString.reserve(64);

  // Initialize as all on
  seqArray[0] = 0x3F;
  seqRepeat[0] = 1;
  arraySize = 1;

  PORTC = 0x00;

  attachInterrupt(digitalPinToInterrupt(triggerPin), ToggleLED,  CHANGE);
  pinMode(buttonPin, INPUT);
  pinMode(outPin, OUTPUT);
  digitalWrite(outPin, LOW);
  DDRC = B11111111;

}

// ------------------------------------------------------ //

void loop() {

  int read = !digitalRead(buttonPin);
  if (read != lastButtonState) {
    lastDebounceTime = millis();
  }

  if ((millis() - lastDebounceTime) > debounceDelay) {
    if (read != buttonState) {
      buttonState = read;
      if (buttonState == HIGH) {
        LEDState = true;
       // Serial.println(LEDState);
        ToggleLED();
      }
      else if (buttonState == LOW) {
        LEDState = false;
       // Serial.println(LEDState);
        ToggleLED();
      }
    }
  }

  lastButtonState = read;
 
  if (stringComplete) {

    // Serial.println(inputString);

    char firstChar = char(inputString[0]);

    switch (firstChar) {

      case ('A') :

        Serial.println("Append Sequence");
        Serial.println(inputString.length());
        Serial.println(inputString);
        readVal = 0;
        inputOK = false;
        if (inputString.length() == 10) {
        
          sequenceStringToIntegers();

        }
        else {
            Serial.println("Bad input. Incorrect number of characters.");
            inputOK = false;
          } 

        if (inputOK) {
            seqArray[arraySize] = readVal;
            Serial.print("Appended B");
            Serial.println(seqArray[arraySize], BIN);
            arraySize++;
          }

        break;

      case ('B') :

        Serial.println("Configure pre-acquistion sequence");
        Serial.println(inputString.length());
        Serial.println(inputString);
        readVal = 0;
        inputOK = false;
        if (inputString.length() == 10) {
        
          sequenceStringToIntegers();

        }
        else {
            Serial.println("Bad input. Incorrect number of characters.");
            inputOK = false;
          } 

          if (inputOK) {
            preIllumSeq = readVal;
            Serial.print("Pre-acquisition B");
            Serial.println(preIllumSeq, BIN);
          }

        break;

      case ('C') :

        Serial.println("Clear Sequence");
        
        for (int i = 0; i < arraySize; i++) {
          seqArray[i] = 0x00; // set all values to 0
        }
        
        arraySize = 0;
        repeatNow = 0;

        preIllumSeq = {0x00};
        preIllumTime = 0;
        postIllumSeq = {0x00};
        postIllumTime = 0;
        
        PORTC = 0x00;
        
        break;

      case ('D') :

        Serial.println("Configure post-acquistion sequence");
        Serial.println(inputString.length());
        Serial.println(inputString);
        readVal = 0;
        inputOK = false;
        if (inputString.length() == 10) {
        
          sequenceStringToIntegers();

        }
        else {
            Serial.println("Bad input. Incorrect number of characters.");
            inputOK = false;
          } 

          if (inputOK) {
            postIllumSeq = readVal;
            Serial.print("Post-acquisition B");
            Serial.println(postIllumSeq, BIN);
          }

        break;

      case ('E') :

        Serial.println("Echo Sequence");
        
        for (int i = 0; i < arraySize; i++) {
  
          if (seqArray[i] < 15){
          Serial.print("0x0");
          Serial.println(seqArray[i], HEX);
          Serial.println(seqRepeat[i], DEC);
          }
          else {
          Serial.print("0x");
          Serial.println(seqArray[i], HEX);
          Serial.println(seqRepeat[i], DEC);
          }
        }
        
        break;

      case ('F') :

        Serial.println("Full on");
        
        seqArray[0] = 0x3F; // Set so all 6 LEDs flash
        seqRepeat[0] = 1;
        arraySize = 1;
        
        break;

      case ('G') :

        // Set pre-acquisition illumination time 
        // G long (ms of exposure time, max 2^32 - 1, or just shy of 50 days)
        // Timing of this handled by Arduino, not by host PC
  
        Serial.print("Set pre-acquisition time to :");
        
        serialInputToLong();
        
        if (!inputOK) {
          Serial.println("");
          Serial.println("Invalid value.  Setting pre-acquisition time to 0");
          preIllumTime = 0;
        }
        else {
          preIllumTime = readVal;
          Serial.println(preIllumTime);
        }
        
        break;

      case ('H') :

        // Set post-acquisition illumination time 
        // H long (ms of exposure time, max 2^32 - 1, or just shy of 50 days)
        // Timing of this handled by Arduino, not by host PC, nor synced to camera 
        
  
        Serial.print("Set post-acquisition counter to :");

        serialInputToLong();
        

        if (!inputOK) {
          Serial.println("");
          Serial.println("Invalid value.  Setting pre-acquisition time to 0");
          postIllumTime = 0;
        }
        else {
          postIllumTime = readVal;
          Serial.println(preIllumTime);
        }
        
        break;

      case ('I') :
        // Set whether or not a pre-acquisition sequence is followed by an output trigger on the
        // outPin.
        // Input is '1' or '0' character

        if (inputString.length() == 4) {
        
          Serial.println(inputString);

            if (inputString[2] == 48) {
              followSeq = false;
            }
            else if (inputString[2] == 49) {
              followSeq = true;
            }
            else {
              Serial.println("Incorrect character for trigger flag.");
            }
            Serial.print("Pre-acquisition cam trigger flag set to: ");
            Serial.println(followSeq, BIN);
          }
        
        
        break;

      case ('J') :
        // Set delay time (in ms) between post-acquisition sequence completion and, if set, 
        // the outgoing pulse to outPin.
        // Input is long integer

        Serial.print("Set pre-acqisition to cam trigger delay to :");

        serialInputToLong();
        

        if (!inputOK) {
          Serial.println("");
          Serial.println("Invalid value.  Setting pre-acquisition time to 0");
          preAcqToCamDelay = 0;
        }
        else {
          preAcqToCamDelay = readVal;
          Serial.println(preAcqToCamDelay);
        }

        break;
        

      case ('K') : 
        // Execute pre-acquisition illumination pattern for G ms
        // Quick-and-dirty using delay() to do the timing.
        // This locks out other serial input (right?) and button during this time
        // but that shouldn't ever be a problem in this application.
        // Otherwise need to move this execution to a function that gets called repeatedly within the loop

        Serial.println("Pre-acquisition Engage");
        PORTC = preIllumSeq;
        delay(preIllumTime);
        PORTC = 0x00;

        if (followSeq) {
          delay(preAcqToCamDelay);
          digitalWrite(outPin, HIGH);
          delay(10);
          digitalWrite(outPin, LOW);
        }


        break;
    
      case ('L') : 
        // Execute post-acquisition illumination pattern for H ms
        // As with K, this stalls loop for H ms, excluding any button pushes or non-interrupt input.
        // Shouldn't cause problems here.

        Serial.println("Post-acquisition Engage");
        PORTC = postIllumSeq;
        delay(postIllumTime);
        PORTC = 0x00;


        break;

       case ('M') :

        Serial.println("Clear and set sequence");
        Serial.println(inputString.length());
        Serial.println(inputString);

        // Clear old sequence and info
        // Equivalent to 'C' command
        for (int i = 0; i < arraySize; i++) {
          seqArray[i] = 0x00; // set all values to 0
        }
        
        arraySize = 0;
        repeatNow = 0;

        preIllumSeq = {0x00};
        preIllumTime = 0;
        postIllumSeq = {0x00};
        postIllumTime = 0;

        // Append input to sequence.
        // Equivalent to 'A BXXXXXX'
        
        readVal = 0;
        inputOK = false;
        if (inputString.length() == 10) {
        
          sequenceStringToIntegers();

        }
        else {
            Serial.println("Bad input. Incorrect number of characters.");
            inputOK = false;
          } 

        if (inputOK) {
            seqArray[arraySize] = readVal;
            Serial.print("Appended B");
            Serial.println(seqArray[arraySize], BIN);

            if (shutterState) {

              PORTC = seqArray[0];
            }
            
            arraySize++;
          }

        break; 

      case ('N') :

        Serial.println("Set repeat numbers");
        Serial.println(inputString);
        readVal = 0;
        if (inputString.length() > (arraySize)) {
        repeatAddCount = 0;
          for (int i = 2; i < inputString.length(); i++) {
         //   Serial.println(inputString[i], DEC);
            if (inputString[i] > 47) {
              
              // Numeral
              
              readVal *= 10;
              readVal = (inputString[i]-48) + readVal;
              
             // Serial.println(readVal);
            }

            else if (inputString[i] == 32) {
              // space, do nothing
            }
            
            else if (inputString[i] == 44) {
              // comma, separating values + terminating string

              seqRepeat[repeatAddCount] = readVal;
            //  Serial.println(readVal);
              repeatAddCount++;
              readVal = 0;
                
            } // if comma

            else if (inputString[i] == 10) {
              // Newline
              // Exit loop thanks to end of string
              readVal = 0;
              break;
            }

            else {
              // Bad value in serial input
              // Exit while loop
              Serial.println("Bad input. Comma-separated long integers allowed.");
              break;
            }
          
          } // For characters
         
        } // If string correct length
        

        else {
            Serial.println("Bad input. Incorrect number of characters.");
            inputOK = false;
        } 
        
        break; 

      case ('O') :

        Serial.println("Turn off all outputs");
        shutterState = false;
        PORTC = 0x00;

        break;

      case ('P') :

        // Set sequence to fire each LED Blue -> Red 
        // Here so there's a test pattern to have already setup
        
        Serial.println("Adopt pre-programmed sequence");
        seqArray[0] = 0x01;
        seqArray[1] = 0x02;
        seqArray[2] = 0x04;
        seqArray[3] = 0x08;
        seqArray[4] = 0x10;
        seqArray[5] = 0x20;

        seqRepeat[0] = 1;
        seqRepeat[1] = 1;
        seqRepeat[2] = 1;
        seqRepeat[3] = 1;
        seqRepeat[4] = 1;
        seqRepeat[5] = 1;
        
        arraySize = 6;
        
        break;

      case ('Q') : 

        Serial.println("Turn on first sequence");
        shutterState = true;
        detachInterrupt(digitalPinToInterrupt(triggerPin));
        PORTC = seqArray[0];

        break;

      case ('R') :

        // Reset counter to first index
        Serial.print("Reset sequence counter");
        cycleNum = 0;
        repeatNow = 0;
        break;       

      case ('S') :

        // Set counter to input value
        // S XX
  
        Serial.print("Set sequence counter to :");
        
        serialInputToLong();
        
        if (!inputOK) {
          Serial.println("Invalid value.  Setting counter to 1");
          cycleNum = 1;
          repeatNow = 0;
        }
        else {
          cycleNum = readVal;
          repeatNow = 0;
          Serial.println(cycleNum);
        }
        
        break;

      case ('T') :

        Serial.println("Test sequence");
        for (int i = 0; i < arraySize; i++) {
          PORTC = seqArray[i];
          delay(500);
        }
        PORTC = 0x00;
        break;

      case ('U') :

          for (int i = 0; i < arraySize; i++) {
  
          if (seqArray[i] < 15){
          Serial.print("0x0");
          Serial.println(seqArray[i], HEX);
          }
          else {
          Serial.print("0x");
          Serial.println(seqArray[i], HEX);
          }
        }

        break;
		
	  case ('V') :
		// Return version via serial port
		Serial.print("Version : ");
		Serial.println(version);
		
		break;

      case ('W') :

        Serial.println("Directly on");
        Serial.println(inputString.length());
        Serial.println(inputString);
        readVal = 0;
        inputOK = false;
        if (inputString.length() == 10) {
        
          sequenceStringToIntegers();

        }
        else {
            Serial.println("Bad input. Incorrect number of characters.");
            inputOK = false;
          } 

        if (inputOK) {
            PORTC = readVal;
            detachInterrupt(digitalPinToInterrupt(triggerPin));
          }

        break;

      case ('X') :

        // Turn off any on pins in previous array
        // reattach interrupt to input pin
        Serial.print("Reset and reattach interrupt");
        cycleNum = 0;
        repeatNow = 0;
        PORTC = 0x00;
        attachInterrupt(digitalPinToInterrupt(triggerPin), ToggleLED,  CHANGE);
        
        break;  

	   case ('Y') : 
		// Return device ID 
		Serial.println("NicoLaseSequencer");
		break;


      case ('?') : 

        // Query device state
        // State depends on next character

        switch (inputString[2]) {

          case ('S'):

          // Return current shutter state
            if (shutterState){
              Serial.println("1");            
            }
            else {
              Serial.println("0");
            }

            break;
            
        }

        break;
	
	  default :
	  // Command not recognized. Return error character 0x15.
	      Serial.println(15, HEX);
		  
		break;



       
    }
    
    inputString = "";
    stringComplete = false;
    
  }

}

/*
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/
void ToggleLED() {

    while (seqArray[cycleNum] == 0) { 
      // fast-forward to next non-Null value
      cycleNum++;
        if (cycleNum >= arraySize) {
          cycleNum = 0;
          repeatNow = 0;
        }
    }
  
   if (LEDToggle) {
      PORTC = seqArray[cycleNum];
     // PORTC = 0xFF;
      
      repeatNow++;
      
      if (repeatNow >= seqRepeat[cycleNum]) {
       cycleNum++;
       repeatNow = 0;
       if (cycleNum >= arraySize) {
          cycleNum = 0;
        }
      }

   }
     
     else {
      PORTC = 0x00;
     }

     LEDToggle = !LEDToggle;
}

/*
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/

void sequenceStringToIntegers() {
          for (int i = 3; i < 9; i++) {
            if ((inputString[i] == 48) || (inputString[i] == 49)) {
        
              readVal *= 2;
              readVal = (inputString[i]-48) + readVal;
              if (i == 8) {
                inputOK = true;
              } // if last input
            } // if 1 or 0

            else {
              // Bad value in serial input
              // Exit while loop
              Serial.println("Bad input. Char 1 or 0 allowed.");
              inputOK = false;
              break;
            }
          }
        }

/*
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/

void serialInputToLong() {
        Serial.println(inputString);
        readVal = 0;
        inputOK = false;
        digitCount = 0;
        for (int i = 2; i < inputString.length(); i++) {

          if ((inputString[i] >= 48) & (inputString[i] < 58)) {
            readVal = readVal*10;
            readVal = readVal + (inputString[i] - 48);
        Serial.println(readVal);
            digitCount++;
          } // if is valid ascii numeral
        } // for loop over valid characters

        if (digitCount == (inputString.length()-(1+2))) {
          inputOK = true;
        }
}

/*
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/

void serialEvent() {

  while (Serial.available()) {
    // get the new byte:
    char inChar = (char)Serial.read();
    // add it to the inputString:
    inputString += inChar;
    // if the incoming character is a newline, set a flag
    // so the main loop can do something about it:
    if (inChar == '\n') {
      stringComplete = true;
      //Serial.println("Input:");
    /*  Serial.println(inputString);
      Serial.println("Size:");
      Serial.println(inputString.length()-1);
      */
    }
  }
}
