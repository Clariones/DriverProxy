package org.skynet.bgby.layout;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.skynet.bgby.driverutils.SimpleFileRepository;

public class LayoutFileRepository extends SimpleFileRepository<List<LayoutData>> implements LayoutRepository{


	@Override
	protected String getFilePostfix() {
		return ".layout.json";
	}

	@Override
	protected List<LayoutData> loadFromFile(FileInputStream fIns) {
		Reader reader = new InputStreamReader(fIns);
		LayoutData[] data = LayoutUtils.gson.fromJson(reader, new LayoutData[0].getClass());
		List<LayoutData> result = new ArrayList<>();
		if (data == null || data.length == 0){
			return result;
		}
		for(int i=0;i<data.length;i++){
			result.add(data[i]);
		}
		return result;
	}

	@Override
	protected String convertToJsonStr(List<LayoutData> data) throws IOException{
		return LayoutUtils.toJson(data);
	}

	@Override
	public List<LayoutData> getLayoutByControllerID(String controllerID) {
		return getDataByID(controllerID);
	}

	@Override
	public void setControllerLayout(String controllerID, List<LayoutData> layoutData) throws IOException {
		setData(controllerID, layoutData);
	}

	@Override
	protected String getDataKey(File dataFile, List<LayoutData> result) {
		// TODO Auto-generated method stub
		String fileName = dataFile.getName();
		return fileName.substring(0, fileName.length()-getFilePostfix().length());
	}

	@Override
	protected void verifyData(List<LayoutData> data) throws IOException {
		if (data == null){
			throw new IOException("Try to save null data");
		}
	}

}
