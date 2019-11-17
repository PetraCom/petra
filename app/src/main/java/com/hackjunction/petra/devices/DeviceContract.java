package com.hackjunction.petra.devices;

import com.hackjunction.petra.BasePresenter;
import com.hackjunction.petra.BaseView;

public interface DeviceContract {
    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter<View> {
    }
}
