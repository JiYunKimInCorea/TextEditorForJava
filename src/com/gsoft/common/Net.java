package com.gsoft.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.gsoft.common.CompilerHelper;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Util.Array;
import com.gsoft.common.Util.ArrayListString;
import com.gsoft.common.Util.BufferByte;
import com.gsoft.common.gui.Control;
import com.gsoft.common.gui.ProgressBar;
import com.gsoft.common.interfaces.Listener;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.View;

public class Net {
	
	private static int fileCountToSend;
	private static int fileCountSent;
	private static long totalFileSize;
	
	public static void setTotalFileSize(long size) {
		totalFileSize = size;
	}
	
	public static void setFileCountToSend(int count) {
		fileCountToSend = count;
	}
	
	public static byte[] toBytes(short[] ip) {
		byte[] r = new byte[4];
		int i;
		for (i=0; i<ip.length; i++) {
			r[i] = (byte) (ip[i]&0xff);
		}
		return r;
	}
	
	public static short[] toShort(byte[] ip) {
		short[] r = new short[4];
		int i;
		for (i=0; i<ip.length; i++) {
			r[i] = (short) (ip[i]&0xff);
		}
		return r;
	}
	
	public static class WifiThread extends Thread {
		View view = Control.view;
		//WifiManager m;
		boolean isWifiEnabled;
		byte[] serverIpAddress;
		
		String wifiState="";
		String bluetoothState="";
		String macAddress;
		String nickname = "abcde";
		ArrayListString fileListOfMultiSelect;
		
		Socket socket=null;	// client socket
			
		public WifiThread(View view, byte[] serverIp, ArrayListString fileListOfMultiSelect) {
			this.view = view;
			this.serverIpAddress = serverIp;
			this.fileListOfMultiSelect = fileListOfMultiSelect;
		}
		public void run() {
			try {
				Wifi.isRunning = true;
				doWifi();
			}
			finally {
				Wifi.isRunning = false;
				Wifi.wifiManager.setWifiEnabled(false);
			}
			
		}
		
		public void killThread() {
			if (isAlive()) {
				if (socket!=null) {
					try {
						socket.close();
						socket = null;
					} catch (IOException e) {
						e.printStackTrace();
						CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
					}
				}
				/*interrupt();
				try {
					join();
				} catch (InterruptedException e) {
					e.printStackTrace();
					CompilerHelper.printStackTrace(Control.textViewLogBird, e);
				}*/
			}			
		}
		
		void setIpAndMacAndNickname(byte[] ipAndMacAndNickname, byte[] ipAddressLocal, 
				byte[] bytesMacAddress, byte[] bytesNickname) {
			int offset=0;
			Array.Copy(ipAddressLocal, 0, ipAndMacAndNickname,offset, ipAddressLocal.length);
			offset += ipAddressLocal.length;
			Array.Copy(bytesMacAddress, 0, ipAndMacAndNickname,offset, bytesMacAddress.length);
			offset += bytesMacAddress.length;
			Array.Copy(bytesNickname, 0, ipAndMacAndNickname,offset, bytesNickname.length);
		}
		
		void doWifi() {
			WifiInfo info=null;
			try {
				info = Wifi.startWifi(view);
				if (info==null) {
					Wifi.setWifiState(false, "can't connnect to server");
				}
				else {
					boolean ping = Wifi.wifiManager.pingSupplicant();
					if (ping) Wifi.setWifiState(false, "ping success");
					else Wifi.setWifiState(false, "ping fails");
					
					byte[] ipAddressLocal=null;
					byte[] bytesMacAddress=null;
					short[] shortIpAddressLocal;
					short[] shortIpAddressServer;
					int port=3000;
					try {				
						
						InetAddress address = InetAddress.getByAddress(serverIpAddress);
						
						if (address!=null) {
							socket = Wifi.createSocket(address, port);
							shortIpAddressServer = Net.toShort(serverIpAddress);
							Wifi.setWifiState(true, " ip : " + shortIpAddressServer[0]+"."+shortIpAddressServer[1]+"."+
									shortIpAddressServer[2]+"."+shortIpAddressServer[3]);
							Wifi.setWifiState(true, " port : " + port);
							Wifi.setWifiState(true, " conntected");
							OutputStream os = socket.getOutputStream();
							//InputStream is = socket.getInputStream();						
							
							ipAddressLocal = Wifi.getIpAddress(info.getIpAddress());
							shortIpAddressLocal = Net.toShort(ipAddressLocal);
							Wifi.setWifiState(true, " My ip : " + shortIpAddressLocal[0]+"."+shortIpAddressLocal[1]+"."+
									shortIpAddressLocal[2]+"."+shortIpAddressLocal[3]);
							macAddress = info.getMacAddress();
							bytesMacAddress = macAddress.getBytes();
							Wifi.setWifiState(true, " My Mac : " + macAddress);
							Wifi.setWifiState(true, " MacLen:" + bytesMacAddress.length);
							
							/*byte[] bytesNickname = nickname.getBytes();
							int ipAndMacAndNicknameLen = 4 + bytesMacAddress.length + bytesNickname.length;
							byte[] ipAndMacAndNickname = new byte[ipAndMacAndNicknameLen];
							
							setIpAndMacAndNickname(ipAndMacAndNickname, ipAddressLocal, bytesMacAddress, bytesNickname);
							
							Wifi.sendMessage(os, IO.toBytes(ipAndMacAndNickname.length));
							Wifi.sendMessage(os, ipAndMacAndNickname);
							Wifi.setWifiState(true, " sent");*/
							
							//File contextDir = context.getFilesDir();
							//String filePath = this.fileListOfMultiSelect;
							
							
							
							boolean r = Wifi.sendLargeFile(os, fileListOfMultiSelect, true, true);
							
							fileListOfMultiSelect.destroy();
							
							if (!r)
								Wifi.setWifiState(true, " send file failed.");
							else
								Wifi.setWifiState(true, " send file succeeded.");
							
							//Thread.sleep(5000);
							
							/*byte[] buf = new byte[4];
							Wifi.receiveMessage(is, buf, 4);
							int msgLen = IO.toInt(buf);
							//Wifi.setWifiState(true, " msgLen:" + msgLen);
							
							buf = new byte[msgLen];
							Wifi.receiveMessage(is, buf, msgLen);
							String msg = new String(buf);
							if (msg.equals("r0"))
								Wifi.setWifiState(true, " send file failed.");
							else
								Wifi.setWifiState(true, " send file succeeded.");*/
							
						} // if (address==null)
					}catch (Exception e) {
						Wifi.setWifiState(true, " WifiThread-doWifi "+e.toString());
						shortIpAddressServer = Net.toShort(serverIpAddress);
						Wifi.setWifiState(true, " ip : " + shortIpAddressServer[0]+"."+shortIpAddressServer[1]+"."+
								shortIpAddressServer[2]+"."+shortIpAddressServer[3]);
						Wifi.setWifiState(true, " port : " + port);
						Wifi.setWifiState(true, " disconntected");
						e.printStackTrace();
						CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
					}
					finally {
						if (socket!=null) closeSocket();
						view.postInvalidate();
					}
					
					
					
				} // else
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				
			}
		}
		
		
		
