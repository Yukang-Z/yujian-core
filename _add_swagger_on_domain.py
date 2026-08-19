# -*- coding: utf-8 -*-
"""Safely add @ApiModel / @ApiModelProperty to domain Java classes. Instance fields only."""
from __future__ import print_function

import os
import re

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                    "yujian-common", "src", "main", "java", "com", "yujian", "common")

CLASS_LINE = re.compile(r"^public class \w+")


def is_instance_field(line):
    s = line.strip()
    if not s.startswith("private "):
        return False
    if s.startswith("private static"):
        return False
    if not s.endswith(";"):
        return False
    if "(" in s:
        eq = s.find("=")
        paren = s.find("(")
        if eq < 0 or paren < eq:
            return False
    return True


def javadoc_text(block_lines):
    parts = []
    for raw in block_lines:
        t = raw.strip()
        t = t.replace("/**", "").replace("*/", "")
        if t.startswith("*"):
            t = t[1:]
        t = t.strip()
        if not t:
            continue
        if t.startswith("@author") or t.startswith("@date") or t.startswith("@param"):
            continue
        if t in ("<p>", "</p>"):
            continue
        t = t.replace("<p>", "").replace("</p>", "").strip()
        if t:
            parts.append(t)
    return " ".join(parts)


def find_field_javadoc(lines, field_idx):
    i = field_idx - 1
    while i >= 0:
        s = lines[i].strip()
        if s.startswith("@") or s == "":
            i -= 1
            continue
        break
    if i < 0:
        return None, None
    s = lines[i].strip()
    if not s.endswith("*/"):
        return None, None
    end = i
    start = i
    while start >= 0 and not lines[start].strip().startswith("/**"):
        start -= 1
    if start < 0:
        return None, None
    text = javadoc_text(lines[start:end + 1])
    return text, end


def find_class_javadoc(lines, class_idx):
    j = class_idx - 1
    while j >= 0:
        s = lines[j].strip()
        if s.startswith("@") or s == "":
            j -= 1
            continue
        break
    if j < 0 or not lines[j].strip().endswith("*/"):
        return None
    end = j
    start = j
    while start >= 0 and not lines[start].strip().startswith("/**"):
        start -= 1
    if start < 0:
        return None
    return javadoc_text(lines[start:end + 1])


def class_title(doc, class_name):
    if not doc:
        return class_name
    first = doc.split(" ")[0] if False else doc
    first = first.strip()
    if "实体" in first:
        first = first.split("实体")[0].strip()
    if "对应表" in first:
        first = first.split("对应表")[0].strip(" ，,")
    if "（" in first:
        first = first.split("（")[0].strip()
    if "(" in first:
        first = first.split("(")[0].strip()
    return first[:40] if first else class_name


def detect_newline(raw):
    if b"\r\n" in raw:
        return "\r\n"
    return "\n"


def process_file(path):
    raw = open(path, "rb").read()
    newline = detect_newline(raw)
    text = raw.decode("utf-8")
    if text.startswith(u"\ufeff"):
        text = text[1:]
    lines = text.splitlines()

    class_idx = None
    class_name = None
    for i, ln in enumerate(lines):
        m = re.search(r"public class (\w+)", ln)
        if m:
            class_idx = i
            class_name = m.group(1)
            break
    if class_idx is None:
        return False, "no class"

    insertions = []  # (index, line) insert BEFORE index, apply from bottom

    # instance fields
    for i, ln in enumerate(lines):
        if i <= class_idx:
            continue
        if not is_instance_field(ln):
            continue
        nearby = "\n".join(lines[max(0, i - 10):i])
        if "@ApiModelProperty" in nearby:
            continue
        indent = re.match(r"^(\s*)", ln).group(1)
        doc, javadoc_end = find_field_javadoc(lines, i)
        if not doc:
            continue
        doc = doc.replace("\\", "\\\\").replace('"', '\\"')
        insertions.append((javadoc_end + 1, indent + '@ApiModelProperty("' + doc + '")'))

    # @ApiModel before public class
    if not any("@ApiModel(" in ln for ln in lines):
        doc = find_class_javadoc(lines, class_idx)
        title = class_title(doc, class_name)
        title = title.replace("\\", "\\\\").replace('"', '\\"')
        insertions.append((class_idx, '@ApiModel("' + title + '")'))

    # imports after last import
    need_model = "import io.swagger.annotations.ApiModel;" not in text
    need_prop = "import io.swagger.annotations.ApiModelProperty;" not in text
    if need_model or need_prop:
        last_import = -1
        for i, ln in enumerate(lines):
            if ln.startswith("import "):
                last_import = i
        if last_import >= 0:
            # same index twice: reverse apply puts first-appended line first
            if need_model:
                insertions.append((last_import + 1, "import io.swagger.annotations.ApiModel;"))
            if need_prop:
                insertions.append((last_import + 1, "import io.swagger.annotations.ApiModelProperty;"))

    if not insertions:
        return False, "nothing"

    insertions.sort(key=lambda x: x[0], reverse=True)
    for idx, line in insertions:
        lines.insert(idx, line)

    out = newline.join(lines) + newline
    open(path, "wb").write(out.encode("utf-8"))
    return True, "ok"


def main():
    changed = 0
    for dirpath, _, files in os.walk(ROOT):
        if os.path.sep + "domain" not in dirpath and not dirpath.endswith("domain"):
            continue
        for name in files:
            if not name.endswith(".java"):
                continue
            path = os.path.join(dirpath, name)
            ok, msg = process_file(path)
            rel = os.path.relpath(path, ROOT)
            print(("%s\t%s" % (rel, msg)).encode("utf-8").decode("utf-8") if False else "%s\t%s" % (rel, msg))
            if ok:
                changed += 1
    print("changed=%d" % changed)


if __name__ == "__main__":
    main()
