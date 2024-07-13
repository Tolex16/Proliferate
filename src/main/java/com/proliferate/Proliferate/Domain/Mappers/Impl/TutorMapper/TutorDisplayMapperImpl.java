package com.proliferate.Proliferate.Domain.Mappers.Impl.TutorMapper;

import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorDisplay;
import com.proliferate.Proliferate.Domain.Entities.Feedback;
import com.proliferate.Proliferate.Domain.Entities.Subject;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.Repository.FeedbackRepository;
import com.proliferate.Proliferate.Repository.SubjectRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class TutorDisplayMapperImpl implements Mapper<TutorEntity, TutorDisplay> {
    private final ModelMapper modelMapper;

    private final FeedbackRepository feedbackRepository;

    private final TutorRepository tutorRepository;
    private final SubjectRepository subjectRepository;
    @Override
    public TutorDisplay mapTo(TutorEntity tutor) {
        TutorDisplay tutorDisplay = new TutorDisplay();
        if (tutor.getTutorImage() != null) {
            tutorDisplay.setProfileImage(Base64.getEncoder().encodeToString(tutor.getTutorImage()));
        } else {
            tutorDisplay.setProfileImage(null); // or set a default image, if applicable
        }

        tutorDisplay.setFullName(tutor.getFirstName() + " " + tutor.getLastName());

        List<Feedback> feedbacks = feedbackRepository.findByTutorTutorId(tutor.getTutorId());
        double averageRating = feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(0);
        tutorDisplay.setRatings(averageRating);
		tutorDisplay.setReviews(feedbacks.size()); // Set the number of feedbacks

        //Optional<Subject> subjectOpt = subjectRepository.findByTutorTutorId(tutor.getTutorId());
        //tutorDisplay.setSubject(subjectOpt.map(Subject::getTitle).orElse("No Subject"));
        //Optional<Subject> subjectOpt = subjectRepository.findByTutorTutorId(tutor.getTutorId());
       // subjectOpt.ifPresent(subject -> {
        //    tutorDisplay.setSubject(subject.getTitle());
            // Calculate the number of students associated with the subject
           int studentCount = (int) subjectRepository.countByTutorTutorId(tutor.getTutorId());
           // tutorDisplay.setStudents(studentCount);
        //});
		
		Optional<Subject> subjectOpt = subjectRepository.findFirstByTutorTutorId(tutor.getTutorId());
        subjectOpt.ifPresent(subject -> {
            tutorDisplay.setSubject(subject.getTitle());

            // Assuming each subject is associated with one student
            // If Subject represents many students, adjust accordingly
            //int studentCount = subject.getStudent() != null ? 1 : 0; // Count only if student is associated
			// Calculate the number of students associated with the subject
            //int studentCount = subject.getStudents().size();
            tutorDisplay.setStudents(studentCount);
        });
		
        tutorDisplay.setBio(tutor.getBio());

        return tutorDisplay;
    }

    @Override
    public TutorEntity mapFrom(TutorDisplay tutorDisplay) {
        return modelMapper.map(tutorDisplay, TutorEntity.class);
    }

    @Override
    public Iterable<TutorDisplay> mapListTo(Iterable<TutorEntity> tutorEntities) {
        return StreamSupport.stream(tutorEntities.spliterator(), false)
                .map(this::mapTo)
                .collect(Collectors.toList());
    }
}
