package org.skynet.bgby.devicestatus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DeviceStatus {

	protected String ID;
	protected String profile;
	protected Map<String, Object> status = new HashMap<>();
//	protected Map<String, Object> identify;

//	public Map<String, Object> getIdentify() {
//		return identify;
//	}
//
//	public void setIdentify(Map<String, Object> identify) {
//		this.identify = identify;
//	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public Map<String, Object> getStatus() {
		return status;
	}

	public void setStatus(Map<String, Object> status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DeviceStatus)) {
			return false;
		}
		if (this == obj) {
			return true;
		}

		DeviceStatus other = (DeviceStatus) obj;
		if (ID == null || other.ID == null || profile == null || other.ID == null || status == null
				|| other.status == null) {
			// any comparetion between half-backed instance would be false
			return false;
		}
		if (!ID.equals(other.ID) || !profile.equals(other.profile)){
			return false;
		}
		if (status.size() != other.status.size()){
			return false;
		}
		
		Iterator<Entry<String, Object>> it = status.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Object> ent = it.next();
			String key = ent.getKey();
			Object value = ent.getValue();
			Object otherValue = other.status.get(key);
			if (value == otherValue){
				continue;
			}
			if (value != null){
				if (value.equals(otherValue)){
					continue;
				}
			}else{
				return false;
			}
		}
		return true;
	}

	public void setStatus(String key, Object value) {
		if (status == null){
			status = new HashMap<>();
		}
		status.put(key, value);
	}

}
