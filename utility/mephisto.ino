/*
Header - Romain Michon 2013
*/

struct oscPar{
  const char* address[5];
  float minVal[5];
  float maxVal[5];
  float sensorsMinVal[5];
  float sensorsMaxVal[5];
  int rate;
  int udp[4];
  String ssid;
  String pswd;
};
oscPar oscParams;

#include <OSCMessage.h>
#include <SPI.h>
#include <WiFi.h>
#include <WiFiUdp.h>
#include <LiquidCrystal.h>
#include <EEPROMEx.h>

int status = WL_IDLE_STATUS;
int selNet = 0;
//char ssid[] = "CCRMA"; //  your network SSID (name) 
//String dudu = "dfgfd";
//char pass[] = "secretPassword";    // your network password (use for WPA, or use as key for WEP)
//int keyIndex = 0;            // your network key Index number (needed only for WEP)
String udpAd = "0.0.0.0";
char udpAdSend[16];

struct userPar{
  int udp[4];
  String ssid;
};
userPar userParams;

// initialize the LCD screen
LiquidCrystal lcd(9, 8, 6, 5, 3, 2);

int old = 0;
int numSsid = 0;
int host[4];

WiFiUDP Udp;

void setup() {

<<< params >>>
  
  // set up the LCD's number of columns and rows: 
  lcd.begin(16, 2);
  lcd.print("Welcome!");
  
  // Pins for the navigation controller
  pinMode(0, INPUT);
  pinMode(1, INPUT);
  
  //Initialize serial and wait for port to open:
  Serial.begin(9600); 
  
  // check for the presence of the shield:
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present"); 
    // don't continue:
    while(true);
  } 
  
  boolean selected = true, tester, std = false;

  lcd.setCursor(0, 0);
  lcd.print("Std. params?");
  while(selected){
    if(analogRead(5) >= 400 && analogRead(5) < 440){
      tester = true;
      selected = false;
    }
    if(analogRead(5) >= 280 && analogRead(5) < 300){
      std = true;
      tester = false;
      selected = false;
    }
    delay(100);
  }
  
  if(tester == true){
    listNetworks();
    Serial.println("Please, choose one of them...");
    lcd.setCursor(0, 0);
    lcd.print("Choose one:");
 
    selected = true; 
    tester = true; 

    int thisNet = 0;
    while(selected){
      if(tester){
        Serial.print(thisNet);
        Serial.print(") ");
        Serial.println(WiFi.SSID(thisNet));
        lcd.setCursor(0, 1);
        lcd.print(thisNet);
        lcd.print(") ");
        lcd.print(WiFi.SSID(thisNet));
        lcd.print("                ");
        //userParams.ssid = WiFi.SSID(selNet);
        tester = false;
      }
      // TODO Well, have to think about it
      if(analogRead(5) >= 325 && analogRead(5) < 350 && thisNet>0){
        thisNet--;
        tester = true;
      }
      if(analogRead(5) >= 490 && analogRead(5) < 540 && thisNet<numSsid){
        thisNet++;
        tester = true;
      }
      if(analogRead(5) >= 200 && analogRead(5) < 250){ 
        selNet = thisNet;
        selected = false;
      }
      delay(200);
    }
    oscParams.ssid = WiFi.SSID(selNet);
  }
  
  // attempt to connect to Wifi network:
  while ( status != WL_CONNECTED) {
    char ssid[oscParams.ssid.length()+1];
    oscParams.ssid.toCharArray(ssid,oscParams.ssid.length()+1); 
    //char ssid[] = "CCRMA";
    Serial.print("Connecting to ");
    Serial.println(ssid);
    lcd.setCursor(0, 0);
    lcd.print("Connecting to");
    lcd.setCursor(0, 1);
    lcd.print(ssid);
    lcd.print("               ");
    // Connect to WPA/WPA2 network. Change this line if using open or WEP network:    
    //status = WiFi.begin(WiFi.SSID(selNet));
    status = WiFi.begin(ssid);
  
    // wait 10 seconds for connection:
    delay(10000);
  } 
  Serial.println("Connected to wifi");
  lcd.setCursor(0, 0);
  lcd.print("Success!        ");
  lcd.setCursor(0, 0);
  printWifiStatus();
  delay(2000);
  if(std == false){
  lcd.setCursor(0, 0);
  lcd.print("Host:           ");
  
  selected = true;
  int pos = 0;
  boolean flash = true;
  
  while(selected){
    //TODO control disabled for now
    if(analogRead(5) >= 490 && analogRead(5) < 540 && oscParams.udp[pos]>0){
      oscParams.udp[pos]--;
    }
    if(analogRead(5) >= 325 && analogRead(5) < 350 && oscParams.udp[pos]<256){ 
      oscParams.udp[pos]++;
    }
    if(analogRead(5) >= 400 && analogRead(5) < 440 && pos>0){ 
      pos--;
    }
    if(analogRead(5) >= 280 && analogRead(5) < 310 && pos<3){ 
      pos++;
    }
    if(analogRead(5) >= 200 && analogRead(5) < 250){
      selected = false;
    }
    
    lcd.setCursor(0, 1);
    if(pos == 0 && flash) lcd.print(oscParams.udp[0]);
    else if(pos == 0 && flash == false) lcd.print("");
    else lcd.print(oscParams.udp[0]);
    lcd.print(".");
    if(pos == 1 && flash) lcd.print(oscParams.udp[1]);
    else if(pos == 1 && flash == false) lcd.print("");
    else lcd.print(oscParams.udp[1]);
    lcd.print(".");
    if(pos == 2 && flash) lcd.print(oscParams.udp[2]);
    else if(pos == 2 && flash == false) lcd.print("");
    else lcd.print(oscParams.udp[2]);
    lcd.print(".");
    if(pos == 3 && flash) lcd.print(oscParams.udp[3]);
    else if(pos == 3 && flash == false) lcd.print("");
    else lcd.print(oscParams.udp[3]);
    lcd.print("          ");
    if(flash) flash = false;
    else flash = true;

    delay(300);
    }
    
    // TODO: Faic Select
    /*
    delay(2000);
    host[0] = 192;
    host[1] = 168;
    host[2] = 178;
    host[3] = 237;
    /selected = false;
    */
  }
  
  delay(100);
  
  udpAd = (String) oscParams.udp[0] + "." + (String) oscParams.udp[1] + "." + (String) oscParams.udp[2] + "." + (String) oscParams.udp[3];
  udpAd.toCharArray(udpAdSend,16);
  
  //userParamsuserParamsoscParams.udp[0] = ;
  EEPROM.writeBlock(0, userParams);
  
  userPar userParams2;
  EEPROM.readBlock(0, userParams2);
  Serial.println("SSID: ");
  Serial.println(oscParams.ssid);
  Serial.println("UDP: ");
  Serial.println(oscParams.udp[0]);
  
  Serial.println("Sending OSC to:");
  Serial.println(udpAd);
  
  lcd.setCursor(0, 0);
  lcd.print("Sending OSC to");
  lcd.setCursor(0, 1);
  lcd.print(udpAd);
  lcd.setCursor(0, 0);
}

