package com.upic.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.upic.common.base.enums.JugeType;
import com.upic.common.base.enums.JygeTypeEnum;
import com.upic.common.document.excel.ExcelDocument;
import com.upic.common.fdfs.FastDFSClient;
import com.upic.condition.*;
import com.upic.dto.*;
import com.upic.dto.excel.IntegralLogInfoExcel;
import com.upic.enums.*;
import com.upic.service.*;
import com.upic.social.user.SocialUsers;
import com.upic.utils.Constans;
import com.upic.utils.UserUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by zhubuqing on 2017/9/18.
 */
@RestController
@RequestMapping("/common")
public class CommonController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(CommonController.class);

//    @Autowired
//    private MailService mailService;

    @Autowired
    private ProjectCategoryService projectCategoryService;

    @Autowired
    private IntegralLogService integralLogService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PrizeService prizeService;

    @Autowired
    private GrainCoinLogService grainCoinLogService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryNodeService categoryNodeService;

    @Autowired
    private CollegeService collegeService;

    @Autowired
    private ClazzService clazzService;

    @Autowired
    private ConfirmationBasisService confirmationBasisService;

    @Autowired
    private AdviceService adviceService;

    @Autowired
    private CheckStatusService checkStatusService;

    /**
     * 根据ID获取项目类别
     *
     * @param id
     * @return
     */
    @GetMapping("/getProjectCategoryById")
    @ApiOperation("根据ID获取项目类别")
    public ProjectCategoryInfo getProjectCategoryById(long id) {
        try {
            ProjectCategoryInfo projectCategoryInfo = projectCategoryService.getProjectCategoryById(id);
            System.out.println(projectCategoryInfo);
            return projectCategoryInfo;
        } catch (Exception e) {
            LOGGER.info("getProjectCategoryById:" + e.getMessage());
            return null;
        }
    }

    /**
     * 根据ID获取项目节点
     *
     * @param id
     * @return
     */
    @GetMapping("/getCategoryNodeById")
    @ApiOperation("根据ID获取项目类别")
    public CategoryNodeInfo getCategoryNodeById(long id) {
        try {
            CategoryNodeInfo categoryNodeInfo = categoryNodeService.getCategoryNodeById(id);
            System.out.println(categoryNodeInfo);
            return categoryNodeInfo;
        } catch (Exception e) {
            LOGGER.info("getCategoryNodeById:" + e.getMessage());
            return null;
        }
    }

    /**
     * 添加项目类别
     *
     * @param projectCategoryInfo
     * @return
     */
    @GetMapping("/addProjectCategory")
    @ApiOperation("添加项目类别")
    public ProjectCategoryInfo addProjectCategory(ProjectCategoryInfo projectCategoryInfo) {
        try {
            return projectCategoryService.addProjectCategory(projectCategoryInfo);
        } catch (Exception e) {
            LOGGER.info("addProjectCategory:" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取所有项目类别
     *
     * @param pageable
     * @param p
     * @return
     * @throws Exception
     */
    @GetMapping("/getAllProjectCategory")
    @ApiOperation("获取所有项目类别")
    public Page<ProjectCategoryInfo> getAllProjectCategory(@PageableDefault(size = 20) Pageable pageable, ProjectCategoryCondition p) {
        try {
            Page<ProjectCategoryInfo> projectCategoryInfoPage = projectCategoryService.searchProjectCategory(p, pageable);
            return projectCategoryInfoPage;
        } catch (Exception e) {
            LOGGER.info("getAllProjectCategory:" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取项目状态
     *
     * @return
     */
    @GetMapping("/getAllProjectImplementationProcess")
    @ApiOperation("获取项目状态")
    public List<String> getAllProjectImplementationProcess() {
        try {
            List<String> implementationProcessList = new ArrayList<String>();
            List<CheckStatusInfo> checkStatusInfoList = checkStatusService.getByType("2");
            for (CheckStatusInfo checkStatusInfo : checkStatusInfoList) {
                implementationProcessList.add(checkStatusInfo.getName());
            }
            return implementationProcessList;
        } catch (Exception e) {
            LOGGER.info("getAllProjectImplementationProcess:" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/getAllIntegralLogStatus")
    @ApiOperation("获取积分状态")
    public List<String> getAllIntegralLogStatus() {
        try {
            List<String> statusList = new ArrayList<String>();
            List<CheckStatusInfo> checkStatusInfoList = checkStatusService.getByType("1");
            for (CheckStatusInfo checkStatusInfo : checkStatusInfoList) {
                statusList.add(checkStatusInfo.getName());
            }
            return statusList;
        } catch (Exception e) {
            LOGGER.info("getAllIntegralLogStatus:" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取所有项目类别
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/getAllProjectCategoryList")
    @ApiOperation("获取所有项目类别")
    public List<ProjectCategoryInfo> getAllProjectCategoryList(ProjectCategoryCondition projectCategoryCondition) throws Exception {
        try {
            List<ProjectCategoryInfo> projectCategoryInfoList = projectCategoryService.getAllProjectCategoryList(projectCategoryCondition);
            for (ProjectCategoryInfo projectCategoryInfo : projectCategoryInfoList) {
                System.out.println(projectCategoryInfo.toString());
            }
            return projectCategoryInfoList;
        } catch (Exception e) {
            LOGGER.info("getAllProjectCategory:" + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 更新项目类别
     *
     * @return
     */
    @GetMapping("/updateProjectCategory")
    @ApiOperation("更新项目类别")
    public ProjectCategoryInfo updateProjectCategory(long id, String categoryName) {
        try {
            ProjectCategoryInfo projectCategoryInfo = projectCategoryService.getProjectCategoryById(id);
            projectCategoryInfo.setCategoryName(categoryName);
            projectCategoryInfo = projectCategoryService.updateProjectCategory(projectCategoryInfo);
            return projectCategoryInfo;
        } catch (Exception e) {
            LOGGER.info("updateProjectCategory:" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/addCategoryNode")
    @ApiOperation("添加项目节点")
    public CategoryNodeInfo searchCategoryNode(CategoryNodeInfo categoryNodeInfo) {
        try {
            return categoryNodeService.addCategoryNode(categoryNodeInfo);
        } catch (Exception e) {
            LOGGER.info("searchCategoryNode:" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/deleteCategoryNode")
    @ApiOperation("删除项目节点")
    public String deleteCategoryNode(long id) {
        try {
            String message = categoryNodeService.deleteCategoryNode(id);
            return message;
        } catch (Exception e) {
            LOGGER.info("deleteCategoryNode:" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取项目节点
     *
     * @param pageable
     * @param categoryNodeCondition
     * @return
     */
    @GetMapping("/searchCategoryNode")
    @ApiOperation("获取项目节点")
    public Page<CategoryNodeInfo> searchCategoryNode(@PageableDefault(size = 20) Pageable pageable, CategoryNodeCondition categoryNodeCondition) {
        try {
            return categoryNodeService.searchCategoryNode(categoryNodeCondition, pageable);
        } catch (Exception e) {
            LOGGER.info("searchCategoryNode:" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取项目节点
     *
     * @param categoryNodeCondition
     * @return
     */
    @GetMapping("/searchCategoryNodeList")
    @ApiOperation("获取项目节点")
    public List<CategoryNodeInfo> searchCategoryNodeList(CategoryNodeCondition categoryNodeCondition) {
        try {
            return categoryNodeService.searchCategoryNodeList(categoryNodeCondition);
        } catch (Exception e) {
            LOGGER.info("searchCategoryNodeList:" + e.getMessage());
            return null;
        }
    }

    /**
     * 更新项目节点
     *
     * @return
     */
    @GetMapping("/updateCategoryNode")
    @ApiOperation("更新项目节点")
    public CategoryNodeInfo updateCategoryNode(long id, String nodeContent) {
        try {
            CategoryNodeInfo categoryNodeInfo = categoryNodeService.getCategoryNodeById(id);
            categoryNodeInfo.setNodeContent(nodeContent);
            categoryNodeInfo = categoryNodeService.updateCategoryNode(categoryNodeInfo);
            return categoryNodeInfo;
        } catch (Exception e) {
            LOGGER.info("updateCategoryNode:" + e.getMessage());
            return null;
        }
    }

//    @GetMapping("/deleteProjectCategory") // 待定
//    @ApiOperation("删除项目类别")
//    public String deleteProjectCategory(long projectCategoryId) {
//        try {
//            projectCategoryService.deleteProjectCategory(projectCategoryId);
//            List<CategoryNodeInfo> categoryNodeInfoList = categoryNodeService.getCategoryNodeByFatherId(projectCategoryId);
//            if (categoryNodeInfoList != null) {
//
//            }
//            return "SUCCESS";
//        } catch (Exception e) {
//            LOGGER.info("updateCategoryNode:" + e.getMessage());
//            return "ERROR";
//        }
//    }

    /**
     * 根据FatherId获取项目节点
     *
     * @param fatherId
     * @return
     */
    @GetMapping("/getCategoryNodeByFatherId")
    @ApiOperation("根据FatherId获取项目节点")
    public List<CategoryNodeInfo> getCategoryNodeByFatherId(long fatherId) {
        try {
            List<CategoryNodeInfo> categoryNodeInfoList = categoryNodeService.getCategoryNodeByFatherId(fatherId);
            return categoryNodeInfoList;
        } catch (Exception e) {
            LOGGER.info("getCategoryNodeByFatherId:" + e.getMessage());
            return null;
        }
    }

    /**
     * 根据项目节点ID获取固定项目
     *
     * @param categoryNodeId
     * @return
     */
    @GetMapping("/getConfirmationBasisByCategoryNodeId")
    @ApiOperation("根据项目节点ID获取固定项目")
    public List<ConfirmationBasisInfo> getConfirmationBasisByCategoryNodeId(long categoryNodeId) {
        try {
            List<ConfirmationBasisInfo> confirmationBasisInfoList = confirmationBasisService.getByCategoryNodeId(categoryNodeId);
            return confirmationBasisInfoList;
        } catch (Exception e) {
            LOGGER.info("getConfirmationBasisByCategoryNodeId:" + e.getMessage());
            return null;
        }
    }

    /**
     * 根据条件查询活动*
     *
     * @throws Exception
     */
    @GetMapping("/getProject")
    @ApiOperation("根据条件查询活动")
    public Page<ProjectInfo> getProject(@PageableDefault(size = 10) Pageable pageable, ProjectCondition p)
            throws Exception {
        try {
            if (p.getField5() != null) {
                List<Map<String, Object>> orList = new ArrayList<Map<String, Object>>();
                Map<String, Object> map = new IdentityHashMap<>();
                if (p.getField5().equals("IN_AUDIT/IN_AUDIT_AGAIN/IN_AUDIT_FINAL")) {
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.IN_AUDIT));
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.IN_AUDIT_AGAIN));
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.IN_AUDIT_FINAL));
                    p.setField5(null);
                } else if (p.getField5().equals("AUDITED/ENROLLMENT/HAVE_IN_HAND")) {
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.AUDITED));
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.ENROLLMENT));
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.HAVE_IN_HAND));
                    p.setField5(null);
                } else if (p.getField5().equals("CHECKING/CHECKING_AGAIN/CHECKING_FINAL")) {
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.CHECKING));
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.CHECKING_AGAIN));
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.CHECKING_FINAL));
                    p.setField5(null);
                } else if (p.getField5().equals("COMPLETED/CHECKED")) {
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.COMPLETED));
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.CHECKED));
                    p.setField5(null);
                } else if (p.getField5().equals("IN_AUDIT_FAIL/IN_AUDIT_AGAIN_FAIL/IN_AUDIT_FINAL_FAIL/CHECKING_FAIL/CHECKING_AGAIN_FAIL/CHECKING_FINAL_FAIL")) {
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.IN_AUDIT_FAIL));
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.IN_AUDIT_AGAIN_FAIL));
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.IN_AUDIT_FINAL_FAIL));
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.CHECKING_FAIL));
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.CHECKING_AGAIN_FAIL));
                    map.put(new String("implementationProcess"), new JugeType(JygeTypeEnum.EQUAL, ImplementationProcessEnum.CHECKING_FINAL_FAIL));
                    p.setField5(null);
                }
                orList.add(map);
                p.setOrList(orList);
            }
            Page<ProjectInfo> projectInfoPage = projectService.searchProject(p, pageable);
            return projectInfoPage;
        } catch (Exception e) {
            LOGGER.info("getProject:" + e.getMessage());
            return null;
        }
    }

    /**
     * 项目导出
     *
     * @return
     */
    @ApiOperation("项目导出")
    @GetMapping("exportProject")
    public void exportProject(HttpServletResponse response, ProjectCondition condition, String baseModel) {
        try {
            List<String> parseArray = JSONArray.parseArray(baseModel, String.class);
            String[] desc = new String[]{};
            String[] array = parseArray.toArray(desc);
            List<Object> byProjectNum = projectService.listProject(condition);
            Workbook wk = ExcelDocument.download(array, ProjectInfo.class, byProjectNum);
            downLoadExcel(response, wk, "项目维护.xls");
        } catch (Exception e) {
            LOGGER.info("exportProject:" + e.getMessage());
            try {
                response.getWriter().println("下载失败！");
            } catch (IOException e1) {
            }
        }
    }

    /**
     * 获取奖品*
     *
     * @param pageable
     * @return
     * @throws Exception
     */
    @GetMapping("/getPrize")
    @ApiOperation("根据条件查询奖品")
    public Page<PrizeInfo> getPrize(@PageableDefault(size = 1000) Pageable pageable, PrizeCondition p) throws Exception {
        try {
            Page<PrizeInfo> prizeInfoPage = prizeService.searchPrizes(p, pageable);
            return prizeInfoPage;
        } catch (Exception e) {
            LOGGER.info("getPrize:" + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 获取相应的站内信*
     *
     * @param pageable
     * @param m
     * @return
     * @throws Exception
     */
//    @GetMapping("/getMail")
//    @ApiOperation("根据条件获取站内信")
//    public Page<MailInfo> getMail(@PageableDefault(size = 10) Pageable pageable, MailCondition m) throws Exception {
//        try {
//            return mailService.searchMail(m, pageable);
//        } catch (Exception e) {
//            LOGGER.info("getMail:" + e.getMessage());
//            throw new Exception(e.getMessage());
//        }
//    }

    /**
     * 根据id获取相应的站内信*
     *
     * @param id
     * @return
     * @throws Exception
     */
//    @GetMapping("/getMailInfo")
//    @ApiOperation("根据ID获取相应的站内信")
//    public MailInfo getMailInfo(@ApiParam("站内信ID") Long id) throws Exception {
//        try {
//            return mailService.getMailById(id);
//        } catch (Exception e) {
//            LOGGER.info("getMailInfo:" + e.getMessage());
//            throw new Exception(e.getMessage());
//        }
//    }

    /**
     * 根据项目编号查询项目人数*
     *
     * @param projectNum
     * @return
     * @throws Exception
     */
    @GetMapping("/getSignUpNumberByProjectNum")
    @ApiOperation("根据项目编号查询项目人数")
    public int getSignUpNumberByProjectNum(@ApiParam("项目编号") String projectNum) {
        try {
            int a = integralLogService.getSignUpNumberByProjectNum(projectNum);
            return a;
        } catch (Exception e) {
            LOGGER.info("getSignUpNumberByProjectNum:" + e.getMessage());
            return 0;
        }
    }

    /**
     * 根据项目编号查询项目人员*
     *
     * @param projectNum
     * @return
     * @throws Exception
     */
    @GetMapping("/getSignUpPeopleByProjectNum")
    @ApiOperation("根据项目编号查询项目人员")
    public List<UserInfo> getSignUpPeopleByProjectNum(@ApiParam("项目编号") String projectNum) {
        try {
            List<IntegralLogInfo> integralLogInfoList = integralLogService.getByProjectNum(projectNum);
            List<String> studentNumList = new ArrayList<>();
            List<String> statusList = new ArrayList<>();
            for (IntegralLogInfo integralLogInfo : integralLogInfoList) {
                studentNumList.add(integralLogInfo.getIntegralLogId().getStudentNum());
                statusList.add(integralLogInfo.getStatus().getContent());
            }
            List<UserInfo> userInfoList = new ArrayList<>();
            for (int i = 0; i < studentNumList.size(); i++) {
                UserInfo userInfo = new UserInfo();
                userInfo = userService.getUserByUserNum(studentNumList.get(i));
                userInfo.setField1(statusList.get(i));
                userInfoList.add(userInfo);
            }
            return userInfoList;
        } catch (Exception e) {
            LOGGER.info("getSignUpPeopleByProjectNum:" + e.getMessage());
            return null;
        }
    }

    /**
     * 根据活动编号查看活动详情*
     *
     * @param projectNum
     * @return
     * @throws Exception
     */
    @GetMapping("/getProjectInfo")
    @ApiOperation("根据项目编号查询活动详情")
    public ProjectInfo getProjectInfo(@ApiParam("项目编号") String projectNum) throws Exception {
        try {
            ProjectInfo projectInfo = projectService.getProjectByNum(projectNum);
            System.out.println(projectInfo);
            return projectInfo;
        } catch (Exception e) {
            LOGGER.info("getProjectInfo:" + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 获取积分明细*
     *
     * @param pageable
     * @param c
     * @return
     * @throws Exception
     */
    @GetMapping("/getIntegralLogPage")
    @ApiOperation("获取积分明细")
    public Page<IntegralLogInfo> getIntegralLogPage(@PageableDefault(size = 10) Pageable pageable,
                                                    IntegralLogCondition c) throws Exception {
        try {
            Page<IntegralLogInfo> integralLogInfoPage = integralLogService.searchIntegralLog(c, pageable);
            return integralLogInfoPage;
        } catch (Exception e) {
            LOGGER.info("getIntegralLogPage:" + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 根据用户编号获取积分明细*
     *
     * @param pageable
     * @return
     * @throws Exception
     */
    @GetMapping("/getIntegralLogPageByUserNum")
    @ApiOperation("根据用户编号获取积分明细")
    public Page<IntegralLogInfo> getIntegralLogPageByUserNum(@PageableDefault(size = 1000) Pageable pageable, String studentNum) throws Exception {
        try {
            Page<IntegralLogInfo> integralLogInfoPage = integralLogService.getIntegralLogByMySelf(studentNum, pageable);
            return integralLogInfoPage;
        } catch (Exception e) {
            LOGGER.info("getIntegralLogPage:" + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 获取素拓币明细*
     *
     * @param pageable
     * @param grainCoinLogCondition
     * @return
     * @throws Exception
     */
    @GetMapping("/getGraincoinLogPage")
    @ApiOperation("获取素拓币明细")
    public Page<GrainCoinLogInfo> getGraincoinLogPage(@PageableDefault(size = 10) Pageable pageable, GrainCoinLogCondition grainCoinLogCondition, String myType) throws Exception {
        try {
            if (myType != null && myType.equals("single")) {
//                grainCoinLogCondition.setUserNum(getUser().getUserNum());
                grainCoinLogCondition.setUserNum(getUser().getUserId());
            }
            return grainCoinLogService.searchPrizeByCondition(grainCoinLogCondition, pageable);
        } catch (Exception e) {
            LOGGER.info("getGraincoinLogPage:" + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 用户查看站内信（可能没用）
     *
     * @param pageable
     * @param userInfo
     * @return
     * @throws Exception
     */
//    @GetMapping("/getMyMail")
//    @ApiOperation("用户查看站内信（可能没用）")
//    public Page<MailInfo> getMyMail(@PageableDefault(size = 10) Pageable pageable, UserInfo userInfo) throws Exception {
//        try {
//            return mailService.getMyMail(userInfo.getCollege(), userInfo.getMajor(), userInfo.getClazz(), userInfo.getUserNum(), pageable);
//        } catch (Exception e) {
//            LOGGER.info("getMyMail:" + e.getMessage());
//            throw new Exception(e.getMessage());
//        }
//    }

    /****************************************************************************************************/

    /**
     * 根据用户获取项目列表
     *
     * @param pageable
     * @param projectCondition
     * @return
     * @throws Exception
     */
    @GetMapping("/getProjectByUser")
    @ApiOperation("根据用户获取项目列表")
    public Page<ProjectInfo> getProjectByUser(@PageableDefault(size = 10) Pageable pageable, ProjectCondition projectCondition) {
        try {
            return projectService.searchProject(projectCondition, pageable);
        } catch (Exception e) {
            LOGGER.info("getProjectByUser:" + e.getMessage());
            return null;
        }
    }

    /**
     * 项目申报
     *
     * @param projectInfo
     * @return
     * @throws Exception
     */
    @PostMapping("/addProject")
    @ApiOperation("项目申报")
    public ProjectInfo addProject(String projectInfo) throws Exception {
        try {
            ProjectInfo p = JSON.parseObject(projectInfo, ProjectInfo.class);
//            p.setProjectNum("SQ" + getUser().getUserNum() + new Date().getTime());
            p.setProjectNum("SQ" + getUser().getUserId() + new Date().getTime());
            p.setImplementationProcess(ImplementationProcessEnum.SAVED);
//            p.setGuidanceNum(getUser().getUserNum());
            p.setGuidanceNum(getUser().getUserId());
            p.setProjectAddWay(ProjectAddWayEnum.MANUAL_ADDITION);
//            p.setUnit("3");
            p.setUnit(getUser().getRank());
            p = projectService.addProject(p);
            return p;
        } catch (Exception e) {
            LOGGER.info("addProject:" + e.getMessage());
            throw new Exception("addProject" + e.getMessage());
        }
    }

    /**
     * 获取单个奖品
     *
     * @param prizeId
     * @return
     * @throws Exception
     */
    @GetMapping("/getPrizeById")
    @ApiOperation("获取单个奖品")
    public PrizeInfo getPrizeById(@ApiParam("奖品ID") long prizeId) throws Exception {
        try {
            return prizeService.getPrizeById(prizeId);
        } catch (Exception e) {
            LOGGER.info("getPrizeById:" + e.getMessage());
            throw new Exception("getPrizeById" + e.getMessage());
        }
    }

    /**
     * 获取历史奖品
     *
     * @param pageable
     * @return
     * @throws Exception
     */
    @GetMapping("/getHistoryPrize")
    @ApiOperation("获取历史奖品")
    public Page<PrizeInfo> getHistoryPrize(@PageableDefault(size = 10) Pageable pageable) throws Exception {
        try {
            Page<PrizeInfo> prizeInfoPage = prizeService.getHistoryPrize(pageable);
            return prizeInfoPage;
        } catch (Exception e) {
            LOGGER.info("getHistoryPrize:" + e.getMessage());
            throw new Exception("getHistoryPrize" + e.getMessage());
        }
    }

    /****************************************************************************************************/

    /**
     * 查询我未报名、并且在报名期间内的活动（学生移动端全部活动查询）
     *
     * @param pageable
     * @return
     * @throws Exception
     */
    @GetMapping("/getProjectWithoutSignUp")
    @ApiOperation("查询我未报名、并且在报名期间内的活动（学生移动端全部活动查询）")
    public Page<ProjectInfo> getProjectWithoutSignUp(@PageableDefault(size = 10) Pageable pageable) throws Exception {
        try {
            ProjectCondition p = new ProjectCondition();
            p.setImplementationProcess(ImplementationProcessEnum.ENROLLMENT);
            Page<ProjectInfo> projectWithoutSignUp = projectService.searchProject(p, pageable);
            return projectWithoutSignUp;
        } catch (Exception exception) {
            LOGGER.info("getProjectWithoutSignUp:" + exception.getMessage());
            throw new Exception("getProjectWithoutSignUp" + exception.getMessage());
        }
    }

    /**
     * 根据项目编号查找已报名的活动*
     *
     * @param projectNum
     * @return
     * @throws Exception
     */
    @GetMapping("/getIntegralLogInfoByMySelf")
    @ApiOperation("根据项目编号查找已报名的活动")
    public IntegralLogInfo getIntegralLogInfoByMySelf(@ApiParam("项目编号") String projectNum) throws Exception {
        try {
            IntegralLogIdInfo integralLogIdInfo = new IntegralLogIdInfo();
            integralLogIdInfo.setProjectNum(projectNum);
//            integralLogIdInfo.setStudentNum(getUser().getUserNum());
            integralLogIdInfo.setStudentNum(getUser().getUserId());
            return integralLogService.getByIntegralLogId(integralLogIdInfo);
        } catch (Exception e) {
            LOGGER.info("getProjectWithoutSignUp:" + e.getMessage());
            throw new Exception("getProjectWithoutSignUp" + e.getMessage());
        }
    }

    /******************************************20171009******************************************/

    /**
     * 积分搜索条
     *
     * @param pageable
     * @param status
     * @param keyword
     * @return
     * @throws Exception
     */
    @GetMapping("/integralLogSearchBar")
    @ApiOperation("积分搜索条")
    public Page<IntegralLogInfo> integralLogSearchBar(@PageableDefault(size = 10) Pageable pageable, @ApiParam("积分状态") IntegralLogStatusEnum status, @ApiParam("关键词") String keyword) throws Exception {
        try {
            return integralLogService.integralLogSearchBar(status, keyword, pageable);
        } catch (Exception e) {
            LOGGER.info("integralLogSearchBar:" + e.getMessage());
            throw new Exception("integralLogSearchBar" + e.getMessage());
        }
    }

    /**
     * 积分搜索条
     *
     * @param pageable
     * @param keyword
     * @return
     * @throws Exception
     */
    @GetMapping("/integralLogSearchBarWithoutStatus")
    @ApiOperation("积分搜索条")
    public Page<IntegralLogInfo> integralLogSearchBarWithoutStatus(@PageableDefault(size = 10) Pageable pageable, @ApiParam("关键词") String keyword) throws Exception {
        try {
            return integralLogService.integralLogSearchBarWithoutStatus(keyword, pageable);
        } catch (Exception e) {
            LOGGER.info("integralLogSearchBar:" + e.getMessage());
            throw new Exception("integralLogSearchBar" + e.getMessage());
        }
    }

    /**
     * 修改积分状态
     *
     * @param status
     * @return
     */
    @GetMapping("/updateIntegralLogStatus")
    @ApiOperation("修改积分状态")
    public String updateIntegralLogStatus(@ApiParam("学生编号）") List<String> studentNumList, @ApiParam("项目编号") List<String> projectNumList, @ApiParam("积分状态") IntegralLogStatusEnum status) {
        try {
            List<IntegralLogIdInfo> integralLogIdInfoList = new ArrayList<IntegralLogIdInfo>();
            for (int i = 0; i < studentNumList.size(); i++) {
                integralLogIdInfoList.get(i).setProjectNum(projectNumList.get(i));
                integralLogIdInfoList.get(i).setStudentNum(studentNumList.get(i));
            }
            return integralLogService.updateIntegralLogStatus(integralLogIdInfoList, status);
        } catch (Exception e) {
            LOGGER.info("updateIntegralLogStatus:" + e.getMessage());
        }
        return null;
    }

    /**
     * 修改积分状态
     *
     * @return
     */
    @GetMapping("/updateIntegralLog")
    @ApiOperation("修改积分状态")
    public IntegralLogInfo updateIntegralLog(String projectNum) {
        try {
//            IntegralLogInfo integralLogInfo = integralLogService.getByIntegralLogId(new IntegralLogIdInfo(getUser().getUserNum(), projectNum));
            IntegralLogInfo integralLogInfo = integralLogService.getByIntegralLogId(new IntegralLogIdInfo(getUser().getUserId(), projectNum));
            if (integralLogInfo != null) {
                integralLogInfo.setStatus(failIntegralLogStatus(integralLogInfo.getStatus()));
                integralLogInfo = integralLogService.changeAllIntegralLogStatus(integralLogInfo);
                return integralLogInfo;
            }
        } catch (Exception e) {
            LOGGER.info("updateIntegralLog:" + e.getMessage());
        }
        return null;
    }

    /**
     * 我的项目搜索条
     *
     * @param pageable
     * @param keyword
     * @return
     * @throws Exception
     */
    @GetMapping("/myProjectSearchBar")
    @ApiOperation("我的项目搜索条")
    public Page<ProjectInfo> myProjectSearchBar(@PageableDefault(size = 10) Pageable pageable, @ApiParam("关键词") String keyword) throws Exception {
        try {
            Page<ProjectInfo> projectInfoPage = projectService.projectSearchBar(getUser().getUserId(), keyword, pageable);
            return projectInfoPage;
        } catch (Exception e) {
            LOGGER.info("myProjectSearchBar:" + e.getMessage());
            throw new Exception("myProjectSearchBar" + e.getMessage());
        }
    }

    /**
     * 项目导出
     *
     * @return
     */
    @ApiOperation("项目导出")
    @GetMapping("exportProjectSearchBar")
    public void exportProjectSearchBar(HttpServletResponse response, String keyword, List<String> baseModel) {
        try {
//            List<Object> byProjectNum = projectService.exportProjectSearchBar(getUser().getUserNum(), keyword);
            List<Object> byProjectNum = projectService.exportProjectSearchBar(getUser().getUserId(), keyword);
            Workbook wk = ExcelDocument.download((String[]) baseModel.toArray(), ProjectInfo.class, byProjectNum);
            downLoadExcel(response, wk, "project");
        } catch (Exception e) {
            LOGGER.info("exportProject:" + e.getMessage());
            try {
                response.getWriter().println("下载失败！");
            } catch (IOException e1) {
            }
        }
    }

    /**
     * 更新项目
     *
     * @param projectInfo
     * @return
     * @throws Exception
     */
    @PostMapping("/updateMyProject")
    @ApiOperation("更新项目")
    public String updateMyProject(String projectInfo) {
        try {
            ProjectInfo pr = JSON.parseObject(projectInfo, ProjectInfo.class);
            ProjectInfo p = projectService.getProjectByNum(pr.getProjectNum());

            p.setStartTime(pr.getStartTime());
            p.setProjectCategory(pr.getProjectCategory());
            p.setDeclareUnit(pr.getDeclareUnit());
            p.setProjectName(pr.getProjectName());
            p.setGuidanceMan(pr.getGuidanceMan());
            p.setIntegral(pr.getIntegral());
            p.setStartTime(pr.getStartTime());
            p.setEndTime(pr.getEndTime());
            p.setMaximum(pr.getMaximum());
            p.setContent(pr.getContent());
            p.setCheckAssessmentCriteraAndForm(pr.getCheckAssessmentCriteraAndForm());
            p.setOnOff(pr.getOnOff());
            projectService.updateProject(p);

            return "SUCCESS";
        } catch (Exception e) {
            LOGGER.info("updateProject:" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/deleteProject")
    @ApiOperation("删除项目")
    public String deleteProject(String projectNum) {
        try {
            ProjectInfo projectInfo = projectService.getProjectByNum(projectNum);
            if (projectInfo == null) {
                return null;
            }
            return projectService.deleteProject(projectInfo.getId());
        } catch (Exception e) {
            LOGGER.info("deleteProject:" + e.getMessage());
            return null;
        }
    }

    /**
     * 更新项目
     *
     * @param projectInfo
     * @return
     * @throws Exception
     */
    @PostMapping("/updateProjectOnOff")
    @ApiOperation("更新项目")
    public String updateProjectOnOff(String projectInfo) {
        try {
            ProjectInfo pr = JSON.parseObject(projectInfo, ProjectInfo.class);
            ProjectInfo p = projectService.getProjectByNum(pr.getProjectNum());

            p.setOnOff(pr.getOnOff());
            projectService.updateProject(p);

            return "SUCCESS";
        } catch (Exception e) {
            LOGGER.info("updateProject:" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/changeProjectStatusUrl")
    public String changeProjectStatusUrl(String projectNum) {
        try {
            SocialUsers socialUsers = getUser();
            ProjectInfo projectInfo = projectService.getProjectByNum(projectNum);

            if (socialUsers.getRank().equals("1") && projectInfo.getImplementationProcess() == ImplementationProcessEnum.SAVED) {
                projectInfo.setImplementationProcess(ImplementationProcessEnum.IN_AUDIT_FINAL);
            } else if (socialUsers.getRank().equals("2") && projectInfo.getImplementationProcess() == ImplementationProcessEnum.SAVED) {
                projectInfo.setImplementationProcess(ImplementationProcessEnum.IN_AUDIT_AGAIN);
            } else {
                if (projectInfo.getImplementationProcess() == ImplementationProcessEnum.SAVED || projectInfo.getImplementationProcess() == ImplementationProcessEnum.IN_AUDIT_FAIL) {
                    projectInfo.setImplementationProcess(ImplementationProcessEnum.IN_AUDIT);
                } else if (projectInfo.getImplementationProcess() == ImplementationProcessEnum.IN_AUDIT_AGAIN_FAIL) {
                    projectInfo.setImplementationProcess(ImplementationProcessEnum.IN_AUDIT_AGAIN);
                } else if (projectInfo.getImplementationProcess() == ImplementationProcessEnum.IN_AUDIT_FINAL_FAIL) {
                    projectInfo.setImplementationProcess(ImplementationProcessEnum.IN_AUDIT_FINAL);
                } else if (projectInfo.getImplementationProcess() == ImplementationProcessEnum.CHECKING_FAIL) {
                    projectInfo.setImplementationProcess(ImplementationProcessEnum.CHECKING);
                } else if (projectInfo.getImplementationProcess() == ImplementationProcessEnum.CHECKING_AGAIN_FAIL) {
                    projectInfo.setImplementationProcess(ImplementationProcessEnum.CHECKING_AGAIN);
                } else if (projectInfo.getImplementationProcess() == ImplementationProcessEnum.CHECKING_FINAL_FAIL) {
                    projectInfo.setImplementationProcess(ImplementationProcessEnum.CHECKING_FINAL);
                }
            }

            projectService.updateProject(projectInfo);
            return "SUCCESS";
        } catch (Exception e) {
            LOGGER.info("changeProjectStatusUrl:" + e.getMessage());
            return null;
        }
    }

    /**
     * 项目审核、验证，通过、不通过
     *
     * @param projectNumList
     * @param status
     * @param failReason
     * @return
     */
    @GetMapping("/changeAllProjectStatus")
    @ApiOperation("审核、验证，通过、不通过")
    public String changeAllProjectStatus(String projectNumList, String status, String failReason) {
        try {
            List<String> projectNumLists = JSONArray.parseArray(projectNumList, String.class);
            for (String projectNum : projectNumLists) {
                ProjectInfo projectInfo = projectService.getProjectByNum(projectNum);
                if (!status.equals("PASS")) {
                    projectInfo.setImplementationProcess(failProjectStatus(projectInfo.getImplementationProcess()));
                    AdviceInfo adviceInfo = new AdviceInfo();
                    adviceInfo.setAdvice(failReason);
                    adviceInfo.setCreatTime(new Date());
//                    adviceInfo.setOperator(getUser().getUsername());
                    adviceInfo.setOperator(getUser().getUserNum());
//                    adviceInfo.setOperatorNum(getUser().getUserNum());
                    adviceInfo.setOperatorNum(getUser().getUserId());
                    adviceInfo.setStatusOperation(AdviceStatusOperationEnum.NOT_PASS);
                    adviceInfo.setProjectId(projectInfo.getId());
                    adviceService.addAdvice(adviceInfo);
                } else {
                    projectInfo.setImplementationProcess(changeProjectStatus(projectInfo.getImplementationProcess()));
                }
                projectService.updateProject(projectInfo);
            }
            return "SUCCESS";
        } catch (Exception e) {
            LOGGER.info("changeAllProjectStatus:" + e.getMessage());
        }
        return null;
    }

    /**
     * 积分审核
     *
     * @param projectNumList
     * @param studentNumList
     * @param status
     * @param failReason
     * @return
     */
    @PostMapping("/changeAllIntegralLogStatus")
    @ApiOperation("审核，通过、不通过")
    public String changeAllIntegralLogStatus(String projectNumList, String studentNumList, String status, String failReason) {
        try {
            List<String> projectNums = JSONArray.parseArray(projectNumList, String.class);
            List<String> studentNums = JSONArray.parseArray(studentNumList, String.class);
            for (int i = 0; i < projectNums.size(); i++) {
                IntegralLogInfo integralLogInfo = integralLogService.getByIntegralLogId(new IntegralLogIdInfo(studentNums.get(i), projectNums.get(i)));
                if (integralLogInfo != null) {
                    if (!status.equals("PASS")) {
                        integralLogInfo.setStatus(failIntegralLogStatus(integralLogInfo.getStatus()));
                    } else {
                        if (integralLogInfo.getStatus().equals(IntegralLogStatusEnum.PENDING_AUDIT_FINAL)) {
                            GrainCoinLogInfo grainCoinLogInfo = new GrainCoinLogInfo();
                            grainCoinLogInfo.setCreatTime(new Date());
                            grainCoinLogInfo.setEvent(integralLogInfo.getEvent());
                            grainCoinLogInfo.setScore((integralLogInfo.getIntegral() * 1000));
                            grainCoinLogInfo.setType(GrainCoinLogTypeEnum.INCOME);
                            grainCoinLogInfo.setStatus(GrainCoinLogStatusEnum.HAVEDONE);
                            grainCoinLogInfo.setUserNum(studentNums.get(i));
                            grainCoinLogInfo.setProjectName(integralLogInfo.getProjectName());
                            grainCoinLogInfo.setProjectNum(integralLogInfo.getIntegralLogId().getProjectNum());
                            grainCoinLogService.saveGrainCoinLog(grainCoinLogInfo);
                        }
                        integralLogInfo.setStatus(changeIntegralLogStatus(integralLogInfo.getStatus()));
                    }
                    integralLogService.changeAllIntegralLogStatus(integralLogInfo);
                }
            }
            return "SUCCESS";
        } catch (Exception e) {
            LOGGER.info("changeAllIntegralLogStatus:" + e.getMessage());
        }
        return null;
    }

    /**
     * 项目搜索条
     *
     * @param pageable
     * @param keyword
     * @return
     * @throws Exception
     */
    @GetMapping("/projectSearchBar")
    @ApiOperation("项目搜索条")
    public Page<ProjectInfo> projectSearchBar(@PageableDefault(size = 10) Pageable pageable, @ApiParam("关键词") String keyword) throws Exception {
        try {
            return projectService.projectSearchBar(keyword, pageable);
        } catch (Exception e) {
            LOGGER.info("projectSearchBar:" + e.getMessage());
            throw new Exception("projectSearchBar" + e.getMessage());
        }
    }

    /**
     * 获取所有用户
     *
     * @param pageable
     * @param userCondition
     * @return
     * @throws Exception
     */
    @GetMapping("/getAllUser")
    @ApiOperation("获取所有用户")
    public Page<UserInfo> getAllUser(@PageableDefault(size = 10) Pageable pageable, UserCondition userCondition) throws Exception {
        Page<UserInfo> userInfoPage = null;
        try {
            userInfoPage = userService.searchUser(userCondition, pageable);
            return userInfoPage;
        } catch (Exception e) {
            LOGGER.info("getAllUser:" + e.getMessage());
            throw new Exception("getAllUser" + e.getMessage());
        }
    }

    /**
     * 获取所有用户
     *
     * @param pageable
     * @param userCondition
     * @return
     * @throws Exception
     */
    @GetMapping("/getMyAllUser")
    @ApiOperation("获取所有用户")
    public Page<UserInfo> getMyAllUser(@PageableDefault(size = 10) Pageable pageable, UserCondition userCondition) throws Exception {
        Page<UserInfo> userInfoPage = null;
        try {
            SocialUsers socialUsers = getUser();
            if (socialUsers.getRank().equals("3")) {
                userCondition.setCollege(socialUsers.getCollege());
            }
            userInfoPage = userService.searchUser(userCondition, pageable);
            return userInfoPage;
        } catch (Exception e) {
            LOGGER.info("getAllUser:" + e.getMessage());
            throw new Exception("getAllUser" + e.getMessage());
        }
    }

    /**
     * 用户导出
     *
     * @return
     */
    @ApiOperation("用户导出")
    @GetMapping("/exportUser")
    public void exportUser(HttpServletResponse response, UserCondition condition, String baseModel) {
        try {
            List<String> parseArray = JSONArray.parseArray(baseModel, String.class);
            String[] desc = new String[]{};
            String[] array = parseArray.toArray(desc);
            List<Object> byProjectNum = userService.listUser(condition);
            Workbook wk = ExcelDocument.download(array, UserInfo.class, byProjectNum);
            downLoadExcel(response, wk, "积分汇总.xls");
        } catch (Exception e) {
            LOGGER.info("exportUser:" + e.getMessage());
            try {
                response.getWriter().println("下载失败！");
            } catch (IOException e1) {
            }
        }
    }

    /**
     * 教师查询目前汇总工作量
     *
     * @param teacherNum
     * @return
     */
    @GetMapping("/getTeacherNowWorkloadSummary")
    @ApiOperation("教师查询目前汇总工作量")
    public Double getTeacherNowWorkloadSummary(@ApiParam("教师编号") String teacherNum) throws Exception {
        try {
            List<String> projectNumList = projectService.getByGuidanceNum(teacherNum); // 获取到的是项目编号的List
            List<IntegralLogInfo> integralLogInfoAllList = new ArrayList<IntegralLogInfo>();
            projectNumList.stream().parallel().forEach((i) -> {
                List<IntegralLogInfo> integralLogInfoList = integralLogService.getByProjectNum(i); // 获取积分列表
                integralLogInfoAllList.addAll(integralLogInfoList);
            });
            //用流实现
            return integralLogInfoAllList.stream().parallel().reduce(IntegralLogInfo::doSum).get().getIntegral();
        } catch (Exception e) {
            LOGGER.info("getTeacherNowWorkloadSummary:" + e.getMessage());
//            throw new Exception("getTeacherNowWorkloadSummary" + e.getMessage());
            return null;
        }
    }

    /**
     * 教师查询汇总工作量
     *
     * @param teacherNum
     * @return
     */
    @GetMapping("/getTeacherAllWorkloadSummary")
    @ApiOperation("教师查询汇总工作量")
    public double getTeacherAllWorkloadSummary(@ApiParam("教师编号") String teacherNum) {
        try {
            return projectService.getTeacherAllWorkloadSummary(teacherNum);
        } catch (Exception e) {
            LOGGER.info("getTeacherAllWorkloadSummary:" + e.getMessage());
//            throw new Exception("getTeacherAllWorkloadSummary" + e.getMessage());
            return 0;
        }
    }

    /**
     * 更新奖品
     *
     * @param prizeInfo
     * @return
     * @throws Exception
     */
    @GetMapping("/updatePrize")
    @ApiOperation("更新奖品")
    public PrizeInfo updatePrize(PrizeInfo prizeInfo) {
        try {
            return prizeService.updatePrize(prizeInfo);
        } catch (Exception e) {
            LOGGER.info("updatePrize:" + e.getMessage());
            return null;
        }
    }

    /**
     * 更新奖品状态
     *
     * @param status
     * @return
     * @throws Exception
     */
    @GetMapping("/changePrizeStatus")
    @ApiOperation("更新奖品")
    public String changePrizeStatus(String status, long prizeId) {
        try {
            PrizeInfo prizeInfo = prizeService.getPrizeById(prizeId);
            if (prizeInfo == null) {
                return null;
            } else {
                if (status != null && status.equals("上架")) {
                    prizeInfo.setStatus(PrizeStatusEnum.SHELVES);
                    prizeService.updatePrize(prizeInfo);
                    return "SUCCESS";
                }
                if (status != null && status.equals("下架")) {
                    prizeInfo.setStatus(PrizeStatusEnum.ALREADY_LAID);
                    prizeService.updatePrize(prizeInfo);
                    return "SUCCESS";
                }
            }
            return null;
        } catch (Exception e) {
            LOGGER.info("updatePrize:" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取所有奖品交易记录
     *
     * @param pageable
     * @param grainCoinLogCondition
     * @return
     * @throws Exception
     */
    @GetMapping("/getGrainCoinLog")
    @ApiOperation("获取所有奖品交易记录")
    public Page<GrainCoinLogInfo> getGrainCoinLog(@PageableDefault(size = 10) Pageable pageable, GrainCoinLogCondition grainCoinLogCondition) throws Exception {
        try {
            return grainCoinLogService.searchPrizeByCondition(grainCoinLogCondition, pageable);
        } catch (Exception e) {
            LOGGER.info("getGrainCoinLog:" + e.getMessage());
            throw new Exception("getGrainCoinLog" + e.getMessage());
        }
    }

    /**
     * 积分导出
     *
     * @return
     */
    @ApiOperation("积分导出")
    @GetMapping("/exportGrainCoinLog")
    public void exportGrainCoinLog(HttpServletResponse response, GrainCoinLogCondition condition, String baseModel) {
        try {
            List<String> parseArray = JSONArray.parseArray(baseModel, String.class);
            String[] desc = new String[]{};
            String[] array = parseArray.toArray(desc);
            List<Object> byProjectNum = grainCoinLogService.exportGrainCoinLog(condition);
            Workbook wk = ExcelDocument.download(array, GrainCoinLogInfo.class, byProjectNum);
            downLoadExcel(response, wk, "兑换记录.xls");
        } catch (Exception e) {
            LOGGER.info("exportGrainCoinLog:" + e.getMessage());
            try {
                response.getWriter().println("下载失败！");
            } catch (IOException e1) {
            }
        }
    }

    /**
     * 更新用户
     *
     * @param userInfo
     * @return
     * @throws Exception
     */
    @PostMapping("/updateUser")
    @ApiOperation("更新用户")
    public String updateUser(UserInfo userInfo) {
        try {
            UserInfo u = userService.getUserByUserNum(userInfo.getUserNum());
            u.setCollege(userInfo.getCollege());
            u.setClazz(userInfo.getClazz());
            u.setUsername(userInfo.getUsername());
            userService.updateUser(u);
            return "SUCCESS";
        } catch (Exception e) {
            LOGGER.info("updateUser:" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/deleteUser")
    @ApiOperation("删除用户")
    public String deleteUser(String userNum) {
        try {
            UserInfo userInfo = userService.getUserByUserNum(userNum);
            if (userInfo != null) {
                userService.deleteUser(userNum);
                return "SUCCESS";
            } else {
                return "ERROR";
            }
        } catch (Exception e) {
            LOGGER.info("deleteUser:" + e.getMessage());
            return null;
        }
    }

    /**
     * 添加用户
     *
     * @param userInfo
     * @return
     * @throws Exception
     */
    @PostMapping("/addUser")
    @ApiOperation("添加用户")
    public String addUser(UserInfo userInfo) {
        try {
            UserInfo u = userService.getUserByUserNum(userInfo.getUserNum());
            if (u != null) {
                return "HAVE";
            } else {
                userInfo.setCreatTime(new Date());
                userInfo.setStatus(UserStatusEnum.NORMAL_CONDITION);
                userService.addUser(userInfo);
                return "SUCCESS";
            }
        } catch (Exception e) {
            LOGGER.info("addUser:" + e.getMessage());
            return null;
        }
    }

    /**
     * 添加奖品
     *
     * @param prizeInfo
     * @return
     * @throws Exception
     */
    @PostMapping("/addPrize")
    @ApiOperation("添加奖品")
    public PrizeInfo addPrize(PrizeInfo prizeInfo, HttpServletRequest request) {
        try {
            String url = getUrl(request, "file");
            if (url == null) {
                return null;
            }
            if (url.equals("NO_FILE")) {
                url = null;
            }
            List<String> pics = new ArrayList<>();
            pics.add(url);

            prizeInfo.setPrizePic(pics);
            prizeInfo.setCreatTime(new Date());
            prizeInfo.setStatus(PrizeStatusEnum.SHELVES);
            return prizeService.addPrize(prizeInfo);
        } catch (Exception e) {
            LOGGER.info("addPrize:" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/deletePrize")
    @ApiOperation("删除奖品")
    public String deletePrize(long id) {
        try {
            PrizeInfo prizeInfo = prizeService.getPrizeById(id);
            if (prizeInfo != null) {
                return prizeService.deletePrize(id);
            }
            return "ERROR";
        } catch (Exception e) {
            LOGGER.info("deletePrize:" + e.getMessage());
            return null;
        }
    }

    /**
     * 根据积分ID获取积分详情
     *
     * @param projectNum
     * @return
     * @throws Exception
     */
    @GetMapping("/getIntegralLogByIntegralLogId")
    @ApiOperation("根据积分ID获取积分详情")
    public IntegralLogInfo getIntegralLogByIntegralLogId(String projectNum) throws Exception {
        try {
            IntegralLogIdInfo integralLogIdInfo = new IntegralLogIdInfo();
//            integralLogIdInfo.setStudentNum(getUser().getUserNum());
            integralLogIdInfo.setStudentNum(getUser().getUserId());
            integralLogIdInfo.setProjectNum(projectNum);
            return integralLogService.getByIntegralLogId(integralLogIdInfo);
        } catch (Exception e) {
            LOGGER.info("getIntegralLogByIntegralLogId:" + e.getMessage());
            throw new Exception("getIntegralLogByIntegralLogId" + e.getMessage());
        }
    }

    /**
     * 根据单个积分状态获取积分
     *
     * @param pageable
     * @param status
     * @return
     * @throws Exception
     */
    @GetMapping("/getIntegralLogWithOutPass")
    @ApiOperation("根据单个积分状态获取积分")
    public Page<IntegralLogInfo> getIntegralLogWithOutPass(@PageableDefault(size = 10) Pageable pageable, IntegralLogStatusEnum status) throws Exception {
        try {
            return integralLogService.getIntegralLogWithOutPass(status, pageable);
        } catch (Exception e) {
            LOGGER.info("getIntegralLogWithOutPass:" + e.getMessage());
            throw new Exception("getIntegralLogWithOutPass" + e.getMessage());
        }
    }

    /**
     * 根据项目编号与用户编号获取积分
     *
     * @return
     */
    @GetMapping("/getIntegralLogByProjectNumStudentNum")
    @ApiOperation("获取所有积分")
    public IntegralLogInfo getIntegralLogByProjectNumStudentNum(String projectNum, String studentNum) {
        try {
            IntegralLogIdInfo integralLogIdInfo = new IntegralLogIdInfo();
            integralLogIdInfo.setStudentNum(studentNum);
            integralLogIdInfo.setProjectNum(projectNum);
            IntegralLogInfo integralLogInfo = integralLogService.getByIntegralLogId(integralLogIdInfo);
            return integralLogInfo;
        } catch (Exception e) {
            LOGGER.info("getIntegralLogWithOutPass:" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取所有积分
     *
     * @param integralLogCondition
     * @param pageable
     * @return
     */
    @GetMapping("/getAllIntegralLog")
    @ApiOperation("获取所有积分")
    public Page<IntegralLogInfo> getAllIntegralLog(IntegralLogCondition integralLogCondition, @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<IntegralLogInfo> integralLogInfoPage = integralLogService.searchIntegralLog(integralLogCondition, pageable);
            System.out.println("!#@$!@#$!@#$!@#$!@#$!@#$!@#$!@#$!@#$!@#$!@#$" + integralLogInfoPage.getContent().size());
            return integralLogInfoPage;
        } catch (Exception e) {
            LOGGER.info("getIntegralLogWithOutPass:" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取用户的积分
     *
     * @return
     * @throws Exception
     */
    @ApiOperation("获取用户的积分")
    @GetMapping("/getIntegeral")
    public double getIntegeral(String studentNum) throws Exception {
        try {
            return integralLogService.watchIntegral(studentNum);
        } catch (Exception e) {
            LOGGER.info("getIntegeral :" + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 获取学生素拓币
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/getGrainCoin")
    public double getGrainCoin(String studentNum) throws Exception {
        try {
            return grainCoinLogService.watchGrainCoin(studentNum);
        } catch (Exception e) {
            LOGGER.info("getGrainCoin:" + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 用户搜索条
     *
     * @param pageable
     * @param keyword
     * @return
     * @throws Exception
     */
    @GetMapping("/userSearchBar")
    public Page<UserInfo> userSearchBar(@PageableDefault(size = 10) Pageable pageable, String keyword) throws Exception {
        try {
            return userService.userSearchBar(keyword, pageable);
        } catch (Exception e) {
            LOGGER.info("userSearchBar:" + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /*******************************************20171018*******************************************/

    /**
     * 查找我的项目
     *
     * @param pageable
     * @return
     */
    @GetMapping("/getProjectByGuidanceNum")
    public Page<ProjectInfo> getProjectByGuidanceNum(@PageableDefault(size = 10) Pageable pageable, ProjectCondition projectCondition) {
        try {
//            projectCondition.setGuidanceNum(getUser().getUserNum());
            projectCondition.setGuidanceNum(getUser().getUserId());
            Page<ProjectInfo> projectInfoPage = projectService.searchProject(projectCondition, pageable);
            System.out.println(projectInfoPage.toString());
            return projectInfoPage;
        } catch (Exception e) {
            LOGGER.info("getProjectByGuidanceNum:" + e.getMessage());
            return null;
        }
    }

    /**
     * 我的项目导出
     *
     * @return
     */
    @ApiOperation("我的项目导出")
    @GetMapping("/exportProjectByGuidanceNum")
    public void exportProjectByGuidanceNum(HttpServletResponse response, ProjectCondition projectCondition, String baseModel) {
        try {
//            projectCondition.setGuidanceNum(getUser().getUserNum());
            projectCondition.setGuidanceNum(getUser().getUserId());

            List<String> parseArray = JSONArray.parseArray(baseModel, String.class);
            String[] desc = new String[]{};
            String[] array = parseArray.toArray(desc);
            List<Object> byProjectNum = projectService.listProject(projectCondition);
            Workbook wk = ExcelDocument.download(array, ProjectInfo.class, byProjectNum);
            downLoadExcel(response, wk, "我的项目.xls");
        } catch (Exception e) {
            LOGGER.info("exportProjectByGuidanceNum:" + e.getMessage());
            try {
                response.getWriter().println("下载失败！");
            } catch (IOException e1) {
            }
        }
    }

    /**
     * 我的积分导出
     *
     * @return
     */
    @ApiOperation("我的积分导出")
    @GetMapping("/exportIntegralLogByGuidanceNum")
    public void exportIntegralLogByGuidanceNum(HttpServletResponse response, IntegralLogCondition integralLogCondition, String baseModel) {
        try {
//            projectCondition.setGuidanceNum(getUser().getUserNum());
            IntegralLogIdInfo integralLogIdInfo = new IntegralLogIdInfo();
            integralLogIdInfo.setStudentNum(getUser().getUserId());
            integralLogCondition.setIntegralLogId(integralLogIdInfo);

            List<String> parseArray = JSONArray.parseArray(baseModel, String.class);
            String[] desc = new String[]{};
            String[] array = parseArray.toArray(desc);
            List<Object> byProjectNum = integralLogService.listIntegralLog(integralLogCondition);
            Workbook wk = ExcelDocument.download(array, IntegralLogInfo.class, byProjectNum);
            downLoadExcel(response, wk, "我的积分.xls");
        } catch (Exception e) {
            LOGGER.info("exportIntegralLogByGuidanceNum:" + e.getMessage());
            try {
                response.getWriter().println("下载失败！");
            } catch (IOException e1) {
            }
        }
    }

    /**
     * 查询project
     *
     * @param projectCondition
     * @param pageable
     * @return
     */
    @GetMapping("/searchProject")
    @ApiOperation("查找项目")
    public Page<ProjectInfo> searchProject(ProjectCondition projectCondition, @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<ProjectInfo> projectInfoPage = projectService.searchProject(projectCondition, pageable);
            return projectInfoPage;
        } catch (Exception e) {
            LOGGER.info("searchProject:" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取学院
     *
     * @param collegeCondition
     * @param pageable
     * @return
     */
    @GetMapping("/getCollege")
    @ApiOperation("获取学院")
    public Page<CollegeInfo> getCollege(CollegeCondition collegeCondition, @PageableDefault(size = 20) Pageable pageable) {
        try {
            Page<CollegeInfo> collegeInfoPage = collegeService.searchCollege(collegeCondition, pageable);
            return collegeInfoPage;
        } catch (Exception e) {
            LOGGER.info("getCollege:" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取所有学院
     *
     * @param collegeCondition
     * @param pageable
     * @return
     */
    @GetMapping("/getAllColleges")
    @ApiOperation("获取所有学院")
    public Page<CollegeInfo> getAllColleges(CollegeCondition collegeCondition, @PageableDefault(size = 20) Pageable pageable) {
        try {
            Page<CollegeInfo> collegeInfoPage = collegeService.searchCollege(collegeCondition, pageable);
            return collegeInfoPage;
        } catch (Exception e) {
            LOGGER.info("getAllColleges:" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取所有学院
     *
     * @param collegeCondition
     * @param pageable
     * @return
     */
    @GetMapping("/getMyColleges")
    @ApiOperation("获取所有学院")
    public Page<CollegeInfo> getMyColleges(CollegeCondition collegeCondition, @PageableDefault(size = 20) Pageable pageable) {
        try {
            SocialUsers socialUsers = getUser();
            collegeCondition.setCollege(socialUsers.getCollege());
            Page<CollegeInfo> collegeInfoPage = collegeService.searchCollege(collegeCondition, pageable);
            return collegeInfoPage;
        } catch (Exception e) {
            LOGGER.info("getAllColleges:" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取所有班级
     *
     * @param clazzCondition
     * @param pageable
     * @return
     */
    @GetMapping("/getClazz")
    @ApiOperation("获取班级")
    public Page<ClazzInfo> getClazz(ClazzCondition clazzCondition, @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<ClazzInfo> clazzInfoPage = clazzService.searchClazz(clazzCondition, pageable);
            return clazzInfoPage;
        } catch (Exception e) {
            LOGGER.info("getClazz:" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取项目状态列表
     *
     * @return
     */
    @GetMapping("/getProjectStatus")
    @ApiOperation("获取项目状态列表")
    public List<String> getProjectStatus() {
        try {
            Map map = ImplementationProcessEnum.toMap();

            List<String> projectStatusList = new ArrayList<>();

            Set entries = map.entrySet();
            Iterator iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                projectStatusList.add(String.valueOf(entry.getValue()));
            }

            return projectStatusList;
        } catch (Exception e) {
            LOGGER.info("getProjectStatus:" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/getUserByUserNum")
    @ApiOperation("根据用户编号查询用户")
    public UserInfo getUserByUserNum(String userNum) {
        try {
            UserInfo userInfo = userService.getUserByUserNum(userNum);
            return userInfo;
        } catch (Exception e) {
            LOGGER.info("getUserByUserNum:" + e.getMessage());
            return null;
        }
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping("/getUserInfo")
    public UserInfo getUserInfo() {
//        UserInfo userInfo = userService.getUserByUserNum(getUser().getUserNum());
        UserInfo userInfo = userService.getUserByUserNum(getUser().getUserId());
        return userInfo;
    }

    private SocialUsers getUser() {
        return UserUtils.getUser();
    }

    private void downLoadExcel(HttpServletResponse response, Workbook wk, String fileName) throws Exception {
        OutputStream outputStream = null;
        try {
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setHeader("Connection", "close");
            response.setHeader("Content-Type", "application/octet-stream");
            outputStream = response.getOutputStream();
            wk.write(outputStream);
            outputStream.flush();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    /**
     * 修改通过项目状态
     *
     * @param implementationProcessEnum
     * @return
     */
    private ImplementationProcessEnum changeProjectStatus(ImplementationProcessEnum implementationProcessEnum) {
        if (implementationProcessEnum == ImplementationProcessEnum.IN_AUDIT) {
            return ImplementationProcessEnum.IN_AUDIT_AGAIN;
        } else if (implementationProcessEnum == ImplementationProcessEnum.IN_AUDIT_AGAIN) {
            return ImplementationProcessEnum.IN_AUDIT_FINAL;
        } else if (implementationProcessEnum == ImplementationProcessEnum.IN_AUDIT_FINAL) {
            return ImplementationProcessEnum.AUDITED;
        } else if (implementationProcessEnum == ImplementationProcessEnum.CHECKING) {
            return ImplementationProcessEnum.CHECKING_FINAL;
        } else if (implementationProcessEnum == ImplementationProcessEnum.CHECKING_AGAIN) {
            return ImplementationProcessEnum.CHECKING_FINAL;
        } else {
            return ImplementationProcessEnum.CHECKED;
        }
    }

    /**
     * 修改未通过项目状态
     *
     * @param implementationProcessEnum
     * @return
     */
    private ImplementationProcessEnum failProjectStatus(ImplementationProcessEnum implementationProcessEnum) {
        if (implementationProcessEnum == ImplementationProcessEnum.IN_AUDIT) {
            return ImplementationProcessEnum.IN_AUDIT_FAIL;
        } else if (implementationProcessEnum == ImplementationProcessEnum.IN_AUDIT_AGAIN) {
            return ImplementationProcessEnum.IN_AUDIT_AGAIN_FAIL;
        } else if (implementationProcessEnum == ImplementationProcessEnum.IN_AUDIT_FINAL) {
            return ImplementationProcessEnum.IN_AUDIT_FINAL_FAIL;
        } else if (implementationProcessEnum == ImplementationProcessEnum.CHECKING) {
            return ImplementationProcessEnum.CHECKING_FAIL;
        } else if (implementationProcessEnum == ImplementationProcessEnum.CHECKING_AGAIN) {
            return ImplementationProcessEnum.CHECKING_AGAIN_FAIL;
        } else if (implementationProcessEnum == ImplementationProcessEnum.CHECKING_FINAL) {
            return ImplementationProcessEnum.CHECKING_FINAL_FAIL;
        } else if (implementationProcessEnum == ImplementationProcessEnum.IN_AUDIT_FAIL) {
            return ImplementationProcessEnum.IN_AUDIT;
        } else if (implementationProcessEnum == ImplementationProcessEnum.IN_AUDIT_AGAIN_FAIL) {
            return ImplementationProcessEnum.IN_AUDIT_AGAIN;
        } else if (implementationProcessEnum == ImplementationProcessEnum.IN_AUDIT_FINAL_FAIL) {
            return ImplementationProcessEnum.IN_AUDIT_FINAL;
        } else if (implementationProcessEnum == ImplementationProcessEnum.CHECKING_FAIL) {
            return ImplementationProcessEnum.CHECKING;
        } else if (implementationProcessEnum == ImplementationProcessEnum.CHECKING_AGAIN_FAIL) {
            return ImplementationProcessEnum.CHECKING_AGAIN;
        } else {
            return ImplementationProcessEnum.CHECKING_FINAL;
        }
    }

    /**
     * 修改通过积分状态
     *
     * @param status
     * @return
     */
    private IntegralLogStatusEnum changeIntegralLogStatus(IntegralLogStatusEnum status) {
        if (status == IntegralLogStatusEnum.PENDING_AUDIT_BEFORE) {
            return IntegralLogStatusEnum.PENDING_AUDIT;
        } else if (status == IntegralLogStatusEnum.PENDING_AUDIT) {
            return IntegralLogStatusEnum.PENDING_AUDIT_AGAIN;
        } else if (status == IntegralLogStatusEnum.PENDING_AUDIT_AGAIN) {
            return IntegralLogStatusEnum.PENDING_AUDIT_FINAL;
        } else {
            return IntegralLogStatusEnum.HAVEPASSED;
        }
    }

    /**
     * 修改未通过积分状态
     *
     * @param status
     * @return
     */
    private IntegralLogStatusEnum failIntegralLogStatus(IntegralLogStatusEnum status) {
        if (status == IntegralLogStatusEnum.PENDING_AUDIT_BEFORE) {
            return IntegralLogStatusEnum.PENDING_AUDIT_BEFORE_FAIL;
        } else if (status == IntegralLogStatusEnum.PENDING_AUDIT) {
            return IntegralLogStatusEnum.PENDING_AUDIT_FAIL;
        } else if (status == IntegralLogStatusEnum.PENDING_AUDIT_AGAIN) {
            return IntegralLogStatusEnum.PENDING_AUDIT_AGAIN_FAIL;
        } else if (status == IntegralLogStatusEnum.PENDING_AUDIT_FINAL) {
            return IntegralLogStatusEnum.PENDING_AUDIT_FINAL_FAIL;
        } else if (status == IntegralLogStatusEnum.PENDING_AUDIT_BEFORE_FAIL) {
            return IntegralLogStatusEnum.PENDING_AUDIT_BEFORE;
        } else if (status == IntegralLogStatusEnum.PENDING_AUDIT_FAIL) {
            return IntegralLogStatusEnum.PENDING_AUDIT;
        } else if (status == IntegralLogStatusEnum.PENDING_AUDIT_AGAIN_FAIL) {
            return IntegralLogStatusEnum.PENDING_AUDIT_AGAIN;
        } else {
            return IntegralLogStatusEnum.PENDING_AUDIT_FINAL;
        }
    }

    public Date getOneDayBefore(Date dateEnd, int dates) {
        Calendar date = Calendar.getInstance();
        date.setTime(dateEnd);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + dates);
        return date.getTime();
    }

    public String getUrl(HttpServletRequest request, String fileName) throws IOException {
        // 转型为MultipartHttpRequest：
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获得文件：
        MultipartFile inputFile = multipartRequest.getFile(fileName);

        if (inputFile == null) {
            return "NO_FILE";
        }

        // 获得文件名：
        String filename = inputFile.getOriginalFilename();

        String uploadFile = FastDFSClient.uploadFile(inputFile.getBytes(), filename);

        String url = Constans.STRONGE_URL + uploadFile;
        return url;

    }
    
    /**
     * 项目导出
     *
     * @return
     */
    @ApiOperation("模板导出")
    @GetMapping("exportModel")
    public void exportModel(HttpServletResponse response, String baseModel,String fileName) {
        try {
            List<String> parseArray = JSONArray.parseArray(baseModel, String.class);
            String[] desc = new String[]{};
            String[] array = parseArray.toArray(desc);
            List<Object> byProjectNum = new ArrayList<>();
            Workbook wk = ExcelDocument.download(array, Object.class, byProjectNum);
            downLoadExcel(response, wk, fileName+".xls");
        } catch (Exception e) {
            LOGGER.info("exportProject:" + e.getMessage());
            try {
                response.getWriter().println("下载失败！");
            } catch (IOException e1) {
            }
        }
    }
}
