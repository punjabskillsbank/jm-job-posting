package com.jobmatrix.aspect;

import com.common.entity.JobPosting;
import com.common.exceptionHandling.JobPostingNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmatrix.dto.JobPostingAuditDTO;
import com.jobmatrix.dto.JobPostingUpdateRequest;
import com.jobmatrix.kafka.AuditKafkaProducer;
import com.jobmatrix.repository.JobPostingRepository;
import com.jobmatrix.test_utils.factory.AuditLogsTestDataFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditLoggingAspectTest {

    private ModelMapper modelMapper;
    private JobPostingRepository jobPostingRepository;
    private AuditKafkaProducer kafkaProducer;
    private ObjectMapper objectMapper;
    private AuditLoggingAspect auditAspect;

    @BeforeEach
    void setUp() {
        modelMapper = mock(ModelMapper.class);
        jobPostingRepository = mock(JobPostingRepository.class);
        kafkaProducer = mock(AuditKafkaProducer.class);
        objectMapper = mock(ObjectMapper.class);
        auditAspect = new AuditLoggingAspect(modelMapper, jobPostingRepository, kafkaProducer, objectMapper);
    }

    @Test
    void testAuditLogSuccess() throws Throwable {
        Long jobId = AuditLogsTestDataFactory.sampleJobId();
        JobPostingUpdateRequest request = AuditLogsTestDataFactory.createUpdateRequest();
        JobPosting oldJob = AuditLogsTestDataFactory.createOldJobPosting();
        JobPosting newJob = AuditLogsTestDataFactory.createNewJobPosting();
        JobPostingAuditDTO oldDTO = AuditLogsTestDataFactory.createOldAuditDTO();
        JobPostingAuditDTO newDTO = AuditLogsTestDataFactory.createNewAuditDTO();

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("updateJobPosting");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{jobId, request});
        when(joinPoint.proceed()).thenReturn("result");

        when(jobPostingRepository.findById(jobId))
                .thenReturn(Optional.of(oldJob))  // before update
                .thenReturn(Optional.of(newJob)); // after update

        when(modelMapper.map(oldJob, JobPostingAuditDTO.class)).thenReturn(oldDTO);
        when(modelMapper.map(newJob, JobPostingAuditDTO.class)).thenReturn(newDTO);
        when(objectMapper.writeValueAsString(any())).thenReturn("{json}");

        Object result = auditAspect.logUpdateJob(joinPoint);

        assertEquals("result", result);
        verify(kafkaProducer).sendAuditLog("{json}");
        verify(objectMapper).writeValueAsString(any());
        verify(modelMapper, times(2)).map(any(), eq(JobPostingAuditDTO.class));
        verify(jobPostingRepository, times(2)).findById(jobId);
    }

    @Test
    void testJobPostingNotFoundThrowsException() {
        Long jobId = AuditLogsTestDataFactory.sampleJobId();
        JobPostingUpdateRequest request = AuditLogsTestDataFactory.createUpdateRequest();

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("updateJobPosting");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{jobId, request});

        when(jobPostingRepository.findById(jobId)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(JobPostingNotFoundException.class,
                () -> auditAspect.logUpdateJob(joinPoint));

        assertEquals("JobPosting not found at given jobPostingId: " + jobId, exception.getMessage());
        verifyNoInteractions(kafkaProducer);
    }

    @Test
    void testAuditSkippedWhenThreadLocalSet() throws Throwable {
        Long jobId = AuditLogsTestDataFactory.sampleJobId();
        JobPostingUpdateRequest request = AuditLogsTestDataFactory.createUpdateRequest();

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("updateJobPosting");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{jobId, request});
        when(joinPoint.proceed()).thenReturn("skipped");

        // Simulate thread-local set (e.g., nested call)
        setAuditInProgress(true);

        try {
            Object result = auditAspect.logUpdateJob(joinPoint);
            assertEquals("skipped", result);
            verifyNoInteractions(jobPostingRepository);
            verifyNoInteractions(kafkaProducer);
        } finally {
            // Clean up thread-local in finally block to ensure it's always cleaned up
            setAuditInProgress(false);
        }
    }

    private void setAuditInProgress(boolean value) {
        try {
            Field field = AuditLoggingAspect.class.getDeclaredField("AUDIT_IN_PROGRESS");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            ThreadLocal<Boolean> auditInProgress = (ThreadLocal<Boolean>) field.get(null);
            if (value) {
                auditInProgress.set(true);
            } else {
                auditInProgress.remove();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set AUDIT_IN_PROGRESS", e);
        }
    }
}
