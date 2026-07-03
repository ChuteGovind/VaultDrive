package com.cfs.bms.repository;

import com.cfs.bms.entity.FileCategory;
import com.cfs.bms.entity.StoredFile;
import com.cfs.bms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StoredFileRepository extends JpaRepository<StoredFile, Long>, JpaSpecificationExecutor<StoredFile> {
    
    List<StoredFile> findByOwnerOrderByUploadedAtDesc(User owner);
    
    List<StoredFile> findByOwnerAndCategoryOrderByUploadedAtDesc(User owner, FileCategory category);
    
    List<StoredFile> findTop5ByOwnerOrderByUploadedAtDesc(User owner);
    
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM StoredFile f WHERE f.owner = :owner")
    Long sumFileSizeByOwner(@Param("owner") User owner);
    
    @Query("SELECT f.category, COUNT(f), COALESCE(SUM(f.fileSize), 0) FROM StoredFile f WHERE f.owner = :owner GROUP BY f.category")
    List<Object[]> getStatsByOwner(@Param("owner") User owner);
}
