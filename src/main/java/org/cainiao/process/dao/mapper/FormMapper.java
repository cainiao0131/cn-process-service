package org.cainiao.process.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.cainiao.process.dto.response.FormResponse;
import org.cainiao.process.entity.Form;

import java.util.List;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
public interface FormMapper extends BaseMapper<Form> {

    List<FormResponse> formInfos(int offset, int size, QueryWrapper<Form> ew);
}
