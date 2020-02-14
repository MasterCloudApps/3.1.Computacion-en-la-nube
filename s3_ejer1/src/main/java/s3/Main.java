package s3;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.apache.commons.io.FileUtils;

public class Main {

  public static  AmazonS3 s3;
  public static  String bucketName;

	public static void main(String[] args) throws IOException {

        s3 = AmazonS3ClientBuilder
              .standard()
              .withRegion(Regions.US_EAST_1)
              .build();

        // Listamos los buckets

        listBuckets();

        // Creamos un nuevo bucket

        bucketName = "iwcn"; // OJO: Cambia el nombre del bucket (por ejemplo: minombre.miapellido.cloudapps)

        System.out.println("Trying to create a new bucket: "+bucketName);

        if(s3.doesBucketExistV2(bucketName)) {
            System.out.println("Error: bucket name: "+bucketName+" is not available");
            return;
        }
        s3.createBucket(bucketName);
        System.out.println("New bucket created!");

        // Listamos los buckets

        listBuckets();

        // Subimos un nuevo objecto al bucket

        String privateObjectName = "privateData.txt";

        System.out.println("Upload object: "+privateObjectName+" to bucket: "+bucketName);

        s3.putObject(
          // BUCKET NAME
          bucketName, 
          // FILE NAME
          privateObjectName,
          // FILE DATA
          new File("files/privateData.txt")
        );

        // Subimos un nuevo objeto al bucket (con permisos de lectura)

        String publicObjectName = "publicData.txt";

        System.out.println("Upload object: "+publicObjectName+" to bucket: "+bucketName);

        PutObjectRequest por = new PutObjectRequest(
          // BUCKET NAME
          bucketName, 
          // FILE NAME
          publicObjectName,
          // FILE DATA
          new File("files/publicData.txt")
        );
        por.setCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(por);

        // Listamos los objectos

        System.out.println("List all objects in bucket: "+bucketName);
        ObjectListing objectListing = s3.listObjects(bucketName);
        for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
            System.out.println("-> Object: "+os.getKey());
        }

        // Descargamos el objeto privado y lo guardamos con otro nombre
        
        System.out.println("Download object: "+privateObjectName+" from bucket: "+bucketName);
        S3Object s3object = s3.getObject(bucketName, privateObjectName);
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        FileUtils.copyInputStreamToFile(inputStream, new File("files/privateDataFromS3.txt"));

        // Esperamos a que el usuario pueda comprobar los cambios en el navegador
        System.out.println("Open: https://"+bucketName+".s3.amazonaws.com/"+publicObjectName+" in browser");

        waitUser();

        // Borramos el objeto privado
        System.out.println("Deleting object: "+privateObjectName+" from bucket: "+bucketName);
        s3.deleteObject(bucketName, privateObjectName);

        // Borramos el objeto público
        System.out.println("Deleting object: "+publicObjectName+" from bucket: "+bucketName);
        s3.deleteObject(bucketName, publicObjectName);

        // Borramos el bucket (Solo podemos borrar buckets vacíos)

        System.out.println("Deleting bucket: "+bucketName);
        s3.deleteBucket(bucketName);

        System.exit(0);

  }

  public static void waitUser(){
	  System.out.println("Press ENTER to continue ...");
	  Scanner scanner = new Scanner(System.in);
	  scanner.nextLine();
	  scanner.close();
  }

  public static void listBuckets() {
    System.out.println("List all buckets in S3");
    for(Bucket s : s3.listBuckets() ) {
        System.out.println("-> "+s.getName());
    }
  }

}
