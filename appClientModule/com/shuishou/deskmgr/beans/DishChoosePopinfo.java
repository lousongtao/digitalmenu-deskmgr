package com.shuishou.deskmgr.beans;

import java.io.Serializable;

public class DishChoosePopinfo implements Serializable{
	private int id;
	
	private String popInfoCN;
	private String popInfoEN;
	private Dish dish;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getPopInfoCN() {
		return popInfoCN;
	}

	public void setPopInfoCN(String popInfoCN) {
		this.popInfoCN = popInfoCN;
	}

	public String getPopInfoEN() {
		return popInfoEN;
	}

	public void setPopInfoEN(String popInfoEN) {
		this.popInfoEN = popInfoEN;
	}

	public Dish getDish() {
		return dish;
	}

	public void setDish(Dish dish) {
		this.dish = dish;
	}

	@Override
	public String toString() {
		return "DishChoosePopinfo [id=" + id + ", dish=" + dish.getChineseName() + ", popInfoCN=" + popInfoCN + "]";
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
		DishChoosePopinfo other = (DishChoosePopinfo) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
