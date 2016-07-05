package org.skynet.bgby.devicedriver.honeywell.wrapper;

import java.io.IOException;
import java.util.Map;

import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.devicedriver.honeywell.ExecutionResult;
import org.skynet.bgby.devicedriver.honeywell.HGW2000Controller;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000;
import org.skynet.bgby.devicedriver.honeywell.wrapper.ControlHbusLight.HBusLightArgs;
import org.skynet.bgby.devicestandard.SimpleDimmer;
import org.skynet.bgby.devicestandard.SimpleLight;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.driverproxy.ExecutionContext;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;

public class QueryHbusLight extends ControlHbusLight  {
	@Override
	protected ExecutionResult invokeDriver(HGW2000Controller driver, Object apiArgs) throws IOException {
		HBusLightArgs arg = (HBusLightArgs) apiArgs;
		return driver.queryHBusLight(arg.area, arg.loop);
	}

	@Override
	protected Object createArgsFromStatus(ExecutionContext ctx) {

		HBusLightArgs args = new HBusLightArgs();
		DeviceStatus status = ctx.getDevice();
		DeviceConfigData cfgData = ctx.getConfig();
		args.loop = DriverUtils.getAsInt(cfgData.getIdentity().get(Hgw2000.IDENTIFIER_LOOP), -1);
		args.area = DriverUtils.getAsInt(cfgData.getIdentity().get(Hgw2000.IDENTIFIER_AREA), -1);
		
		if (args.loop <= 0 || args.area <= 0) {
			throw new RuntimeException(status.getID() + " profile loop and area not correct:" + cfgData.getIdentity());
		}
		return args;
	}

	@Override
	protected IRestResponse updateAndCheckParams(ExecutionContext executionContext, Object apiArgs) {
		if (!(apiArgs instanceof HBusLightArgs)) {
			return newWrongStatusResult();
		}
		return null;
	}
	
	
	
}
