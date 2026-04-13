package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.Course;
import br.com.fatecads.fatecads.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public void save(Course course) {
        if (course == null) {
            return;
        }
        courseRepository.save(course);
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public void deleteById(Integer id) {
        courseRepository.deleteById(id);
    }

    public Course findById(Integer id) {
        return courseRepository.findById(id).orElse(null);
    }
}
