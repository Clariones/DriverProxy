package org.skynet.bgby.layout;

import java.util.Map;

public interface ILayout {

	void setParams(Map<String, Object> params);

	Map<String, Object> getParams();

	void setType(String type);

	String getType();

	void init(ILayout layoutData) throws LayoutException;
}
