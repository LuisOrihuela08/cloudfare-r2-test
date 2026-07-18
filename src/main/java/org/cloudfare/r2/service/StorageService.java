package org.cloudfare.r2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageService {

    private final S3Client s3Client;
    private final Logger logger = LoggerFactory.getLogger(StorageService.class);
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public StorageService(S3Client s3Client){
        this.s3Client = s3Client;
    }

    @Value("${storage.bucket-name}")
    private String bucketName;

    //Upload files to bucket R2 or AWS S3
    public String uploadFile(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()){
            logger.warn("file is empty");
            throw new IllegalArgumentException("File is null or empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            logger.warn("file exceeds the maximum allowed size (10 MB)");
            throw new IllegalArgumentException("The file exceeds the maximum allowed size (10 MB)");
        }

        String key = file.getOriginalFilename();
        if (key == null || key.isBlank()){
            logger.warn("file name is empty");
            throw new IllegalArgumentException("File name is null or empty");
        }

        try {

            PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            logger.info("Upload File Successfully: {}", key);
            return key;
        } catch (S3Exception  e){
            logger.error("Upload File Failed to S3: '{}'", key, e);
            throw new IOException("Upload File Failed", e);
        }

    }

    //Download files from bucket R2 or AWS S3
    public ResponseInputStream<GetObjectResponse> downloadFile(String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseInputStream<GetObjectResponse> object = s3Client.getObject(request);
            logger.info("Download File Successfully: {}", key);
            return object;
        } catch (NoSuchKeyException  e) {
            logger.warn("File not found: {} ", key);
            throw e;
        } catch (S3Exception e){
            logger.error("Error downloading file: {} ", key, e);
            throw new RuntimeException("Error downloading file: "+ key);
        }

    }

    //List files in the bucket of R2 or AWS S3
    public List<String> listFiles(){
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketName)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);

            logger.info("List File Successfully");
            return response.contents().stream()
                    .map(S3Object::key)
                    .collect(Collectors.toList());
        } catch (S3Exception  e) {
            logger.error("Error to list files: ", e);
            throw new RuntimeException("Error to list files from bucket", e);
        }

    }

    //Delete files in the bucket of R2 or AWS S3
    public void deleteFile(String key){
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
        } catch (NoSuchKeyException e) {
            logger.warn("Try delete file not found: {}", key);
            throw e;
        }

        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucketName)
                    .key(key)
                    .build();

            logger.info("Delete File Successfully");
            s3Client.deleteObject(request);
        } catch (Exception e) {
            logger.error("Error to try delete file: " + e.getMessage());
            throw new RuntimeException("Error to try delete file", e);
        }

    }
}
