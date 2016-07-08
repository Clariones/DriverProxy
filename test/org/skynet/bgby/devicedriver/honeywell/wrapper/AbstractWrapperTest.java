package org.skynet.bgby.devicedriver.honeywell.wrapper;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.skynet.bgby.devicedriver.honeywell.ExecutionResult;
import org.skynet.bgby.devicedriver.honeywell.HGW2000Controller;
import org.skynet.bgby.driverproxy.ExecutionContext;
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
	protected ExecutionResult invokeDriver(HGW2000Controller driver, Object apiArgs) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateStatus(ExecutionContext executionContext, IRestResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IRestResponse convertResultToResponse(ExecutionContext executionContext, ExecutionResult result) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IRestResponse updateAndCheckParams(ExecutionContext executionContext, Object apiArgs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object createArgsFromStatus(ExecutionContext executionContext) {
		// TODO Auto-generated method stub
		return null;
	}

	
}