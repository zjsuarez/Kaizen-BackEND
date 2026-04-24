package com.kaizen.gym_api.service;

import com.kaizen.gym_api.dto.request.WorkoutRequest;
import com.kaizen.gym_api.dto.response.WorkoutResponse;
import com.kaizen.gym_api.dto.response.WorkoutSetResponse;
import com.kaizen.gym_api.model.Exercise;
import com.kaizen.gym_api.model.Routine;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.model.Workout;
import com.kaizen.gym_api.model.WorkoutSet;
import com.kaizen.gym_api.repository.ExerciseRepository;
import com.kaizen.gym_api.repository.RoutineRepository;
import com.kaizen.gym_api.repository.UserRepository;
import com.kaizen.gym_api.repository.WorkoutRepository;
import com.kaizen.gym_api.repository.WorkoutSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional
    public WorkoutResponse createWorkout(String email, WorkoutRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Routine routine = null;
        if (request.getRoutineId() != null && !request.getRoutineId().isEmpty()) {
            routine = routineRepository.findByIdAndOwner_Email(request.getRoutineId(), email)
                    .orElseThrow(() -> new RuntimeException("Routine not found or doesn't belong to user"));
        }

        Workout workout = Workout.builder()
                .user(user)
                .routine(routine)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .notes(request.getNotes())
                .build();

        Workout savedWorkout = workoutRepository.save(workout);

        List<WorkoutSet> savedSets = List.of();
        if (request.getSets() != null && !request.getSets().isEmpty()) {
            savedSets = request.getSets().stream().map(req -> {
                String customExerciseId = req.getCustomExerciseId();
                String builtinExerciseKey = req.getBuiltinExerciseKey();

                boolean hasCustomExercise = customExerciseId != null && !customExerciseId.isBlank();
                boolean hasBuiltinExercise = builtinExerciseKey != null && !builtinExerciseKey.isBlank();

                if (hasCustomExercise == hasBuiltinExercise) {
                    throw new RuntimeException("Exactly one of customExerciseId or builtinExerciseKey must be provided");
                }

                Exercise customExercise = null;
                if (hasCustomExercise) {
                    customExercise = exerciseRepository
                            .findByIdAndCreatedByUser_EmailAndIsCustomTrue(customExerciseId, email)
                            .orElseThrow(() -> new RuntimeException("Custom exercise not found or does not belong to user"));
                }

                String normalizedBuiltinExerciseKey = hasBuiltinExercise ? builtinExerciseKey.trim() : null;

                // PR Detection logic
                boolean isPR = false;
                if (req.getWeightKg() != null && req.getReps() != null) {
                    BigDecimal maxHistoricVolume = hasCustomExercise
                            ? workoutSetRepository.findMaxVolumeByCustomExerciseAndUser(email, customExercise.getId())
                            : workoutSetRepository.findMaxVolumeByBuiltinExerciseKeyAndUser(email, normalizedBuiltinExerciseKey);
                    BigDecimal currentVolume = req.getWeightKg().multiply(new BigDecimal(req.getReps()));
                    
                    if (maxHistoricVolume == null || currentVolume.compareTo(maxHistoricVolume) > 0) {
                        isPR = true;
                    }
                }

                WorkoutSet set = WorkoutSet.builder()
                        .workout(savedWorkout)
                    .customExercise(customExercise)
                    .builtinExerciseKey(normalizedBuiltinExerciseKey)
                        .setNumber(req.getSetNumber())
                        .weightKg(req.getWeightKg())
                        .reps(req.getReps())
                        .rpe(req.getRpe())
                        .value(req.getValue())
                        .type(req.getType())
                        .isPR(isPR)
                        .build();
                return workoutSetRepository.save(set);
            }).collect(Collectors.toList());
        }

        return mapToResponse(savedWorkout, savedSets);
    }

    public List<WorkoutResponse> getAllWorkouts(String email) {
        List<Workout> workouts = workoutRepository.findByUser_EmailOrderByStartTimeDesc(email);
        return workouts.stream().map(w -> {
            // Lazy loading or direct fetch might be needed, currently depending on JPA relations 
            // Better to use custom repository method if N+1 becomes an issue, but standard for now
            // To avoid N+1, workoutSetRepository should arguably be used: 
            return mapToResponse(w, w.getId());
        }).collect(Collectors.toList());
    }

    public WorkoutResponse getWorkoutById(String email, String workoutId) {
        Workout workout = workoutRepository.findByIdAndUser_Email(workoutId, email)
                .orElseThrow(() -> new RuntimeException("Workout not found"));
        return mapToResponse(workout, workout.getId());
    }

    private WorkoutResponse mapToResponse(Workout workout, String workoutId) {
        // Find sets explicitly since they aren't mapped in Workout.java entity (it's unidirectional from WorkoutSet)
        List<WorkoutSet> sets = workoutSetRepository.findAll().stream()
                .filter(ws -> ws.getWorkout().getId().equals(workoutId))
                .collect(Collectors.toList()); // Note: In production, add findByWorkout_Id to WorkoutSetRepository
        return mapToResponse(workout, sets);
    }

    private WorkoutResponse mapToResponse(Workout workout, List<WorkoutSet> sets) {
        List<WorkoutSetResponse> setResponses = sets.stream().map(s -> WorkoutSetResponse.builder()
                .id(s.getId())
                .customExerciseId(s.getCustomExercise() != null ? s.getCustomExercise().getId() : null)
                .builtinExerciseKey(s.getBuiltinExerciseKey())
                .exerciseName(s.getCustomExercise() != null ? s.getCustomExercise().getName() : null)
                .setNumber(s.getSetNumber())
                .weightKg(s.getWeightKg())
                .reps(s.getReps())
                .rpe(s.getRpe())
                .value(s.getValue())
                .type(s.getType())
                .isPR(s.getIsPR())
                .build()
        ).collect(Collectors.toList());

        return WorkoutResponse.builder()
                .id(workout.getId())
                .routineId(workout.getRoutine() != null ? workout.getRoutine().getId() : null)
                .routineName(workout.getRoutine() != null ? workout.getRoutine().getName() : null)
                .startTime(workout.getStartTime())
                .endTime(workout.getEndTime())
                .notes(workout.getNotes())
                .sets(setResponses)
                .build();
    }
}
