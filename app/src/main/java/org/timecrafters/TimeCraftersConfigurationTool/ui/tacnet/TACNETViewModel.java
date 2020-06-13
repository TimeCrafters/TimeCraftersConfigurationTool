package org.timecrafters.TimeCraftersConfigurationTool.ui.tacnet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TACNETViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TACNETViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is TACNET fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}