package com.hp.grocerystore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hp.grocerystore.utils.FilterData;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<FilterData> filterData = new MutableLiveData<>();

    public LiveData<FilterData> getFilterData() {
        return filterData;
    }

    public void setFilterData(FilterData data) {
        filterData.setValue(data);
    }

}
