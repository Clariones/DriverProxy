package org.skynet.bgby.devicedriver.honeywell.wrapper;

import java.io.IOException;

import org.skynet.bgby.devicedriver.honeywell.ExecutionResult;
import org.skynet.bgby.devicedriver.honeywell.HGW2000Controller;

public class ControlLight extends ControlHbusLight {

	@Override
	protected ExecutionResult invokeDriver(HGW2000Controller driver, Object apiArgs) throws IOException {
		// TODO
		return driver.controlLight(1, 1, 1, 1);
	}

}
