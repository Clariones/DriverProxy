package org.skynet.bgby.deviceprofile;

import java.util.HashMap;
import java.util.Map;

public class DeviceProfileManagerPCImpl implements DeviceProfileManager {

	public DeviceProfile getProfile(String profileID) {
		DeviceProfile demoProfile = new DeviceProfile();
		demoProfile.setID("Honeywell HDW 2000 HVAC");
		demoProfile.setStandard("HVAC.Normal");
		demoProfile.setIdentifiers(new String[]{"ipAddress","id"});
		Map<String, Object> spec = new HashMap<>();
		spec.put("validRunningModes", new String[]{"关机","制冷","制热","自动"});
		spec.put("roomTemperatureRange", new double[]{16.0,35.0});
		spec.put("temperatureSettingRange", new double[]{16.0,35.0});
		spec.put("validFanModes", new String[]{"高速","舒适","柔和"});
		spec.put("humidityFunction", "no");
		demoProfile.setSpec(spec);
		return demoProfile;
	}

}
