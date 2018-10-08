package com.gsoft.DataTransfer.pipe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import com.gsoft.common.Code.CodeString;
import com.gsoft.common.CommonGUI;
import com.gsoft.common.Compiler_types.Language;
import com.gsoft.common.IO;
import com.gsoft.common.IO.FileHelper.LanguageAndTextFormat;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.Util.ArrayListString;
import com.gsoft.common.interfaces.OnTouchListener;
import com.gsoft.common.gui.Control;

public class Pipe {
	/** createCompilerProcess()에서 생성된 서브 프로세스들의 리스트, 
	 * listOfProcesses,listOfPathOfOpenProcesses,listOfSockets,listOfSocketStream,
	 * listOfThreadPipeForReceivingOfMain,listOfSocketsForReceivingOfMain, 
	 * listOfSocketStreamForReceivingOfMain의 인덱스는
	 * 모두 같은 서브 프로세스를 가리킨다.*/
	public static ArrayList listOfProcesses = new ArrayList(10);
	
	/** createCompilerProcess()에서 생성된 서브 프로세스들이 읽어들일 파일 경로 리스트,
	 * listOfProcesses,listOfPathOfOpenProcesses,listOfSockets,listOfSocketStream,
	 * listOfThreadPipeForReceivingOfMain,listOfSocketsForReceivingOfMain, 
	 * listOfSocketStreamForReceivingOfMain의 인덱스는
	 * 모두 같은 서브 프로세스를 가리킨다.*/
	public static ArrayListString listOfPathOfOpenProcesses = new ArrayListString(10);
	
	/** 메인 프로세스(TextEditorForJava.jar)가 갖는 송신용 Socket[],
	 * listOfProcesses,listOfPathOfOpenProcesses,listOfSockets,listOfSocketStream,
	 * listOfThreadPipeForReceivingOfMain,listOfSocketsForReceivingOfMain, 
	 * listOfSocketStreamForReceivingOfMain의 인덱스는
	 * 모두 같은 서브 프로세스를 가리킨다.*/
	public static ArrayList listOfSockets = new ArrayList(10);
	/**listOfSockets 소켓의 송신용 OutputStream[],
	 * listOfProcesses,listOfPathOfOpenProcesses,listOfSockets,listOfSocketStream,
	 * listOfThreadPipeForReceivingOfMain,listOfSocketsForReceivingOfMain, 
	 * listOfSocketStreamForReceivingOfMain의 인덱스는
	 * 모두 같은 서브 프로세스를 가리킨다.*/
	public static ArrayList listOfSocketStream = new ArrayList(10);
	
	
	
	/** createCompilerProcess()에서 생성된 프로세스들과 수신을 하기위한 스레드들의 리스트,
	 * listOfProcesses,listOfPathOfOpenProcesses,listOfSockets,listOfSocketStream,
	 * listOfThreadPipeForReceivingOfMain,listOfSocketsForReceivingOfMain, 
	 * listOfSocketStreamForReceivingOfMain의 인덱스는
	 * 모두 같은 서브 프로세스를 가리킨다.*/
	public static ArrayList listOfThreadPipeForReceivingOfMain = new ArrayList(10);
	
	/** 메인 프로세스(TextEditorForJava.jar)가 갖는 수신용 Socket[],
	 * listOfProcesses,listOfPathOfOpenProcesses,listOfSockets,listOfSocketStream,
	 * listOfThreadPipeForReceivingOfMain,listOfSocketsForReceivingOfMain, 
	 * listOfSocketStreamForReceivingOfMain의 인덱스는
	 * 모두 같은 서브 프로세스를 가리킨다.*/
	public static ArrayList listOfSocketsForReceivingOfMain = new ArrayList(10);
	/**listOfSocketsForReceiving 소켓의 수신용 InputStream[],
	 * listOfProcesses,listOfPathOfOpenProcesses,listOfSockets,listOfSocketStream,
	 * listOfThreadPipeForReceivingOfMain,listOfSocketsForReceivingOfMain, 
	 * listOfSocketStreamForReceivingOfMain의 인덱스는
	 * 모두 같은 서브 프로세스를 가리킨다.*/
	public static ArrayList listOfSocketStreamForReceivingOfMain = new ArrayList(10);
	
	
	
	
	
