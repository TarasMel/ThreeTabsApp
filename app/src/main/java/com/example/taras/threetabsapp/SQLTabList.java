package com.example.taras.threetabsapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class SQLTabList extends Fragment {

    OnFragmentInteractionListener mListener;

    public SQLTabList() {}

    Button btnAddToList;
    ListView listView;
    ArrayList<String> listItem;
    ArrayAdapter<String> adapter;
    AutoCompleteTextView autoCompleteTextView;
    DatabaseHelper mDatabaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sql_tab_list_layout, container, false);
        mDatabaseHelper = new DatabaseHelper(getContext());
        listView = (ListView) view.findViewById(R.id.listView_GeoTab_ID);
        btnAddToList = (Button) view.findViewById(R.id.btn_AddToList_ID);
        final EditText editTextAdd = (EditText) view.findViewById(R.id.edit_SearchFieldInBase_ID);
        listItem = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, listItem);
        listView.setAdapter(adapter);
        btnAddToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEntry = editTextAdd.getText().toString();
                if (editTextAdd.length() != 0) {
                    listItem.add(0, editTextAdd.getText().toString());
                    AddData(newEntry);
                    adapter.notifyDataSetChanged();
                    editTextAdd.setText("");
                } else {
                    toastMessage("You must put something in the text field!");
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(view.getContext().getApplicationContext(),listItem.get(position),Toast.LENGTH_LONG);
            }
        });
        registerForContextMenu(listView);

        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autoCTW_ID);
        autoCompleteTextView.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, listItem));
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        InputMethodManager inm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE) ;
        inm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        mListener = null;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater menuInflater = this.getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_file, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo obj = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.delete:
                listItem.remove(obj.position);
                mDatabaseHelper.deleteName(obj.position,obj.toString());
                toastMessage("removed from database");
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void AddData(String newEntry) {
        boolean insertData = mDatabaseHelper.addData(newEntry);
        if (insertData) {
            toastMessage("Data Successfully Inserted!");
        } else {
            toastMessage("Something went wrong");
        }
    }

    private void toastMessage(String message){
        Toast.makeText(getContext(),message, Toast.LENGTH_SHORT).show();
    }

    interface OnFragmentInteractionListener {
    }
}