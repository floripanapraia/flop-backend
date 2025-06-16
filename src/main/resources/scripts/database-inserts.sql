-- Usando o banco de dados
USE `flop_db`;

SET FOREIGN_KEY_CHECKS = 0; -- Desabilita a verificação de chaves estrangeiras
TRUNCATE TABLE `denuncia`;
TRUNCATE TABLE `avaliacao_condicoes`;
TRUNCATE TABLE `avaliacao`;
TRUNCATE TABLE `postagem`;
TRUNCATE TABLE `sugestao`;
TRUNCATE TABLE `forgot_password`;
TRUNCATE TABLE `usuario`;
TRUNCATE TABLE `praia`;
SET FOREIGN_KEY_CHECKS = 1; -- Habilita a verificação novamente

-- -----------------------------------------------------
-- Inserindo dados na tabela `usuario`
-- -----------------------------------------------------
-- Inserir usuários (deixando o BD gerar os IDs 1, 2, 3, 4, 5)
INSERT INTO `usuario` (`bloqueado`, `criado_em`, `email`, `foto_perfil`, `is_admin`, `nickname`, `nome`, `senha`) VALUES
(0, '2025-01-10 08:00:00', 'admin@florianopolis.com', NULL, 1, 'admin_floripa', 'Admin', 'senha_super_segura_admin'),
(0, '2025-02-15 10:30:00', 'ana.silva@email.com', NULL, 0, 'ana_surf', 'Ana Silva', 'senha123'),
(0, '2025-03-20 14:00:00', 'carlos.santos@email.com', NULL, 0, 'carlos_praiano', 'Carlos Santos', 'senha456'),
(1, '2025-04-05 09:45:00', 'beatriz.costa@email.com', NULL, 0, 'bia_verao', 'Beatriz Costa', 'senha789'),
(0, '2025-05-12 11:20:00', 'lucas.oliveira@email.com', NULL, 0, 'lucas_sol', 'Lucas Oliveira', 'senha101');

-- -----------------------------------------------------
-- Inserindo dados na tabela `praia`
-- -----------------------------------------------------
INSERT INTO `praia` (`id_praia`, `imagem`, `latitude`, `longitude`, `nome_praia`, `place_id`) VALUES
(1, NULL, -27.4339, -48.3986, 'Praia dos Ingleses', 'ChIJ1Z2xLq1fJ5URkPDR2YpiC9o'),
(2, NULL, -27.6000, -48.4250, 'Praia do Campeche', 'ChIJ-c9-e7pfJ5URaP9r8tGfA4I'),
(3, NULL, -27.4984, -48.4231, 'Praia de Jurerê', 'ChIJFzQ3XGdfJ5URsNe2cW_yB_E'),
(4, NULL, -27.6333, -48.4500, 'Praia da Joaquina', 'ChIJX9YJ3KVfJ5URmXp-bJbB0hA'),
(5, NULL, -27.5953, -48.5482, 'Praia Mole', 'ChIJ_z_ZPKJfJ5URT3fC6Jg_A3o');

-- -----------------------------------------------------
-- Inserindo dados na tabela `avaliacao_seq`
-- -----------------------------------------------------
INSERT INTO `avaliacao_seq` (`next_val`) VALUES (100); -- Começando a sequência de avaliações a partir de 100

-- -----------------------------------------------------
-- Inserindo dados na tabela `avaliacao`
-- -----------------------------------------------------
INSERT INTO `avaliacao` (`id_avaliacao`, `criado_em`, `praia_id`, `usuario_id`) VALUES
(1, '2025-06-10 15:00:00', 1, 2),
(2, '2025-06-11 11:00:00', 2, 3),
(3, '2025-06-12 16:30:00', 3, 5),
(4, '2025-06-13 09:00:00', 1, 3),
(5, '2025-06-14 10:00:00', 4, 2);

