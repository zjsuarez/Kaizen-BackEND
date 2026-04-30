package com.kaizen.gym_api.service;

import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.WorkoutRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final WorkoutRepository workoutRepository;

    @Async
    public void sendWorkoutSummary(User user, String routineName) {
        try {
            long weeklyCount = countWorkoutsThisWeek(user.getId());

            String displayName = user.getUsername();
            String subject = "Workout logged — keep pushing, " + displayName + "!";
            String body = buildHtml(displayName, routineName, weeklyCount);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            log.info("Workout summary email sent to {}", user.getEmail());
        } catch (MessagingException ex) {
            log.error("Failed to send workout summary email to {}: {}", user.getEmail(), ex.getMessage());
        }
    }

    private long countWorkoutsThisWeek(String userId) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        Timestamp weekStart = Timestamp.valueOf(monday.atStartOfDay());
        Timestamp weekEnd = Timestamp.valueOf(today.atTime(LocalTime.MAX));
        return workoutRepository.countByUser_IdAndEndTimeBetween(userId, weekStart, weekEnd);
    }

    private String buildHtml(String name, String routineName, long weeklyCount) {
        String routine = (routineName != null && !routineName.isBlank())
                ? routineName
                : "Ad-hoc Workout";

        return """
                <div style="font-family:'Segoe UI',Roboto,Arial,sans-serif;max-width:520px;margin:0 auto;padding:32px;background:#0d0d0d;color:#e0e0e0;border-radius:12px;">
                  <h2 style="margin:0 0 24px;color:#ffffff;">Hey %s,</h2>
                  <p style="font-size:16px;line-height:1.6;margin:0 0 16px;">
                    Your workout <strong style="color:#4ade80;">%s</strong> has been logged successfully.
                  </p>
                  <div style="background:#1a1a1a;border-left:4px solid #4ade80;padding:16px 20px;border-radius:8px;margin:24px 0;">
                    <p style="margin:0;font-size:14px;color:#a3a3a3;">This week so far</p>
                    <p style="margin:4px 0 0;font-size:28px;font-weight:700;color:#4ade80;">%d workout%s</p>
                  </div>
                  <p style="font-size:14px;line-height:1.6;color:#a3a3a3;margin:24px 0 0;">
                    Small steps, big results — that's the Kaizen way.
                  </p>
                  <p style="font-size:12px;color:#525252;margin:32px 0 0;">Kaizen Fitness</p>
                </div>
                """.formatted(name, routine, weeklyCount, weeklyCount == 1 ? "" : "s");
    }
}
