package android;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.gsoft.common.CommonGUI;
import com.gsoft.common.CompilerHelper;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.R.R;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Region.Op;


public class graphics {
	
	public static class PixelFormat {

	}

	public static class Region {

		public static enum Op {
			REPLACE
			
		}

	}

	public static class drawable {
		public static class Drawable {
			File imageFilePath;
			public Drawable(File imageFilePath) {
				this.imageFilePath = imageFilePath;
			}
		}
	}
	
	public static class PointF {
		public PointF(float x, float y) {
			// TODO Auto-generated constructor stub
			this.x = x;
			this.y = y;
		}
		public float x;
		public float y;
	}
	
	public static class Point {

		public Point(int x, int y) {
			// TODO Auto-generated constructor stub
			this.x = x;
			this.y = y;
		}
		public Point() {
			
		}
		public int x;
		public int y;
		
	}
	
	public static class Rect {

		public Rect(int left, int top, int right, int bottom) {
			// TODO Auto-generated constructor stub
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
					
		}
		public Rect() {
			// TODO Auto-generated constructor stub
		}
		public int left;
		public int top;
		public int right;
		public int bottom;
		
	}
	
	public static class RectF {

		public float left;
		public float top;
		public float right;
		public float bottom;

	}

	public static class BitmapFactory {

		public static Bitmap decodeResource(Resources resources, int id) throws IOException {
			// TODO Auto-generated method stub
			String path = Context.drawable_path+File.separator+R.drawable_value.values[id & 0x0000000f];
			String filename = FileHelper.getFilename(path);
			
			File drawableDir = new File(Context.drawable_path);
			String[] list = drawableDir.list();
			int i;
			for (i=0; i<list.length; i++) {
				String fn = FileHelper.getFilenameExceptExt(list[i]);
				if (fn.equals(filename)) break;
			}
			File file = new File(Context.drawable_path+File.separator+list[i]);
			BufferedImage image = ImageIO.read(file);
			return new Bitmap(image);
		}

		public static Bitmap decodeStream(InputStream iStream) throws IOException {
			// TODO Auto-generated method stub
			BufferedImage image = ImageIO.read(iStream);
			return new Bitmap(image);
		}

	}

	

	public static class Paint {

		public enum Style {
			FILL, STROKE

		}
		
		int color;
		Style style;
		float textSize;
		Typeface typeface = Typeface.DEFAULT;
		int alpha = 255;
		boolean isUnderline;
		
		/**디폴트 Graphics*/
		public static Graphics g;
		

		public void setStyle(Style style) {
			// TODO Auto-generated method stub
			this.style = style;
		}

		public void setColor(int color) {
			// TODO Auto-generated method stub
			this.color = color;
		}

		public int getColor() {
			// TODO Auto-generated method stub
			return color;
		}

		public Style getStyle() {
			// TODO Auto-generated method stub
			return style;
		}

		public float getTextSize() {
			// TODO Auto-generated method stub
			return textSize;
		}
		
		public synchronized void setTextSize(float textSize) {
			// TODO Auto-generated method stub
			this.textSize = textSize;
			Font font = new Font(typeface.font.getName(), typeface.font.getStyle(), (int)textSize);
			if (g!=null) g.setFont(font);
		}
		
		public Typeface getTypeface() {
			// TODO Auto-generated method stub
			return typeface;
		}
		
		public void setTypeface(Typeface typeface) {
			// TODO Auto-generated method stub
			this.typeface = typeface;
		}

		public synchronized float measureText(String text) {
			// TODO Auto-generated method stub
			FontMetrics m = null;
			int r = 0;
			try {
				if (text==null) {
					int a;
					a=0;
					a++;
				}
			m = g.getFontMetrics();
			r = m.charsWidth(text.toCharArray(), 0, text.length());
			}catch(Exception e) {				
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				setTextSize(textSize);
				m = g.getFontMetrics();
				r = m.charsWidth(text.toCharArray(), 0, text.length());
			}
			
			return r;
		}

		public void setAlpha(int alpha) {
			// TODO Auto-generated method stub
			this.alpha = alpha;
		}
		
		public int getAlpha() {
			return alpha;
		}
		
		public void setUnderlineText(boolean b) {
			// TODO Auto-generated method stub
			this.isUnderline = b;
		}

		/** @param text	The text to measure. Cannot be null.
		 *  @param start	The index of the first character to start measuring
		 *  @param end	1 beyond the index of the last character to measure*/
		public int measureText(String message, int start, int end) {
			// TODO Auto-generated method stub
			FontMetrics m = g.getFontMetrics();
			int r = m.charsWidth(message.toCharArray(), start, end-start);
			return r;
		}
	
	}

	public static class Canvas  {
		public Graphics g;
		
		/**CustomView의 public void paint(Graphics g)에서 생성된다. */
		public Canvas(Graphics g) {
			this.g = g;
		}
		
