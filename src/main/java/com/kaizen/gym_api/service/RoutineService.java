package com.kaizen.gym_api.service;

import com.kaizen.gym_api.dto.request.RoutineExerciseRequest;
import com.kaizen.gym_api.dto.request.RoutineRequest;
import com.kaizen.gym_api.dto.response.RoutineExerciseResponse;
import com.kaizen.gym_api.dto.response.RoutineResponse;
import com.kaizen.gym_api.model.Exercise;
import com.kaizen.gym_api.model.Routine;
import com.kaizen.gym_api.model.RoutineExercise;
import com.kaizen.gym_api.model.TrainingPlan;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.ExerciseRepository;
import com.kaizen.gym_api.repository.RoutineExerciseRepository;
import com.kaizen.gym_api.repository.RoutineRepository;
import com.kaizen.gym_api.repository.TrainingPlanRepository;
import com.kaizen.gym_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final RoutineExerciseRepository routineExerciseRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional
    public RoutineResponse createRoutine(String email, RoutineRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Routine routine = Routine.builder()
                .owner(user)
                .name(request.getName())
                .description(request.getDescription())
                .schedulingValue(request.getSchedulingValue())
                .startingDate(request.getStartingDate())
                .build();

        if (request.getPlanId() != null && !request.getPlanId().isEmpty()) {
            TrainingPlan plan = trainingPlanRepository.findByIdAndUser_Email(request.getPlanId(), email)
                    .orElseThrow(() -> new RuntimeException("Training plan not found or does not belong to user"));
            routine.setPlan(plan);
        }

        Routine savedRoutine = routineRepository.save(routine);
        return mapToResponse(savedRoutine, List.of());
    }

    public List<RoutineResponse> getRoutines(String email, String planId) {
        List<Routine> routines;
        if (planId != null && !planId.isEmpty()) {
            routines = routineRepository.findByOwner_EmailAndPlan_Id(email, planId);
        } else {
            routines = routineRepository.findByOwner_Email(email);
        }

        return routines.stream().map(routine -> {
            List<RoutineExercise> exercises = routineExerciseRepository.findByRoutine_IdOrderByOrderIndexAsc(routine.getId());
            return mapToResponse(routine, exercises);
        }).collect(Collectors.toList());
    }

    public RoutineResponse getRoutineById(String email, String routineId) {
        Routine routine = routineRepository.findByIdAndOwner_Email(routineId, email)
                .orElseThrow(() -> new RuntimeException("Routine not found or does not belong to user"));
        List<RoutineExercise> exercises = routineExerciseRepository.findByRoutine_IdOrderByOrderIndexAsc(routine.getId());
        return mapToResponse(routine, exercises);
    }

    @Transactional
    public RoutineResponse updateRoutine(String email, String routineId, RoutineRequest request) {
        Routine routine = routineRepository.findByIdAndOwner_Email(routineId, email)
                .orElseThrow(() -> new RuntimeException("Routine not found or does not belong to user"));

        routine.setName(request.getName());
        routine.setDescription(request.getDescription());
        routine.setSchedulingValue(request.getSchedulingValue());
        routine.setStartingDate(request.getStartingDate());

        if (request.getPlanId() != null && !request.getPlanId().isEmpty()) {
            TrainingPlan plan = trainingPlanRepository.findByIdAndUser_Email(request.getPlanId(), email)
                    .orElseThrow(() -> new RuntimeException("Training plan not found or does not belong to user"));
            routine.setPlan(plan);
        } else {
            routine.setPlan(null); // Detach if not provided
        }

        Routine savedRoutine = routineRepository.save(routine);
        List<RoutineExercise> exercises = routineExerciseRepository.findByRoutine_IdOrderByOrderIndexAsc(routine.getId());
        return mapToResponse(savedRoutine, exercises);
    }

    @Transactional
    public void deleteRoutine(String email, String routineId) {
        Routine routine = routineRepository.findByIdAndOwner_Email(routineId, email)
                .orElseThrow(() -> new RuntimeException("Routine not found or does not belong to user"));

        // Delete associated exercises first to avoid foreign key violations
        routineExerciseRepository.deleteByRoutine_Id(routine.getId());
        routineRepository.delete(routine);
    }

    @Transactional
    public RoutineResponse updateRoutineExercises(String email, String routineId, List<RoutineExerciseRequest> exerciseRequests) {
        Routine routine = routineRepository.findByIdAndOwner_Email(routineId, email)
                .orElseThrow(() -> new RuntimeException("Routine not found or does not belong to user"));

        // Clear existing exercises
        routineExerciseRepository.deleteByRoutine_Id(routine.getId());

        // Create and save new exercises
        int orderIndex = 0;
        for (RoutineExerciseRequest req : exerciseRequests) {
            Exercise exercise = exerciseRepository.findById(req.getExerciseId())
                    .orElseThrow(() -> new RuntimeException("Exercise not found with ID: " + req.getExerciseId()));

            RoutineExercise re = RoutineExercise.builder()
                    .routine(routine)
                    .exercise(exercise)
                    .orderIndex(orderIndex++)
                    .targetSets(req.getTargetSets())
                    .targetReps(req.getTargetReps())
                    .restSeconds(req.getRestSeconds())
                    .build();

            routineExerciseRepository.save(re);
        }

        List<RoutineExercise> updatedExercises = routineExerciseRepository.findByRoutine_IdOrderByOrderIndexAsc(routine.getId());
        return mapToResponse(routine, updatedExercises);
    }

    private RoutineResponse mapToResponse(Routine routine, List<RoutineExercise> exercises) {
        List<RoutineExerciseResponse> exerciseResponses = exercises.stream().map(ex -> RoutineExerciseResponse.builder()
                .id(ex.getId())
                .exerciseId(ex.getExercise().getId())
                .exerciseName(ex.getExercise().getName())
                .orderIndex(ex.getOrderIndex())
                .targetSets(ex.getTargetSets())
                .targetReps(ex.getTargetReps())
                .restSeconds(ex.getRestSeconds())
                .build()
        ).collect(Collectors.toList());

        return RoutineResponse.builder()
                .id(routine.getId())
                .planId(routine.getPlan() != null ? routine.getPlan().getId() : null)
                .name(routine.getName())
                .description(routine.getDescription())
                .schedulingValue(routine.getSchedulingValue())
                .startingDate(routine.getStartingDate())
                .lastPerformedDate(routine.getLastPerformedDate())
                .createdAt(routine.getCreatedAt())
                .exercises(exerciseResponses)
                .build();
    }
}
