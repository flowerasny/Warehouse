package com.kik.warehouse2.main;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.util.Log;

import com.kik.warehouse2.data.Slab;

import java.util.List;

import static android.content.ContentValues.TAG;

public class SlabsDiffUtillCallback extends DiffUtil.Callback {
    List<Slab> oldSlabs;
    List<Slab> newSlabs;

    public SlabsDiffUtillCallback(List<Slab> oldSlabs, List<Slab> newSlabs) {
        this.oldSlabs = oldSlabs;
        this.newSlabs = newSlabs;
    }

    @Override
    public int getOldListSize() {
        return oldSlabs.size();
    }

    @Override
    public int getNewListSize() {
        return newSlabs.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Log.d(TAG, "areItemsTheSame: ");
        return oldSlabs.get(oldItemPosition).getCode().matches(newSlabs.get(newItemPosition).getCode());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Log.d(TAG, "areContentsTheSame: ");
        boolean result = true;
     
        if (oldSlabs.get(oldItemPosition).getStatus().matches(newSlabs.get(newItemPosition).getStatus())) result = false;
        return result;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
