package com.decawave.argomanager.scontroller.constant;

/**
 * Created by Hu_codeman on 2019/5/31.
 */

public class UrlConstant {
    /**
     * App后端url
     */
//    public static final String APP_BACK_END_IP = "192.168.1.160";
//    public static final String APP_BACK_END_PORT = "8080";
    public static final String APP_BACK_END_IP = "fudanse.club";
    public static final String APP_BACK_END_PORT = "80/sc";

    //用户登录1
    public static final String APP_BACK_END_USER_LOGIN_SERVICE = "user/login";

    public static final String APP_BACK_END_USER_TEST = "user/test";

    //保存位置信息
    public static final String APP_BACK_END_USER_SAVE_LOCATION= "user/saveLocation";

    //测试用--获取owls
    public static final String APP_BACK_END_TASKS_GET_OWLS= "task/getOwls";

    //测试用--获取bpmn
    public static final String APP_BACK_END_TASKS_GET_BPMN= "task/getBPMN";

    //保存任务
    public static final String APP_BACK_END_TASKS_SAVE_TASK= "task/saveTask";

    //获取所有任务列表
    public static final String APP_BACK_END_TASKS_GET_ONGOING_TASKS= "task/getOngoingTasks";

    //获取所有任务列表
    public static final String APP_BACK_END_DEVICE_SAVE_DEVICE_INFO= "device/saveDeivce";

    public static final String APP_BACK_END_DEVICE_SAVE_DEVICE_LOCATION= "device/saveDeivceLocation";


    public static String getAppBackEndServiceURL(String  service) {
        String serviceURL = String.format("http://%s:%s/%s", APP_BACK_END_IP, APP_BACK_END_PORT, service);
        return serviceURL;
    }

    /**
     * 本体库平台url
     */
    public static final String ONTOLOGY_IP = "47.100.23.182";//142
    public static final String ONTOLOGY_PORT = "8004";

    //用户登录
    public static final String ONTOLOGY_GET_OWLS = "query/compositeServiceQuery?serviceName=IfRoomEmptyService";

    public static String getOntologyServiceURL(String  service) {
        String serviceURL = String.format("http://%s:%s/%s", ONTOLOGY_IP, ONTOLOGY_PORT, service);
        return serviceURL;
    }

    /**
     * 流程执行引擎url
     */
    public static final String ACTIVITI_IP = "192.168.1.120";//142
    public static final String ACTIVITI_PORT = "8080";

    //用户登录
    public static final String ACTIVITI_RUN_BPMN_ENGINE= "runBpmnEngine";

    public static String getActivitiServiceURL(String  service) {
        String serviceURL = String.format("http://%s:%s/%s", ACTIVITI_IP, ACTIVITI_PORT, service);
        return serviceURL;
    }

    /**
     * 众包平台url
     */


    /**
     * 文件服务器
     */
    public static final String FILE_IP = "10.141.221.88";//142,148
    public static final String FILE_PORT = "12321";

    //上传设备发现裁剪图片
    public static final String FILE_ADD_DEVICE_IMAGE= "/upload/uploadImage";

    public static String getFlieServiceURL(String  service) {
        String serviceURL = String.format("http://%s:%s/%s", FILE_IP, FILE_PORT, service);
        return serviceURL;
    }

    /**
     * 拍立淘接口
     */
    public static final String PAILITAO_IP = "10.141.221.88";//142,148
    public static final String PAILITAO_PORT = "10300";

    //调用拍立淘查询设备信息
    public static final String PAILITAO_SEARCH_DEVICE_INFO= "sensing/search_info_from_Web";

    //调用拍立淘获取设备信息
    public static final String PAILITAO_GET_DEVICE_INFO= "sensing/get_search_result";

    public static String getPailitaoServiceURL(String  service) {
        String serviceURL = String.format("http://%s:%s/%s", PAILITAO_IP, PAILITAO_PORT, service);
        return serviceURL;
    }
}
