package android;

import com.gsoft.common.gui.Control;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

public class app {
	public static class Activity {
		public View view;
		Context context = new Context();
		Window window = new Window();
		boolean isFinishing;
		
		public Context getApplicationContext() {
			// TODO Auto-generated method stub
			return context;
		}


		public void finish() {
			// TODO Auto-generated method stub
			isFinishing = true;
			onDestroy();
			Control.view.dispose();
		}

		protected void onResume() {
			// TODO Auto-generated method stub
			
		}

		protected void onPause() {
			// TODO Auto-generated method stub
			
		}
		
		public boolean isFinishing() {
			// TODO Auto-generated method stub
			return isFinishing;
		}

		protected void onDestroy() {
			// TODO Auto-generated method stub
			
		}
		
		public Window getWindow() {
			// TODO Auto-generated method stub
			return window;
		}


		public void startActivity(Intent intent) {
			// TODO Auto-generated method stub
			
		}
		
		public void setContentView(View view) {
			// TODO Auto-generated method stub
			this.view = view;			
			this.view.setVisible(true);
		}
		
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			return true;
		}
	}
	
	
}