package ink.z31.liverprotector.game;


import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ink.z31.liverprotector.bean.CheckVersionBean;
import ink.z31.liverprotector.bean.PathBean;
import ink.z31.liverprotector.bean.PathConfigBean;
import ink.z31.liverprotector.exception.HmException;
import ink.z31.liverprotector.util.Config;
import ink.z31.liverprotector.util.Encode;
import ink.z31.liverprotector.util.ListUtil;
import ink.z31.liverprotector.util.Requests;


public class NetSender {
    private static NetSender netSender = new NetSender();
    private NetSender(){}  // 私有构造器禁止实例化
    public static NetSender getInstance(){  // 获取实例
        return netSender;
    }
    private static final String TAG = "NetSender";


    public CheckVersionBean checkVersion() {
        String url = "http://www.simonkimi.top/pe/version.json";
        Requests requests = new Requests.Builder()
                .url(url)
                .get()
                .build()
                .execute();
        return JSON.parseObject(requests.text, CheckVersionBean.class);
    }

    public HashMap<String, PathConfigBean> getPath() {
        String url = "http://www.simonkimi.top/pe/path.json";
        Requests requests = new Requests.Builder()
                .url(url)
                .get()
                .build()
                .execute();
        Log.i(TAG, "下载配置:" + requests.text);
        PathBean bean = JSON.parseObject(requests.text, PathBean.class);
        for (String key: bean.path.keySet()) {
            Log.i(TAG, key + JSON.toJSONString(bean.path.get(key)));
        }
        return bean.path;
    }

    /**
     * 登录的第一次获取的init数据,貌似没有什么用
     * @return 没有用的String
     */
    public String loginInitConfig(){
        String url = Config.loginApiHead + "1.0/get/initConfig/@self";
        Requests requests = new Requests.Builder()
                .url(url)
                .post("{}")
                .build()
                .execute();
        return requests.text;
    }

    /**
     * 验证token是否过期
     * @param data token数据
     * @return 服务器返回的String
     */
    public String loginUserInfo(String data){
        String url = Config.loginApiHead + "1.0/get/userInfo/@self";
        Requests requests = new Requests.Builder()
                .url(url)
                .post(data)
                .build()
                .execute();
        return requests.text;
    }


    /**
     * 由用户名密码判断是否是正确的
     * 如果是错的会返回errmsg,error不是0
     * @param data 帐号密码
     * @return 服务器返回的String
     */
    public String loginLogin(String data){
        String url = Config.loginApiHead + "1.0/get/login/@self";
        Requests requests = new Requests.Builder()
                .url(url)
                .post(data)
                .build()
                .execute();
        return requests.text;
    }


    /**
     * 第一次登录由token获取serverList
     * @param token token
     * @return String 服务数据
     * @throws HmException 错误信息
     */
    public String gameLogin(String token) throws HmException {
        String url = Config.loginHead + "index/hmLogin/" + token + this.getUrlEnd();
        Requests requests = new Requests.Builder()
                .url(url)
                .get()
                .zlib()
                .build()
                .execute();
        String data = requests.text;
        HmException.errorFind("index/hmLogin/", data);
        return data;
    }


    /**
     * 第一次登录向服务器发送设备信息
     * @param userId 用户的uid
     * @param phoneType 设备信息
     * @return String 服务数据
     * @throws HmException 错误代码
     */
    public String indexLogin(String userId,  String phoneType) throws HmException {
        String url = Config.host + String.format("index/login/%s?&%s", userId, phoneType) + this.getUrlEnd();
        Requests requests = new Requests.Builder()
                .url(url)
                .get()
                .zlib()
                .build()
                .execute();
        String data = requests.text;
        HmException.errorFind("index/login/", data);
        return data;
    }


    /**
     * 游戏启动时候初始化数据
     * @return 用户等级,uid,船只,装备等一大堆信息
     * @throws HmException 错误信息
     */
    public String apiInitGame() throws HmException {
        String url = Config.host + "api/initGame?&crazy=0" + this.getUrlEnd();
        Requests requests = new Requests.Builder().url(url).get().zlib().build().execute();
        String data = requests.text;
        HmException.errorFind("api/initGame", data);
        return data;
    }


    /**
     * 获取pve的点数信息
     * @return pve的关卡编号,每个点的配置信息
     * @throws HmException 错误信息
     */
    public String pveGetPveData() throws HmException {
        String url = Config.host + "pve/getPveData/" + this.getUrlEnd();
        Requests requests = new Requests.Builder()
                .url(url)
                .get()
                .zlib()
                .build()
                .execute();
        String data = requests.text;
        HmException.errorFind("pve/getPveData/", data);
        return data;
    }

    /**
     * 获取活动的点数信息
     * @return 活动的关卡编号,每个点的配置信息
     */
    public String peventGetPveData() {
        try {
            String url = Config.host + "pevent/getPveData/" + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("pevent/getPveData/", data);
            return data;
        } catch (Exception e) {
            Log.e(TAG, "获取活动数据失败, 可能不在活动期间");
        }
        return null;
    }


