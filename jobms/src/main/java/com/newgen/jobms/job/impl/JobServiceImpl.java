package com.newgen.jobms.job.impl;


import com.newgen.jobms.job.Job;
import com.newgen.jobms.job.JobRepository;
import com.newgen.jobms.job.JobService;
import com.newgen.jobms.job.clients.CompanyClient;
import com.newgen.jobms.job.clients.ReviewClient;
import com.newgen.jobms.job.dto.JobDTO;
import com.newgen.jobms.job.external.Company;
import com.newgen.jobms.job.external.Review;
import com.newgen.jobms.job.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

//    @Autowired
//    RestTemplate restTemplate;
    JobRepository jobRepository;

    private CompanyClient companyClient;
    private ReviewClient reviewClient;

    public JobServiceImpl(JobRepository jobRepository, CompanyClient companyClient,
                          ReviewClient reviewClient) {
        this.jobRepository = jobRepository;
        this.companyClient = companyClient;
        this.reviewClient = reviewClient;
    }

    @Override
    public List<JobDTO> findAll() {
        return  jobRepository.findAll().stream().map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private JobDTO convertToDto(Job job) {
        Company company = companyClient.getCompany(job.getCompanyId());
        List<Review> reviews = reviewClient.getReviews(job.getCompanyId());

        return JobMapper.
                mapToJobWithCompanyDto(job,company,reviews);
    }

    @Override
    public void createJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        return convertToDto(job);
    }

    @Override
    public boolean deleteJobById(Long id) {
        try {
            jobRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateJob(Long id, Job updatedJob) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            job.setTitle(updatedJob.getTitle());
            job.setDescription(updatedJob.getDescription());
            job.setMinSalary(updatedJob.getMinSalary());
            job.setMaxSalary(updatedJob.getMaxSalary());
            job.setLocation("Test location 123213");
            jobRepository.save(job);
            return true;
        }
        return false;
    }
}