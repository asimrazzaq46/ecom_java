package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.interfaces.IFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService implements IFileService {
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // File name of current / original file
        String originalFilename = file.getOriginalFilename();

        // generate a unique file name
        String randomId = UUID.randomUUID().toString();
        String filename = randomId.concat(originalFilename.substring(originalFilename.lastIndexOf('.')));
        String filePath = path + File.separator + filename;

        // check if path exist and create
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        // return file name
        return filename;
    }
}
