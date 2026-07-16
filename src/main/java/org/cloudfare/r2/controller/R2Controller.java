package org.cloudfare.r2.controller;

import org.cloudfare.r2.service.R2Service;
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
@RequestMapping("/r2")
public class R2Controller {

    private final R2Service r2Service;

    public R2Controller(R2Service r2Service){
        this.r2Service = r2Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String,String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String key = r2Service.uploadFile(file);
        return new ResponseEntity<>(Map.of("mensaje", "Archivo subido con el nombre: " + key), HttpStatus.OK);
    }

    @GetMapping("/download/{key}")
    public ResponseEntity<InputStreamResource> dowload(@PathVariable String key){
        var s3Object = r2Service.downloadFile(key);
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
        List<String> listFiles = r2Service.listFiles();
        return new ResponseEntity<>(Map.of("archivos", listFiles), HttpStatus.OK);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Map<String,String>> deleteFile(@PathVariable String key){
        r2Service.deleteFile(key);
        return new ResponseEntity<>(Map.of("mensaje", "Archivo eliminado con la key: " + key), HttpStatus.OK);
    }
}
