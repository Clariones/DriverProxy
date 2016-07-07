package org.skynet.bgby.layout;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.skynet.bgby.command.management.UpdateResult;
import org.skynet.bgby.driverproxy.DPManagedModule;

public interface LayoutManager extends DPManagedModule{

	List<LayoutData> getControllerLayout(String controllerID);

	void setControllerLayout(String controllerID, List<LayoutData> layoutData) throws IOException;

	Map<String, List<LayoutData>> getAllLayout();

	UpdateResult update(Map<String, List<LayoutData>> data, boolean overWriteAll);

}
