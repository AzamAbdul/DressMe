package com.example.dressme;

import android.app.Activity;
import android.content.Context;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class BoyOrGirlActivity extends Activity{

	boolean boy=false;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boyorgirl);
		
	}



	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}


