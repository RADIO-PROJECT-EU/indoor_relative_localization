

import com.fasterxml.jackson.databind.ObjectMapper;
import models.ObservationReq;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpHeaders.USER_AGENT;

/**
 * {

 &quot;data&quot;: {

 &quot;id&quot;: &quot;550e8400-e29b- 41d4-a716- 446655440000&quot;,

 &quot;type&quot;: &quot;authentication&quot;,

 &quot;attributes&quot;: {

 &quot;email&quot;: &quot;chpanag@gmail.com&quot;,

 &quot;password&quot;: &quot;chpanag&quot;

 }

 }

 }
 */

public class Client {

    public static void authenticate() throws IOException{
        String url = "http://healthcare247.gr/rest/v1/auth/login";
        String body = "{\"data\":{\"id\": \"550e8400-e29b-41d4-a716-446655440000\",\"type\": \"authentication\",\"attributes\":{\"email\": \"chpanag@gmail.com\",\"password\": \"chpanag\"}}}";

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        post.addHeader("accept", "application/json");

        StringEntity input = new StringEntity(body);
        input.setContentType("application/json");
        post.setEntity(input);

        HttpResponse response = client.execute(post);
        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        System.out.println(result);
    }


    public static void post(ObservationReq observationReq) throws IOException {
        String url = "http://healthcare247.gr/rest/v1/observations";

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        // add header
        post.addHeader("accept", "application/json");
        post.addHeader("Authorization","Bearer 3643b94a-b328-49d1-82e1-fe4c78cca405");
        ObjectMapper mapper = new ObjectMapper();
        //Object to JSON in String
        String observationJson = mapper.writeValueAsString(observationReq);
        StringEntity input = new StringEntity(observationJson);
        input.setContentType("application/json");
        post.setEntity(input);

        HttpResponse response = client.execute(post);
        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        System.out.println(result.toString());
    }

}