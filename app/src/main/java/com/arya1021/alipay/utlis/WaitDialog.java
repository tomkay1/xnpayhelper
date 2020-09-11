/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arya1021.alipay.utlis;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Window;


/**
 * Created in Oct 23, 2015 1:19:04 PM.
 *
 * @author Shihy.
 */
public class WaitDialog extends ProgressDialog {

    public WaitDialog(Context context, String msg) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        setProgressStyle(STYLE_SPINNER);
        if (TextUtils.isEmpty(msg)) {
            setMessage("正在请求，请稍后…");
        } else {
            setMessage(msg);
        }
    }

}
