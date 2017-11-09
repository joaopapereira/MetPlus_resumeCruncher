package org.metplus.curriculum.domain;

import java.util.Map;

public class JobCruncherData {
    public Map<String, JobCruncherDataMap> getCruncherData() {
        return cruncherData;
    }

    public void setCruncherData(Map<String, JobCruncherDataMap> cruncherData) {
        this.cruncherData = cruncherData;
    }

    Map<String, JobCruncherDataMap> cruncherData;
}
