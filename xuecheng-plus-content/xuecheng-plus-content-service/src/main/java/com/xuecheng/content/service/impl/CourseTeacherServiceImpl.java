package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseTeacherServiceImpl implements CourseTeacherService {
    private final CourseTeacherMapper courseTeacherMapper;
    private final TeachplanMapper teachplanMapper;
    private final CourseBaseMapper courseBaseMapper;

    @Override
    public List<CourseTeacher> getCourseTeacherBaseInfo(long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(courseTeachers)) {
          return   Collections.emptyList();

        }


        return courseTeachers;
    }

    @Override
    public CourseTeacher addCourseTeacher(CourseTeacher courseTeacher, long CompneyId) {
        checkCompenyId(courseTeacher, CompneyId);
        Long teacherId = courseTeacher.getId();
        CourseTeacher teacher1 = new CourseTeacher();
        BeanUtils.copyProperties(courseTeacher, teacher1);
        CourseTeacher teacher = courseTeacherMapper.selectById(teacherId);
        if (teacher == null) {
            teacher1.setCreateDate(LocalDateTime.now());
            int insert = courseTeacherMapper.insert(teacher1);
            if (insert != 1) {
                XueChengPlusException.cast("添加老师失败");
            }
        } else {

            BeanUtils.copyProperties(courseTeacher, teacher);
            int i = courseTeacherMapper.updateById(teacher);
            if (i != 1) {

                XueChengPlusException.cast("更新老师失败");
            }

        }


        return teacher == null ? teacher1 : teacher;
    }


    @Override
    public void deleteCourseTeacher(Long compneyId, Long courseId, Long teacherId) {
        if (courseId == null) {

            XueChengPlusException.cast("课程id不能为空");
        }
        if (teacherId == null) {

            XueChengPlusException.cast("老师id不能为空");
        }

        CourseTeacher courseTeacher = courseTeacherMapper.selectById(teacherId);
        checkCompenyId(courseTeacher, compneyId);
        int i = courseTeacherMapper.deleteById(courseTeacher);
        if (i != 1) {

            XueChengPlusException.cast("删除老师失败");
        }

    }

    private void checkCompenyId(CourseTeacher courseTeacher, long CompneyId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseTeacher.getCourseId());
        if (!courseBase.getCompanyId().equals(CompneyId)) {
            XueChengPlusException.cast("非本机构课程，不能添加老师");

        }
    }


}
