package com.jobmatrix.controller;

import com.common.dto.PresignedUrlResponseDTO;
import com.jobmatrix.dto.PresignedUrlRequestDTO;
import com.common.util.S3FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/presigned_url")
@RequiredArgsConstructor
public class PresignedUrlController {

    private final S3FileUtil fileService;

    @PostMapping("/upload/job_attachment")
    public ResponseEntity<Map<String, PresignedUrlResponseDTO>> generateUploadUrlsForJobAttachments(
            @RequestBody PresignedUrlRequestDTO request) {

        Map<String, PresignedUrlResponseDTO> responses = fileService.generateMultipleJobAttachmentUrls(
                String.valueOf(request.getJob_posting_id()),
                request.getFileNames(),
                "job-attachments/"
        );
        return ResponseEntity.ok(responses);
    }

}