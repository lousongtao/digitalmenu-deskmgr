package com.shuishou.deskmgr.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class DishConfigGroup implements Serializable{

	private int id;
	
	private String firstLanguageName;
	
	private String secondLanguageName;
	
	private int sequence;
	
	private Dish dish;
	
	/**
	 * 必须选择的数量
	 */
	private int requiredQuantity;

	private ArrayList<DishConfig> dishConfigs;
	
	private boolean allowDuplicate = false;
	
	private String uniqueName;
	
	
	public boolean isAllowDuplicate() {
		return allowDuplicate;
	}

	public void setAllowDuplicate(boolean allowDuplicate) {
		this.allowDuplicate = allowDuplicate;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public ArrayList<DishConfig> getDishConfigs() {
		return dishConfigs;
	}

	public void setDishConfigs(ArrayList<DishConfig> dishConfigs) {
		this.dishConfigs = dishConfigs;
	}

	public void addDishConfig(DishConfig dc){
		if (dishConfigs == null){
			dishConfigs = new ArrayList<>();
		}
		dishConfigs.add(dc);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Dish getDish() {
		return dish;
	}

	public void setDish(Dish dish) {
		this.dish = dish;
	}

	public int getRequiredQuantity() {
		return requiredQuantity;
	}

	public void setRequiredQuantity(int requiredQuantity) {
		this.requiredQuantity = requiredQuantity;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getFirstLanguageName() {
		return firstLanguageName;
	}

	public void setFirstLanguageName(String firstLanguageName) {
		this.firstLanguageName = firstLanguageName;
	}

	public String getSecondLanguageName() {
		return secondLanguageName;
	}

	public void setSecondLanguageName(String secondLanguageName) {
		this.secondLanguageName = secondLanguageName;
	}

	@Override
	public String toString() {
		return "DishConfigGroup [firstLanguageName=" + firstLanguageName + ", secondLanguageName=" + secondLanguageName + "]";
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
		DishConfigGroup other = (DishConfigGroup) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
