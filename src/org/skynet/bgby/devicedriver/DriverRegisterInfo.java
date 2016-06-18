package org.skynet.bgby.devicedriver;

import com.google.gson.JsonElement;

public class DriverRegisterInfo {
	protected String ID;
	protected String driverClass;
	protected String configurationClass;
	protected JsonElement configurationData;
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getDriverClass() {
		return driverClass;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	public String getConfigurationClass() {
		return configurationClass;
	}
	public void setConfigurationClass(String configurationClass) {
		this.configurationClass = configurationClass;
	}
	public JsonElement getConfigurationData() {
		return configurationData;
	}
	public void setConfigurationData(JsonElement configurationData) {
		this.configurationData = configurationData;
	}
	
}