		public Canvas(Bitmap bitmap) {
			g = bitmap.getGraphics();
		}

		public void drawBitmap(Bitmap bitmap, Rect src, RectF dst,
				Paint paint) {
			// TODO Auto-generated method stub
			//Paint.g = g;
			g.drawImage(bitmap.image, 
					(int)dst.left, (int)dst.top, (int)dst.right, (int)dst.bottom, 
					src.left, src.top, src.right, src.bottom, null);
		}
		
		public void drawBitmap(Bitmap bitmap, float x, float y, Paint paint) {
			// TODO Auto-generated method stub
			//Paint.g = g;
			g.drawImage(bitmap.image, (int)x, (int)y, null);
		}

		public void drawText(String text, float x, float y,
				Paint paint) {
			// TODO Auto-generated method stub
			//Paint.g = g;
			Font font = paint.getTypeface().font;
			int textSize = (int) paint.getTextSize();
			Typeface typeface = new Typeface(font.getName(), textSize, font.getStyle()); 
			g.setFont(typeface.font);
			
			int color = paint.getColor();
			java.awt.Color awtColor = Color.fromInt(color);
			g.setColor(awtColor);
			
			
			//int yJava = (int) (y - textSize); 
			g.drawString(text, (int)x, (int)y);
			
		}

		public void drawRect(RectF rectF, Paint paint) {
			// TODO Auto-generated method stub
			//Paint.g = g;
			int color = paint.getColor();
			java.awt.Color awtColor = Color.fromInt(color);
			g.setColor(awtColor);
			
			if (paint.getStyle()!=Paint.Style.FILL) {
				g.drawRect((int)rectF.left, (int)rectF.top, 
						(int)(rectF.right-rectF.left), (int)(rectF.bottom-rectF.top));
			}
			else {
				//java.awt.Color xorColor = Color.fromInt(ColorEx.reverseColor(color)); 
				//g.setXORMode(xorColor);
				if (paint.getAlpha()!=255) {
					return;
				}
				g.fillRect((int)rectF.left, (int)rectF.top, 
						(int)(rectF.right-rectF.left), (int)(rectF.bottom-rectF.top));
			}
			
		}

		public void drawRoundRect(RectF rectF, float rx, float ry, Paint paint) {
			// TODO Auto-generated method stub
			//Paint.g = g;
			int color = paint.getColor();
			java.awt.Color awtColor = Color.fromInt(color);
			g.setColor(awtColor);
			
			if (paint.getStyle()!=Paint.Style.FILL) {
				g.drawRoundRect((int)rectF.left, (int)rectF.top, 
						(int)(rectF.right-rectF.left), (int)(rectF.bottom-rectF.top), 
						(int)rx, (int)ry);
			}
			else {
				g.fillRoundRect((int)rectF.left, (int)rectF.top, 
						(int)(rectF.right-rectF.left), (int)(rectF.bottom-rectF.top), 
						(int)rx, (int)ry);
			}
		}
		
		public void drawColor(int colorBackground) {
			// TODO Auto-generated method stub
			java.awt.Color awtColor = Color.fromInt(colorBackground);
			g.setColor(awtColor);
						
		}

		public boolean clipRect(Rect clipRect, Op replace) {
			// TODO Auto-generated method stub
			g.setClip(clipRect.left, clipRect.top, 
					clipRect.right-clipRect.left, clipRect.bottom-clipRect.top);
			return false;
		}

		public void restore() {
			// TODO Auto-generated method stub
			g.setClip(null);
		}

		public void save() {
			// TODO Auto-generated method stub
			
		}
	}
	
	public static class Color {

		public static final int WHITE = java.awt.Color.WHITE.getRGB();
		public static final int BLACK = java.awt.Color.BLACK.getRGB();
		public static final int YELLOW = java.awt.Color.YELLOW.getRGB();
		public static final int MAGENTA = java.awt.Color.MAGENTA.getRGB();
		public static final int BLUE = java.awt.Color.BLUE.getRGB();
		public static final int RED = java.awt.Color.RED.getRGB();
		public static final int CYAN = java.awt.Color.CYAN.getRGB();
		public static final int DKGRAY = java.awt.Color.DARK_GRAY.getRed();
		public static final int GRAY = java.awt.Color.GRAY.getRGB();
		public static final int GREEN = java.awt.Color.GREEN.getRGB();
		public static final int LTGRAY = java.awt.Color.LIGHT_GRAY.getRGB();
		public static final int TRANSPARENT = 0;
		
		/** Color(int rgba, boolean hasalpha)
			Creates an sRGB color with the specified combined RGBA value 
			consisting of the alpha component in bits 24-31, 
			the red component in bits 16-23, the green component in bits 8-15, 
			and the blue component in bits 0-7.*/
		public static java.awt.Color fromInt(int color) {
			java.awt.Color r = new java.awt.Color(color, true);
			return r;
		}
		
