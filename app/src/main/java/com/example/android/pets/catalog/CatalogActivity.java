package com.example.android.pets.catalog;

import android.content.CursorLoader;

import com.example.android.pets.BaseActivity;
import com.example.android.pets.R;
import com.example.android.pets.data.PetContract;
import com.example.android.pets.util.ActivityUtils;

/**
 * CatalogActivity 加载 Fragment 和 Presenter
 */

public class CatalogActivity extends BaseActivity {

    private CatalogFragment mCatalogFragment;

    @Override
    public void initView() {
        mCatalogFragment = (CatalogFragment) getFragmentManager().findFragmentById(R.id.contentFrame);
        if (mCatalogFragment == null) {
            // 创建 Fragment
            mCatalogFragment = CatalogFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(), mCatalogFragment, R.id.contentFrame);
        }
    }

    @Override
    public void initData() {
        String[] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
        };
        CursorLoader cursorLoader = new CursorLoader(this, PetContract.PetEntry.CONTENT_URI, projection, null, null, null);
        new CatalogPresenter(getLoaderManager(), cursorLoader, mCatalogFragment);
    }

    @Override
    public int getLayout() {
        return R.layout.catalog_activity;
    }
}
