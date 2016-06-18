package org.skynet.bgby.driverproxy;

public interface DPModuleStatusReporter {

	void reportStatus(int id, String title, String detail);

	void reportError(int id, String title, String detail);

}
