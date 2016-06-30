package org.skynet.bgby.driverproxy;

public interface DPManagedModule {
	void start() throws DPModuleException;
	void stop();
	void setDriverProxy(DriverProxyService proxy);
	void setStartingReporter(DPModuleStatusReporter reporter);
}
