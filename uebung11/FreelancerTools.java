package ch.zhaw.freelancer4u.tools;

import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import ch.zhaw.freelancer4u.model.Company;
import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobState;
import ch.zhaw.freelancer4u.model.JobType;
import ch.zhaw.freelancer4u.repository.CompanyRepository;
import ch.zhaw.freelancer4u.repository.JobRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FreelancerTools {
    private final JobRepository jobs;
    private final CompanyRepository companies;

    @Tool(name = "search_jobs", description = "Search open jobs by a keyword in the title or description")
    public List<Job> searchJobs(String keyword) {
        String query = keyword.strip();
        return jobs.findByJobStateAndTitleContainingIgnoreCaseOrJobStateAndDescriptionContainingIgnoreCase(
                JobState.NEW, query, JobState.NEW, query, PageRequest.of(0, 10));
    }

    @Tool(name = "create_job", description = """
            Create a demo job with a title, description, job type, earnings and company name.
            Job types are TEST, IMPLEMENT, REVIEW and OTHER. Creates the company if it is missing.
            """)
    public Job createJob(String title, String description, JobType jobType, double earnings, String companyName) {
        if (title.isBlank() || description.isBlank() || companyName.isBlank()
                || jobType == null || !Double.isFinite(earnings) || earnings < 0) {
            throw new IllegalArgumentException("Provide a title, description, job type, nonnegative earnings and company name.");
        }

        String name = companyName.strip();
        Company company = companies.findFirstByNameIgnoreCase(name)
                .orElseGet(() -> companies.save(new Company(name, "demo@example.invalid")));
        return jobs.save(new Job(title.strip(), description.strip(), jobType, earnings, company.getId()));
    }
}
