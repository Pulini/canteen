package com.pzx.canteen.http;

import com.pzx.canteen.listener.OnHttpListener;
import com.pzx.canteen.listener.OnWebServiceCallBack;
import com.pzx.canteen.utils.JsonUtil;

import java.util.HashMap;




/**
 * File Name : DoWebService
 * Created by : PanZX on 2020/03/14
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：开启webservice接口访问
 */
public class DoWebService {
    OnHttpListener WC;

    public static final String WEB_BASE_ERPFORANDROIDSTOCKSERVER = "http://geapp.goldemperor.com:8012/ErpForAndroidStockServer.asmx";
    public static final String WEB_ERPFORAPPSERVER = "http://geapp.goldemperor.com:8012/ErpForAppServer.asmx";

    public static final String WEB_GETDATATABLEACCOUNTSUIT = "GetDataTableAccountSuit";
    public static final String WEB_GETORGANIZATION = "GetOrganization";
    public static final String WEB_NEWPHONELOGIN = "NewPhoneLogin";
    public static final String WEB_UPDATECANTEENQRCARD = "UpdateCanteenQRCard";

    /**
     * 初始化
     * @param hl
     */
    public DoWebService(OnHttpListener hl) {
        this.WC = hl;
    }

    /**
     * 获取账套
     */
    public void GetDataTableAccountSuit() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("suitID", "1");
        WebService.callWebService(
                WEB_BASE_ERPFORANDROIDSTOCKSERVER,
                WEB_GETDATATABLEACCOUNTSUIT,
                hashMap,
                new OnWebServiceCallBack() {
                    @Override
                    public void callBack(String result) {
                        WC.Result(JsonUtil.GetDataTableAccountSuit(WEB_GETDATATABLEACCOUNTSUIT,result));
                    }
                }
        );
    }

    /**
     * 获取组织
     * @param paramString
     */
    public void GetOrganization(String paramString) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("suitID", paramString);
        WebService.callWebService(
                WEB_BASE_ERPFORANDROIDSTOCKSERVER,
                WEB_GETORGANIZATION,
                hashMap,
                new OnWebServiceCallBack() {
                    @Override
                    public void callBack(String result) {
                        WC.Result(JsonUtil.GetOrganization(WEB_GETORGANIZATION,result));
                    }
                }
        );
    }

    /**
     * 登录
     * @param accountSuitID     账套ID
     * @param organizeID        组织ID
     * @param phone             手机号码
     * @param password          密码
     */
    public void NewLogin(String accountSuitID, String organizeID, String phone, String password) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("accountSuitID", accountSuitID);
        hashMap.put("organizeID", organizeID);
        hashMap.put("phone", phone);
        hashMap.put("password", password);
        hashMap.put("code", "999999");
        WebService.callWebService(
                WEB_ERPFORAPPSERVER,
                WEB_NEWPHONELOGIN,
                hashMap,
                new OnWebServiceCallBack() {
                    @Override
                    public void callBack(String result) {
                        WC.Result(JsonUtil.NewPhoneLogin(WEB_NEWPHONELOGIN,result));
                    }
                }
        );
    }

    /**
     * 提交餐券
     * @param CanteenQRCardAddress  员工ID
     * @param CanteenQRCardID       餐券ID
     */
    public void submit(String CanteenQRCardID,String CanteenQRCardAddress) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("CanteenQRCardID", CanteenQRCardID);
        hashMap.put("CanteenQRCardAddress", CanteenQRCardAddress);
        WebService.callWebService(
                WEB_ERPFORAPPSERVER,
                WEB_UPDATECANTEENQRCARD,
                hashMap,
                new OnWebServiceCallBack() {
                    @Override
                    public void callBack(String result) {
                        WC.Result(JsonUtil.UpdateCanteenQRCord(WEB_UPDATECANTEENQRCARD,result));
                    }
                }
        );
    }
}
