package com.kpstv.sample_ktx;

import com.squareup.sqldelight.ColumnAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SQLDelightAdapters {
    public static ColumnAdapter<List<Data>, String> SampleUserAdapter() {
        return new ColumnAdapter<List<Data>, String>() {
            @NotNull
            @Override
            public List<Data> decode(String s) {
                return null;
            }

            @Override
            public String encode(@NotNull List<Data> data) {
                return null;
            }
        };
    }
}
