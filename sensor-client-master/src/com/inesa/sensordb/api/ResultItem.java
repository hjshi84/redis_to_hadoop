package com.inesa.sensordb.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 15-5-21.
 */
class ResultItem {
    String id;
    long ts;
    double x;
    double y;
    double z;
    Map<String, String> values = new HashMap<>();
}