package com.jobmatrix.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlRequestDTO {
    private Long job_posting_id;
    private Set<String> fileNames = new HashSet<>();
}
