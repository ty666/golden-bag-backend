package com.zmdev.goldenbag.web.template_module;

import com.zmdev.goldenbag.domain.*;
import com.zmdev.goldenbag.exception.AuthorizationException;
import com.zmdev.goldenbag.exception.ModelNotFoundException;
import com.zmdev.goldenbag.service.AssessmentTemplateService;
import com.zmdev.goldenbag.service.QuarterService;
import com.zmdev.goldenbag.web.Auth;
import com.zmdev.goldenbag.web.BaseController;
import com.zmdev.goldenbag.web.insterceptor.PermissionInterceptor;
import com.zmdev.goldenbag.web.result.Result;
import com.zmdev.goldenbag.web.result.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/templates", produces = "application/json;charset=UTF-8")
public class TemplateController extends BaseController {

    private AssessmentTemplateService assessmentTemplateService;
    private Auth auth;
    private QuarterService quarterService;

    public TemplateController(@Autowired AssessmentTemplateService assessmentTemplateService) {
        this.assessmentTemplateService = assessmentTemplateService;
    }

    static {
        PermissionInterceptor.addSpecialAbilitie(TemplateController.class, "storeProject", "edit");
        PermissionInterceptor.addSpecialAbilitie(TemplateController.class, "updateProject", "edit");
        PermissionInterceptor.addSpecialAbilitie(TemplateController.class, "storeProjectItem", "edit");
        PermissionInterceptor.addSpecialAbilitie(TemplateController.class, "updateProjectItem", "edit");
        PermissionInterceptor.addSpecialAbilitie(TemplateController.class, "storeTemplateInput", "edit");
        PermissionInterceptor.addSpecialAbilitie(TemplateController.class, "updateTemplateInput", "edit");
        PermissionInterceptor.addSpecialAbilitie(TemplateController.class, "deleteProjectItem", "edit");
        PermissionInterceptor.addSpecialAbilitie(TemplateController.class, "deleteTemplateInput", "edit");
        PermissionInterceptor.addSpecialAbilitie(TemplateController.class, "deleteProject", "edit");
    }

    @Autowired
    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    @Autowired
    public void setQuarterService(QuarterService quarterService) {
        this.quarterService = quarterService;
    }

    @GetMapping
    public Result index(@RequestParam(defaultValue = "") String type) {
        if ("".equals(type)) {
            return ResultGenerator.genSuccessResult(assessmentTemplateService.findByType(null));
        }
        return ResultGenerator.genSuccessResult(
                assessmentTemplateService.findByType(
                        "staff".equals(type) ? AssessmentTemplate.Type.STAFF_TEMPLATE :
                                AssessmentTemplate.Type.MANAGER_TEMPLATE
                )
        );
    }

    @GetMapping("/{templateId}")
    public Result show(@PathVariable Long templateId) {
        return ResultGenerator.genSuccessResult(assessmentTemplateService.findById(templateId));
    }

    @RequestMapping(value = "/{templateId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public Result update(@PathVariable Long templateId, @RequestBody AssessmentTemplate template) {
        return ResultGenerator.genSuccessResult(assessmentTemplateService.updateTemplate(templateId, template));
    }

    @PostMapping("/{templateId}/project")
    public Result storeProject(@PathVariable Long templateId, @RequestBody AssessmentProject assessmentProject) {
        assessmentProject.setId(null);
        return ResultGenerator.genSuccessResult(assessmentTemplateService.saveProject(templateId, assessmentProject));
    }

    @RequestMapping(value = "/project/{projectId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public Result updateProject(@PathVariable Long projectId, @RequestBody AssessmentProject assessmentProject) {
        return ResultGenerator.genSuccessResult(assessmentTemplateService.updateProject(projectId, assessmentProject));
    }

    // 删除项目
    @RequestMapping(value = "/project/{projectId}", method = {RequestMethod.DELETE})
    public Result deleteProject(@PathVariable Long projectId) {
        assessmentTemplateService.deleteProject(projectId);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/{projectId}/project_item")
    public Result storeProjectItem(@PathVariable Long projectId, @RequestBody AssessmentProjectItem projectItem) {
        projectItem.setId(null);
        return ResultGenerator.genSuccessResult(assessmentTemplateService.saveProjectItem(projectId, projectItem));
    }

    @RequestMapping(value = "/project_item/{projectItemId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public Result updateProjectItem(@PathVariable Long projectItemId, @RequestBody AssessmentProjectItem projectItem) {
        return ResultGenerator.genSuccessResult(assessmentTemplateService.updateProjectItem(projectItemId, projectItem));
    }

    // 删除项目 Item
    @RequestMapping(value = "/project_item/{projectItemId}", method = {RequestMethod.DELETE})
    public Result deleteProjectItem(@PathVariable Long projectItemId) {
        assessmentTemplateService.deleteProjectItem(projectItemId);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/{templateId}/template_input")
    public Result storeTemplateInput(@PathVariable Long templateId, @RequestBody AssessmentInput assessmentInput) {
        assessmentInput.setId(null);
        return ResultGenerator.genSuccessResult(assessmentTemplateService.saveTemplateInput(templateId, assessmentInput));
    }

    @RequestMapping(value = "/template_input/{templateInputId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public Result updateTemplateInput(@PathVariable Long templateInputId, @RequestBody AssessmentInput assessmentInput) {
        return ResultGenerator.genSuccessResult(assessmentTemplateService.updateTemplateInput(templateInputId, assessmentInput));
    }

    @RequestMapping(value = "/template_input/{templateInputId}", method = {RequestMethod.DELETE})
    public Result deleteTemplateInput(@PathVariable Long templateInputId) {
        assessmentTemplateService.deleteTemplateInput(templateInputId);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 获取模板类型
     *
     * @return Result
     */
    @GetMapping("/types")
    public Result getTemplateType() {
        List<TemplateType> l = new ArrayList<>();
        for (AssessmentTemplate.Type type : AssessmentTemplate.Type.values()) {
            l.add(new TemplateType(AssessmentTemplate.typeMap.get(type), type));
        }
        return ResultGenerator.genSuccessResult(l);
    }

    /**
     * 获取当前用户当前季度的模板
     *
     * @return Result
     */
    @GetMapping("/get_template")
    public Result getTemplateByAuth() {
        User user = auth.getUser();
        // 获取当前季度
        Quarter currentQuarter = quarterService.findCurrentQuarter();
        if (currentQuarter.getId() == null) {
            throw new ModelNotFoundException("没有设置当前季度");
        }
        if (user == null) {
            throw new AuthorizationException("没有登陆");
        }
        AssessmentTemplate assessmentTemplate = assessmentTemplateService.findByTypeAndQuarter(user.getType(), currentQuarter);
        return ResultGenerator.genSuccessResult(assessmentTemplate);
    }

    private class TemplateType {
        private String name;
        private int value;

        public TemplateType(String name, AssessmentTemplate.Type value) {
            this(name, value.ordinal());
        }

        public TemplateType(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
