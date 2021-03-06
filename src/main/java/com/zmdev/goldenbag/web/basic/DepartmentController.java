package com.zmdev.goldenbag.web.basic;

import com.zmdev.goldenbag.domain.Department;
import com.zmdev.goldenbag.service.DepartmentService;
import com.zmdev.goldenbag.web.BaseController;
import com.zmdev.goldenbag.web.result.Result;
import com.zmdev.goldenbag.web.result.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 直接用@RestController替代@Controller就不需要再配置@ResponseBody，默认返回json格式
@RequestMapping(value = "/departments", produces = "application/json;charset=UTF-8")
public class DepartmentController extends BaseController {

    private DepartmentService departmentService;

    @Autowired
    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * 顯示部門以及項目組列表
     *
     * @return Response
     */
    @GetMapping
    public Result index() {
        try {
            List<Department> departmentList = departmentService.findAllByParent(null);
            return ResultGenerator.genSuccessResult(departmentList);
        } catch (Exception e) {
            return ResultGenerator.genSuccessResult();
        }
    }

    @GetMapping("/{id}")
    public Result show(@PathVariable Long id) {
        return ResultGenerator.genSuccessResult(departmentService.findById(id));
    }

    /**
     * 添加部門
     *
     * @param department
     * @return Response
     */
    @PostMapping
    public Result store(@RequestBody Department department) {
        return ResultGenerator.genSuccessResult(departmentService.save(department));
    }

    //TODO 这里有死循环的BUG
    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public Result update(@PathVariable Long id, @RequestBody Department department) {
        return ResultGenerator.genSuccessResult(departmentService.update(id, department));
    }

    /**
     * 刪除部門
     *
     * @param id
     * @return Response
     */
    @DeleteMapping("/{id}")
    public Result destroy(@PathVariable Long id) {
        departmentService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }
}