	/** 메인 프로세스에서 수신용 소켓을 사용하는 스레드의 listener*/
	public static OnTouchListener listener;
	
	/** 서브 프로세스에서 메인으로 가는 송신용 소켓, 
	 * ThreadPipe에서 createClientSocketToMain()호출을 통해 생성되고 
	 * destroySubProcess()에서 close()된다.*/
	public static Socket socketToMain_client;
	
	public static InetAddress localHost;
	public static final int StartPortNum = 24001;
	public static final int StartPortNumForRecevingOfMain = 25001;
	
	/**메인프로세스의 createCompilerProcess()에서 서브프로세스가 생성될때 환경변수를 통해
	 * 쓰여지면 서브프로세스의 MainActivity의 main()에서 읽혀진다. 
	 * Control.numOfCurProcess와 값이 같게 된다.
	 */
	public static int countOfCreatedProcesses = 0;
	
	
	/** Control.isMasterOrSlave(MainActivity의 인자갯수에 따라 구분)가 
	 * true일때 즉 매스터일때 실제로 일을 하는 TextEditorForJava
	 * 프로세스를 생성(Pipe.countOfCreatedProcesses 이 javaw.exe -jar TextEditorForJava.jar 뒤에 인자로 붙는 것을 주의한다.)하고 
	 * 생성된 서버당 하나씩 소켓을 생성하여 연결하고 로드할 filePath를 소켓스트림에 쓴다.
	 * @param filePath : 서버(Slave)에서 로드해야할 파일 패스
	 * @return : 생성된 서버(Slave) 프로세스
	 */
	public static Process createCompilerProcess(String filePath) {
		String[] driveList = {"C:", "D:", "E", "F:", "G:", "H", "A:", "B:"};
		String[] programFilesList = {"Program Files (x86)", "Program Files"};

		int m, n;
		boolean found = false;
		String[] jrePathList = null;

		String drive;
		String programFiles;
		String java = "java";
		String bin = "bin";
		String javaPath = null;

		for (m=0; m<driveList.length; m++) {
			for (n=0; n<programFilesList.length; n++) {
				drive = driveList[m];
				programFiles = programFilesList[n];

				javaPath = drive + File.separator + programFiles + File.separator + java;
				File file = new File(javaPath);
				if (file.exists()) {
					jrePathList = file.list();
					found = true;
					break;
				}
			}
			if (found) break;
		}

		if (jrePathList==null) return null;


		// jre나 jdk 디렉토리
		int i;
		String jdkPath = null;
		String jrePath = null;
		String binPath = null;
		String result = null;
		for (i=0; i<jrePathList.length; i++) {
			if (jrePathList[i].contains("jre")) {
				jrePath = javaPath + File.separator + jrePathList[i];
				binPath = jrePath + File.separator + bin;
				result = binPath + File.separator + "javaw.exe";
				break;
			}
			else if (jrePathList[i].contains("jdk")) {
				jdkPath = javaPath + File.separator + jrePathList[i];
				binPath = jdkPath + File.separator + bin;
				result = binPath + File.separator + "javaw.exe";
				break;
			}
		}

		
		if (result==null) {
		}

		
		File jarFile = new File(System.getProperties().getProperty("user.dir")  + File.separator + "TextEditorForJava.jar");

		boolean jarFileExists = jarFile.exists();
		boolean javaFileExists = (new File(result)).exists();

		
		
		if (javaFileExists && jarFileExists) {
					
			// Pipe.countOfCreatedProcesses 이 javaw.exe -jar TextEditorForJava.jar 뒤에 인자로 붙는 것을 주의한다.
			ProcessBuilder pb = new ProcessBuilder(result, "-jar", jarFile.getAbsolutePath(), Pipe.countOfCreatedProcesses+"");
			Map<String, String> map = pb.environment();
			map.put("countOfCreatedProcesses", Pipe.countOfCreatedProcesses+"");
			List<String> list = pb.command();
			
			
			
			// TextEditorForJava 프로세스를 생성한다. 이 프로세스는 ServerSocket을 생성하고
			// 연결될 준비를 해야 한다.
			try {
				Process process = pb.start();
				Pipe.listOfProcesses.add(process);
				Pipe.listOfPathOfOpenProcesses.add(filePath);
				//process.waitFor();
				Thread.sleep(2000); // sub 프로세스의 소켓이 바운드될때까지 기다려야 한다.
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
					
			
			
			// 서버(slave) 당 하나씩 소켓을 생성하여 연결하고 로드할 filePath를 소켓스트림에 쓴다.
			try {
				if (localHost==null) localHost = InetAddress.getLocalHost();
				
				int portNum = Pipe.countOfCreatedProcesses + Pipe.StartPortNum;
				if (portNum==24003) {
					int a;
					a=0;
					a++;
				}
				// sub 프로세스의 소켓이 바운드될때까지 기다려야 하므로 루프를 돈다.
				// sub 프로세스의 소켓에 연결한다.
				Socket socket = null;
				while (true) {
					try {
					socket = new Socket(localHost, portNum);
					break;
					}catch(Exception e) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						int a;
						a=0;
						a++;
					}
				}
				Pipe.listOfSockets.add(socket);
				
				Pipe.countOfCreatedProcesses++;
				
				OutputStream socketStream = socket.getOutputStream();
				Pipe.listOfSocketStream.add(socketStream);
				
				// sub 프로세스의 Control.numOfCurProcess가 된다.
				IO.writeString(socketStream, Pipe.listOfPathOfOpenProcesses.count-1+"", TextFormat.UTF_8, true, true);
				// sub 프로세스가 읽어들일 파일 경로
				IO.writeString(socketStream, filePath, TextFormat.UTF_8, true, true);
				
				
				// 메인 프로세스에서 sub 프로세스의 메시지를 수신하기 위한 스레드들을 생성한다.
				// 포트번호는 메인 프로세스의 송신용 포트번호에 1000을 더한다.
				ThreadPipeForReceivingOfMain thread = new ThreadPipeForReceivingOfMain(portNum+1000, listener);
				listOfThreadPipeForReceivingOfMain.add(thread);
				thread.start();
				
				
			} 
			catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		
		}
		return null;
		
	}
	
