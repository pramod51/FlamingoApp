package com.app.flamingo.Interface;

public interface IExpandableListViewClickListener {

    void OnEdit(int groupPosition, int childPosition);
    void OnDelete(int groupPosition, int childPosition);
}
