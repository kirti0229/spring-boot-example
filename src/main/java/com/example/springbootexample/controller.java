package com.example.springbootexample;

import com.example.springbootexample.FileUpload.FileUploadResponse;
import com.example.springbootexample.util.FileDownloadUtil;
import com.example.springbootexample.util.FileUploadUtil;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;


@RestController
@RequestMapping(path = "/task")
public class controller {

    @GetMapping("/hello")
    public String sayHello(@RequestParam String name) {
        return String.format("Hello %s!", name);
    }

    @GetMapping("/task1")
    public String task(@RequestParam String fname,@RequestParam (required = false)String mname,@RequestParam String lname ) {
        String goodName = "";
        if (mname==""){
            goodName = Character.toString(fname.charAt(0)).concat(" ").concat(lname);
        }else{
            goodName = Character.toString(fname.charAt(0)).concat(" ").concat(Character.toString(mname.charAt(0))).concat(" ").concat(lname);
        }

        return String.format( goodName.toUpperCase());
    }
    @GetMapping("/getImage/app/path")
    public ResponseEntity<InputStreamResource> getImage( @RequestParam String filePath) throws IOException {
        URL url = new URL(filePath);
//        image = ImageIO.read(url);
        BufferedImage urlImage = ImageIO.read(url);
        InputStream image = url.openStream();
//        InputStream imgFile = getClass().getResourceAsStream(filePath);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(urlImage , "png", byteArrayOutputStream);

        byte[] imageInByte = byteArrayOutputStream.toByteArray();

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(image));


    }

    @PostMapping("/uploadFile")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile multipartFile)
            throws IOException {

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        long size = multipartFile.getSize();

        String filecode = FileUploadUtil.saveFile(fileName, multipartFile);

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        response.setDownloadUri("/downloadFile/kirti" + filecode);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/downloadFile/{fileCode}")
    public ResponseEntity<?> downloadFile(@PathVariable("fileCode") String fileCode) {
        FileDownloadUtil downloadUtil = new FileDownloadUtil();

        Resource resource = null;
        try {
            resource = downloadUtil.getFileAsResource(fileCode);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

}
