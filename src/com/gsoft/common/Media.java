package com.gsoft.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.gsoft.common.CompilerHelper;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.Util.ArrayListString;
import com.gsoft.common.Util.Sort;
import com.gsoft.common.Util.Math;
import com.gsoft.common.gui.Control;
import com.gsoft.common.gui.MenuWithClosable;
import com.gsoft.common.gui.ProgressBar;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.Menu.MenuType;
import com.gsoft.common.interfaces.OnTouchListener;
import com.gsoft.common.interfaces.TimerListener;

public class Media {
	static String mediaState = "";
	static View view = Control.view;
	
	public static String[] extensionOfVideo = {".mp4", ".avi", ".wmv", ".asf", ".mov"};
	public static String[] extensionOfAudio = {".mp3", ".wav", ".wma", ".ogg", ".m4a"};
	public static String[] extensionOfImage = {".bmp", ".jpg", ".gif", ".png"};
	
	public synchronized static void setMediaState(boolean addOrReplace, String msg) {
		try {
			if (addOrReplace) {
				mediaState += msg;			
			}
			else {
				mediaState = msg;
			}
			CommonGUI.loggingForNetwork.setText(true, mediaState, false);
			CommonGUI.loggingForNetwork.setHides(false);
			Control.view.postInvalidate();
		}
		catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
	}
	public synchronized static void setMediaStateSync(boolean addOrReplace, String msg) {
		try {
			if (addOrReplace) {
				mediaState += msg;			
			}
			else {
				mediaState = msg;
			}
			CommonGUI.loggingForNetwork.setText(true, mediaState, false);
			CommonGUI.loggingForNetwork.setHides(false);
			Control.view.invalidate();
		}
		catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
	}
	
	public static class MediaRecorderDel {
		MediaRecorder recorder;
		public static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
				File.separator + "Recording";
		public static String recordFile = filePath + File.separator + "record.ogg";
		public boolean isInitialized;
		public boolean isRunning;
		
		public MediaRecorderDel() {
			recorder = new MediaRecorder();
			File file = new File(filePath);
			file.mkdirs();
			File record = new File(recordFile);
			try {
				FileOutputStream stream = new FileOutputStream(record);
				stream.close();
			} catch (Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			} 
			initialize();
		}
		
		public boolean initialize() {
			try {
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				recorder.setOutputFile(recordFile);
				recorder.prepare();
				isInitialized = true;
				this.isRunning = false;
				setMediaState(false, " MediaRecorder initialized.");
				return true;
			}
			catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				return false;
			} 
		}
		
		public void start() {
			try {
				if (isInitialized) {
					recorder.start();
					this.isRunning = true;
					setMediaState(true, " MediaRecorder started.");
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				setMediaState(true, e.toString());
			}
		}
		
		public void stop() {
			try {
				if (isInitialized) {
					recorder.stop();
					setMediaState(true, " MediaRecorder stopped.");
					reset();
					this.isRunning = false;
				}
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				setMediaState(true, e.toString());
			}
		}
		
		public void reset() {
			recorder.reset();
			isInitialized = false;
			this.isRunning = false;
		}
		
		public void release() {
			recorder.release();
			isInitialized = false;
			recorder = null;
			this.isRunning = false;
		}
	}
	
	public static class MediaPlayerDel implements OnCompletionListener, TimerListener, OnTouchListener {
		static MediaPlayer mediaPlayer;
		
		Context context;
		
		ArrayList fileList_1Dim;
		public static int indexOfFileList_1Dim;
		
				
		Timer timer;		
		Timer exitTimer;
		
		//public static int notificationID;
		//public static NotificationManager notificationManager;
		
		public enum State {
			Init,
			Play,
			Pause,
			Stop,
			End
		}
		
		public static State state;
		
		/**state를 5개의 문자로 정렬하여 리턴한다.*/
		static String stateToString() {
			if (state==State.Init) return "Init_";
			else if (state==State.Play) return "Play_";
			else if (state==State.Pause) return "Pause";
			else if (state==State.Stop) return "Stop_";
			else if (state==State.End) return "End__";
			return null;
		}
		
		public static State stateFromString(String state) {
			if (state.equals("Init_")) return State.Init;
			else if (state.equals("Play_")) return State.Play;
			else if (state.equals("Pause")) return State.Pause;
			else if (state.equals("Stop_")) return State.Stop;
			else if (state.equals("End__")) return State.End;
			return null;
		}
		
		ProgressBar seekBar;
		int duration;
		public static int curPos;
		int itemCount;
		int itemPos;
		int tickInterval;
		
		//private View view;
		
		//MenuWithAlwaysOpen menuSoundControl;
		public MenuWithClosable menuSoundControl;
		public static String[] namesOfMenuSoundControl = {"Play", "Pause", "Stop", 
			"Next", "Prev", "Volume Up", "Volume Down", "Settings"};
		
		MenuWithClosable menuSoundControlSettings;
		public static String[] namesOfMenuSoundControlSettings = {
			"Set auto exit time", /*"Forward/Backward",*/ "Random On/Off", "Repeat all"};
		
		MenuWithClosable menuExitTime;
		public static String[] namesOfMenuExitTime = {"10 min", "20 min", "30 min", 
			"1 hour", "2 hour", "3 hour", "4 hour"};
		
		private Rectangle bounds;

		static public boolean allRepeated;
		//boolean isForwardOrBackward = true;
		static public boolean isRandomOnOrOff;
		
