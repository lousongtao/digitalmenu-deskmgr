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
	
	private double paidPrice;//ʵ�ʸ�����
	
	private String payWay;//���ʽ
	
	private byte status = ConstantValue.INDENT_STATUS_OPEN;

	//�������, ÿ�մ�1��ʼ,
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

	public String getPayWay() {
		return payWay;
	}

	public void setPayWay(String payWay) {
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

	public String getFormatPaidPrice(){
		return String.format("%.2f", paidPrice);
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
	
	public String getFormatTotalPrice(){
		return String.format("%.2f", totalPrice);
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
	
	public Indent copy(){
		Indent newi = new Indent();
		newi.customerAmount = this.customerAmount;
		newi.dailySequence = this.dailySequence;
		newi.deskName = this.deskName;
		newi.endTime = this.endTime;
		newi.id = this.id;
		newi.paidPrice = this.paidPrice;
		newi.payWay = this.payWay;
		newi.startTime = this.startTime;
		newi.status = this.status;
		newi.totalPrice = this.totalPrice;
		ArrayList<IndentDetail> items = new ArrayList<>();
		for(IndentDetail d : this.items){
			IndentDetail dc = d.copy();
			dc.setIndent(newi);
			items.add(dc);
		}
		newi.items = items;
		return newi;
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
