package com.kik.warehouse2.main;

import android.app.FragmentManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kik.warehouse2.R;
import com.kik.warehouse2.data.Slab;
import com.kik.warehouse2.data.SlabWrite;
import com.kik.warehouse2.viewmodel.SlabViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private RecyclerViewListAdapter adapter;
    private SlabViewModel slabViewModel;
    private CardView cvAddNewSlab;
    private String searchPhrase = "";

    private Button btnOpenAddCard;
    private AutoCompleteTextView actv_slabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        configureRecyclerView();

        observeSlabViewModel();


        btnOpenAddCard = findViewById(R.id.btn_open_add_card);

        btnOpenAddCard.setOnClickListener(v -> {
            btnOpenAddCard.setVisibility(View.GONE);
            openAddNewSlabCard();
        });
    }

    private void observeSlabViewModel() {
        slabViewModel = ViewModelProviders.of(this).get(SlabViewModel.class);
        slabViewModel.getSlabToDisplay().observe(this, this::setSlabsToAdapter);
        slabViewModel.getSlabColors().observe(this, this::configureAutoCompleteTextView);
    }

    private void setSlabsToAdapter(List<Slab> slabList) {
        //adapter.setSlabs(slabList);
        //adapter.getFilter().filter(searchPhrase);
    }

    /* RECYCLER VIEW */

    private void configureRecyclerView() {
        Log.d(TAG, "configureRecyclerView: "+searchPhrase);
        recyclerView = findViewById(R.id.rv_slab_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(initializeAdapter());
    }

    private RecyclerViewListAdapter initializeAdapter() {
        Log.d(TAG, "initializeAdapter: "+searchPhrase);
        adapter = new RecyclerViewListAdapter(this, this.getFragmentManager());
        if (searchPhrase.length() > 0){
            adapter.getFilter().filter(searchPhrase);
        }
        return adapter;
    }

    private void configureAutoCompleteTextView(List<String> strings) {
        actv_slabs = findViewById(R.id.actv_name);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                strings
        );
        actv_slabs.setAdapter(adapter);
    }


    private void openAddNewSlabCard() {
        cvAddNewSlab = findViewById(R.id.cv_add_new_slab);
        Spinner sSurface = findViewById(R.id.s_surface);
        Button btnCloseCard = findViewById(R.id.btn_close_card);
        Button btnAddSlab = findViewById(R.id.btn_add_slab);
        AutoCompleteTextView actvColor= findViewById(R.id.actv_name);
        EditText etWidth1 = findViewById(R.id.et_width1);
        EditText etHeight1 = findViewById(R.id.et_height1);
        EditText etWidth2 = findViewById(R.id.et_width2);
        EditText etHeight2 = findViewById(R.id.et_height2);


        cvAddNewSlab.setVisibility(View.VISIBLE);
        configureSurfaceSpinner(sSurface);
        btnAddSlab.setOnClickListener(v -> {

            if (actvColor.getText().length()>0 && etHeight1.getText().length()>0 && etWidth1.getText().length()>0){
                for (int i=0; i<200; i++) {
                    slabViewModel.addNewSlab(new SlabWrite(
                            actvColor.getText().toString(),
                            Long.parseLong(etWidth1.getText().toString()),
                            Long.parseLong(etHeight1.getText().toString()),
                            etWidth2.getText().length() > 0 ? Long.parseLong(etWidth2.getText().toString()) : 0L,
                            etHeight2.getText().length() > 0 ? Long.parseLong(etHeight2.getText().toString()) : 0L,
                            sSurface.getSelectedItem().toString()));
                }
                Toast.makeText(this, "Dodano nowy slab!", Toast.LENGTH_LONG).show();
                hideKeyboard();
            }else {
                Toast.makeText(this,"Puste pole!", Toast.LENGTH_LONG).show();
            }
        });
        btnCloseCard.setOnClickListener(v -> closeCard());
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.findViewById(android.R.id.content).getWindowToken(), 0);
    }

    private void configureSurfaceSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.surfaces_pl, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }


    private void closeCard() {
        recyclerView.setEnabled(true);
        cvAddNewSlab.setVisibility(View.GONE);
        btnOpenAddCard.setVisibility(View.VISIBLE);
    }

/* TOOLBAR MENU */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchPhrase = newText;
                Log.d(TAG, "onQueryTextChange: "+searchPhrase);
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_scan_barcode){
            openBarcodeReader();
        }
        return true;
    }

/* BARCODE READER */

    private void openBarcodeReader() {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("SKANUJ KOD");
        integrator.setCameraId(0);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                adapter.getFilter().filter(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("SEARCH_LINE", searchPhrase);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        adapter.getFilter().filter(savedInstanceState.getString("SEARCH_LINE"));
        super.onRestoreInstanceState(savedInstanceState);

    }
}

