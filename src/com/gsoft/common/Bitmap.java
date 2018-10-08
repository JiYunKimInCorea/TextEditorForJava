package com.gsoft.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.Log;

class BitmapFileHeader{

	/*UInt16*/short Type;            //BM 이라고 써있으면 bmp
	int Size;           //이미지 크기
	short Reserved1; 
	short Reserved2; 
	int OffBits;      //이미지 데이터가 있는 곳의 포인터
};

class BitmapInfoHeader {

	int Size;          //현 구조체의 크기 
	long Width;          //이미지의 가로 크기 
	long Height;         //이미지의 세로 크기
	short Planes;        //플레인수
	short BitCount;     //비트 수 
	int Compression;  //압축 유무 
	int SizeImage;       //이미지 크기 
	long XPelsPerMeter;  //미터당 가로 픽셀 
	long YPelsPerMeter;  //미터당 세로 픽셀 
	int ClrUsed;         //컬러 사용 유무 
	int ClrImportant;  //중요하게 사용하는 색 
}

class Palette {

	Color[] colorTable;
}


public class Bitmap
{

	BitmapFileHeader fileHeader;
	BitmapInfoHeader infoHeader;
	Palette palette;

	byte[] imageData;
	byte[] data;

	int width;
	int height;
	PixelFormat format;

	FileInputStream fs;
	BufferedInputStream  bis;
	InputStream is;
	
	public Bitmap(String fileName)
	{
		try {
			fs = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			Log.e("Bitmap",e.toString());
		}
		bis = new BufferedInputStream (fs);

		loadBitmapFileHeader();
		loadBitmapInfoHeader();
		loadPalette();
		loadImageData();
	}
	
	public Bitmap(InputStream is)
	{	
		this.is = is;
		bis = new BufferedInputStream (is);
				
		loadBitmapFileHeader();
		loadBitmapInfoHeader();
		loadPalette();
		loadImageData();
	}
	
	short readUInt16() throws IOException {
		byte[] buf = new byte[2];
		bis.read(buf, 0, 2);
		short r = 0;
		r = (short) (((short)buf[1])<<8 | buf[0]);
		return r;
	}
	
	short readInt16() throws IOException {
		byte[] buf = new byte[2];
		bis.read(buf, 0, 2);
		short r = 0;
		r = (short) (((short)buf[1])<<8 | buf[0]);
		return r;
	}
	
	int readInt32() throws IOException {
		byte[] buf = new byte[4];
		bis.read(buf, 0, 4);
		int r = 0;
		r = (int) (((int)buf[3])<<24 | ((int)buf[2])<<16 | ((int)buf[1])<<8 | buf[0]);
		return r;
	}

	
	void loadBitmapFileHeader() {
		try {
			//fs->Seek(0,SeekOrigin::Begin);
			// ReadUInt16
			fileHeader = new BitmapFileHeader();
			short type = readUInt16();
			fileHeader.Type = type;
			if (fileHeader.Type!=19778)
				throw new Exception("지원되지 않는 이미지 포맷입니다");
						
			fileHeader.Size = readInt32();
			fileHeader.Reserved1 = readInt16();
			fileHeader.Reserved2 = readInt16();
			fileHeader.OffBits = readInt32();
		}catch(Exception e) {
			Log.e("loadBitmapFileHeader", e.toString());
			//fs->Seek(sizeof(BitmapFileHeader),SeekOrigin::Begin);
		}
	}

	void loadBitmapInfoHeader() {
		/*try {
			infoHeader.Size = reader->ReadInt32();
		}catch(IOException) {
			fs->Seek(18,SeekOrigin::Begin);
		}
		try {
			infoHeader.Width = reader->ReadInt32();
			width = infoHeader.Width;		
		}catch(IOException e) {
			fs->Seek(22,SeekOrigin::Begin);
			throw e;
		}
		try {
			infoHeader.Height = reader->ReadInt32();
			height = infoHeader.Height;
		}catch(IOException e) {
			fs->Seek(26,SeekOrigin::Begin);
			throw e;
		}
		try {
			infoHeader.Planes = reader->ReadInt16();
		}catch(IOException) {
			fs->Seek(28,SeekOrigin::Begin);
		}
		try {
			infoHeader.BitCount = reader->ReadInt16();
			switch (infoHeader.BitCount) {
				case 1:
					format = PixelFormat::Format1bppIndexed; break;
				case 4:
					format = PixelFormat::Format4bppIndexed; break;
				case 8:
					format = PixelFormat::Format8bppIndexed; break;
				case 16:
					format = PixelFormat::Format16bppRgb555; break;
				case 24:
					format = PixelFormat::Format24bppRgb; break;
				case 32:
					format = PixelFormat::Format32bppArgb; break;
			}
		}catch(IOException e) {
			fs->Seek(30,SeekOrigin::Begin);
			throw e;
		}
		try {
			infoHeader.Compression = reader->ReadInt32();
		}catch(IOException) {
			fs->Seek(34,SeekOrigin::Begin);
		}
		try {
			infoHeader.SizeImage = reader->ReadInt32();
		}catch(IOException) {
			fs->Seek(38,SeekOrigin::Begin);
		}
		try {
			infoHeader.XPelsPerMeter = reader->ReadInt32();
			infoHeader.YPelsPerMeter = reader->ReadInt32();
			infoHeader.ClrUsed = reader->ReadInt32();
			infoHeader.ClrImportant = reader->ReadInt32();
		}catch(IOException) {
			fs->Seek(54,SeekOrigin::Begin);
		}*/		
	}

