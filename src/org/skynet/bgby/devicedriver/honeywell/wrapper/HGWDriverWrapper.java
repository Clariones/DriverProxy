package org.skynet.bgby.devicedriver.honeywell.wrapper;

import java.util.Map;

import org.skynet.bgby.devicedriver.honeywell.HGW2000Controller;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000DriverConfig;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.protocol.IRestResponse;

public interface HGWDriverWrapper {

	IRestResponse execute(HGW2000Controller driver, Hgw2000DriverConfig config, String command, DeviceStatus deviceStatus,
			Map<String, String> params);

}
