package android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.gsoft.common.IO.FileHelper;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.JavaSoundAudioDeviceFactory;
import javazoom.jl.player.Player;
import javazoom.jl.decoder.*;

import android.content.Context;
import android.net.Uri;

public class media {

	public static class SoundPool {

		public int load(String path, int priority) {
			// TODO Auto-generated method stub
			return 0;
		}

		public void release() {
			// TODO Auto-generated method stub
			
		}

		public boolean unload(int soundID) {
			// TODO Auto-generated method stub
			return false;
		}

		public int play(int soundID, float leftVolume, float rightVolume,
				int priority, int loop, float playbackRate) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	public static class MediaRecorder {

		public static class AudioSource {
			public static int MIC = 1;
		}

		public static class OutputFormat {

			public static final int THREE_GPP = 1;
			
		}

		public static class AudioEncoder {

			public static final int AMR_NB = 1;
			
		}

		public void setAudioSource(int mIC) {
			// TODO Auto-generated method stub
			
		}

		public void setOutputFormat(int threeGpp) {
			// TODO Auto-generated method stub
			
		}

		public void setAudioEncoder(int amrNb) {
			// TODO Auto-generated method stub
			
		}

		public void setOutputFile(String recordFile) {
			// TODO Auto-generated method stub
			
		}

		public void prepare() {
			// TODO Auto-generated method stub
			
		}

		public void start() {
			// TODO Auto-generated method stub
			
		}

		public void stop() {
			// TODO Auto-generated method stub
			
		}

		public void reset() {
			// TODO Auto-generated method stub
			
		}

		public void release() {
			// TODO Auto-generated method stub
			
		}

	}
	
	
	/*public static class MediaPlayer {

		
		public interface OnCompletionListener {
			public void onCompletion(MediaPlayer mp);
		}
		
		OnCompletionListener onCompletionListener;
		Player player;
		int mCurPosition;
		MusicThread thread;
		Uri uri;

		public static MediaPlayer create(Context context, Uri uri) {
			// TODO Auto-generated method stub
			
			return null;
		}

		public void setVolume(float leftVolume, float rightVolume) {
			// TODO Auto-generated method stub
			
		}

		public int getDuration() {
			// TODO Auto-generated method stub
			return 100000;
		}

		public int getCurrentPosition() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void setDataSource(String path) {
			// TODO Auto-generated method stub
			
		}

		public void prepare() throws Exception {
			// TODO Auto-generated method stub
			
		}

		public void seekTo(int curPos) {
			// TODO Auto-generated method stub
			mCurPosition = curPos;
		}

		public void setOnCompletionListener(OnCompletionListener listener) {
			// TODO Auto-generated method stub
			onCompletionListener = listener;
		}
		
		static class MusicThread extends Thread {
			MediaPlayer player;
			public MusicThread(MediaPlayer player) {
				this.player = player;
			}
			public void run() {
				
			}
		}

		public void start() {
			// TODO Auto-generated method stub
			thread = new MusicThread(this);
			thread.start();
		}

		
		public void pause() {
			// TODO Auto-generated method stub
		}

		public void release() {
			// TODO Auto-generated method stub
			mCurPosition = 0;
		}

		public void reset() {
			// TODO Auto-generated method stub
			mCurPosition = 0;
		}

		public void stop() {
			// TODO Auto-generated method stub
			
		}

	}*/
	

