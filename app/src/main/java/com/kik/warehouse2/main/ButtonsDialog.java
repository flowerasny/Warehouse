package com.kik.warehouse2.main;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.kik.warehouse2.R;
import com.kik.warehouse2.data.Slab;
import com.kik.warehouse2.data.SlabsRepository;

public class ButtonsDialog extends DialogFragment {

    String code;

    public int getContentView() {
        return R.layout.buttons_dialog;
    }

    public static ButtonsDialog newInstance(String code) {
        ButtonsDialog detailsDialog = new ButtonsDialog();

        Bundle args = new Bundle();
        args.putString("SLAB", code);
        detailsDialog.setArguments(args);

        return detailsDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        code = getArguments().getString("SLAB");
        Window window = getDialog().getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
        View view = inflater.inflate(getContentView(), container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ImageButton btnReserve = view.findViewById(R.id.btn_reserve);
        btnReserve.setOnClickListener(v -> {
            SlabsRepository.reserveSlab(code);
            this.dismiss();
        });
        ImageButton btnDelete = view.findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(v -> {
            SlabsRepository.deleteSlab(code);
            this.dismiss();
        });
        ImageButton btnProduction = view.findViewById(R.id.btn_production);
        btnProduction.setOnClickListener(v -> {
            SlabsRepository.productionSlab(code);
            this.dismiss();
        });
        ImageButton btnClose = view.findViewById(R.id.btn_close_dialog);
        btnClose.setOnClickListener(v -> this.dismiss());

        super.onViewCreated(view, savedInstanceState);
    }
}
