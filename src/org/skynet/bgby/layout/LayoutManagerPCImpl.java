package org.skynet.bgby.layout;

import java.io.File;

public class LayoutManagerPCImpl extends LayoutManagerImpl {
	
	public LayoutManagerPCImpl(){
		repository = new LayoutFileRepository();
	}

	protected File baseFolder;

	public File getBaseFolder() {
		return baseFolder;
	}

	public void setBaseFolder(File baseFolder) {
		this.baseFolder = baseFolder;
		if (this.repository == null) {
			repository = new LayoutFileRepository();
		}
		((LayoutFileRepository) repository).setBaseFolder(baseFolder);
	}

}
