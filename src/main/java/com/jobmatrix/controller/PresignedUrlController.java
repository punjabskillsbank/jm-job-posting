package com.jobmatrix.controller;

import com.jobmatrix.dto.PresignedUrlRequestDTO;
import com.jobmatrix.dto.PresignedUrlResponse;
import com.jobmatrix.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presigned_url")
@RequiredArgsConstructor
public class PresignedUrlController {

    private final FileService fileService;

    @PostMapping("/upload/job_attachment")
    public ResponseEntity<List<PresignedUrlResponse>> generateUploadUrlsForJobAttachments(
            @RequestBody PresignedUrlRequestDTO request) {

        List<PresignedUrlResponse> responses = fileService.generateMultipleJobAttachmentUrls(
                String.valueOf(request.getJob_posting_id()),
                request.getFile()
        );
        return ResponseEntity.ok(responses);
    }

}