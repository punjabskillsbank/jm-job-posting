package com.jobmatrix.serviceimpl;

import com.common.dto.PresignedUrlResponseDTO;
import com.common.util.S3PresignedURLUtil;
import com.jobmatrix.exceptionHandling.InvalidFileTypeException;
import com.jobmatrix.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final S3PresignedURLUtil s3Service;

    private static final Set<String> NOT_ALLOWED_EXTENSIONS = Set.of(
            ".exe", ".msi", ".bat", ".cmd", ".com", ".scr", ".pif", ".cpl", ".gadget",
            ".sh", ".bash", ".zsh", ".ps1", ".vbs", ".js", ".jse", ".wsf", ".wsh", ".reg",
            ".dll", ".so", ".dmg", ".pkg", ".jar", ".class",
            ".iso", ".img", ".vhd", ".vhdx",
            ".lnk", ".url",
            ".xlsm", ".docm", ".pptm"
    );

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public Map<String, PresignedUrlResponseDTO> generateMultipleJobAttachmentUrls(String jobId, Set<String> originalFilenames) {
        Map<String, PresignedUrlResponseDTO> urls = new HashMap<>();


        for (String fileName : originalFilenames) {
            String extension = getExtensionFromFileName(fileName).toLowerCase();

            //Validate extension
            if (NOT_ALLOWED_EXTENSIONS.contains(extension)) {
                throw new InvalidFileTypeException(extension);
            }

            String finalFileName = sanitizeFileName(fileName);  // Safe name
            String s3Key = "job-attachments/" + jobId + "/" + finalFileName;

            String contentType = guessContentType(extension);

            URL presignedUrl = s3Service.generatePresignedUploadUrl(s3Key, contentType,bucketName);
            urls.put(fileName, new PresignedUrlResponseDTO(presignedUrl, s3Key));
        }

        return urls;
    }

    private String getExtensionFromFileName(String fileName) {
        int index = fileName.lastIndexOf('.');
        return index != -1 ? fileName.substring(index).toLowerCase() : "";
    }

    private String guessContentType(String ext) {
        return switch (ext) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".webp" -> "image/webp";
            case ".pdf" -> "application/pdf";
            case ".doc" -> "application/msword";
            case ".docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".ppt" -> "application/vnd.ms-powerpoint";
            case ".pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case ".mp4" -> "video/mp4";
            case ".mp3" -> "audio/mpeg";
            case ".wav" -> "audio/wav";
            default -> "application/octet-stream"; // fallback
        };
    }

    // Replace spaces and reserved characters (/ \ ? % * : | " < > #) with underscore
    // these characters may break the URL during presigned upload as they have special meaning in the URLS
    // So replacing them make the file name remains safe for uploading to S3.
    private String sanitizeFileName(String fileName) {
        return fileName.trim().replaceAll("[\\s/\\\\?%*:|\"<>#]", "_");
    }

}
