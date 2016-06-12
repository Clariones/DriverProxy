package org.skynet.bgby.layout;

import java.util.List;

public interface ILayoutGroup extends ILayout{

	void setLayoutContent(List<ILayout> layoutContent);

	List<ILayout> getLayoutContent();

}
