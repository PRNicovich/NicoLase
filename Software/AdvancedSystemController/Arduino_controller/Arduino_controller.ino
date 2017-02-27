#include "MyHeaders.h"


bool arduino_master = false;
bool currentlyfiring=false;

String inputString = "";         // a string to hold incoming data
boolean stringComplete = false;  // whether the string is complete

const int triggerPin = 2;
const int outPin = 4;

unsigned long debounceDelay = 5;
unsigned long debouncestarttime;


mainloop ml;

void setup() {
  // put your setup code here, to run once:

    delay(2000);
    pinMode(outPin, OUTPUT);
    pinMode(triggerPin, INPUT);//initialize trigger pins
    
    inputString.reserve(256);
        
    DDRC = B11111111;//set the laser pins to output

    attachInterrupt(digitalPinToInterrupt(triggerPin), turnofflaser,  FALLING);
    
    Serial.begin(9600);
    while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
    }


    ml.clearall();
}

void loop() {
    if(arduino_master)ml.updatemain(millis());//arduino controlled
    else {//camera controlled
          if(currentlyfiring==false && digitalRead(triggerPin)==HIGH){
                PORTC=B11111111;
                digitalWrite(outPin, HIGH);
                currentlyfiring=true;
                debouncestarttime=millis();
          }
    }
    if(digitalRead(outPin)==HIGH && ((millis()-debouncestarttime)>=debounceDelay)||millis()<debouncestarttime){ //Once the camera signal has been sent and enough time has passed  -  stop sending it;
                digitalWrite(outPin, LOW);      
          }

  
    if(stringComplete)dealwithinput();//serial commands
}

void turnofflaser(){
       PORTC=B00000000;
       digitalWrite(outPin, LOW);
       currentlyfiring=false; 
}

void serialEvent() {

  while (Serial.available()) {
    // get the new byte:
    char inChar = (char)Serial.read();
    // add it to the inputString:
    
    if (inChar == '\n') {
      stringComplete = true;
      break;
    }
    inputString += inChar;
    // if the incoming character is a newline, set a flag
    // so the main loop can do something about it:
  }
}

void dealwithinput(){

     //Serial.println("Input:");
    // Serial.println(inputString);
     int intinput;
     char firstChar = char(inputString[0]);
     inputString=inputString.substring(1);
     if(firstChar=='A'||firstChar=='a')ml.addlasersequence(inputString);
     else if(firstChar=='M'||firstChar=='m'){
          intinput= inputString.toInt();
          if(intinput==0){arduino_master = false;Serial.println("Triggering from camera");}
          else if(intinput==1){ml.runbool=false;arduino_master = true;Serial.println("Using arduino as master controller");}
          else Serial.println("Error updating mode , please try again");
     }
     else if(firstChar=='N'||firstChar=='n')ml.setloops(inputString);
     else if(firstChar=='r'||firstChar=='R'){turnofflaser();ml.clearall();arduino_master = false;Serial.println("Reset");}
     else if(firstChar=='S'||firstChar=='s'){Serial.println("Beginning to run");ml.runmain(millis());}
     else if(firstChar=='Z'||firstChar=='z'){turnofflaser();ml.runbool=false;}
    inputString = "";
    stringComplete = false;
}
