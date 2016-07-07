package org.skynet.bgby.layout;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.skynet.bgby.command.management.UpdateResult;
import org.skynet.bgby.driverproxy.DPModuleException;
import org.skynet.bgby.driverproxy.DPModuleStatusReporter;
import org.skynet.bgby.driverproxy.DriverProxyService;

public class LayoutManagerImpl implements LayoutManager {
	protected static final String TAG = LayoutManagerImpl.class.getName();
	protected DPModuleStatusReporter startingReporter;
	protected LayoutRepository repository;
	protected DriverProxyService proxy;

	@Override
	public List<LayoutData> getControllerLayout(String controllerID) {
		return repository.getLayoutByControllerID(controllerID);
	}

	@Override
	public void setControllerLayout(String controllerID, List<LayoutData> layoutData) throws IOException {
		repository.setControllerLayout(controllerID, layoutData);
	}

	@Override
	public void start() throws DPModuleException {
		// so far nothing to do when start
	}

	@Override
	public void stop() {
		// so far nothing to do when stop
	}

	@Override
	public void setStartingReporter(DPModuleStatusReporter reporter) {
		startingReporter = reporter;
	}

	@Override
	public void setDriverProxy(DriverProxyService proxy) {
		this.proxy = proxy;
	}

	@Override
	public Map<String, List<LayoutData>> getAllLayout() {
		return repository.getAll();
	}

	@Override
	public UpdateResult update(Map<String, List<LayoutData>> data, boolean overWriteAll) {
		return repository.update(data, overWriteAll);
	}

	
	
}
