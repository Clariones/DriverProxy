package org.skynet.bgby.layout;

import java.io.IOException;
import java.util.List;

import org.skynet.bgby.driverproxy.DPModuleException;
import org.skynet.bgby.driverproxy.DPModuleStatusReporter;

public class LayoutManagerImpl implements LayoutManager {
	private static final String TAG = LayoutManagerImpl.class.getName();
	protected DPModuleStatusReporter startingReporter;
	protected LayoutRepository repository;

	@Override
	public List<ILayout> getControllerLayout(String controllerID) {
		return repository.getLayoutByControllerID(controllerID);
	}

	@Override
	public void setControllerLayout(String controllerID, List<ILayout> layoutData) throws IOException {
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

}
