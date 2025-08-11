package com.jobmatrix.service;

import com.jobmatrix.dto.PresignedUrlResponse;

import java.util.List;

public interface FileService {

    /**
     * Generates presigned URLs for uploading and downloading job posting attachements
     *
     * @param jobId      the ID of the job post
     * @param originalFilenames the MIME type of the file
     * @return an array containing [uploadUrl, downloadUrl]
     */
    List<PresignedUrlResponse> generateMultipleJobAttachmentUrls(String jobId, List<String> originalFilenames);

}