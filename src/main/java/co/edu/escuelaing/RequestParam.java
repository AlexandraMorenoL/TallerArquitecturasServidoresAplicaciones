package co.edu.escuelaing;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Enlaza un parámetro String con ?key=value; permite defaultValue.
 * @author Alexandra Moreno
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface RequestParam {

    String value();

    String defaultValue() default "";
}
