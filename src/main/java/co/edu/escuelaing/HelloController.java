package co.edu.escuelaing;

/**
 * Controlador básico de ejemplo.
 * Rutas:
 *  - GET "/"      → página de inicio con enlaces de prueba.
 *  - GET "/hello" → saludo simple.
 * @author Alexandra Moreno
 */
@RestController
public class HelloController {

    /** Página de inicio con enlaces a endpoints y a un recurso estático. */
    @GetMapping("/")
    public String home() {
        return "<h1>Bienvenido</h1>" +
               "<ul>" +
               "<li><a href='/hello'>/hello</a></li>" +
               "<li><a href='/greeting?name=Alexandra'>/greeting?name=Alexandra</a></li>" +
               "<li><a href='/img/logo.png'>/img/logo.png</a></li>" +
               "</ul>";
    }

    /** Saludo simple en HTML. */
    @GetMapping("/hello")
    public String hello() {
        return "<h1>Hola! desde HelloController</h1>";
    }
}
