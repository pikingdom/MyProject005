package com.nd.hilauncherdev.plugin.navigation.share;

import android.graphics.drawable.Drawable;

import com.nd.hilauncherdev.plugin.navigation.commonsliding.datamodel.ICommonDataItem;

/**
 * 分享bean<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class SharedItem implements ICommonDataItem {

	private String id;
	private String pkg;
	private String className;
	private String desc;
	private String installName;
	private Drawable icon;
	private boolean isInstalled;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SharedItem other = (SharedItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isFolder() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPosition(int arg0) {
		// TODO Auto-generated method stub

	}

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isInstalled() {
		return isInstalled;
	}

	public void setInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}

	public String getInstallName() {
		return installName;
	}

	public void setInstallName(String installName) {
		this.installName = installName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
