package com.kaizen.gym_api.service;

import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.model.Workout;
import com.kaizen.gym_api.model.WorkoutSet;
import com.kaizen.gym_api.model.enums.WorkoutSetType;
import com.kaizen.gym_api.repository.WorkoutRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private static final BigDecimal KG_TO_LB = new BigDecimal("2.20462");

    private final JavaMailSender mailSender;
    private final WorkoutRepository workoutRepository;

    @Async
    public void sendWorkoutSummary(User user, String routineName, Workout workout, List<WorkoutSet> sets) {
        try {
            long weeklyCount = countWorkoutsThisWeek(user.getId());
            boolean useImperial = "imperial".equalsIgnoreCase(user.getUnitSystem());
            String effortLabel = "RIR".equalsIgnoreCase(user.getEffortMeasurement()) ? "RIR" : "RPE";

            String displayName = user.getUsername();
            String subject = "Workout logged — keep pushing, " + displayName + "!";
            String body = buildHtml(displayName, routineName, workout, sets, weeklyCount, useImperial, effortLabel);

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

    // ── Stat helpers ──────────────────────────────────────────────────────

    private String formatDuration(Workout workout) {
        Timestamp start = workout.getStartTime();
        Timestamp end = workout.getEndTime();
        if (start == null || end == null) return "—";
        long totalSeconds = (end.getTime() - start.getTime()) / 1000;
        if (totalSeconds < 0) return "—";
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0) return String.format("%dh %02dm %02ds", hours, minutes, seconds);
        return String.format("%dm %02ds", minutes, seconds);
    }

    private String formatTonnage(List<WorkoutSet> sets, boolean useImperial) {
        BigDecimal tonnage = BigDecimal.ZERO;
        for (WorkoutSet s : sets) {
            if (s.getWeightKg() != null && s.getReps() != null) {
                tonnage = tonnage.add(s.getWeightKg().multiply(new BigDecimal(s.getReps())));
            }
        }
        if (useImperial) {
            tonnage = tonnage.multiply(KG_TO_LB);
        }
        tonnage = tonnage.setScale(1, RoundingMode.HALF_UP);
        return tonnage.stripTrailingZeros().toPlainString() + (useImperial ? " lb" : " kg");
    }

    private long countFailureSets(List<WorkoutSet> sets) {
        return sets.stream()
                .filter(s -> s.getType() == WorkoutSetType.FAILURE)
                .count();
    }

    private String formatAverageEffort(List<WorkoutSet> sets, String effortLabel) {
        List<Integer> values = sets.stream()
                .filter(s -> s.getRpe() != null)
                .map(WorkoutSet::getRpe)
                .toList();
        if (values.isEmpty()) return "—";
        double avg = values.stream().mapToInt(Integer::intValue).average().orElse(0);
        return String.format("%.1f %s", avg, effortLabel);
    }

    // ── HTML builder ──────────────────────────────────────────────────────

    private String buildHtml(String name, String routineName, Workout workout,
                             List<WorkoutSet> sets, long weeklyCount,
                             boolean useImperial, String effortLabel) {

        String routine = (routineName != null && !routineName.isBlank()) ? routineName : "Ad-hoc Workout";
        String duration = formatDuration(workout);
        String tonnage = formatTonnage(sets, useImperial);
        int totalSets = sets.size();
        long failureSets = countFailureSets(sets);
        String avgEffort = formatAverageEffort(sets, effortLabel);
        String notes = (workout.getNotes() != null && !workout.getNotes().isBlank())
                ? escapeHtml(workout.getNotes())
                : "No notes for this session.";

        String ROW = """
                <tr>
                  <td style="padding:6px 0;color:#a3a3a3;font-size:13px;width:40%%;">%s</td>
                  <td style="padding:6px 0;color:#ffffff;font-size:14px;font-weight:600;">%s</td>
                </tr>""";

        String sectionHeader = """
                <td colspan="2" style="padding:18px 0 6px;font-size:11px;font-weight:700;letter-spacing:1.5px;text-transform:uppercase;color:#4ade80;border-bottom:1px solid #2a2a2a;">
                  %s
                </td>""";

        return """
                <div style="font-family:'Segoe UI',Roboto,Arial,sans-serif;max-width:520px;margin:0 auto;padding:32px;background:#0d0d0d;color:#e0e0e0;border-radius:12px;">
                  <h2 style="margin:0 0 4px;color:#ffffff;">Hey %s,</h2>
                  <p style="font-size:15px;color:#a3a3a3;margin:0 0 24px;">Your workout has been logged successfully.</p>

                  <div style="background:#1a1a1a;border-radius:10px;padding:20px 24px;margin-bottom:20px;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="border-collapse:collapse;">
                      <tr>%s</tr>
                      %s
                      %s
                      %s

                      <tr>%s</tr>
                      %s
                      %s
                      %s

                      <tr>%s</tr>
                      <tr>
                        <td colspan="2" style="padding:10px 0 0;font-style:italic;color:#d4d4d4;font-size:14px;line-height:1.5;">
                          "%s"
                        </td>
                      </tr>
                    </table>
                  </div>

                  <div style="background:#1a1a1a;border-left:4px solid #4ade80;padding:14px 20px;border-radius:8px;margin-bottom:24px;">
                    <p style="margin:0;font-size:13px;color:#a3a3a3;">This week so far</p>
                    <p style="margin:4px 0 0;font-size:26px;font-weight:700;color:#4ade80;">%d workout%s</p>
                  </div>

                  <p style="font-size:13px;line-height:1.6;color:#a3a3a3;margin:0 0 0;">
                    Small steps, big results — that's the Kaizen way.
                  </p>
                  <p style="font-size:11px;color:#525252;margin:24px 0 0;">Kaizen Fitness</p>
                </div>
                """.formatted(
                name,
                // SESSION
                sectionHeader.formatted("Session"),
                ROW.formatted("Routine", routine),
                ROW.formatted("Tonnage", tonnage),
                ROW.formatted("Active time", duration),
                // INTENSITY
                sectionHeader.formatted("Intensity"),
                ROW.formatted("Total sets", String.valueOf(totalSets)),
                ROW.formatted("Failure sets", String.valueOf(failureSets)),
                ROW.formatted("Avg " + effortLabel, avgEffort),
                // NOTES
                sectionHeader.formatted("Notes"),
                notes,
                // WEEKLY
                weeklyCount, weeklyCount == 1 ? "" : "s"
        );
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("\n", "<br/>");
    }
}
