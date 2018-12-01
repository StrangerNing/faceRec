package com.welcome;

import com.welcome.faceapi.FaceApi;
import com.welcome.utils.Base64Util;
import com.welcome.utils.FileUtil;
import net.sf.json.JSONObject;

import java.util.Date;

/**
 * @author Tsening Chu
 * @version 1.0
 * @date 2018/12/1
 */
public class FaceRecognition {
    public static UserInfo faceRecog(String filePath) {
        try {
            //变量预定义
            String userId = "";
            String operationResult = "";
            Long createTime = 0L;
            Long updateTime = 0L;
            Integer visitedTimes = 0;

            String imgBase64 = Base64Util.encode(FileUtil.readFileByBytes(filePath));
            FaceForm faceForm = new FaceForm();
            faceForm.setImage(imgBase64);
            faceForm.setLivenessControl("LOW");
            faceForm.setGroupId("FaceWelcome");
            faceForm.setImageType("BASE64");
            faceForm.setQualityControl("LOW");

            //人脸库搜索
            String searchResult = FaceApi.faceSearch(faceForm);
            System.out.println(searchResult);
            JSONObject searchResultJason = JSONObject.fromObject(searchResult);

            //人脸是否已被注册，如果没有，那么新增人脸
            if (searchResultJason.getString("result").equals("null")) {
                userId = "user" + String.valueOf((int) (Math.random() * 8998) + 1000 + 1);
                faceForm.setUserId(userId);
                createTime = new Date().getTime();
                updateTime = new Date().getTime();
                visitedTimes = 1;
                faceForm.setUserInfo("{\"create_time\":" + createTime +
                        ",\"update_time\":" + updateTime +
                        ",\"visited_times\":" + visitedTimes + "}");

                operationResult = FaceApi.faceAdd(faceForm);
            } else {
                //如果用户已注册，就获取用户信息。
                String result = searchResultJason.getString("result");
                JSONObject resultJson = JSONObject.fromObject(result);
                String userList = resultJson.getString("user_list");
                userList = userList.substring(1, userList.length() - 1);
                JSONObject userListJson = JSONObject.fromObject(userList);

                //获取用户Id
                userId = userListJson.getString("user_id");
                String userInfo = userListJson.getString("user_info");

                //获取用户信息
                userInfo = userInfo.substring(1, userInfo.length() - 1);
                JSONObject userInfoJson = JSONObject.fromObject(userInfo);

                //获取用户注册时间、更新时间、造访次数
                createTime = userInfoJson.getLong("create_time");
                updateTime = userInfoJson.getLong("update_time");
                visitedTimes = Integer.parseInt(userInfoJson.getString("visited_times"));

                //如果用户在一小时内来过，那么只算来过一次，否则更新光临次数
                Long newUpdateTime = new Date().getTime();
                if ((updateTime + (60 * 60 * 1000)) < newUpdateTime) {
                    faceForm.setUserId(userId);
                    faceForm.setUserInfo("{\"create_time\":" + createTime +
                            ",\"update_time\":" + newUpdateTime +
                            ",\"visited_times\":" + (++visitedTimes) + "}");
                    operationResult = FaceApi.faceUpdate(faceForm);

                }
            }
            JSONObject updateResultJson = JSONObject.fromObject(operationResult);
            if (updateResultJson.getString("error_msg").equals("SUCCESS")) {
                UserInfo userInfo = new UserInfo();
                userInfo.setUserId(userId);
                userInfo.setCreateTime(createTime);
                userInfo.setUpdateTime(updateTime);
                userInfo.setVisitedTimes(visitedTimes);
                return userInfo;
            } else {
                throw new RuntimeException("人脸注册或更新失败");
            }

        } catch (Exception e) {
            System.out.println("ERROR:" + e);
        }
        return null;
    }
}
