# ===================================================================
# SCRIPT PARA TESTE DE CARGA - CENÁRIO 3: EDITAR USUÁRIO
# ===================================================================

# FORÇA A CODIFICAÇÃO DE SAÍDA PARA UTF-8 PARA CORRIGIR ACENTOS
$OutputEncoding = [System.Text.Encoding]::UTF8

$corTitulo = "Green"
$corComando = "Cyan"
$corInfo = "Yellow"

Clear-Host
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host "  Iniciando Teste de Carga: Cenário 3 - Editar Usuário" -ForegroundColor $corTitulo
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host ""

# --- DEFINIÇÃO DE CAMINHOS DINÂMICOS ---
$projectRoot = Resolve-Path "$PSScriptRoot/.." 

$collectionFile = "$projectRoot/collections/Cenario3_EditarUsuario.postman_collection.json"
$dataFile = "$projectRoot/data/dados_cenario3_editar_usuario.csv"
$environmentFile = "$projectRoot/flop_local.postman_environment.json"
$reportFile = "$projectRoot/relatorio_cenario3.html"

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
Write-Host "  Teste do Cenário 3 CONCLUÍDO." -ForegroundColor $corTitulo
Write-Host "  Verifique o relatório em: `"$reportFile`"" -ForegroundColor $corTitulo
Write-Host "===========================================================" -ForegroundColor $corTitulo
Write-Host ""

Read-Host "Pressione Enter para fechar esta janela..."