package es.urjc.code.s3_ejem2;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/buckets")
public class BucketsController {

	@Autowired
	S3Service s3service;

	@GetMapping("/")
	public List<MyBucket> getBuckets() {
		return s3service.getAllBuckets();
	}

	@GetMapping("/{bucketName}/objects")
	public List<MyS3Object> getBucket(@PathVariable String bucketName) {
		return s3service.getBucketObjects(bucketName);
	}

	@PostMapping("/{bucketName}")
	public ResponseEntity<String> createBucket(@PathVariable String bucketName) {
		s3service.createBucket(bucketName);
		return new ResponseEntity<>("Bucket created!", HttpStatus.CREATED);
	}

	@PostMapping("/{bucketName}/uploadObject")
	public ResponseEntity<String> uploadFile(@PathVariable String bucketName, @RequestParam("file") MultipartFile file)
			throws IllegalStateException, IOException {
        s3service.uploadFile(bucketName, file);
		return new ResponseEntity<>("Object "+file.getOriginalFilename()+" created in bucket "+bucketName, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/{bucketName}/{objectName}")
	public ResponseEntity<String> deleteObject(@PathVariable String bucketName, @PathVariable String objectName) {
		s3service.deleteObject(bucketName, objectName);
		return new ResponseEntity<>("Object deleted!", HttpStatus.OK);
	}

	@DeleteMapping("/{bucketName}")
	public ResponseEntity<String> deleteBucket(@PathVariable String bucketName) {
		s3service.deleteBucket(bucketName);
		return new ResponseEntity<>("Bucket deleted!", HttpStatus.OK);
	}

}
