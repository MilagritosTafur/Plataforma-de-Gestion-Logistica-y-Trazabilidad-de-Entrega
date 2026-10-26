package com.example.demo.Repository;

import com.example.demo.Entity.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
//extienden de JpaRepository. 
//Esto actúa como el antiguo DAO y se encarga de traducir métodos como .save() o .findById() a consultas SQL automáticamente.
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> { }
