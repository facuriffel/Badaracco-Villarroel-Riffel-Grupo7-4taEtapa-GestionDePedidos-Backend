package com.trabajopp1.backendpp1.repository;

import com.trabajopp1.backendpp1.entity.MenuDia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MenuDiaRepository extends JpaRepository<MenuDia, Integer> {
    
    
}
