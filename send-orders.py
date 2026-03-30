#!/usr/bin/env python3
"""
Script para enviar pedidos para a fila RabbitMQ
Usado para testar o listener de pedidos
"""

import pika
import json
import sys
import time
import random

# Configuração
RABBITMQ_HOST = "localhost"
RABBITMQ_PORT = 5672
RABBITMQ_USER = "admin"
RABBITMQ_PASS = "123456"
QUEUE_NAME = "btg-pactual-order-created"

class Colors:
    OKGREEN = '\033[92m'
    FAIL = '\033[91m'
    OKCYAN = '\033[96m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'

def print_success(text):
    print(f"{Colors.OKGREEN}✓ {text}{Colors.ENDC}")

def print_error(text):
    print(f"{Colors.FAIL}✗ {text}{Colors.ENDC}")

def print_info(text):
    print(f"{Colors.OKCYAN}→ {text}{Colors.ENDC}")

def create_order_message(order_id, customer_id):
    """Cria uma mensagem de pedido de exemplo"""
    return {
        "codigoPedido": order_id,
        "codigoCliente": customer_id,
        "itens": [
            {
                "produto": "lápis",
                "quantidade": 100,
                "preco": 1.10
            },
            {
                "produto": "caderno",
                "quantidade": 10,
                "preco": 1.00
            },
            {
                "produto": "borracha",
                "quantidade": 50,
                "preco": 0.50
            }
        ]
    }

def send_order(message):
    """Envia uma mensagem para a fila RabbitMQ"""
    try:
        # Conecta ao RabbitMQ
        credentials = pika.PlainCredentials(RABBITMQ_USER, RABBITMQ_PASS)
        connection = pika.BlockingConnection(
            pika.ConnectionParameters(
                host=RABBITMQ_HOST,
                port=RABBITMQ_PORT,
                credentials=credentials,
                connection_attempts=3,
                retry_delay=2
            )
        )
        channel = connection.channel()

        # Declara a fila
        channel.queue_declare(queue=QUEUE_NAME, durable=True)

        # Publica a mensagem
        channel.basic_publish(
            exchange='',
            routing_key=QUEUE_NAME,
            body=json.dumps(message),
            properties=pika.BasicProperties(
                delivery_mode=pika.DeliveryMode.Persistent
            )
        )

        connection.close()
        return True

    except pika.exceptions.AMQPConnectionError as e:
        print_error(f"Não foi possível conectar ao RabbitMQ: {e}")
        print_info(f"Verifique se RabbitMQ está rodando em {RABBITMQ_HOST}:{RABBITMQ_PORT}")
        return False
    except Exception as e:
        print_error(f"Erro ao enviar mensagem: {e}")
        return False

def main():
    """Função principal"""
    print(f"\n{Colors.BOLD}{'='*60}")
    print("ENVIAR PEDIDOS PARA FILA RABBITMQ")
    print(f"{'='*60}{Colors.ENDC}\n")

    print_info(f"RabbitMQ Host: {RABBITMQ_HOST}")
    print_info(f"RabbitMQ Port: {RABBITMQ_PORT}")
    print_info(f"Queue: {QUEUE_NAME}")

    # Menu de opções
    print(f"\n{Colors.BOLD}Escolha uma opção:{Colors.ENDC}")
    print("1 - Enviar 1 pedido")
    print("2 - Enviar 5 pedidos")
    print("3 - Enviar 10 pedidos")
    print("4 - Enviar pedido personalizado")
    print("0 - Sair")

    choice = input("\nOpção: ").strip()

    if choice == "1":
        messages = [create_order_message(1001, 1)]
    elif choice == "2":
        messages = [create_order_message(1001 + i, 1) for i in range(5)]
    elif choice == "3":
        messages = [create_order_message(1001 + i, 1) for i in range(10)]
    elif choice == "4":
        order_id = input("ID do Pedido (ex: 1001): ").strip()
        customer_id = input("ID do Cliente (ex: 1): ").strip()
        try:
            messages = [create_order_message(int(order_id), int(customer_id))]
        except ValueError:
            print_error("IDs devem ser números")
            return 1
    elif choice == "0":
        print("Saindo...")
        return 0
    else:
        print_error("Opção inválida")
        return 1

    # Envia as mensagens
    print(f"\n{Colors.BOLD}Enviando {len(messages)} pedido(s)...{Colors.ENDC}\n")

    success_count = 0
    for i, message in enumerate(messages, 1):
        print_info(f"Pedido {i}/{len(messages)}: codigoPedido={message['codigoPedido']}, codigoCliente={message['codigoCliente']}")

        if send_order(message):
            print_success(f"Pedido {message['codigoPedido']} enviado com sucesso")
            success_count += 1
        else:
            print_error(f"Erro ao enviar pedido {message['codigoPedido']}")

        time.sleep(0.5)  # Pequeno delay entre pedidos

    # Resumo
    print(f"\n{Colors.BOLD}{'='*60}")
    print(f"Resultado: {success_count}/{len(messages)} pedidos enviados com sucesso")
    print(f"{'='*60}{Colors.ENDC}\n")

    if success_count == len(messages):
        print_success("Todos os pedidos foram enviados!")
        return 0
    else:
        print_error(f"{len(messages) - success_count} pedido(s) falharam")
        return 1

if __name__ == "__main__":
    sys.exit(main())

