# ===================================================================
# SCRIPT PARA TESTE DE CARGA - CENÁRIO 1: CONSULTAR PRAIA
# ===================================================================

# FORÇA A CODIFICAÇÃO DE SAÍDA PARA UTF-8 PARA CORRIGIR ACENTOS
$OutputEncoding = [System.Text.Encoding]::UTF8

$corTitulo = "Green"
$corComando = "Cyan"
$corInfo = "Yellow"

Clear-Host
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host "  Iniciando Teste de Carga: Cenário 1 - Consultar Praia" -ForegroundColor $corTitulo
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host ""

# --- DEFINIÇÃO DE CAMINHOS DINÂMICOS ---
$projectRoot = Resolve-Path "$PSScriptRoot/.." 

$collectionFile = "$projectRoot/collections/Cenario1_ConsultarPraia.postman_collection.json"
$dataFile = "$projectRoot/data/dados_cenario1_consultar_praia.csv"
$environmentFile = "$projectRoot/flop_local.postman_environment.json"
$reportFile = "$projectRoot/relatorio_cenario1.html"

# Executa o comando Newman diretamente, usando o acento grave (`) para quebrar a linha
# Esta é a forma mais robusta e evita erros de interpretação do PowerShell.
newman run "$collectionFile" `
    -e "$environmentFile" `
    -d "$dataFile" `
    -n 20 `
    --reporters cli,html `
    --reporter-html-export "$reportFile"

# Mensagem de conclusão
Write-Host ""
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host "  Teste do Cenário 1 CONCLUÍDO." -ForegroundColor $corTitulo
Write-Host "  Verifique o relatório em: `"$reportFile`"" -ForegroundColor $corTitulo
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host ""

Read-Host "Pressione Enter para fechar esta janela..."