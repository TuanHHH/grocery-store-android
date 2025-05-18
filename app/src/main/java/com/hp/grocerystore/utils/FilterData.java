package com.hp.grocerystore.utils;

public class FilterData {
    private long selectedCategoryId;
    private String selectedCategorySlug;
    private String selectedSort;
    private int minPrice;
    private int maxPrice;
    private String searchText;

    public FilterData(String selectedCategorySlug) {
        this.selectedCategorySlug = selectedCategorySlug;
    }

    public FilterData(long selectedCategoryId, String selectedCategorySlug, String selectedSort, int minPrice, int maxPrice, String searchText) {
        this.selectedCategoryId = selectedCategoryId;
        this.selectedCategorySlug = selectedCategorySlug;
        this.selectedSort = selectedSort;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.searchText = searchText;
    }

    public long getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void setSelectedCategoryId(long selectedCategoryId) {
        this.selectedCategoryId = selectedCategoryId;
    }

    public String getSelectedCategorySlug() {
        return selectedCategorySlug;
    }

    public void setSelectedCategorySlug(String selectedCategorySlug) {
        this.selectedCategorySlug = selectedCategorySlug;
    }

    public String getSelectedSort() {
        return selectedSort;
    }

    public void setSelectedSort(String selectedSort) {
        this.selectedSort = selectedSort;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}

