package com.hp.grocerystore.model.base;

import java.util.List;

public class PaginationResponse<T> {
    private Meta meta;
    private List<T> result;
    public Meta getMeta() { return meta; }
    public void setMeta(Meta meta) { this.meta = meta; }

    public List<T> getResult() { return result; }
    public void setResult(List<T> result) { this.result = result; }
}
