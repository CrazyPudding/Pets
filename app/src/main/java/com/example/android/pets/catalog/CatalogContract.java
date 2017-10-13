package com.example.android.pets.catalog;

import android.content.ContentResolver;
import android.database.Cursor;

import com.example.android.pets.BaseView;
import com.example.android.pets.data.PetDbHelper;

/**
 * View 和 Presenter 的契约类，列出两者的功能
 */

public interface CatalogContract {

    interface View extends BaseView<Presenter> {
        void showAddPet();      // 跳转添加页面
        void showPetDetails(long id);  // 跳转 Pet 详情页
        void swapData(Cursor data);
    }

    interface Presenter {
        void start();
        void insertDummyData(PetDbHelper dbHelper);  // 插入测试元素
        void deleteAllPets(ContentResolver contentResolver);   // 删除所有元素
        void addNewPet();  // 添加一个元素
        void openPetDetails(long id);  //打开详情
    }
}
