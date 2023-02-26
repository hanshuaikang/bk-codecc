/*
 * Generated by code generator, see module: 'common-code-generator' for more details.
 */

package com.tencent.bk.codecc.defect.model.defect.code.gen;

import com.tencent.bk.codecc.defect.model.defect.CommonDefectEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@EqualsAndHashCode(callSuper = true)
public class CommonDefectEntityTracking extends CommonDefectEntity {

    private static final int NUMBER_OF_FIELDS = 39;
    private HashMap<String, Object> changesMap = null;


    @Override
    public void setAuthorList(java.util.Set val) {
        if (!Objects.equals(getAuthorList(), val)) {
            mapEntityChanges("author_list", val);
        }

        super.setAuthorList(val);
    }


    @Override
    public void setChecker(java.lang.String val) {
        if (!Objects.equals(getChecker(), val)) {
            mapEntityChanges("checker_name", val);
        }

        super.setChecker(val);
    }


    @Override
    public void setCreateBuildNumber(java.lang.String val) {
        if (!Objects.equals(getCreateBuildNumber(), val)) {
            mapEntityChanges("create_build_number", val);
        }

        super.setCreateBuildNumber(val);
    }


    @Override
    public void setCreateTime(long val) {
        if (!Objects.equals(getCreateTime(), val)) {
            mapEntityChanges("create_time", val);
        }

        super.setCreateTime(val);
    }


    @Override
    public void setCreatedBy(java.lang.String val) {
        if (!Objects.equals(getCreatedBy(), val)) {
            mapEntityChanges("created_by", val);
        }

        super.setCreatedBy(val);
    }


    @Override
    public void setCreatedDate(java.lang.Long val) {
        if (!Objects.equals(getCreatedDate(), val)) {
            mapEntityChanges("create_date", val);
        }

        super.setCreatedDate(val);
    }


    @Override
    public void setDefectInstances(java.util.List val) {
        if (!Objects.equals(getDefectInstances(), val)) {
            mapEntityChanges("defect_instances", val);
        }

        super.setDefectInstances(val);
    }


    @Override
    public void setDisplayCategory(java.lang.String val) {
        if (!Objects.equals(getDisplayCategory(), val)) {
            mapEntityChanges("display_category", val);
        }

        super.setDisplayCategory(val);
    }


    @Override
    public void setDisplayType(java.lang.String val) {
        if (!Objects.equals(getDisplayType(), val)) {
            mapEntityChanges("display_type", val);
        }

        super.setDisplayType(val);
    }


    @Override
    public void setExcludeTime(long val) {
        if (!Objects.equals(getExcludeTime(), val)) {
            mapEntityChanges("exclude_time", val);
        }

        super.setExcludeTime(val);
    }


    @Override
    public void setExtBugid(java.lang.String val) {
        if (!Objects.equals(getExtBugid(), val)) {
            mapEntityChanges("ext_bug_id", val);
        }

        super.setExtBugid(val);
    }


    @Override
    public void setFileMd5(java.lang.String val) {
        if (!Objects.equals(getFileMd5(), val)) {
            mapEntityChanges("file_md5", val);
        }

        super.setFileMd5(val);
    }


    @Override
    public void setFileName(java.lang.String val) {
        if (!Objects.equals(getFileName(), val)) {
            mapEntityChanges("file_name", val);
        }

        super.setFileName(val);
    }


    @Override
    public void setFilePath(java.lang.String val) {
        if (!Objects.equals(getFilePath(), val)) {
            mapEntityChanges("file_path_name", val);
        }

        super.setFilePath(val);
    }


    @Override
    public void setFileVersion(java.lang.String val) {
        if (!Objects.equals(getFileVersion(), val)) {
            mapEntityChanges("file_version", val);
        }

        super.setFileVersion(val);
    }


