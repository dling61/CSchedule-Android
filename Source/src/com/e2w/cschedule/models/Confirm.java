package com.e2w.cschedule.models;

import com.e2w.cschedule.utils.CommConstant;

public class Confirm {
	private int confirm = CommConstant.CONFIRM_UNKNOWN;
	private int memberId = -1;

	public int getConfirm() {
		return confirm;
	}

	public void setConfirm(int confirm) {
		this.confirm = confirm;
	}

	public Confirm(int memberId,int confirm) {
		super();
		this.confirm = confirm;
		this.memberId = memberId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
}
