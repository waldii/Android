package com.waldispd.homecast;

/**
 * Created by waldispd on 15.12.2014.
 */
public class Episode {
    public String path;
    public int number;

    public Episode()
    {

    }

    public Episode(String path, int number)
    {
        this.number = number;
        this.path = path;
    }
}
