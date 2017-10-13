package com.example.android.pets.editor;

import android.content.CursorLoader;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import com.example.android.pets.BaseActivity;
import com.example.android.pets.R;
import com.example.android.pets.data.PetContract;
import com.example.android.pets.util.ActivityUtils;

/**
 * EditorActivity 加载 View 和 Presenter
 */

public class EditorActivity extends BaseActivity {

    private EditorFragment mEditorFragment;
    private Uri mCurrentUri;

    @Override
    public void initView() {
        mCurrentUri = getIntent().getData();
        setToolbarTitle(mCurrentUri);

        mEditorFragment = (EditorFragment) getFragmentManager().findFragmentById(R.id.details_content_frame);
        if (mEditorFragment == null) {
            mEditorFragment = EditorFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(), mEditorFragment, R.id.details_content_frame);
        }
        if (mCurrentUri != null) {
            Bundle args = new Bundle();
            args.putString("mCurrentUri", String.valueOf(mCurrentUri));
            mEditorFragment.setArguments(args);
        }
    }

    private void setToolbarTitle(Uri currentUri) {
        if (currentUri == null) {
            setTitle(R.string.editor_activity_title_new_pet);
        } else {
            setTitle(R.string.editor_activity_title_pet_details);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mEditorFragment.isPetChanged()) {
            super.onBackPressed();
        } else {
            DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            };
            mEditorFragment.showUnsavedChangesDialog(discardButtonClickListener);
        }
    }

    @Override
    public void initData() {
        if (mCurrentUri != null) {
            String[] projection = {
                    PetContract.PetEntry._ID,
                    PetContract.PetEntry.COLUMN_PET_NAME,
                    PetContract.PetEntry.COLUMN_PET_BREED,
                    PetContract.PetEntry.COLUMN_PET_GENDER,
                    PetContract.PetEntry.COLUMN_PET_WEIGHT
            };
            CursorLoader cursorLoader = new CursorLoader(this, mCurrentUri, projection, null, null, null);
            new EditorPresenter(getLoaderManager(), cursorLoader, mEditorFragment);
        } else {
            new EditorPresenter(mEditorFragment);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.editor_activity;
    }
}
