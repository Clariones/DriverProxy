package org.skynet.bgby.layout;

import java.util.Map;

public abstract class LayoutBaseImpl implements ILayout {
	protected String type;
	protected Map<String, Object> params;
	@Override
	public String getType() {
		return type;
	}
	@Override
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public Map<String, Object> getParams() {
		return params;
	}
	@Override
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	
	@Override
	public void init(ILayout layoutData) throws LayoutException {
		setParams(layoutData.getParams());
		initByParameters(layoutData.getParams());
	}
	
	protected abstract void initByParameters(Map<String, Object> params);
	
}
