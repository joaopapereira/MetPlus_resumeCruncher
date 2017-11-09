package org.metplus.curriculum.database.repository;

import org.metplus.curriculum.database.domain.JobMongo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface of the repository that will handle the CRUD operations
 * on Jobs
 */
@Repository
public interface JobDocumentRepository extends CrudRepository<JobMongo, String> {
    JobMongo findByJobId(String jobId);
}