void loop() {
  sendOSCMess(oscParams.address[0], 5511, 0);
  sendOSCMess(oscParams.address[1], 5511, 1);
  sendOSCMess(oscParams.address[2], 5511, 2);
  sendOSCMess(oscParams.address[3], 5511, 3);
  sendOSCMess(oscParams.address[4], 5511, 4);
  delay(oscParams.rate);
}

void sendOSCMess(const char* address, int port, int numb){
  OSCMessage msg(address);
  //Sensor type scaling
  float messVal = (float) (analogRead(numb) - oscParams.sensorsMinVal[numb])/(oscParams.sensorsMaxVal[numb] - oscParams.sensorsMinVal[numb]); 
  //Scaling in function of the desired output values
  messVal = messVal*(oscParams.maxVal[numb] - oscParams.minVal[numb]) + oscParams.minVal[numb];
  msg.add(messVal);
  Udp.beginPacket(udpAdSend, port);
  msg.send(Udp);
  Udp.endPacket();
  msg.empty();
}

void listNetworks() {
  // scan for nearby networks:
  delay(1000);
  Serial.println("** Scan Networks **");
  lcd.setCursor(0, 0);
  lcd.print("Scanning        ");
  lcd.setCursor(0, 1);
  lcd.print("Networks...     ");
  lcd.setCursor(0, 0);
  numSsid = WiFi.scanNetworks();
  if (numSsid == -1)
  {
    Serial.println("Couldn't get a wifi connection");
    lcd.setCursor(0, 0);
    lcd.print("No Wifi");
    lcd.setCursor(0, 1);
    lcd.print("Networks");
    while(true);
  }

  // print the list of networks seen:
  Serial.print("number of available networks:");
  Serial.println(numSsid);
  delay(100);
  lcd.setCursor(0, 0);
  lcd.print(numSsid);
  lcd.print(" networks");
  lcd.setCursor(0, 1);
  lcd.print("found!     ");
  delay(3000);

  // print the network number and name for each network found:
  for (int thisNet = 0; thisNet<numSsid; thisNet++) {
    Serial.print(thisNet);
    Serial.print(") ");
    Serial.print(WiFi.SSID(thisNet));
    Serial.print("\tSignal: ");
    Serial.print(WiFi.RSSI(thisNet));
    Serial.println(" dBm");
  }
}

void printWifiStatus() {
  // print the SSID of the network you're attached to:
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print your WiFi shield's IP address:
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}

/*
float roundTo(float value, int depth){
  int tmp;
  float result;
  tmp = (int) value * depth;
  result = (float) tmp / depth;
  return result;
}
*/
