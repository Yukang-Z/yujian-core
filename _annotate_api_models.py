# -*- coding: utf-8 -*-
"""Add @ApiModel / @ApiModelProperty to Java domain classes that already have field JavaDoc."""
import os
import re

ROOTS = [
    r"d:\Yujian\yujian-core\yujian-common\src\main\java\com\yujian\common\core\domain",
    r"d:\Yujian\yujian-core\yujian-common\src\main\java\com\yujian\common\system\domain",
    r"d:\Yujian\yujian-core\yujian-common\src\main\java\com\yujian\common\biz\domain",
]

SKIP_FILES = {"LoginUser.java"}  # still annotate, actually include all

CLASS_API_MODEL = {
    "R.java": "统一响应",
    "PageResult.java": "分页结果",
    "BaseEntity.java": "公共审计字段",
    "LoginUser.java": "登录用户上下文",
    "SysClinic.java": "诊所",
    "SysDept.java": "部门",
    "SysEmployee.java": "员工",
    "SysRole.java": "角色",
    "SysMenu.java": "菜单权限",
    "SysEmployeeClinic.java": "员工诊所关联",
    "SysEmployeeRole.java": "员工角色关联",
    "SysRoleMenu.java": "角色菜单关联",
    "BizPatient.java": "患者",
    "BizAppointment.java": "预约",
    "BizAppointmentLog.java": "预约操作日志",
    "BizSchedule.java": "员工日程",
    "BizVisit.java": "就诊",
    "BizMedicalRecord.java": "电子病历",
    "BizTreatmentRecord.java": "处置记录",
    "BizChargeRecord.java": "收费记录",
    "BizFollowUp.java": "回访",
    "BizPatientFile.java": "患者附件",
    "BizTreatPlan.java": "治疗计划",
    "BizConsultRecord.java": "咨询沟通",
    "BizPatientRelation.java": "亲友关系",
    "BizPatientLog.java": "患者操作日志",
    "BizPatientTag.java": "患者标签",
    "BizPatientTagRel.java": "患者标签关联",
    "BizPatientSource.java": "患者来源",
    "BizTreatItem.java": "诊疗项目",
    "BizDictType.java": "字典类型",
    "BizDictData.java": "字典数据",
}

IMPORT = "import io.swagger.annotations.ApiModel;\nimport io.swagger.annotations.ApiModelProperty;\n"

field_block = re.compile(
    r"(?P<doc>[ \t]*/\*\*[^*]*\*+(?:[^/*][^*]*\*+)*/)\s*"
    r"(?P<ann>(?:[ \t]*@(?!ApiModelProperty)[\w.(,\s=\"')]+\s*)*)"
    r"(?P<field>[ \t]*(?:private|protected|public)\s+)",
    re.S,
)


def first_javadoc_line(doc):
    lines = []
    for line in doc.splitlines():
        s = line.strip()
        s = s.replace("/**", "").replace("*/", "").lstrip("*").strip()
        if s and not s.startswith("@") and s != "<p>" and s != "</p>":
            lines.append(s)
    if not lines:
        return ""
    return lines[0].replace('"', "'")


def process(path):
    name = os.path.basename(path)
    if name not in CLASS_API_MODEL:
        return False
    with open(path, "r", encoding="utf-8") as f:
        text = f.read()
    if "@ApiModelProperty" in text and "@ApiModel(" in text:
        return False
    original = text
    model = CLASS_API_MODEL[name]

    if "import io.swagger.annotations.ApiModel;" not in text:
        # insert after last import
        imports = list(re.finditer(r"^import .+;\n", text, re.M))
        if imports:
            last = imports[-1].end()
            text = text[:last] + IMPORT + text[last:]
        else:
            text = text.replace("package ", "package ", 1)

    # add @ApiModel before class
    if "@ApiModel(" not in text:
        text = re.sub(
            r"(public class \w+)",
            "@ApiModel(\"" + model + "\")\npublic class " + r"\1".replace("public class ", ""),
            text,
            count=1,
        )
        # the replace above is messy; do cleaner
        text = original
        if "import io.swagger.annotations.ApiModel;" not in text:
            imports = list(re.finditer(r"^import .+;\n", text, re.M))
            last = imports[-1].end() if imports else 0
            if last:
                text = text[:last] + IMPORT + text[last:]
        text = re.sub(
            r"(\n)(public class )",
            r"\n@ApiModel(\"" + model + r"\")\n\2",
            text,
            count=1,
        )

    def repl(m):
        doc = m.group("doc")
        ann = m.group("ann") or ""
        field = m.group("field")
        if "@ApiModelProperty" in ann:
            return m.group(0)
        desc = first_javadoc_line(doc)
        if not desc:
            return m.group(0)
        indent = re.match(r"[ \t]*", doc).group(0)
        return "%s\n%s@ApiModelProperty(\"%s\")\n%s%s" % (doc, indent, desc, ann, field)

    text = field_block.sub(repl, text)

    if text != original:
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            f.write(text)
        return True
    return False


changed = []
for root in ROOTS:
    for fn in os.listdir(root):
        if fn.endswith(".java"):
            p = os.path.join(root, fn)
            if process(p):
                changed.append(fn)
print("updated:", ", ".join(changed) if changed else "(none)")
