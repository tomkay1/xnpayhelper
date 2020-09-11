package com.arya1021.alipay.request;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.arya1021.alipay.GlobalConstants;
import com.arya1021.alipay.MyApplication;
import com.arya1021.alipay.request.po.Configer;
import com.arya1021.alipay.request.po.DataSynEvent;
import com.arya1021.alipay.utlis.DeviceUtils;
import com.arya1021.alipay.utlis.MD5Util;
import com.arya1021.alipay.utlis.PayHelperUtils;
import com.arya1021.alipay.utlis.SealAppContext;
import com.arya1021.alipay.utlis.SpUtils;
import com.arya1021.alipay.ws.BaseMsg;
import com.arya1021.alipay.ws.Channel;
import com.arya1021.alipay.ws.ChannelType;
import com.arya1021.alipay.ws.ClientSwitch;
import com.arya1021.alipay.ws.Heartbeat;
import com.arya1021.alipay.ws.MqTopic;
import com.arya1021.alipay.ws.MsgNewOrder;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;


/**
 * @ Created by Dlg
 * @ <p>TiTle:  ServiceMain</p>
 * @ <p>Description: 这个类就一直轮循去请求是否需要二维码</p>
 * @ date:  2018/09/22
 */
public class ServiceMain extends Service {


    public static volatile boolean mIsAlipayRunning = false;


    private static String wsUrl = null;
    private static volatile long lastHeartbeat = 0;

    private static volatile boolean lastCheckAlipayNotRun = false;

    /**
     * 是否需要心跳任务。防止出现多个心跳任务
     */
    private static volatile boolean needHeartbeatTask = true;

    //防止被休眠，你们根据情况可以开关，我是一直打开的，有点费电是必然的，哈哈
    private PowerManager.WakeLock mWakeLock;

    private Context mContext;


