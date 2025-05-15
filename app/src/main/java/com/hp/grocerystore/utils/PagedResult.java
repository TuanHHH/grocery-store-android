package com.hp.grocerystore.utils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PagedResult<T> {

    private Meta meta;

    private List<T> result;

    public Meta getMeta() { return meta; }
    public List<T> getResult() { return result; }

    public static class Meta {
        private int page;
        private int pageSize;
        private int pages;
        private int total;

        public int getPage() { return page; }
        public int getPageSize() { return pageSize; }
        public int getPages() { return pages; }
        public int getTotal() { return total; }
    }
}
