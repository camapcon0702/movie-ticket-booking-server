package qnt.moviebooking.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStoreService {
    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String prefix) throws Exception {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new Exception("File rỗng hoặc không có tên.");
        }

        try {
            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", prefix,
                    "resource_type", "image"
            );

            Map<?, ?> uploadResult = cloudinary.uploader()
                    .upload(file.getBytes(), options);

            String secureUrl = uploadResult.get("secure_url").toString();

            String publicId = uploadResult.get("public_id").toString();
            log.info("File uploaded successfully to Cloudinary: {}", publicId);

            return secureUrl;
        } catch (IOException e) {
            log.error("Error uploading file to Cloudinary: {}", e.getMessage());
            throw new Exception("Lỗi khi tải file lên Cloudinary: " + e.getMessage(), e);
        }
    }


    public String getFileUrl(String objectName) {
        try {
            String resourceType = getResourceType(objectName);
            String publicIdWithFormat = getPublicIdWithFormat(objectName);

            return cloudinary.url().resourceType(resourceType).generate(publicIdWithFormat);
        } catch (Exception e) {
            log.error("Error generating Cloudinary URL for object: {}", objectName, e);
            return null;
        }
    }

    public void deleteFile(String objectName) {
        if (objectName == null || objectName.isBlank()) {
            log.warn("Attempted to delete a file with null or blank object name.");
            return;
        }

        try {
            String decodedObjectName = URLDecoder.decode(objectName, StandardCharsets.UTF_8);

            String resourceType = getResourceType(decodedObjectName);
            String publicId = getPublicId(decodedObjectName);

            Map options = ObjectUtils.asMap("resource_type", resourceType);

            cloudinary.uploader().destroy(publicId, options);
            log.info("Successfully deleted file from Cloudinary: {}", publicId);
        } catch (IOException e) {
            log.error("Could not delete file '{}' from Cloudinary: {}", objectName, e.getMessage());
        } catch (Exception e) {
            log.error("Error parsing objectName for deletion: {}", objectName, e);
        }
    }

    public String getPresignedDownloadUrl(String objectName) throws Exception {
        if (objectName == null || objectName.isBlank()) {
            return null;
        }

        try {
            String resourceType = getResourceType(objectName);
            String publicIdWithFormat = getPublicIdWithFormat(objectName);

            return cloudinary.url().resourceType(resourceType).secure(true).signed(true)
                    .generate(publicIdWithFormat);

        } catch (Exception e) {
            log.error("Lỗi khi tạo pre-signed URL cho '{}': {}", objectName, e.getMessage());
            throw new Exception("Không thể tạo URL tải xuống: " + e.getMessage(), e);
        }
    }



    private String getResourceType(String objectName) {
        int slashIndex = objectName.indexOf('/');
        if (slashIndex == -1) {
            throw new IllegalArgumentException(
                    "Invalid objectName format. Expected 'resourceType/publicId.format'");
        }
        return objectName.substring(0, slashIndex);
    }

    private String getPublicId(String objectName) {
        int slashIndex = objectName.indexOf('/');
        int lastDotIndex = objectName.lastIndexOf('.');
        if (slashIndex == -1 || lastDotIndex == -1 || lastDotIndex < slashIndex) {
            throw new IllegalArgumentException(
                    "Invalid objectName format. Expected 'resourceType/publicId.format'");
        }
        return objectName.substring(slashIndex + 1, lastDotIndex);
    }

    private String getPublicIdWithFormat(String objectName) {
        int slashIndex = objectName.indexOf('/');
        if (slashIndex == -1) {
            throw new IllegalArgumentException(
                    "Invalid objectName format. Expected 'resourceType/publicId.format'");
        }
        return objectName.substring(slashIndex + 1);
    }
}
