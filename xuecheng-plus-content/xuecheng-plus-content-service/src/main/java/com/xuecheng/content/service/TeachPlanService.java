package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.xuecheng.content.model.dto.TeachPlanDto;

import java.util.List;

public interface TeachPlanService {


 List<TeachPlanDto> getTeachPlanById(long courseId);

    void saveTeachPlan(SaveTeachPlanDto teachPlanDto);

    void deleteTeachPlan(Long teachPlanId);

    void moveUp(Long teachPlanId);

    void moveDown(Long teachPlanId);
}

