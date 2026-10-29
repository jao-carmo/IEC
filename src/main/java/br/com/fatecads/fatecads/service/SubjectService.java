package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.Subject;
import br.com.fatecads.fatecads.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public void save(Subject subject) {
        if (subject == null) {
            return;
        }
        RecordValidation.text(subject.getSubjectName(), 100, "Subject name");
        RecordValidation.text(subject.getCode(), 15, "Subject code");
        RecordValidation.workload(subject.getWorkloadHours());
        if (subject.getCourse() == null || subject.getProfessor() == null)
            throw new IllegalArgumentException("Select a course and professor.");
        subjectRepository.save(subject);
    }

    public List<Subject> findAll() { return subjectRepository.findAll(); }

    public void deleteById(Integer id) {
        subjectRepository.deleteById(id);
    }

    public Subject findById(Integer id) {
        return subjectRepository.findById(id).orElse(null);
    }
}
