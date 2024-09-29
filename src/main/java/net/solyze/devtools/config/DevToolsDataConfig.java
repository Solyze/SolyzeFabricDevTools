package net.solyze.devtools.config;

import net.solyze.devtools.DevTools;

@ConfigInfo(name = DevTools.MOD_ID + "_data")
public class DevToolsDataConfig {

	private boolean
			showItemComponents = false,
			fullbrightEnabled = false,
			hudEnabled = true;

	public boolean isShowItemComponents() {
		return showItemComponents;
	}

	public void setShowItemComponents(boolean showItemComponents) {
		this.showItemComponents = showItemComponents;
	}

	public boolean isFullbrightEnabled() {
		return fullbrightEnabled;
	}

	public void setFullbrightEnabled(boolean fullbrightEnabled) {
		this.fullbrightEnabled = fullbrightEnabled;
	}

	public boolean isHudEnabled() {
		return hudEnabled;
	}

	public void setHudEnabled(boolean hudEnabled) {
		this.hudEnabled = hudEnabled;
	}
}
