package com.inesa.sensordb.api;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

/**
 * Created by pc on 15-5-12.
 */
public class JsonConvertor {

    public static Date date_now = new Date();

    class SingleItem {

        public String sensorID;
        public byte[] sensorID_byte;
        public long timestamp;
        public double x;
        public double y;
        public double z;
        public Map<String, byte[]> values_map = new HashMap<String, byte[]>();
        public Values values;

//        @Override
//        public String toString() {
//            return SUBJECT + " - " + GRADE;
//        }
    }

    class Values {

        public String word_separators;
        public byte[] word_separators_byte;
        public int font_size;
        public boolean fold_buttons;
        public Map<String, byte[]> values_map = new HashMap<String, byte[]>();
        public String values;

//        @Override
//        public String toString() {
//            return word_separators + ": " + GRADE;
//        }
    }


    public String jsonstr(FileReader jsonfile) {
        BufferedReader buf_reader = new BufferedReader(jsonfile);
        String jsonstr;
        Gson gson = new Gson();
        JsonParser jsonparser = new JsonParser();
        JsonObject jo = (JsonObject) jsonparser.parse(buf_reader);
        jsonstr = gson.toJson(jo);
//        jsonstr_sub = gson.fromJson("\"abc\"", String.class);
        System.out.println("jsonstr: " + jsonstr);

        return jsonstr;
    }

    public void json2java(FileReader jsonfile) throws IOException {
        try {
            BufferedReader buf_reader = new BufferedReader(jsonfile);

            SingleItem p = new Gson().fromJson(buf_reader, SingleItem.class);
            System.out.println(p.sensorID);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public ItemWithoutValue item_without_value(FileReader jsonfile)
            throws IOException {
        ItemWithoutValue item = new ItemWithoutValue();
        try {//(
            BufferedReader buf_reader = new BufferedReader(jsonfile);
            item = new Gson().fromJson(buf_reader,
                    ItemWithoutValue.class);
            System.out.println(item.sensorID);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return item;
    }

    public ItemWithoutValue item_without_value(String jsonstr)
            throws IOException {
        ItemWithoutValue item = new ItemWithoutValue();
        try {
            item = new Gson().fromJson(jsonstr,
                    ItemWithoutValue.class);
            System.out.println(item.sensorID);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return item;
    }

    public String sub_json(String jsonstr, String name) {
        String jsonstr_sub;
        Gson gson = new Gson();
        JsonParser jsonparser = new JsonParser();
        JsonObject jo = (JsonObject) jsonparser.parse(jsonstr);
        JsonElement je = jo.get(name);
        jsonstr_sub = gson.toJson(je);

        return jsonstr_sub;
    }

    public String sub_json(FileReader jsonfile, String name) {
        BufferedReader buf_reader = new BufferedReader(jsonfile);
        String jsonstr_sub;
        Gson gson = new Gson();
        JsonParser jsonparser = new JsonParser();
        JsonObject jo = (JsonObject) jsonparser.parse(buf_reader);
        JsonElement je = jo.get(name);
        jsonstr_sub = gson.toJson(je);

        return jsonstr_sub;
    }

    public Map<String, byte[]> get_map(String jsonstr) {

        Map<String, byte[]> values_map = new HashMap<String, byte[]>();
        try {
            values_map = new Gson().fromJson(jsonstr,
                    new TypeToken<HashMap<String, Object>>() {
                    }.getType());
//            System.out.println("sample_values: " + values_map);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {

            System.out.println("JsonConvertor.convert finally");
        }
        return values_map;
    }

    public Map<String, byte[]> get_map(FileReader jsonfile) {
        Map<String, byte[]> values_map = new HashMap<String, byte[]>();
        BufferedReader buf_reader = new BufferedReader(jsonfile);
        try {
            values_map = new Gson().fromJson(buf_reader,
                    new TypeToken<HashMap<String, Object>>() {
                    }.getType());
//            System.out.println("sample_values: " + values_map);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            System.out.println("JsonConvertor.convert finally");
        }
        return values_map;
    }

    public Map<String, Object> get_objmap(String jsonstr) {

        Map<String, Object> values_map = new HashMap<String, Object>();
        try {
            values_map = new Gson().fromJson(jsonstr,
                    new TypeToken<HashMap<String, Object>>() {
                    }.getType());
//            System.out.println("sample_values: " + values_map);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {

            System.out.println("JsonConvertor.convert finally");
        }
        return values_map;
    }

    public Map<String, String> get_strmap(String jsonstr) {

        Map<String, String> values_map = new HashMap<String, String>();
        try {
            values_map = new Gson().fromJson(jsonstr,
                    new TypeToken<HashMap<String, Object>>() {
                    }.getType());
//            System.out.println("sample_values: " + values_map);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {

            System.out.println("JsonConvertor.convert finally");
        }
        return values_map;
    }
}
