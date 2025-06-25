# ===================================================================
# SCRIPT PARA TESTE DE CARGA - CENÁRIO 2: CRIAR POSTAGEM
# ===================================================================

# FORÇA A CODIFICAÇÃO DE SAÍDA PARA UTF-8 PARA CORRIGIR ACENTOS
$OutputEncoding = [System.Text.Encoding]::UTF8

$corTitulo = "Green"
$corComando = "Cyan"
$corInfo = "Yellow"

Clear-Host
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host "  Iniciando Teste de Carga: Cenário 2 - Criar Postagem" -ForegroundColor $corTitulo
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host ""

# --- DEFINIÇÃO DE CAMINHOS DINÂMICOS ---
$projectRoot = Resolve-Path "$PSScriptRoot/.." 

$collectionFile = "$projectRoot/collections/Cenario2_CriarPostagem.postman_collection.json"
$dataFile = "$projectRoot/data/dados_cenario2_criar_postagem.csv"
$environmentFile = "$projectRoot/flop_local.postman_environment.json"
$reportFile = "$projectRoot/relatorio_cenario2.html"

# Executa o comando Newman diretamente
newman run "$collectionFile" `
    -e "$environmentFile" `
    -d "$dataFile" `
    -n 20 `
    --reporters cli,html `
    --reporter-html-export "$reportFile"

# Mensagem de conclusão
Write-Host ""
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host "  Teste do Cenário 2 CONCLUÍDO." -ForegroundColor $corTitulo
Write-Host "  Verifique o relatório em: `"$reportFile`"" -ForegroundColor $corTitulo
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host ""

Read-Host "Pressione Enter para fechar esta janela..."