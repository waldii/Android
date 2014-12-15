package com.waldispd.homecast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by waldispd on 15.12.2014.
 */
public class Staffel {
    public List<Episode> episodeList;
    public int number;

    public Staffel()
    {
        episodeList = new ArrayList<Episode>();
    }

    public Staffel(int number, List<Episode> episodeList)
    {
        this.number = number;
        this.episodeList = episodeList;
    }
}
