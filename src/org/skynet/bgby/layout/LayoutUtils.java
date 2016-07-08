package org.skynet.bgby.layout;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LayoutUtils {
	public static final Gson gson;
	static{
		gson= new GsonBuilder().setPrettyPrinting().create();
	}
	protected LayoutUtils(){}
	public static final Map<String, Class> layoutTypes = new HashMap<String, Class>();
	public static final String PARAM_DEVICE_ID = "deviceID";
	public static void registerLayoutType(String type, Class clazz){
		layoutTypes.put(type, clazz);
	}
	public static Class getLayoutComponentByType(String type){
		return layoutTypes.get(type);
	}
	
	public static List<ILayout> fromJson(InputStream ins) throws LayoutException{
		InputStreamReader reader = new InputStreamReader(ins);
		LayoutData[] datas = new Gson().fromJson(reader, new LayoutData[0].getClass());
		List<ILayout> result = new ArrayList<ILayout>();
		for(int i=0;i<datas.length;i++){
			LayoutData data = datas[i];
			ILayout instance = createLayoutInstance(data);
			result.add(instance);
		}
		return result;
	}
	
	public static ILayout createLayoutInstance(ILayout data) throws LayoutException {
		String type = data.getType();
		Class clazz = LayoutUtils.getLayoutComponentByType(type);
		if (clazz == null){
			throw new LayoutException("No class registered for type " + type);
		}
		ILayout child = null;
		try {
			child = (ILayout) clazz.newInstance();
			child.init(data);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new LayoutException(e);
		}
		return child;
	}

	public static String toJson(List<? extends ILayout> layouts) {
		List<LayoutData> layoutDatas = new ArrayList<>();
		if (layouts == null || layouts.isEmpty()){
			return "[]";
		}
		Iterator<? extends ILayout> it = layouts.iterator();
		while(it.hasNext()){
			ILayout comp = it.next();
			LayoutData data = getLayoutData(comp);
			layoutDatas.add(data);
		}
		return gson.toJson(layoutDatas);
	}

	protected static LayoutData getLayoutData(ILayout comp) {
		LayoutData data = new LayoutData();
		data.setType(comp.getType());
		data.setParams(comp.getParams());
		if (!(comp instanceof ILayoutGroup)){
			return data;
		}
		ILayoutGroup group = (ILayoutGroup) comp;
		List<ILayout> children = group.getLayoutContent();
		if (children == null || children.isEmpty()){
			return data;
		}
		List<LayoutData> childrenData = new ArrayList<LayoutData>();
		Iterator<ILayout> it = children.iterator();
		while(it.hasNext()){
			ILayout child = it.next();
			LayoutData childData = getLayoutData(child);
			childrenData.add(childData);
		}
		data.setLayoutContent(childrenData);
		return data;
	}
}
