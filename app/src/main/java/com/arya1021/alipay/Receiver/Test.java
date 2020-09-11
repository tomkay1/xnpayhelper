package com.arya1021.alipay.Receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Test {

    public static void main(String[] args) {

        String str = "{\"actionLink\":\"alipays://platformapi/startapp?appId=2018030502317554&query=channel%3Dcard\",\"actionName\":\"收款记录\",\"actionTitle\":\"商家服务·店员通\",\"assistMsg1\":\"今日第1笔收款，共计￥0.01\",\"assistMsg2\":\"点此查看收款明细\",\"bizMonitor\":\"{\"expireLink\":\"\",\"gmtCreate\":1569139857381,\"gmtValid\":1571731857374,\"hiddenSum\":\"0\",\"homePageTitle\":\"商家服务: ￥0.01 收款到账通知\",\"icon\":\"https://gw.alipayobjects.com/zos/mwalletmng/XYNZOTVpZxkryjGaSsoi.png\",\"id\":\"4bb13e31125016c9ae5717179ac33c5900816pep_MDailyBill_assistant_Push2088032721738162\",\"link\":\"alipays://platformapi/startapp?appId=2018030502317554&query=activeTab%3DBILL%26channel%3Dcard&page=pages%2Fclerk%2Fhome%2Findex&chInfo=ch_clerktone_card_money_notice\",\"linkName\":\"\",\"msgId\":\"4bb13e31125016c9ae5717179ac33c5900816\",\"msgType\":\"NOTICE\",\"operate\":\"UPDATE\",\"status\":\"\",\"templateCode\":\"pep_MDailyBill_assistant_Push\",\"templateId\":\"WALLET-BILL@mbill-pay-fwc-dynamic-v2\",\"templateName\":\"商家服务收款到账(店员)\",\"templateType\":\"S\",\"title\":\"商家服务·收款到账(店员)\",\"userId\":\"2088032721738162\"}\",\"cardLink\":\"alipays://platformapi/startapp?appId=2018030502317554&query=activeTab%3DBILL%26channel%3Dcard&page=pages%2Fclerk%2Fhome%2Findex&chInfo=ch_clerktone_card_money_notice\",\"content\":\"收款金额￥0.01\",\"contentList\":[{\"content\":\"今日第1笔收款，共计￥0.01\",\"isHighlight\":false,\"label\":\"汇总\"},{\"content\":\"点此查看收款明细\",\"isHighlight\":true,\"label\":\"备注\"}],\"expireLink\":\"https://render.alipay.com/p/f/fd-jblxfp45/pages/home/index.html\",\"gmtValid\":1571731857374,\"link\":\"alipays://platformapi/startapp?appId=2018030502317554&query=activeTab%3DBILL%26channel%3Dcard&page=pages%2Fclerk%2Fhome%2Findex&chInfo=ch_clerktone_card_money_notice\",\"mainAmount\":\"0.01\",\"mainTitle\":\"收款金额\",\"sceneExt\":{\"sceneTemplateId\":\"USER_DEFINED\",\"sceneTitle\":\"进入应用\",\"sceneType\":\"nativeApp\",\"sceneUrl\":\"alipays://platformapi/startapp?appId=2018030502317554&query=activeTab%3DBILL%26channel%3Dcard&page=pages%2Fclerk%2Fhome%2Findex&chInfo=ch_clerktone_card_money_notice\"},\"templateId\":\"WALLET-BILL@mbill-pay-fwc-dynamic-v2\"}";

        str = "{\"actionLink\":\"alipays://platformapi/startapp?appId=77700292&page=pages%2Findex%2Findex%3FdateType%3Dday%26startDate%3D2019-09-28&chInfo=fftxdayCard\",\"actionName\":\"我的收入统计\",\"actionTitle\":\"查看更多\",\"assistMsg1\":\"收款笔数1笔\",\"assistMsg2\":\"付款人数1人\",\"bizMonitor\":\"{\"expireLink\":\"\",\"gmtCreate\":1569723215515,\"gmtValid\":1572315215504,\"hiddenSum\":\"0\",\"homePageTitle\":\"商家服务: ￥1000.00二维码收款日报\",\"icon\":\"https://gw.alipayobjects.com/mdn/rms_a7fa41/afts/img/A*oj7STJnhYzYAAAAAAAAAAABkARQnAQ\",\"id\":\"de58f413533cf314f17658e88aaae58f00816pep_MDailyBill_top_Push22088032721738162\",\"link\":\"alipays://platformapi/startapp?appId=77700292&page=pages%2Findex%2Findex%3FdateType%3Dday%26startDate%3D2019-09-28&chInfo=fftxdayCard\",\"linkName\":\"\",\"msgId\":\"de58f413533cf314f17658e88aaae58f00816\",\"msgType\":\"NOTICE\",\"operate\":\"SEND\",\"status\":\"\",\"templateCode\":\"pep_MDailyBill_top_Push2\",\"templateId\":\"WALLET-BILL@mbill-rzy-card\",\"templateName\":\"商家服务收款通知\",\"templateType\":\"S\",\"title\":\"商家服务·收入统计\",\"userId\":\"2088032721738162\"}\",\"cardLink\":\"alipays://platformapi/startapp?appId=77700292&page=pages%2Findex%2Findex%3FdateType%3Dday%26startDate%3D2019-09-28&chInfo=fftxdayCard\",\"content\":\"9月28日共收款￥1000.00\",\"contentList\":[{\"content\":\"共￥1000.00，1笔\",\"isHighlight\":false,\"label\":\"收款\",\"linkUrl\":\"\"},{\"content\":\"顾客共1人，全部为新顾客\",\"isHighlight\":false,\"label\":\"顾客\",\"linkUrl\":\"\"},{\"content\":\"\",\"isHighlight\":false,\"label\":\"服务费\",\"linkUrl\":\"\"},{\"content\":\"点此查看收款明细\",\"isHighlight\":false,\"label\":\"备注\",\"linkUrl\":\"\"}],\"expireLink\":\"https://render.alipay.com/p/f/fd-jblxfp45/pages/home/index.html\",\"gmtValid\":1572315215504,\"link\":\"alipays://platformapi/startapp?appId=77700292&page=pages%2Findex%2Findex%3FdateType%3Dday%26startDate%3D2019-09-28&chInfo=fftxdayCard\",\"mainAmt\":\"1000.00\",\"mainDesc\":\"9月28日收款日报\",\"rightsId\":\"\",\"sessionId\":\"1569723215492\",\"templateId\":\"WALLET-BILL@mbill-rzy-card\",\"type\":\"day\",\"weekRange\":\"\"}";



        str = str.replace("\"bizMonitor\":\"","\"bizMonitor\":");
        str = str.replace("}\",\"cardLink\"","},\"cardLink\"");

        JSONObject jsonObject = JSON.parseObject(str);

        System.out.println("=====actionName==="+jsonObject.getString("actionName"));


        String ss = jsonObject.getString("assistMsg1");
        System.out.println(ss);

        System.out.println(ss.substring(0,ss.indexOf("收款")+1));

        System.out.println(jsonObject.toJSONString());
    }
}
