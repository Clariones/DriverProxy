package org.skynet.bgby.driverutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.skynet.bgby.command.management.UpdateResult;
import org.skynet.bgby.error.ErrorCode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class SimpleFileRepository<T> {
	protected static final String TAG = SimpleFileRepository.class.getName();
	protected static final int ERR_SET_DATA = ErrorCode.BASE_PC_REPOSITORY_CODE + 1;
	
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
	protected String convertToJsonStr(T data) throws IOException{
		return gson.toJson(data);
	}

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
		verifyData(data);
		cacheData(key, data);
		saveData(key, data);

	}
	protected abstract void verifyData(T data) throws IOException;
	
	protected void saveData(String key, T data) throws IOException {
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

	public Map<String, T> getAll(){
		List<String> ids = getAllDataIds();
		if (ids == null || ids.isEmpty()){
			return null;
		}
		Map<String, T> datas = new HashMap<>();
		for(String id: ids){
			datas.put(id, getDataByID(id));
		}
		return datas;
	}
	private List<String> getAllDataIds() {
		List<String> ids = new ArrayList<>();
		collectDataFileAsId(this.getBaseFolder(), ids);
		return ids;
	}
	private void collectDataFileAsId(File file, List<String> ids) {
		if (file.isDirectory()){
			for(File child: file.listFiles()){
				collectDataFileAsId(child, ids);
			}
			return;
		}
		if (!file.getName().endsWith(getFilePostfix())){
			return;
		}
		
		T result = loadDataFromFile(file);
		String key = getDataKey(file, result);
		cacheData(key, result);
		ids.add(key);
	}
	protected abstract String getDataKey(File dataFile, T result);
	
	public int clearAll(){
		List<String> ids = getAllDataIds();
		for(String id: ids){
			deleteData(id);
		}
		
		
		return ids.size();
	}
	protected void deleteData(String key) {
		String fileName = calcRelativeFileName(key);
		File tgtFile = new File(getBaseFolder(), fileName);
		cache.remove(key);
		if (!tgtFile.canWrite() || tgtFile.isDirectory()) {
			DriverUtils.log(Level.FINE, TAG, "Remove data for {0} failed: exist={1}, canRead={2}, isDirectoty{3}",
					new Object[] { tgtFile.getAbsolutePath(), tgtFile.exists(), tgtFile.canRead(),
							tgtFile.isDirectory() });
			return;
		}
		boolean deleted = tgtFile.delete();
		DriverUtils.log(Level.FINE, TAG, "Delete file " + tgtFile+": " + deleted);
		File pFile = tgtFile.getParentFile();
		while(pFile != null) {
			if (pFile.getAbsoluteFile().equals(getBaseFolder().getAbsoluteFile())){
				return;
			}
			File[] leftFiles = pFile.listFiles();
			if (leftFiles == null || leftFiles.length == 0){
				tgtFile = pFile.getParentFile();
				deleted = pFile.delete();
				DriverUtils.log(Level.FINE, TAG, "Delete folder " + tgtFile+": " + pFile);
				pFile = tgtFile;
			}else{
				return;
			}
		}
	}
	
	public UpdateResult update(Map<String, T> data, boolean overWriteAll) {
		UpdateResult rst = new UpdateResult();
		if (data == null || data.isEmpty()){
			return rst; // nothing to do. 
		}
		rst.setReceived(data.size());
		if (overWriteAll){
			rst.setDeleted(clearAll());
		}
		for(String id: data.keySet()){
			rst.incHandled();
			T oldData = getDataByID(id);
			if (oldData != null){
				rst.incUpdated();
			}else{
				rst.incAdded();
			}
			try {
				this.setData(id, data.get(id));
			} catch (IOException e) {
				e.printStackTrace();
				rst.incInvalid();
				rst.setErrorCode(ERR_SET_DATA);
				rst.setErrorTitle("Exception when save new data");
				rst.setErrorDetail(DriverUtils.dumpExceptionToString(e));
				return rst;
			}
		}
		return rst;
	}
}
