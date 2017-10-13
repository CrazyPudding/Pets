package com.example.android.pets.editor;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.android.pets.data.PetContract;

/**
 * Presenter 层
 */

public class EditorPresenter implements EditorContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDIT_LOADER = 1;

    private EditorContract.View mEditorView;
    private LoaderManager mLoaderManager;
    private CursorLoader mCursorLoader;

    public EditorPresenter(EditorContract.View editorView) {
        if (editorView != null) {
            mEditorView = editorView;
            mEditorView.setPresenter(this);
        }
    }

    public EditorPresenter(LoaderManager loaderManager, CursorLoader cursorLoader, EditorContract.View editorView) {
        if (loaderManager != null && cursorLoader != null &&editorView != null) {
            mLoaderManager = loaderManager;
            mCursorLoader = cursorLoader;
            mEditorView = editorView;
            mEditorView.setPresenter(this);
        }
    }

    private int mGender = PetContract.PetEntry.GENDER_UNKNOWN;

    @Override
    public void setGender(int gender) {
        mGender = gender;
    }

    @Override
    public int getGender() {
        return mGender;
    }

    @Override
    public void start() {
        mLoaderManager.initLoader(EDIT_LOADER, null, this);
    }

    @Override
    public void loadPet(Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT);

            String currentName = data.getString(nameColumnIndex);
            String currentBreed = data.getString(breedColumnIndex);
            int currentGender = data.getInt(genderColumnIndex);
            int currentWeight = data.getInt(weightColumnIndex);

            mEditorView.showPetDetails(currentName, currentBreed, currentWeight, currentGender);
        }
    }

    @Override
    public void savePet(ContentResolver contentResolver, Uri currentUri, String nameString, String breedString, String weightString) {
        if (currentUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedString) && TextUtils.isEmpty(weightString) && getGender() == PetContract.PetEntry.GENDER_UNKNOWN) {
            mEditorView.showSavePetFailed();
            return;
        }

        ContentValues values = new ContentValues();
        int weight = 0;
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, nameString);
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, breedString);
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, getGender());
        if (!TextUtils.isEmpty(weightString)) {
            weight = Integer.parseInt(weightString);
        }
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, weight);

        if (currentUri == null) {
            Uri uri = contentResolver.insert(PetContract.PetEntry.CONTENT_URI, values);

            if (uri == null) {
                mEditorView.showSavePetFailed();
            } else {
                mEditorView.showSavePetSuccess();
            }
        } else {
            int rowsUpdate = contentResolver.update(currentUri, values, null, null);
            if (rowsUpdate == 0) {
                mEditorView.showUpdateFailed();
            } else {
                mEditorView.showUpdateSuccess();
            }
        }
    }

    @Override
    public void deletePet(ContentResolver contentResolver, Uri currentUri) {
        int rowDelete = contentResolver.delete(currentUri, null, null);
        if (rowDelete == 0) {
            mEditorView.showDeleteFailed();
        } else {
            mEditorView.showDeleteSuccess();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case EDIT_LOADER:
                return mCursorLoader;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loadPet(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEditorView.showPetDetails("", "", 0, 0);
    }
}
