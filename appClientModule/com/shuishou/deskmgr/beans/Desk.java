package com.shuishou.deskmgr.beans;


public class Desk {
	private int id;
	
	private String name;
	
	private String mergeTo;
	
	public Desk(){}
	
	public Desk(int id, String name){
		this.id = id;
		this.name = name;
	}
	

	public String getMergeTo() {
		return mergeTo;
	}

	public void setMergeTo(String mergeTo) {
		this.mergeTo = mergeTo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Desk [name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Desk other = (Desk) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
