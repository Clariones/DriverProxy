package org.skynet.bgby.driverproxy;

public interface DPManagedModule {
	void start() throws DPModuleException;
	void stop();
	void setStartingReporter(DPModuleStatusReporter reporter);
}
