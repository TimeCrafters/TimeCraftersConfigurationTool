package org.timecrafters.TimeCraftersConfigurationTool.ui.editor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditorViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EditorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is editor fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}