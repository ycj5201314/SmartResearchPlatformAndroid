package Soap;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Map;

import Interface.RequestFunc;

/**
 * Created by Administrator on 2015/12/13.
 */
public class WebService {
    final String WEB_SERVICE_URL = "http://192.168.191.1:333/Service.asmx?wsdl";
    final String Namespace = "http://tempuri.org/";
    private RequestFunc ReqFunc;
    public String Result;


    public WebService(RequestFunc ReqFunc) {
        this.ReqFunc = ReqFunc;
    }

    public String CallWebService(String MethodName, Map<String, String> Params) {
        // 1、指定webservice的命名空间和调用的方法名
        SoapObject request = new SoapObject(Namespace, MethodName);
        // 2、设置调用方法的参数值，如果没有参数，可以省略，
        if (Params != null) {
            for (Object o : Params.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                request.addProperty((String) entry.getKey(), entry.getValue());
            }
        }
        // 3、生成调用Webservice方法的SOAP请求信息。该信息由SoapSerializationEnvelope对象描述
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = request;
        // c#写的应用程序必须加上这句
        envelope.dotNet = true;
        HttpTransportSE ht = new HttpTransportSE(WEB_SERVICE_URL);
        // 使用call方法调用WebService方法
        try {
            ht.call(null, envelope);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        try {
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            if (result != null) {
                Log.d("收到的回复", result.toString());
                return result.toString();
            }
        } catch (Exception e) {
            Log.e("发生错误", e.getMessage());
        }
        return null;
    }

    public void Request(Object... params) {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length == 2) {
                    return CallWebService((String) params[0], (Map<String, String>) params[1]);
                } else if (params != null && params.length == 1) {
                    return CallWebService((String) params[0], null);
                } else {
                    return null;
                }
            }


            @Override
            protected void onPostExecute(String result) {
                Result = result;
                //对回调信息进行相应操作
                ReqFunc.Func();
            }
        }.execute(params);
    }

}
