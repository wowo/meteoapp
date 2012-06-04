package pl.sznapka.meteoapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.os.Environment;

/**
 * @author wowo
 * Unified Cache Manager
 */
public class CacheManager {
	
	protected File defaultCacheDir;
	
	public CacheManager(File defaultCacheDir) {
		
		this.defaultCacheDir = defaultCacheDir;
	}
	
	public File getDefaultCacheDir() {
		
		return defaultCacheDir;
	}
	
	public Object loadFromCache(String target) {
    	
    	File cacheFile = getCacheFile(target); 
    	if (cacheFile.exists()) {
	    	FileInputStream fis = null;
	    	ObjectInputStream ois = null;
	    	try {
	    		fis = new FileInputStream(cacheFile);
	    		ois = new ObjectInputStream(fis);
	    		return ois.readObject();
	    	} catch (Exception e) {
	    	} finally {
	    		try {
	    			if (fis != null) fis.close();
	    			if (ois != null) ois.close();
	    		} catch (Exception e) {
	    		}
	    	}
    	} 
		return null;
    }
    /**
     * Stores in cache
     * 
     * @param cities
     */
	public void storeObjectInCache(String target, Object object) {
    	
    	FileOutputStream fos = null;
    	ObjectOutputStream oos = null;
    	
    	try {
    		fos = new FileOutputStream(getCacheFile(target));
    		oos = new ObjectOutputStream(fos);
    		oos.writeObject(object);
    	} catch (Exception e) {
    	} finally {
    		try {
    			if (oos != null) oos.close();
    			if (fos != null) fos.close();
    		} catch (Exception e) {
			}
    	}
    }
    
    
    /**
     * Gets cache file
     * 
     * @param target
     */
    protected File getCacheFile(String target) {

    	String state = Environment.getExternalStorageState();
    	if (Environment.MEDIA_MOUNTED.equals(state) && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
    		System.out.println("Cache dir in SD card");
    		File dataDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/meteoApp");
    		dataDir.mkdir();
    		
    		return new File(dataDir.getAbsolutePath() + "/" + target + ".dat");
    	} else {
    		System.out.println("Cache dir in data");
    		
    		return new File(defaultCacheDir.getAbsoluteFile() + "/" + target + ".dat");
    	}
	}
}
