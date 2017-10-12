package com.example.android.pets.editor;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import com.example.android.pets.BaseActivity;
import com.example.android.pets.R;
import com.example.android.pets.util.ActivityUtils;

/**
 * EditorActivity 加载 View 和 Presenter
 */

public class EditorActivity extends BaseActivity {

    private EditorFragment mEditorFragment;

    @Override
    public void initView() {
        Uri currentUri = getIntent().getData();
        setToolbarTitle(currentUri);

        mEditorFragment = (EditorFragment) getFragmentManager().findFragmentById(R.id.details_content_frame);
        if (mEditorFragment == null) {
            mEditorFragment = EditorFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(), mEditorFragment, R.id.details_content_frame);
        }
        if (currentUri != null) {
            Bundle args = new Bundle();
            args.putString("currentUri", String.valueOf(currentUri));
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
       new EditorPresenter(mEditorFragment);
    }

    @Override
    public int getLayout() {
        return R.layout.editor_activity;
    }
}
