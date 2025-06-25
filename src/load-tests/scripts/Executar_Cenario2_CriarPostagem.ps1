# ===================================================================
# SCRIPT PARA TESTE DE CARGA - CENARIO 2: CRIAR POSTAGEM
# ===================================================================

$OutputEncoding = [System.Text.Encoding]::UTF8

$corTitulo = "Green"
$corInfo = "Yellow"

Clear-Host
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host "  Iniciando Teste de Carga: Cenario 2 - Criar Postagem" -ForegroundColor $corTitulo
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host ""

# --- DEFINICAO DE CAMINHOS DINAMICOS ---
$projectRoot = Resolve-Path "$PSScriptRoot/.." 

$collectionFile = "$projectRoot/collections/Cenario2_CriarPostagem.postman_collection.json"
$dataFile = "$projectRoot/data/dados_cenario2_criar_postagem.csv"
$environmentFile = "$projectRoot/flop_local.postman_environment.json"
$reportFile = "$projectRoot/relatorio_cenario2.html"

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
Write-Host "  Teste do Cenario 2 CONCLUIDO." -ForegroundColor $corTitulo
Write-Host "  Verifique o relatorio em: `"$reportFile`"" -ForegroundColor $corTitulo
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host ""

Read-Host "Pressione Enter para fechar esta janela..."