	void loadPalette() {
		if (infoHeader.BitCount == 1) {
		} else if (infoHeader.BitCount == 4) {
		} else if (infoHeader.BitCount == 8) {
		}
		else { // 24, 32  // 팔레트를 사용안 함.
			return;
		}

		/*palette = new Palette();		
		palette->ColorTable = new array<Color>(colorTableLength);
		
		array<Byte> buffer;
		buffer = reader->ReadBytes(4*colorTableLength);

		register int i;
		Color color;
		for (i=0; i<colorTableLength; i++) {
			color = Color::FromArgb(255,buffer[4*i+2],buffer[4*i+1],buffer[4*i]);
			palette->ColorTable[i] = color;
		}*/
	}

	void loadImageData() {
		/*int i, j, k=0;
		Color curColor, nextColor;
		int start, end;
		int elementsCount=0;
		int rowSize;

		fs->Seek(this->fileHeader.OffBits,SeekOrigin::Begin);

		if (infoHeader.BitCount==24 || infoHeader.BitCount==32) {
			rowSize = 4*(int)Math::Floor((infoHeader.BitCount*width+31)/32);
		}
		else if (infoHeader.BitCount==8) {
			rowSize = this->width;
		}
		else if (infoHeader.BitCount==4) {
			rowSize = this->width/2;
		}
		
		imageData = new array<array<Byte>>(height);
		imageIndex = new array<array<ImageElement>>(height);		
			
		for (j=0; j<height; j++) {
			imageData[height-1-j] = reader->ReadBytes(rowSize);

			imageIndex[height-1-j] = new array<ImageElement>(infoHeader.Width);
			elementsCount = 0;
			curColor = getColor(0,height-1-j);
			start = 0;
			for (i=1; i<width; i++) {
				nextColor = getColor(i,height-1-j);
				if (curColor!=nextColor) {
					end = i-1;
					imageIndex[height-1-j][elementsCount++] = ImageElement(curColor,height-1-j,start,end);
					start = i;
					curColor = nextColor;
				}
			}
			end = i-1;
			imageIndex[height-1-j][elementsCount++] = ImageElement(curColor,height-1-j,start,end);
			Array::Resize(imageIndex[height-1-j],elementsCount);

		}

		fs->Close();
		reader->Close();*/			
		
		//delete imageData;
	}

	Color getColor(int col, int row) {
		//try {
		/*Color color=Color::FromArgb(0,0,0);
		int index;
		if (infoHeader.BitCount==1) {
			Byte colorByte = imageData[row][col/8];
			colorByte = colorByte << (col%8);
			Byte indexByte = colorByte & 0x80;
			if (indexByte==0x80) index = 1;
			else index = 0;			
			return palette->ColorTable[index];
		}
		else if (infoHeader.BitCount==4) {
			if (col%2==0) {
				color = palette->ColorTable[imageData[row][col/2]&0x0f];
			}
			else {
				color = palette->ColorTable[(imageData[row][col/2]>>4)&0x0f];
			}
			return color;
		}
		else if (infoHeader.BitCount==8) {			
			color = palette->ColorTable[imageData[row][col]];
			return color;
		}
		else if (infoHeader.BitCount==16) {
			//return Color::FromArgb(imageData[row][col]);
		}
		else if (infoHeader.BitCount==24) {
			color = Color::FromArgb(imageData[row][3*col+2],imageData[row][3*col+1],imageData[row][3*col]); 
			return color;
		}
		else if (infoHeader.BitCount==32) {
			color = Color::FromArgb(imageData[row][4*col+3],imageData[row][4*col+2],imageData[row][4*col+1],imageData[row][4*col+0]); 
			if (color.A==0)
				color = Color::FromArgb(200,200,200);
			return color;
		}		
		return color;*/
		return null;
	}
};