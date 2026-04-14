package com.viniciuspessoni.controller

import com.viniciuspessoni.domain.Cliente
import com.viniciuspessoni.repository.ClienteRepository
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class ClienteController(private val clienteRepository: ClienteRepository) {

    @GetMapping("clientes", "/")
    fun getTodosClientes(): ResponseEntity<Map<Int, Cliente>> {
        System.out.println("PEGA TODOS CLIENTES")
        val mapa = clienteRepository.findAll().associateBy { it.id }
        return status(OK).body(mapa)
    }

    @GetMapping("cliente/{id}")
    fun getClientePorId(@PathVariable @Valid id: Int): ResponseEntity<Any> {
        return clienteRepository.findById(id)
            .map { cliente ->
                System.out.println("PEGA CLIENTE COM ID: $cliente")
                status(OK).body(cliente as Any)
            }
            .orElseGet {
                System.out.println("CLIENTE NÃO ENCONTRADO: $id")
                status(NOT_FOUND).body("Cliente não encontrado" as Any)
            }
    }

    @GetMapping("risco/{id}")
    fun getRiscoPorId(@PathVariable @Valid id: Int): ResponseEntity<Any> {
        return clienteRepository.findById(id)
            .map { cliente ->
                cliente.calcularRisco()
                val salvo = clienteRepository.save(cliente)
                System.out.println("PEGA RISCO DO CLIENTE PELO ID: $salvo")
                status(OK).body(salvo as Any)
            }
            .orElseGet {
                System.out.println("CLIENTE NÃO ENCONTRADO: $id")
                status(NOT_FOUND).body("Cliente não encontrado" as Any)
            }
    }

    @PostMapping(path = ["/cliente"], consumes = ["application/json"])
    fun cadastraCliente(@RequestBody @Valid cliente: Cliente): ResponseEntity<Map<Int, Cliente>> {
        val novo = Cliente(nome = cliente.nome, idade = cliente.idade, id = 0, risco = cliente.risco)
        val salvo = clienteRepository.save(novo)
        System.out.println("CLIENTE ADD: $salvo")
        val mapa = clienteRepository.findAll().associateBy { it.id }
        return status(CREATED).body(mapa)
    }

    @PutMapping(path = ["cliente"], consumes = ["application/json"])
    fun atualizaCliente(@RequestBody cliente: Cliente): ResponseEntity<Any> {
        if (!clienteRepository.existsById(cliente.id)) {
            System.out.println("CLIENTE NÃO ENCONTRADO: $cliente")
            return status(NOT_FOUND).body("Cliente não encontrado")
        }
        clienteRepository.save(cliente)
        System.out.println("CLIENTE ATUALIZADO: $cliente")
        val mapa = clienteRepository.findAll().associateBy { it.id }
        return status(OK).body(mapa)
    }

    @DeleteMapping("cliente/{id}")
    fun deletaCliente(@PathVariable @Valid id: Int): ResponseEntity<String> {
        return clienteRepository.findById(id)
            .map { cliente ->
                clienteRepository.deleteById(id)
                System.out.println("CLIENTE REMOVIDO: $cliente")
                status(OK).body("CLIENTE REMOVIDO: $cliente")
            }
            .orElseGet {
                System.out.println("CLIENTE NÃO ENCONTRADO: $id")
                status(NOT_FOUND).body("Cliente não encontrado")
            }
    }

    /**
     * Esse endpoint foi criado para ajudar a realizar os testes facilmente.
     * Com ele, podemos remover todos os clientes de uma vez.
     */
    @DeleteMapping("cliente/apagaTodos")
    fun deletaTodosClientes(): String {
        clienteRepository.deleteAll()
        System.out.println("TODOS CLIENTES REMOVIDOS")
        return clienteRepository.findAll().toString()
    }
}
