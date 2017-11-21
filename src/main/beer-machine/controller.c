#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#include <Servo.h>

#define NETWORK_SSID "your-network-ssid"
#define NETWORK_PASSWORD "your-network-password"
#define HACKABOT_URL "http://your.hackabot.url"

int LEDPin = D0;
int sensorval = 0;
int LEDWhite = D2;
int photo = D3;
int inphoto = A0;

Servo servo1;

void setup() {
  // setup serial port
  Serial.begin(9600);
  // connect to WiFi
  WiFi.begin(NETWORK_SSID, NETWORK_PASSWORD);

  while (WiFi.status() != WL_CONNECTED) {

    delay(1000);
    Serial.print("Connecting..");
    digitalWrite(LEDPin, LOW);
    delay(1000);
    digitalWrite(LEDPin, HIGH);
    delay(1000);
  }

  pinMode(LEDPin, OUTPUT);

  pinMode(LEDWhite, OUTPUT);
  pinMode(photo, OUTPUT);
  pinMode(inphoto, INPUT);

  servo1.attach(D6);

  // LED Startblink
  digitalWrite(LEDPin, HIGH);
  delay(300);
  digitalWrite(LEDPin, LOW);
  digitalWrite(LEDPin, HIGH);
  delay(300);
  digitalWrite(LEDPin, LOW);
  digitalWrite(LEDPin, HIGH);
  digitalWrite(inphoto, HIGH);

  servo1.write(10);

  Serial.println("entering main loop");
  // turn();
}

void loop() {
  sensorval = analogRead(inphoto);
  Serial.print("Sensorval: ");
  Serial.println(sensorval);

  if (WiFi.status() == WL_CONNECTED) { // Check WiFi connection status
    Serial.println("connected");
    HTTPClient http;          // Declare an object of class HTTPClient
    http.begin(HACKABOT_URL); // Specify request destination
    int httpCode = http.GET();
    // Send the request

    if (httpCode > 0) { // Check the returning code

      String payload = http.getString(); // Get the request response payload
      Serial.println(payload);           // Print the response payload

      if (payload == "BEER") {
        turn();
        delay(5000);
      }
    } else {
      digitalWrite(LEDPin, LOW);
      delay(1000);
      digitalWrite(LEDPin, HIGH);
    }

    http.end(); // Close connection

  } else {
    Serial.println("Not connected to the Internet");
  }
  delay(3000); // Send a request every  n seconds
}

void turn() {
  digitalWrite(LEDWhite, HIGH);
  digitalWrite(photo, HIGH);
  sensorval = analogRead(inphoto);
  servo1.write(180);
  delay(5000);
  servo1.write(10);
  digitalWrite(LEDWhite, LOW);
  digitalWrite(photo, LOW);
}
