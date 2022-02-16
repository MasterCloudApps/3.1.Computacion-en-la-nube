package es.codeurjc.s3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

public class Main {

  public static S3Client s3;
  public static String bucketName;

  public static void main(String[] args) throws IOException {

    s3 = S3Client.builder()
            .region(Region.US_EAST_1)
            .build();

    // Listamos los buckets

    listBuckets();

    // Creamos un nuevo bucket

    bucketName = "mca-cloud"; // OJO: Cambia el nombre del bucket (por ejemplo: minombre.miapellido.cloudapps)

    System.out.println("Trying to create a new bucket: " + bucketName);

    try {
        s3.headBucket(
            HeadBucketRequest.builder()
                .bucket(bucketName)
                .build()
        );
        System.out.println("Error: bucket name: " + bucketName + " is not available");
    } catch (NoSuchBucketException e) {
        s3.createBucket(
            CreateBucketRequest.builder()
                .bucket(bucketName)
                .build()
        );
    }

    System.out.println("New bucket created!");

    // Listamos los buckets

    listBuckets();

    // Subimos un nuevo objecto al bucket

    String privateObjectName = "privateData.txt";

    System.out.println("Upload object: " + privateObjectName + " to bucket: " + bucketName);

    s3.putObject(
            PutObjectRequest.builder()
                .bucket(bucketName)
                .key(privateObjectName)
                .build(), 
            RequestBody.fromFile(new File("files/privateData.txt"))
        );

    // Subimos un nuevo objeto al bucket (con permisos de lectura)

    String publicObjectName = "publicData.txt";

    System.out.println("Upload object: " + publicObjectName + " to bucket: " + bucketName);

    s3.putObject(
        PutObjectRequest.builder()
            .bucket(bucketName)
            .key(publicObjectName)
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build(), 
        RequestBody.fromFile(new File("files/publicData.txt"))
    );

    // Listamos los objectos

    System.out.println("List all objects in bucket: " + bucketName);

    List<S3Object> objects = s3.listObjects(
        ListObjectsRequest.builder()
          .bucket(bucketName)
          .build()
    ).contents();

    for(S3Object object: objects){
      System.out.println("-> Object: " + object.key());
    }

    // Descargamos el objeto privado y lo guardamos con otro nombre

    System.out.println("Download object: " + privateObjectName + " from bucket: " + bucketName);
    s3.getObject(
        GetObjectRequest.builder()
                  .bucket(bucketName)
                  .key(privateObjectName)
                  .build()
    ).transferTo(new FileOutputStream("files/privateDataFromS3.txt"));

    // Esperamos a que el usuario pueda comprobar los cambios en el navegador
    System.out.println("Open: https://s3.amazonaws.com/" + bucketName + "/" + publicObjectName + " in browser");

    waitUser();

    // Borramos el objeto privado
    System.out.println("Deleting object: " + privateObjectName + " from bucket: " + bucketName);
    s3.deleteObject(
        DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(privateObjectName)
            .build()
    );

    // Borramos el objeto público
    System.out.println("Deleting object: " + publicObjectName + " from bucket: " + bucketName);
    s3.deleteObject(
        DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(publicObjectName)
            .build()
    );

    // Borramos el bucket (Solo podemos borrar buckets vacíos)

    System.out.println("Deleting bucket: " + bucketName);
    s3.deleteBucket(
        DeleteBucketRequest.builder()
            .bucket(bucketName)
            .build()
    
    );

    System.exit(0);

  }

  public static void waitUser() {
    System.out.println("Press ENTER to continue ...");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
    scanner.close();
  }

  public static void listBuckets() {
    System.out.println("List all buckets in S3");

    for (Bucket bucket : s3.listBuckets().buckets()) {
      System.out.println("-> " + bucket.name());
    }
  }

}
