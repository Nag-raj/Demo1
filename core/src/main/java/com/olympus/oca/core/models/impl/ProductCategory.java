package com.olympus.oca.core.models.impl;

public class ProductCategory {
	
	String id;
    String name;
    String url;
    Boolean children;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = removeQuotes(id);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = removeQuotes(name);
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Boolean getChildren() {
		return children;
	}
	public void setChildren(Boolean children) {
		this.children = children;
	}
	
	public String removeQuotes(String catField) {
		return catField.replaceAll("(^\")|(\"$)", "");
	}
	
}
