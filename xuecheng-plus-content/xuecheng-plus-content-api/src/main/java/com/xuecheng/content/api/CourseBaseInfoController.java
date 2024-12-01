package com.xuecheng.content.api;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.*;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/11 15:44
 */
@Slf4j
@Api(value = "课程信息管理接口", tags = "课程信息管理接口")
@RestController
@RequiredArgsConstructor
public class CourseBaseInfoController {


    private final CourseBaseInfoService courseBaseInfoService;
    private final TeachPlanService teachPlanService;
    private final CourseTeacherService courseTeacherService;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto) {

        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);

        return courseBasePageResult;

    }

    @ApiOperation("新增课程")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody AddCourseDto addCourseDto) {
        //获取到用户所属机构的id
        Long companyId = 1232141425L;
//        int i = 1/0;
        CourseBaseInfoDto courseBase = courseBaseInfoService.createCourseBase(companyId, addCourseDto);
        return courseBase;
    }

    @ApiOperation("修改课程")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody EditCourseDto editCourseDto) {
        Long companyId = 1232141425L;

        return courseBaseInfoService.updateCouseBaseInfo(companyId, editCourseDto);
    }

    @GetMapping("/course/{id}")
    public CourseBaseInfoDto getCourseBaseInfoById(@PathVariable Long id) {

        return courseBaseInfoService.getCourseBaseInfo(id);

    }

    @ApiOperation("查询课程计划")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachPlanDto> getTeachPlanById(@PathVariable Long courseId) {
        if (courseId == null) {
            XueChengPlusException.cast("courseId is null");
        }


        return teachPlanService.getTeachPlanById(courseId);
    }

    @ApiOperation("新增/修改课程计划")
    @PostMapping("/teachplan")
    public void saveTeachPlan(@RequestBody SaveTeachPlanDto teachPlanDto) {
        teachPlanService.saveTeachPlan(teachPlanDto);

    }

    @ApiOperation("删除课程计划章节")
    @DeleteMapping("/teachplan/{teachPlanId}")
    public void deleteTeachPlan(@PathVariable Long teachPlanId) {

        teachPlanService.deleteTeachPlan(teachPlanId);

    }

    @PostMapping("/teachplan/moveup/{teachPlanId}")
    @ApiOperation("课程章节上移")
    public void teachPlanMoveUp(@PathVariable Long teachPlanId) {


        teachPlanService.moveUp(teachPlanId);
    }

    @PostMapping("/teachplan/movedown/{teachPlanId}")
    @ApiOperation("课程章节下移")
    public void teachPlanMoveDown(@PathVariable Long teachPlanId) {
        teachPlanService.moveDown(teachPlanId);
    }

    @GetMapping("/courseTeacher/list/{courseId}")
    @ApiOperation("查询课程老师信息")
    public List<CourseTeacher> getCourseTeacher(@PathVariable Long courseId) {
        return courseTeacherService.getCourseTeacherBaseInfo(courseId);
    }

    @PostMapping("/courseTeacher")
    @ApiOperation("添加课程老师")
    public CourseTeacher addCourseTeacher(@RequestBody CourseTeacher courseTeacher) {
        Long CompneyId = 1232141425L;

        return courseTeacherService.addCourseTeacher(courseTeacher, CompneyId);


    }

    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    @ApiOperation("删除课程老师")
    public void deleteCourseTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
        Long CompneyId = 1232141425L;
        courseTeacherService.deleteCourseTeacher(CompneyId, courseId, teacherId);


    }

    @DeleteMapping("/course/{courseId}")
    @ApiOperation("删除课程")
    public void deleteCourse(@PathVariable Long courseId) {
        Long CompneyId = 1232141425L;
        courseBaseInfoService.deleteCourse(courseId, CompneyId);


    }


}
