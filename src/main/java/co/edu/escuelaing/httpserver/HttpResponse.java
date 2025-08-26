package co.edu.escuelaing.httpserver;

/**
 *
 * Autor: Alexandra Moreno Latorre
 */

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private int statusCode = 200;
    private String statusMessage = "OK";
    private Map<String, String> headers = new HashMap<>();
    private String body = "";

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int code) {
        this.statusCode = code;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String msg) {
        this.statusMessage = msg;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String buildResponse() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMessage).append("\r\n");
        headers.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\r\n"));
        sb.append("\r\n");
        sb.append(body);
        return sb.toString();
    }
}
