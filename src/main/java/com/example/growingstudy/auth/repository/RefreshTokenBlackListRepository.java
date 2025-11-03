package com.example.growingstudy.auth.repository;

import com.example.growingstudy.auth.entity.RefreshTokenBlackList;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenBlackListRepository extends CrudRepository<RefreshTokenBlackList, String> {
}
