package com.pzx.canteen.utils;

import com.google.gson.Gson;
import com.pzx.canteen.model.AccountSuitModel;
import com.pzx.canteen.model.LoginModel;
import com.pzx.canteen.model.OrganizationModel;
import com.pzx.canteen.model.ResultModel;
import com.thoughtworks.xstream.XStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;



/**
 * File Name : JsonUtil
 * Created by : PanZX on 2020/03/14
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：json数据解析工具
 */
public class JsonUtil {
    /**
     * 解析账套数据
     * @param MethodName    接口名
     * @param Result        接口返回值
     * @return
     */
    public static ResultModel GetDataTableAccountSuit(String MethodName, String Result) {
        ResultModel RM = new ResultModel();
        RM.setName(MethodName);
        RM.setSuccess(false);
        if (Result == null || "".equals(Result)) {
            RM.setData("接口无返回");
        }else{
            try {
                Result = URLDecoder.decode(Result, "UTF-8");
                LOG.e("Result="+ Result);
                XStream xStream = new XStream();
                xStream.processAnnotations(AccountSuitModel.class);
                AccountSuitModel ASM = (AccountSuitModel) xStream.fromXML(Result);
                RM.setSuccess(true);
                RM.setData(ASM);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                RM.setData("数据解码异常");
            }
        }
        return RM;
    }

    /**
     * 解析组织数据
     * @param MethodName    接口名
     * @param Result        接口返回值
     * @return
     */
    public static ResultModel GetOrganization(String MethodName, String Result) {
        ResultModel RM = new ResultModel();
        RM.setName(MethodName);
        RM.setSuccess(false);
        if (Result == null || "".equals(Result)) {
            RM.setData("接口无返回");
        }else{
            try {
                Result = URLDecoder.decode(Result, "UTF-8");
                XStream xStream = new XStream();
                xStream.processAnnotations(OrganizationModel.class);
                OrganizationModel OM = (OrganizationModel) xStream.fromXML(Result);
                RM.setSuccess(true);
                RM.setData(OM);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                RM.setData("数据解码异常");
            }
        }
        return RM;
    }

    /**
     *  解析登录
     * @param MethodName    接口名
     * @param Result        接口返回值
     * @return
     */
    public static ResultModel NewPhoneLogin(String MethodName, String Result) {
        ResultModel RM = new ResultModel();
        RM.setName(MethodName);
        RM.setSuccess(false);
        if (Result == null || "".equals(Result)) {
            RM.setData("接口无返回");
        } else {
            try {
                Result = URLDecoder.decode(Result, "UTF-8");
                JSONObject jsonObject = new JSONObject(Result);
                String ReturnType = jsonObject.getString("ReturnType");
                String ReturnMsg = jsonObject.getString("ReturnMsg");
                if ("success".equals(ReturnType)) {//给MES的接口，成功时type为S
                    RM.setSuccess(true);
                    RM.setData(
                            new Gson().fromJson(
                                    new JSONArray(ReturnMsg)
                                            .getJSONObject(0)
                                            .toString(),
                                    LoginModel.class
                            )
                    );
                }else{
                    RM.setData(ReturnMsg);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                RM.setData("数据解码异常");
            } catch (JSONException e) {
                e.printStackTrace();
                RM.setData("数据解析异常");
            }
        }
        return RM;
    }
    /**
     *  解析餐券提交
     * @param MethodName    接口名
     * @param Result        接口返回值
     * @return
     */
    public static ResultModel UpdateCanteenQRCord(String MethodName, String Result) {
        ResultModel RM = new ResultModel();
        RM.setName(MethodName);
        RM.setSuccess(false);
        if (Result == null || "".equals(Result)) {
            RM.setData("接口无返回");
        } else {
            try {
                Result = URLDecoder.decode(Result, "UTF-8");
                JSONObject jsonObject = new JSONObject(Result);
                String ReturnType = jsonObject.getString("ReturnType");
                String ReturnMsg = jsonObject.getString("ReturnMsg");
                if ("success".equals(ReturnType)) {//给MES的接口，成功时type为S
                    RM.setSuccess(true);
                    RM.setData(ReturnMsg);
                }else{
                    RM.setData(ReturnMsg);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                RM.setData("数据解码异常");
            } catch (JSONException e) {
                e.printStackTrace();
                RM.setData("数据解析异常");
            }
        }
        return RM;
    }

}
