package com.welcome.faceapi;

import com.welcome.FaceForm;

/**
 * @author Tsening Chu
 * @version 1.0
 * @date 2018/11/30
 */
public interface FaceApi {

    Boolean getAuth();

    String faceAdd(FaceForm faceForm);

    String faceSearch(FaceForm faceForm);

    String faceUpdate(FaceForm faceForm);

    String groupAdd(FaceForm faceForm);

    String getUserInfo(FaceForm faceForm);
}
