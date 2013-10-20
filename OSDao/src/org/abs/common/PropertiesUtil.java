package org.abs.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {
    private static final Map<String, Properties>propMap = new HashMap<String, Properties>();
    private static final String DEFAULT_PROPFILE_NAME = "/abs.properties";
    static{
        load(DEFAULT_PROPFILE_NAME);
    }
    public static String getProp(String propName){
        return getProp(propName, DEFAULT_PROPFILE_NAME);
    }
    public static String getProp(String propName, String fileName){
        Properties prop = getProperties(fileName);
       return  prop.getProperty(propName);
    }

    public static Properties getProperties(String fileName){
    	Properties prop = propMap.get(fileName);
        if(prop ==null){
        	load(fileName);
        }
        return propMap.get(fileName);
    }

    public static void load(String fileName){
        Properties prop = new Properties();
        InputStream is = null;
        try {
            is = PropertiesUtil.class.getResourceAsStream(fileName);
            prop.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        propMap.put(fileName, prop);
    }
    
 
}