    /**
     * 不知道干什么的,可能是活动关卡
     * @return 未知
     * @throws HmException 错误信息
     */
    public String bseaGetData() throws HmException {
        String url = Config.host + "bsea/getData/" + this.getUrlEnd();
        Requests requests = new Requests.Builder()
                .url(url)
                .get()
                .zlib()
                .build()
                .execute();
        String data = requests.text;
        HmException.errorFind("bsea/getData/", data);
        return data;
    }


    /**
     * 食堂食谱信息
     * @return 厨房的各种信息
     * @throws HmException 错误信息
     */
    public String liveGetUserInfo() throws HmException {
        String url = Config.host + "live/getUserInfo" + this.getUrlEnd();
        Requests requests = new Requests.Builder()
                .url(url)
                .get()
                .zlib()
                .build()
                .execute();
        String data = requests.text;
        HmException.errorFind("live/getUserInfo", data);
        return data;
    }


    /**
     * 活动信息以及签到信息
     * @return 活动信息以及签到信息
     * @throws HmException 错误信息
     */
    public String activeGetUserData() throws HmException {
        String url = Config.host + "active/getUserData/" + this.getUrlEnd();
        Requests requests = new Requests.Builder()
                .url(url)
                .get()
                .zlib()
                .build()
                .execute();
        String data = requests.text;
        HmException.errorFind("active/getUserData/", data);
        return data;
    }

    /**
     * 貌似是哪些点已经走过了
     * @return pveLevel/passedNodes点数信息
     * @throws HmException 错误信息
     */
    public String pveGetUserData() throws HmException {
        String url = Config.host + "pve/getUserData/" + this.getUrlEnd();
        Requests requests = new Requests.Builder()
                .url(url)
                .get()
                .zlib()
                .build()
                .execute();
        String data = requests.text;
        HmException.errorFind("pve/getUserData/", data);
        return data;
    }


    /**
     * 可以进行的战役点数,以及剩余的战役数量
     * @return canCampaignChallengeLevel/passInfo
     * @throws HmException 错误信息
     */
    public String campaignGetUserData() throws HmException {
        try {
            String url = Config.host + "campaign/getUserData/" + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("campaign/getUserData/", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"campaignGetUserData错误:" + e.toString());
            throw e;
        }

    }


    /**
     * 获取游戏特大init数据
     * @return 数据的String类型
     * @throws HmException 错误信息
     */
    public String getInitData()throws HmException {
        String url = Config.resUrl + this.getUrlEnd();
        try {
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;

            HmException.errorFind("init", data);
            return data;
        }catch (HmException e){
            Log.e(TAG, "获取init数据出错:" + e.toString());
            throw e;
        }
    }

    /**
     * 补给船只
     * @param boats 需要补给船只的List
     * @return boat/supplyBoats
     * @throws HmException 错误信息
     */
    public String supplyBoats(List<Integer> boats) throws HmException {
        try {
            String p = ListUtil.listJoinInt(boats, ",");
            String url = Config.host + String.format("boat/supplyBoats/[%s]/0/0/", p) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("boat/supplyBoats/[%s]/0/0/", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"boat/supplyBoats错误:" + e.toString());
            throw e;
        }
    }

    /**
     * 锁定船只
     * @param id 需要补给船只的List
     * @return boat/lock
     * @throws HmException 错误信息
     */
    public void boatLock(int id) throws HmException {
        try {
            String url = Config.host + String.format("boat/lock/%s/", id) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("boat/lock", data);
        }catch (HmException e){
            Log.e(TAG,"boat/lock错误:" + e.toString());
            throw e;
        }
    }

    /**
     * 分解船只
     * @param boats 需要分解的List
     * @param isUnload 是否需要卸装备
     * @return dock/dismantleBoat
     * @throws HmException 错误信息
     */
    public String dismantleBoat(List<Integer> boats, boolean isUnload) throws HmException {
        try {
            String [] p = {ListUtil.listJoinInt(boats, ","), isUnload ? "0": "1"};
            String url = Config.host + String.format("dock/dismantleBoat/[%s]/%s/", p[0], p[1]) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("dock/dismantleBoat/", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"dock/dismantleBoat错误:" + e.toString());
            throw e;
        }
    }

    /**
     * 获取远征的结果
     * @param map 远征的地图
     * @return explore/getResult
     * @throws HmException 错误信息
     */
    public String exploreGetResult(String map) throws HmException {
        try {
            String url = Config.host + String.format("explore/getResult/%s/", map) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("explore/getResult/", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"explore/getResult错误:" + e.toString());
            throw e;
        }
    }

    /**
     * 开始一个新的远征
     * @param fleet 远征的队伍
     * @param map 远征的地图
     * @return explore/start
     * @throws HmException 错误信息
     */
    public String exploreStart(String fleet, String map) throws HmException {
        try {
            String url = Config.host + String.format("explore/start/%s/%s/", fleet, map) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("explore/start/", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"explore/start错误:" + e.toString());
            throw e;
        }
    }

    /**
     * 出浴船只
     * @param dock 船只在澡堂中的索引
     * @param ship 船只id
     * @return String
     * @throws HmException 错误
     */
    public String repairComplete(int dock, String ship) throws HmException {
        try {
            String url = Config.host + String.format(Locale.CHINA, "boat/repairComplete/%d/%s/", dock, ship) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("boat/repairComplete/", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"explore/start错误:" + e.toString());
            throw e;
        }
    }

