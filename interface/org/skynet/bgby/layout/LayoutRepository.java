package org.skynet.bgby.layout;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.skynet.bgby.command.management.UpdateResult;

public interface LayoutRepository {

	List<LayoutData> getLayoutByControllerID(String controllerID);

	void setControllerLayout(String controllerID, List<LayoutData> layoutData) throws IOException;

	Map<String, List<LayoutData>> getAll();

	UpdateResult update(Map<String, List<LayoutData>> data, boolean overWriteAll);

}
