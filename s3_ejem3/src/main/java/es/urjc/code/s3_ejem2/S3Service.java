package es.urjc.code.s3_ejem2;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    public static S3Client s3;

    public S3Service() {
        s3 = S3Client.builder()
            .region(Region.US_EAST_1)
            .build(); 
    }

    public List<MyBucket> getAllBuckets() {
        return s3.listBuckets().buckets().stream().map((bucket)->{
            return new MyBucket(bucket.name(), bucket.creationDate());
        }).collect(Collectors.toList());
    }

    public List<MyS3Object> getBucketObjects(String bucketName) {
        return s3.listObjects(
            ListObjectsRequest.builder()
            .bucket(bucketName)
            .build()
        ).contents().stream().map((object)->{
            return new MyS3Object(object.key(), object.lastModified());
        }).collect(Collectors.toList());
    }

    public void createBucket(String bucketName) {
        s3.createBucket(
            CreateBucketRequest.builder()
                .bucket(bucketName)
                .build()
        );
    }

    public void uploadFile(String bucketName, MultipartFile multiPartFile) throws IllegalStateException, IOException {
        String fileName = multiPartFile.getOriginalFilename();
        File file = new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
        multiPartFile.transferTo(file);
        s3.putObject(
            PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build(), 
            RequestBody.fromFile(file)
        );
    }

    public void deleteObject(String bucketName, String objectName){
        s3.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build()
        );
    }

    public void deleteBucket(String bucketName){
        s3.deleteBucket(
            DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build()
        
        );
    }

}