    /**
     * 泡澡船只
     * @param ship 船只的id编号
     * @return String
     * @throws HmException 错误
     */
    public String boatRepair(int ship) throws HmException {
        try {
            String url = Config.host + String.format(Locale.CHINA, "boat/repair/%d/0/", ship) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("boat/repair/", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"boat/repair错误:" + e.toString());
            throw e;
        }
    }

    /**
     * 搓澡船只
     * @param ship 船只编号
     * @return 返回值
     * @throws HmException 错误
     */
    public String boatRubdown(int ship) throws HmException {
        try {
            String url = Config.host + String.format(Locale.CHINA, "boat/rubdown/%d", ship) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("boat/rubdown/", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"boat/rubdown错误:" + e.toString());
            throw e;
        }
    }

    /**
     * 补给船只
     * @param ships 船只列表
     * @return 服务器返回数据
     * @throws HmException 错误代码
     */
    public String boatSupplyBoats(List<Integer> ships) throws HmException {
        try {
            String url = Config.host + String.format(Locale.CHINA, "boat/supplyBoats/[%s]/0/0/", ListUtil.listJoinInt(ships, ",")) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("boat/supplyBoats/", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"boat/supplyBoats:" + e.toString());
            throw e;
        }
    }

    /**
     * 快速修理所有船只
     * @param ships 需要修理的船只
     * @return 服务器数据
     * @throws HmException 参数错误
     */
    public String boatInstantRepairShips(List<Integer> ships) throws HmException {
        try {
            String url = Config.host + String.format(Locale.CHINA, "boat/instantRepairShips/[%s]/", ListUtil.listJoinInt(ships, ",")) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("boat/instantRepairShips/", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"boat/instantRepairShips:" + e.toString());
            throw e;
        }
    }

    /**
     * 开始战斗
     * @param head 战斗请求头部
     * @param map 地图
     * @param fleet 队伍
     * @throws HmException 服务器错误信息
     */
    public void battleChallenge(String head, String map, String fleet) throws HmException {
        try {
            String url;
            if (head.equals("pve")){
                url = Config.host + String.format(Locale.CHINA, "%s/cha11enge/%s/%s/0/", head, map, fleet) + this.getUrlEnd();
            } else {
                url = Config.host + String.format(Locale.CHINA, "%s/challenge/%s/%s/0/", head, map, fleet) + this.getUrlEnd();
            }
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("challenge", data);
        }catch (HmException e){
            Log.e(TAG,"battle/Challenge出错:" + e.toString());
            throw e;
        }
    }

    /**
     * 下一点
     * @param head 请求头部
     * @return 返回值
     * @throws HmException 服务器错误
     */
    public String battleNewNext(String head) throws HmException {
        try {
            String url = Config.host + String.format(Locale.CHINA, "%s/newNext/", head) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("newNext", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"battle/newNext:" + e.toString());
            throw e;
        }
    }

    public String battleSpy(String head) throws HmException {
        try {
            String url = Config.host + String.format(Locale.CHINA, "%s/spy/", head) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("spy", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"battle/spy:" + e.toString());
            throw e;
        }
    }

    public String battleDealto(String head, String node, String fleet, String format) throws HmException {
        try {
            String url = Config.host + String.format(Locale.CHINA, "%s/dealto/%s/%s/%s/", head, node, fleet, format) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("dealto", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"battle/spy:" + e.toString());
            throw e;
        }
    }

    public String battleGetWarResult(String head, boolean nightFight) throws HmException {
        try {
            String url = Config.host + String.format(Locale.CHINA, "%s/getWarResult/%d/", head, nightFight? 1: 0) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("battle/getWarResult:", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"battle/getWarResult:" + e.toString());
            throw e;
        }
    }

    public String battleSkipWar(String head) throws HmException {
        try {
            String url = Config.host + String.format(Locale.CHINA, "%s/SkipWar/", head) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("battle/SkipWar", data);
            return data;
        }catch (HmException e){
            Log.e(TAG,"battle/SkipWar:" + e.toString());
            throw e;
        }
    }

    public void setFleet(int fleet) throws HmException  {
        try {
            String url = Config.host + String.format(Locale.CHINA, "pevent/setFleet/%d/", fleet) + this.getUrlEnd();
            Requests requests = new Requests.Builder()
                    .url(url)
                    .get()
                    .zlib()
                    .build()
                    .execute();
            String data = requests.text;
            HmException.errorFind("pevent/setFleet", data);
        }catch (HmException e){
            Log.e(TAG,"pevent/setFleet:" + e.toString());
            throw e;
        }
    }









    private String getUrlEnd(){
        String key = "ade2688f1904e9fb8d2efdb61b5e398a";
        long time = new Date().getTime() * 1000;
        String md5 = Encode.stringToMD5(time + key);
        return String.format("&t=%s&e=%s&gz=1&market=2&channel=%s&version=%s", time, md5, Config.channel, Config.version);
    }


}
