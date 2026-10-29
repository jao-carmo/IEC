package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.*;
import br.com.fatecads.fatecads.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Creates the academic record and its optional login together, or rolls both back. */
@Service
public class AcademicRegistrationService {
    private final StudentService students;
    private final ProfessorService professors;
    private final UserService users;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;

    public AcademicRegistrationService(StudentService students, ProfessorService professors, UserService users,
            StudentRepository studentRepository, ProfessorRepository professorRepository) {
        this.students = students; this.professors = professors; this.users = users;
        this.studentRepository = studentRepository; this.professorRepository = professorRepository;
    }

    @Transactional
    public void saveStudent(Student student, boolean createAccount, String login, String password) {
        User existing = student.getStudentId() == null ? null : studentRepository.findById(student.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found.")).getAccount();
        student.setAccount(existing);
        if (student.getAcademicStatus() == null) throw new IllegalArgumentException("Select an academic status.");
        if (createAccount) {
            if (existing != null) throw new IllegalArgumentException("This student already has a login account. Use Manage account to edit it.");
            student.setAccount(createUser(student.getFullName(), student.getEmail(), login, password));
        }
        students.save(student);
    }

    @Transactional
    public void saveProfessor(Professor professor, boolean createAccount, String email, String login, String password) {
        User existing = professor.getProfessorId() == null ? null : professorRepository.findById(professor.getProfessorId())
                .orElseThrow(() -> new IllegalArgumentException("Professor not found.")).getAccount();
        professor.setAccount(existing);
        if (createAccount) {
            if (existing != null) throw new IllegalArgumentException("This professor already has a login account. Use Manage account to edit it.");
            professor.setAccount(createUser(professor.getFullName(), email, login, password));
        }
        professors.save(professor);
    }

    private User createUser(String fullName, String email, String login, String password) {
        User account = new User();
        account.setFullName(fullName); account.setEmail(email); account.setLogin(login);
        account.setPassword(password); account.setRole("ROLE_USER");
        return users.save(account);
    }
}