    final Timer timer = new Timer();
    WebSocketClient socketConnect;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        mContext = this.getApplicationContext();
        PayHelperUtils.sendmsg(mContext,"[公共消息]后台监听服务启动..");
        super.onCreate();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | ACQUIRE_CAUSES_WAKEUP,
                "com.arya1021.alipay:waketag");
        mWakeLock.acquire();

        GlobalConstants.mIsRunning = true;
        Log.d("arik", "onCreate: 服务启动===ServiceMain.mIsRunning=="+GlobalConstants.mIsRunning);
        try {
            openSocket();
        }catch (Exception e){
            Log.d("arik", "openSocket 出错:" + e.toString());
            PayHelperUtils.sendmsg(mContext,"[公共消息]连接服务器出错："+e.getMessage());
        }
        ReceiveUtils.startReceive();
        EventBus.getDefault().register(this);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.sendEmptyMessage(0);
        return START_STICKY;
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (handler.hasMessages(0)) {
                return;
            }

            handler.sendEmptyMessageDelayed(0,
                    5000);
        }
    };

    public static void sendSwitchMsg(Context mContext,String channelType,
                                     String channelAccount,boolean switchOnOff){
        // 发送开启收款通知
        BaseMsg<ClientSwitch> msg = new BaseMsg<>();
        msg.setTopic(MqTopic.SWITCH_BY_CLIENT);
        msg.setFromUserId(Configer.getInstance().getUid(mContext));

        ClientSwitch clientSwitch = new ClientSwitch();
        clientSwitch.setChannelType(channelType);
        clientSwitch.setChannelAccount(channelAccount);
        clientSwitch.setStatus(switchOnOff?1:0);
        msg.setContent(clientSwitch);

        Gson gson = new Gson();
        boolean succ = ServiceMain.sendMsg(mContext,gson.toJson(msg));
        if(succ){
            String showInfo = switchOnOff ? "已通知服务器开启收款。" : "已通知服务器立即停止收款。";
            PayHelperUtils.sendmsg(mContext,showInfo);
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (mWakeLock != null)
                mWakeLock.release();
            mWakeLock = null;
        } catch (Exception e) {
            Log.d("arik",e.getMessage());
        }

        Log.d("arik","ServiceMain服务停止====closeByApp=="+GlobalConstants.closeByApp);

        // 不是主动停止的 需要拉起
        if( !GlobalConstants.closeByApp ){
            Intent intent = new Intent(this.getApplicationContext(), ServiceMain.class);
            this.startService(intent);
            PayHelperUtils.sendmsg(this.getApplicationContext(),"[公共消息]服务被杀死。");
        }else{
            PayHelperUtils.sendmsg(this.getApplicationContext(),"[公共消息]服务已停止。");
        }

        GlobalConstants.mIsRunning = false;
        needHeartbeatTask = true;

        // 最后close socket
        try{
            if(socketConnect.isOpen()){
                socketConnect.close(99999);
            }

            WsocketChannelAccountSingleton.getInstance().
                    getWebSocketMap().remove("alipay"+Configer.getInstance().getCurrentAlipay(mContext));
        }catch (Exception e){
            Log.d("arik",e.getMessage());
        }


    }

    public void openSocket() throws URISyntaxException {

        Log.d("arik",Configer.getInstance().toString());
        if(Configer.getInstance().getUid(this.getApplicationContext()) == null){
            PayHelperUtils.sendmsg(mContext, "[提示]请先登录并绑定！");
            return;
        }
        if(Configer.getInstance().getCurrentAlipay(mContext) == null ){
            PayHelperUtils.sendmsg(mContext, "[提示]请先点击启动支付宝！");
            return;
        }
        if(WsocketChannelAccountSingleton.getInstance().
                getWebSocketMap().get("alipay"+Configer.getInstance().getCurrentAlipay(mContext)) == null){


            String sign = MD5Util.stringMD5(Configer.getInstance().getUid(mContext)
                    +Configer.getInstance().getToken(mContext)
                    +"alipay"+Configer.getInstance().getCurrentAlipay(mContext));

            wsUrl = Configer.getInstance().getWebSocketUrl(mContext)
                    + Configer.getInstance().getUid(mContext)+ "/"
                    + "alipay" + "/"
                    + Configer.getInstance().getCurrentAlipay(mContext)+"/"
                    + sign;


            Log.d("arik", "openSocket: 尝试连接 wsUrl="+wsUrl);
            socketConnect = new WebSocketClient(new URI(wsUrl)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {

                    GlobalConstants.socketNum = 1;

                    WsocketChannelAccountSingleton.getInstance()
                            .getWebSocketMap().putIfAbsent("alipay"+Configer.getInstance().getCurrentAlipay(mContext)
                            ,this);
                    Log.d("arik", "WebSocket onOpen连接成功！");
                    PayHelperUtils.sendmsg(mContext, "[公共消息]连接成功！");

                    // 重发失败的消息
                    resendWebsocketMsg();


                    try {
                        // 定时发送心跳包
                        if(needHeartbeatTask){
                            heartbeat();
                        }

                    }catch (Exception e){
                        Log.d("arik",e.getMessage());
                    }
                }
                @Override
                /**
                 * 收到服务端消息
                 */
                public void onMessage(String message) {
                    Log.d("arik", "recevie socket msg："+message);

                    Gson gson = new Gson();
                    try{
                        BaseMsg baseMsg = gson.fromJson(message, BaseMsg.class);

                        switch (baseMsg.getTopic()){

                            case MqTopic.HEARTBEAT:
                                lastHeartbeat = System.currentTimeMillis();
                                //PayHelperUtils.sendmsg(mContext, "[New心跳]APP正常服务中..");
                                break;
                            case MqTopic.NEW_ORDER:

                                //发送ack
                                BaseMsg ackMsg = new BaseMsg();
                                ackMsg.setTopic(MqTopic.ACK);
                                ackMsg.setMsgId(baseMsg.getMsgId());
                                ackMsg.setFromUserId(Configer.getInstance().getUid(mContext));
                                ServiceMain.sendMsg(mContext,gson.toJson(ackMsg));

                                try{
                                    final MsgNewOrder msgNewOrder = gson.fromJson(baseMsg.getContent().toString(),MsgNewOrder.class);

                                    PayHelperUtils.sendmsg(mContext, "[New订单]收到新订单请求："+baseMsg.getContent());

                                    // 如果不需要app生成则跳过
                                    if( !msgNewOrder.getNeedAppGenQrCode()){
                                        break;
                                    }
                                    Channel channel = Channel.getChannel(msgNewOrder.getChannel());

                                    switch (channel){

                                        case ALIPAY_HB:
                                        case ALIPAY_ZZ:
                                        case ALIPAY_ZZ_BZ:
                                        case ALIPAY_ZZ_NOBZ:
                                        case ALIPAY_JHYZZ_BZ:
                                        case ALIPAY_JHYZZ_NOBZ:
                                        case ALIPAY_QRCODE_AUTO:
                                        case ALIPAY_QRCODE_FIX:
                                        case ALIPAY_QRCODE_HALF:

                                        case ALIPAY_NEW_ZZ:
                                        case ALIPAY_HB_KOULING:
                                        case ALIPAY_DY:
                                        case ALIPAY_NEW_ZK:

                                            PayUtils.getInstance().sendAlipayQrcodeInfo(msgNewOrder.getChannel(),
                                                    MyApplication.app
                                                    , msgNewOrder.getRealMoney()
                                                    , msgNewOrder.getTradeNo());

                                            break;
                                        default:
                                            break;

                                    }

                                }catch (Exception e){
                                    // TODO error
                                }
                                break;
                        }

                    }catch (Exception e){
                        //
                        Log.d("arik", "msg not valid!");
                    }

                }

                @Override
                public void reconnect() {
                    GlobalConstants.socketNum = 0;
                    Log.e("arik", "重连...");
                    super.reconnect();
                }
                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("arik", "ServiceMain...WebSocket onClose,reason:"+reason + "   " + remote + "   "+code);

                    if(code == 99999){
                        Log.d("arik","close by 99999");
                        return;
                    }
                    GlobalConstants.socketNum = 0;
                    needHeartbeatTask = true;

                    WsocketChannelAccountSingleton.getInstance()
                            .getWebSocketMap().remove("alipay"+Configer.getInstance().getCurrentAlipay(mContext));

                    reconnectToServer();
                }
                @Override
                public void onError(Exception ex) {
                    Log.e("arik", "onError:" + ex.toString());

                    GlobalConstants.socketNum = 0;
                    needHeartbeatTask = true;

                    WsocketChannelAccountSingleton.getInstance()
                            .getWebSocketMap().remove("alipay"+Configer.getInstance().getCurrentAlipay(mContext));

                    reconnectToServer();
                }
            };

            socketConnect.connect();
        }
        else
        {
            Log.d("arik","已建立连接，不重复操作");
        }
    }

    public void reconnectToServer() {
        Log.d("arik","ready reconnectToServer......GlobalConstants.socketNum====" +
                ""+GlobalConstants.socketNum+",,mIsRunning==="+GlobalConstants.mIsRunning);

        if(GlobalConstants.socketNum != 0 || GlobalConstants.mIsRunning == false){
            return;
        }


        PayHelperUtils.sendmsg(mContext, "[公共消息]连接服务器失败！开始重新连接..");

        // 延迟重连，避免过多连接
        WsocketChannelAccountSingleton.getInstance()
                .getWebSocketMap().remove("alipay"+Configer.getInstance().getCurrentAlipay(mContext));

        try {
            // 延迟8s重连
            Thread.sleep(8000);
            Log.d("arik", "reconnect: 尝试用WebSocket连接服务器");
            openSocket();
        } catch (Exception e) {
            Log.e("arik", "reconnectToServer 出错:" + e.toString());

        }

    }

    public void heartbeat()
    {
        if(Configer.getInstance().getUid(this.getApplicationContext()) == null){

            Log.d("arik", "用户未绑定" );
            PayHelperUtils.sendmsg(mContext, "[提示]请先登录并绑定！");

            return;
        }
        if(GlobalConstants.socketNum == 0){
            Log.d("arik", "未连接服务器websocket" );
            return;
        }
        Log.d("arik", "heartbeat: 进入函数，lockReconnect为" + GlobalConstants.socketNum);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // 未启动则取消心跳
                if( !GlobalConstants.mIsRunning ||
                        !mIsAlipayRunning ){
                    PayHelperUtils.sendmsg(mContext, "[提示]点击启动监听服务开始收款吧！");
                    return;
                }

                // 距离上次心跳时间 没有超过5秒。跳过
                if( (System.currentTimeMillis() - lastHeartbeat) < 5*1000){
                    Log.d("arik", "距离上次心跳时间 没有超过5秒。跳过");
                    return;
                }
                // 检查支付宝是否在运行
                boolean isRun = PayHelperUtils.isRun(mContext, SealAppContext.ALIPAY_PACKAGE);
                if( !isRun ){

                    //通知服务器立即停止收款
                    sendSwitchMsg(mContext, ChannelType.ALIPAY.getCode(),
                            Configer.getInstance().getCurrentAlipay(mContext),
                            false);

                    lastCheckAlipayNotRun = true;
                    PayHelperUtils.sendmsg(mContext, "[公共消息]检测到支付宝未运行，正在尝试自动打开..");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }
                    PayHelperUtils.startAPP(mContext,SealAppContext.ALIPAY_PACKAGE);

                }
                if( isRun && lastCheckAlipayNotRun){

                    lastCheckAlipayNotRun = false;

                    PayHelperUtils.sendmsg(mContext, "[公共消息]重新启动支付宝成功！");
                }

                try {
                    needHeartbeatTask = false;
                    BaseMsg baseMsg = new BaseMsg();
                    baseMsg.setFromUserId(Configer.getInstance().getUid(mContext));
                    baseMsg.setTopic(MqTopic.HEARTBEAT);
                    Heartbeat heartbeat = new Heartbeat();
                    heartbeat.setDeviceInfo(DeviceUtils.getDeviceInfo());
                    heartbeat.setImei(DeviceUtils.getIMEI());
                    List<Heartbeat.ChannelAccount> list = new ArrayList<>();

                    if(mIsAlipayRunning && Configer.getInstance().getCurrentAlipay(mContext)!=null){
                        Heartbeat.ChannelAccount channelAccount = heartbeat.new ChannelAccount();
                        channelAccount.setChannelType(ChannelType.ALIPAY.getCode());
                        channelAccount.setChannelAccount(Configer.getInstance().getCurrentAlipay(mContext));
                        list.add(channelAccount);
                    }

                    heartbeat.setList(list);
                    baseMsg.setContent(heartbeat);
                    Gson gson = new Gson();

                    sendMsg(mContext,gson.toJson(baseMsg));

                } catch (Exception e) {
                    Log.e("arik", "发送心跳包 出错:" + e.toString());
                }
            }
        }, 500,8000);
    }

    /**
     *发送消息
     */
    public static boolean sendMsg(Context context,String msg) {


        boolean succ = false;
        String wsKey = "alipay"+Configer.getInstance().getCurrentAlipay(context);
        try {

            WebSocketClient webSocketClient = WsocketChannelAccountSingleton.getInstance()
                    .getWebSocketMap().get(wsKey);

            // 检查连接是否打开
            if( webSocketClient !=null && webSocketClient.isOpen() ){

                webSocketClient.send(msg);
                succ = true;

            }else{
                addMsgtoWait(context,msg);
            }

        } catch (Exception e) {
            Log.d("arik", "sendMsg error: " + e.getMessage());
            addMsgtoWait(context,msg);
        }
        if(succ){
            Log.d("arik", " 成功发送websocket消息wsKey="+wsKey+": " + msg);
            try{
                Gson gson = new Gson();
                BaseMsg baseMsg = gson.fromJson(msg, BaseMsg.class);
                String showMsg = "";
                switch (baseMsg.getTopic()){
                    case "new.order":

                        int maxIndex = baseMsg.getContent().toString().length() - 1;
                        if(maxIndex > 300){
                            maxIndex = 300;
                        }
                        showMsg = baseMsg.getContent().toString().substring(0,maxIndex);
                        PayHelperUtils.sendmsg(context, "[ws消息已发送]二维码生成:\n"+showMsg+"..");
                        break;
                    case "pay.result":
                        maxIndex = baseMsg.getContent().toString().length() - 1;
                        if(maxIndex > 300){
                            maxIndex = 300;
                        }
                        showMsg = baseMsg.getContent().toString().substring(0,maxIndex);
                        PayHelperUtils.sendmsg(context, "[ws消息已发送]支付结果:\n"+showMsg+"..");
                        break;
                    default:
                        break;
                }

            }catch (Exception e){
                Log.d("arik","sendmsg error="+e.getMessage());
            }


        }
        return succ;
    }

    /**
     * 待发送的websocket消息持久化
     * @param msg
     */
    private static void addMsgtoWait(Context context,String msg){
        Gson gson = new Gson();
        Set<String> saveValues = null;
        try{

            BaseMsg baseMsg = gson.fromJson(msg,BaseMsg.class);
            // 如果不是心跳消息，则需要持久化 然后在下次连接成功的时候进行重发。
            if(!MqTopic.HEARTBEAT.equals(baseMsg.getTopic())){
                Log.d("arik", "发送websocket消息失败，丢入重发队列");
                saveValues = SpUtils.getSet(context,SpUtils.WS_WAIT_SEND_MSG);
                if( saveValues == null ){
                    // 需要保证线程 安全
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        saveValues = ConcurrentHashMap.newKeySet();
                    }
                }
                saveValues.add(msg);

                SpUtils.saveSet(context,SpUtils.WS_WAIT_SEND_MSG,saveValues);
            }
        }catch (Exception e){
            // 消息不合法的话 直接丢弃了
            Log.d("arik", "消息格式不合法直接丢弃。");
        }

        Log.d("arik","需要重发的消息有"+saveValues.size()+"条，等待重发..");

    }

    /**
     * 重发消息
     */
    private void resendWebsocketMsg(){

        Set<String> saveValues = SpUtils.getSet(mContext,SpUtils.WS_WAIT_SEND_MSG);
        if( saveValues == null ){
            Log.d("arik",SpUtils.WS_WAIT_SEND_MSG+"需要重发的消息为空。");
            return;
        }
        // 先删除
        SpUtils.saveSet(mContext,SpUtils.WS_WAIT_SEND_MSG,null);
        Log.d("arik","需要重发的消息有"+saveValues.size()+"条，准备开始重发..");
        for(String msg : saveValues){
            sendMsg(mContext,msg);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(DataSynEvent event) {
        final String str = event.getMessage();
        Log.d("收到的信息main:", str);
        if(str!=null) sendMsg(mContext,str);
    }
}
