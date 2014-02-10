package com.example.dressme;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;



import java.util.Scanner;

import android.util.JsonReader;

public class GetWeather2 implements Runnable{

	enum Condition {RAINLOW,RAINHIGH,SNOW,NORMAL};
	enum Temperature{COLD,WARM,HOT};
	enum Wind{NORMAL,GUSTY};
	
	private double latitude;
	private double longitude;
	
	private float temperature=0f;
	private String weatherCondition ="";
	private Double windSpeeds;
	
	 Temperature finalTemperature;
	 Condition finalCondition;
	 Wind finalWind;
	
	public GetWeather2(Double latitude,Double longitude){
		this.latitude=latitude;
		this.longitude = longitude;
	}

	@Override
	public void run() {

		try{
			URL connection; 
			HttpURLConnection openedConnection;
			connection = new URL("http://api.wunderground.com/api/9cd86aad74d54e48/conditions/q/"
					+latitude+","+longitude+".json");

			openedConnection=(HttpURLConnection)connection.openConnection();

			openedConnection.setDoOutput(false);
			openedConnection.setDoInput(true);
			
			//connects to the website,creates a buffered reader
			System.out.println("opened connection");
			openedConnection.setDoInput(true);
			BufferedReader reader = new BufferedReader(new InputStreamReader(openedConnection.getInputStream()));
			JsonReader reader2 = new JsonReader(reader);
			Scanner scan = new Scanner(reader);
			String entireJsonFile = "";


			while(scan.hasNextLine()){
				String line = scan.nextLine();
				entireJsonFile+=line;
				String temperatureinF="";
				if(line.contains("temp_f")){
					int semicolonIndex = line.indexOf(":");

					//starts at the index of the semi colon plus one and goes to the end
					for(int iOfLine = semicolonIndex+1;iOfLine<line.length();iOfLine++){
						char charAtIndex = line.charAt(iOfLine);

						//comma at the end of temperature
						if(charAtIndex==',')
							break;

						//gets the temperature since it is in string format
						temperatureinF +=line.charAt(iOfLine);

					}
					temperature=Float.parseFloat(temperatureinF);
					System.out.println("Temperature:"+temperature);
				}
				//Gets weather condition data
				if(line.contains("weather\"")){
					int semicolonIndex = line.indexOf(":");

					//starts at the index of the semi colon plus one and goes to the end
					for(int iOfLine = semicolonIndex+1;iOfLine<line.length();iOfLine++){
						char charAtIndex = line.charAt(iOfLine);

						//comma at the end of temperature
						if(charAtIndex==',')
							break;

						//gets the temperature since it is in string format
						weatherCondition +=line.charAt(iOfLine);

					}
					
					System.out.println("Weather Condition:"+weatherCondition);
				}
				//Gets wind speeds
				String windspeedsString="";
				if(line.contains("wind_mph")){
					int semicolonIndex = line.indexOf(":");

					//starts at the index of the semi colon plus one and goes to the end
					for(int iOfLine = semicolonIndex+1;iOfLine<line.length();iOfLine++){
						char charAtIndex = line.charAt(iOfLine);

						//comma at the end of temperature
						if(charAtIndex==',')
							break;

						//gets the wind speeds since it is in string format
						windspeedsString+=line.charAt(iOfLine);
						
					}
					windSpeeds = Double.parseDouble(windspeedsString);
					System.out.println("Wind speeds:"+windSpeeds);
				}




				


			}

			setFinalValues();

		}catch (MalformedURLException e) {
			System.out.println("Malformed id");

		}catch (IOException e) {
			System.out.println("ioexception");
		}
		System.out.println("dfsfs");
		
	}

	private void setFinalValues(){
		if(temperature>70){
			finalTemperature=Temperature.HOT;
		}else if(temperature >50)
			finalTemperature=Temperature.WARM;
		else{
			finalTemperature=Temperature.COLD;
		}
		
		//setRainLow
		if(weatherCondition.contains("Drizzle")||weatherCondition.equals("Light Rain")||
		weatherCondition.equals("Light Thunderstorms and Rain")){
			finalCondition=Condition.RAINLOW;
		//setRainHigh
		}else if(weatherCondition.contains("Heavy Rain")||
		weatherCondition.contains("Rain Showers")||weatherCondition.contains("Thunder")
		){
			
			finalCondition=Condition.RAINHIGH;
		//sets to snowy conditions
		}else if(weatherCondition.contains("Freezing")||weatherCondition.contains("Snow")){
			finalCondition=Condition.SNOW;
		//sets to calm weather conditions
		}else if(weatherCondition.contains("Clear")){
			finalCondition=Condition.NORMAL;
		}
		if(windSpeeds>20){
			finalWind=Wind.GUSTY;
		}else{
			finalWind=Wind.NORMAL;
		}
		
		
	}
	
	
}
