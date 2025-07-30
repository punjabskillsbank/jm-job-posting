package com.jobmatrix.serviceimpl;

import com.jobmatrix.dto.PresignedUrlResponse;
import com.jobmatrix.service.FileService;
import com.jobmatrix.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final Logger logger = Logger.getLogger(FileServiceImpl.class);
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB (if you plan to validate size later)

    private final S3Service s3Service;

    @Override
    public PresignedUrlResponse generateJobAttachementUrl(String jobId, String contentType) {
        // Generate a unique file name using UUID
        String uniqueFileName = UUID.randomUUID().toString();

        // Optional: get file extension based on content type
        String extension = getExtensionFromContentType(contentType);

        // Construct the S3 key: job-attachments/{jobId}/{uuid}.{ext}
        String s3Key = "job-attachments/" + jobId + "/" + uniqueFileName + extension;

        // Generate presigned URL
        URL uploadUrl = s3Service.generatePresignedUploadUrl(s3Key, contentType);

        return new PresignedUrlResponse(uploadUrl, s3Key);
    }

    private String getExtensionFromContentType(String contentType) {
        switch (contentType.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg": return ".jpg";
            case "image/png": return ".png";
            case "image/webp": return ".webp";
            case "application/pdf": return ".pdf";
            case "application/msword": return ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document": return ".docx";
            case "video/mp4": return ".mp4";
            default: return ""; // fallback: no extension
        }
    }
}
