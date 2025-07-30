package com.jobmatrix.service;

import com.jobmatrix.dto.PresignedUrlResponse;

public interface FileService {

    /**
     * Generates presigned URLs for uploading and downloading job posting attachements
     *
     * @param jobId      the ID of the job post
     * @param contentType the MIME type of the file
     * @return an array containing [uploadUrl, downloadUrl]
     */
    PresignedUrlResponse generateJobAttachementUrl(String jobId, String contentType);

}