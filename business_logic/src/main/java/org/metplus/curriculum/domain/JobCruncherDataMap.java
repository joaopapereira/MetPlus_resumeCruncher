package org.metplus.curriculum.domain;

import java.util.Map;

public class JobCruncherDataMap {
    public Map<String, JobCruncherDataField> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, JobCruncherDataField> metaData) {
        this.metaData = metaData;
    }

    Map<String, JobCruncherDataField> metaData;
}
