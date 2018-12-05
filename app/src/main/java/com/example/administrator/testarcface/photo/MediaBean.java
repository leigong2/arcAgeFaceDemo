package com.example.administrator.testarcface.photo;

import java.io.Serializable;

/**
 * Created by 王志龙 on 2018/11/8 008.
 */
public class MediaBean implements Serializable{
    public String path;
    public long size;
    public String displayName;

    public MediaBean(String path, long size, String displayName) {
        this.path = path;
        this.size = size;
        this.displayName = displayName;
    }
}
