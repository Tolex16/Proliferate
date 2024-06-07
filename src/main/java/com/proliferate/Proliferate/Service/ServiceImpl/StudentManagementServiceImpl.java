package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Student.StudentTable;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.Entities.Assignment;
import com.proliferate.Proliferate.Domain.Entities.AttendanceEntity;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.Repository.AssignmentRepository;
import com.proliferate.Proliferate.Repository.AttendanceRepository;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Service.StudentManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentManagementServiceImpl implements StudentManagementService {

    private final AssignmentRepository assignmentRepository;

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    private final Mapper<StudentEntity, StudentTable> studentMapper;
    private final Mapper<Assignment, AssignmentDto> assignmentMapper;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes
	
    public ResponseEntity<?> createAssignment(AssignmentDto assignmentDto, MultipartFile assignmentFile){

        try {
            if (!validateFileSize(assignmentFile)) {
            return new ResponseEntity<>("Assignment attachment exceed the maximum allowed size of 5MB", HttpStatus.BAD_REQUEST);
            }
            Assignment assignment = assignmentMapper.mapFrom(assignmentDto);
            if (!assignmentFile.isEmpty()) {
                assignment.setAssignmentFile(assignmentFile.getBytes());
            }
			assignmentRepository.save(assignment);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    private boolean validateFileSize(MultipartFile file) {
        return file.getSize() <= MAX_FILE_SIZE;
    }

//	   public StudentDto getStudentProfile(Long id) {
//        return studentRepository.findById(id)
//                .map(student -> new StudentDto(
//                        student.getFirstName() + " " + student.getLastName(),
//                        student.getAge(),
//                        student.getGradeYear(),
//                        student.getAttendanceType(),
//                        student.getAvailability(),
//                        student.getAdditionalPreferences(),
//                        student.getShortTermGoals(),
//                        student.getLongTermGoals()
//                ))
//                .orElseThrow(() -> new UserNotFoundException("User not found"));
//    }
    public List<StudentEntity> getAllStudents() {
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
}
