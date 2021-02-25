package es.urjc.code.s3_ejem2;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3Service {

    public static AmazonS3 s3;

    public S3Service() {
        s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    }

    public List<Bucket> getAllBuckets() {
        return s3.listBuckets();
    }

    public List<S3ObjectSummary> getBucketObjects(String bucketName) {
        return s3.listObjects(bucketName).getObjectSummaries();
    }

    public void createBucket(String bucketName) {
        if(s3.doesBucketExistV2(bucketName)) {
            throw new AmazonS3Exception("Bucket name already exist");
        }
        s3.createBucket(bucketName);
    }

    public void uploadFile(String bucketName, MultipartFile multiPartFile) throws IllegalStateException, IOException {
        String fileName = multiPartFile.getOriginalFilename();
        File file = new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
        multiPartFile.transferTo(file);
        s3.putObject(bucketName, fileName, file);
    }

    public void deleteObject(String bucketName, String objectName){
        s3.deleteObject(bucketName, objectName);
    }

    public void deleteBucket(String bucketName){
        s3.deleteBucket(bucketName);
    }

}