package com.inesa.sensordb.api;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by pc on 15-5-13.
 */
public class SensordbItem {

    public byte[] sensorID;
    public long timestamp;
    public double x;
    public double y;
    public double z;
    public Map<String, byte[]> values = new HashMap<String, byte[]>();

    private ItemWithoutValue item_without_value = new ItemWithoutValue();
    private String values_str;
    //    private Map<String, String> values_strmap = new HashMap<String, String>();
    private Map<String, Object> values_objmap = new HashMap<String, Object>();

    public SensordbItem() {
    }

    SensordbItem(String json_in) {
        set_item(json_in);
    }

    public void set_item(String json_in) {
        JsonConvertor jsonconv = new JsonConvertor();
        try {
            this.item_without_value = jsonconv.item_without_value(json_in);
            values_str = jsonconv.sub_json(json_in, "values");
//                this.values = jsonconv.get_map(values_str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.sensorID = this.item_without_value.sensorID.getBytes();
        this.timestamp = this.item_without_value.timestamp;
        this.x = this.item_without_value.x;
        this.y = this.item_without_value.y;
        this.z = this.item_without_value.z;
        values_objmap = jsonconv.get_objmap(values_str);

        for (Map.Entry<String, Object> entry : values_objmap.entrySet()) {
            this.values.put(entry.getKey(), entry.getValue().toString().getBytes());

        }
    }

    public void set_item_byfile(String json_file) {
        JsonConvertor jsonconv = new JsonConvertor();
        try {
            this.item_without_value = jsonconv.item_without_value(
                    new FileReader(json_file));
            values_str = jsonconv.sub_json(
                    new FileReader(json_file), "values");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.sensorID = this.item_without_value.sensorID.getBytes();
        this.timestamp = this.item_without_value.timestamp;
        this.x = this.item_without_value.x;
        this.y = this.item_without_value.y;
        this.z = this.item_without_value.z;
        this.values = jsonconv.get_map(values_str);
    }
}
