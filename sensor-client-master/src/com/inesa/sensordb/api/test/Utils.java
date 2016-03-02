package com.inesa.sensordb.api.test;

import com.inesa.sensordb.api.SensordbItem;
import org.cesl.sensordb.core.Item;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by pc on 15-6-1.
 */
public class Utils {

    public List<SensordbItem> get_sensordb_items(int num) {
//        Map<String, ByteBuffer> values = new HashMap();
        double[] spacexyz = {1.2, 2.3, 3.4};
        String sensorID = "sn";
        Date date = new Date();
        SensordbItem item = new SensordbItem();
        List<SensordbItem> items = new ArrayList<SensordbItem>();
        for (int i = 0; i < num; ++i) {
            Map<String, byte[]> values = new HashMap<String, byte[]>();
            values.put("key_test1" + String.valueOf(i),
                    ("value_" + String.valueOf(i)).getBytes());
            item.sensorID = (sensorID.concat("_e_").concat(String.valueOf(i))
                    .getBytes());
            item.timestamp = date.getTime();
            item.x = spacexyz[0] + i;
            item.y = spacexyz[1] + i;
            item.z = spacexyz[2] + i;
            item.values = values;
            items.add(item);
        }

        return items;
    }

    public List<Item> get_batch_items(int num) {
//        Map<String, ByteBuffer> values = new HashMap();
        Date date = new Date();
        Item item = new Item();
        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < num; ++i) {
            Map<String, ByteBuffer> values = new HashMap();
            values.put("key_test1" + String.valueOf(i),
                    ByteBuffer.wrap(("value_" + String.valueOf(i)).getBytes()));
            item.sensorID = ByteBuffer.wrap("testsendor".concat("_f_")
                    .concat(String.valueOf(i)).getBytes());
            item.sampledts = date.getTime();
            item.x = (item.sampledts%3+1)+i;
            item.y = (item.sampledts%3+2)+i;
            item.z = (item.sampledts%3+3)+i;
            item.values = values;
            items.add(item);

        }

        return items;
    }
}
