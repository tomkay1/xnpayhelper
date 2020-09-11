package com.arya1021.alipay.request;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.arya1021.alipay.Main2Activity;
import com.arya1021.alipay.request.po.QrBean;
import com.arya1021.alipay.utlis.MoneyUtil;
import com.arya1021.alipay.utlis.PayHelperUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @ Created by Dlg
 * @ <p>TiTle:  PayUtils</p>
 * @ <p>Description: </p>
 * @ date:  2018/9/23
 */
public class PayUtils {

    //软件首次启动后，只处理支付最近xxx秒的订单，默认为只处理最近20分钟的订单
    private final static int ALIPAY_BILL_TIME = 1200 * 1000;

    private static PayUtils mPayUtils;

    public synchronized static PayUtils getInstance() {
        if (mPayUtils == null) {
            mPayUtils = new PayUtils();
        }
        return mPayUtils;
    }


    /**
     * 这里为了统一，要求就设置为和微信一样了。
     *
     * @param context
     * @param money   金额，单位元，范围1-30000000
     * @param mark    收款备注，最长30个字符，不能为空
     */
    public void sendAlipayQrcodeInfo(String channel, Context context, String money, String mark) {
        if (money == null || TextUtils.isEmpty(mark)) {
            return;
        }
        Long fenMoney = MoneyUtil.yuanToFen(money);

        if (mark.length() > 30 || fenMoney > 30000000 || fenMoney < 1) {
            return;
        }

        if( TextUtils.isEmpty(Main2Activity.currAlipayLoginId) ||
                TextUtils.isEmpty(Main2Activity.currAlipayUserId )){
            PayHelperUtils.sendmsg(context,"请先登录支付宝");
            return;
        }
        /*String QrCode = "alipays://platformapi/startapp?" +
                "appId=88886666&applaunchMode=3&canSearch=false&chatLoginId="
                +Main2Activity.currAlipayLoginId+
                "&chatUserId=" + Main2Activity.currAlipayUserId +
                "&chatUserType=1&entryMode=personalS%E2%80%A6at&schemaMode=portalInside&target=personal&money=" +
                amount + "&amount=" +
                amount + "&remark=" + mark;*/
        Map<String,String> map = new HashMap<>();
        map.put("alipayUserId",Main2Activity.currAlipayUserId);
        map.put("alipayLoginId",Main2Activity.currAlipayLoginId);

        map.put("nickname",Main2Activity.currNickName);
        map.put("realName",Main2Activity.currRealName);
        map.put("userAvatar",Main2Activity.currUserAvatar);

        Gson gson = new Gson();
        String qrCodeTxt = gson.toJson(map);

        QrBean qrBean = new QrBean();
        qrBean.setChannel(channel);
        // mark为服务器订单号
        qrBean.setMark_sell(mark);
        qrBean.setUrl(qrCodeTxt);
        qrBean.setMoney(money);
        Intent broadCastIntent = new Intent()
                .putExtra("data", qrBean.toString())
                .setAction(HookConstants.RECEIVE_QR_ALIPAY);


        context.sendBroadcast(broadCastIntent);
    }


    /**
     * @param context
     * @param money   金额，单位元
     * @param mark    收款备注，最长30个字符，不能为空
     */

    public void creatWechatQr(Context context, String money, String mark) {
        if (money == null || TextUtils.isEmpty(mark)) {
            return;
        }

        if (mark.length() > 30 || Double.valueOf(money) > 300000 || Double.valueOf(money) < 1) {
            return;
        }
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(HookConstants.WECHAT_CREAT_QR);
        broadCastIntent.putExtra("mark", mark);
        broadCastIntent.putExtra("money", money);
        context.sendBroadcast(broadCastIntent);
    }


