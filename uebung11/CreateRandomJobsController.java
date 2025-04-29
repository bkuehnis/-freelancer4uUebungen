package ch.zhaw.freelancer4u.controller;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.freelancer4u.model.Job;

@RestController
public class CreateRandomJobsController {
    
    @Autowired
    ChatClient chatClient;

    @GetMapping("/randomjob")
    public ResponseEntity<Job> randomJob() {
        
        Job randomJob = chatClient.prompt(
        "Create a random job")
        .call()
        .entity(Job.class);

        return ResponseEntity.status(HttpStatus.OK).body(randomJob);
    }

    @GetMapping("/randomjobs")
    public ResponseEntity<List<Job>> randomJobs() {
        
        List<Job> randomJobs = chatClient.prompt(
        "Create three random jobs")
        .call()
        .entity(new ParameterizedTypeReference<List<Job>>(){});

        return new ResponseEntity<>(randomJobs, HttpStatus.OK);
    }
}
