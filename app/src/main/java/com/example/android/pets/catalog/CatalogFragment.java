package com.example.android.pets.catalog;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.pets.PetCursorAdapter;
import com.example.android.pets.R;
import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.editor.EditorActivity;

/**
 * View 层
 */

public class CatalogFragment extends Fragment implements CatalogContract.View {

    private CatalogContract.Presenter mCatalogPresenter;
    private PetCursorAdapter mCursorAdapter;

    public static CatalogFragment newInstance() {
        return new CatalogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCursorAdapter = new PetCursorAdapter(getActivity(), null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.catalog_fragment, container, false);

        // 设置 ListView
        ListView listView = (ListView) root.findViewById(R.id.list);
        listView.setAdapter(mCursorAdapter);

        View emptyView = root.findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCatalogPresenter.openPetDetails(id);
            }
        });

        // 设置 FAB
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCatalogPresenter.addNewPet();
            }
        });

        // 设置选项菜单
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCatalogPresenter.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // 加载菜单选项
        inflater.inflate(R.menu.menu_catalog, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 响应 Insert Dummy Data 选项菜单
            case R.id.action_insert_dummy_data:
                PetDbHelper dbHelper = new PetDbHelper(getActivity());
                mCatalogPresenter.insertDummyData(dbHelper);
                getActivity().getContentResolver().notifyChange(PetContract.PetEntry.CONTENT_URI, null);
                break;
            // 响应 Delete All Pets 选项菜单
            case R.id.action_delete_all_entries:
                ContentResolver contentResolver = getActivity().getContentResolver();
                mCatalogPresenter.deleteAllPets(contentResolver);
                break;
        }
        return true;
    }

    @Override
    public void setPresenter(CatalogContract.Presenter presenter) {
        if (presenter != null) {
            mCatalogPresenter = presenter;
        }
    }

    @Override
    public void showAddPet() {
        Intent intent = new Intent(getActivity(), EditorActivity.class);
        startActivity(intent);
    }

    @Override
    public void showPetDetails(long id) {
        Intent petDetailsIntent = new Intent(getActivity(), EditorActivity.class);

        Uri currentUri = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, id);
        petDetailsIntent.setData(currentUri);
        startActivity(petDetailsIntent);
    }

    @Override
    public void swapData(Cursor data) {
        mCursorAdapter.swapCursor(data);
    }
}
