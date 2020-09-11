package com.arya1021.alipay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.arya1021.alipay.request.po.Configer;
import com.arya1021.alipay.utlis.SpUtils;

public class SettingActivity extends AppCompatActivity {
    EditText edGatewayUrl;
    EditText edWebsocketUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        edGatewayUrl = findViewById(R.id.ed_gateway_url);
        edWebsocketUrl = findViewById(R.id.ed_websocket_url);

        if ( Configer.getInstance().getUrl(this.getApplicationContext()) != null){
            edGatewayUrl.setText(Configer.getInstance().getUrl(this.getApplicationContext()));
        }
        if ( Configer.getInstance().getWebSocketUrl(this.getApplicationContext()) != null){
            edWebsocketUrl.setText(Configer.getInstance().getWebSocketUrl(this.getApplicationContext()));
        }

    }


    public void BtnSave(View view) {
        String gatewayUrl = edGatewayUrl.getText().toString();
        String websocketUrl = edWebsocketUrl.getText().toString();
        if (TextUtils.isEmpty(gatewayUrl)) {
            Toast.makeText(this, "支付网关不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(websocketUrl)) {
            Toast.makeText(this, "WebSocket地址不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if( !gatewayUrl.endsWith("/") ){
            gatewayUrl += "/";
        }
        if( !websocketUrl.endsWith("/") ){
            websocketUrl += "/";
        }
        SpUtils.saveString(this.getApplicationContext(),"gatewayUrl",gatewayUrl);
        SpUtils.saveString(this.getApplicationContext(),"websocketUrl",websocketUrl);


        Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();
        finish();
    }

}
