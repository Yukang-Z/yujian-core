package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 患者影像/文档附件实体，对应表 t_patient_file
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@TableName("t_patient_file")
public class BizPatientFile implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 诊所ID */
    private Long clinicId;

    /** 患者ID */
    private Long patientId;

    /** 关联就诊ID */
    private Long visitId;

    /** 文件分类：image图片 / document文档 / agreement协议 */
    private String fileCategory;

    /** 文件类型（扩展名或MIME） */
    private String fileType;

    /** 文件名称 */
    private String fileName;

    /** 文件访问地址 */
    private String fileUrl;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date uploadTime;

    /** 创建人 */
    private Long createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /** 备注 */
    private String remark;

    /** 逻辑删除标记：0正常 1已删除 */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
