package com.olympus.oca.core.models;

import java.util.List;
import java.util.Map;

import com.olympus.oca.core.models.impl.ProductCategory;

public interface CategoryListModel {

	List<ProductCategory> getCategoriesL1();
	
	Map<String, List<ProductCategory>> getCategoriesL2();
	
	Map<String, List<ProductCategory>> getCategoriesL3();
}
