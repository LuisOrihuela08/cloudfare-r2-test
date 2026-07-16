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

    public StorageService(S3Client s3Client){
        this.s3Client = s3Client;
    }

    @Value("${storage.bucket-name}")
    private String bucketName;

    //Cargar archivos al bucket de R2
    public String uploadFile(MultipartFile file) throws IOException {
        try {
            String key = file.getOriginalFilename();

            PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            logger.info("Upload File Successfully");
            return key;
        } catch (Exception e){
            logger.error("Error al subir el archivo: " + e.getMessage());
            throw new IOException("Error al subir el archivo", e);
        }

    }

    //Descargar archivos del bucket de R2
    public ResponseInputStream<GetObjectResponse> downloadFile(String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            logger.info("Download File Successfully");
            return s3Client.getObject(request);
        } catch (Exception e) {
            logger.error("Error al descargar el archivo: " + e.getMessage());
            throw new RuntimeException("Error al descargar el archivo", e);
        }

    }

    //Listar los archivos en el bucket de R2
    public List<String> listFiles(){
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketName)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);

            logger.info("List File Successfully");
            return response.contents().stream()
                    .map(S3Object::key)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al listar los archivos: " + e.getMessage());
            throw new RuntimeException("Error al listar los archivos", e);
        }

    }

    //Eliminar un archivo en el bucket de R2
    public void deleteFile(String key){
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucketName)
                    .key(key)
                    .build();

            logger.info("Delete File Successfully");
            s3Client.deleteObject(request);
        } catch (Exception e) {
            logger.error("Error al eliminar el archivo: " + e.getMessage());
            throw new RuntimeException("Error al eliminar el archivo", e);
        }

    }
}
