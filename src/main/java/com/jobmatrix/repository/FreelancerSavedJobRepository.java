package com.jobmatrix.repository;

import com.jobmatrix.entity.FreelancerSavedJobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FreelancerSavedJobRepository extends JpaRepository<FreelancerSavedJobPosting, Long> {

    boolean existsByFreelancerIdAndJobPostingId(UUID freelancerId, Long jobPostingId);

    void deleteByFreelancerIdAndJobPostingId(UUID freelancerId, Long jobPostingId);

    List<FreelancerSavedJobPosting> findAllByFreelancerId(UUID freelancerId);
}
