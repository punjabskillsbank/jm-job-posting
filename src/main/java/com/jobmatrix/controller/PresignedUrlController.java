package com.jobmatrix.controller;

import com.common.dto.PresignedUrlResponseDTO;
import com.common.dto.PresignedUrlRequestDTO;
import com.jobmatrix.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/presigned_url")
@RequiredArgsConstructor
public class PresignedUrlController {

    private final FileService fileService;

    @PostMapping("/upload/job_attachment")
    public ResponseEntity<Map<String, PresignedUrlResponseDTO>> generateUploadUrlsForJobAttachments(
            @RequestBody PresignedUrlRequestDTO request) {

        Map<String, PresignedUrlResponseDTO> responses = fileService.generateMultipleJobAttachmentUrls(
                String.valueOf(request.getJob_posting_id()),
                request.getFileNames()
        );
        return ResponseEntity.ok(responses);
    }

}