package co.edu.escuelaing;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Asocia un método a una ruta GET. Retorno permitido en este prototipo: String.
 * @author Alexandra Moreno
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface GetMapping {

    String value() default "/";
}
