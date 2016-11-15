package com.shynixn.bungeesignminigamelib.business.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;

/**
 * Created by Shynixn
 */
class BungeeCordHelper
{
    static boolean writeAllLines(File file, String... text) {
        try {
            if(file.exists()) {
                if(!file.delete())
                    throw new IllegalStateException("Cannot delete previous file!");
            }
            if(!file.createNewFile())
                throw new IllegalStateException("Cannot create file!");
            try(BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"))) {
                for(int i = 0; i< text.length; i++) {
                    bufferedWriter.write(text[i] + "\n");
                }
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static Object invokeMethodByObject(Object object, String name, Object... params) {
        Class<?> clazz = object.getClass();
        do {
            for(Method method : clazz.getDeclaredMethods()) {
                try {
                    if(method.getName().equalsIgnoreCase(name)) {
                        method.setAccessible(true);
                        return method.invoke(object, params);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }while (clazz != null);
        throw new RuntimeException("Cannot find correct method.");
    }
}
