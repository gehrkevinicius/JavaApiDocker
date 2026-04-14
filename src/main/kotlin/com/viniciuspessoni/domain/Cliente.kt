package com.viniciuspessoni.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "clientes")
class Cliente(
    var nome: String = "",
    var idade: Int = 0,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,
    var risco: Int = 0
) {

    fun calcularRisco(): Int {
        risco = 110 - idade * 5
        return risco
    }

    override fun toString(): String {
        return "{ NOME: $nome, IDADE: $idade, ID: $id }"
    }
}