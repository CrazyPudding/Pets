package com.example.android.pets.catalog;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;

/**
 * Presenter 层
 */

public class CatalogPresenter implements CatalogContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;

    private CatalogContract.View mCatalogView;
    private LoaderManager mLoaderManager;
    private CursorLoader mCursorLoader;

    public CatalogPresenter(LoaderManager loaderManager, CursorLoader cursorLoader, CatalogContract.View catalogView) {
        if (loaderManager != null && cursorLoader != null && catalogView != null) {
            mLoaderManager =loaderManager;
            mCursorLoader = cursorLoader;
            mCatalogView = catalogView;
            mCatalogView.setPresenter(this);
        }
    }

    @Override
    public void start() {
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    @Override
    public void insertDummyData(PetDbHelper dbHelper) {
        if (dbHelper != null) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(PetContract.PetEntry.COLUMN_PET_NAME, "Toto");
            values.put(PetContract.PetEntry.COLUMN_PET_BREED, "America");
            values.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
            values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 12);

            db.insert(PetContract.PetEntry.TABLE_NAME, null, values);
        }
    }

    @Override
    public void deleteAllPets(ContentResolver contentResolver) {
        if (contentResolver != null)
            contentResolver.delete(PetContract.PetEntry.CONTENT_URI, null, null);
    }

    @Override
    public void addNewPet() {
        mCatalogView.showAddPet();
    }

    @Override
    public void openPetDetails(long id) {
        mCatalogView.showPetDetails(id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCatalogView.swapData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCatalogView.swapData(null);
    }
}
