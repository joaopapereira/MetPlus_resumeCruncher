package org.metplus.curriculum.process;

import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.database.domain.Settings;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.database.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by joaopereira on 2/1/2016.
 */
public class CrunchResume {
    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private SettingsRepository repository;

    public void crunch() {
        for(Settings settings: repository.findAll()) {
            settings.
        }
    }

    public void crunch(Cruncher cruncher) {

    }
}
