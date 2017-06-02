package in.playcer.smartView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by HARIKRISHNA on 8/21/2015.
 * at CaretTech
 */
public class WebImageCache {
    private static final String DISK_CACHE_PATH = "/web_image_cache/";

    private static ConcurrentHashMap<String, SoftReference<Bitmap>> memoryCache;
    private static String diskCachePath;
    private boolean diskCacheEnabled = false;
    private ExecutorService writeThread;

    public WebImageCache(Context context) {
        // Set up in-memory cache store
        memoryCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();

        // Set up disk cache store
        Context appContext = context.getApplicationContext();
        diskCachePath = appContext.getCacheDir().getAbsolutePath() + DISK_CACHE_PATH;

        File outFile = new File(diskCachePath);
        outFile.mkdirs();

        diskCacheEnabled = outFile.exists();

        // Set up thread pool for image fetching tasks
        writeThread = Executors.newSingleThreadExecutor();
    }

    public WebImageCache() {
		// TODO Auto-generated constructor stub
	}

	public Bitmap get(final String url) {
        Bitmap bitmap = null;

        // Check for image in memory
        bitmap = getBitmapFromMemory(url);

        // Check for image on disk cache
        if(bitmap == null) {
            bitmap = getBitmapFromDisk(url);

            // Write bitmap back into memory cache
            if(bitmap != null) {
                cacheBitmapToMemory(url, bitmap);
            }
        }

        return bitmap;
    }

    public void put(String url, Bitmap bitmap) {
        cacheBitmapToMemory(url, bitmap);
        cacheBitmapToDisk(url, bitmap);
    }

    public void remove(String url) {
        if(url == null){
            return;
        }

        // Remove from memory cache
        memoryCache.remove(getCacheKey(url));

        // Remove from file cache
        File f = new File(diskCachePath, getCacheKey(url));
        if(f.exists() && f.isFile()) {
            f.delete();
        }
    }

    public void clear() {
    	try {
            // Remove everything from memory cache
            memoryCache.clear();

            // Remove everything from file cache
            File cachedFileDir = new File(diskCachePath);
            if(cachedFileDir.exists() && cachedFileDir.isDirectory()) {
                File[] cachedFiles = cachedFileDir.listFiles();
                for(File f : cachedFiles) {
                    if(f.exists() && f.isFile()) {
                        f.delete();
                    }
                }
            }
		} catch (Exception e) {
			if (e != null) {
				e.printStackTrace();
			}
		}
    }

    private void cacheBitmapToMemory(final String url, final Bitmap bitmap) {
        memoryCache.put(getCacheKey(url), new SoftReference<Bitmap>(bitmap));
    }

    private void cacheBitmapToDisk(final String url, final Bitmap bitmap) {
        writeThread.execute(new Runnable() {
            @Override
            public void run() {
                if(diskCacheEnabled) {
                    BufferedOutputStream ostream = null;
                    try {
                        ostream = new BufferedOutputStream(new FileOutputStream(new File(diskCachePath, getCacheKey(url))), 2*1024);
                        bitmap.compress(CompressFormat.PNG, 100, ostream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if(ostream != null) {
                                ostream.flush();
                                ostream.close();
                            }
                        } catch (IOException e) {}
                    }
                }
            }
        });
    }

    private Bitmap getBitmapFromMemory(String url) {
        Bitmap bitmap = null;
        SoftReference<Bitmap> softRef = memoryCache.get(getCacheKey(url));
        if(softRef != null){
            bitmap = softRef.get();
        }

        return bitmap;
    }

    private Bitmap getBitmapFromDisk(String url) {
        Bitmap bitmap = null;
        if(diskCacheEnabled){
            String filePath = getFilePath(url);
            File file = new File(filePath);
            if(file.exists()) {
            	BitmapFactory.Options options = new BitmapFactory.Options();
                //bitmap = BitmapFactory.decodeFile(filePath);
            	try {
            		bitmap = BitmapFactory.decodeFile(filePath, options);
            	} catch (OutOfMemoryError e){
            		e.printStackTrace();
            		options.inSampleSize = 2;
            		bitmap = BitmapFactory.decodeFile(filePath, options);
            	}            	
            }
        }
        return bitmap;
    }

    private String getFilePath(String url) {
        return diskCachePath + getCacheKey(url);
    }

    private String getCacheKey(String url) {
        if(url == null){
            throw new RuntimeException("Null url passed in");
        } else {
            return url.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
        }
    }
}