    public void creatUnionpayQr(final String tradeNo,final Context context, final String channel,final String money, final String mark) {

        Log.d("arik","准备生成二维码channel="+channel+",money="+money+",mark="+mark);
        if (money == null || TextUtils.isEmpty(mark)) {
            return;
        }
        Long fenMoney = MoneyUtil.yuanToFen(money);
        if (mark.length() > 30 || fenMoney > 30000000L || fenMoney < 1L) {
            return;
        }
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(HookConstants.UNIONPAY_CREAT_QR);
        broadCastIntent.putExtra("channel", channel);
        broadCastIntent.putExtra("mark", mark);
        broadCastIntent.putExtra("money", money);
        broadCastIntent.putExtra("tradeNo", tradeNo);
        context.sendBroadcast(broadCastIntent);

       /* Message message = new Message();
        message.what = 4;
        message.obj = channel+"-"+money + "-" + mark;
        UnionPayHook.sendMessage(message);*/

    }


    /**
     * 通过支付宝APP得到web访问的Cookies数据
     * 因为全是static方法，还是很方便获取的
     *
     * @param paramClassLoader
     * @return 成功返回cookies，失败返回空文本，非null
     */
    /*public static String getAlipayCookieStr(ClassLoader paramClassLoader) {
        XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alipay.mobile.common.transportext.biz.appevent.AmnetUserInfo", paramClassLoader), "getSessionid", new Object[0]);
        Context localContext = (Context) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alipay.mobile.common.transportext.biz.shared.ExtTransportEnv", paramClassLoader), "getAppContext", new Object[0]);
        if (localContext != null) {
            if (XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alipay.mobile.common.helper.ReadSettingServerUrl", paramClassLoader), "getInstance", new Object[0]) != null) {
                return (String) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alipay.mobile.common.transport.http.GwCookieCacheHelper", paramClassLoader), "getCookie", new Object[]{".alipay.com"});
            }
            LogUtils.show("支付宝订单Cookies获取异常");
            return "";
        }
        LogUtils.show("支付宝Context获取异常");
        return "";
    }*/


    /**
     * 通过网络请求获取最近的20个订单号
     * 把最近xx分钟内的订单传号传给getAlipayTradeDetail函数处理
     *
     * @param context
     * @param cookies
     */
    /*public static void dealAlipayWebTrade(final Context context, final String cookies) {
        long l = System.currentTimeMillis() + 200000;//怕手机的时间比支付宝慢了点，刚产生的订单就无法获取到
        String getUrl = "https://mbillexprod.alipay.com/enterprise/simpleTradeOrderQuery.json?beginTime=" + (l - 864000000L)
                + "&limitTime=" + l + "&pageSize=20&pageNum=1&channelType=ALL";
        StringRequestGet request = new StringRequestGet(getUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    *//*JSONObject jsonObject = JSON.parseObject(response);
                    List<AliBillList> aliBillLists = jsonObject.getJSONObject("result")
                            .getJSONArray("list").toJavaList(AliBillList.class);

                    SaveUtils saveUtils = new SaveUtils();
                    List<String> list = saveUtils.getJsonArray(SaveUtils.BILL_LIST_LAST, String.class);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    for (AliBillList aliBillList : aliBillLists) {
                        //20分钟前的订单就忽略
                        if (System.currentTimeMillis() - aliBillList.getGmtCreateStamp().getTime() > ALIPAY_BILL_TIME) {
                            break;
                        }

                        //首次，或者上次一样，就返回
                        if (list.contains(aliBillList.getTradeNo())) {
                            continue;//最新的订单都已经处理过，那就直接返回
                        }
                        list.add(aliBillList.getTradeNo());
                        getAlipayTradeDetail(context, aliBillList.getTradeNo()
                                , formatMoneyToCent(aliBillList.getTotalAmount() + "")
                                , cookies);
                    }
                    if (list.size() > 100) {
                        list.subList(0, 50).clear();
                    }
                    saveUtils.putJson(SaveUtils.BILL_LIST_LAST, list).commit();*//*
                } catch (Exception e) {
                    LogUtils.show("支付宝订单获取网络错误" + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.show("支付宝订单获取网络错误，请不要设置代理");
            }
        });

        String dataNow = new SimpleDateFormat("yyyy-MM-dd").format(new Date(l));
        String dataLastDay = new SimpleDateFormat("yyyy-MM-dd").format(new Date(l - 864000000L));

        request.addHeaders("Cookie", cookies)
                .addHeaders("Referer", "https://render.alipay.com/p/z/merchant-mgnt/simple-order.html?beginTime="
                        + dataLastDay + "&endTime=" + dataNow + "&fromBill=true&channelType=ALL");

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
        queue.start();
    }*/


