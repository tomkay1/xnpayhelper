package com.arya1021.alipay.request;

import android.content.IntentFilter;

import com.arya1021.alipay.MyApplication;


/**
 * @ Created by Dpc
 * @ <p>TiTle:  ReceiveUtils</p>
 * @ <p>Description:</p>
 * @ date:  2018/10/14 12:02
 */
public class ReceiveUtils {

    public static void startReceive() {
        if (!ReceiverMain.mIsInit) {
            IntentFilter filter = new IntentFilter();

            filter.addAction(HookConstants.RECEIVE_QR_ALIPAY);
            filter.addAction(HookConstants.RECEIVE_BILL_ALIPAY);
            filter.addAction(HookConstants.RECEIVE_BILL_ALIPAYHB);
            filter.addAction(HookConstants.RECEIVE_BILL_ALIPAYZZ);
            filter.addAction(HookConstants.RECEIVE_BILL_ALIPAYZK);
            MyApplication.app.registerReceiver(new ReceiverMain(), filter);
        }
    }

}
