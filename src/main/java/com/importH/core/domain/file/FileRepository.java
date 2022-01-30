package com.importH.core.domain.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface FileRepository extends JpaRepository<File, Long> {

}
