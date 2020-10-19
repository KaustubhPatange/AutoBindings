package com.kpstv.bindings;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class TestClass {
    public static <T> Type inferType() {

        return new TypeToken<T>() {}.getType();
    }
}
