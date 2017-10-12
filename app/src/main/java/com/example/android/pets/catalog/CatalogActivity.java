package com.example.android.pets.catalog;

import com.example.android.pets.BaseActivity;
import com.example.android.pets.R;
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
        // 创建 Presenter 实例
        new CatalogPresenter(mCatalogFragment);
    }

    @Override
    public int getLayout() {
        return R.layout.catalog_activity;
    }
}
