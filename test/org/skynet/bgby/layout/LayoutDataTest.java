package org.skynet.bgby.layout;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class LayoutDataTest {

	@Test
	public void testInit() {
		String fileName = "C:/Users/Clariones/Desktop/巴国布衣项目/04.例子-布局.json.txt";
//		Gson gson = new Gson();
//		try {
//			LayoutData[] data = gson.fromJson(new FileReader(fileName), new LayoutData[0].getClass());
//			
//			System.out.println(data[0].getParams());
//		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(fileName);
			List<ILayout> list = LayoutUtils.fromJson(fin);
			System.out.println(list);
			
			String jsonStr = LayoutUtils.toJson(list);
			System.out.println(jsonStr);
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
	}

}
