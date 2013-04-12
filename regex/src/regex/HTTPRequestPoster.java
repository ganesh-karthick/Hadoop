/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package regex;

/**
 *
 * @author gk
 */





    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.io.OutputStream;
    import java.io.OutputStreamWriter;
    import java.io.Reader;
    import java.io.Writer;
    import java.net.HttpURLConnection;
    import java.net.ProtocolException;
    import java.net.URL;
    import java.net.URLConnection;

    public class HTTPRequestPoster
    {
    /**
    * Sends an HTTP GET request to a url
    *
    * @param endpoint - The URL of the server. (Example: " http://www.yahoo.com/search")
    * @param requestParameters - all the request parameters (Example: "param1=val1&param2=val2"). Note: This method will add the question mark (?) to the request - DO NOT add it yourself
    * @return - The response from the end point
    */
    public static String sendGetRequest(String endpoint, String requestParameters)
    {
    String result = null;
    if (endpoint.startsWith("http://"))
    {
    // Send a GET request to the servlet
    try
    {

    // Send data
    String urlStr = endpoint;
    if (requestParameters != null && requestParameters.length () > 0)
    {
    urlStr += "?" + requestParameters;
    }
    URL url = new URL(urlStr);
    URLConnection conn = url.openConnection ();

    // Get the response
    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    StringBuffer sb = new StringBuffer();
    String line;
    while ((line = rd.readLine()) != null)
    {
    sb.append(line);
    }
    rd.close();
    result = sb.toString();
    } catch (Exception e)
    {
    e.printStackTrace();
    }
    }
    return result;
    }
    }