package com.kik.warehouse2.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.kik.warehouse2.data.Slab;
import com.kik.warehouse2.data.SlabWrite;
import com.kik.warehouse2.data.SlabsRepository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SlabViewModel extends ViewModel{

    private static final String TAG = "SlabViewModel";

    private MutableLiveData<String> filter = new MutableLiveData<>();
    private SlabsRepository repository = new SlabsRepository();

    public SlabViewModel(){
        setInitialList();
    }

    public void addNewSlab(SlabWrite newSlab){
        repository.sendNewSlabToDatabase(newSlab);
    }

    private void setInitialList() {
        this.setFilter("Alabastro 20");
    }

    public void setFilter(String value) {
        filter.setValue(value);
    }


    public LiveData<List<Slab>> getSlabToDisplay() {
        return Transformations.switchMap(filter, value -> repository.getSlabs());
    }

    public LiveData<List<String>> getSlabColors(){
        return Transformations.switchMap(filter, value -> repository.getColors());
    }
}
