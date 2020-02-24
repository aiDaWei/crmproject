/* * 文 件 名： * 版权(c) 2018-xxx公司 xxx项目组： * 版 本 号： */package com.csdj.crmproject.crmproject.service.impl.Sales.PaymentPlan;import com.csdj.crmproject.crmproject.dao.Sales.PaymentPlan.PaymentPlanMapper;import com.csdj.crmproject.crmproject.entity.ReCeiPlan;import com.csdj.crmproject.crmproject.entity.customermanagement.ClientTable;import com.csdj.crmproject.crmproject.entity.salesmanagement.Order;import com.csdj.crmproject.crmproject.service.Sales.PaymentPlan.PaymentPlanServer;import com.github.pagehelper.PageHelper;import com.github.pagehelper.PageInfo;import org.springframework.data.redis.core.RedisTemplate;import org.springframework.data.redis.core.ValueOperations;import org.springframework.stereotype.Service;import javax.annotation.Resource;import java.util.List;import java.util.concurrent.TimeUnit;/** * @author 贺芳 * @description: 收款计划 */@Servicepublic class PaymentPlanServerImpl implements PaymentPlanServer {    @Resource    private PaymentPlanMapper paymentPlanMapper;    @Resource    private RedisTemplate redisTemplate;    /**     * 收款总记录数     * @param rePlanCurrentState 收款状态     * @return     */    @Override    public int planCount(String rePlanCurrentState) {        return paymentPlanMapper.planCount(rePlanCurrentState);    }    /**     * 查询及分页     * @param pageNum     * @param pageSize     * @return     */    @Override    public PageInfo<ReCeiPlan> findPlan(int pageNum, int pageSize,String rePlanCurrentState) {        PageHelper.startPage(pageNum, pageSize);        List<ReCeiPlan> list=paymentPlanMapper.findPlan(rePlanCurrentState);        PageInfo<ReCeiPlan> pageInfo=new PageInfo<>(list);        return pageInfo;    }    /**     * 添加     * @param reCeiPlan     * @return     */    @Override    public int addPlan(ReCeiPlan reCeiPlan) {        return paymentPlanMapper.addPlan(reCeiPlan);    }    /**     * 根据ID查询收款计划     * @param pkRePlanId 收款编号     * @return     */    @Override    public ReCeiPlan findGetPlanId(long pkRePlanId) {        ReCeiPlan reCeiPlan=null;        String key="pkRePlanId_"+pkRePlanId;        try {            ValueOperations<String,ReCeiPlan> operations=redisTemplate.opsForValue();            boolean hasKey=redisTemplate.hasKey(key);            if (hasKey){                reCeiPlan=operations.get(key);                System.out.println("==========从缓存中获得数据=========");                return reCeiPlan;            }else {                reCeiPlan=paymentPlanMapper.findGetPlanId(pkRePlanId);                System.out.println("==========从数据表中获得数据=========");                operations.set(key,reCeiPlan,5, TimeUnit.MINUTES);                return reCeiPlan;            }        } catch (Exception e) {            System.out.println("redis服务异常");            reCeiPlan=paymentPlanMapper.findGetPlanId(pkRePlanId);        }        return reCeiPlan;    }    /**     * 修改     * @param reCeiPlan     * @return     */    @Override    public int updatePlan(ReCeiPlan reCeiPlan) {        ValueOperations<String,ReCeiPlan> valueOperations=redisTemplate.opsForValue();        int i=paymentPlanMapper.updatePlan(reCeiPlan);        if(i!=0){            String key="reCeiPlan_"+reCeiPlan.getPkRePlanId();            boolean keyHas=redisTemplate.hasKey(key);            if (keyHas){                redisTemplate.delete(key);                ReCeiPlan reCeiPlans=paymentPlanMapper.findGetPlanId(reCeiPlan.getPkRePlanId());                if (reCeiPlans!=null){                    valueOperations.set(key,reCeiPlans,5, TimeUnit.MINUTES);                }            }        }        return i;    }    /**     * 删除     * @param array     * @return     */    @Override    public int deletePlan(int[] array) {        int result=paymentPlanMapper.deletePlan(array);        if (result!=0){            for (int i=0;i<array.length;i++){                String key="reCeiPlan_"+array[i];                boolean hasKey=redisTemplate.hasKey(key);                if (hasKey){                    redisTemplate.delete(key);                }            }        }        return result;    }    /**     * 查询客户类型     * @param fkTypeNumberId 客户类型编号     * @param pageNo     * @return     */    @Override    public PageInfo<ClientTable> findClientTableById(String fkTypeNumberId, int pageNo) {        PageHelper.startPage(pageNo,3);        List<ClientTable> list=paymentPlanMapper.findClientTableById(fkTypeNumberId);        PageInfo<ClientTable> pageInfo=new PageInfo<>(list);        return pageInfo;    }}