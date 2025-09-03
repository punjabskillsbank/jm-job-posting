package com.jobmatrix.service;

import com.common.dto.PresignedUrlResponseDTO;

import java.util.Map;
import java.util.Set;

public interface FileService {

    /**
     * Generates presigned URLs for uploading and downloading job posting attachements
     *
     * @param jobId      the ID of the job post
     * @param originalFilenames the MIME type of the file
     * @return an array containing [uploadUrl, downloadUrl]
     */
    Map<String, PresignedUrlResponseDTO> generateMultipleJobAttachmentUrls(String jobId, Set<String> originalFilenames);

}