	/*public static class MediaPlayer extends Player {

		public MediaPlayer(InputStream arg0, Uri uri) throws JavaLayerException {
			super(arg0);			
			// TODO Auto-generated constructor stub
			this.uri = uri;
		}

		public interface OnCompletionListener {
			public void onCompletion(MediaPlayer mp);
		}
		
		OnCompletionListener onCompletionListener;
		Player player;
		int mCurPosition;
		MusicThread thread;
		Uri uri;

		public static MediaPlayer create(Context context, Uri uri) {
			// TODO Auto-generated method stub
			InputStream is = null;
			MediaPlayer player = null;
			
			try {
				is = new FileInputStream(uri.file);
				player = new MediaPlayer(is, uri);
				int position = player.getPosition();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return player;
		}

		public void setVolume(float leftVolume, float rightVolume) {
			// TODO Auto-generated method stub
			
		}

		public int getDuration() {
			// TODO Auto-generated method stub
			return 100000;
		}

		public int getCurrentPosition() {
			// TODO Auto-generated method stub
			return this.getPosition();
		}

		public void setDataSource(String path) {
			// TODO Auto-generated method stub
			
		}

		public void prepare() throws Exception {
			// TODO Auto-generated method stub
			
		}

		public void seekTo(int curPos) {
			// TODO Auto-generated method stub
			mCurPosition = curPos;
		}

		public void setOnCompletionListener(OnCompletionListener listener) {
			// TODO Auto-generated method stub
			onCompletionListener = listener;
		}
		
		static class MusicThread extends Thread {
			MediaPlayer player;
			public MusicThread(MediaPlayer player) {
				this.player = player;
			}
			public void run() {
				try {
					player.play();
				} catch (JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void start() {
			// TODO Auto-generated method stub
			thread = new MusicThread(this);
			thread.start();
		}

		public void close() {
			super.close();
		}
		
		public void pause() {
			// TODO Auto-generated method stub
			mCurPosition = this.getPosition();
			//thread.interrupt();
			super.close();
			//this.player = MediaPlayer.create(Control.activity.context, uri);
		}

		public void release() {
			// TODO Auto-generated method stub
			mCurPosition = 0;
		}

		public void reset() {
			// TODO Auto-generated method stub
			mCurPosition = 0;
		}

		public void stop() {
			// TODO Auto-generated method stub
			
		}

	}*/
	
	
	public static class MyJavaSoundAudioDeviceFactory extends JavaSoundAudioDeviceFactory {
		public MyJavaSoundAudioDeviceFactory() {
			super();
		}
		public JavaSoundAudioDevice createAudioDeviceImpl() throws JavaLayerException {
			JavaSoundAudioDevice r = super.createAudioDeviceImpl();
			return r;
		}
	}
	
	public static class MyJavaSoundAudioDevice extends JavaSoundAudioDevice {
		public MyJavaSoundAudioDevice() {
			super();
		}
	}
	
	
	public static class MediaPlayer  {

		enum State {
			Init,
			Pause,
			Play,
			Closed
		}

		


		public interface OnCompletionListener {
			public void onCompletion(MediaPlayer mp);
		}
		
		OnCompletionListener onCompletionListener;
		//Player player;
		int mCurPosition;
		MusicThread thread;
		Uri uri;
		int countOfFrames;
		int mDuration;
		JavaSoundAudioDevice device;
		Player mPlayer;
		InputStream mIs;
		State state;
		
