package ch.zhaw.freelancer4u.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import ch.zhaw.freelancer4u.model.Job;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${freelancer.mail.from}")
    private String from;

    public void sendJobStatus(String recipient, Job job) {
        if (recipient == null || recipient.isBlank()) {
            log.warn("Skipping job notification because the user has no email claim.");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject("Freelancer4u job status");
        message.setText(job.getTitle() + ": " + job.getJobState());
        try {
            mailSender.send(message);
        } catch (MailException failure) {
            log.warn("Could not send notification for job {} ({})", job.getId(), failure.getClass().getSimpleName());
        }
    }
}
