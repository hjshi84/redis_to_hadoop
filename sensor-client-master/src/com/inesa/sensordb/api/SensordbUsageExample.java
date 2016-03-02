package com.inesa.sensordb.api;

import com.google.gson.Gson;
import org.cesl.sensordb.client.Connection;
import org.cesl.sensordb.client.ResultSet;
import org.cesl.sensordb.core.Item;
import org.cesl.sensordb.exception.DBException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;


public class SensordbUsageExample {
    public static String IP_SENSORDB = "122.144.166.103";
    public static int PORT_SENSORDB = 6677;
    public static String NEW_TABLE = "test_table_1";
    public static String SENSOR_ID = "camera1";
    public static Date date_now = new Date();
    public static double[] SPACE_XYZ = {1.2, 2.3, 3.4};
    public static Map<String, byte[]> sample_values = new HashMap();


    public void create_table() {
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        try {
            conn.connect();
            conn.createTable("test_table_2");
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            conn.close();
        }
    }


    public void delete_table() {
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        try {
            conn.connect();
            conn.dropTable("test_table_2");
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            conn.close();
        }
    }


    public void list_tables() {
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        try {
            conn.connect();
            List<String> table_list = conn.listTableNames();
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            conn.close();
        }
    }


    public void put() {
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        try {
            conn.connect();
            sample_values.put("key1", "value1".getBytes());
            sample_values.put("key2", "value2".getBytes());
            conn.put(NEW_TABLE, SENSOR_ID.getBytes(), date_now.getTime(),
                    SPACE_XYZ[0], SPACE_XYZ[1], SPACE_XYZ[2], sample_values);
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            conn.close();
        }
    }


    public void multi_put(int item_num) {
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        try {
            conn.connect();
            List<Item> items = items_for_batchPut(item_num);
            conn.batchPut(NEW_TABLE, items);

        } catch (DBException e1) {
            e1.printStackTrace();
        } finally {
            conn.close();
        }
    }


    public void put_from_jsonstr(String json_value) {
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        SensordbItem item = new SensordbItem(json_value);

        try {
            conn.connect();
            conn.put(NEW_TABLE, item.sensor_id, item.timestamp,
                    item.x, item.y, item.z, item.values);
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            conn.close();
        }
    }


    public void get() {
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        try {
            conn.connect();

            ResultSet result_set = conn.get(NEW_TABLE, /*optionally add params*/
                    "2015-05-12 06:29:43", "2015-05-12 12:50:00");
            System.out.println("table: " + NEW_TABLE
                    + " --error code: " + result_set.getErrCode()
                    + " --size: " + result_set.getSize());
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            conn.close();
        }
    }


    public void iterating_record(List<String> valuekeys) {
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        ResultItem item = new ResultItem();
        try {
            conn.connect();
            ResultSet result_set = conn.get(NEW_TABLE,
                    "2015-05-12 06:29:43", "2015-05-12 12:50:00");
            System.out.println("result_set: " + result_set + " size:"
                    + result_set.getSize() + " errorcode:" +
                    result_set.getErrCode());

            while (result_set.next()) {
                item.id = result_set.getString("id");
                item.ts = result_set.getLong("ts");
                item.x = result_set.getDouble("x");
                item.y = result_set.getDouble("y");
                item.z = result_set.getDouble("z");
                String tst = result_set.getString("word_separators");
                for (int i=0; i<valuekeys.size(); ++i){
                    item.values.put(valuekeys.get(i),
                            result_set.getString(valuekeys.get(i)));
                }
                System.out.println("item - sensorID: " + item.id
                        + "  timestamp: " + item.ts
                        + " tst:"+tst+" x:"+item.x+" y:"+item.y+" z:"+item.z
                        +"  values: "+item.values);
            }
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            conn.close();
        }
    }


    public List<Item> items_for_batchPut(int num) {
        Date date = new Date();
        Item item = new Item();
        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < num; ++i) {
            Map<String, ByteBuffer> values = new HashMap();
            values.put("key_test1" + String.valueOf(i),
                    ByteBuffer.wrap(("value_" + String.valueOf(i)).getBytes()));
            item.sensorID = ByteBuffer.wrap(SENSOR_ID.concat("_m_")
                    .concat(String.valueOf(i)).getBytes());
            item.sampledts = date.getTime();
            item.x = SPACE_XYZ[0]+i;
            item.y = SPACE_XYZ[1]+i;
            item.z = SPACE_XYZ[2]+i;
            item.values = values;
            items.add(item);
        }
        return items;
    }


    class SensordbItem {
        public byte[] sensor_id;
        public long timestamp;
        public double x;
        public double y;
        public double z;
        public Map<String, byte[]> values = new HashMap<String, byte[]>();

        private ItemWithoutValue item_without_value = new ItemWithoutValue();
        private String values_str;
        private Map<String, Object> values_objmap = new HashMap<String, Object>();

        SensordbItem() {}

        SensordbItem(String json_in) {
            set_item(json_in);
        }

        public void set_item(String json_in) {
            JsonConvertor jsonconv = new JsonConvertor();
            try {
                this.item_without_value = jsonconv.item_without_value(json_in);
                values_str = jsonconv.sub_json(json_in, "values");
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.sensor_id = this.item_without_value.sensorID.getBytes();
            this.timestamp = this.item_without_value.timestamp;
            this.x = this.item_without_value.x;
            this.y = this.item_without_value.y;
            this.z = this.item_without_value.z;
            values_objmap = jsonconv.get_objmap(values_str);

            for (Map.Entry<String, Object> entry : values_objmap.entrySet()) {
                this.values.put(entry.getKey(),
                        entry.getValue().toString().getBytes());
            }
        }
    }
}