		public MediaPlayer(InputStream is, JavaSoundAudioDevice device,
				int countOfFrames) {
			// TODO Auto-generated constructor stub
			try {
				mPlayer = new Player(is, device);
				state = State.Init; 
			} catch (JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.mIs = is;
			this.device = device;
			this.countOfFrames = countOfFrames;
		}

		public static JavaSoundAudioDevice createDevice() {
			MyJavaSoundAudioDeviceFactory factory = new MyJavaSoundAudioDeviceFactory();
			try {
				factory.testAudioDevice();
				JavaSoundAudioDevice device = factory.createAudioDeviceImpl();
				return device;
			} catch (JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}

		public static MediaPlayer create(Context context, Uri uri) {
			// TODO Auto-generated method stub
			InputStream is = null;
			MediaPlayer player = null;			
			
			try {
				is = new FileInputStream(uri.file);
				Bitstream bitstream = new Bitstream(is);
				
				/*Decoder.Params params = new Decoder.Params();
				params.setOutputChannels(OutputChannels.BOTH);
				Decoder decoder = new Decoder(params);*/
				
				
				
				JavaSoundAudioDevice device = createDevice();
				
				
				//PCM_SIGNED 44100.0 Hz, 16 bit, stereo, 4 bytes/frame, little-endian
				/*AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
						44100, 16, 2, 413, 4, false);
				Equalizer equalizer = new Equalizer();*/ 
				
				Header header = bitstream.readFrame();
				
				int fileSize = (int) FileHelper.getFileSize(uri.file.getAbsolutePath());
				int countOfFrames = (int) (fileSize / header.framesize);
				//float msPerFrame = header..ms_per_frame();
				int duration = (int) header.total_ms(fileSize);
				
				
				
				/*device.open(decoder);
				device.open(format);
				
				SampleBuffer samepleBuffer = new SampleBuffer(44100, 2);
				decoder.setOutputBuffer(samepleBuffer);
				for (int i=0; i<10; i++) {
					//header = bitstream.readFrame();
					SampleBuffer buffer = (SampleBuffer) decoder.decodeFrame(header, bitstream);
					short[] sBuf = buffer.getBuffer();
					device.write(sBuf, 0, sBuf.length);
				
				}*/
				
			
				
				MediaPlayer p = new MediaPlayer(is, device, countOfFrames);
				p.mDuration = duration;
				p.uri = uri;
				
				return p;
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
			return player;
		}

		public void setVolume(float leftVolume, float rightVolume) {
			// TODO Auto-generated method stub
			
		}

		public int getDuration() {
			// TODO Auto-generated method stub
			return this.mDuration;
		}

		public int getCurrentPosition() {
			return mCurPosition;
			// TODO Auto-generated method stub
		}

		public void setDataSource(String path) {
			// TODO Auto-generated method stub
			this.uri = new Uri(new File(path));
		}

		public void prepare() throws Exception {
			// TODO Auto-generated method stub
			
		}

		public void seekTo(int curPos) {
			// TODO Auto-generated method stub
			mCurPosition = curPos;
		}

		public void setOnCompletionListener(OnCompletionListener listener) {
			// TODO Auto-generated method stub
			onCompletionListener = listener;
		}
		
		static class MusicThread extends Thread {
			MediaPlayer player;
			public MusicThread(MediaPlayer player) {
				this.player = player;
			}
			public void run() {
				try {
					//player.mPlayer.play(player.countOfFrames);
					player.mPlayer.play();
					if (player.mPlayer.isComplete()) {
						player.onCompletionListener.onCompletion(player);
					}
					 
				} catch (JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		

		public void start() {
			// TODO Auto-generated method stub
			if (state==State.Pause || state==State.Closed) {
				device = createDevice();
				try {
					InputStream is = new FileInputStream(uri.file);
					Bitstream bitstream = new Bitstream(is);
					Header header = bitstream.readFrame();
					
					int fileSize = (int) FileHelper.getFileSize(uri.file.getAbsolutePath());
					int countOfFrames = (int) (fileSize / header.framesize);
					//float msPerFrame = header..ms_per_frame();
					mDuration = (int) header.total_ms(fileSize);
					//long n = is.skip(mCurPosition);
					//mIs.reset();
					mPlayer = new Player(is, device);
				} catch (JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
							
			}
			thread = new MusicThread(this);
			thread.start();
			state = State.Play;
		}

		
		public void pause() {
			// TODO Auto-generated method stub
			//this.mCurPosition = mPlayer.getPosition();
			mCurPosition = 0;
			mPlayer.close();
			device.close();
			state = State.Pause;
		}

		public void release() {
			// TODO Auto-generated method stub
			mCurPosition = 0;
			mPlayer.close();
			device.close();
			state = State.Closed;
		}

		public void reset() {
			// TODO Auto-generated method stub
			mCurPosition = 0;
			mPlayer.close();
			device.close();
			state = State.Closed;
		}

		public void stop() {
			// TODO Auto-generated method stub
			mCurPosition = 0;
			mPlayer.close();
			device.close();
			state = State.Pause;
		}

	}

}