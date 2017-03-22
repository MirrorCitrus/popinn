package cdf.com.easypop.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by cdf on 2017/3/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LayoutDimension {
    enum LayoutDimensionMode { MATCH_PARENT, WRAP_CONTENT };

    LayoutDimensionMode value() default LayoutDimensionMode.MATCH_PARENT;
}
