package com.example.android.pets.editor;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.pets.BaseView;

/**
 * View 和 Presenter 的契约类，列出两者的功能
 */

public interface EditorContract {
    interface View extends BaseView<Presenter> {
        void showPetDetails(String nameString, String breedString, int weight, int gender);
        void chooseGender();
        void showSavePetSuccess();
        void showSavePetFailed();
        void showUpdateSuccess();
        void showUpdateFailed();
        void showDeleteConfirmationDialog();
        void showDeleteSuccess();
        void showDeleteFailed();
        void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener);
    }

    interface Presenter {
        void start();
        void loadPet(Cursor data);
        void setGender(int gender);
        int getGender();
        void savePet(ContentResolver contentResolver, Uri currentUri, String nameString, String breedString, String weightString);
        void deletePet(ContentResolver contentResolver, Uri currentUri);
    }
}
