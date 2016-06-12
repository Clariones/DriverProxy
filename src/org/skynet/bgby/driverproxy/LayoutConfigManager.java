package org.skynet.bgby.driverproxy;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.skynet.bgby.layout.ILayout;
import org.skynet.bgby.layout.LayoutData;
import org.skynet.bgby.layout.LayoutUtils;

public class LayoutConfigManager {

	public List<ILayout> getControllerLayout(String controllerID) {
		// TODO Below Just for debug
		String fileName = "C:/Users/Clariones/Desktop/巴国布衣项目/04.例子-布局.json.txt";
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(fileName);
			ILayout[] list = LayoutUtils.gson.fromJson(new FileReader(fileName), new LayoutData[0].getClass());
			return Arrays.asList(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if (fin != null){
				try {
					fin.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

}
