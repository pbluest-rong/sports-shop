package com.pblues.sportsshop.service.upload;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    String upload(MultipartFile sourceFile);
    void deleteByUrl(String url);
}