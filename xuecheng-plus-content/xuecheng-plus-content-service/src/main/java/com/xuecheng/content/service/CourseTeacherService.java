package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {
    List<CourseTeacher> getCourseTeacherBaseInfo(long courseId);

    CourseTeacher addCourseTeacher(CourseTeacher courseTeacher,long CompneyId);


    void deleteCourseTeacher(Long compneyId, Long courseId, Long teacherId);
}
