package pkg;

import org.skynet.bgby.driverproxy.DPStatusReporter;

public class TestReporter implements DPStatusReporter {

	@Override
	public void reportError(int i, String strTitle, String strMessage) {
		System.err.println("Step " + i + ": " + strTitle);
		if (strMessage != null)
			System.err.println("\t" + strMessage);
	}

	@Override
	public void reportStatus(int i, String strMsg, String detail) {
		System.out.println("Step " + i + ": " + strMsg);
		if (detail != null) {
			System.out.println("\t" + detail);
		}
	}

}
