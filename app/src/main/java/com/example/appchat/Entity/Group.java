package com.example.appchat.Entity;

public class Group {
    private String groupId,groupTitle,groupDes,groupImg,timestamp,createBy;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupDes() {
        return groupDes;
    }

    public void setGroupDes(String groupDes) {
        this.groupDes = groupDes;
    }

    public String getGroupImg() {
        return groupImg;
    }

    public void setGroupImg(String groupImg) {
        this.groupImg = groupImg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Group(String groupId, String groupTitle, String groupDes, String groupImg, String timestamp, String createBy) {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.groupDes = groupDes;
        this.groupImg = groupImg;
        this.timestamp = timestamp;
        this.createBy = createBy;
    }

    public Group() {
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId='" + groupId + '\'' +
                ", groupTitle='" + groupTitle + '\'' +
                ", groupDes='" + groupDes + '\'' +
                ", groupImg='" + groupImg + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", createBy='" + createBy + '\'' +
                '}';
    }
}