-- -----------------------------------------------------
-- Inserindo dados na tabela `avaliacao_condicoes`
-- -----------------------------------------------------
-- Condições para a avaliação 1 (Ana na Praia dos Ingleses)
INSERT INTO `avaliacao_condicoes` (`avaliacao_id_avaliacao`, `condicoes`) VALUES
(1, 'SOL'), (1, 'MAR_CALMO'), (1, 'LIMPA'), (1, 'ESTACIONAMENTO');
-- Condições para a avaliação 2 (Carlos na Praia do Campeche)
INSERT INTO `avaliacao_condicoes` (`avaliacao_id_avaliacao`, `condicoes`) VALUES
(2, 'VENTO'), (2, 'MAR_ONDAS'), (2, 'AGUA_VIVA');
-- Condições para a avaliação 3 (Lucas em Jurerê)
INSERT INTO `avaliacao_condicoes` (`avaliacao_id_avaliacao`, `condicoes`) VALUES
(3, 'SOL'), (3, 'LOTADA'), (3, 'MUSICA'), (3, 'ALIMENTACAO');
-- Condições para a avaliação 4 (Carlos na Praia dos Ingleses)
INSERT INTO `avaliacao_condicoes` (`avaliacao_id_avaliacao`, `condicoes`) VALUES
(4, 'NUBLADO'), (4, 'AGUA_GELADA');
-- Condições para a avaliação 5 (Ana na Praia da Joaquina)
INSERT INTO `avaliacao_condicoes` (`avaliacao_id_avaliacao`, `condicoes`) VALUES
(5, 'SOL'), (5, 'MAR_ONDAS'), (5, 'SALVA_VIDAS');

-- -----------------------------------------------------
-- Inserindo dados na tabela `postagem`
-- -----------------------------------------------------
INSERT INTO `postagem` (`id_postagem`, `criado_em`, `excluida`, `imagem`, `mensagem`, `praia_id`, `usuario_id`) VALUES
(1, '2025-06-10 15:05:00', 0, NULL, 'Dia lindo na Praia dos Ingleses! Água calma e sol brilhando.', 1, 2),
(2, '2025-06-11 11:10:00', 0, NULL, 'Cuidado com as águas-vivas no Campeche hoje.', 2, 3),
(3, '2025-06-12 16:35:00', 1, NULL, 'Jurerê está impossível de cheia, mas a festa está boa!', 3, 5),
(4, '2025-06-14 10:05:00', 0, NULL, 'Ótimas ondas para surfar na Joaquina!', 4, 2);

-- -----------------------------------------------------
-- Inserindo dados na tabela `denuncia`
-- -----------------------------------------------------
-- Denúncia para a postagem 3, que foi excluída
INSERT INTO `denuncia` (`id_denuncia`, `criado_em`, `motivo`, `status`, `postagem_id`, `usuario_id`) VALUES
(1, '2025-06-12 17:00:00', 'INADEQUADO', 'ACEITA', 3, 2), -- Ana denunciou a postagem de Lucas
(2, '2025-06-11 12:00:00', 'SPAM_PROPAGANDA', 'PENDENTE', 2, 5); -- Lucas denunciou a postagem de Carlos

-- -----------------------------------------------------
-- Inserindo dados na tabela `sugestao`
-- -----------------------------------------------------
INSERT INTO `sugestao` (`id_sugestao`, `analisada`, `bairro`, `criada_em`, `descricao`, `nome_praia`, `usuario_id`) VALUES
(1, 0, 'Pântano do Sul', '2025-06-01 18:00:00', 'Praia muito bonita e tranquila no sul da ilha, ótima para famílias.', 'Praia do Pântano do Sul', 3),
(2, 1, 'Barra da Lagoa', '2025-05-25 12:00:00', 'Adicionar a prainha da Barra da Lagoa, que é um local muito frequentado.', 'Prainha da Barra', 2);

-- -----------------------------------------------------
-- Inserindo dados na tabela `forgot_password`
-- -----------------------------------------------------
-- Exemplo de token de redefinição de senha para o usuário Carlos
INSERT INTO `forgot_password` (`fpid`, `expiration_time`, `otp`, `user_id_usuario`) VALUES
(1, '2025-06-15 12:00:00', 123456, 3);

