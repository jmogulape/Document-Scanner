package com.joshuabutton.queenscanner.process.presenter;

import com.joshuabutton.queenscanner.process.model.FilterModel;

import java.util.List;


public interface IProcessPresenter {
    void onItemClick(FilterModel adjuster);

    List<FilterModel> getListModel();

    String getFolderPath(String folderPath);

}
