package com.jobmatrix.aspect;

import com.common.entity.JobPosting;
import com.common.exceptionHandling.JobPostingNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmatrix.dto.JobPostingAuditDTO;
import com.jobmatrix.dto.JobPostingUpdateRequest;
import com.jobmatrix.kafka.AuditKafkaProducer;
import com.jobmatrix.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLoggingAspect {

    private final ModelMapper modelMapper;
    private final JobPostingRepository jobPostingRepository;
    private final AuditKafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(AuditLoggingAspect.class);
    
    // ThreadLocal to track if we're already in an audit context
    private static final ThreadLocal<Boolean> AUDIT_IN_PROGRESS = ThreadLocal.withInitial(() -> false);

    @Around("execution(* com.jobmatrix..*.updateJobPosting(..))")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object logUpdateJob(ProceedingJoinPoint joinPoint) throws Throwable {
        // Skip if we're already in an audit context to prevent duplicate logging
        if (AUDIT_IN_PROGRESS.get()) {
            return joinPoint.proceed();
        }
        
        try {
            AUDIT_IN_PROGRESS.set(true);
            logger.info(">> AuditLoggingAspect triggered for method: {}", joinPoint.getSignature());

            Object[] args = joinPoint.getArgs();
            Long jobId = (Long) args[0];
            JobPostingUpdateRequest request = (JobPostingUpdateRequest) args[1];

            // Extract userId from request
            UUID userId = request.getClientId();

            // Fetch old data
            JobPosting oldEntity = jobPostingRepository.findById(jobId)
                    .orElseThrow(() -> new JobPostingNotFoundException(jobId));
            JobPostingAuditDTO oldData = modelMapper.map(oldEntity, JobPostingAuditDTO.class);

            // Proceed with update method
            Object result = joinPoint.proceed();

            // Fetch updated data in a new transaction
            JobPosting updatedEntity = jobPostingRepository.findById(jobId)
                    .orElseThrow(() -> new JobPostingNotFoundException(jobId));
            JobPostingAuditDTO newData = modelMapper.map(updatedEntity, JobPostingAuditDTO.class);

            // Build audit payload
            Map<String, Object> auditPayload = Map.of(
                    "serviceName", "job-posting",
                    "userId", userId,
                    "entityId", jobId,
                    "oldData", oldData,
                    "newData", newData
            );

            // Serialize and send to Kafka
            String auditLogJson = objectMapper.writeValueAsString(auditPayload);
            kafkaProducer.sendAuditLog(auditLogJson);

            return result;
        } finally {
            // Always clear the thread local to avoid memory leaks
            AUDIT_IN_PROGRESS.remove();
        }
    }
}