	/** 메인 프로세스에서 원하는 서브 프로세스를 focus 한다.*/
	public static void focusSubProcess(int numOfSubProcess) {
		Socket socket = (Socket)(Pipe.listOfSockets.getItem(numOfSubProcess));
		OutputStream os;
		try {
			os = socket.getOutputStream();
			IO.writeString(os, "focus", TextFormat.UTF_8, true, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/** 메인 프로세스에서 numOfProcess를 제외한 모든 서브 프로세스를 toBack 한다.*/
	public static void toBackAllSubProcessExceptOne(int numOfProcess) {
		int i;
		for (i=0; i<listOfSockets.count; i++) {
			if (i==numOfProcess) continue;
			Socket socket = (Socket) listOfSockets.getItem(i);
			if (socket==null || socket.isClosed()) continue;
			try {
				OutputStream os = socket.getOutputStream();
				IO.writeString(os, "toBack", TextFormat.UTF_8, true, true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/** 메인 프로세스에서 원하는 서브 프로세스를 toBack() 한다.*/
	public static void toBackSubProcess(int numOfSubProcess) {
		Socket socket = (Socket)(Pipe.listOfSockets.getItem(numOfSubProcess));
		OutputStream os;
		try {
			os = socket.getOutputStream();
			IO.writeString(os, "toBack", TextFormat.UTF_8, true, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/** Control.isMasterOrSlave가 true일 경우 생성한 프로세스들을 모두 종료시킨다.*/
	public static void destroyCompilerProcesses() {
		int i;
		for (i=0; i<Pipe.listOfProcesses.count; i++) {
			if (Pipe.listOfProcesses!=null) {
				Process process = (Process) Pipe.listOfProcesses.getItem(i);
				if (process!=null) process.destroy();
			}
		}
	}
	
	/** 메인프로세스에서 사용하는 수신용 스레드들과 송신용 소켓들을 모두 닫고 
	 * 서브 프로세스들을 종료시킨다.*/
	public static void destroyMainProcess() {
		int i;
		for (i=0; i<Pipe.listOfSockets.count; i++) {
			Socket socket = (Socket) Pipe.listOfSockets.getItem(i);
			if (socket!=null)
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
		}
		Pipe.destroyCompilerProcesses();
		Pipe.destroyListOfThreadPipeForReceivingOfMain();
	}
	
	
	
	/** 서브 프로세스에서 수신용 소켓을 사용하는 스레드*/
	public static class ThreadPipe extends Thread {
		boolean exits;
		ServerSocket socket = null;
		Socket connectedSocket = null;
		public int numOfCurProcess = -1;
		
		public void run() {
			
			Pipe.socketToMain_client = Pipe.createClientSocketToMain(Control.numOfCurProcess);
			
			InputStream inputStream = null;
			
			try {
				//int portNum = Pipe.countOfCreatedProcesses + Pipe.StartPortNum;
				int portNum = Pipe.countOfCreatedProcesses + Pipe.StartPortNum;
				if (portNum==2003) {
					int a;
					a=0;
					a++;
				}
				socket = new ServerSocket(portNum);
				
				CommonGUI.loggingForNetwork.setText(true, "Listening to port "+portNum, false);
				CommonGUI.loggingForNetwork.setHides(false);
				Control.view.postInvalidate();
				
				
				connectedSocket = socket.accept();
				
				inputStream = connectedSocket.getInputStream();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			int i = 0;
			while (exits==false) {
				String message = null;
				try {					
					message = IO.readStringUTF8(inputStream, null);
					
					if (message!=null) { 
						// i 가 0이나 1은 createCompilerProcess()에서 sub 프로세스로 보내진다.
						if (i==0) {
							// sub 프로세스의 일련번호, 메인프로세스의 listOfPathOfOpenProcesses의 인덱스이다.
							int numOfCurProcess = Integer.parseInt(message);
							this.numOfCurProcess = numOfCurProcess;
							Control.numOfCurProcess = numOfCurProcess;
						}
						else if (i==1) {//프로세스가 읽어들일 파일의 경로
							LanguageAndTextFormat languageAndTextFormat = 
								FileHelper.getLanguageAndTextFormat(message);
							boolean r = 
								CommonGUI.editText_compiler.setIsProgramCode(languageAndTextFormat.lang, message, languageAndTextFormat.format);
							
							if (r) {
								CommonGUI.editText_compiler.changeFontSize(Control.view.getHeight() * 0.03f); 
								CommonGUI.editText_compiler.setText(0, CommonGUI.editText_compiler.getCompileOutput(Language.Java));
								Control.view.postInvalidate();
							}
						}
						else {
							if (message.equals("focus")) {
								//Control.view.setAutoRequestFocus(true);
								//Control.view.setFocusableWindowState(true);
								Control.view.setVisible(true);
								Control.view.toFront();
								Control.view.postInvalidate();
							}
							else if (message.equals("toBack")) {
								Control.view.setVisible(false);
								Control.view.toBack();
								Control.view.postInvalidate();
							}
						}
					}//if (message!=null) {
					Thread.sleep(1000);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} finally {
					i++;
				}
			}//while (true) {
			
			if (socket!=null)
				try {
					socket.close();
				} catch (IOException e) {
				}
			if (connectedSocket!=null)
				try {
					connectedSocket.close();
				} catch (IOException e) {
				}
			if (inputStream!=null)
				try {
					inputStream.close();
				} catch (IOException e) {
				}
		}
		
		public void destroy() {
			exits = true;
			if (socket!=null)
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (connectedSocket!=null)
				try {
					connectedSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}//ThreadPipe
	
	
	
	/** 서브프로세스에서 메인프로세스에 종료 메시지를 보낸다.
	 * "close"-numOfProcess(Control.numOfCurProcess) 순으로 보내진다.*/
	public static void destroySubProcess() {		
		try {
			//Pipe.socketToMain_client = Pipe.createClientSocketToMain(Control.numOfCurProcess);
			Socket socket = Pipe.socketToMain_client;
			if (socket==null) return;
			OutputStream os = socket.getOutputStream();
			IO.writeString(os, "close", TextFormat.UTF_8, true, true);
			IO.writeString(os, Control.numOfCurProcess+"", TextFormat.UTF_8, true, true);
			if (Pipe.socketToMain_client!=null)
				Pipe.socketToMain_client.close();
			//socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** Pipe.socketToMain_client(서브프로세스에서 메인으로의 송신용소켓)을 만들고 리턴한다.*/
	public static Socket createClientSocketToMain(int numOfCurProcess) {
		try {
			if (Pipe.socketToMain_client!=null) return socketToMain_client;
			if (localHost==null) localHost = InetAddress.getLocalHost();			
			int portNum = Control.numOfCurProcess + Pipe.StartPortNumForRecevingOfMain;
			// 메인 프로세스의 소켓에 연결한다.
			Socket socket = new Socket(localHost, portNum);
			socketToMain_client = socket;
			return socket;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return null;
	}
	
	/** 서브프로세스에서 메인프로세스에 rename 메시지를 보낸다. 
	 * "rename"-numOfProcess(Control.numOfCurProcess)-바뀐 filePath 순으로 보내진다.*/
	public static void renameProcess(int numOfCurProcess, String filePath) {
		try {
			Pipe.socketToMain_client = Pipe.createClientSocketToMain(numOfCurProcess);
			Socket socket = Pipe.socketToMain_client;
			OutputStream os = socket.getOutputStream();
			IO.writeString(os, "rename", TextFormat.UTF_8, true, true);
			IO.writeString(os, Control.numOfCurProcess+"", TextFormat.UTF_8, true, true);
			IO.writeString(os, filePath, TextFormat.UTF_8, true, true);
			//socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 메인프로세스에서 서브 프로세스를 죽인다.*/
	public static void destroySubProcess(int numOfSubProcess) {
		Process process = (Process) Pipe.listOfProcesses.getItem(numOfSubProcess);
		if (process!=null) {
			process.destroy();
		}
		// Pipe 의 프로세스 리스트를 갱신한다.
		Pipe.listOfProcesses.list[numOfSubProcess] = null;
		Pipe.listOfPathOfOpenProcesses.list[numOfSubProcess] = null;
		Socket socket = (Socket) Pipe.listOfSockets.list[numOfSubProcess];
		try {
			socket.close();
		} catch (IOException e1) {
		}
		// Pipe 의 수신용 스레드를 종료한다.
		ThreadPipeForReceivingOfMain thread = (ThreadPipeForReceivingOfMain) Pipe.listOfThreadPipeForReceivingOfMain.list[numOfSubProcess];
		thread.destroy();
		Pipe.listOfThreadPipeForReceivingOfMain.list[numOfSubProcess] = null;
	}
	
	/** 메인 프로세스에서 수신용 스레드들을 모두 종료시킨다.*/
	public static void destroyListOfThreadPipeForReceivingOfMain() {
		int i;
		for (i=0; i<Pipe.listOfThreadPipeForReceivingOfMain.count; i++) {
			ThreadPipeForReceivingOfMain thread = (ThreadPipeForReceivingOfMain) Pipe.listOfThreadPipeForReceivingOfMain.getItem(i);
			if (thread!=null) {
				thread.destroy();
				thread = null;
			}
		}
	}
	
	/** 메인 프로세스에서 수신용 소켓을 사용하는 스레드*/
	public static class ThreadPipeForReceivingOfMain extends Thread {
		boolean exits;
		ServerSocket socket = null;
		Socket connectedSocket = null;
		private int portNum;
		/** 메인 프로세스에서 수신용 소켓을 사용하는 스레드의 listener*/
		private OnTouchListener listener;
		ThreadPipeForReceivingOfMain(int portNum, OnTouchListener listener) {
			this.portNum = portNum;
			this.listener = listener;
		}
		public void run() {
			
			InputStream inputStream = null;
			
			try {
				//int portNum = Pipe.countOfCreatedProcesses + Pipe.StartPortNumForRecevingOfMain;
				
				socket = new ServerSocket(portNum);
				
				connectedSocket = socket.accept();
				
				inputStream = connectedSocket.getInputStream();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
			}catch (Exception e2) {
				// TODO Auto-generated catch block
			}
			
			while (exits==false) {
				String message = null;
				try {					
					message = IO.readStringUTF8(inputStream, null);
					
					if (message!=null && message.equals("")==false) {
						if (message.equals("close")) {
							// "close"-numOfProcess(Control.numOfCurProcess)
							String message2 = IO.readStringUTF8(inputStream, null);
							int numOfProcess = Integer.parseInt(message2);
							Process process = (Process) Pipe.listOfProcesses.getItem(numOfProcess);
							//process.destroy();
							Pipe.listOfProcesses.list[numOfProcess] = null;
							
							Pipe.listOfPathOfOpenProcesses.list[numOfProcess] = null;
							
							Socket socket = (Socket) Pipe.listOfSockets.list[numOfProcess];
							try{
							socket.close();
							} catch (IOException e) {
							}
							Pipe.listOfSockets.list[numOfProcess] = null;
							
							ThreadPipeForReceivingOfMain thread = (ThreadPipeForReceivingOfMain) Pipe.listOfThreadPipeForReceivingOfMain.list[numOfProcess];
							thread.destroy();
							Pipe.listOfThreadPipeForReceivingOfMain.list[numOfProcess] = null;
							
							exits = true;
							Control.view.postInvalidate();
						}
						else if (message.equals("rename")) {
							//"rename"-numOfProcess(Control.numOfCurProcess)-바뀐 filePath
							// 순으로 보내진다.
							String message2 = IO.readStringUTF8(inputStream, null);
							int numOfProcess = Integer.parseInt(message2);
							String message3 = IO.readStringUTF8(inputStream, null);
							String path = message3;
							Pipe.listOfPathOfOpenProcesses.list[numOfProcess] = path;
							Control.view.postInvalidate();
						}
						else if (message.equals("logWrite")) {
							//"logWrite"-numOfProcess(Control.numOfCurProcess)-log message
							// 순으로 보내진다.
							String message2 = IO.readStringUTF8(inputStream, null);
							int numOfProcess = Integer.parseInt(message2);
							String message3 = IO.readStringUTF8(inputStream, null);
							CodeString text = new CodeString(message3, CommonGUI.textViewLogBird.textColor);
							CommonGUI.textViewLogBird.setText(CommonGUI.textViewLogBird.numOfLines, text);
							CommonGUI.textViewLogBird.vScrollPosToLastPage();
							Control.view.postInvalidate();
							
						}
						if (listener!=null) listener.onTouchEvent(this, null);
						
						
						
					}//if (message!=null) {
					Thread.sleep(1000);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} finally {
				}
			}//while (exits==false) {
			
			if (socket!=null)
				try {
					socket.close();
					socket = null;
				} catch (IOException e) {
				}
			if (connectedSocket!=null)
				try {
					connectedSocket.close();
					connectedSocket = null;
				} catch (IOException e) {
				}
			if (inputStream!=null)
				try {
					inputStream.close();
					inputStream = null;
				} catch (IOException e) {
				}
			
		}
		
		public void destroy() {
			exits = true;
			if (socket!=null)
				try {
					socket.close();
					socket = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (connectedSocket!=null)
				try {
					connectedSocket.close();
					connectedSocket = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}//ThreadPipeForReceivingOfMain
}