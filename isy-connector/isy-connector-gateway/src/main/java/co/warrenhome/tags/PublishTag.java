package co.warrenhome.tags;

import com.inductiveautomation.ignition.common.sqltags.model.types.DataType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublishTag {
    String name();
    DataType dataType();
    boolean writeable() default false;
    double euMax() default 100.0;
    double euMin() default 0.0;
    String units() default "";
    String format() default "#,##0.##";
    String tooltip() default "";
    String documentation() default "";
    String relativePath() default "/";
    boolean historyEnabled() default false;
}
