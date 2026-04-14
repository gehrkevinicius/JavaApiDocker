package com.viniciuspessoni.repository

import com.viniciuspessoni.domain.Cliente
import org.springframework.data.jpa.repository.JpaRepository

interface ClienteRepository : JpaRepository<Cliente, Int>
