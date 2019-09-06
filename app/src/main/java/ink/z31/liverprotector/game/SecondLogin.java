package ink.z31.liverprotector.game;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ink.z31.liverprotector.exception.HmException;
import ink.z31.liverprotector.interfaces.SecondLoginCallBack;
import ink.z31.liverprotector.util.Config;
import ink.z31.liverprotector.util.Util;

public class SecondLogin {
    private static final String TAG = "SecondLogin";
    private SecondLogin(){}
    private static SecondLogin secondLogin = new SecondLogin();
    private static UserData userData = UserData.getInstance();
    public static SecondLogin getInstance(){
        return secondLogin;
    }
    // 使用的常量
    private static NetSender netSender = NetSender.getInstance();

    public void login(final SecondLoginCallBack callBack){
        try {
            //登录发送用户数据
            Map<String, String> userPhoneData = new HashMap<>();
            userPhoneData.put("client_version", Config.version);
            userPhoneData.put("phone_type", "mi+max");
            userPhoneData.put("phone_version", "5.1.1");
            userPhoneData.put("ratio", "1920*1080");
            userPhoneData.put("service", "CHINA+MOBILE");
            //设备随机数
            Random random = new Random(Integer.valueOf(Config.userId).longValue());
            StringBuilder udid = new StringBuilder();
            for (int i=0; i<15; i++) {
                udid.append(random.nextInt(10));
            }
            userPhoneData.put("udid", udid.toString());
            userPhoneData.put("source", "android");
            userPhoneData.put("affiliate", "WIFI");
            String data = "";
            for (String key: userPhoneData.keySet()) {
                data += (key + "=" + userPhoneData.get(key) + "&");
            }
            data = data.substring(0, data.length() - 1);
            netSender.indexLogin(Config.userId, data);
            // 初始化用户基本信息
            String apiInitGame = netSender.apiInitGame();
            userData.parseUserData(apiInitGame);
            // 初始化点数信息
            userData.pveNodeGet(netSender.pveGetPveData());
            String pveData = netSender.peventGetPveData();
            if (pveData != null && pveData.length() > 10 && pveData.contains("{")) {
                userData.peventNodeGet(pveData);
            }
            callBack.onFinish();
        }catch (HmException e){
            Log.e(TAG, e.getMessage());
            callBack.onError(e.getMessage());
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            callBack.onError(Util.getErrMsg(e));
        }
    }
}
