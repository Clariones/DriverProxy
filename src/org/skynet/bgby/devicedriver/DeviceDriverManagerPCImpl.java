package org.skynet.bgby.devicedriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.skynet.bgby.driverutils.DriverUtils;

import com.google.gson.Gson;

public class DeviceDriverManagerPCImpl extends DriverManagerImpl {
	protected File baseFolder;

	public File getBaseFolder() {
		return baseFolder;
	}

	public void setBaseFolder(File baseFolder) {
		this.baseFolder = baseFolder;
	}

	protected void loadDriverRegisterInfos() throws DriverModuleException{
		verifyBaseFolder();
		List<File> driverRegisterFiles = findAllDriverRegisterFiles();
		if (driverRegisterFiles.isEmpty()) {
			throw new DriverModuleException(
					"Cannot found any valid driver register info file under " + baseFolder.getAbsolutePath());
		}
		startingReporter.reportStatus(this.curStartingStep(), "Search all driver register infos",
				"Totally found " + driverRegisterFiles.size() + " driver infos");
		for (File file : driverRegisterFiles) {
			try {
				loadDriver(file);
			} catch (Exception e) {
				// e.printStackTrace();
				String msg = DriverUtils.dumpExceptionToString(e);
				startingReporter.reportError(this.curStartingStep(), "Failed to create driver", msg);
				throw new DriverModuleException("Failed to create driver", e);
			}
		}
	}

	protected void loadDriver(File file) {
		FileReader reader = null;
		Gson gson = new Gson();
		try {
			reader = new FileReader(file);
			DriverRegisterInfo config = gson.fromJson(reader, DriverRegisterInfo.class);
			addDriverRegisterInfo(config);
		} catch (FileNotFoundException e) {
			// this should not happen
			assert(false);
		}
	}

	protected void verifyBaseFolder() throws DriverModuleException {
		if (baseFolder == null) {
			throw new DriverModuleException("Base folder for DriverManager not set");
		}
		if (!baseFolder.exists() || !baseFolder.isDirectory() || !baseFolder.canRead()) {
			String msg = String.format("Base folder %s invalid: exist:%b, canRead:%b, isDirectory:%b",
					baseFolder.getAbsolutePath(), baseFolder.exists(), baseFolder.canRead(), baseFolder.isDirectory());

			throw new DriverModuleException(msg);
		}
	}

	protected List<File> findAllDriverRegisterFiles() {
		List<File> files = new ArrayList<>();
		addFilesFromFolder(files, getBaseFolder());
		return files;
	}

	protected void addFilesFromFolder(List<File> files, File folder) {
		File[] curFiles = folder.listFiles();
		for (File file : curFiles) {
			if (file.isDirectory()) {
				addFilesFromFolder(files, file);
			} else if (file.getName().endsWith(".driver.json")) {
				files.add(file);
			}
		}
	}

}
