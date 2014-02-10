package com.example.dressme;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import com.example.dressme.GetWeather2.Condition;
import com.example.dressme.GetWeather2.Temperature;
import com.example.dressme.GetWeather2.Wind;
import com.google.android.gms.location.LocationClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.Menu;
import android.widget.TextView;

public class DisplayMessageActivity extends Activity{

	TextView textLong;
	TextView textLat;
	enum Condition {RAINLOW,RAINHIGH,SNOW,NORMAL};
	enum Temperature{COLD,WARM,HOT};
	enum Wind{NORMAL,GUSTY};
	Temperature finalTemperature;
	Condition finalCondition;
	Wind finalWind;

	LocationClient mLocationClient;
	boolean gotLocation = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getwweather);

		textLong = (TextView)findViewById(R.id.longitude);
		textLat = (TextView)findViewById(R.id.latitud);

		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new myLocationListener();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		Intent intent = new Intent(this,BoyOrGirlActivity.class);
    	startActivity(intent);
	}

	private class myLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			if(location!=null && !gotLocation){
				double longitude = location.getLongitude();
				double latitude =location.getLatitude();
				textLong.setText(Double.toString(longitude));
				textLat.setText(Double.toString(latitude));
				GetWeather3 weather = new GetWeather3(latitude,longitude);
				Thread connectToWebsite = new Thread(weather);
				System.out.println("Started thread");
				connectToWebsite.start();
				gotLocation=true;
				try {
					connectToWebsite.join();
				} catch (InterruptedException e) {
					
				}
				
			}

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);


		return true;
	}

	public class GetWeather3 implements Runnable{

		

		private double latitude;
		private double longitude;

		private float temperature=0f;
		private String weatherCondition ="";
		private Double windSpeeds;

	

		public GetWeather3(Double latitude,Double longitude){
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





}
