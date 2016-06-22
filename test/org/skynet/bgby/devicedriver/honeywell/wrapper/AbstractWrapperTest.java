package org.skynet.bgby.devicedriver.honeywell.wrapper;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.skynet.bgby.devicedriver.honeywell.ExecutionResult;
import org.skynet.bgby.devicedriver.honeywell.HGW2000Controller;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000DriverConfig;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.protocol.IRestResponse;

public class AbstractWrapperTest {

	@Test
	public void testParseResult() {
		String inStr = "g8F+tGKTuisbYG8OWKylF20NBPAOozttFOH+9z0RZ0UhVWiheqcPY1EQ/qXS6MZQMA==$cfg,hbuslig,1,2,4,0,100,0";
		// String inStr = "$cfg,hbuslig,1,2,4,0,100,0";
		Testee testee = new Testee();

		Map<String, String> rst = testee.parseResult(ControlHbusLight.cmdFormat, inStr);
		System.out.println(rst);
	}

}

class Testee extends AbstractWrapper {

	@Override
	protected IRestResponse convertResultToResponse(String command, DeviceStatus status, Map<String, String> params,
			ExecutionResult result) {
		return null;
	}

	@Override
	protected ExecutionResult invokeDriver(HGW2000Controller driver, Object apiArgs) throws IOException {
		return null;
	}

	@Override
	protected IRestResponse updateAndCheckParams(String command, Object apiArgs, Map<String, String> params) {
		return null;
	}

	@Override
	protected Object createArgsFromStatus(Hgw2000DriverConfig config, DeviceStatus deviceStatus) {
		return null;
	}

	@Override
	protected void updateStatus(String command, DeviceStatus deviceStatus, Map<String, String> params,
			IRestResponse response) {
		// TODO Auto-generated method stub

	}

}