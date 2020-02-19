package com.beacon.moive.Beans;

import java.util.List;

/**
 * Author Qumoy
 * Create Date 2020/2/3
 * Description：
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */
public class MoiveBean {
    //电影图片地址
    private String moivePic;
    //电影名称
    private String moiveName;
    //电影上映时间
    private String moiveTime;
    //电影演员
    private String moiveActor;
    //电影类型
    private String moiveType;
    //电影描述
    private String moiveDescription;
    //电影海报
    private List<String> moivePost;
    //BeaconMinor
    private int minor;

    public List<String> getMoivePost() {
        return moivePost;
    }

    public void setMoivePost(List<String> moivePost) {
        this.moivePost = moivePost;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }


    public String getMoivePic() {
        return moivePic;
    }

    public void setMoivePic(String moivePic) {
        this.moivePic = moivePic;
    }

    public String getMoiveName() {
        return moiveName;
    }

    public void setMoiveName(String moiveName) {
        this.moiveName = moiveName;
    }

    public String getMoiveTime() {
        return moiveTime;
    }

    public void setMoiveTime(String moiveTime) {
        this.moiveTime = moiveTime;
    }

    public String getMoiveActor() {
        return moiveActor;
    }

    public void setMoiveActor(String moiveActor) {
        this.moiveActor = moiveActor;
    }

    public String getMoiveType() {
        return moiveType;
    }

    public void setMoiveType(String moiveType) {
        this.moiveType = moiveType;
    }

    public String getMoiveDescription() {
        return moiveDescription;
    }

    public void setMoiveDescription(String moiveDescription) {
        this.moiveDescription = moiveDescription;
    }


}