		float leftVolume = 0.7f;
		float rightVolume = 0.7f;
		
		Random rand;

		

		//private boolean isWriting;

		//private ArrayList fileList;
		
		

		//private MediaPlayerThread mediaPlayerThread;
		
		synchronized void initAttrsOfProgressBar() {
			try {
				duration = mediaPlayer.getDuration();
				itemCount = (int) (duration * 0.001f / 60.0f * 5.0f);
				if (itemCount<=0) itemCount = 1;
				curPos = mediaPlayer.getCurrentPosition();
				itemPos = (int) ((float)itemCount * (float)curPos / (float)duration);
				tickInterval = (int) (1.0f / (float)itemCount * duration);
				if (seekBar!=null) {
					seekBar.setItemCount(itemCount);
					seekBar.setItemPos(itemPos);
					seekBar.initialize();
				}
				
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
		}
		
		/**"Play", "Pause", "Stop", 
			"Next", "Prev", "Volume Up", "Volume Down", "Settings"*/
		void createMenuSoundControl(View view) {
			//setMediaState(true, " createMenuSoundControl().");
			int x, y, w, h;
			w = (int) (bounds.width * 0.3f);
			h = (int) (bounds.height * 0.8f);
			x = bounds.width-w;
			y = bounds.y + bounds.height/2 - h/2;
			Rectangle boundsMenuSoundControl = new Rectangle(x,y,w,h);  
			menuSoundControl = new MenuWithClosable/*MenuWithAlwaysOpen*/("menuSoundControl", boundsMenuSoundControl, 
					MenuType.Vertical, view, namesOfMenuSoundControl, new Size(3,3), true, this);
			
		}
		
		/**"Set exit time",  "Random On/Off", "Repeat all"*/
	void createMenuSoundControlSettings(View view) {
		//setMediaState(true, " createMenuSoundControl().");
		int x, y, w, h;
		w = (int) (bounds.width * 0.45f);
		h = (int) (bounds.height * 0.4f);
		x = bounds.x + bounds.width/2 - w/2;
		y = bounds.y + bounds.height/2 - h/2;
		Rectangle boundsMenuSoundControlSettings = new Rectangle(x,y,w,h);  
		menuSoundControlSettings = new /*MenuWithClosable*/MenuWithClosable("menuSoundControlSettings", boundsMenuSoundControlSettings, 
				MenuType.Vertical, view, namesOfMenuSoundControlSettings, new Size(3,3), true, this);
		
					
		/*menuSoundControlSettings.buttons[1].selectable = true;	// Screen On/Off 메뉴는 토글로 동작한다.
		menuSoundControlSettings.buttons[1].toggleable = true;
		menuSoundControlSettings.buttons[1].ColorSelected = Color.YELLOW;
		menuSoundControlSettings.buttons[1].isSelected = true;*/
		
		/*menuSoundControlSettings.buttons[1].selectable = true;	// Forward/Backward 메뉴는 토글로 동작한다.
		menuSoundControlSettings.buttons[1].toggleable = true;
		menuSoundControlSettings.buttons[1].ColorSelected = Color.YELLOW;
		menuSoundControlSettings.buttons[1].isSelected = true;*/
		
		menuSoundControlSettings.buttons[1].selectable = true;	// Random On/Off 메뉴는 토글로 동작한다.
		menuSoundControlSettings.buttons[1].toggleable = true;
		menuSoundControlSettings.buttons[1].ColorSelected = Color.YELLOW;
		menuSoundControlSettings.buttons[1].isSelected = false;
		
		menuSoundControlSettings.buttons[2].selectable = true;	// Repeat all 메뉴는 토글로 동작한다.
		menuSoundControlSettings.buttons[2].toggleable = true;
		menuSoundControlSettings.buttons[2].ColorSelected = Color.YELLOW;
		menuSoundControlSettings.buttons[2].isSelected = false;
	}
		
		void createMenuExitTime(View view) {
			//setMediaState(true, " createMenuSoundControl().");
			int x, y, w, h;
			w = (int) (bounds.width * 0.4f);
			h = (int) (bounds.height * 0.6f);
			x = bounds.x + bounds.width/2 - w/2;
			y = bounds.y + bounds.height/2 - h/2;
			Rectangle boundsMenuExitTime = new Rectangle(x,y,w,h);  
			menuExitTime = new /*MenuWithClosable*/MenuWithClosable("menuExitTime", boundsMenuExitTime, 
					MenuType.Vertical, view, namesOfMenuExitTime, new Size(3,3), true, this);
			
		}
		
		void createProgressBar(View view) {	
			try {
				//initAttrsOfProgressBar();
				int x, y, w, h;
				w = (int) (bounds.width * 0.1f);
				h = (int) (bounds.height * 0.65f);
				x = w;
				y = bounds.y + bounds.height/2 - h/2;
				Rectangle bounds = new Rectangle(x,y,w,h); 
				seekBar = new ProgressBar(true, 20, bounds, 0);
				seekBar.setHides(true);
				seekBar.setOnTouchListener(this);
				//setMediaState(false, " SeekBar created.");
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
		}
		
		/*public static String[] read(InputStream is) {
			int i;
			
			IO.readString(is);
			
			int count = IO.readInt(is);
			ArrayListString r = new ArrayListString(count);
			
			for (i=0; i<count; i++) {
				r.add(IO.readString(is));
			}
			return r.getItems();
		}
		
		public void write(OutputStream os) {
			int i;
			String className = getClass().getName();
			IO.writeString(os, className);
			
			IO.writeInt(os, mFileList.count);
			for (i=0; i<mFileList.count; i++) {
				Object item = mFileList.getItem(i);
				//File file = (File)item;
				//String path = file.getAbsolutePath();
				String path = (String)item;
				IO.writeString(os, path);
			}
			//IO.writeString(os, ";");
		}*/
		
		/*public void draw(Canvas canvas) {
			//setMediaState(true, " MediaPlayerDel drawed."+" SeekBar.hides:"+seekBar.hides+
			//		" menuSoundControl.isOpen:"+menuSoundControl.isOpen);
			try{
			if (!seekBar.hides) {
				seekBar.draw(canvas);
			}
			if (menuSoundControl.getIsOpen()) {
	        	menuSoundControl.draw(canvas);
			}
			if (menuSoundControlSettings.getIsOpen()) {
				menuSoundControlSettings.draw(canvas);
			}
			if (menuExitTime.getIsOpen()) {
				menuExitTime.draw(canvas);
			}
			}catch(Exception e) {
	    		
	    	}
		}*/		
		
	    /*public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
	    	boolean r=false;
	    	if (event.actionCode==MotionEvent.ActionDown) {
	    		if (menuSoundControlSettings.getIsOpen()) {
					r = menuSoundControlSettings.onTouch(event, scaleFactor);
					if (r) return true;
	    		}
	    		if (seekBar!=null) {
	    			r = seekBar.onTouch(event, null);
	    			if (r) return true;
	    		}
	    		if (menuSoundControl.getIsOpen()) {
					r = menuSoundControl.onTouch(event, scaleFactor);
					if (r) return true;
	    		}	    		
	    		if (menuExitTime.getIsOpen()) {
					r = menuExitTime.onTouch(event, scaleFactor);
					if (r) return true;
	    		}
	    	}
	    	return false;	    	
	    }*/
	    
	    /** fileList에서 발견된 첫번째 video파일을 찾아 리턴한다.
	     * @param fileList 는 절대경로의 파일(디렉토리)들의 리스트
	     * @return
	     */
	    public static String getVideoFile(ArrayListString fileList) {
	    	int i;
	    	for (i=0; i<fileList.count; i++) {
		    	String absFilename = fileList.getItem(i);
			    File file  = new File(absFilename);
				if (file.isDirectory()==false) {
					String ext = FileHelper.getExt(file.getAbsolutePath());
        			if (ext==null) continue;
        			ext = ext.toLowerCase();
        			/*if (ext.contains(".mp4") || ext.contains(".avi") | ext.contains(".wmv") |
        					ext.contains(".asf") | ext.contains(".mov")) {
        				return absFilename;
        			}*/
					for (int k=0; k<extensionOfVideo.length; k++) {
        				if (ext.contains(extensionOfVideo[k])) {
        					return absFilename;
        				}
        			}
				}
				else {
					ArrayList fileListForDirectory;
					fileListForDirectory = FileHelper.getFileList(absFilename);
					int j;
					for (j=0; j<fileListForDirectory.count; j++) {
						File item = (File)fileListForDirectory.getItem(j);
						String filename = item.getAbsolutePath();
						String ext = FileHelper.getExt(file.getAbsolutePath());
            			if (ext==null) continue;
            			ext = ext.toLowerCase();
	        			/*if (ext.contains(".mp4") || ext.contains(".avi") | ext.contains(".wmv") |
	        					ext.contains(".asf") | ext.contains(".mov")) {
	        				return filename;
	        			}*/
						for (int k=0; k<extensionOfVideo.length; k++) {
	        				if (ext.contains(extensionOfVideo[k])) {
	        					return filename;
	        				}
	        			}
					}
					
				}
	    	}
	    	return null;
	    }
		
	    ArrayList makeFileList1Dim(ArrayList fileList) {
	    	ArrayList fileList_1Dim = new ArrayList(100);
			int i;
			for (i=0; i<fileList.count; i++) {
				Object item = fileList.getItem(i);
				boolean isDirectory = (item instanceof ArrayList);
				if (isDirectory) {
					ArrayList list = (ArrayList)item;
					int j;
	    			for (j=0; j<list.count; j++) {
	            		File file = (File) list.getItem(j);
	            		if (file.isDirectory()==false) {
	            			String ext = FileHelper.getExt(file.getAbsolutePath());
	            			if (ext==null) continue;
	            			ext = ext.toLowerCase();
	            			/*if (ext.contains(".mp3") || ext.contains(".wav") | ext.contains(".wma") |
	            					ext.contains(".ogg") | ext.contains(".m4v")) {
	            				fileList_1Dim.add(file);
	            			}*/
	            			for (int k=0; k<extensionOfAudio.length; k++) {
	            				if (ext.contains(extensionOfAudio[k])) {
	            					fileList_1Dim.add(file);
	            					break;
	            				}
	            			}
	            		}
	    			}					
				}
				else {
					File file = (File) item;
					String ext = FileHelper.getExt(file.getAbsolutePath());
        			if (ext==null) continue;
        			ext = ext.toLowerCase();
					/*if (ext.contains(".mp3") || ext.contains(".wav") | ext.contains(".wma") |
        					ext.contains(".ogg") | ext.contains(".m4v")) {
        				fileList_1Dim.add(file);
        			}*/
					for (int k=0; k<extensionOfAudio.length; k++) {
        				if (ext.contains(extensionOfAudio[k])) {
        					fileList_1Dim.add(file);
        					break;
        				}
        			}
				}
			}
			if (fileList_1Dim.count>0) {
				File[] temp = new File[fileList_1Dim.count];
				int a;
				for (a=0; a<temp.length; a++) {
					temp[a] = (File) fileList_1Dim.getItem(a);
				}
				Sort.merge_sort(temp, 0, fileList_1Dim.count-1, true);
				fileList_1Dim.reset();
				for (a=0; a<temp.length; a++) {
					fileList_1Dim.add(temp[a]);
				}
				return fileList_1Dim;
			}
			return null;
		}
		
		public MediaPlayerDel(View paramView, Rectangle bounds) {
			view = paramView;
			this.context = view.getContext();
			this.bounds = bounds;
			
			createProgressBar(view);
			createMenuSoundControl(view);
			createMenuSoundControlSettings(view);
			createMenuExitTime(view);
			
			if (timer==null) {
				timer = new Timer(0, this, 100, 10000);
				timer.setSyncTime(200,2000);
			}
			if (exitTimer==null) {
				exitTimer = new Timer(1, this, 1000*60*5, 1000000);
				exitTimer.setSyncTime(1000*60,1000*60);
			}
			
			rand = new Random();
			//startMediaPlayerThread();
		}
		
		void initMediaPlayer(String path) {
			boolean r=false;
			try {
				mediaPlayer.setDataSource(path);
				mediaPlayer.prepare();
				r = true;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				setMediaState(true, e.toString());
				r = false;
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				setMediaState(true, e.toString());
				r = false;
			} catch (IllegalStateException e) {
				setMediaState(true, e.toString());
				r = false;
			} catch (IOException e) {
				setMediaState(true, e.toString());
				r = false;
			}
			catch (Exception e) {
				//setMediaState(true, e.toString());
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				r = false;
			}
			
			if (!r) {
				if (mediaPlayer!=null) {
					release();
				}
				File file = new File(path);
				Uri uri = Uri.fromFile(file);
				mediaPlayer = MediaPlayer.create(view.getContext(), uri);
				mediaPlayer.setVolume(leftVolume, rightVolume);
			}
		}
		
		public boolean initialize(ArrayList fileList, PlayListAndCurSongInfo info) {
			state = State.Init;
			if (info!=null) {
				indexOfFileList_1Dim = info.indexOfCurSong;
				allRepeated = info.allRepeated;
				curPos = info.seekOfCurSong;
				//this.isForwardOrBackward = info.isForwardOrBackward;
				isRandomOnOrOff = info.isRandomOnOrOff;
				//"Set auto exit time", "Forward/Backward", "Random On/Off", "Repeat all"
				//menuSoundControlSettings.buttons[1].isSelected = isForwardOrBackward; 
				menuSoundControlSettings.buttons[1].isSelected = isRandomOnOrOff;
				menuSoundControlSettings.buttons[2].isSelected = allRepeated;
			}
			else {
				indexOfFileList_1Dim = 0;
				allRepeated = false;
				curPos = 0;
				//this.isForwardOrBackward = true;
				isRandomOnOrOff = false;
			}
			if (mediaPlayer!=null) {
				//release();
				reset();
			}
			fileList_1Dim = makeFileList1Dim(fileList);
			if (fileList_1Dim!=null) {
				if (indexOfFileList_1Dim<0) indexOfFileList_1Dim=0;
				if (indexOfFileList_1Dim>fileList_1Dim.count-1) 
					indexOfFileList_1Dim=0;
					//indexOfFileList_1Dim=fileList_1Dim.count-1; 
				setMediaState(false, fileList_1Dim.count + " songs FileList1Dim created.");
				
				if (isRandomOnOrOff) {
					indexOfFileList_1Dim = rand.nextInt(fileList_1Dim.count);
				}
				else {
					if (info==null) {
						indexOfFileList_1Dim = 0;
					}
				}
				File file = (File) fileList_1Dim.getItem(indexOfFileList_1Dim);
				Uri uri = Uri.fromFile(file);
				if (mediaPlayer==null) {
					mediaPlayer = MediaPlayer.create(context, uri);				
					mediaPlayer.setVolume(leftVolume, rightVolume); 
				}
				else {
					initMediaPlayer(file.getAbsolutePath());
				}
				try {
					mediaPlayer.seekTo(curPos);
				}catch(Exception e) {
					e.printStackTrace();
					CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				}
				
				mediaPlayer.setOnCompletionListener(this);
				
				//setMediaState(true, " Media Player created.");
				
				seekBar.setHides(false);
				menuSoundControl.open(true);
				
				//play();
				return true;
			}
			else {
				setMediaState(true, " No songs.");
				seekBar.setHides(true);
				menuSoundControl.open(false);
				return false;
			}
			
		}
		
		public void enableSoundControl(boolean b) {
			if (b) {
				menuSoundControl.open(true);
				seekBar.setHides(false);
				CommonGUI.loggingForNetwork.setHides(false);
			}
			else {
				menuSoundControl.open(false);
				seekBar.setHides(true);
				CommonGUI.loggingForNetwork.setHides(true);
			}
			
		}
		
		/*class MediaPlayerThread extends Thread {

			public void run() {
				while (!exit) {
					if (isPlaying) {
						play();
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}
		}
		
		void killMediaPlayerThread() {
			if (mediaPlayerThread!=null) {
				isPlaying = false;
				exit = true;
				try {
					mediaPlayerThread.join();
				} catch (InterruptedException e) {
				}
				mediaPlayerThread = null;
			}
		}
			
		
		void startMediaPlayerThread() {
			mediaPlayerThread = new MediaPlayerThread();
			mediaPlayerThread.start();
		}
		
		public void playAudio() {
			isPlaying = true;
		}*/
		
		public boolean isPlaying() {
			if (state==State.Play) return true;
			return false;
		}
		
		public void resume() {
			/*if (timer!=null) {
				setMediaState(true, " Media resume(). State:"+state+ " TimerStop:"+timer.getIsStoped());
			}
			if (state==State.Play) {
				if (timer!=null && timer.getIsStoped()==true) {
					timer.startTimer();
				}
			}*/
		}
		
		
		
		public void play() {
			try {
				PowerManagement.getPartialWakeLock(view.getContext());
			File file = (File) fileList_1Dim.getItem(indexOfFileList_1Dim);
			String filename = FileHelper.getFilename(file.getAbsolutePath());
			
			if (state==State.Stop || state==State.End) {
				initMediaPlayer(file.getAbsolutePath());
				
			}
			//mediaPlayer.seekTo(seekOfCurSong);
			
			//start();
			
			initAttrsOfProgressBar();
			
			float songLenInSec = duration * 0.001f;
			float intervalInSec = tickInterval * 0.001f;
			setMediaState(false, filename + " song(" + songLenInSec+" sec) played.");
						
			if (timer!=null && timer.isStoped==false) {
				timer.cancelTimer();
				setMediaState(true, " Timer canceled.");
			}
			
			if (state!=State.Pause) {
				//timer = new Timer(this, tickInterval, duration);
				//timer.startTimer();
				timer.setInterval(tickInterval);
				timer.setFinishTime(duration);
				//timer.listener = this;
				timer.startTimer();
				setMediaState(true, " Interval:"+intervalInSec+
						" Duration:"+songLenInSec+" Timer started.");
			}
			else {
				timer.setInterval(tickInterval);
				timer.setFinishTime(duration-curPos);
				//timer.listener = this;
				timer.startTimer();
				
				songLenInSec = (duration-curPos) * 0.001f;
				setMediaState(true, " Interval:"+intervalInSec+
						" Duration-curPos:"+songLenInSec+" Timer started.");
				
			}
			mediaPlayer.setOnCompletionListener(this);
			state = State.Play;
			
			mediaPlayer.start();
			
			}catch(Exception e) {
				setMediaState(true, " indexOfFileList_1Dim:"+indexOfFileList_1Dim+" Media-play() error.");
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
		}
		
		public void pause() {
			PowerManagement.releaseWakeLock();
			File file = (File) fileList_1Dim.getItem(indexOfFileList_1Dim);
			String filename = FileHelper.getFilename(file.getAbsolutePath());
			setMediaState(true, filename + " song paused.");
			mediaPlayer.pause();
			state = State.Pause;
			if (timer!=null && timer.isStoped==false) {
				timer.cancelTimer();
				setMediaState(true, " Timer canceled.");
			}
		}
		
		public void stop() {
			PowerManagement.releaseWakeLock();
			File file = (File) fileList_1Dim.getItem(indexOfFileList_1Dim);
			String filename = FileHelper.getFilename(file.getAbsolutePath());
			setMediaState(true, filename + " song stopped.");
			mediaPlayer.stop();
			state = State.Stop;
			reset();
			if (timer!=null && timer.isStoped==false) {
				timer.cancelTimer();
				setMediaState(true, " Timer canceled.");
			}
		}
		
		public void reset() {
			if (mediaPlayer!=null) {
				mediaPlayer.reset();
				state = State.End;
				if (timer!=null && timer.isStoped==false) {
					timer.cancelTimer();
					setMediaState(true, " Timer canceled.");
				}
			}
		}
		
		public void release() {
			if (mediaPlayer!=null) {
				mediaPlayer.release();
				mediaPlayer = null;
				state = State.End;
				if (timer!=null && timer.isStoped==false) {
					timer.cancelTimer();
					setMediaState(true, " Timer canceled.");
				}
			}
		}
		
		public synchronized void next() {
			try {
				if (isRandomOnOrOff) {
					indexOfFileList_1Dim = rand.nextInt(fileList_1Dim.count);
				}
				else {
					indexOfFileList_1Dim++;
					if (indexOfFileList_1Dim>=fileList_1Dim.count) {
						if (allRepeated) indexOfFileList_1Dim = 0;
						else {
							indexOfFileList_1Dim = fileList_1Dim.count-1;
							//timer.release();
							//release();
							reset();
							return;
						}
					}
					/*if (isForwardOrBackward) {
						setIndexOfFileList_1Dim(indexOfPlayList+1);
						if (indexOfPlayList>=fileList_1Dim.count) {
							if (allRepeated) setIndexOfFileList_1Dim(0);
							else {
								setIndexOfFileList_1Dim(fileList_1Dim.count-1);
								//timer.release();
								//release();
								//reset();
								return;
							}
						}
					}
					else {
						setIndexOfFileList_1Dim(indexOfPlayList--);
						if (indexOfFileList_1Dim<0) {
							if (allRepeated) setIndexOfFileList_1Dim(fileList_1Dim.count-1);
							else {
								setIndexOfFileList_1Dim(0);
								//release();
								//reset();
								return;
							}
						}
						
					}*/
				}
				
				//mediaPlayer.release();
				//mediaPlayer = null;
				reset();
				
				//File file = (File) fileList_1Dim.getItem(indexOfFileList_1Dim);
				//Uri uri = Uri.fromFile(file);
				//initMediaPlayer(file.getAbsolutePath());
				play();
			}catch(Exception e) {
				setMediaState(true, " Media-next() error.");
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
		}
		
		public synchronized void prev() {
			try {
				if (isRandomOnOrOff) {
					indexOfFileList_1Dim = rand.nextInt(fileList_1Dim.count);
				}
				else {
					indexOfFileList_1Dim--;
					if (indexOfFileList_1Dim<0) {
						if (allRepeated) indexOfFileList_1Dim = fileList_1Dim.count-1;
						else {
							indexOfFileList_1Dim = 0;
							//release();
							reset();
							return;
						}
					}
					/*if (isForwardOrBackward) {
						setIndexOfFileList_1Dim(indexOfPlayList-1);
						if (indexOfPlayList<0) {
							if (allRepeated) setIndexOfFileList_1Dim(fileList_1Dim.count-1);
							else {
								setIndexOfFileList_1Dim(0);
								//release();
								//reset();
								return;
							}
						}
					}
					else {
						setIndexOfFileList_1Dim(indexOfPlayList++);
						if (indexOfPlayList>=fileList_1Dim.count) {
							if (allRepeated) setIndexOfFileList_1Dim(0);
							else {
								setIndexOfFileList_1Dim(fileList_1Dim.count-1);
								//release();
								//reset();
								return;
							}
						}
					}*/
				}
				//mediaPlayer.release();
				//mediaPlayer = null;
				reset();
				
				//File file = (File) fileList_1Dim.getItem(indexOfFileList_1Dim);
				//Uri uri = Uri.fromFile(file);
				//mediaPlayer = MediaPlayer.create(context, uri);
				//mediaPlayer.setVolume(leftVolume, rightVolume);
				//initMediaPlayer(file.getAbsolutePath());
				play();
			}catch(Exception e) {
				setMediaState(true, " Media-prev() error.");
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
		}

				
		/*public void resume() {
			if (state!=State.Init) {				
				play();
				setMediaState(true, " resume().");
			}
		}*/

		/** timer스레드 callback*/
		@Override
		public synchronized void onTick(Object sender) {
			// TODO Auto-generated method stub
			//setMediaState(true, " onTick().");
			Timer timer = (Timer)sender;
			if (timer.iName==this.timer.iName) {
				seekBar.setItemPos(seekBar.getItemPos()+1);
				if (PowerManagement.isScreenOn) {
					//Control.setModified(true);
					view.postInvalidate();
				}
			}
			
		}

		/** timer스레드 callback*/
		@Override
		public synchronized void onFinish(Object sender) {
			// TODO Auto-generated method stub
			Timer timer = (Timer)sender;
			/*if (timer.iName==this.timer.iName) {
				setMediaState(true, " onFinish().");
				if (timer!=null && timer.isStoped==false) {
					timer.cancelTimer();
					setMediaState(true, " Timer canceled.");
				}
				
				next();
				//mediaPlayer.start();
				//play();
				//view.postInvalidate();
			}
			else*/ if (timer.iName==this.exitTimer.iName) {
				setMediaState(true, " onFinish().");
				if (exitTimer!=null && exitTimer.isStoped==false) {
					exitTimer.cancelTimer();
					setMediaState(true, " ExitTimer canceled.");
				}
				Control.textOfNotification = " was destroyed because of auto-exit time.";				
				Control.exit(true);
				
			}
			
		}
		
		void killTimers() {
			if (timer!=null) {
				timer.killTimer();
				timer = null;
			}
			if (exitTimer!=null) {
				exitTimer.killTimer();
				exitTimer = null;
			}
		}
		
		public void destroy() {
			killTimers();
			//killMediaPlayerThread();
			release();
		}

		@Override
		public void onTouchEvent(Object sender, MotionEvent e) {
			// TODO Auto-generated method stub
			if (sender instanceof ProgressBar) {
				if (mediaPlayer==null) return;
				if (timer!=null && timer.isStoped==false) timer.cancelTimer();
				
				ProgressBar bar = (ProgressBar)sender;
				itemPos = bar.itemPos;
				//duration = mediaPlayer.getDuration();
				int seekPos = (int) ((float)duration * 
						(float)(itemPos) / (float)bar.itemCount);
				mediaPlayer.seekTo(seekPos);
								
				
				//timer = new Timer(this, tickInterval, duration-seekPos);
				//timer.startTimer();
				timer.setInterval(tickInterval);
				timer.setFinishTime(duration-seekPos);
				timer.startTimer();
				
			}
			else if (sender instanceof Button) {
				Button button = (Button)sender;
				/**/
				// "Play", "Pause", "Stop", 
				// "Next", "Prev", "Volume Up", "Volume Down", "Settings"
	            if (button.iName==menuSoundControl.buttons[0].iName) {	// Play
	            	if (mediaPlayer!=null) play();
	            }
	            else if (button.iName==menuSoundControl.buttons[1].iName) {	// Pause()
	            	if (mediaPlayer!=null) pause();
	            }
	            else if (button.iName==menuSoundControl.buttons[2].iName) {	// Stop
	            	if (mediaPlayer!=null) stop();
	            }
	            else if (button.iName==menuSoundControl.buttons[3].iName) {	// Next
	            	if (mediaPlayer!=null) next();
	            }
	            else if (button.iName==menuSoundControl.buttons[4].iName) {	// Prev
	            	if (mediaPlayer!=null) prev();
	            }
	            else if (button.iName==menuSoundControl.buttons[5].iName) {	// Volume Up
	            	leftVolume += 0.1f;
	            	if (leftVolume>1.0f) leftVolume = 1.0f;
	            	rightVolume += 0.1f;
	            	if (rightVolume>1.0f) rightVolume = 1.0f;
	            	if (mediaPlayer!=null) mediaPlayer.setVolume(leftVolume, rightVolume);
	            }
	            else if (button.iName==menuSoundControl.buttons[6].iName) {	// Volume Down
	            	leftVolume -= 0.1f;
	            	if (leftVolume<0.0f) leftVolume = 0.0f;
	            	rightVolume -= 0.1f;
	            	if (rightVolume<0.0f) rightVolume = 0.0f;
	            	if (mediaPlayer!=null) mediaPlayer.setVolume(leftVolume, rightVolume);
	            }
	            else if (button.iName==menuSoundControl.buttons[7].iName) {	// Settings
	            	if (menuSoundControlSettings!=null) menuSoundControlSettings.open(true);
	            }
	            
	            // "Set exit time", "Forward/Backward", "Random On/Off"
	            else if (button.iName==menuSoundControlSettings.buttons[0].iName) {	// Set Exit time
	            	//if (mediaPlayer!=null) mediaPlayer.stop();
	            	menuExitTime.open(true);
	            	menuSoundControlSettings.open(false);
	            }
	            /*else if (button.iName==menuSoundControlSettings.buttons[1].iName) {	// Screen On
	            	if (menuSoundControlSettings.buttons[1].isSelected) {
	            		PowerManagement.keepScreenOn();
	            	}
	            	else {
	            		PowerManagement.clearScreenOn();
	            		//PowerManagement.goToSleep(view.getContext(), 0);
	            	}
	            	menuSoundControlSettings.open(false);
	            }*/
	            /*else if (button.iName==menuSoundControlSettings.buttons[1].iName) {	// Forward/Backward
	            	if (menuSoundControlSettings.buttons[1].isSelected) {
	            		isForwardOrBackward = true;
	            	}
	            	else {
	            		isForwardOrBackward = false;
	            	}
	            	menuSoundControlSettings.open(false);
	            }*/
	            else if (button.iName==menuSoundControlSettings.buttons[1].iName) {	// Random On/Off
	            	if (menuSoundControlSettings.buttons[1].isSelected) {
	            		isRandomOnOrOff = true;
	            	}
	            	else {
	            		isRandomOnOrOff = false;
	            	}
	            	menuSoundControlSettings.open(false);
	            }
	            else if (button.iName==menuSoundControlSettings.buttons[2].iName) {	// Repeat all
	            	if (menuSoundControlSettings.buttons[2].isSelected) {
	            		allRepeated = true;
	            	}
	            	else {
	            		allRepeated = false;
	            	}
	            	menuSoundControlSettings.open(false);
	            }
	            else {	// MenuExitTime handler
	            	int i;
	            	int mins;
	            	// "10 min", "20 min", "30 min", "1 hour", "2 hour", "3 hour", "4 hour"
	            	for (i=0; i<menuExitTime.buttons.length; i++) {
	            		if (button.iName==menuExitTime.buttons[i].iName) {
	            			switch(i) {
	            			case 0: mins = 10; break;
	            			case 1: mins = 20; break;
	            			case 2: mins = 30; break;
	            			case 3: mins = 60; break;
	            			case 4: mins = 120; break;
	            			case 5: mins = 180; break;
	            			case 6: mins = 240; break;
	            			default: mins = 10;
	            			}
	            			setExitTimer(mins);
	            			menuExitTime.open(false);
	            			return;
	            		}
	            	}
	            }
			}
		}
		
		public void setExitTimer(int mins) {
			if (exitTimer!=null && exitTimer.isStoped==false) {
				exitTimer.cancelTimer();
			}
			
			long finishTime = 1000 * 60 * mins;
			exitTimer.setFinishTime(finishTime);
			exitTimer.startTimer();
			
		}

		/**state에 상관없이 저장한다.*/
		public static void write(OutputStream os, ArrayListString mPlayList, TextFormat format) throws Exception {
			// TODO Auto-generated method stub
			try {
				int i;
				/*PrintWriter pw = new PrintWriter(os);
				String className = getClass().getName();
				pw.write(className);
				
				pw.write(mPlayList.count);
				for (i=0; i<mPlayList.count; i++) {
					Object item = mPlayList.getItem(i);
					String path = (String)item;
					pw.write(path);
				}
				pw.write(indexOfFileList_1Dim);
				pw.write(mediaPlayer.getCurrentPosition());*/
				String className = "MediaPlayerDel";
				IO.writeString(os, className, format, true, true);
				
				String strState = stateToString();
				IO.writeString(os, strState, format, true, true);
				
				if (mPlayList==null) return;
				
				//if (strState.equals("Play_")/* || strState.equals("Pause")*/) {
					IO.writeInt(os, mPlayList.count, true);
					for (i=0; i<mPlayList.count; i++) {
						Object item = mPlayList.getItem(i);
						String path = (String)item;
						IO.writeString(os, path, format, true, true);
					}
					
					IO.writeInt(os, indexOfFileList_1Dim, true);
					
					if (mediaPlayer!=null) {
						curPos = mediaPlayer.getCurrentPosition();
					}
					IO.writeInt(os, curPos, true);
					IO.writeBoolean(os, allRepeated);
					//IO.writeBoolean(os, isForwardOrBackward);
					IO.writeBoolean(os, isRandomOnOrOff);					
				//}
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				throw e;
			}
		}
		
		public static class PlayListAndCurSongInfo {
			public String[] playList;
			public int indexOfCurSong;
			public int seekOfCurSong;
			
			public String state;
			public boolean allRepeated;
			//public boolean isForwardOrBackward;
			public boolean isRandomOnOrOff;
		}
		
		public static PlayListAndCurSongInfo readPlayListAndCurSongInfo(InputStream is, TextFormat format) throws Exception {
			try { 
				IO.readString(is, format);
				
				PlayListAndCurSongInfo info = new PlayListAndCurSongInfo();
								 
				info.state = IO.readString(is, format);
				//if (strState.equals("Play")) {				
					int count = IO.readInt(is, true);
					if (count<0 || count>500) return null;
					int i;
					String[] r = new String[count];
					for (i=0; i<count; i++) {
						String path = IO.readString(is, format);
						r[i] = path;
					}
					
					
					info.playList = r;
					info.indexOfCurSong = IO.readInt(is, true);
					info.seekOfCurSong = IO.readInt(is, true);
					
					info.allRepeated = IO.readBoolean(is);
					//info.isForwardOrBackward = IO.readBoolean(is);
					info.isRandomOnOrOff = IO.readBoolean(is);
					return info;
				//}
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				throw e;
			}
			
		}

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			setMediaState(true, " onCompletion().");
			if (timer!=null && timer.isStoped==false) {
				timer.cancelTimer();
				setMediaState(true, " Timer canceled.");
			}
			next();
		}

		
	}
	
	
	
	
	public static class SoundPlayer extends Thread {
		SoundPool soundPool;
		
		ArrayList fileList_1Dim;
		int indexOfFileList_1Dim;
		
		String[] fileListString = new String[5];
		int countOfCachedFileList;
		int[] arrSoundID = new int[5];
		int countOfLoadedSounds;
		
		int loop = 0;
		float leftVolume = 1.0f;
		float rightVolume = 1.0f;
		float playbackRate = 1.0f;
		int priority = 1;
		
		void makeFileList1Dim(ArrayList fileList) {
			fileList_1Dim = new ArrayList(100);
			int i;
			for (i=0; i<fileList.count; i++) {
				Object item = fileList.getItem(i);
				boolean isDirectory = (item instanceof ArrayList);
				if (isDirectory) {
					ArrayList list = (ArrayList)item;
					int j;
	    			for (j=0; j<list.count; j++) {
	            		File file = (File) list.getItem(j);
	            		if (file.isDirectory()==false) {
	            			String ext = FileHelper.getExt(file.getAbsolutePath());
	            			if (ext.equals(".mp3") || ext.equals(".wav") || ext.equals(".wma")) {
	            				fileList_1Dim.add(file);
	            			}
	            		}
	    			}					
				}
				else {
					File file = (File) item;
					String ext = FileHelper.getExt(file.getAbsolutePath());
        			if (ext.equals(".mp3") || ext.equals(".wav") || ext.equals(".wma")) {
        				fileList_1Dim.add(file);
        			}
				}
			}
		}
		
		public SoundPlayer(SoundPool soundPool, ArrayList fileList) {
			this.soundPool = soundPool;
			makeFileList1Dim(fileList);
			setMediaState(false, "FileList1Dim created.");
			
		}
		
		void getCachedFileList(String[] result, ArrayList fileList_1Dim, int curIndex) {
			int count=0;
			int i;
			int start = Math.max(curIndex, 0);
			int end = Math.min(curIndex+5, fileList_1Dim.count);
			setMediaState(true, " start:"+start+" end:"+end);
			for (i=start; i<end; i++) {
				File file = (File)fileList_1Dim.getItem(i);
				String path = file.getAbsolutePath();
				result[count++] = path;
				setMediaState(true, " "+path+" cached.");
			}
			countOfCachedFileList = count;
		}
		
		void load(int[] result, String[] cachedFileList) {
			int i;
			setMediaState(true, " countOfCachedFileList:"+countOfCachedFileList);
			for (i=0; i<countOfCachedFileList; i++) {
				result[i] = soundPool.load(cachedFileList[i], 1);
				setMediaState(true, " SoundID:"+result[i]+" loaded.");
			}
			countOfLoadedSounds = i;
		}
		
		void unload(int[] cachedSounds) {
			int i;
			boolean r;
			for (i=0; i<countOfLoadedSounds; i++) {
				r = soundPool.unload(cachedSounds[i]);
				if (r) setMediaState(true, " SoundID:"+cachedSounds[i]+" unloaded.");
			}
		}
		
		public void run() {
			getCachedFileList(fileListString, fileList_1Dim, indexOfFileList_1Dim);
			//setMediaState(true, " FileList cached.");
			load(arrSoundID, fileListString);
			//setMediaState(true, " Sounds loaded.");
			
			int i;
			for (i=0; i<countOfLoadedSounds; i++) {
				int streamID = soundPool.play(arrSoundID[i], leftVolume, rightVolume, priority, loop, playbackRate);
				if (streamID!=0) setMediaState(true, " "+arrSoundID[i]+ " Sounds played.");
				else setMediaState(true, " "+arrSoundID[i]+ " Sounds not played.");
			}
			
		}
		
		public void exit() {
			try {
				unload(arrSoundID);
				setMediaState(true, " Sounds unloaded.");
				soundPool.release();
				soundPool = null;
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				
			}
		}
	}
}