package org.skynet.bgby.devicedriver.honeywell.wrapper;

import org.skynet.bgby.devicedriver.honeywell.HGW2000Controller;
import org.skynet.bgby.driverproxy.ExecutionContext;
import org.skynet.bgby.protocol.IRestResponse;

public interface HGWDriverWrapper {

	IRestResponse execute(HGW2000Controller driver, ExecutionContext executionContext);

}
