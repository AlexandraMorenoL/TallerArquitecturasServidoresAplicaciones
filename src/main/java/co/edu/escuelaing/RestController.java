package co.edu.escuelaing;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marca una clase como controlador REST para ser encontrada por reflexión.
 * @author Alexandra Moreno
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface RestController {
}
