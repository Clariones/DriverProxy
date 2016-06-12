package org.skynet.bgby.layout;

import java.util.List;
import java.util.Map;

public class LayoutData implements ILayoutGroup {
	protected String type;
	protected Map<String, Object> params;
	protected List<LayoutData> layoutContent;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	public List getLayoutContent() {
		return layoutContent;
	}
	public void setLayoutContent(List layoutContent) {
		this.layoutContent = layoutContent;
	}
	@Override
	public void init(ILayout layoutData) throws LayoutException {
		throw new UnsupportedOperationException();
	}
	
		
}
