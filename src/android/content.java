package android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.gsoft.common.R.R;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.PowerManager;

public class content {
	public static class Intent {

		public static final String ACTION_INSTALL_PACKAGE = "ACTION_INSTALL_PACKAGE";
		public static final String ACTION_VIEW = "ACTION_VIEW";
		
		Uri data;
		String type;
		String action;
		
		Intent(Uri data, String type) {
			this.data = data;
			this.type = type;
		}
		public Intent() {
			
		}
		
		public Intent setDataAndType(Uri data, String type) {
			return new Intent(data,type);
		}
		public void setAction(String action) {
			// TODO Auto-generated method stub
			this.action = action;
		}
		public void setData(Uri uriOfFile) {
			// TODO Auto-generated method stub
			this.data = uriOfFile;
		}

	}

	public static class res {
		public static class AssetManager {
			
			

			public String[] list(String name) throws IOException {
				// TODO Auto-generated method stub
				File file = new File(Context.assets_path+File.separator+name);
				String[] r = file.list();
				return r;
			}

			/**is = asset.open("lib/project.zip");
			 * @throws FileNotFoundException */
			public InputStream open(String name) throws FileNotFoundException {
				// TODO Auto-generated method stub
				File file = new File(Context.assets_path+File.separator+name);
				FileInputStream r = new FileInputStream(file);
				return r;
			}

		}

		public static class Resources {
			
			static AssetManager assetManager = new AssetManager();
			static R r = new R();

			public String getString(int id) {
				// TODO Auto-generated method stub
				String value = R.string_value.values[id  & 0x0000ffff];
				return value;
			}

			public AssetManager getAssets() {
				// TODO Auto-generated method stub
				return assetManager;
			}

			public Drawable getDrawable(int id) {
				// TODO Auto-generated method stub
				File file = new File(Context.drawable_path+File.separator+R.drawable_value.values[id & 0x0000000f]);
				Drawable r = new Drawable(file);
				return r;
			}
			
		}
	}
	
	public static class Context {
		
		static Resources resources = new Resources();

		public static final String POWER_SERVICE = "POWER_SERVICE";
		public static final String WIFI_SERVICE = "WIFI_SERVICE";
		
		public static String CurDir = System.getProperties().getProperty("user.dir");
		public static String assets_path = CurDir + File.separator + "assets";
		public static String filesDir = assets_path + File.separator + "files";
		public static String drawable_path = CurDir + File.separator + "res"+File.separator+"drawable";
		/** 윈도우즈의 경우 예를들어 c:\TextEditorForJava 가 되고, 
		 * 안드로이드의 경우는 /mnt/sdcard 가 된다.
		 */
		public static String ExternalStorage_path = CurDir;
		public static String Root_path = CurDir;
		
		
		public static PowerManager powerManager = new PowerManager();
		public static WifiManager wifiManager;
		

		public File getFilesDir() {
			// TODO Auto-generated method stub
			
			return new File(filesDir);
		}

		public Resources getResources() {
			// TODO Auto-generated method stub
			return resources;
		}

		/*public Object getPackageManager() {
			// TODO Auto-generated method stub
			return null;
		}*/

		public AssetManager getAssets() {
			// TODO Auto-generated method stub
			return Resources.assetManager;
		}

		public Object getSystemService(String serviceName) {
			// TODO Auto-generated method stub
			if (serviceName.equals(POWER_SERVICE)) return powerManager;
			else if (serviceName.equals(WIFI_SERVICE)) {
				if (wifiManager==null || wifiManager.isWifiEnabled==false) {
					wifiManager = new WifiManager();
				}
				return wifiManager;
			}
			return null;
		}
		
	}
}