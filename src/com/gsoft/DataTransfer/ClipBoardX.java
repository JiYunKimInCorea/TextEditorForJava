package com.gsoft.DataTransfer;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ClipBoardX  implements ClipboardOwner {
	Clipboard clipBoard;
	String dataOfClipBoard;
	boolean isClipBoardUsable;
	//ClipBoardThread clipBoardThread;
	
	public ClipBoardX() {
		//clipBoard = new Clipboard("ForEditText");
		Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
		clipBoard = toolkit.getSystemClipboard();
		
		//Transferable t = clipBoard.getContents(this);
		
		//clipBoardThread = new ClipBoardThread(this);
		//clipBoardThread.start();
		
	}
	
	/*public boolean isClipBoardUsable() {
		if (isClipBoardUsable) return true;
		return false;
	}*/
	
	/** 클립보드에서 텍스트 데이터를 가져온다. 클립보드 데이터가 없으면 null을 리턴한다.*/
	public String getData() {
		return this.getClipBoardData();
	}
	
	
	class TransferableX implements Transferable {
		
		String data;
		
		public TransferableX(String data) {
			this.data = data;
		}

		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			// TODO Auto-generated method stub
			/*Reader reader = flavor.getReaderForText(this);
			
			char[] buf = new char[1];
			ArrayListChar list = new ArrayListChar(100); 
			int off = 0;
			while (true) {
				int count = reader.read(buf, off, 1);
				if (count<0) break;
				list.add(buf[0]);
			}
						
			char[] arr = list.getItems();
			return new String(arr);*/
			return data;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {			
			// TODO Auto-generated method stub
			DataFlavor[] r = {DataFlavor.stringFlavor};
			return r;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			// TODO Auto-generated method stub
			if (flavor.equals(DataFlavor.stringFlavor))
				return true;
			return false;
		}
		
	}
	
	public void setData(String data) {
		TransferableX transferable = new TransferableX(data); 
		this.clipBoard.setContents(transferable, this);
	}
	
	/*static class ClipBoardThread extends Thread {
		ClipBoardX observer;
		ClipBoardThread(ClipBoardX observer) {
			this.observer = observer;
		}
		public void run() {
			while (true) {
				try {
					observer.getClipBoardData();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						break;
					}
				}catch (Exception e) {
					// TODO Auto-generated catch block
					break;
				}
			}
		}
		public void killThread() {
			observer = null;
		}
	}*/
	
	String getClipBoardData() {
		DataFlavor[] flavors = clipBoard.getAvailableDataFlavors();
		int i;
		for (i=0; i<flavors.length; i++) {
			try {
				String name = flavors[i].getHumanPresentableName();
				String type = flavors[i].getSubType();
				if (name.equals("Unicode String")) {
					Object data = clipBoard.getData(flavors[i]);
					if (data instanceof String) {
						dataOfClipBoard = (String) data;
						return dataOfClipBoard;
					}
				}
			} catch (UnsupportedFlavorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void destroy() {
		/*if (clipBoardThread!=null) {
			clipBoardThread.killThread();
		}*/
	}

	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		// TODO Auto-generated method stub
		
	}

	
}