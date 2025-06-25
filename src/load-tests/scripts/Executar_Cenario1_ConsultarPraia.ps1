# ===================================================================
# SCRIPT PARA TESTE DE CARGA - CENARIO 1: CONSULTAR PRAIA
# ===================================================================

$OutputEncoding = [System.Text.Encoding]::UTF8

$corTitulo = "Green"
$corInfo = "Yellow"

Clear-Host
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host "  Iniciando Teste de Carga: Cenario 1 - Consultar Praia" -ForegroundColor $corTitulo
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host ""

# --- DEFINICAO DE CAMINHOS DINAMICOS ---
$projectRoot = Resolve-Path "$PSScriptRoot/.."

$collectionFile = "$projectRoot/collections/Cenario1_ConsultarPraia.postman_collection.json"
$dataFile = "$projectRoot/data/dados_cenario1_consultar_praia.csv"
$environmentFile = "$projectRoot/flop_local.postman_environment.json"
$reportFile = "$projectRoot/relatorio_cenario1.html"

# Executa o comando Newman com o valor do reporters entre aspas
newman run "$collectionFile" `
    -e "$environmentFile" `
    -d "$dataFile" `
    -n 20 `
    --reporters "cli,html" `
    --reporter-html-export "$reportFile"

# Mensagem de conclusao
Write-Host ""
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host "  Teste do Cenario 1 CONCLUIDO." -ForegroundColor $corTitulo
Write-Host "  Verifique o relatorio em: `"$reportFile`"" -ForegroundColor $corTitulo
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host ""

Read-Host "Pressione Enter para fechar esta janela..."