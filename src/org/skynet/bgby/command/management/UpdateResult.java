package org.skynet.bgby.command.management;

public class UpdateResult {
	protected int errorCode = 0;
	protected String errorTitle = "success";
	protected String errorDetail = "no error";
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorTitle() {
		return errorTitle;
	}
	public void setErrorTitle(String errorTitle) {
		this.errorTitle = errorTitle;
	}
	public String getErrorDetail() {
		return errorDetail;
	}
	public void setErrorDetail(String errorDetail) {
		this.errorDetail = errorDetail;
	}
	protected int added = 0;
	protected int updated = 0;
	protected int deleted = 0;
	protected int invalid = 0;
	protected int received = 0;
	protected int ignored = 0;
	protected int handled = 0;
	
	public int getAdded() {
		return added;
	}
	public void setAdded(int added) {
		this.added = added;
	}
	public int getUpdated() {
		return updated;
	}
	public void setUpdated(int updated) {
		this.updated = updated;
	}
	public int getDeleted() {
		return deleted;
	}
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}
	public int getInvalid() {
		return invalid;
	}
	public void setInvalid(int invalid) {
		this.invalid = invalid;
	}
	public int getReceived() {
		return received;
	}
	public void setReceived(int received) {
		this.received = received;
	}
	public int getIgnored() {
		return ignored;
	}
	public void setIgnored(int ignored) {
		this.ignored = ignored;
	}
	public int getHandled() {
		return handled;
	}
	public void setHandled(int handled) {
		this.handled = handled;
	}
	
	public int incAdded() { return ++added;}
	public int incUpdated() { return ++updated;}
	public int incDeleted() { return ++deleted;}
	public int incInvalid() { return ++invalid;}
	public int incReceived() { return ++received;}
	public int incIgnored() { return ++ignored;}
	public int incHandled() { return ++handled;}
}