    /**
     * 获取指定订单号的订单信息，如果是已收款状态，则发送给服务器，
     * 失败的会自动加数据库以后补发送。
     *
     * @param context
     * @param tradeNo
     * @param money   单位为分
     * @param cookies
     */
    /*private static void getAlipayTradeDetail(Context context, final String tradeNo, final int money, String cookies) {
        String getUrl = "https://tradeeportlet.alipay.com/wireless/tradeDetail.htm?tradeNo=" + tradeNo + "&source=channel&_from_url=https%3A%2F%2Frender.alipay.com%2Fp%2Fz%2Fmerchant-mgnt%2Fsimple-order._h_t_m_l_%3Fsource%3Dmdb_card";
        StringRequestGet request = new StringRequestGet(getUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String html = response.toLowerCase();
                    html = html.replace(" ", "")
                            .replace("\r", "")
                            .replace("\n", "")
                            .replace("\t", "");
                    html = getMidText(html, "\"id=\"j_logourl\"/>", "j_maskcode\"class=\"maskcodemain\"");

                    String tmp;
                    QrBean qrBean = new QrBean();
                    qrBean.setChannel(QrBean.ALIPAY);
                    qrBean.setOrder_id(tradeNo);

                    tmp = getMidText(html, "<divclass=\"am-flexbox\">当前状态</div>", "<divclass=\"am-list-itemtrade-info-item\">");
                    qrBean.setMark_buy(getMidText(tmp, "<divclass=\"trade-info-value\">", "</div>"));

                    tmp = getMidText(html, "<divclass=\"am-flexbox-item\">说</div><divclass=\"am-flexbox-item\">明", "<divclass=\"am-list-itemtrade-info-item\">");
                    qrBean.setMark_sell(getMidText(tmp, "<divclass=\"trade-info-value\">", "</div"));

                    //tmp = getMidText(html, "am-flexbox-item\">金</div><divclass=\"am-flexbox-item\">额", "<divclass=\"am-list-itemtrade-info-item\">");
                    //Float money = Float.valueOf(getMidText(tmp, "<divclass=\"trade-info-value\">", "</div")) * 100;
                    qrBean.setMoney(money);

                    if (TextUtils.isEmpty(qrBean.getMark_sell())
                            || !qrBean.getMark_buy().contentEquals("已收款")) {
                        return;
                    }
                    ReceiverMain.setmLastSucc(0);
                    LogUtils.show("支付宝发送支付成功任务：" + tradeNo + "|" + qrBean.getMark_sell() + "|" + qrBean.getMoney());
                    new ApiBll().payQR(qrBean);
                } catch (Exception ignore) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.show("支付宝订单详情获取错误：" + tradeNo + "-->" + error.getMessage());
            }
        });

        request.addHeaders("Cookie", cookies);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
        queue.start();
    }*/


    /**
     * 获取指定文本的两指定文本之间的文本
     *
     * @param text
     * @param begin
     * @param end
     * @return
     */
    public static String getMidText(String text, String begin, String end) {
        try {
            int b=0;
            if(begin!=""){
                b = text.indexOf(begin) + begin.length();
            }
            int e = text.indexOf(end, b);
            return text.substring(b, e);
        } catch (Exception e) {
            Log.d("arik",e.getMessage());
            return "";
        }
    }
	
    /**
     * 格式化金钱，把元变为分的单位
     *
     * @param money
     * @return
     */
    public static Integer formatMoneyToCent(String money) {
        return Integer.valueOf(new DecimalFormat("#").format(Float.valueOf(money.trim()) * 100));
    }

    /**
     * 格式化金钱，把分变为元的单位
     *
     * @param money
     * @return
     */
    public static String formatMoneyToYuan(String money) {
        String yuan = new DecimalFormat("#.00").format(Float.valueOf(money.trim()) / 100f);
        if (yuan.startsWith(".")) {
            yuan = "0" + yuan;
        }
        return yuan;
    }
	
}
