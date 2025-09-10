package com.pblues.sportsshop.service.upload;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryService implements UploadService {
    private final Cloudinary cloudinary;

    @Override
    public String upload(MultipartFile sourceFile) {
        String resourceType = detectResourceType(sourceFile);
        try {
            String publicId = generatePublicValue(sourceFile.getOriginalFilename());
            Map uploadResult = cloudinary.uploader().upload(
                    sourceFile.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", resourceType,
                            "public_id", publicId
                    )
            );
            String url = uploadResult.get("secure_url").toString();
            return url;
        } catch (IOException e) {
            throw new RuntimeException("Upload " + resourceType + " failed", e);
        }
    }
    private String detectResourceType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) return "raw";
        if (contentType.startsWith("image/")) return "image";
        if (contentType.startsWith("video/")) return "video";

        return "raw";
    }
    public String generatePublicValue(String originalName) {
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }

    @Override
    public void deleteByUrl(String url) {
        try {
            String publicId = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("invalidate", true));
        } catch (IOException e) {
            throw new RuntimeException("Delete failed", e);
        }
    }
}
