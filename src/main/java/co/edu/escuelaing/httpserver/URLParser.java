package co.edu.escuelaing.httpserver;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
* Autor: Alexandra Moreno Latorre 
*/
public class URLParser {
    public static void main(String[] args)throws MalformedURLException {
        URL personalurl = new URL("http://aml.escuelaing.edu.co/personal/index.html?val=565color=red#publicaciones");
        System.out.println("Protocol: "+ personalurl.getProtocol());
        System.out.println("Authority: "+ personalurl.getAuthority());
        System.out.println("Host: "+ personalurl.getHost());
        System.out.println("Port: "+ personalurl.getPort());
        System.out.println("Path: "+ personalurl.getPath());
        System.out.println("Query: "+ personalurl.getQuery());
        System.out.println("File: "+ personalurl.getFile());
        System.out.println("Ref: "+ personalurl.getRef());
    }
}
