package com.gsoft.common.java.net;

import java.net.NetworkInterface;

public class InetAddress {
	/** Compares this object against the specified object.*/
	public boolean	equals(Object obj) {
		return false;
		
	}
	/**Returns the raw IP address of this InetAddress object.*/
	byte[]	getAddress() {
		return null;
		
	}
	/**Given the name of a host, returns an array of its IP addresses, based on the configured name service on the system.*/
	static InetAddress[]	getAllByName(String host) {
		return null;
		
	}
	/**Returns an InetAddress object given the raw IP address .*/
	static InetAddress	getByAddress(byte[] addr) {
		return null;
		
	}
	/**Creates an InetAddress based on the provided host name and IP address.*/
	static InetAddress	getByAddress(String host, byte[] addr) {
		return null;
		
	}
	/**Determines the IP address of a host, given the host's name.*/
	static InetAddress	getByName(String host) {
		return null;
		
	}
	/**Gets the fully qualified domain name for this IP address.*/
	String	getCanonicalHostName() {
		return null;
		
	}
	/**Returns the IP address string in textual presentation.*/
	String	getHostAddress() {
		return null;
		
	}
	/**Gets the host name for this IP address.*/
	String	getHostName() {
		return null;
		
	}
	/**Returns the address of the local host.*/
	static InetAddress	getLocalHost() {
		return null;
		
	}
	/**Returns the loopback address.*/
	static InetAddress	getLoopbackAddress() {
		return null;
		
	}
	/**Returns a hashcode for this IP address.*/
	public int	hashCode() {
		return 0;
		
	}
	/**Utility routine to check if the InetAddress in a wildcard address.*/
	boolean	isAnyLocalAddress() {
		return false;
		
	}
	/**Utility routine to check if the InetAddress is an link local address.*/
	boolean	isLinkLocalAddress() {
		return false;
		
	}
	/**Utility routine to check if the InetAddress is a loopback address.*/
	boolean	isLoopbackAddress() {
		return false;
		
	}
	/**Utility routine to check if the multicast address has global scope.*/
	boolean	isMCGlobal() {
		return false;
		
	}
	/**Utility routine to check if the multicast address has link scope.*/
	boolean	isMCLinkLocal() {
		return false;
		
	}
	/**Utility routine to check if the multicast address has node scope.*/
	boolean	isMCNodeLocal() {
		return false;
		
	}
	/**Utility routine to check if the multicast address has organization scope.*/
	boolean	isMCOrgLocal() {
		return false;
		
	}
	/**Utility routine to check if the multicast address has site scope.*/
	boolean	isMCSiteLocal() {
		return false;
		
	}
	/**Utility routine to check if the InetAddress is an IP multicast address.*/
	boolean	isMulticastAddress() {
		return false;
		
	}
	/**Test whether that address is reachable.*/
	boolean	isReachable(int timeout) {
		return false;
		
	}
	/**Test whether that address is reachable.*/
	boolean	isReachable(NetworkInterface netif, int ttl, int timeout) {
		return false;
		
	}
	/**Utility routine to check if the InetAddress is a site local address.*/
	boolean	isSiteLocalAddress() {
		return false;
		
	}
	/**Converts this IP address to a String.*/
	public String	toString() {
		return null;
		
	}
	
}