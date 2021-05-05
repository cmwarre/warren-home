package co.warrenhome.util;

import com.inductiveautomation.ignition.common.util.LoggerEx;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

    private static final LoggerEx logger = LoggerEx.newBuilder().build(ReflectionUtils.class);

    /**
     * Search through a model for fields and pass them to a consumer
     * */
    public static <T> List<Field> getModelFieldsAsList(Class<T> type){

        List<Field> fields = new ArrayList<>();
        Class<?> clazz = type;

        while(clazz != null){
            fields.addAll(List.of(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }

        return fields;
    }

}
