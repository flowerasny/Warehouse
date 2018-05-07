package com.kik.warehouse2.main;

import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kik.warehouse2.R;
import com.kik.warehouse2.data.Slab;

import java.util.LinkedList;
import java.util.List;

public class RecyclerViewListAdapter extends RecyclerView.Adapter<RecyclerViewListAdapter.SlabViewHolder> implements Filterable {
    private static final String TAG = "RecyclerViewListAdapter";
    private Context context;
    private List<Slab> slabs = new LinkedList<>();
    private List<Slab> filteredSlabs = new LinkedList<>();

    private FragmentManager fragmentManager;


    public RecyclerViewListAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public SlabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: "+ slabs.size() + " " + filteredSlabs.size());
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_slab, parent, false);
        return new SlabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SlabViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+ slabs.size() + " " + filteredSlabs.size());
        Slab slab = filteredSlabs.get(position);

        holder.code.setText(slab.getCode());
        holder.dimensions.setText(slab.getDimensions());
        holder.deliveryDate.setText(slab.getDate());
        holder.statusDiode.setBackgroundResource(chooseStatusColor(slab.getStatus()));

        holder.itemLayout.setOnClickListener(v -> ButtonsDialog.newInstance(slab.getCode()).show(fragmentManager, "TAG"));
    }

    private int chooseStatusColor(String status) {
        switch (status) {
            case "dostępny":
                return R.color.cardStatusDiodeGreen;
            case "rezerwacja":
                return R.color.cardStatusDiodeYellow;
            case "produkcja":
                return R.color.cardStatusDiodeRed;
            default:
                return R.color.cardStatusDiodeDefault;
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+ slabs.size() + " " + filteredSlabs.size());
        return filteredSlabs.size();
    }

    public void setSlabs(List<Slab> slabs) {
        this.slabs = slabs;

        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        Log.d(TAG, "getFilter: ");
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();

                if (charString.isEmpty()) {
                    filteredSlabs = slabs;
                } else {
                    List<Slab> result = new LinkedList<>();
                    String[] queries = charString.split("\\s");
                    for (Slab slab : slabs) {
                        if (queries.length > 3 && TextUtils.isDigitsOnly(queries[2]) && TextUtils.isDigitsOnly(queries[3])) {
                            if (slab.getColor().toLowerCase().contains(queries[0]) &&
                                    slab.getColor().contains(queries[1]) &&
                                    slab.getWidth1() > Long.parseLong(queries[2]) &&
                                    slab.getHeight1() > Long.parseLong(queries[3])) {
                                result.add(slab);
                            }
                        } else if (slab.getColor().toLowerCase().contains(charString) ||
                                slab.getStatus().contains(charString) ||
                                slab.getCode().matches(charString)) {
                            result.add(slab);
                        }

                    }

                    filteredSlabs = result;
                }
                FilterResults results = new FilterResults();
                results.values = filteredSlabs;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }

    class SlabViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        TextView code, dimensions, deliveryDate, statusDiode;

        public SlabViewHolder(View itemView) {
            super(itemView);

            itemLayout = itemView.findViewById(R.id.item_layout);
            code = itemView.findViewById(R.id.tv_code);
            dimensions = itemView.findViewById(R.id.tv_dimensions);
            deliveryDate = itemView.findViewById(R.id.tv_delivery_date);
            statusDiode = itemView.findViewById(R.id.iv_status_diode);
        }
    }
}
