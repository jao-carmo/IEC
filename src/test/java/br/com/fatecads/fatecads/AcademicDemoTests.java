package br.com.fatecads.fatecads;

import br.com.fatecads.fatecads.entity.*;
import br.com.fatecads.fatecads.repository.*;
import br.com.fatecads.fatecads.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AcademicDemoTests {
    @Autowired MockMvc mvc;
    @Autowired DemoDataService demo;
    @Autowired AcademicRegistrationService registration;
    @Autowired CourseRepository courses;
    @Autowired ProfessorRepository professors;
    @Autowired StudentRepository students;
    @Autowired SubjectRepository subjects;
    @Autowired ProductRepository products;
    @Autowired UserRepository users;

    @Test void fixturesAreRelatedAndRunOnlyOnceWithoutReplacingExistingProducts() {
        Product existing = new Product(); existing.setProductDescription("Notebook"); existing.setProductBrand("My brand");
        existing.setProductPrice(4321.0); existing.setProductUnit("unit"); existing.setImagePath("/uploads/my-notebook.png"); products.save(existing);
        assertThat(demo.seed()).isTrue();
        assertThat(courses.count()).isEqualTo(3);
        assertThat(professors.count()).isEqualTo(3);
        assertThat(subjects.count()).isEqualTo(6);
        assertThat(students.count()).isEqualTo(6);
        assertThat(products.count()).isEqualTo(8);
        assertThat(products.findById(existing.getProductId()).orElseThrow().getProductPrice()).isEqualTo(4321.0);
        assertThat(products.findById(existing.getProductId()).orElseThrow().getImagePath()).isEqualTo("/uploads/my-notebook.png");
        assertThat(students.findAll()).allMatch(s -> s.getCourse() != null && s.getAcademicStatus() != null && s.getAccount() == null);
        assertThat(subjects.findAll()).allMatch(s -> s.getCourse() != null && s.getProfessor() != null);
        // User edits and deletions are respected on subsequent application startups.
        existing.setProductPrice(999.0); products.save(existing);
        products.delete(products.findFirstByProductDescriptionIgnoreCase("Wireless Mouse").orElseThrow()); products.flush();
        assertThat(demo.seed()).isFalse();
        assertThat(products.count()).isEqualTo(7);
        assertThat(products.findById(existing.getProductId()).orElseThrow().getProductPrice()).isEqualTo(999.0);
    }

    @Test void studentRegistrationCreatesSafeLoginAndShowsOwnCourseAndStatus() throws Exception {
        Course course = course();
        mvc.perform(post("/student/save").with(user("root").roles("ADMIN")).with(csrf())
                .param("fullName", "Account Student").param("email", "academic.student@example.test")
                .param("phone", "11999999999").param("address", "Campus road, 1").param("cpf", "00000000001")
                .param("registrationNumber", "ACCOUNT-S001").param("course", "" + course.getCourseId())
                .param("academicStatus", "COMPLETED").param("createAccount", "true")
                .param("accountLogin", "academic.student").param("accountPassword", "Student!Password123")
                .param("account.userId", "" + users.findByLogin("root").orElseThrow().getUserId())
                .param("account.role", "ROLE_ADMIN"))
                .andExpect(redirectedUrl("/student/list"));
        Student saved = students.findByAccountLogin("academic.student").orElseThrow();
        assertThat(saved.getAcademicStatus()).isEqualTo(AcademicStatus.COMPLETED);
        assertThat(saved.getAccount().getRole()).isEqualTo("ROLE_USER");
        mvc.perform(formLogin("/login").user("academic.student").password("Student!Password123"))
                .andExpect(redirectedUrl("/home"));
        mvc.perform(get("/home").with(user("academic.student"))).andExpect(status().isOk())
                .andExpect(content().string(containsString("My academic profile")))
                .andExpect(content().string(containsString("Integration Course")))
                .andExpect(content().string(containsString("Completed")));
        mvc.perform(get("/home").with(user("other.student"))).andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Account Student"))));
        mvc.perform(get("/student/edit/" + saved.getStudentId()).with(user("root").roles("ADMIN")))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Manage account")));
        mvc.perform(get("/student/list").with(user("root").roles("ADMIN")))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Completed")));
        mvc.perform(post("/student/save").with(user("root").roles("ADMIN")).with(csrf())
                .param("studentId", "" + saved.getStudentId()).param("fullName", "Account Student Updated")
                .param("email", saved.getEmail()).param("phone", saved.getPhone()).param("address", saved.getAddress())
                .param("cpf", saved.getCpf()).param("registrationNumber", saved.getRegistrationNumber())
                .param("course", "" + course.getCourseId()).param("academicStatus", "ENROLLED")
                .param("account.userId", "1"))
                .andExpect(redirectedUrl("/student/list"));
        assertThat(students.findById(saved.getStudentId()).orElseThrow().getAccount().getLogin()).isEqualTo("academic.student");
    }

    @Test void professorCanReceiveLoginFromTheRegistrationForm() throws Exception {
        mvc.perform(post("/professor/save").with(user("root").roles("ADMIN")).with(csrf())
                .param("fullName", "Account Professor").param("phone", "11999999999").param("graduation", "Computer Science")
                .param("RM", "ACCOUNT-P001").param("createAccount", "true").param("accountEmail", "academic.prof@example.test")
                .param("accountLogin", "academic.prof").param("accountPassword", "Professor!Password123"))
                .andExpect(redirectedUrl("/professor/list"));
        Professor saved = professors.findByAccountLogin("academic.prof").orElseThrow();
        assertThat(saved.getAccount().getRole()).isEqualTo("ROLE_USER");
        mvc.perform(formLogin("/login").user("academic.prof").password("Professor!Password123"))
                .andExpect(redirectedUrl("/home"));
        mvc.perform(get("/home").with(user("academic.prof"))).andExpect(status().isOk())
                .andExpect(content().string(containsString("My teaching profile")));
        mvc.perform(get("/professor/edit/" + saved.getProfessorId()).with(user("root").roles("ADMIN")))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Manage account")));
        mvc.perform(get("/professor/list").with(user("root").roles("ADMIN"))).andExpect(status().isOk());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void invalidAcademicRecordRollsBackItsNewAccount() {
        Student invalid = new Student(); invalid.setFullName("Invalid Student"); invalid.setEmail("rollback@example.test");
        invalid.setCpf("bad"); invalid.setRegistrationNumber("ROLLBACK-S001");
        assertThatThrownBy(() -> registration.saveStudent(invalid, true, "rollback.student", "Rollback!Password123"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(users.findByLogin("rollback.student")).isEmpty();
        assertThat(students.findFirstByRegistrationNumber("ROLLBACK-S001")).isEmpty();
    }

    @Test void accountCreationErrorsReturnToTheAcademicForm() throws Exception {
        mvc.perform(post("/professor/save").with(user("root").roles("ADMIN")).with(csrf())
                .param("fullName", "Invalid Account").param("phone", "11999999999").param("graduation", "Science")
                .param("RM", "BAD-P001").param("createAccount", "true").param("accountEmail", "invalid@example.test")
                .param("accountLogin", "invalid.prof").param("accountPassword", "short"))
                .andExpect(view().name("professor/professorForm")).andExpect(model().attributeExists("errorMessage"))
                .andExpect(content().string(containsString("Manage users")));
    }

    @Test void storeSearchFiltersSortingAndBundledArtworkWork() throws Exception {
        demo.seed();
        mvc.perform(get("/product/list").with(user("buyer")).param("q", "mouse"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Wireless Mouse")))
                .andExpect(content().string(not(containsString("Mechanical Keyboard"))));
        var result = mvc.perform(get("/product/list").with(user("buyer")).param("budget", "under200").param("sort", "price-asc"))
                .andExpect(status().isOk()).andReturn();
        @SuppressWarnings("unchecked")
        var filtered = (java.util.List<Product>) result.getModelAndView().getModel().get("products");
        assertThat(filtered).hasSize(4).allMatch(p -> p.getProductPrice() < 200);
        assertThat(filtered).extracting(Product::getProductPrice).isSorted();
        for (String image : new String[]{"notebook.png", "mouse.svg", "keyboard.svg", "headphones.svg", "monitor.svg", "backpack.svg", "ssd.svg", "bottle.svg"})
            mvc.perform(get("/images/demo/" + image)).andExpect(status().isOk());
        mvc.perform(get("/js/academic-account.js")).andExpect(status().isOk());
    }

    private Course course() {
        Course course = new Course(); course.setCourseName("Integration Course"); course.setPeriod("Evening");
        course.setWorkloadHours(2400); return courses.save(course);
    }
}
