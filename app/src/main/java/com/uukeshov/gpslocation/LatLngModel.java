package com.uukeshov.gpslocation;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uukeshov on 06.10.2016.
 */

public class LatLngModel {
    public static final List<LatLng> latLngs = new ArrayList<>();

    public LatLngModel() {
    }

    public static List<LatLng> getLatLngs() {
        return latLngs;
    }

    public static void addLatLng() {
        latLngs.add(new LatLng(42.876331, 74.596027));
        latLngs.add(new LatLng(42.876331, 74.596027));
        latLngs.add(new LatLng(42.874885, 74.637054));
        latLngs.add(new LatLng(42.858047, 74.636623));
        latLngs.add(new LatLng(42.854985, 74.634992));
        latLngs.add(new LatLng(42.853288, 74.633903));
        latLngs.add(new LatLng(42.846029, 74.634771));
    }

    public static LatLng getLatLngItem(Integer index){
        return latLngs.get(index);
    }
}

