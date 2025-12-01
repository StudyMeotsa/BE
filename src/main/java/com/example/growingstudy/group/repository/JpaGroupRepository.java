package com.example.growingstudy.group.repository;

import jakarta.persistence.EntityManager;
import com.example.growingstudy.group.entity.Group;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaGroupRepository {

    private final EntityManager em;

    public JpaGroupRepository(EntityManager em) {
        this.em = em;
    }

    public Group save(Group group) {
        em.persist(group);
        return group;
    }

    public Optional<Group> findById(Long id) {
        Group group = em.find(Group.class, id);
        return Optional.ofNullable(group);
    }

    public List<Group> findAll() {
        return em.createQuery("select g from Group g", Group.class).getResultList();
    }

    public Optional<Group> findByName(String name) {
        List<Group> result = em.createQuery("select g from Group g where g.name = :name", Group.class)
                .setParameter("name", name)
                .getResultList();
        return result.stream().findAny();
    }

    public Optional<Group> findByCode(String code) {
        Group group = em.find(Group.class, code);
        return Optional.ofNullable(group);
    }

}
