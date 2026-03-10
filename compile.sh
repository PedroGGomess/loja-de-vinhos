#!/bin/bash
# compile.sh — Script para compilar e executar a Loja de Vinhos em Java

set -e

echo "🍷 Loja de Vinhos — Compilação e Execução"
echo "=========================================="

# Cria diretório de output
mkdir -p out

# Compila todos os ficheiros Java
echo "⚙  A compilar..."
javac -d out -sourcepath src $(find src -name "*.java")
echo "✅ Compilação concluída!"

# Executa a aplicação
echo "🚀 A iniciar a aplicação..."
echo ""
java -cp out main.Main
