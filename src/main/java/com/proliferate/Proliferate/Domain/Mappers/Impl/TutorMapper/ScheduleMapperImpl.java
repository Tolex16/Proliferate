package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;


import com.proliferate.Proliferate.Domain.DTO.Schedule;
import com.proliferate.Proliferate.Domain.Entities.ClassSchedule;
import com.proliferate.Proliferate.Domain.Entities.Feedback;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.Repository.FeedbackRepository;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.SubjectRepository;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Data
@Component
public class ScheduleMapperImpl implements Mapper<ClassSchedule, Schedule> {

    private final ModelMapper modelMapper;
    private final StudentRepository studentRepository;

    private final SubjectRepository subjectRepository;

    private final FeedbackRepository feedbackRepository;

    @Override
    public Schedule mapTo(ClassSchedule classSchedule) {
        Schedule dto = new Schedule();
        dto.setClassScheduleId(classSchedule.getClassScheduleId());
        dto.setTime(classSchedule.getTime());
        dto.setSchedule(classSchedule.getSchedule());
        dto.setSubjectTitle(classSchedule.getSubject().getTitle());
        dto.setLocation(classSchedule.getLocation());
        dto.setReason(classSchedule.getReason());
        dto.setTutorName(classSchedule.getTutor().getFirstName() + " " + classSchedule.getTutor().getLastName());
        dto.setStudentName(classSchedule.getStudent().getFirstName() + " " + classSchedule.getStudent().getLastName());
        dto.setDate(classSchedule.getDate());
        List<Feedback> feedbacks = feedbackRepository.findByTutorTutorId(classSchedule.getTutor().getTutorId());
        double averageRating = feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(0);
        dto.setRating(averageRating);

        return dto;
    }


    @Override
    public ClassSchedule mapFrom(Schedule schedule) {
        return modelMapper.map(schedule, ClassSchedule.class);
    }

    @Override
    public Iterable<Schedule> mapListTo(Iterable<ClassSchedule> classSchedules) {
        return StreamSupport.stream(classSchedules.spliterator(), false)
                .map(this::mapTo)
                .collect(Collectors.toList());

    }
}
