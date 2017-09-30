package com.shuishou.deskmgr.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.shuishou.deskmgr.ConstantValue;


public class Indent {

	private int id;
	
	private String deskName;
	
	private Date startTime;
	
	private int customerAmount;
	
	private Date endTime;
	
	private List<IndentDetail> items;
	
	private double totalPrice;
	
	private double paidPrice;//实际付款金额
	
	private byte payWay;//付款方式
	
	private byte status = ConstantValue.INDENT_STATUS_OPEN;

	//订单序号, 每日从1开始,
	private int dailySequence = -1;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCustomerAmount() {
		return customerAmount;
	}

	public void setCustomerAmount(int customerAmount) {
		this.customerAmount = customerAmount;
	}

	public byte getPayWay() {
		return payWay;
	}

	public void setPayWay(byte payWay) {
		this.payWay = payWay;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date time) {
		this.startTime = time;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public double getPaidPrice() {
		return paidPrice;
	}

	public void setPaidPrice(double paidPrice) {
		this.paidPrice = paidPrice;
	}

	public List<IndentDetail> getItems() {
		return items;
	}

	public void setItems(List<IndentDetail> items) {
		this.items = items;
	}
	
	public void addItem(IndentDetail detail){
		if (items == null)
			items = new ArrayList<IndentDetail>();
		items.add(detail);
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	
	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Order [desk=" + deskName + ", totalPrice=" + totalPrice + "]";
	}

	
	public String getDeskName() {
		return deskName;
	}

	public void setDeskName(String deskName) {
		this.deskName = deskName;
	}

	public int getDailySequence() {
		return dailySequence;
	}

	public void setDailySequence(int dailySequence) {
		this.dailySequence = dailySequence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Indent other = (Indent) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
