package com.welcome;

import org.opencv.core.Mat;

import java.util.Date;

/**
 * @author Tsening Chu
 * @version 1.0
 * @date 2018/12/1
 */
public class UserInfo {
    private String userId;
    private Date createTime;
    private Date updateTime;
    private Integer visitedTimes;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = new Date(createTime);
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = new Date(updateTime);
    }

    public Integer getVisitedTimes() {
        return visitedTimes;
    }

    public void setVisitedTimes(Integer visitedTimes) {
        this.visitedTimes = visitedTimes;
    }

}
