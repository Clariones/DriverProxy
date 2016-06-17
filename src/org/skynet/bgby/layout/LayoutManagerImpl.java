package org.skynet.bgby.layout;

import java.io.IOException;
import java.util.List;

public class LayoutManagerImpl implements LayoutManager {
	private static final String TAG = LayoutManagerImpl.class.getName();
	protected LayoutRepository repository;
	
	@Override
	public List<ILayout> getControllerLayout(String controllerID) {
		return repository.getLayoutByControllerID(controllerID);
	}
	@Override
	public void setControllerLayout(String controllerID, List<ILayout> layoutData) throws IOException {
		repository.setControllerLayout(controllerID, layoutData);
	}


}
