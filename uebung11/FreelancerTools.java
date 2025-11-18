package ch.zhaw.freelancer4u.tools;

import java.util.List;

import org.springframework.ai.tool.annotation.Tool;

import ch.zhaw.freelancer4u.model.Company;
import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.repository.JobRepository;
import ch.zhaw.freelancer4u.service.CompanyService;

public class FreelancerTools {
    private JobRepository jobRepository;
    private CompanyService companyService;

    public FreelancerTools(JobRepository jobRepository, CompanyService companyService) {
        
        this.jobRepository = jobRepository;
        this.companyService = companyService;
    }

    @Tool(description = "Information about the jobs in the database.")
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Tool(description = "Information about the companies in the database.")
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }
}
