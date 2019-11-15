package com.mapscanner.mapscanner.baidubrain;

import com.mapscanner.mapscanner.utils.GsonUtils;
import com.mapscanner.mapscanner.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceRecogniseUtil {
    /**
     * 获取权限token
     * @return 返回示例：
     * {
     * "access_token": "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
     * "expires_in": 2592000
     * }
     */
    public static String getAuth() {
        // 官网获取的 API Key 更新为你注册的
        String clientId = "HZF10zpVFx4ctcRhOIGaeZg2";
        // 官网获取的 Secret Key 更新为你注册的
        String clientSecret = "gqaT7Svesnb9jgidKZAhWZx5yzLL4q1U";
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
    public static String getAuth(String ak, String sk) {
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
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }

    //人脸注册
    public static String add(String accessToken, String imgBase64, String userInfo) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
        try {
            JSONArray grouplist = groupGetlist(accessToken);
            String groupId = grouplist.get(0).toString();

            // 动态获取user_id
            JSONArray users = groupGetusers(accessToken, groupId);
            // 将int转为定长String
            int max = 0;
            for (int i=0; i < users.length(); i++) {
                int id = Integer.parseInt((String) users.get(i));
                if (id > max){
                    max = id;
                }
            }
            int id = max + 1;
            char temp[] = {'0','0','0','0','0','0'};
            if (id < 1000000){
                for (int i = id, cout = 0; i > 0; i = id / 10){
                    int j = i % 10;
                    temp[5-cout++] = (char)(j+48);
                }
            }
            String userId = new String(temp);
            System.out.println(userId);

            Map<String, Object> map = new HashMap<>();
            map.put("image", imgBase64);
            map.put("group_id", groupId);
            map.put("user_id", userId);
            map.put("user_info", userInfo);
            map.put("liveness_control", "NORMAL");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取用户组
    public static JSONArray groupGetlist(String accessToken) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/getlist";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("start", 0);
            map.put("length", 100);

            String param = GsonUtils.toJson(map);

            String result = HttpUtil.post(url, accessToken, "application/json", param);
//            System.out.println(result);
            JSONObject jsonGroup = (JSONObject) new JSONObject(result).get("result");
            System.out.println(jsonGroup);
            JSONArray group_id_list = (JSONArray) jsonGroup.get("group_id_list");
            return group_id_list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取用户列表
    public static JSONArray groupGetusers(String accessToken, String groupId) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/getusers";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("group_id", groupId);
            String param = GsonUtils.toJson(map);

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            JSONObject jsonUsers = (JSONObject) new JSONObject(result).get("result");
            JSONArray user_id_list = (JSONArray) jsonUsers.get("user_id_list");
            return user_id_list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 人脸搜索1:N
    public static String faceSearch(String accessToken, String base64, String groupId) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", base64);
            map.put("liveness_control", "NORMAL");
            map.put("group_id_list", groupId);
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 人脸搜索M:N
    public static String facesSearch(String accessToken, String base64, String groupId){
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/multi-search";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", base64);
            map.put("liveness_control", "NORMAL");
            map.put("group_id_list", groupId);
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");
            map.put("max_face_num", 3);
            map.put("match_threshold", 80);
            map.put("max_user_num", 3);

            String param = GsonUtils.toJson(map);

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 人脸检测
    public static String faceDetect(String accessToken, String base64) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", base64);
            map.put("face_field", "age,beauty,gender,race,faceshape");
            map.put("image_type", "BASE64");

            String param = GsonUtils.toJson(map);

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 人脸对比
    public static String faceMatch(String accessToken, String base64One, String base64Two) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/match";
        try {

            List<Map<String, Object>> maps = new ArrayList<>(2);
            Map<String, Object> map = new HashMap<>();
            map.put("image", base64One);
            map.put("image_type", "BASE64");
            map.put("face_type", "LIVE");
            map.put("quality_control", "LOW");
            map.put("liveness_control", "HIGH");
            maps.add(map);
            Map<String, Object> map2 = new HashMap<>();
            map2.put("image", base64Two);
            map2.put("image_type", "BASE64");
            map2.put("face_type", "LIVE");
            map2.put("quality_control", "LOW");
            map2.put("liveness_control", "HIGH");
            maps.add(map2);

            String param = GsonUtils.toJson(maps);

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
