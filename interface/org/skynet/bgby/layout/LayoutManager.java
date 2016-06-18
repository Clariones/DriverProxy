package org.skynet.bgby.layout;

import java.io.IOException;
import java.util.List;

import org.skynet.bgby.driverproxy.DPManagedModule;

public interface LayoutManager extends DPManagedModule{

	List<ILayout> getControllerLayout(String controllerID);

	void setControllerLayout(String controllerID, List<ILayout> layoutData) throws IOException;

}
