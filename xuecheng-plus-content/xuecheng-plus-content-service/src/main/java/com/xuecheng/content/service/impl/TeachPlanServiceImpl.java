package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.injector.methods.Delete;
import com.baomidou.mybatisplus.core.injector.methods.DeleteById;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Wrapper;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeachPlanServiceImpl  implements TeachPlanService {
    private  final TeachplanMapper teachplanMapper;
    private  final TeachplanMediaMapper teachplanMediaMapper;
    @Override
    public List<TeachPlanDto> getTeachPlanById(long courseId) {
       List<TeachPlanDto>teachPlanDtos=teachplanMapper.selectTreeNodes(courseId);
       if (teachPlanDtos==null||teachPlanDtos.size()==0){
           return Collections.emptyList();
       }
        return teachPlanDtos;
    }


    @Override
    public void saveTeachPlan(SaveTeachPlanDto teachPlanDto) {
        Long userId = teachPlanDto.getId();
        if (userId!=null)
        {
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(teachPlanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }else
        {
            LambdaQueryWrapper<Teachplan> teachplanLambdaQueryWrapper = new LambdaQueryWrapper<>();
            teachplanLambdaQueryWrapper.eq(Teachplan::getCourseId,teachPlanDto.getCourseId())
                    .eq(Teachplan::getParentid,teachPlanDto.getParentid());
            Integer i = teachplanMapper.selectCount(teachplanLambdaQueryWrapper);

            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(teachPlanDto,teachplan);
            teachplan.setOrderby(i+1);
            teachplanMapper.insert(teachplan);
        }


    }

    @Override
    public void deleteTeachPlan(Long teachPlanId) {

    LambdaQueryWrapper<Teachplan> teachPlanwrapper = new LambdaQueryWrapper<>();
    teachPlanwrapper.eq(Teachplan::getParentid,teachPlanId);
    Integer count = teachplanMapper.selectCount(teachPlanwrapper);
    if (count>0)
{
    XueChengPlusException.cast("子章节未删除");

}
else {
    LambdaUpdateWrapper<TeachplanMedia> teachplanMediaLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    teachplanMediaLambdaUpdateWrapper.eq(TeachplanMedia::getTeachplanId,teachPlanId);
    LambdaUpdateWrapper<Teachplan> teachplanLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    teachplanLambdaUpdateWrapper.eq(Teachplan::getId,teachPlanId);
 teachplanMapper.delete(teachplanLambdaUpdateWrapper);
teachplanMediaMapper.delete(teachplanMediaLambdaUpdateWrapper);
}



    }

    @Override
    public void moveUp(Long teachPlanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getGrade,teachplan.getGrade())
                        .eq(Teachplan::getCourseId,teachplan.getCourseId());
        List<Teachplan> teachplans = teachplanMapper.selectList(wrapper);
    Optional<Teachplan> maxTeachPlan = teachplans.stream().filter(teachplan1 -> teachplan1.getOrderby() < teachplan.getOrderby())
            .max(new Comparator<Teachplan>() {
                @Override
                public int compare(Teachplan o1, Teachplan o2) {
                    return o1.getOrderby() - o2.getOrderby();
                }
            });
    boolean present = maxTeachPlan.isPresent();
    if (!present)
    {
        XueChengPlusException.cast("当前已经第一位，不能再上移了");

    }
    Teachplan teachplanToBeMoveDown =maxTeachPlan.get();
        Integer orderby = teachplanToBeMoveDown.getOrderby();
        int tempOrderby = teachplan.getOrderby();
        teachplan.setOrderby(orderby);
        teachplanToBeMoveDown.setOrderby(tempOrderby);
        teachplanMapper.updateById(teachplanToBeMoveDown);
        teachplanMapper.updateById(teachplan);


    }

    @Override
    public void moveDown(Long teachPlanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getGrade,teachplan.getGrade())
                .eq(Teachplan::getCourseId,teachplan.getCourseId());
        List<Teachplan> teachplans = teachplanMapper.selectList(wrapper);
        Optional<Teachplan> min = teachplans.stream().filter(teachplan1 -> teachplan1.getOrderby() > teachplan.getOrderby())
                .min(new Comparator<Teachplan>() {

                    @Override
                    public int compare(Teachplan o1, Teachplan o2) {
                        return o1.getOrderby() - o2.getOrderby();
                    }
                });
        boolean present = min.isPresent();
        if (!present)
        {
            XueChengPlusException.cast("已经最后一位，不能再下移了");

        }
        Teachplan teachplanToBeMoveUp = min.get();
        Integer orderby = teachplanToBeMoveUp.getOrderby();
        teachplanToBeMoveUp.setOrderby(teachplan.getOrderby());
        teachplan.setOrderby(orderby);
        teachplanMapper.updateById(teachplanToBeMoveUp);
        teachplanMapper.updateById(teachplan);
    }
}
