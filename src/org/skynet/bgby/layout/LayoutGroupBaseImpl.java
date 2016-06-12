package org.skynet.bgby.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class LayoutGroupBaseImpl extends LayoutBaseImpl implements ILayoutGroup{
	protected List<ILayout> layoutContent;

	@Override
	public List<ILayout> getLayoutContent() {
		return layoutContent;
	}

	@Override
	public void setLayoutContent(List<ILayout> layoutContent) {
		this.layoutContent = layoutContent;
	}
	
	@Override
	public void init(ILayout layoutData) throws LayoutException {
		super.init(layoutData);
		if (layoutData instanceof ILayoutGroup){
			ILayoutGroup grpData = (ILayoutGroup) layoutData;
			List<ILayout> children = grpData.getLayoutContent();
			if (children == null || children.isEmpty()){
				return;
			}
			preInitChildren(grpData);
			initChildren(grpData);
			postInitChildren(grpData);
		}
	}

	protected void postInitChildren(ILayoutGroup grpData) {
		// by default, nothing to do
	}

	protected void initChildren(ILayoutGroup grpData) throws LayoutException {
		
		List<ILayout> list = grpData.getLayoutContent();
		assert(list != null && !list.isEmpty());
		Iterator<ILayout> it = list.iterator();
		layoutContent = new ArrayList<ILayout>();
		while(it.hasNext()){
			ILayout data = it.next();
			ILayout child = LayoutUtils.createLayoutInstance(data);
			child.setParams(data.getParams());
			preInitChildLayoutData(child, data);
			child.init(data);
			layoutContent.add(child);
			postInitChildLayoutData(child, data);
		}
		
	}

	

	protected void preInitChildren(ILayoutGroup grpData) {
		// by default, nothing to do
	}

	protected abstract void preInitChildLayoutData(ILayout child, ILayout data);

	protected abstract void postInitChildLayoutData(ILayout child, ILayout data);
}
