package com.waldispd.homecast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by waldispd on 15.12.2014.
 */
public class Serie {
    public String titel;
    public List<Staffel> staffelList;

    public Serie()
    {
        staffelList = new ArrayList<Staffel>();
    }

    public Serie(String titel, List<Staffel> staffelList)
    {
        this.titel = titel;
        this.staffelList = staffelList;
    }
}
