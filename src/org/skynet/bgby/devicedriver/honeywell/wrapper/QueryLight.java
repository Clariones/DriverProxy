package org.skynet.bgby.devicedriver.honeywell.wrapper;

import java.io.IOException;

import org.skynet.bgby.devicedriver.honeywell.ExecutionResult;
import org.skynet.bgby.devicedriver.honeywell.HGW2000Controller;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000;
import org.skynet.bgby.driverproxy.ExecutionContext;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;

public class QueryLight extends ControlLight  {

	@Override
	protected ExecutionResult invokeDriver(HGW2000Controller driver, Object apiArgs) throws IOException {
		return driver.queryLight((int)apiArgs);
	}

	@Override
	protected IRestResponse updateAndCheckParams(ExecutionContext executionContext, Object apiArgs) {
		if (apiArgs == null) {
			return newWrongStatusResult();
		}
		return null;
	}

	@Override
	protected Object createArgsFromStatus(ExecutionContext ctx) {
		int id = DriverUtils.getAsInt(ctx.getConfig().getIdentity().get(Hgw2000.IDENTIFIER_ID), -1);
		assert(id != -1);
		return id;
	}

}
