package com.cfs.BMS.repository;

import com.cfs.BMS.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TheaterRepository extends JpaRepository<Theater,Long> {

    List<Theater> findByCity_id(Long cityId);
}
