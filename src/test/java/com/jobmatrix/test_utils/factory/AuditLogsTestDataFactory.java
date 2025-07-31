package com.jobmatrix.test_utils.factory;

import com.common.entity.JobPosting;
import com.jobmatrix.dto.JobPostingAuditDTO;
import com.jobmatrix.dto.JobPostingUpdateRequest;

import java.util.UUID;

public class AuditLogsTestDataFactory {

    public static Long sampleJobId() {
        return 1001L;
    }

    public static UUID sampleClientId() {
        return UUID.fromString("11111111-1111-1111-1111-111111111111");
    }

    public static JobPostingUpdateRequest createUpdateRequest() {
        JobPostingUpdateRequest request = new JobPostingUpdateRequest();
        request.setClientId(sampleClientId());
        return request;
    }

    public static JobPosting createOldJobPosting() {
        JobPosting job = new JobPosting();
        job.setJobPostingId(sampleJobId());
        job.setDescription("Old Description");
        return job;
    }

    public static JobPosting createNewJobPosting() {
        JobPosting job = new JobPosting();
        job.setJobPostingId(sampleJobId());
        job.setDescription("New Description");
        return job;
    }

    public static JobPostingAuditDTO createOldAuditDTO() {
        JobPostingAuditDTO dto = new JobPostingAuditDTO();
        dto.setDescription("Old Description");
        return dto;
    }

    public static JobPostingAuditDTO createNewAuditDTO() {
        JobPostingAuditDTO dto = new JobPostingAuditDTO();
        dto.setDescription("New Description");
        return dto;
    }
}