-- -----------------------------------------------------
-- Inserindo dados na tabela `praia`
-- -----------------------------------------------------
INSERT INTO flop_db.praia (nome_praia, place_id, latitude, longitude) VALUES
('Praia da Saudade', 'ChIJk70M1bw3J5URW9wBzLoCdkk', -27.6115724, -48.58135919999999),
('Praia Do Sambaqui', 'ChIJBS63Ht5FJ5URaWMHdNmAJ1A', -27.4860832, -48.5384513),
('Praia do Forte', 'ChIJJXSopB1FJ5UR5U13sK9k3Hk', -27.4351236, -48.5203432),
('Praia da Joaquina', 'ChIJBSybQTg8J5URP5RckJSq30g', -27.6293577, -48.4490173),
('Praia dos Ingleses', 'ChIJAX_-a_xpJ5URfAUDZnM_FPg', -27.4371624, -48.3894599),
('Praia de Cachoeira do Bom Jesus', 'ChIJy7yS3VNCJ5UR1m_jcRrLf2k', -27.4209406, -48.4357595),
('Praia da Tapera', 'ChIJR5-LS44wJ5URBS9NcIka9Uw', -27.6862607, -48.57166540000001),
('Praia de São Luís', 'ChIJSSeVxBA4J5URnS6Ev6MvL0w', -27.583703, -48.5466671),
('Praia da Barra da Lagoa', 'ChIJlXla2Zc_J5UREVpUyRBJraI', -27.5734502, -48.42493899999999),
('Praia da Saudade', 'ChIJu0sYU703J5URfFnF35RjisU', -27.610838, -48.5792088),
('Praia das Palmeiras', 'ChIJg9WyErI3J5UREfpR2ofzytc', -27.6157893, -48.5953054),
('Praia do Matadeiro', 'ChIJNZ7dWV0kJ5URvYKnHEkKq_A', -27.7548429, -48.4985647),
('Praia da Galheta', 'ChIJT2ez228-J5UROebf7VbK5Io', -27.5925134, -48.4251814),
('Praia do Gravatá', 'ChIJOwQIf9E9J5URGV8X8I9FIpw', -27.6133684, -48.4338418),
('Praia do Campeche', 'ChIJA9ZasVk7J5URM59o-uEPlqY', -27.662215, -48.4734326),
('Praia do Caldeirão', 'ChIJt1hsAMMkJ5URlJ31NtMwpwc', -27.7266782, -48.50627309999999),
('Praia do Santinho', 'ChIJs84CakNqJ5UREetJtVz5okQ', -27.4618653, -48.3761513),
('Praia do Canto da Lagoa', 'ChIJKwGno045J5URzYQALyZhVYY', -27.6112209, -48.4841839),
('Praia da Daniela', 'ChIJp5Uw_59FJ5UR4JqKumLiF18', -27.4499966, -48.5398341),
('Praia da Armação', 'ChIJj6-75-8kJ5URWIjlVEM-7Bg', -27.7442843, -48.5076271),
('Praia de Canasvieiras', 'ChIJifbU-6FDJ5URYx_rGbNXDTw', -27.4271341, -48.4585088),
('Praia do Morro das Pedras', 'ChIJp_NPSbUkJ5URF8CMLwcKNz4', -27.6917609, -48.4821619),
('Praia Comprida', 'ChIJVS8BkVRGJ5URbM7VBS7cipc', -27.5221902, -48.52369059999999),
('Praia do Canto da Lagoa', 'ChIJLTAVKgE8J5URqXr7fkqCspM', -27.626358, -48.4761561),
('Praia de Canajurê', 'ChIJU4QQ-n9DJ5URcDZV9dtjAVM', -27.4292352, -48.478023),
('Praia da Tapera', 'ChIJGZc9XI8wJ5UReCChaekLQvE', -27.6883383, -48.5691247),
('Praia do Forte', 'ChIJ9f18gB1FJ5UROAANvHmPNos', -27.431987, -48.5187537),
('Praia de Santo Antônio de Lisboa', 'ChIJqy-nt1hGJ5URCVK0XW46CuQ', -27.5076326, -48.520087),
('Praia de Fora', 'ChIJ7apJ7xs4J5URiEN05mVMt3k', -27.586212, -48.5528347),
('Praia do Ribeirão da Ilha', 'ChIJEQixMA8wJ5URs3B8B6WB2hQ', -27.7213403, -48.5648258),
('Praia do Cacupé Pequeno', 'ChIJg1SGaa9HJ5URjGtqHxvW9Mg', -27.5310867, -48.5246673),
('Praia de Jurere', 'ChIJ5cbMnvVEJ5UR1t4XzkgOT9c', -27.4369269, -48.500732),
('Praia do Cacupé Grande', 'ChIJz5h1a6JHJ5URhObinkKssF8', -27.5414801, -48.5262796);