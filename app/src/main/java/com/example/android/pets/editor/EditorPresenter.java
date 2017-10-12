package com.example.android.pets.editor;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.example.android.pets.data.PetContract;

/**
 * Presenter 层
 */

public class EditorPresenter implements EditorContract.Presenter {

    private EditorContract.View mEditorView;

    public EditorPresenter(EditorContract.View editorView) {
        if (editorView != null) {
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
}
