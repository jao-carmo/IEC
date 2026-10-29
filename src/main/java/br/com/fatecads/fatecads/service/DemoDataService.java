package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.*;
import br.com.fatecads.fatecads.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Versioned, additive fixtures: run once per database, without resetting user content. */
@Service
public class DemoDataService {
    private static final String VERSION = "academic-store-v1";
    private final CourseRepository courses;
    private final ProfessorRepository professors;
    private final SubjectRepository subjects;
    private final StudentRepository students;
    private final ProductRepository products;
    private final DemoDataVersionRepository versions;

    public DemoDataService(CourseRepository courses, ProfessorRepository professors, SubjectRepository subjects,
            StudentRepository students, ProductRepository products, DemoDataVersionRepository versions) {
        this.courses = courses; this.professors = professors; this.subjects = subjects;
        this.students = students; this.products = products; this.versions = versions;
    }

    @Transactional
    public boolean seed() {
        if (versions.existsById(VERSION)) return false;
        Course software = course("Software Development", "Evening", 2400);
        Course business = course("Business Management", "Morning", 2000);
        Course data = course("Data Science", "Evening", 2400);
        Professor emma = professor("DEMO-P001", "Emma Wilson", "Software Engineering", "11900000001");
        Professor oliver = professor("DEMO-P002", "Oliver Bennett", "Business Administration", "11900000002");
        Professor mia = professor("DEMO-P003", "Mia Thompson", "Applied Mathematics", "11900000003");
        subject("DEV101", "Programming Fundamentals", 80, software, emma);
        subject("DEV202", "DevOps and Cloud Computing", 80, software, emma);
        subject("BUS101", "Organizational Management", 80, business, oliver);
        subject("BUS202", "Entrepreneurship", 40, business, oliver);
        subject("DAT101", "Statistics and Probability", 80, data, mia);
        subject("DAT202", "Database Systems", 80, data, emma);
        student("DEMO-S001", "Alex Morgan", "alex.morgan@example.test", software, AcademicStatus.ENROLLED, 1);
        student("DEMO-S002", "Sophie Carter", "sophie.carter@example.test", software, AcademicStatus.COMPLETED, 2);
        student("DEMO-S003", "Lucas Parker", "lucas.parker@example.test", business, AcademicStatus.ENROLLED, 3);
        student("DEMO-S004", "Grace Evans", "grace.evans@example.test", business, AcademicStatus.COMPLETED, 4);
        student("DEMO-S005", "Noah Collins", "noah.collins@example.test", data, AcademicStatus.ENROLLED, 5);
        student("DEMO-S006", "Chloe Mitchell", "chloe.mitchell@example.test", data, AcademicStatus.ENROLLED, 6);
        product("Notebook", "FatecJales", 3500, "notebook.png");
        product("Wireless Mouse", "Campus Essentials", 79.90, "mouse.svg");
        product("Mechanical Keyboard", "Campus Essentials", 249.90, "keyboard.svg");
        product("Wireless Headphones", "Study Studio", 199.90, "headphones.svg");
        product("24-inch Monitor", "Study Studio", 899.90, "monitor.svg");
        product("Everyday Backpack", "Campus Essentials", 159.90, "backpack.svg");
        product("Portable SSD 1TB", "Study Studio", 429.90, "ssd.svg");
        product("Insulated Water Bottle", "Campus Essentials", 59.90, "bottle.svg");
        versions.save(new DemoDataVersion(VERSION));
        return true;
    }

    private Course course(String name, String period, int hours) {
        return courses.findFirstByCourseNameIgnoreCase(name).orElseGet(() -> {
            Course course = new Course(); course.setCourseName(name); course.setPeriod(period); course.setWorkloadHours(hours);
            return courses.save(course);
        });
    }
    private Professor professor(String rm, String name, String graduation, String phone) {
        return professors.findFirstByRM(rm).orElseGet(() -> {
            Professor professor = new Professor(); professor.setRM(rm); professor.setFullName(name);
            professor.setGraduation(graduation); professor.setPhone(phone);
            return professors.save(professor);
        });
    }
    private void subject(String code, String name, int hours, Course course, Professor professor) {
        if (subjects.findFirstByCode(code).isPresent()) return;
        Subject subject = new Subject(); subject.setCode(code); subject.setSubjectName(name);
        subject.setWorkloadHours(hours); subject.setCourse(course); subject.setProfessor(professor); subjects.save(subject);
    }
    private void student(String ra, String name, String email, Course course, AcademicStatus status, int number) {
        if (students.findFirstByRegistrationNumber(ra).isPresent()) return;
        Student student = new Student(); student.setRegistrationNumber(ra); student.setFullName(name); student.setEmail(email);
        student.setPhone("1190000000" + number); student.setCpf("0000000000" + number);
        student.setAddress(number + " University Avenue"); student.setCourse(course); student.setAcademicStatus(status);
        students.save(student);
    }
    private void product(String name, String brand, double price, String image) {
        if (products.findFirstByProductDescriptionIgnoreCase(name).isPresent()) return;
        Product product = new Product(); product.setProductDescription(name); product.setProductBrand(brand);
        product.setProductUnit("unit"); product.setProductPrice(price); product.setImagePath("/images/demo/" + image);
        products.save(product);
    }
}
