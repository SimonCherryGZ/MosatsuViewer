package com.simoncherry.mosatsu.Activity;

import com.simoncherry.mosatsu.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivity extends Activity {
	private Button menu_btn_start;
	private Button menu_btn_manage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_menu);
		
		menu_btn_start = (Button)findViewById(R.id.btn_start);
		menu_btn_manage = (Button)findViewById(R.id.btn_manage);
		
		menu_btn_start.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO
				SharedPreferences preference = getSharedPreferences(
						"mosatsu_test", Context.MODE_PRIVATE);
				int count = preference.getInt("count", 0);
				if(count > 0){
					startActivity(new Intent(MenuActivity.this, MainActivity.class));
					overridePendingTransition(R.anim.pull_in_from_left, R.anim.anim_nothing);
					MenuActivity.this.finish();
				}else{
					Toast.makeText(getApplicationContext(), "图片库为空，请先点击设置添加图片！", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		menu_btn_manage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MenuActivity.this, ManageActivity.class));
				MenuActivity.this.finish();
			}
		});
	}
}
