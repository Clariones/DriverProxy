package org.skynet.bgby.layout;

import java.io.IOException;
import java.util.List;

public interface LayoutRepository {

	List<ILayout> getLayoutByControllerID(String controllerID);

	void setControllerLayout(String controllerID, List<ILayout> layoutData) throws IOException;

}