		void closeSocket() {
			if (socket!=null) {
				//socket.shutdownInput();
				//socket.shutdownOutput();
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				}
			}
		}
	}
	
	public static class ServiceThread extends Thread{
		//String wifiState;
		public View owner;
		public boolean exit;
		InetAddress addressLocal;
		int portLocal;
		
		int MaxCount = 100;
		byte[] ipList = new byte[MaxCount*4];
		byte[] macList = new byte[MaxCount*17];
		byte[] nicknameList = new byte[MaxCount*20];
		int count=0;
		
		Socket socket=null;
		ServerSocket serverSocket=null;
		/*synchronized void setWifiState(boolean addOrReplace, String msg) {
			if (addOrReplace) {
				wifiState += msg;			
			}
			else {
				wifiState = "";
			}
			Control.logging.setText(wifiState, true);
			Control.logging.setHides(false);
			owner.postInvalidate();
		}*/
		
		public void killThread() {
			exit = true;
			if (isAlive()) {
				if (socket!=null) {
					try {
						socket.close();
						socket = null;
					} catch (IOException e) {
						e.printStackTrace();
						CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
					}
				}
				if (serverSocket!=null) {
					try {
						serverSocket.close();
						serverSocket = null;
					} catch (IOException e) {
						e.printStackTrace();
						CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
					}
				}
				interrupt();
				try {
					join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				}
			}			
		}
		
		public ServiceThread(InetAddress addressLocal, int portLocal) {
			this.addressLocal = addressLocal;
			this.portLocal = portLocal;
			
		}
		
		void getIpAndMacAndNickname(byte[] data) {
			Array.Copy(data,0,ipList,count*4,4);
			Array.Copy(data,0,macList,count*17,17);
			Array.Copy(data,0,nicknameList,count*20,data.length-4-17);
		}
		
		
		
		/** 
		 * 클라이언트 서비스 : 
		 * 1. 2에서 받을 메시지의 길이(정수,4byte)를 receive한다.
		 * 2. 길이정보를 바탕으로 메시지를 receive한다.
		 * 3. 서비스 횟수(count,String)의 길이(4byte)를 send한다.
		 * 4. 서비스 횟수를 send한다.
		 */
		public void run() {
			Wifi.isRunning = true;
			
			try {
				try {
					serverSocket = Wifi.createServerSocket(addressLocal, portLocal);
					Wifi.setWifiState(true, " ServerSocket created");			
				}		
				catch (Exception e) {
					Wifi.setWifiState(true, " Socket is already bound to "+portLocal+".");
					e.printStackTrace();
					CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
					return;
				}
				//do {
					if (serverSocket==null) return;
					try {
						socket = serverSocket.accept();	
						count++;
						//Wifi.setWifiState(false, " " + count + " accepted");	
					} catch (Exception e) {
						Wifi.setWifiState(true, " ServiceThread-accept " + e.toString());
						//e.printStackTrace();
						//CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
						return;
					}
					
					//String msgToSend;
					//OutputStream os=null;
					InputStream is=null;
					try {
						//os = socket.getOutputStream();			
						is = socket.getInputStream();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e1);
					}
					try{			
						
						boolean r = Wifi.receiveLargeFile(is, true, true);
						//getIpAndMacAndNickname(buf);
						if (r) {
							/*msgToSend = "r1";
							Wifi.setWifiState(true, " receive file(s) succeeded.");				
							
							byte[] bytesMsgToSend = msgToSend.getBytes();
							Wifi.sendMessage(os, IO.toBytes(bytesMsgToSend.length), 0, 4);
							Wifi.sendMessage(os, bytesMsgToSend, 0, bytesMsgToSend.length);
							//Wifi.setWifiState(true, " sent");*/
							Wifi.setWifiState(true, " receive file(s) succeeded.");	
							//owner.postInvalidate();
						}
						else {
							/*msgToSend = "r0";
							byte[] bytesMsgToSend = msgToSend.getBytes();
							try {
								Wifi.sendMessage(os, IO.toBytes(bytesMsgToSend.length), 0, 4);				
								Wifi.sendMessage(os, bytesMsgToSend, 0, bytesMsgToSend.length);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
							}*/
							Wifi.setWifiState(true, " receive file(s) failed.");
						}
						//sleep(1000);
					}catch (Exception e) {
						Wifi.setWifiState(true, " ServiceThread-receiveMessage " + e.toString());
						e.printStackTrace();
						CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
					}
					
					
					//Wifi.setWifiState(true, "");
				//}while(!exit);
			}
			catch (Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				
			}
			finally {				
				try {
					try {
						if (socket!=null) {
							//socket.shutdownInput();
							//socket.shutdownOutput();
							socket.close();
							socket = null;
							Wifi.setWifiState(true, " closed");							
							//sleep(1000);
						}
					}catch (Exception e) {
						Wifi.setWifiState(true, " ServiceThread-close " + e.toString());
						e.printStackTrace();
						CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
					}
					if (serverSocket!=null) {
						serverSocket.close();
						serverSocket = null;
					}
					
					Wifi.wifiManager.setWifiEnabled(false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Wifi.setWifiState(true, "ServiceThread-close " + e.toString());
					e.printStackTrace();
					CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				}
				owner.postInvalidate();
				Wifi.isRunning = false;
			}
			
		} // run()
		
		
	}


	/*public static class BluetoothService implements Listener {
		BluetoothAdapter adapter;
		public boolean isBluetoothEnabled;
		
		ArrayListString listOfNamesOfPairedDevices = new ArrayListString(100);
		ArrayListString listOfAddressesOfPairedDevices = new ArrayListString(100);
		
		String[] arrayAdapterOfDiscoveredDevices = new String[100];
		int countOfArrayAdapterOfDiscoveredDevices = 0;
		
		UUID uuidApp = new UUID((long)1234567, (long)7654321);
		AcceptThread acceptThread;
		ConnectThread connectThread;
		ReadThread readThread;
		
		Set<BluetoothDevice> pairedDevices;
		
		public enum BluetoothState {
			Accepted,
			Connected,
			Read,
			Write
		}
		public BluetoothState bluetoothState;
		
		private String msg = "";
		private LoggingScrollable logging;
		
		public byte[] dataReceived;
		Listener listener;
		
		public BluetoothDevice getRemoteDevice(String address) {
			return adapter.getRemoteDevice(address);
		}
		
		public ArrayListString getPairedDevices() {
			return listOfNamesOfPairedDevices;
		}
		
		public ArrayListString getAddressesOfPairedDevices() {
			return listOfAddressesOfPairedDevices;
		}
		
		public synchronized String getMsg() {
			return msg;
		}
		
		public synchronized void setMsg(String msg) {
			this.msg = msg;
		}
		
		public BluetoothService(Listener listener, LoggingScrollable logging) {
			this.listener = listener;
			this.logging = logging;
		}
		
		public void acceptBluetooth() {
			if (acceptThread!=null) {
				acceptThread.cancel();
				acceptThread = null;
			}
			//resetBluetooth();
		    msg = " accept UUID : " + uuidApp;
		    acceptThread = new AcceptThread(this, adapter, "BluetoothService", uuidApp);
		    acceptThread.start();
		    msg += " accept start";
		    logging.setText(true, msg, true);
		}
		
		
		
		public void connectBluetooth(BluetoothDevice device) {			
			if (connectThread!=null) {
				connectThread.cancel();
				connectThread = null;
			}
			//resetBluetooth();
			msg = " connect UUID : " + uuidApp;
			msg += " connect start";
			connectThread = new ConnectThread(this, adapter, device, uuidApp);
			connectThread.start();
			logging.setText(true, msg, true);
			
		}
		
		public void read(BluetoothSocket socket, byte[] buffer, int len) {
			if (readThread!=null) {
				readThread.cancel();
				readThread = null;
		    }
			msg += " read start";
			readThread = new ReadThread(this, socket, buffer, len);
			readThread.start();
			logging.setText(true, msg, true);
		}
		
		// accept, connect스레드 실행 전에 msg를 바꾼다.
		public void doBluetooth() {		
			adapter = BluetoothAdapter.getDefaultAdapter();
			if (adapter==null)	{
				msg += " adapter==null";
			}
			else {
				if (!adapter.isEnabled()) {
					isBluetoothEnabled = false;
					msg += " isBluetoothEnabled = false";
					return;
				    //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				    //activity.startActivity(enableBtIntent);
					
				}
				isBluetoothEnabled = true;
				msg += " isBluetoothEnabled = true";
				//msg += " paired list : ";
				Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
				// If there are paired devices
				if (pairedDevices.size() > 0) {
					listOfNamesOfPairedDevices.reset();
					listOfAddressesOfPairedDevices.reset();
				    // Loop through paired devices
				    for (BluetoothDevice device : pairedDevices) {
				        // Add the name and address to an array adapter to show in a ListView
				    	listOfNamesOfPairedDevices.add(device.getName());
				    	listOfAddressesOfPairedDevices.add(device.getAddress());
				    	msg += device.getName() + "\n" + device.getAddress();
				    }
				    //listener.createMenuBluetoothDevices(listOfNamesOfPairedDevices);
				    
				    acceptBluetooth();		  
				    
				}
				else {
					//registerBroadcastReceiver();
					//adapter.startDiscovery();
					msg += " discovered list : ";
				}
			}
			logging.setText(true, msg, true);
			//listener.invalidate();
			
		}
		
		
		
		public void resetBluetooth() {
			if (acceptThread!=null)	{
				acceptThread.cancel();
				acceptThread = null;
			}
			if (connectThread!=null) {
				connectThread.cancel();
				connectThread = null;
			}
			if (readThread!=null) {
				readThread.cancel();
				readThread = null;
		    }
			
		}

		// acceptThread, ConnectThread, ReadThread에서 호출
		@Override
		synchronized public void onEvent(Object sender) {
			// TODO Auto-generated method stub
			String className = (sender.getClass()).getName();
			String packageName = sender.getClass().getPackage().getName();		
			//msg += " onEvent : className : " + className;
			//msg += " onEvent : packageName : " + packageName;
			if (className.equals(packageName + ".Net$Bluetooth$AcceptThread")) {
				//AcceptThread acceptThread = (AcceptThread)sender;
				//BluetoothSocket socket = acceptThread.mmSocket;
				bluetoothState = BluetoothState.Accepted;
				msg = " accepted";
			}
			else if (className.equals(packageName + ".Net$Bluetooth$ConnectThread")) {
				//ConnectThread connectThread = (ConnectThread)sender;
				//BluetoothSocket socket = connectThread.mmSocket;
				bluetoothState = BluetoothState.Connected;
				msg = " connected";
				
			}
			else if (className.equals(packageName + ".Net$Bluetooth$ReadThread")) {
				ReadThread readThread = (ReadThread)sender;
				dataReceived = readThread.buffer;
				bluetoothState = BluetoothState.Read;
				msg = " connected";
			}
			
     		logging.setText(true, msg, true);
			
			//listener.postInvalidate();
		}

	}*/
	
	/*public static class Bluetooth {
		public static class AcceptThread extends Thread {
		    private BluetoothServerSocket mmServerSocket;		    
		    public BluetoothSocket mmSocket = null;
		    		    
		    boolean isEnd=false;
		    BluetoothService owner;
		
		    public AcceptThread(BluetoothService owner, BluetoothAdapter adapter, String name, UUID uuid) {
		        // Use a temporary object that is later assigned to mmServerSocket,
		        // because mmServerSocket is final
		    	this.owner = owner;
		        try {
		            // MY_UUID is the app's UUID string, also used by the client code
		            mmServerSocket = adapter.listenUsingRfcommWithServiceRecord(name, uuid);
		        } catch (IOException e) { }
		    }
		
		    public void run() {		        
		        // Keep listening until exception occurs or a socket is returned
		        while (true) {
		            try {
		                mmSocket = mmServerSocket.accept();
		            } catch (IOException e) {
		            } catch (Exception e) {
		            }
		            if (isEnd) break;
		            // If a connection was accepted
		            if (mmSocket != null) {
		                // Do work to manage the connection (in a separate thread)
		                //manageConnectedSocket(socket);
		            	if (owner!=null) owner.onEvent(this);
		                break;
		            }
		        }
		    }
		
		    ///** Will cancel the listening socket, and cause the thread to finish 
		    synchronized public void cancel() {
		        try {
		        	isEnd = true;
		            if (mmServerSocket!=null) {
		            	mmServerSocket.close();
		            }
		            if (mmSocket!=null) mmSocket.close();
		        } catch (IOException e) { }
		    }
		}

		public static class ConnectThread extends Thread {
		    public final BluetoothSocket mmSocket;
		    private final BluetoothDevice mmDevice;
		    BluetoothService owner;
		    
		  		 
		    public ConnectThread(BluetoothService owner, BluetoothAdapter adapter, BluetoothDevice device, UUID uuid) {
		        // Use a temporary object that is later assigned to mmSocket,
		        // because mmSocket is final
		    	this.owner = owner;
		        BluetoothSocket tmp = null;
		        mmDevice = device;
		
		        // Get a BluetoothSocket to connect with the given BluetoothDevice
		        try {
		            // MY_UUID is the app's UUID string, also used by the server code
		            tmp = mmDevice.createRfcommSocketToServiceRecord(uuid);
		        } catch (IOException e) { }
		        mmSocket = tmp;
		    }
		
		    public void run() {
		        // Cancel discovery because it will slow down the connection
		        //adapter.cancelDiscovery();
		
		        try {
		            // Connect the device through the socket. This will block
		            // until it succeeds or throws an exception
		            mmSocket.connect();
		            if (owner!=null) owner.onEvent(this);
		        } catch (IOException connectException) {
		            // Unable to connect; close the socket and get out
		            try {
		                mmSocket.close();
		            } catch (IOException closeException) { }		            
		            return;
		        } catch (Exception e) {
		        	try {
		                mmSocket.close();
		            } catch (IOException closeException) { }		            
		            return;
		        }
		
		        // Do work to manage the connection (in a separate thread)
		        //manageConnectedSocket(mmSocket);
		    }
		
		    // Will cancel an in-progress connection, and close the socket 
		    synchronized public void cancel() {
		        try {
		        	if (mmSocket!=null) {
		        		mmSocket.close();
		        	}
		        } catch (IOException e) { }
		    }
		}

		public static class ReadThread extends Thread {
		    private final BluetoothSocket mmSocket;
		    private final InputStream mmInStream;
		    public byte[] buffer;
		    public int len;
		    BluetoothService owner;
		
		    public ReadThread(BluetoothService owner, BluetoothSocket socket, byte[] buffer, int len) {
		    	this.owner = owner;
		        mmSocket = socket;
		        this.buffer = buffer;
		        this.len = len;
		        //this.listener = listener;
		        InputStream tmpIn = null;
		
		        // Get the input and output streams, using temp objects because
		        // member streams are final
		        try {
		            tmpIn = socket.getInputStream();
		        } catch (IOException e) { }
		
		        mmInStream = tmpIn;
		    }
		
		    public void run() {
		        try {
					mmInStream.read(buffer,0,len);
					if (owner!=null) owner.onEvent(this);
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
		    } 
		
		    // Call this from the main activity to shutdown the connection 
		    synchronized public void cancel() {
		        try {
		            mmSocket.close();
		        } catch (IOException e) { }
		    }
		}

		public static BluetoothSocket accept(BluetoothAdapter adapter, String name, UUID uuid, String msg) {
			 BluetoothServerSocket serverSocket = null;
			 BluetoothSocket socket = null;
			 while (true) {
			     try {
			    	 serverSocket = adapter.listenUsingRfcommWithServiceRecord(name, uuid);
			         msg += " ServerSocket created";
		             socket = serverSocket.accept();		             
		        	 if (socket!=null && socket.isConnected()==true) {
		        		 serverSocket.close();
		        		 msg += " ServerSocket accepted";		             
			             msg += " ServerSocket closed";
		        		 return socket;
		        	 }
		         } catch (IOException e) { 
		        	 msg += e.toString();
		         }
			 }
		}
		
		public static void write(BluetoothSocket socket, byte[] data) {
			try {
				OutputStream os = socket.getOutputStream(); 
				os.write(data);
			}catch (IOException e) { }
		}
		
		public static int read(BluetoothSocket socket, byte[] buffer, int len) {
	        InputStream is = null;
	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            is = socket.getInputStream();
	            return is.read(buffer,0,len);
	        } catch (IOException e) { }
	        return 0;
	    }
		
		public static void close(BluetoothSocket socket) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		
		public static BluetoothSocket connect(BluetoothDevice device, UUID uuid, String msg) {
			BluetoothSocket socket = null;
		
		    // Get a BluetoothSocket to connect with the given BluetoothDevice
		    try {
		        // MY_UUID is the app's UUID string, also used by the server code
		    	socket = device.createRfcommSocketToServiceRecord(uuid);
		    	msg += " Socket created";
		    } catch (IOException e) { }
		    
		    try {
		        // Connect the device through the socket. This will block
		        // until it succeeds or throws an exception
		    	socket.connect();
		    	if (socket.isConnected()==true){
		    		msg += " Socket connected";        	
		    		return socket;
		    	}
		    } catch (IOException e) {
		        // Unable to connect; close the socket and get out
		    	msg += e.toString();
		        try {
		        	socket.close();
		        } catch (IOException e1) { }		            
		        return null;
		    } catch (Exception e) {
		    	msg += e.toString();
		    	try {
		    		socket.close();
		        } catch (IOException e1) { }		            
		        return null;
		    }
		    return null;
		
		}

		
	}*/
	
	static public class Wifi { 
		public static WifiManager wifiManager;
		static boolean LoopEnd = false;
		
		static String wifiState = "";
		static View view = Control.view;
		
		public static ProgressBar progressBar;
		
		static int MaxFragmentLen = 800 * 400 * 5;
		
		public static boolean isRunning;
		
		
		public static ServerSocket createServerSocket(InetAddress localAddress, int localPort) throws IOException {
			ServerSocket socket = new ServerSocket(localPort);
			return socket;
		}
		
		public static Socket createSocket(InetAddress dstAddress, int dstPort) throws IOException {
			Socket socket = new Socket(dstAddress, dstPort);
			return socket;
		}
		
		public static Socket createSocket(InetAddress dstAddress, int dstPort, 
				InetAddress localAddress, int localPort) throws IOException {
			Socket socket = new Socket(dstAddress, dstPort, localAddress, localPort);
			return socket;
		}
		
		public static void sendMessage(OutputStream os, String msg) throws IOException {
			os.write(msg.getBytes());
		}
		
		public static void sendMessage(OutputStream os, byte[] msg, int offset, int len) throws IOException {
			os.write(msg, offset, len);
		}
		
		public synchronized static void showWifiControlState(boolean enableLog, boolean enableProgressBar) {
			boolean willDraw = false;
			if (enableLog) {
				CommonGUI.loggingForNetwork.setHides(false);
				willDraw = true;
			}
			if (enableProgressBar) {
				if (progressBar!=null) {
					progressBar.setHides(false);
					willDraw = true;
				}
			}
			if (willDraw/* && PowerManagement.isScreenOn*/) {
				view.postInvalidate();
			}
		}
		
		public synchronized static void setWifiState(boolean addOrReplace, String msg) {
			if (addOrReplace) {
				wifiState += msg;			
			}
			else {
				wifiState = msg;
			}
			CommonGUI.loggingForNetwork.setText(true, wifiState, false);
			CommonGUI.loggingForNetwork.setHides(false);
			//if (PowerManagement.isScreenOn) {
				view.postInvalidate();
				
				/*try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			//}
		}
		
		public synchronized static void setWifiStateSync(boolean addOrReplace, String msg) {
			if (addOrReplace) {
				wifiState += msg;			
			}
			else {
				wifiState = msg;
			}
			CommonGUI.loggingForNetwork.setText(true, wifiState, false);
			CommonGUI.loggingForNetwork.setHides(false);
			//if (PowerManagement.isScreenOn) {
				view.invalidate();
			//}
		}
		
		/** 2G이하(Integer.MaxValue)의 작은 파일을 네트워크를 통해서 받는다. 먼저 길이를 받고 그 후에 그 길이 만큼의
		 * 데이타를 받는다. 받을 데이터는 path와 실제 파일의 내용으로 구성된다. 
		 * @param is
		 * @return
		 */
		public static boolean receiveFile(InputStream is) {
			try {
				byte[] buf = new byte[4];
				Wifi.receiveMessage(is, buf, 4);
				int msgLen = IO.toInt(buf, true);
				buf = new byte[msgLen];
				Wifi.receiveMessage(is, buf, msgLen);
				
				setWifiState(true, " dataLen:"+buf.length+ " data received");				
				
				BufferByte bufferByte = new BufferByte(buf);
				String path = IO.readStringIncludingNull(bufferByte, TextFormat.UTF_8, true);
				String filename = FileHelper.getFilename(path);
				int fileLen = msgLen - bufferByte.offset;
				
				setWifiState(true, " path:"+path+" filename:"+filename+" fileLen:"+fileLen+
						" bufferOffset:"+bufferByte.offset);
				
				Context context = view.getContext();
				File pathOfContext = context.getFilesDir();
				FileOutputStream fileStream = new FileOutputStream(pathOfContext+File.separator+filename);
				//BufferedOutputStream stream = BufferedOutputStream((OutputStream)fileStream/*, IO.DefaultBufferSize*/); 
				fileStream.write(buf, bufferByte.offset, fileLen);
				fileStream.close();
				
				setWifiState(true, " completed");
				return true;
			}
			catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				return false;
			}
			
		}

		
		/** 2G이하(Integer.MaxValue)의 작은 파일을 네트워크를 통해서 보낸다. 먼저 길이를 보내고 그 후에 그 길이 만큼의
		 * 데이타를 보낸다. 보내는 데이터는 path와 실제 파일의 내용으로 구성된다. 
		 * @param is
		 * @return
		 * @throws Exception 
		 */
		public static boolean sendFile(OutputStream os, File file) throws Exception {
			try {
				int fileLen = (int) file.length();
				String filePath = file.getAbsolutePath();
				
				//널문자 포함 filePath의 바이트 길이(3은 문자당 바이트수)
				int filePathLenInByte = (filePath.length() + 1) * 3;  
				int dataLen = filePathLenInByte + fileLen;
				byte[] data = new byte[dataLen];
				setWifiState(true, " byteLenOfFilePath:"+filePathLenInByte+
						" srcFileLen:"+fileLen+" dataLen:"+dataLen);
				
				// Buffer에 filePath를 쓴다.
				BufferByte bufferByte = new BufferByte(data);
				IO.writeString(bufferByte, filePath, com.gsoft.common.IO.TextFormat.UTF_8, true);
				
				// Buffer에 file을 쓴다.
				FileInputStream fileStream = new FileInputStream(file);
				BufferedInputStream bufferedStream = new BufferedInputStream(fileStream);						
				bufferedStream.read(bufferByte.buffer, bufferByte.offset, fileLen);
				bufferedStream.close();
				fileStream.close();
				
				// 보내는 데이터(filePath+file)의 바이트 길이를 wifi에 쓴다.
				Wifi.sendMessage(os, IO.toBytes(dataLen, true), 0, 4);
				// 실제 데이터를 wifi에 쓴다.
				Wifi.sendMessage(os, data, 0, data.length);
				setWifiState(true, " sent");
				return true;
			}
			catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				throw e;
			}
		}
		
		/** 2G이하(Integer.MaxValue)의 fragment을 네트워크를 통해서 보낸다. 
		 * 먼저 길이를 보내고 그 후에 그 길이 만큼의 fragment를 보낸다.  
		 * @param is
		 * @return
		 */
		public static boolean sendFragment(OutputStream os, byte[] fragment, int offset, int len) {
			try {
				
				// 보내는 데이터(filePath+file)의 바이트 길이를 wifi에 쓴다.
				//Wifi.sendMessage(os, IO.toBytes(len, true), 0, 4);
				// 실제 데이터를 wifi에 쓴다.
				Wifi.sendMessage(os, fragment, offset, len);
				/*setWifiState(true, " sendFragment fragmentLen:"+fragmentLen);
				for (int i=0; i<10; i++) {
					setWifiState(true, " Fragment["+i+"]:"+fragment[i]);
				}*/
				return true;
			}
			catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				return false;
			}
		}
		
		/** 2G이하(Integer.MaxValue)의 fragment을 네트워크를 통해서 받는다. 
		 * 먼저 길이를 받고 그 후에 그 길이 만큼의 fragment를 받는다.  
		 * @param is
		 * @return
		 */
		public static boolean receiveFragment(InputStream is, byte[] buf, int len) {
			try {
				boolean r = Wifi.receiveMessage(is, buf, len);
				if (r) return true;
				return false;
			}
			catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				return false;
			}
		}
		
		public static class FileHeader {
			public long dataLen;
			public boolean isFragment;
			public int numOfFragment; 
		}
		
		/*static BufferByte allocate(int size) {
			try {
				return new BufferByte(new byte[size]);
			}catch(OutOfMemoryError e) {
				setWifiState(true, " BufferByte creation failed.");
				return null;
			}
			
		}*/
		
		static void createProgressBar() {
			int x, y, w, h;
			w = (int) (view.getWidth() * 0.8f);
			h = (int) (view.getHeight() * 0.065f);
			x = view.getWidth()/2 - w/2;
			y = h;
			Rectangle boundsOfProgressBar = new Rectangle(x, y, w, h);
			
			progressBar = new ProgressBar(false, 20, boundsOfProgressBar, 0);
			//progressBar.setOnTouchListener(this);
			progressBar.setHides(true);
			//controlInfo.listOfControlsInContainer.add(fileDialog);
		}
		
		public static long sendLargeFileRecursive(OutputStream os, File file, String relativePath, boolean enableLog, boolean enableProgressBar, byte[] fragBuf) throws Exception {
			long r=0;
			try {
				String[] fileList = file.list();
				String absFilename = file.getAbsolutePath();
				if (file.isDirectory()) {
					long bytes;
					bytes = sendFile(os, file, relativePath, enableLog, enableProgressBar, fragBuf); // 빈 디렉토리
					r += bytes;
					fileCountSent++;
					setWifiState(false, /*" totalFileCount:"+totalFileCount+ */" fileCount(sent):"+fileCountSent+
							" sizeToSend(reserved):"+(totalFileSize-=bytes));
					
					if (fileList!=null && fileList.length>0) {
						int i;				
						for (i=0; i<fileList.length; i++) {
							if (FileHelper.isSeparator( absFilename.charAt(absFilename.length()-1) )==false) {
								File itemFile = new File(absFilename+File.separator+fileList[i]);
								r += sendLargeFileRecursive(os, itemFile, relativePath, enableLog, enableProgressBar, fragBuf);
							}
							else {
								File itemFile = new File(absFilename+fileList[i]);
								r += sendLargeFileRecursive(os, itemFile, relativePath, enableLog, enableProgressBar, fragBuf);
							}
							//if (!r) return false;
						}
					}
					
					
				}
				else {
					long bytes;
					bytes = sendFile(os, file, relativePath, enableLog, enableProgressBar, fragBuf);
					r += bytes;
					fileCountSent++;
					setWifiState(false, /*" totalFileCount:"+totalFileCount+ */" fileCount(sent):"+fileCountSent+
							" sizeToSend(reserved):"+(totalFileSize-=bytes));
					//if (!r) return false;
				}
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				throw e;
			}
			return r;
		}
		
		public static boolean sendLargeFile(OutputStream os, ArrayListString fileList, boolean enableLog, boolean enableProgressBar)  {
			//if (file.isDirectory()==false) return false;
			if (progressBar==null) createProgressBar();
			try {
				int i;
				fileCountSent = 0;
				
				Wifi.sendMessage(os, IO.toBytes(fileCountToSend, true), 0, 4);
				Wifi.sendMessage(os, IO.toBytes(totalFileSize, true), 0, 8);
				setWifiState(true, " fileCountToSend:"+fileCountToSend + " total fileSize:"+totalFileSize);				
				
				//String relativeRoot = FileHelper.getFilename(absFilename);
				
				byte[] fragBuf = new byte[MaxFragmentLen];
				
				for (i=0; i<fileList.count; i++) {
					String absFilename = fileList.getItem(i);
					String relativePath = FileHelper.getFilename(absFilename);
					File file = new File(absFilename); 
					sendLargeFileRecursive(os, file, relativePath, enableLog, enableProgressBar, fragBuf);
				}
				// 다시 초기화 해준다.
				fileCountToSend = 0;
				totalFileSize = 0;
				return true;
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				return false;
			}
			
		}
		
		public static boolean receiveLargeFile(InputStream is, boolean enableLog, boolean enableProgressBar) throws Exception {
			if (progressBar==null) createProgressBar();
			
			long fileSizeRead=0;
			int fileCount=0;
			try {
				// 헤더를 wifi에서 읽는다.
				byte[] bufFileCountToReceive = new byte[4];
				Wifi.receiveMessage(is, bufFileCountToReceive, 4);
				int fileCountToReceive = IO.toInt(bufFileCountToReceive, true);
				
				byte[] buf = new byte[8];
				Wifi.receiveMessage(is, buf, 8);
			
				long fileSize = IO.toLong(buf, true);
				setWifiState(true, " fileSizeToReceive:"+fileSize);
				
				byte[] fragBuf = new byte[MaxFragmentLen];
				ArrayListString listOfPath = new ArrayListString(100); 
				
				//while (fileSize>fileSizeRead) {
				while (fileCount<fileCountToReceive) {
					fileSizeRead += receiveFile(is, enableLog, enableProgressBar, fragBuf, listOfPath);
					fileCount++;
					setWifiState(false, " fileCount(received):"+fileCount+" sizeToReceive(reserved):"+(fileSize-fileSizeRead));
				}
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				return false;
			}
		}
		
		/** Long.MaxValue(2G*2^32)바이트 이하의 파일을 네트워크를 통해서 보낸다.
		 * 헤더는 데이터의 길이(path길이+null(3바이트)+파일길이, long타입)와 
		 * 파편으로 나뉘어있는지,아닌지를 말하는 isFragment(int), 
		 * 파편개수(numOfFragment,int타입)로 구성된다. 
		 * 먼저 헤더를 보내고, 데이터를 구성하는 파편들을 보낸다. 
		 * 그리고 파편을 보낼 때는 파편의 길이를 먼저 보내고 파편 데이터를 보낸다.(따라서 sendFragment을 사용한다.)
		 * 데이터는 path와 실제 파일의 내용으로 구성된다. 
		 * 
		 * @param os
		 * @return
		 * @throws Exception 
		 */
		public static long sendFile(OutputStream os, File file, String relativePath, boolean enableLog, boolean enableProgressBar, byte[] fragBuf) throws Exception {
			long r=0;
			try {
				setWifiState(true, " send file start");
				
				long fileLen;
				if (file.isDirectory()==false) fileLen = file.length();
				else fileLen = 0;
				
				String absFilePath = file.getAbsolutePath(); 
				String filePath = absFilePath.substring(absFilePath.indexOf(relativePath));
				
				//널문자 포함 filePath의 바이트 길이
				//int filePathLenInByte = (filePath.length() + 1) * 3;  
				int filePathLenInByte = IO.getByteLen(filePath);
				long dataLen = filePathLenInByte + fileLen;
				
				
				int numOfFragment = (dataLen % MaxFragmentLen)!=0 ? 
						(int) (dataLen / MaxFragmentLen + 1) : 
							(int) (dataLen / MaxFragmentLen);
						
				int isDirectory;
				if (file.isDirectory()) isDirectory=1;
				else isDirectory=0;
						
				
				
				setWifiState(true, " dataLen:"+dataLen+ " numOfFragment:"+numOfFragment);
						
				setWifiState(true, " fileLen:"+fileLen);
				
				if (enableProgressBar) {
					progressBar.setItemCount(numOfFragment);
					progressBar.setItemPos(0);
					progressBar.initialize();
					showWifiControlState(enableLog, enableProgressBar);
				}
				//Control.setModified(true);
				
				BufferByte[] arrBufferByte = new BufferByte[numOfFragment];
				//setWifiState(true, " BufferByte[] created");
				int i;
				
				// 헤더를 wifi에 쓴다.
				Wifi.sendMessage(os, IO.toBytes(dataLen, true), 0, 8);
				//Wifi.sendMessage(os, IO.toBytes(isFragment));
				Wifi.sendMessage(os, IO.toBytes(numOfFragment, true), 0, 4);
				Wifi.sendMessage(os, IO.toBytes(isDirectory, true), 0, 4);
				
				
				// 첫번째 fragment
				if (numOfFragment > 1) {
					//arrBufferByte[0] = allocate(fragmentLen);
					//fragBuf = new byte[MaxFragmentLen];
					arrBufferByte[0] = new BufferByte(fragBuf);
				}
				else {
					//arrBufferByte[0] = allocate((int)dataLen);
					//fragBuf = new byte[(int)dataLen];
					arrBufferByte[0] = new BufferByte(fragBuf);
				}
				
				// Buffer에 filePath를 쓴다.				
				IO.writeString(arrBufferByte[0], filePath, Control.NetworkStringFormat, true);
				
				setWifiState(true, " filePath:"+filePath);
				
				// file을 읽어서 Buffer에 file을 쓴다.
				FileInputStream fileStream = null;
				BufferedInputStream bufferedStream = null;
				int readLen;
				long fileLenRead;
				i = 0;
				
				if (isDirectory==0) {
					fileStream = new FileInputStream(file);
					bufferedStream = new BufferedInputStream(fileStream);
													
					if (numOfFragment > 1) {
						readLen = MaxFragmentLen - filePathLenInByte;
					}
					else {
						readLen = (int) fileLen;
					}
					fileLenRead = readLen;								
					bufferedStream.read(arrBufferByte[i].buffer, arrBufferByte[i].offset, readLen);
					//setWifiState(true, " i:"+i+" bufferOffset:"+arrBufferByte[i].offset+
					//		" bufferSize:"+arrBufferByte[i].buffer.length + " readLen:"+readLen);
				}
				else {
					readLen = (int) fileLen;
					fileLenRead = readLen;
				}
				
				// 보낼 Fragment의 길이를 보낸다.
				Wifi.sendMessage(os, IO.toBytes(filePathLenInByte + readLen, true), 0, 4);
				sendFragment(os, arrBufferByte[0].buffer, 0, filePathLenInByte + readLen);
				r += readLen;
				//String strFragment = new String(arrBufferByte[i].buffer);
				//setWifiState(true, " i:"+i+" Fragment:"+strFragment);
				//Thread.sleep(3000);
				if (enableProgressBar) {
					progressBar.setItemPos(i+1);
					showWifiControlState(enableLog, enableProgressBar);
				}
				//Control.setModified(true);
				
				// 중간 fragment들
				readLen = MaxFragmentLen;				
				for (i=1; i<arrBufferByte.length-1; i++) {
					//arrBufferByte[i] = allocate(readLen);
					arrBufferByte[i] = new BufferByte(fragBuf);
					bufferedStream.read(arrBufferByte[i].buffer, arrBufferByte[i].offset, readLen);
					fileLenRead += readLen;
					//setWifiState(true, " i:"+i+" bufferOffset:"+arrBufferByte[i].offset+
					//		" bufferSize:"+arrBufferByte[i].buffer.length + " readLen:"+readLen);
					
					// 보낼 Fragment의 길이를 보낸다.
					Wifi.sendMessage(os, IO.toBytes(readLen, true), 0, 4);
					sendFragment(os, arrBufferByte[i].buffer, 0, readLen);
					r += readLen;
					//String strFragment = new String(arrBufferByte[i].buffer);
					//setWifiState(true, " i:"+i+" Fragment:"+strFragment);
					//Thread.sleep(3000);
					if (enableProgressBar) {
						progressBar.setItemPos(i+1);
						showWifiControlState(enableLog, enableProgressBar);
					}
					//Control.setModified(true);
					
				}
				
				// 마지막 fragment
				if (fileLenRead < fileLen) {
					readLen = (int) (fileLen - fileLenRead);
					i = arrBufferByte.length-1;
					//arrBufferByte[i] = allocate(readLen);
					arrBufferByte[i] = new BufferByte(fragBuf);
					bufferedStream.read(arrBufferByte[i].buffer, arrBufferByte[i].offset, readLen);
					//setWifiState(true, " i:"+i+" bufferOffset:"+arrBufferByte[i].offset+
					//		" bufferSize:"+arrBufferByte[i].buffer.length + " readLen:"+readLen);
					
					// 보낼 Fragment의 길이를 보낸다.
					Wifi.sendMessage(os, IO.toBytes(readLen, true), 0, 4);
					sendFragment(os, arrBufferByte[i].buffer, 0, readLen);
					
					fileLenRead += readLen;
					r += readLen;
					//String strFragment = new String(arrBufferByte[i].buffer);
					//setWifiState(true, " i:"+i+" Fragment:"+strFragment);
					//Thread.sleep(3000);
					if (enableProgressBar) {
						progressBar.setItemPos(i+1);
						showWifiControlState(enableLog, enableProgressBar);
					}
					//Control.setModified(true);
					
					
				}
				
				if (isDirectory==0) {
					bufferedStream.close();
					fileStream.close();
				}
				
				
				
				os.flush();
				if (enableProgressBar) {
					showWifiControlState(enableLog, enableProgressBar);
				}
				//setWifiState(true, " send file completed..");
				return r;
			}
			catch(Exception e) {
				if (enableProgressBar) progressBar.setHides(false);
				setWifiState(true, " send file failed..");
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				throw e;
			}
		}
		
		/** Long.MaxValue(2G*2^32)바이트 이하의 파일을 네트워크를 통해서 받는다.
		 * 헤더는 데이터의 길이(path길이+null(3바이트)+파일길이, long타입)와 
		 * 파편으로 나뉘어있는지,아닌지를 말하는 isFragment(int), 
		 * 파편개수(numOfFragment,int타입)로 구성된다. 
		 * 먼저 헤더를 받고, 그에 따라 데이터를 구성하는 파편들을 받는다. 
		 * 그리고 파편을 받을 때는 파편의 길이를 먼저 받고 파편 데이터를 받는다.(따라서 receiveFragment을 사용한다.)
		 * 데이터는 path와 실제 파일의 내용으로 구성된다. 
		 * 
		 * @param os
		 * @return 읽어들인 dataLen
		 * @throws Exception 
		 */
		public static long receiveFile(InputStream is, boolean enableLog, boolean enableProgressBar, byte[] fragBuf, ArrayListString listOfPath) throws Exception {
			long r=0;
			long lenOfFileWritten=0;
			try {
				int i;
				setWifiState(true, " receive file start");
				
					
				
				// 헤더를 wifi에서 읽는다.
				byte[] buf = new byte[8];
				Wifi.receiveMessage(is, buf, 8);
				long dataLen = IO.toLong(buf, true);
				
				buf = new byte[4];
				Wifi.receiveMessage(is, buf, 4);
				int numOfFragment = IO.toInt(buf, true);
				///int lenOfFragment = MaxFragmentLen;
				
				Wifi.receiveMessage(is, buf, 4);
				int isDirectory = IO.toInt(buf, true);
				
				setWifiState(true, " dataLen:"+dataLen+" numOfFragment:"+numOfFragment);
				
				BufferByte[] arrBufferByte = new BufferByte[numOfFragment];
				
				if (enableProgressBar) {
					progressBar.setItemCount(numOfFragment);
					progressBar.setItemPos(0);
					progressBar.initialize();
					showWifiControlState(enableLog, enableProgressBar);
				}
				
				setWifiState(true, " enableProgressBar test");
				//Control.setModified(true);
								
				//byte[] fragBuf = new byte[MaxFragmentLen];
				
				// 첫번째 fragment
				buf = new byte[4];
				Wifi.receiveMessage(is, buf, 4);
				int fragLen = IO.toInt(buf, true);					
				setWifiState(true, " fragLen:"+fragLen);				
				
				receiveFragment(is, fragBuf, fragLen);
				r += fragLen;
				setWifiState(true, " fragment received");
				arrBufferByte[0] = new BufferByte(fragBuf);
				//String strFragment = new String(arrBufferByte[i].buffer);
				//setWifiState(true, " i:"+i+" Fragment:"+strFragment);
				//Thread.sleep(3000);
				if (enableProgressBar) {
					progressBar.setItemPos(1);
					showWifiControlState(enableLog, enableProgressBar);
				}
				//Control.setModified(true);
				
				String filePath = IO.readStringIncludingNull(arrBufferByte[0], Control.NetworkStringFormat, true);
				filePath = filePath.replace('/', File.separatorChar);
				filePath = filePath.replace('\\', File.separatorChar);
				//String filename = FileHelper.getFilename(filePath);
				//setWifiState(true, " filePath:"+(filePath==null));
				
				//long lenOfFile = dataLen - (filePath.length()+1)*3;
				int filePathLenInByte = IO.getByteLen(filePath);
				long lenOfFile = dataLen - filePathLenInByte;
				
				// sendLargeFile에서 totalFileSize가 파일경로를 포함하지 않은 전체 파일사이즈이므로
				// 파일경로길이를 빼준다.
				r -= filePathLenInByte;
				
				setWifiState(true, " filePath:"+filePath+" lenOfFile:"+lenOfFile);
				
				//FileOutputStream fileStream = new FileOutputStream(pathOfContext+File.separator+filename);
				String wifiDir = Control.pathWifi;
				File fileWifi = new File(wifiDir);
				if (fileWifi.exists()==false) {
					fileWifi.mkdirs();
				}
				
				
				FileOutputStream fileStream = null;
				BufferedOutputStream stream = null;
				int lenOfFileToWrite;
				i = 0;
				
				if (isDirectory==0) {
					fileStream = new FileOutputStream(wifiDir+File.separator+filePath);
					listOfPath.add(filePath);
					stream = new BufferedOutputStream(fileStream/*, IO.DefaultBufferSize*/);				
					
					lenOfFileToWrite = fragLen - arrBufferByte[0].offset;
					lenOfFileWritten = lenOfFileToWrite;
					
					stream.write(arrBufferByte[i].buffer, arrBufferByte[i].offset, lenOfFileToWrite);
					//setWifiState(true, " i:"+i+" bufferOffset:"+arrBufferByte[i].offset);
				}
				else {
					lenOfFileToWrite = 0;
					lenOfFileWritten = lenOfFileToWrite;
					File dir = new File(wifiDir+File.separator+filePath);
					dir.mkdir();
					//if (dir.mkdir()==false)
					//	throw new Exception("Directory not created.");
				}	
				
				
				// 중간 fragment들
				lenOfFileToWrite = MaxFragmentLen;
				for (i=1; i<arrBufferByte.length-1; i++) {
					Wifi.receiveMessage(is, buf, 4);
					fragLen = IO.toInt(buf, true);					
					//setWifiState(true, " fragLen:"+fragLen);
					
					//byte[] fragBuf = new byte[fragLen];
					receiveFragment(is, fragBuf, fragLen);
					r += fragLen;
					//setWifiState(true, " fragment received");
					arrBufferByte[i] = new BufferByte(fragBuf);
					//String strFragment = new String(arrBufferByte[i].buffer);
					//setWifiState(true, " i:"+i+" Fragment:"+strFragment);
					//Thread.sleep(3000);
					if (enableProgressBar) {
						progressBar.setItemPos(i+1);
						showWifiControlState(enableLog, enableProgressBar);
					}
					//Control.setModified(true);
					
					stream.write(arrBufferByte[i].buffer, arrBufferByte[i].offset, lenOfFileToWrite);
					lenOfFileWritten += lenOfFileToWrite;
					//setWifiState(true, " i:"+i+" bufferOffset:"+arrBufferByte[i].offset);
				}
				
				// 마지막 fragment
				if (lenOfFileWritten < lenOfFile) {
					i = arrBufferByte.length-1;
					Wifi.receiveMessage(is, buf, 4);
					fragLen = IO.toInt(buf, true);					
					//setWifiState(true, " fragLen:"+fragLen);
					
					//byte[] fragBuf = new byte[fragLen];
					receiveFragment(is, fragBuf, fragLen);
					r += fragLen;
					//setWifiState(true, " fragment received");
					arrBufferByte[i] = new BufferByte(fragBuf);
					//String strFragment = new String(arrBufferByte[i].buffer);
					//setWifiState(true, " i:"+i+" Fragment:"+strFragment);
					//Thread.sleep(3000);
					if (enableProgressBar) {
						progressBar.setItemPos(i+1);
						showWifiControlState(enableLog, enableProgressBar);
					}
					//Control.setModified(true);
					
					lenOfFileToWrite = (int) (lenOfFile - lenOfFileWritten);				
					stream.write(arrBufferByte[i].buffer, arrBufferByte[i].offset, lenOfFileToWrite);
					lenOfFileWritten += lenOfFileToWrite;
					//setWifiState(true, " i:"+i+" bufferOffset:"+arrBufferByte[i].offset);
				}
				
				if (isDirectory==0) {
					stream.close();
					fileStream.close();
				}
				
				if (enableProgressBar) {
					showWifiControlState(enableLog, enableProgressBar);
				}
				//setWifiState(true, " receive file completed");
				
				r = lenOfFileWritten;
				//Control.loggingForNetwork.setText(true, "Transmission completed", false);
				//Control.loggingForNetwork.setHides(false);
				//Control.setModified(true);
				return r;
				
				
			}
			catch (OutOfMemoryError e) {
				setWifiState(true, " OutOfMemory");
				if (enableProgressBar) progressBar.setHides(true);
				setWifiState(true, " receive file failed");
				throw e;
			}
			catch(Exception e) {
				/*int i;
				String traceMsg = "";
				StackTraceElement[] traces = e.getStackTrace();
				for (i=0; i<4; i++) {
					traceMsg += traces[i].getMethodName() + " " + traces[i].getLineNumber();
				}
				setWifiState(true, " "+traceMsg);*/
				if (enableProgressBar) progressBar.setHides(true);
				setWifiState(true, " receive file failed");
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				throw e;
			}
		}
		
		
		/*public static void sendFile(OutputStream os, File file) throws IOException {
			
			int len = (int) file.length();
			IO.writeInt(os, len);
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			
			byte[] buf = new byte[len];
			bis.read(buf, 0, len);
			
			sendMessage(os, buf);
			
			fis.close();
			bis.close();
		}
		
		static class ReceiveFileThread extends Thread {
			InputStream is;
			String absPath;
			Listener listener;
			public File file;
			
			public ReceiveFileThread(InputStream is, String absPath, Listener listener) {
				this.is = is;
				this.absPath = absPath;
				this.listener = listener;
			}
			public void run() {
				int len = IO.readInt(is);
				
				FileOutputStream fos=null;
				BufferedOutputStream bos=null;
				try {
					file = new File(absPath);
					fos = new FileOutputStream(file);
					bos = new BufferedOutputStream(fos); 
								
					byte[] buf = receiveMessage(is, len);
					bos.write(buf);
					if (bos!=null) bos.close();
					if (fos!=null) fos.close();
					
					listener.onEvent(this);
				}catch(Exception e) {
					
				}
				finally {
					try {
						if (bos!=null) bos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
					
					try {
						if (fos!=null) fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				}
			}
			
		}
		
		
		public static File receiveFile(InputStream is, String absPath) throws IOException {
			
			//int len = (int) file.length();
			int len = IO.readInt(is);
			
			File file = new File(absPath);
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos); 
						
			byte[] buf = receiveMessage(is, len);
			bos.write(buf);
			bos.close();
			fos.close();
			return file;			
		}*/
		
		/**받은 바이트수가 len가 될 때까지 block한다.*/
		public static boolean receiveMessage(InputStream is, byte[] buf, int len) throws IOException {
			/*int count=0;
			int readLen = 1;
			do{
				try {
					//readLen = is.available();
					count += is.read(buf, count, readLen);
					if (count>=len) {
						//setWifiState(true, " countRead :"+count);
						return true;
					}
				}catch(Exception e){
					return false;
				}
				
				
			}while(!LoopEnd);
			return false;*/
			int count=0;
			do {
				try {
					count += is.read(buf, count, len-count);				
				}catch(Exception e){
					return false;
				}
			}while(count<len);
			//if (count<len)
			//	count += is.read(buf, count, len-count);
			return true;
		}
		
		static class ReceiveThread extends Thread {
			InputStream is;
			int len;
			Listener listener;
			public byte[] data;
			ReceiveThread(Listener listener, InputStream is, int len) {
				this.is = is;
				this.len = len;
				this.listener = listener;
			}
			public void run() {
				try {
					data = new byte[len];
					Wifi.receiveMessage(is, data, len);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				}
				listener.onEvent(this);
			}
		}
		
		/**받은 바이트수가 len가 될 때까지 no block한다. len만큼 받으면 call back호출한다.*/
		public static void receiveMessageNoBlocking(InputStream is, int len, Listener listener ) throws IOException {
			ReceiveThread thread = new ReceiveThread(listener, is, len);
			thread.start();
		}
		
		static int containsNull(byte[] data) {
			int i;
			for (i=0; i<data.length; i++)
				if (data[i] == 0) return i;
			return -1;
		}
		
		public static byte[] getIpAddress(int ip) {
			byte[] ipAddress = new byte[4];
			ipAddress[0] = (byte)(ip&0xff);
			ipAddress[1] = (byte)(ip>>8&0xff); 
			ipAddress[2] = (byte)(ip>>16&0xff);
			ipAddress[3] = (byte)(ip>>24&0xff);
			return ipAddress;
		}
		
		public static byte[] getIpAddress(String ip) throws Exception {
			try {
				int[] indicesOfDots = new int[3];
				int count=0;		
				int i;
				for (i=0; i<ip.length(); i++) {
					if (ip.charAt(i)=='.') {
						indicesOfDots[count++] = i;
					}
				}
				if (count!=3) throw new Exception("IP is wrong");
				
				short[] r = new short[4];
				count = 0;
				int oldIndex = 0;
				for (i=0; i<indicesOfDots.length; i++) {
					String element = ip.substring(oldIndex, indicesOfDots[i]);
					oldIndex = indicesOfDots[i]+1;
					r[count++] = Short.parseShort(element);
				}
				if (i==indicesOfDots.length) {
					String element = ip.substring(indicesOfDots[i-1]+1, ip.length());
					r[count++] = Short.parseShort(element);
				}
				
				for (i=0; i<r.length; i++) {
					if (!(0<=r[i] && r[i]<=255))
						throw new Exception("IP is wrong");
				}
				
				byte[] bytesR = Net.toBytes(r);
				return bytesR;
			}
			catch(Exception e) {
				throw new Exception("IP is wrong");
			}
			
		}
		
		
		
		/*public static boolean isWifiConnected(Context context) {
			wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			if (wifiManager.isWifiEnabled()) {
				WifiInfo info = wifiManager.getConnectionInfo();
				if (info.getIpAddress()!=0) return true;
			}
			return false;
		}*/
		
		public static WifiInfo startWifi(View paramView) {
			view = paramView;
			Context context = view.getContext();
			wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			if (wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
				WifiInfo info = wifiManager.getConnectionInfo();
				return info;
			}
			return null;
		}
		
		public static void endWifi() {
			if (wifiManager==null) return;
			if (wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(false);
			}
			LoopEnd = true;
		}

	
	}
}