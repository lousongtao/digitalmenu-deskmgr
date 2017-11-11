package com.shuishou.deskmgr.beans;

public class IndentDetail {

	private int id;
	
	private Indent indent;
	
	private int dishId;
	
	private int amount;
	
	private double dishPrice;//����dish�۸�, ������amount
	
	private String dishChineseName;
	
	private String dishEnglishName;
	
	private String additionalRequirements;

	private double weight;
	
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Indent getIndent() {
		return indent;
	}

	public void setIndent(Indent indent) {
		this.indent = indent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getDishId() {
		return dishId;
	}

	public void setDishId(int dishId) {
		this.dishId = dishId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public double getDishPrice() {
		return dishPrice;
	}

	public void setDishPrice(double dishPrice) {
		this.dishPrice = dishPrice;
	}

	public String getDishChineseName() {
		return dishChineseName;
	}

	public void setDishChineseName(String dishChineseName) {
		this.dishChineseName = dishChineseName;
	}

	public String getDishEnglishName() {
		return dishEnglishName;
	}

	public void setDishEnglishName(String dishEnglishName) {
		this.dishEnglishName = dishEnglishName;
	}

	
	public String getAdditionalRequirements() {
		return additionalRequirements;
	}

	public void setAdditionalRequirements(String additionalRequirements) {
		this.additionalRequirements = additionalRequirements;
	}
	
	public IndentDetail copy(){
		IndentDetail detail = new IndentDetail();
		detail.additionalRequirements = this.additionalRequirements;
		detail.amount = this.amount;
		detail.dishChineseName = this.dishChineseName;
		detail.dishEnglishName = this.dishEnglishName;
		detail.dishId = this.dishId;
		detail.dishPrice = this.dishPrice;
		detail.id = this.id;
		detail.indent = this.indent;
		detail.weight = this.weight;
		return detail;
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
		IndentDetail other = (IndentDetail) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderDetail [amount=" + amount + ", dishChineseName=" + dishChineseName + "]";
	}
}
