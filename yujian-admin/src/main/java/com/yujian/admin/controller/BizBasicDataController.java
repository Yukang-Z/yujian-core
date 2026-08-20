package com.yujian.admin.controller;

import com.yujian.admin.service.IBizBasicDataService;
import com.yujian.common.biz.domain.BizDictData;
import com.yujian.common.biz.domain.BizDictType;
import com.yujian.common.biz.domain.BizPatientSource;
import com.yujian.common.biz.domain.BizPatientTag;
import com.yujian.common.biz.domain.BizTreatItem;
import com.yujian.common.core.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 基础数据管理接口：字典、患者标签/来源、诊疗项目、医生与咨询师列表。
 * <p>
 * 医生 / 咨询师 / 诊疗项目的 clinicId 在账号授权诊所范围内生效。
 * </p>
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Api(tags = "基础数据")
@RestController
@RequestMapping("/biz/basic")
public class BizBasicDataController {

    @Autowired
    private IBizBasicDataService basicDataService;

    /**
     * 查询全部字典类型列表（全局数据，不按诊所隔离）。
     *
     * @return 统一响应，data 为 {@link BizDictType} 列表
     */
    @ApiOperation("字典类型列表")
    @GetMapping("/dict/types")
    public R<List<BizDictType>> dictTypes() {
        return R.ok(basicDataService.selectDictTypeList());
    }

    /**
     * 按类型编码查询字典数据项（全局数据，不按诊所隔离）。
     *
     * @param dictType 字典类型编码，如 appoint_status
     * @return 统一响应，data 为 {@link BizDictData} 列表
     */
    @ApiOperation("字典数据")
    @GetMapping("/dict/{dictType}")
    public R<List<BizDictData>> dictData(
            @ApiParam(value = "字典类型编码", required = true) @PathVariable String dictType) {
        return R.ok(basicDataService.selectDictByType(dictType));
    }

    /**
     * 新增或修改字典数据项（全局数据，不按诊所隔离）。
     *
     * @param data 字典数据项（新增时 id 为空，修改时须含 id）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("保存字典数据")
    @PostMapping("/dict/data")
    public R<?> saveDictData(@RequestBody BizDictData data) {
        return basicDataService.saveDictData(data) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除指定字典数据项（全局数据，不按诊所隔离）。
     *
     * @param id 字典项ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("删除字典数据")
    @PostMapping("/dict/data/remove/{id}")
    public R<?> removeDictData(@ApiParam(value = "字典项ID", required = true) @PathVariable Long id) {
        return basicDataService.deleteDictData(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询当前诊所患者标签列表；按当前所选诊所隔离。
     *
     * @param clinicId 诊所ID（前端传入将被忽略，以当前所选诊所为准）
     * @return 统一响应，data 为 {@link BizPatientTag} 列表
     */
    @ApiOperation("患者标签列表")
    @GetMapping("/tag/list")
    public R<List<BizPatientTag>> tagList(@ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId) {
        return R.ok(basicDataService.selectTagList(clinicId));
    }

    /**
     * 新增或修改患者标签；按当前所选诊所隔离。
     *
     * @param tag 标签信息（新增时 id 为空，修改时须含 id）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("保存患者标签")
    @PostMapping("/tag")
    public R<?> saveTag(@RequestBody BizPatientTag tag) {
        return basicDataService.saveTag(tag) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除指定患者标签；按当前所选诊所隔离。
     *
     * @param id 标签ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("删除患者标签")
    @PostMapping("/tag/remove/{id}")
    public R<?> removeTag(@ApiParam(value = "标签ID", required = true) @PathVariable Long id) {
        return basicDataService.deleteTag(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询当前诊所患者来源树形结构；按当前所选诊所隔离。
     *
     * @param clinicId 诊所ID（前端传入将被忽略，以当前所选诊所为准）
     * @return 统一响应，data 为 {@link BizPatientSource} 树形列表
     */
    @ApiOperation("患者来源树")
    @GetMapping("/source/tree")
    public R<List<BizPatientSource>> sourceTree(
            @ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId) {
        return R.ok(basicDataService.selectSourceTree(clinicId));
    }

    /**
     * 新增或修改患者来源；按当前所选诊所隔离。
     *
     * @param source 来源信息（新增时 id 为空，修改时须含 id）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("保存患者来源")
    @PostMapping("/source")
    public R<?> saveSource(@RequestBody BizPatientSource source) {
        return basicDataService.saveSource(source) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除指定患者来源；按当前所选诊所隔离。
     *
     * @param id 来源ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("删除患者来源")
    @PostMapping("/source/remove/{id}")
    public R<?> removeSource(@ApiParam(value = "来源ID", required = true) @PathVariable Long id) {
        return basicDataService.deleteSource(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询授权诊所下诊疗项目列表（含 duration，空则默认 30）；支持名称/编码搜索。
     *
     * @param clinicId 授权诊所ID，空=会话当前诊所
     * @param keyword  项目名称/编码关键字，可选
     * @return 统一响应，data 为 {@link BizTreatItem} 列表
     */
    @ApiOperation("诊疗项目列表")
    @GetMapping("/item/list")
    public R<List<BizTreatItem>> itemList(
            @ApiParam("诊所ID（授权范围内生效）") @RequestParam(required = false) Long clinicId,
            @ApiParam("项目名称/编码关键字") @RequestParam(required = false) String keyword) {
        return R.ok(basicDataService.selectTreatItemList(clinicId, keyword));
    }

    /**
     * 新增或修改诊疗项目；按当前所选诊所隔离。
     *
     * @param item 诊疗项目信息（新增时 id 为空，修改时须含 id）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("保存诊疗项目")
    @PostMapping("/item")
    public R<?> saveItem(@RequestBody BizTreatItem item) {
        return basicDataService.saveTreatItem(item) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除指定诊疗项目；按当前所选诊所隔离。
     *
     * @param id 项目ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("删除诊疗项目")
    @PostMapping("/item/remove/{id}")
    public R<?> removeItem(@ApiParam(value = "项目ID", required = true) @PathVariable Long id) {
        return basicDataService.deleteTreatItem(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询当前账号授权诊所下的医生列表（可传 clinicId / keyword）；写操作诊所仍用会话诊所。
     *
     * @param clinicId 授权诊所ID，空=会话当前诊所
     * @param keyword  姓名/手机号模糊，可选
     * @return 统一响应，data 为医生列表（id/name/empNo/position/clinicId/mobile）
     */
    @ApiOperation("医生列表")
    @GetMapping("/doctor/list")
    public R<List<?>> doctorList(
            @ApiParam("诊所ID（授权范围内生效）") @RequestParam(required = false) Long clinicId,
            @ApiParam("姓名/手机号关键字") @RequestParam(required = false) String keyword) {
        return R.ok(basicDataService.selectDoctorList(clinicId, keyword));
    }

    /**
     * 查询当前账号授权诊所下的咨询师列表（可传 clinicId / keyword）。
     *
     * @param clinicId 授权诊所ID，空=会话当前诊所
     * @param keyword  姓名/手机号模糊，可选
     * @return 统一响应，data 为咨询师列表（id/name/empNo/position/clinicId/mobile）
     */
    @ApiOperation("咨询师列表")
    @GetMapping("/consultant/list")
    public R<List<?>> consultantList(
            @ApiParam("诊所ID（授权范围内生效）") @RequestParam(required = false) Long clinicId,
            @ApiParam("姓名/手机号关键字") @RequestParam(required = false) String keyword) {
        return R.ok(basicDataService.selectConsultantList(clinicId, keyword));
    }
}