    @Override
    public void setFixedBuildNumber(java.lang.String val) {
        if (!Objects.equals(getFixedBuildNumber(), val)) {
            mapEntityChanges("fixed_build_number", val);
        }

        super.setFixedBuildNumber(val);
    }


    @Override
    public void setFixedTime(long val) {
        if (!Objects.equals(getFixedTime(), val)) {
            mapEntityChanges("fixed_time", val);
        }

        super.setFixedTime(val);
    }


    @Override
    public void setId(java.lang.String val) {
        if (!Objects.equals(getId(), val)) {
            mapEntityChanges("id", val);
        }

        super.setId(val);
    }


    @Override
    public void setIgnoreAuthor(java.lang.String val) {
        if (!Objects.equals(getIgnoreAuthor(), val)) {
            mapEntityChanges("ignore_author", val);
        }

        super.setIgnoreAuthor(val);
    }


    @Override
    public void setIgnoreReason(java.lang.String val) {
        if (!Objects.equals(getIgnoreReason(), val)) {
            mapEntityChanges("ignore_reason", val);
        }

        super.setIgnoreReason(val);
    }


    @Override
    public void setIgnoreReasonType(int val) {
        if (!Objects.equals(getIgnoreReasonType(), val)) {
            mapEntityChanges("ignore_reason_type", val);
        }

        super.setIgnoreReasonType(val);
    }


    @Override
    public void setIgnoreTime(long val) {
        if (!Objects.equals(getIgnoreTime(), val)) {
            mapEntityChanges("ignore_time", val);
        }

        super.setIgnoreTime(val);
    }


    @Override
    public void setLineNum(int val) {
        if (!Objects.equals(getLineNum(), val)) {
            mapEntityChanges("line_number", val);
        }

        super.setLineNum(val);
    }


    @Override
    public void setMark(java.lang.Integer val) {
        if (!Objects.equals(getMark(), val)) {
            mapEntityChanges("mark", val);
        }

        super.setMark(val);
    }


    @Override
    public void setMarkButNoFixed(java.lang.Boolean val) {
        if (!Objects.equals(getMarkButNoFixed(), val)) {
            mapEntityChanges("mark_but_no_fixed", val);
        }

        super.setMarkButNoFixed(val);
    }


    @Override
    public void setMarkTime(java.lang.Long val) {
        if (!Objects.equals(getMarkTime(), val)) {
            mapEntityChanges("mark_time", val);
        }

        super.setMarkTime(val);
    }


    @Override
    public void setMaskPath(java.lang.String val) {
        if (!Objects.equals(getMaskPath(), val)) {
            mapEntityChanges("mask_path", val);
        }

        super.setMaskPath(val);
    }


    @Override
    public void setMessage(java.lang.String val) {
        if (!Objects.equals(getMessage(), val)) {
            mapEntityChanges("message", val);
        }

        super.setMessage(val);
    }


    @Override
    public void setPlatformBuildId(java.lang.String val) {
        if (!Objects.equals(getPlatformBuildId(), val)) {
            mapEntityChanges("platform_build_id", val);
        }

        super.setPlatformBuildId(val);
    }


    @Override
    public void setPlatformProjectId(java.lang.String val) {
        if (!Objects.equals(getPlatformProjectId(), val)) {
            mapEntityChanges("platform_project_id", val);
        }

        super.setPlatformProjectId(val);
    }


    @Override
    public void setRelPath(java.lang.String val) {
        if (!Objects.equals(getRelPath(), val)) {
            mapEntityChanges("rel_path", val);
        }

        super.setRelPath(val);
    }


    @Override
    public void setRevision(java.lang.String val) {
        if (!Objects.equals(getRevision(), val)) {
            mapEntityChanges("revision", val);
        }

        super.setRevision(val);
    }


    @Override
    public void setSeverity(int val) {
        if (!Objects.equals(getSeverity(), val)) {
            mapEntityChanges("severity", val);
        }

        super.setSeverity(val);
    }


