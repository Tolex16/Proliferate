package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.Subject;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByTutor(TutorEntity tutor);

    Optional<Subject> findByTitle(String title);

    Optional<Subject> findByTutorTutorId(long tutorId);
	
	long countByTutorTutorId(Long tutorId);
	
	Optional<Subject> findFirstByTutorTutorId(Long tutorId); // Ensure only one result

}
