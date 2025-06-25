# ===================================================================
# SCRIPT PARA TESTE DE CARGA - CENÁRIO 1: CONSULTAR PRAIA
# ===================================================================

$corTitulo = "Green"
$corComando = "Cyan"
$corInfo = "Yellow"

Clear-Host
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host "  Iniciando Teste de Carga: Cenário 1 - Consultar Praia" -ForegroundColor $corTitulo
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host ""

# --- DEFINIÇÃO DE CAMINHOS DINÂMICOS ---
# $PSScriptRoot é o caminho para a pasta onde este script está
$projectRoot = Resolve-Path "$PSScriptRoot/.." # Caminho para a pasta raiz `load-tests/`

# Define os caminhos completos para cada arquivo
$collectionFile = "$projectRoot/collections/Cenario1_ConsultarPraia.postman_collection.json"
$dataFile = "$projectRoot/data/dados_cenario1_consultar_praia.csv"
$environmentFile = "$projectRoot/flop_local.postman_environment.json"
$reportFile = "$projectRoot/relatorio_cenario1.html"

# Monta o comando Newman em uma variável para clareza
$newmanCommand = "newman run `"$collectionFile`" `
    -e `"$environmentFile`" `
    -d `"$dataFile`" `
    -n 20 `
    --reporters cli,html `
    --reporter-html-export `"$reportFile`""

Write-Host "O script está na pasta: $PSScriptRoot" -ForegroundColor $corInfo
Write-Host "A raiz do projeto é: $projectRoot" -ForegroundColor $corInfo
Write-Host "Executando o comando:" -ForegroundColor $corInfo
Write-Host $newmanCommand -ForegroundColor $corComando
Write-Host ""

# Executa o comando Newman usando as variáveis
Invoke-Expression $newmanCommand

# Mensagem de conclusão
Write-Host ""
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host "  Teste do Cenário 1 CONCLUÍDO." -ForegroundColor $corTitulo
Write-Host "  Verifique o relatório em: `"$reportFile`"" -ForegroundColor $corTitulo
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host ""

Read-Host "Pressione Enter para fechar esta janela..."