package com.example.demo.service.impl;

import com.example.demo.async.AsyncDemoService;
import com.example.demo.dto.StudentDto;
import com.example.demo.entity.Student;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.StudentMapper;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private StudentRepository studentRepository;
    private AsyncDemoService asyncDemoService;

    @Override
    @Caching(evict = @CacheEvict(value = "students", key = "'all'"))
    public StudentDto createStudent(StudentDto studentDto) {
        Student student = StudentMapper.mapToStudent(studentDto);
        Student saved = studentRepository.save(student);
        asyncDemoService.simulateTask("createStudent");
        return StudentMapper.mapToStudentDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "students", key = "#id")
    public StudentDto getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return StudentMapper.mapToStudentDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "students", key = "'all'")
    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentMapper::mapToStudentDto)
                .toList();
    }

    @Override
    @Caching(
        put = @CachePut(value = "students", key = "#id"),
        evict = @CacheEvict(value = "students", key = "'all'")
    )
    public StudentDto updateStudent(Long id, StudentDto studentDto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setEmail(studentDto.getEmail());
        Student updated = studentRepository.save(student);
        return StudentMapper.mapToStudentDto(updated);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "students", key = "#id"),
        @CacheEvict(value = "students", key = "'all'")
    })
    public void deleteStudent(Long id) {
        studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        studentRepository.deleteById(id);
        asyncDemoService.simulateTask("deleteStudent");
    }
}
