package com.jerry.qq_50_slidingmenu;

import com.jerry.slidingmenu.view.SlidingMenu;

import android.os.Bundle;
import android.app.Activity;
import android.app.DownloadManager.Request;
import android.view.Menu;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity {
 
	private SlidingMenu mLeftMenu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		mLeftMenu=(SlidingMenu) findViewById(R.id.id_Menu);
	}
  
	public void toggleMenu(View view){
		mLeftMenu.toggle();
	}
	

}
