package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Student.StudentTable;
import com.proliferate.Proliferate.Domain.DTO.Tutor.AssignmentDto;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.AssignmentNotCreatedException;
import com.proliferate.Proliferate.Repository.AssignmentRepository;
import com.proliferate.Proliferate.Repository.AttendanceRepository;
import com.proliferate.Proliferate.Repository.ClassScheduleRepository;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Service.StudentManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentManagementServiceImpl implements StudentManagementService {

    private final AssignmentRepository assignmentRepository;

    private final AttendanceRepository attendanceRepository;

    private final ClassScheduleRepository classScheduleRepository;
    private final StudentRepository studentRepository;

    private final Mapper<StudentEntity, StudentTable> studentMapper;
    private final Mapper<Assignment, AssignmentDto> assignmentMapper;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes
	
    public ResponseEntity<?> createAssignment(AssignmentDto assignmentDto){

        try {
            if (!validateFileSize(assignmentDto.getAssignmentFile())) {
              throw new AssignmentNotCreatedException("Assignment attachment exceeds the maximum allowed size of 5MB");
            }
            Assignment assignment = assignmentMapper.mapFrom(assignmentDto);
            if (!assignmentDto.getAssignmentFile().isEmpty() && assignmentDto.getAssignmentFile() != null ) {
                assignment.setAssignmentFile(assignmentDto.getAssignmentFile().getBytes());
            }
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
                    if (assignment.getAssignmentFile() != null) {
                        String base64File = Base64.getEncoder().encodeToString(assignment.getAssignmentFile());
                        String fileType = determineFileType(base64File);
                        dto.setAssignmentFileBase64("data:" + fileType + ";base64," + base64File);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private String determineFileType(String base64File) {
        // This is a simplified method to determine the file type based on its content
        // You may want to use a more robust method in a real-world application
        if (base64File.startsWith("JVBERi0")) {
            return "application/pdf";
        } else if (base64File.startsWith("/9j/")) {
            return "image/jpeg";
        } else if (base64File.startsWith("iVBORw0KGgo")) {
            return "image/png";
        } else {
            return "application/octet-stream"; // Default fallback
        }
    }

    private boolean validateFileSize(MultipartFile file) {
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

    public ClassSchedule saveClassSchedule(ClassSchedule classSchedule) {
        return classScheduleRepository.save(classSchedule);
    }

    public List<ClassSchedule> getAllClassSchedule() {
        return classScheduleRepository.findAll();
    }

}
