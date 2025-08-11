package com.jobmatrix.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlRequestDTO {
    private Long job_posting_id;
    private List<String> file;
}