    @Override
    public void setStatus(java.lang.Integer val) {
        if (!Objects.equals(getStatus(), val)) {
            mapEntityChanges("status", val);
        }

        super.setStatus(val);
    }


    @Override
    public void setStreamName(java.lang.String val) {
        if (!Objects.equals(getStreamName(), val)) {
            mapEntityChanges("stream_name", val);
        }

        super.setStreamName(val);
    }


    @Override
    public void setTaskId(long val) {
        if (!Objects.equals(getTaskId(), val)) {
            mapEntityChanges("task_id", val);
        }

        super.setTaskId(val);
    }


    @Override
    public void setToolName(java.lang.String val) {
        if (!Objects.equals(getToolName(), val)) {
            mapEntityChanges("tool_name", val);
        }

        super.setToolName(val);
    }


    @Override
    public void setUpdatedBy(java.lang.String val) {
        if (!Objects.equals(getUpdatedBy(), val)) {
            mapEntityChanges("updated_by", val);
        }

        super.setUpdatedBy(val);
    }


    @Override
    public void setUpdatedDate(java.lang.Long val) {
        if (!Objects.equals(getUpdatedDate(), val)) {
            mapEntityChanges("updated_date", val);
        }

        super.setUpdatedDate(val);
    }


    private void mapEntityChanges(String fieldName, Object fieldVal) {
        if (getEntityId() == null || getEntityId().trim().isEmpty()) {
            return;
        }

        if (changesMap == null) {
            changesMap = new HashMap<>(NUMBER_OF_FIELDS + 1, 1.0f);
        }

        changesMap.put(fieldName, fieldVal);
    }

    @Transient
    public Map<String, Object> getChangedMap() {
        return changesMap;
    }

    public static class ListContainer extends ArrayList<CommonDefectEntityTracking> {

        /**
         * 保存变更
         *
         * @mongoTemplate db provider
         */
        public void saveChanges(MongoTemplate mongoTemplate) {
            BulkOperations ops = mongoTemplate.bulkOps(BulkMode.UNORDERED, CommonDefectEntity.class);
            final boolean[] needExecute = {false};

            forEach(entity -> {
                boolean setOk = setUpdateInfo(entity, ops);
                if (setOk && !needExecute[0]) {
                    needExecute[0] = true;
                }
            });

            if (needExecute[0]) {
                ops.execute();
            }
        }

        /**
         * 分批保存变更
         *
         * @param mongoTemplate db provider
         * @param batchSize 每一批大小
         */
        public void saveChanges(MongoTemplate mongoTemplate, int batchSize) {
            if (size() <= batchSize) {
                saveChanges(mongoTemplate);
                return;
            }

            int counter = 0;
            boolean needExecute = false;
            Iterator<CommonDefectEntityTracking> iterator = iterator();
            BulkOperations ops = mongoTemplate.bulkOps(BulkMode.UNORDERED, CommonDefectEntity.class);

            while (iterator.hasNext()) {
                counter++;
                CommonDefectEntityTracking entity = iterator.next();

                boolean setOk = setUpdateInfo(entity, ops);
                if (setOk && !needExecute) {
                    needExecute = true;
                }

                if ((counter % batchSize == 0 || size() == counter) && needExecute) {
                    ops.execute();
                    needExecute = false;
                    ops = mongoTemplate.bulkOps(BulkMode.UNORDERED, CommonDefectEntity.class);
                }
            }
        }

        private boolean setUpdateInfo(CommonDefectEntityTracking entity, BulkOperations ops) {
            if (entity.getChangedMap() == null || entity.getChangedMap().isEmpty()) {
                return false;
            }

            Query query = Query.query(Criteria.where("_id").is(entity.getEntityId()));
            Update update = new Update();
            entity.getChangedMap().forEach((key, value) -> {
                if (value != null) {
                    update.set(key, value);
                } else {
                    update.unset(key);
                }
            });

            ops.updateOne(query, update);

            return true;
        }
    }
}
