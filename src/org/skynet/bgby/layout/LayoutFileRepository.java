package org.skynet.bgby.layout;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.skynet.bgby.driverutils.SimpleFileRepository;

public class LayoutFileRepository extends SimpleFileRepository<List<ILayout>> implements LayoutRepository{


	@Override
	protected String getFilePostfix() {
		return ".layout.json";
	}

	@Override
	protected List<ILayout> loadFromFile(FileInputStream fIns) {
		Reader reader = new InputStreamReader(fIns);
		LayoutData[] data = LayoutUtils.gson.fromJson(reader, new LayoutData[0].getClass());
		List<ILayout> result = new ArrayList<>();
		if (data == null || data.length == 0){
			return result;
		}
		for(int i=0;i<data.length;i++){
			result.add(data[i]);
		}
		return result;
	}

	@Override
	protected String convertToJsonStr(List<ILayout> data) throws IOException{
		return LayoutUtils.toJson(data);
	}

	@Override
	public List<ILayout> getLayoutByControllerID(String controllerID) {
		return getDataByID(controllerID);
	}

	@Override
	public void setControllerLayout(String controllerID, List<ILayout> layoutData) throws IOException {
		setData(controllerID, layoutData);
	}

	

}
