#!/usr/bin/env python3
"""
Script de teste para a API BTG Challenge
Testa os endpoints da aplicação
"""

import requests
import json
import sys
from datetime import datetime

# Configuração
BASE_URL = "http://localhost:8080"
CUSTOMER_ID = 1

class Colors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKCYAN = '\033[96m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'

def print_header(text):
    print(f"\n{Colors.HEADER}{Colors.BOLD}{'='*60}")
    print(f"{text}")
    print(f"{'='*60}{Colors.ENDC}\n")

def print_success(text):
    print(f"{Colors.OKGREEN}✓ {text}{Colors.ENDC}")

def print_error(text):
    print(f"{Colors.FAIL}✗ {text}{Colors.ENDC}")

def print_info(text):
    print(f"{Colors.OKCYAN}→ {text}{Colors.ENDC}")

def test_health_check():
    """Testa o endpoint de health check"""
    print_header("1. HEALTH CHECK")

    try:
        response = requests.get(f"{BASE_URL}/health", timeout=5)

        if response.status_code == 200:
            print_success("Health Check respondeu com sucesso")
            print(f"Response: {json.dumps(response.json(), indent=2)}")
            return True
        else:
            print_error(f"Health Check retornou status {response.status_code}")
            return False
    except requests.exceptions.ConnectionError:
        print_error("Não foi possível conectar à API. Verifique se está rodando em http://localhost:8080")
        return False
    except Exception as e:
        print_error(f"Erro ao testar health check: {e}")
        return False

def test_customer_summary():
    """Testa o endpoint de resumo do cliente"""
    print_header("2. RESUMO DO CLIENTE")

    try:
        url = f"{BASE_URL}/customers/{CUSTOMER_ID}/summary"
        print_info(f"GET {url}")

        response = requests.get(url, timeout=5)

        if response.status_code == 200:
            data = response.json()
            print_success("Resumo obtido com sucesso")
            print(json.dumps(data, indent=2, default=str))
            return True
        else:
            print_error(f"Status {response.status_code}: {response.text}")
            return False
    except Exception as e:
        print_error(f"Erro ao obter resumo: {e}")
        return False

def test_orders_paginated():
    """Testa o endpoint de pedidos com paginação"""
    print_header("3. PEDIDOS COM PAGINAÇÃO")

    try:
        url = f"{BASE_URL}/customers/{CUSTOMER_ID}/orders?page=0&pageSize=10"
        print_info(f"GET {url}")

        response = requests.get(url, timeout=5)

        if response.status_code == 200:
            data = response.json()
            print_success("Pedidos obtidos com sucesso")

            if 'summary' in data:
                print(f"\n{Colors.BOLD}Resumo:{Colors.ENDC}")
                print(json.dumps(data['summary'], indent=2, default=str))

            if 'pagination' in data:
                print(f"\n{Colors.BOLD}Paginação:{Colors.ENDC}")
                print(json.dumps(data['pagination'], indent=2, default=str))

            if 'data' in data:
                print(f"\n{Colors.BOLD}Pedidos ({len(data['data'])}):{Colors.ENDC}")
                if len(data['data']) > 0:
                    print(json.dumps(data['data'][:2], indent=2, default=str))  # Mostra apenas os 2 primeiros
                    if len(data['data']) > 2:
                        print(f"... e mais {len(data['data']) - 2} pedidos")
                else:
                    print("Nenhum pedido encontrado")

            return True
        else:
            print_error(f"Status {response.status_code}: {response.text}")
            return False
    except Exception as e:
        print_error(f"Erro ao obter pedidos: {e}")
        return False

def test_all_orders():
    """Testa o endpoint de todos os pedidos"""
    print_header("4. TODOS OS PEDIDOS (SEM PAGINAÇÃO)")

    try:
        url = f"{BASE_URL}/customers/{CUSTOMER_ID}/all-orders"
        print_info(f"GET {url}")

        response = requests.get(url, timeout=5)

        if response.status_code == 200:
            data = response.json()
            print_success("Todos os pedidos obtidos com sucesso")

            print(f"\n{Colors.BOLD}Informações:{Colors.ENDC}")
            print(f"  - Customer ID: {data.get('customerId')}")
            print(f"  - Total de Pedidos: {data.get('quantity')}")
            print(f"  - Valor Total: R$ {data.get('totalValue')}")

            if 'orders' in data and len(data['orders']) > 0:
                print(f"\n{Colors.BOLD}Primeiros 2 Pedidos:{Colors.ENDC}")
                print(json.dumps(data['orders'][:2], indent=2, default=str))
                if len(data['orders']) > 2:
                    print(f"... e mais {len(data['orders']) - 2} pedidos")

            return True
        else:
            print_error(f"Status {response.status_code}: {response.text}")
            return False
    except Exception as e:
        print_error(f"Erro ao obter todos os pedidos: {e}")
        return False

def main():
    """Função principal"""
    print_header("TESTES DA API BTG CHALLENGE")
    print_info(f"Base URL: {BASE_URL}")
    print_info(f"Customer ID: {CUSTOMER_ID}")
    print_info(f"Data/Hora: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")

    results = []

    # Executa os testes
    results.append(("Health Check", test_health_check()))
    results.append(("Resumo do Cliente", test_customer_summary()))
    results.append(("Pedidos com Paginação", test_orders_paginated()))
    results.append(("Todos os Pedidos", test_all_orders()))

    # Resumo dos resultados
    print_header("RESUMO DOS TESTES")

    for test_name, result in results:
        if result:
            print_success(f"{test_name}")
        else:
            print_error(f"{test_name}")

    # Contagem
    total = len(results)
    passed = sum(1 for _, result in results if result)
    failed = total - passed

    print(f"\n{Colors.BOLD}Total: {total} | Sucesso: {passed} | Falha: {failed}{Colors.ENDC}")

    if failed == 0:
        print(f"\n{Colors.OKGREEN}{Colors.BOLD}✓ TODOS OS TESTES PASSARAM!{Colors.ENDC}\n")
        return 0
    else:
        print(f"\n{Colors.FAIL}{Colors.BOLD}✗ ALGUNS TESTES FALHARAM{Colors.ENDC}\n")
        return 1

if __name__ == "__main__":
    sys.exit(main())

