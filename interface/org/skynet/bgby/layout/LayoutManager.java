package org.skynet.bgby.layout;

import java.io.IOException;
import java.util.List;

public interface LayoutManager {

	List<ILayout> getControllerLayout(String controllerID);

	void setControllerLayout(String controllerID, List<ILayout> layoutData) throws IOException;
}
