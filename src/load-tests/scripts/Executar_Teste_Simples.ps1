# ===================================================================
# SCRIPT DE TESTE DE CARGA SIMPLIFICADO
# Foco em endpoints públicos, sem necessidade de login.
# ===================================================================

$OutputEncoding = [System.Text.Encoding]::UTF8
$corTitulo = "Green"

Clear-Host
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host "  Iniciando Teste de Carga Simplificado - Consulta de Praias" -ForegroundColor $corTitulo
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host ""

# --- Define os caminhos para os arquivos do teste ---
$projectRoot = Resolve-Path "$PSScriptRoot/.." 

$collectionFile = "$projectRoot/collections/Carga_Simples_Praias.postman_collection.json"
$dataFile = "$projectRoot/data/dados_praias.csv"
$environmentFile = "$projectRoot/flop_local.postman_environment.json"
$reportFile = "$projectRoot/relatorio_carga_simples.html"

# Executa o comando Newman com -n 100 para rodar 100 vezes
newman run "$collectionFile" `
    -e "$environmentFile" `
    -d "$dataFile" `
    -n 100 `
    --reporters "cli,html" `
    --reporter-html-export "$reportFile"

# Mensagem de conclusão
Write-Host ""
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host "  Teste de Carga Simplificado CONCLUIDO." -ForegroundColor $corTitulo
Write-Host "  Verifique o relatorio em: `"$reportFile`"" -ForegroundColor $corTitulo
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host ""

Read-Host "Pressione Enter para fechar esta janela..."