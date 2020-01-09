#include <Wire.h>
#include <LiquidCrystal.h>
 
LiquidCrystal lcd(12, 11, 5, 4, 3, 2);
 
const int colorR = 255;
const int colorG = 0;
const int colorB = 0;
 
float sensor_volt;
float RS_gas; 
float R0;
float ratio;
float BAC;
int R2 = 2000;
 
void setup() 
{
    lcd.begin(16, 2); 
//    lcd.begin(16, 2);
//  lcd.setCursor(0,1);
//  lcd.write("JiMA.in");
    Serial.begin(9600);
}
 
void loop() 
{
    double sensorValue = analogRead(A0);
    float sensorValue1 = analogRead(3)/100.0;
    sensor_volt=(float)sensorValue/1024*5.0;
    RS_gas = ((5.0 * R2)/sensor_volt) - R2; 
   /*-Replace the value of R0 with the value of R0 in your test -*/
    R0 = 16000;
    ratio = RS_gas/R0;// ratio = RS/R0
    double x = 0.4*ratio;   
    BAC = (pow(x,-1.431))*100;  //BAC in mg/L
    lcd.setCursor(0,0);
    lcd.print("BAC = ");
    lcd.print(BAC);  //convert to ppm
    lcd.print(" PPM");
    delay(500);
    lcd.setCursor(0,1);
    lcd.print("CO2: ");
    lcd.print(sensorValue1);  
    lcd.print(" % VOL");
    delay(500);
    
    Serial.print("BAC = ");
    Serial.print(BAC);
    Serial.print(" ppm, CO2:");
    Serial.print(" CO2:");
    Serial.print(sensorValue1);
    Serial.print(" VOL.");
    Serial.print("\n");
    
    delay(1000);
}
