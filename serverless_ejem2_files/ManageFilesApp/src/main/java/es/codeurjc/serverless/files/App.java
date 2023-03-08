package es.codeurjc.serverless.files;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.*;
import java.util.Map;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final AmazonS3 S3 = AmazonS3ClientBuilder.standard().build();
    private static final String BUCKET_NAME = "mca-serverless-bucket";

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        String httpMethod = event.getHttpMethod();

        String fileName = event.getPathParameters().get("fileName");

        switch (httpMethod) {
            case "GET":
                String responseBody = null;
                try {
                    responseBody = readObject(fileName);
                } catch (IOException e) {
                    response.setBody("Exception while reading the object: "+e.getMessage());
                    response.setStatusCode(500);
                }

                response.setHeaders(Map.of("Content-Type", "text/plain"));
                response.setBody(responseBody.toString());
                response.setStatusCode(200);
                break;

            case "POST":
            case "PUT":
                String content = event.getBody();
                writeObject(fileName, content, "text/plain");
                response.setStatusCode(201);
                break;

            case "DELETE":
                deleteObject(fileName);
                response.setStatusCode(200);
                break;
        }

        return response;
    }

    private void writeObject(String fileName, Object data, String contentType) {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(data.toString().getBytes().length);

        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, fileName,
                new ByteArrayInputStream(data.toString().getBytes()), metadata);

        S3.putObject(request);
    }

    private String readObject(String fileName) throws IOException {
        GetObjectRequest request = new GetObjectRequest(BUCKET_NAME, fileName);
        InputStream inputStream = S3.getObject(request).getObjectContent();

        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private void deleteObject(String fileName) {
        DeleteObjectRequest request = new DeleteObjectRequest(BUCKET_NAME, fileName);
        S3.deleteObject(request);
    }
}

