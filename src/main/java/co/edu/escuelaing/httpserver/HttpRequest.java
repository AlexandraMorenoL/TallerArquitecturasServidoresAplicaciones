package co.edu.escuelaing.httpserver;

/**
 *
 * Autor: Alexandra Moreno Latorre
 */

import java.util.*;

public class HttpRequest {
    private String method;
    private String path;
    private Map<String, String> queryParams = new HashMap<>();

    public HttpRequest(String requestLine) {
        String[] parts = requestLine.split(" ");
        method = parts[0];
        String fullPath = parts[1];

        if (fullPath.contains("?")) {
            String[] split = fullPath.split("\\?");
            path = split[0];
            String[] params = split[1].split("&");
            for (String param : params) {
                String[] kv = param.split("=");
                queryParams.put(kv[0], kv.length > 1 ? kv[1] : "");
            }
        } else {
            path = fullPath;
        }
    }

    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getValues(String key) { return queryParams.get(key); }
}
