package org.skynet.bgby.driverproxy;

public interface DPStatusReporter {

	void reportError(int i, String strTitle, String strMessage);

	void reportStatus(int i, String strTitle, String strMessage);

}
