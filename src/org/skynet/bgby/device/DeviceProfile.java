package org.skynet.bgby.device;

import java.util.Map;

public class DeviceProfile {
	protected String ID;
	protected String standard;
	protected String[] identifiers;
	protected Map<String, Object> spec;
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
	
	
}
