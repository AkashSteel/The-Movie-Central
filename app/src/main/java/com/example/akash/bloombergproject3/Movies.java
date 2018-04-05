package com.example.akash.bloombergproject3;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Akash on 3/9/2018.
 */

public class Movies implements Serializable {
    String name;
    String link;
    String votes;

    public  Movies(String name,String link,String votes){
        this.name = name;
        this.votes = votes;
        this.link = link;
    }

    public String getName(){
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getVotes() {
        return votes;
    }
}
