package com.pzx.canteen.http;

import com.pzx.canteen.listener.OnWebServiceCallBack;
import com.pzx.canteen.utils.LOG;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;
import org.ksoap2.transport.ServiceConnectionSE;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * File Name : WebService
 * Created by : PanZX on 2020/03/14
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： webservice请求工具类
 */
public class WebService {
    private static String WEBSERVER_NAMESPACE = "http://tempuri.org/";
    private  static MyHttpTransportSE ht;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static void callWebService(String url, final String methodName, HashMap<String, String> properties, final OnWebServiceCallBack webServiceCallBack) {
        LOG.e("Url=" + url);
        LOG.e("MethodName=" + methodName);
        LOG.E("map=" + properties.toString());
        // 创建MyHttpTransportSE对象，传递WebService服务器地址
        ht= new MyHttpTransportSE(url, 40000);
        // 创建SoapObject对象
        SoapObject soapObject = new SoapObject(WEBSERVER_NAMESPACE, methodName);

        // SoapObject添加参数
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            soapObject.addProperty(entry.getKey(), entry.getValue());
        }


        // 实例化SoapSerializationEnvelope，传入WebService的SOAP协议的版本号
        final SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        // 设置是否调用的是.Net开发的WebService
        soapEnvelope.bodyOut = soapObject;
        soapEnvelope.dotNet = true;
        soapEnvelope.setOutputSoapObject(soapObject);


        // 开启线程去访问WebService
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                {
                    String resultSoapObject = null;
                    try {
                        ht.call(WEBSERVER_NAMESPACE + methodName, soapEnvelope);
                        if (soapEnvelope.getResponse() != null) {
                            // 获取服务器响应返回的SoapObject
                            resultSoapObject = soapEnvelope.getResponse().toString();
                            LOG.E("result=\r\n" + resultSoapObject);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        // 将获取的消息利用Handler发送到主线程
                        webServiceCallBack.callBack(resultSoapObject);
                    }
                }
            }
        });
    }

   static class MyHttpTransportSE extends HttpTransportSE {
        private int timeout = 1000 * 60 * 2;
        public MyHttpTransportSE(String paramString, int paramInt) {
            super(paramString);
            this.timeout = paramInt;
        }

        protected ServiceConnection getServiceConnection() throws IOException {
            return new ServiceConnectionSE(this.url, this.timeout);
        }
    }
}
