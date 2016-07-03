package org.skynet.bgby.deviceprofile;

import java.util.Map;
import java.util.Set;

public class DeviceProfile {
	protected String ID;
	protected String standard;
	protected String[] identifiers;
	protected Map<String, Object> spec;
	protected Map<String, Object> extParams;
	
	public Map<String, Object> getExtParams() {
		return extParams;
	}
	public void setExtParams(Map<String, Object> extParams) {
		this.extParams = extParams;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getStandard() {
		return standard;
	}
	public void setStandard(String standard) {
		this.standard = standard;
	}
	public String[] getIdentifiers() {
		return identifiers;
	}
	public void setIdentifiers(String[] identifiers) {
		this.identifiers = identifiers;
	}
	public Map<String, Object> getSpec() {
		return spec;
	}
	public void setSpec(Map<String, Object> spec) {
		this.spec = spec;
	}
	public Object getExtParam(String paramName) {
		if (extParams == null){
			return null;
		}
		return extParams.get(paramName);
	}
	
	
}
