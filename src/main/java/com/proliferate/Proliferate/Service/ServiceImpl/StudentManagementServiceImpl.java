package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Schedule;
import com.proliferate.Proliferate.Domain.DTO.Student.ScoreDto;
import com.proliferate.Proliferate.Domain.DTO.Student.StudentTable;
import com.proliferate.Proliferate.Domain.DTO.Student.SubjectDto;
import com.proliferate.Proliferate.Domain.DTO.Student.TestDto;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.AssignmentNotCreatedException;
import com.proliferate.Proliferate.ExeceptionHandler.AssignmentNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.SubjectNotFoundException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.*;
import com.proliferate.Proliferate.Service.StudentManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentManagementServiceImpl implements StudentManagementService {

    private final AssignmentRepository assignmentRepository;

    private final AttendanceRepository attendanceRepository;

    private final ClassScheduleRepository classScheduleRepository;
    private final StudentRepository studentRepository;
    private final ScoreRepository scoreRepository;
    private final SubjectRepository subjectRepository;
    private final TestRepository testRepository;

    private final TutorRepository tutorRepository;

    private final Mapper<StudentEntity, StudentTable> studentMapper;
    private final Mapper<Assignment, AssignmentDto> assignmentMapper;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes

    public ResponseEntity<?> createAssignment(AssignmentDto assignmentDto) {
        try {
            if (!validateFileSize(assignmentDto.getAssignmentFile())) {
                throw new AssignmentNotCreatedException("Assignment attachment exceeds the maximum allowed size of 5MB");
            }
            Subject subject = subjectRepository.findByTitle(assignmentDto.getSubjectName()).orElseThrow(() -> new SubjectNotFoundException("Subject not present"));
            StudentEntity student = studentRepository.findByFirstName(assignmentDto.getAssignedStudentName()).orElseThrow(() -> new UserNotFoundException("Student not present"));
            Assignment assignment = assignmentMapper.mapFrom(assignmentDto);
            if (!assignmentDto.getAssignmentFile().isEmpty() && assignmentDto.getAssignmentFile() != null) {
                assignment.setAssignmentFile(assignmentDto.getAssignmentFile().getBytes());
            }
            assignment.setSubject(subject);
            assignment.setAssignedStudent(student);
            assignmentRepository.save(assignment);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (AssignmentNotCreatedException | IOException error) {
            throw new AssignmentNotCreatedException("Assignment could not be created");
        }
    }

    public List<AssignmentDto> getAllAssignments() {
        return assignmentRepository.findAll().stream()
                .map(assignment -> {
                    AssignmentDto dto = assignmentMapper.mapTo(assignment);
                    dto.setAssignedStudentName(assignment.getAssignedStudent().getFirstName());
                    if (assignment.getAssignmentFile() != null) {
                        String base64File = Base64.getEncoder().encodeToString(assignment.getAssignmentFile());
                        //String fileType = determineFileType(base64File);
                        dto.setAssignmentFileBase64(base64File);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

//    private String determineFileType(String base64File) {
//        // This is a simplified method to determine the file type based on its content
//        // You may want to use a more robust method in a real-world application
//        if (base64File.startsWith("JVBERi0")) {
//            return "application/pdf";
//        } else if (base64File.startsWith("/9j/")) {
//            return "image/jpeg";
//        } else if (base64File.startsWith("iVBORw0KGgo")) {
//            return "image/png";
//        } else {
//            return "application/octet-stream"; // Default fallback
//        }
//    }

    public void deleteAssignment(Long assignmentId) {
        Optional<Assignment> assignment = assignmentRepository.findById(assignmentId);
        if (assignment.isPresent()) {
            assignmentRepository.deleteById(assignmentId);
        } else {
            throw new AssignmentNotFoundException("Assignment not found with id: " + assignmentId);
        }
    }

    private boolean validateFileSize(MultipartFile file) {
        List<String> allowedContentTypes = Arrays.asList("application/pdf", "image/png", "image/jpeg");

        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType)) {
            return false;
        }

        return file.getSize() <= MAX_FILE_SIZE;
    }

    public Iterable<StudentEntity> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<StudentEntity> getStudentProfile(Long studentId) {
        return studentRepository.findById(studentId);
    }

    public List<AttendanceEntity> getAllAttendanceRecords() {
        return attendanceRepository.findAll();
    }

    public AttendanceEntity addAttendanceRecord(AttendanceEntity attendanceEntity) {
        return attendanceRepository.save(attendanceEntity);
    }


    public List<ClassSchedule> getTutorSchedule(Long tutorId) {
        return classScheduleRepository.findByTutorTutorId(tutorId);
    }

    public Score addScore(ScoreDto scoreDto) {
        Score score = new Score();
        score.setStudent(studentRepository.findById(scoreDto.getStudentId()).orElseThrow(() -> new UserNotFoundException("Tutor not found")));
        score.setTest(testRepository.findById(scoreDto.getTestId()).orElseThrow(() -> new UserNotFoundException("Tutor not found")));
        score.setMarks(scoreDto.getMarks());
        score.setQuestionsAttempted(scoreDto.getQuestionsAttempted());
        score.setCorrectAnswers(scoreDto.getCorrectAnswers());
        score.setWrongAnswers(scoreDto.getWrongAnswers());
        score.setResult(scoreDto.getResult());

        return scoreRepository.save(score);
    }

    public Test addTest(TestDto testDto) {
        Test test = new Test();
        test.setTestTitle(testDto.getTestTitle());
        test.setTestDate(testDto.getTestDate());
        test.setTotalMarks(testDto.getTotalMarks());

        return testRepository.save(test);
    }
}