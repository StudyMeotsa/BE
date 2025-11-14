package com.checklist.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.Duration;

@Service
public class TimerService {

    public Long calculateDurationMinutes(LocalDateTime start, LocalDateTime end) {
        if (start == null) { return 0L; }
        LocalDateTime actualEnd = (end != null) ? end : LocalDateTime.now(); 
        Duration duration = Duration.between(start, actualEnd);
        return duration.toMinutes();
    }
}