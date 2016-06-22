package org.skynet.bgby.driverutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class SimpleFileRepository<T> {
	protected static final String TAG = SimpleFileRepository.class.getName();
	protected File baseFolder;
	protected Map<String, T> cache;
	protected boolean isDevelopingMode = false;
	protected Gson gson = new GsonBuilder().setPrettyPrinting().create();

	protected void cacheData(String key, T result) {
		if (cache == null){
			cache = new HashMap<>();
		}
		cache.put(key, result);
	}
	protected String calcRelativeFileName(String id){
		return id.replaceAll("[ \\-]", "_").replace('.', '/') + getFilePostfix();
	}
	protected abstract String convertToJsonStr(T data) throws IOException;

	public File getBaseFolder() {
		return baseFolder;
	}

	protected T getCachedData(String key) {
		if (cache == null){
			return null;
		}
		return cache.get(key);
	}
	
	public T getDataByID(String key){
		T result = null;
		if (!isDevelopingMode) {
			result = getCachedData(key);
			if (result != null) {
				DriverUtils.log(Level.FINE, TAG, "Get cached data for {0}", key);
				return result;
			}
		}
		
		String fileName = calcRelativeFileName(key);
		File tgtFile = new File(getBaseFolder(), fileName);
		if (!tgtFile.exists() || !tgtFile.canRead() || tgtFile.isDirectory()) {
			DriverUtils.log(Level.FINE, TAG, "Get data for {0} failed: exist={1}, canRead={2}, isDirectoty{3}",
					new Object[] { tgtFile.getAbsolutePath(), tgtFile.exists(), tgtFile.canRead(),
							tgtFile.isDirectory() });
			return null;
		}
		result = loadDataFromFile(tgtFile);
		if (result == null) {
			return null;
		}
		cacheData(key, result);
		return result;
		
	}

	protected abstract String getFilePostfix();

	public boolean isDevelopingMode() {
		return isDevelopingMode;
	}

	protected T loadDataFromFile(File tgtFile) {
		FileInputStream fIns = null;
		try {
			fIns = new FileInputStream(tgtFile);
			T result = loadFromFile(fIns);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fIns != null) {
				try {
					fIns.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	protected abstract T loadFromFile(FileInputStream fIns);

	public void setBaseFolder(File baseFolder) {
		this.baseFolder = baseFolder;
	}
	
	public void setData(String key, T data) throws IOException {
		this.cacheData(key, data);
		String jsonStr = convertToJsonStr(data);
		String fileName = calcRelativeFileName(key);
		File tgtFile = new File(getBaseFolder(), fileName);

		if (tgtFile.exists()) {
			if (!tgtFile.delete()) {
				DriverUtils.log(Level.SEVERE, TAG,
						"Cannot remove old data {0}: exist={1}, canRead={2}, isDirectoty{3}",
						new Object[] { tgtFile.getAbsolutePath(), tgtFile.exists(), tgtFile.canRead(),
								tgtFile.isDirectory() });
				throw new IOException("Cannot delete " + tgtFile.getAbsolutePath());
			} else {
				DriverUtils.log(Level.FINE, TAG, "Remove old data {0}", tgtFile.getAbsolutePath());
			}
		}else{
			File pFile = tgtFile.getParentFile();
			DriverUtils.log(Level.INFO, TAG, "Check folder existed or not: " + pFile.getAbsolutePath());
			boolean done;
			if (!pFile.exists()){
				done = pFile.mkdirs();
				DriverUtils.log(Level.INFO, TAG, "Create folder " + pFile.getAbsolutePath() + ": " + done);
			}
			done = tgtFile.createNewFile();
			DriverUtils.log(Level.INFO, TAG, "Create file " + tgtFile.getAbsolutePath() + ": " + done);
		}

		FileWriter fw = new FileWriter(tgtFile);
		try {
			fw.write(jsonStr);
			fw.flush();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (Exception e) {
				}
			}
		}

	}
	public void setDevelopingMode(boolean isDevelopingMode) {
		this.isDevelopingMode = isDevelopingMode;
	}


}
