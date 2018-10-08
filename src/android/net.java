package android;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.net.Inet6Address;

import com.gsoft.common.IO;
import com.gsoft.common.Util.ArrayList;

public class net {

	public static class Uri {
		File file;
		
		public Uri (File file) {
			this.file = file;
		}

		public static Uri fromFile(File file) {
			// TODO Auto-generated method stub
			return new Uri(file);
		}

	}
	
	public static class wifi {
		public static class WifiManager {
			boolean isWifiEnabled;
			InetAddress[] listInetAddressReachable;
			byte[] macAddress;
			
			public WifiManager() {
				try {
					
					Enumeration<NetworkInterface> list = NetworkInterface.getNetworkInterfaces();
					ArrayList arrayList = new ArrayList(10);
					int k = 0;
					while (list.hasMoreElements()) {
						NetworkInterface network = list.nextElement();
						if (k==9) {
							int a;
							a=0;
							a++;
						}
						if (network.isVirtual()) continue;
						
						boolean found = false;
						Enumeration<InetAddress> listInetAddress = network.getInetAddresses();
						while (listInetAddress.hasMoreElements()) {
							InetAddress address = listInetAddress.nextElement();
							try {
								if (address instanceof Inet6Address) continue;
								//if (address.isMulticastAddress()) continue;
								if (address.isLoopbackAddress()) continue;
								//if (address.isSiteLocalAddress()==false) {
									boolean r = address.isReachable(3000);								
									if (r) {
										isWifiEnabled = true;
										macAddress = network.getHardwareAddress();
										arrayList.add(address);
										found = true;
									}
								//}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								continue;
							}
						}
						if (found) break;
						//arrayList.add(network);
						k++;
					}
					listInetAddressReachable = new InetAddress[arrayList.count];
					for (int i=0; i<arrayList.count; i++) {
						listInetAddressReachable[i] = (InetAddress) arrayList.getItem(i);
					}
					
					
					
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public boolean isWifiEnabled() {
				// TODO Auto-generated method stub
				return isWifiEnabled;
			}

			public void setWifiEnabled(boolean b) {
				// TODO Auto-generated method stub
				if (!b) isWifiEnabled = false;
			}

			public WifiInfo getConnectionInfo() {
				// TODO Auto-generated method stub
				WifiInfo info = new WifiInfo(listInetAddressReachable[0], macAddress);
				return info;
			}

			public boolean pingSupplicant() {
				// TODO Auto-generated method stub
				return true;
			}

		}

		public static class WifiInfo {
			int ipAddress;
			String macAddress;
			
			WifiInfo(InetAddress inetAddress, byte[] macAddress) {
				byte[] address = inetAddress.getAddress();
				this.ipAddress = IO.toInt(address, true);
				this.macAddress = "";
				int i;
				for (i=0; i<macAddress.length; i++) {
					if (i!=macAddress.length-1) {
						this.macAddress += IO.toHexa(macAddress[i]) + "-";
					}
					else {
						this.macAddress += IO.toHexa(macAddress[i]);
					}
				}
			}

			public int getIpAddress() {
				// TODO Auto-generated method stub
				return ipAddress;
			}

			public String getMacAddress() {
				// TODO Auto-generated method stub
				return macAddress;
			}

		}
	}

}