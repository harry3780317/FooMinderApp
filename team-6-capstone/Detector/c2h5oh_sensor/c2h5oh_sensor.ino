#include <Wire.h>
#include <LiquidCrystal.h>
 
LiquidCrystal lcd(7, 8, 9, 10, 11 , 12);
 
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
}
 
void loop() 
{
    int sensorValue = analogRead(A0);
    sensor_volt=(float)sensorValue/1024*5.0;
    RS_gas = ((5.0 * R2)/sensor_volt) - R2; 
   /*-Replace the value of R0 with the value of R0 in your test -*/
    R0 = 16000;
    ratio = RS_gas/R0;// ratio = RS/R0
    double x = 0.4*ratio;   
    BAC = pow(x,-1.431);  //BAC in mg/L
    lcd.setCursor(0,0);
    lcd.print("BAC = ");
    lcd.print(BAC);  //convert to ppm
    lcd.print(" ppm");
    delay(1000);
}