		public static int red(int color) {
			// TODO Auto-generated method stub
			int r = (color & 0x00ff0000) >> 16;
			return r;
		}
		
		public static int green(int color) {
			// TODO Auto-generated method stub
			int g = (color & 0x0000ff00) >> 8;
			return g;
		}
		
		public static int blue(int color) {
			// TODO Auto-generated method stub
			int b = (color & 0x000000ff);
			return b;
		}

		public static int rgb(int r, int g, int b) {
			// TODO Auto-generated method stub
			int color = (255 << 24) | (r << 16) | (g << 8) | (b);
			return color;
		}
		
	}
	
	
	
	public static class Typeface {
		public static Typeface DEFAULT = Typeface.create(Typeface.DEFAULT, Typeface.PLAIN);		
		public static Typeface SANS_SERIF = Typeface.create(Typeface.SANS_SERIF, Typeface.PLAIN);
		public static Typeface SERIF = Typeface.create(Typeface.SERIF, Typeface.PLAIN);
		public static int PLAIN = 0;
		public static int BOLD = 1;
		public static int ITALIC = 2;
		public static int BOLD_ITALIC = 3;
		
		public Font font;
		
		/** @param familyName : java.awt.Font의 familyName
		 * @param style : java.awt.Font의 style
		 */
		public Typeface(String familyName, int textSize, int style) {
			Font font = new Font(familyName, style, textSize);
			this.font = font;
		}

		public static Typeface create(Typeface family, int style) {
			String name = Font.DIALOG;
			if (family==DEFAULT) name = Font.DIALOG;
			else if (family==SANS_SERIF) name = Font.SANS_SERIF;
			else if (family==SERIF) name = Font.SERIF;
			
			int fontStyle = Font.PLAIN;
			if (style==BOLD) fontStyle = Font.BOLD;
			else if (style==ITALIC) fontStyle = Font.ITALIC;
			else if (style==BOLD_ITALIC) fontStyle = Font.BOLD;
			
			return new Typeface(name, 20, fontStyle);
		}

		public static Typeface createFromAsset(AssetManager assets,
				String name) {
			// TODO Auto-generated method stub
			return Typeface.DEFAULT;
		}
	}
	
	public static class Bitmap {
		public static enum CompressFormat {
			JPEG,
			PNG,
			WEBP
		}
		
		public enum Config {
			ALPHA_8, 	//Each pixel is stored as a single translucency (alpha) channel. 
			ARGB_4444,	//This field was deprecated in API level 13. Because of the poor quality of this configuration, it is advised to use ARGB_8888 instead.  
			ARGB_8888, 	//Each pixel is stored on 4 bytes. 
			RGB_565 
		}
		
		public static int getBufferedImageType(Config bitmapConfig) {
			if (bitmapConfig==Config.RGB_565) return BufferedImage.TYPE_USHORT_565_RGB;
			return BufferedImage.TYPE_INT_RGB;
		}
		
		int width;
		int height;
		public BufferedImage image;
		
		public Bitmap(BufferedImage image) {
			this.image= image;
			width = image.getWidth();
			height = image.getHeight();
		}

		public static Bitmap createBitmap(Bitmap bitmap, int x,
				int y, int w, int h) {
			// TODO Auto-generated method stub
			return new Bitmap(bitmap.image);
		}
		
		public static Bitmap createBitmap(int width, int height, Bitmap.Config config) {
			int type = getBufferedImageType(config);
			Bitmap r = new Bitmap(width, height, type);
			return r;
		}
		
		public Bitmap(File file) throws IOException {
			this.image = ImageIO.read(file);
			width = image.getWidth();
			height = image.getHeight();
		}
		
		/** @param type : BufferedImage.TYPE_INT_ARGB 등*/
		public Bitmap(int w, int h, int type) {
			image = new BufferedImage(w, h, type);
			width = image.getWidth();
			height = image.getHeight();
		}
		
		public java.awt.Graphics getGraphics() {
			return image.getGraphics();
		}

		public int getWidth() {
			// TODO Auto-generated method stub
			return width;
		}

		public int getHeight() {
			// TODO Auto-generated method stub
			return height;
		}

		public void compress(CompressFormat format, int quality, OutputStream os) {
			// TODO Auto-generated method stub
			try {
				if (format==CompressFormat.PNG) {
					ImageIO.write(image, "png", os);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		public int getPixel(int x, int y) {
			// TODO Auto-generated method stub
			return image.getRGB(x, y);
		}

		public void setPixel(int x, int y, int color) {
			// TODO Auto-generated method stub
			image.setRGB(x, y, color);
		}

		public boolean isMutable() {
			// TODO Auto-generated method stub
			return true;
		}
		
	}
}