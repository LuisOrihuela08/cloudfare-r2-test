package org.cloudfare.r2.controller;

import org.cloudfare.r2.service.StorageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/storage")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService){
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String,String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String key = storageService.uploadFile(file);
        return new ResponseEntity<>(Map.of("mensaje", "Archivo subido con el nombre: " + key), HttpStatus.OK);
    }

    @GetMapping("/download/{key}")
    public ResponseEntity<InputStreamResource> dowload(@PathVariable String key){
        var s3Object = storageService.downloadFile(key);
        InputStreamResource resource = new InputStreamResource(s3Object);

        String contentType = s3Object.response().contentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + key + "\"")
                .body(resource);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, List<String>>> listFiles(){
        List<String> listFiles = storageService.listFiles();
        return new ResponseEntity<>(Map.of("archivos", listFiles), HttpStatus.OK);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Map<String,String>> deleteFile(@PathVariable String key){
        storageService.deleteFile(key);
        return new ResponseEntity<>(Map.of("mensaje", "Archivo eliminado con la key: " + key), HttpStatus.OK);
    }
}
