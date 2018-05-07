package com.kik.warehouse2.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.v4.app.NavUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SlabsRepository {

    private static final String TAG = "SlabsRepository";

    private MutableLiveData<List<Slab>> slabs = new MutableLiveData<>();
    private MutableLiveData<List<String>> colors = new MutableLiveData<>();
    private HashMap<String, String> colorTypes = new HashMap<>();


    public SlabsRepository() {
        setSlabsToSlabList();
        setColorList();
    }

    private void setSlabsToSlabList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("slabs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Slab> slabsInDatabase = new LinkedList<>();
                for (DataSnapshot slab : dataSnapshot.getChildren()) {
                    slabsInDatabase.add(slab.getValue(Slab.class));
                }
                setSlabListToLiveData(slabsInDatabase);
                Log.d(TAG, "onDataChange: " + slabsInDatabase.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
/*
    private List<Slab> orderProductionFirstReservedSecond(List<Slab> slabsInDatabase) {
        Collections.sort(slabsInDatabase, (s1, s2) -> {
            String s1status = s1.getStatus();
            String s2status = s2.getStatus();
            if (s2status.matches("production")) return 1;
            else if (s2status.matches("reserved") && s1status.matches("onstock")) return 1;
            else return -1;
        });
        return slabsInDatabase;
    }
*/
    private void setSlabListToLiveData(List<Slab> slabs) {
        this.slabs.setValue(slabs);
    }

    private void setColorList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("colortypes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> types = new HashMap<>();
                List<String> slabColors = new LinkedList<>();
                for (DataSnapshot color : dataSnapshot.getChildren()) {
                    slabColors.add(color.getKey());
                    types.put(color.getKey(),color.getValue().toString());
                }
                setColorListToLiveData(slabColors);
                setTypesHashMap(types);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void setTypesHashMap(HashMap<String, String> types) {
        this.colorTypes = types;
    }

    private void setColorListToLiveData(List<String> slabColors) {
        colors.setValue(slabColors);
    }

    public LiveData getColors() {
        return colors;
    }

    public LiveData<List<Slab>> getSlabs() {
        return slabs;
    }


    public void sendNewSlabToDatabase(SlabWrite newSlab) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("slabs");
        databaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                int lastId = -1;
                for (MutableData slab : mutableData.getChildren()) {
                    if (lastId == -1) lastId = 0;
                    String slabColor = slab.child("color").getValue().toString();
                    int slabId = Integer.valueOf(slab.child("code").getValue().toString().substring(12));
                    if (slabColor.matches(newSlab.getColor()) && slabId > lastId) {
                        lastId = slabId;
                    }
                }
                if (lastId > -1){
                    newSlab.generateCode(getTypeCode(newSlab.getColor()), lastId);
                    mutableData.child(newSlab.getCode()).setValue(newSlab);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    private String getTypeCode(String color) {
        return colorTypes.isEmpty() ? getTypeCode(color) : colorTypes.get(color).substring(0,1);
    }

    public static void deleteSlab(String code){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("slabs").child(code);
        databaseReference.removeValue();
    }

    public static void reserveSlab(String code){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("slabs").child(code).child("status");
        databaseReference.setValue("rezerwacja");
    }

    public static void unreserveSlab(String code){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("slabs").child(code).child("status");
        databaseReference.setValue("dostępny");
    }

    public static void productionSlab(String code) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("slabs").child(code).child("status");
        databaseReference.setValue("produkcja");
    }
}
