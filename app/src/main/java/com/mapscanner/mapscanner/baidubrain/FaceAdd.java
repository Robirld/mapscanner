package com.mapscanner.mapscanner.baidubrain;

import com.mapscanner.mapscanner.utils.GsonUtils;
import com.mapscanner.mapscanner.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * 人脸注册
 */
public class FaceAdd {
    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    public static String add(String imgBase64, String userInfo) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
        try {
            String accessToken = AuthService.getAuth();
            String str = GroupGetlist.groupGetlist(accessToken);
            JSONObject jsonGroup = (JSONObject) new JSONObject(str).get("result");
            System.out.println(jsonGroup);
            JSONArray group_id_list = (JSONArray) jsonGroup.get("group_id_list");
            String groupId = (String) group_id_list.get(0);

            // 动态获取user_id
            String userResult = GroupGetusers.groupGetusers(accessToken, groupId);
            JSONObject jsonUsers = (JSONObject) new JSONObject(userResult).get("result");
            JSONArray user_id_list = (JSONArray) jsonUsers.get("user_id_list");
                // 将int转为定长String
            int max = 0;
            for (int i = 0; i < user_id_list.length(); i++) {
                int id = Integer.parseInt((String) (user_id_list.get(i)));
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
}
