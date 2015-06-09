package cn.wangmeng.test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

@SuppressLint("NewApi")
public class PicLoader {
	//¿ª±Ù8MÓ²»º´æ¿Õ¼ä  
    private final int hardCachedSize = 8*1024*1024; 

	private final LruCache<String, Bitmap> sHardBitmapCache = new LruCache<String, Bitmap>(hardCachedSize){
		protected int sizeOf(String key, Bitmap value) {
			return value.getRowBytes() * value.getHeight();  
		}
	};

	     public PicLoader() {
	     }
	  
	     @SuppressLint("NewApi")
		public Bitmap loadDrawable(final String imageUrl, final ImageCallback imageCallback) {
	    	 Bitmap bmp=sHardBitmapCache.get(imageUrl);
	         if (bmp!=null) {
	        	 return bmp;
	         }
	         final Handler handler = new Handler() {
	             public void handleMessage(Message message) {
	                 imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
	             }
	         };
	         new Thread() {
	             @Override
	             public void run() {
	                 Bitmap drawable = loadImageFromUrl(imageUrl);
	                 sHardBitmapCache.put(imageUrl, drawable);
	                 Message message = handler.obtainMessage(0, drawable);
	                 handler.sendMessage(message);
	             }
	         }.start();
	         return null;
	     }
	  
		public static Bitmap loadImageFromUrl(String url) {
			URL m;
			InputStream i = null;
			try {
				m = new URL(url);
				i = (InputStream) m.getContent();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Bitmap d = BitmapFactory.decodeStream(i);
			return d;
		}
	  
	     public interface ImageCallback {
	         public void imageLoaded(Drawable imageDrawable, String imageUrl);
	     }

}
