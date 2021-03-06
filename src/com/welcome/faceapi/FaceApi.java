package com.welcome.faceapi;

import com.welcome.form.FaceForm;
import com.welcome.utils.GsonUtils;
import com.welcome.utils.HttpUtil;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tsening Chu
 * @version 1.0
 * @date 2018/11/30
 */
public class FaceApi {

    private static String accessToken = "";

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        FaceApi.accessToken = accessToken;
    }

    public FaceApi(){
        if(FaceApi.getAuth()){
            System.out.println("获取accessToken成功");
        }else {
            System.out.println("获取accessToken失败");
        }
    }

    public static Boolean getAuth() {
        // 官网获取的 API Key 更新为你注册的
        String clientId = "UlhoPOcHF3NiYh1vFK55sYEM";
        // 官网获取的 Secret Key 更新为你注册的
        String clientSecret = "SrIVUmDrRwb1WzCOcLQGPaQ6E66T6KLd";
        return getAuth(clientId, clientSecret);
    }

    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */

    private static Boolean getAuth(String ak, String sk) {
        System.out.println("===============================");
        System.out.println("       开始请求AccessToken");
        System.out.println("===============================");
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            System.err.println("result:" + result);
            JSONObject jsonObject = JSONObject.fromObject(result);
            FaceApi.accessToken = jsonObject.getString("access_token");
            return true;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }

    public static String faceAdd(FaceForm faceForm) {
        System.out.println("===============================");
        System.out.println("           开始 注册人脸");
        System.out.println("===============================");
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", faceForm.getImage());
            map.put("group_id", faceForm.getGroupId());
            map.put("user_id", faceForm.getUserId());
            map.put("user_info", faceForm.getUserInfo());
            map.put("liveness_control", faceForm.getLivenessControl());
            map.put("image_type", faceForm.getImageType());
            map.put("quality_control", faceForm.getQualityControl());

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = FaceApi.accessToken;

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String faceSearch(FaceForm faceForm) {
        System.out.println("===============================");
        System.out.println("          开始 人脸库搜索");
        System.out.println("===============================");
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", faceForm.getImage());
            map.put("liveness_control", faceForm.getLivenessControl());
            map.put("group_id_list", faceForm.getGroupId());
            map.put("image_type", faceForm.getImageType());
            map.put("quality_control", faceForm.getQualityControl());

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = FaceApi.accessToken;

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String faceUpdate(FaceForm faceForm) {
        System.out.println("===============================");
        System.out.println("          开始 人脸更新");
        System.out.println("===============================");
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", faceForm.getImage());
            map.put("group_id", faceForm.getGroupId());
            map.put("user_id", faceForm.getUserId());
            map.put("user_info", faceForm.getUserInfo());
            map.put("liveness_control", faceForm.getLivenessControl());
            map.put("image_type", faceForm.getImageType());
            map.put("quality_control", faceForm.getQualityControl());

            String param = GsonUtils.toJson(map);

            // 请求url
            String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/update";

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = FaceApi.accessToken;

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String groupAdd(String groupId) {
        System.out.println("===============================");
        System.out.println("          开始 增加用户组");
        System.out.println("===============================");
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/add";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("group_id", groupId);

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = FaceApi.accessToken;

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUserInfo(FaceForm faceForm) {
        System.out.println("===============================");
        System.out.println("         开始请求用户信息");
        System.out.println("===============================");
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/get";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("user_id", faceForm.getUserId());
            map.put("group_id", faceForm.getGroupId());

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = FaceApi.accessToken;

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String groupDelete(String groupId) {
        // 请求url
        try {
            System.out.println("===============================");
            System.out.println("          开始 删除用户组");
            System.out.println("===============================");
            String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/delete";
            Map<String, Object> map = new HashMap<>();
            map.put("group_id", groupId);

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = FaceApi.accessToken;

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
