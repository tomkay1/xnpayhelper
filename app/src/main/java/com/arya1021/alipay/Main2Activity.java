package com.arya1021.alipay;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.arya1021.alipay.Receiver.BillReceived;
import com.arya1021.alipay.request.ReceiveUtils;
import com.arya1021.alipay.request.ServiceMain;
import com.arya1021.alipay.request.po.Configer;
import com.arya1021.alipay.utlis.HTTPSTrustManager;
import com.arya1021.alipay.utlis.PayHelperUtils;
import com.arya1021.alipay.utlis.QRCode;
import com.arya1021.alipay.utlis.SealAppContext;
import com.arya1021.alipay.utlis.WaitDialog;
import com.arya1021.alipay.ws.ChannelType;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txStateAlipay;
    TextView txStateWechat;
    TextView txStateUnionpay;
    ScrollView scSubclass;
    TextView console;
    EditText edBankCode;
    EditText edMoney;
    EditText edMark;
    ImageView igCode;
    private static Context mContext;
    private Main2Activity.StateReceived stateReceived;
    private BillReceived billReceived;
    private String currentAlipay = "";
    private String currentWechat = "";
    private String currentUnionpay = "";

    public static String currAlipayUserId;
    public static String currAlipayLoginId;

    public static String currNickName;
    public static String currRealName;
    public static String currUserAvatar;


    private String userId;
    private String loginid;

    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 默认发音人
    private String voicer = "xiaoyan";
    String texts = "";
    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    private static Button mBtnAlipay;
    private static Button mBtnService;

    private static TextView txtXniuWelcome;

    public static synchronized TextView getTxtXniuWelcome(){
        return txtXniuWelcome;
    }

    public static synchronized Button getBtnStartService(){
        return mBtnService;
    }

    public static  synchronized Context getContext(){
        return mContext;
    }
    public static void doubleClick(Button button){
        for(int i=0;i<2;i++){
            button.callOnClick();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.d("arik",e.getMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        HTTPSTrustManager.allowAllSSL();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        txtXniuWelcome = headerLayout.findViewById(R.id.txtXniuWelcome);

        mContext = this;
        initView();
        initData();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // 设置
            Intent intent = new Intent();
            intent.setClass(this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initView() {
        scSubclass = findViewById(R.id.sv_subclass);
        console = findViewById(R.id.console);
        txStateAlipay = findViewById(R.id.tx_state_alipay);

        mBtnAlipay = findViewById(R.id.btn_start_alipay);
        mBtnService = findViewById(R.id.btn_start_service);

        // 未绑定启动按钮不可点击
        if( Configer.getInstance().getUid(mContext) == null){
            mBtnService.setClickable(false);
        }
        if ( Configer.getInstance().getLoginName(mContext) != null ) {
            txtXniuWelcome.setText("你好，"+Configer.getInstance().getLoginName(mContext));
        }

        console.setMovementMethod(ScrollingMovementMethod.getInstance());


    }

    private void initData() {
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);

        billReceived = new BillReceived();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SealAppContext.BILLRECEIVED_ACTION);
        intentFilter.addAction(SealAppContext.TRADENORECEIVED_SHI_ACTION);
        registerReceiver(billReceived, intentFilter);

        stateReceived = new Main2Activity.StateReceived();
        IntentFilter stateFilter = new IntentFilter();
        stateFilter.addAction(SealAppContext.LOGINIDRECEIVED_ACTION);
        stateFilter.addAction(SealAppContext.MSGRECEIVED_ACTION);
        stateFilter.addAction(SealAppContext.QRCODERECEIVED_ACTION);
        stateFilter.addAction(SealAppContext.VOICE_ACTION);
        registerReceiver(stateReceived, stateFilter);

    }



    private boolean changeServiceStatus() {
        //GlobalConstants.mIsRunning.compareAndSet(GlobalConstants.mIsRunning.get(),!GlobalConstants.mIsRunning.get());

        GlobalConstants.mIsRunning = !GlobalConstants.mIsRunning;
        mBtnService.setText(GlobalConstants.mIsRunning? "停止监听服务" : "启动监听服务");
        return GlobalConstants.mIsRunning;
    }

    public void startService(View view) {
        try{
            if(!GlobalConstants.mIsRunning){
                //有的手机就算已经静态注册服务还是不行启动，再手动启动一下。
                startService(new Intent(this, ServiceMain.class));

                if(Configer.getInstance().getCurrentAlipay(mContext)!=null){
                    ServiceMain.sendSwitchMsg(mContext, ChannelType.ALIPAY.getCode(),
                            Configer.getInstance().getCurrentAlipay(mContext),
                            ServiceMain.mIsAlipayRunning);
                }

                //广播也再次注册一下。。。机型兼容。。。
                ReceiveUtils.startReceive();
                addStatusBar();
            }else{
                // 发送立即停止收款的消息。（心跳有延迟）
                if(Configer.getInstance().getCurrentAlipay(mContext)!=null){
                    ServiceMain.sendSwitchMsg(mContext,ChannelType.ALIPAY.getCode(),
                            Configer.getInstance().getCurrentAlipay(mContext),false);
                }
                PayHelperUtils.sendmsg(mContext,"已通知服务器立即停止收款。");
                stopService(new Intent(this, ServiceMain.class));
                GlobalConstants.closeByApp = true;
            }
            changeServiceStatus();
        }catch (Throwable e){
            Log.d("arik",e.getMessage());
            Log.d("arik","startService error==============="+e.getMessage());
        }


    }

    private boolean changeAlipayStatus() {
        ServiceMain.mIsAlipayRunning = !ServiceMain.mIsAlipayRunning;
        mBtnAlipay.setText(ServiceMain.mIsAlipayRunning ? "停止" : "启动");
        return ServiceMain.mIsAlipayRunning;
    }

    public void OpenAlipay(View view) {

        if (getPackageInfo(SealAppContext.ALIPAY_PACKAGE) != null
                && !getPackageInfo(SealAppContext.ALIPAY_PACKAGE).versionName.contentEquals("10.1.38.2139")) {
            Toast.makeText(Main2Activity.this, "支付宝版本不对！官方下载版本号：10.1.38.2139", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!ServiceMain.mIsAlipayRunning){
            startAPP("alipay");

            Log.d("arik","======发送开启收款通知");
            ServiceMain.sendSwitchMsg(mContext,ChannelType.ALIPAY.getCode(),
                    Configer.getInstance().getCurrentAlipay(mContext),true);

        }else{
            txStateAlipay.setText("未启动");
            Log.d("arik","======发送关闭收款通知");
            ServiceMain.sendSwitchMsg(mContext,ChannelType.ALIPAY.getCode(),
                    Configer.getInstance().getCurrentAlipay(mContext),false);
        }
        changeAlipayStatus();

    }


    /**
     * 在状态栏添加图标
     */
    private void addStatusBar() {
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();

        PendingIntent pi = PendingIntent.getActivity(this, 0, getIntent(), 0);
        Notification noti = new Notification.Builder(this)
                .setTicker("程序启动成功")
                .setContentTitle("看到我，说明我在后台正常运行")
                .setContentText("始于：" + new SimpleDateFormat("MM-dd HH:mm:ss").format(new java.util.Date()))
                .setSmallIcon(R.mipmap.ic_launcher)//设置图标
                .setDefaults(Notification.DEFAULT_SOUND)//设置声音
                .setContentIntent(pi)//点击之后的页面
                .build();

        manager.notify(17953, noti);
    }

    private Handler loginHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0: // 支付宝
                    txStateAlipay.setText(msg.obj.toString());
                    break;
                case 1: // 微信
                    txStateWechat.setText(msg.obj.toString());
                    break;
                case 2: // 云闪付
                    txStateUnionpay.setText(msg.obj.toString());
                    break;
                default:
                    break;
            }

        }
    };


    public void startAPP(final String type) {

        String appName = "";
        switch (type){
            case "alipay":
                appName = "支付宝";

                break;
            case "wechat":
                appName = "微信";
                break;
            case "unionpay":
                appName = "云闪付";
                break;
            default:
                break;
        }
        final WaitDialog waitDialog = new WaitDialog(mContext, appName + "启动中...");
        waitDialog.show();
        SingleTaskPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (type.equals("alipay")) {

                    PayHelperUtils.startAPP(mContext, SealAppContext.ALIPAY_PACKAGE);

                    while (true) {
                        if (PayHelperUtils.isRun(mContext, SealAppContext.ALIPAY_PACKAGE)) {
                            PayHelperUtils.startAPP(mContext);
                            waitDialog.dismiss();
                            break;
                        }
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if(Configer.getInstance().getCurrentAlipay(mContext)!=null){
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = Configer.getInstance().getCurrentAlipay(mContext) + " 运行中...";
                        loginHandler.sendMessage(msg);
                        PayHelperUtils.sendmsg(mContext, "当前登录支付宝账号:" + Configer.getInstance().getCurrentAlipay(mContext));
                    }
                }
            }
        });


    }

    public void sendmsg(String txt) {
        Message msg = new Message();
        msg.what = 1;
        Bundle data = new Bundle();
        long l = System.currentTimeMillis();
        Date date = new Date(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
        String d = dateFormat.format(date);
        data.putString("log", txt + "\n" + d);
        msg.setData(data);
        try {
            handler.sendMessageDelayed(msg,200);
            return;
        } catch (Exception e) {
            Log.d("arik",e.getMessage());
        }
    }

    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        public void handleMessage(final Message message) {
            super.handleMessage(message);


            /*String str = message.getData().getString("log");
            if(console.getText().toString().length() > 8000){
                console.setText("日志定时清理完成...\n\n" + str);
            }else{
                console.append(str);
            }
            console.append("\n-------------------------------------------------\n");

            int offset=console.getLineCount()*console.getLineHeight();
            if(offset>console.getHeight()){
                //console.scrollTo(0,offset-console.getHeight());
                console.scrollTo(0,0);
            }*/

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            String str = message.getData().getString("log");
            if (console != null) {
                console.append("\n-------------------------------------------------------------\n");
                if (console.getText().toString().length() > 8000) {
                    console.setText("日志定时清理完成...\n\n" + str);
                } else {
                    console.append(str);
                }

                Handler sHandler = new Handler();

                sHandler.post(new Runnable() {
                    public void run() {
                        scSubclass.fullScroll(ScrollView.FOCUS_DOWN);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }
                    }
                });
            }


        }
    };

    public void GetQrCode_zz(View view) {

    }


    public void GetQrCode_ws(View view) {
        String Money = edMoney.getText().toString();
        String Mark = edMark.getText().toString();
        if (TextUtils.isEmpty(Money)) {
            Toast.makeText(this, "金额不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(Mark)) {
            Toast.makeText(this, "备注不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        PayHelperUtils.sendAppMsg(Money, Mark, "wechat", mContext);
    }

    public void goLogin(View view) {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
    }

    public class StateReceived extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().contentEquals(SealAppContext.MSGRECEIVED_ACTION)) {
                sendmsg(intent.getStringExtra("msg"));
            } else if (intent.getAction().contentEquals(SealAppContext.VOICE_ACTION)) {
                VoiceAnnouncements(intent.getStringExtra("VoiceContent"));
            } else if (intent.getAction().contentEquals(SealAppContext.LOGINIDRECEIVED_ACTION)) {
                loginid = intent.getStringExtra("loginid");
                String type = intent.getStringExtra("type");

                currNickName = intent.getStringExtra("nickname");
                currUserAvatar = intent.getStringExtra("userAvatar");
                currRealName = intent.getStringExtra("realName");

                if (!TextUtils.isEmpty(loginid)) {
                    if (type.equals("alipay") && !loginid.equals(currentAlipay)) {
                        userId = intent.getStringExtra("userId");
                        currAlipayUserId = userId;
                        PayHelperUtils.sendmsg(mContext, "当前登录支付宝账号:" + loginid);
                        currentAlipay = loginid;
                        Configer.getInstance().setCurrentAlipay(mContext,loginid);
                        currAlipayLoginId = loginid;
                        txStateAlipay.setText(loginid + " 运行中...");
                    } else if (type.equals("wechat") && !loginid.equals(currentWechat)) {
                        PayHelperUtils.sendmsg(mContext, "当前登录微信账号:" + loginid);
                        currentWechat = loginid;
                        txStateWechat.setText(loginid + " 运行中...");
                        Configer.getInstance().setCurrentWechat(mContext,loginid);
                    } else if (type.equals("unionpay") && !loginid.equals(currentUnionpay)) {
                        PayHelperUtils.sendmsg(mContext, "当前登录云闪付账号:" + loginid);
                        currentUnionpay = loginid;
                        txStateUnionpay.setText(loginid + " 运行中...");
                        Configer.getInstance().setCurrentUnionpay(mContext,loginid);
                    }
                }
            } else if (intent.getAction().contentEquals(SealAppContext.QRCODERECEIVED_ACTION)) {//二维码生成完成
                String money = intent.getStringExtra("money");
                String mark = intent.getStringExtra("mark");
                String type = intent.getStringExtra("type");
                String payurl = intent.getStringExtra("payurl");
                Log.e("--" + type + "生成二维码--", "money：" + money + " money：" + mark + "\n" + payurl);
                sendmsg("生成" + (type.equals("alipay") ? "支付宝" : "微信") + "二维码成功\n金额：￥" + money + "\n备注（订单号）：" + mark + "\n二维码：" + payurl);
                PayHelperUtils.startAPP(context);
                igCode.setImageBitmap(QRCode.createQRCode(payurl));

                Bitmap bmp = null;
                if (type.equals("alipay")) {
                    bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_alipay);
                } else if (type.equals("wechat")) {
                    bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_wechat);
                }
                igCode.setImageBitmap(QRCode.createQRCodeWithLogo(payurl, bmp));
            }
        }
    }

    //语音播报
    public void VoiceAnnouncements(String content) {

        if (null == mTts) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            sendmsg("创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
            return;
        }
        // 设置参数
        setParam();
        int code = mTts.startSpeaking(content, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            sendmsg("语音合成失败,错误码: " + code);
        }
    }


    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d("---初始化监听--", "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                sendmsg("初始化失败,错误码：" + code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };
    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            Log.e("---语音播报--", "开始播放");
        }

        @Override
        public void onSpeakPaused() {
            Log.e("---语音播报--", "暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            Log.e("---语音播报--", "继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // 合成进度
            mPercentForBuffering = percent;
            Log.e("---语音播报--", String.format(getString(R.string.tts_toast_format), mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                Log.e("---语音播报--", "播放完成");
            } else if (error != null) {
                Log.e("---语音播报--", error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}

            if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
                byte[] buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER);
                Log.e("MscSpeechLog", "buf is =" + buf);
            }

        }
    };

    /**
     * 参数设置
     *
     * @return
     */
    private void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, "50");
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, "50");
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, "50");
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");

        }
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.pcm");
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(stateReceived);
        unregisterReceiver(billReceived);


        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
        super.onDestroy();
    }

//    @Override
//    public void onBackPressed() {
//        moveTaskToBack(true);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == 4) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    private PackageInfo getPackageInfo(String packageName) {
        PackageInfo pInfo = null;
        try {
            //通过PackageManager可以得到PackageInfo
            PackageManager pManager = getPackageManager();
            pInfo = pManager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
            return pInfo;
        } catch (Exception e) {
            Log.d("arik",e.getMessage());
        }
        return pInfo;
    }
}
