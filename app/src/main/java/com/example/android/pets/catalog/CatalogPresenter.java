package com.example.android.pets.catalog;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;

/**
 * Presenter 层
 */

public class CatalogPresenter implements CatalogContract.Presenter {

    private CatalogContract.View mCatalogView;

    public CatalogPresenter(CatalogContract.View catalogView) {
        if (catalogView != null) {
            mCatalogView = catalogView;
            mCatalogView.setPresenter(this);
        }
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
}
