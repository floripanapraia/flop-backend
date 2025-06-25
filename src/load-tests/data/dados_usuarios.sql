-- =================================================================
-- SCRIPT PARA INSERIR 20 USUÁRIOS DE TESTE (PILOTOS DE F1)
-- =================================================================

-- A senha para todos os usuários é 'Formula1_!'
-- O hash BCrypt correspondente é: '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He'
-- =================================================================
INSERT INTO usuario (nome, nickname, email, senha, is_admin, bloqueado) VALUES
('Max Emilian Verstappen', 'MadMax', 'max.verstappen@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Sir Lewis Hamilton', 'GOAT', 'lewis.hamilton@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Charles Marc Leclerc', 'Il Predestinato', 'charles.leclerc@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Lando N. Norris', 'Lando', 'lando.norris@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Ayrton Senna da Silva', 'Magic', 'ayrton.senna@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Michael S. Schumacher', 'Schumi', 'michael.schumacher@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Fernando Alonso Diaz', 'El Nano', 'fernando.alonso@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Sebastian V. Vettel', 'Seb', 'sebastian.vettel@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Oscar J. Piastri', 'Piastri', 'oscar.piastri@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('George W. Russell', 'Mr. Saturday', 'george.russell@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Carlos Sainz Vazquez', 'Chilli', 'carlos.sainz@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Sergio Perez Mendoza', 'Checo', 'sergio.perez@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Alain M. Prost', 'The Professor', 'alain.prost@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Nelson Piquet Souto Maior', 'Nelsão', 'nelson.piquet@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Andreas Nikolaus Lauda', 'The Rat', 'niki.lauda@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Juan Manuel Fangio', 'El Maestro', 'juan.fangio@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Mika Pauli Hakkinen', 'The Flying Finn', 'mika.hakkinen@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Emerson Fittipaldi', 'Emmo', 'emerson.fittipaldi@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Yuki K. Tsunoda', 'Yuki', 'yuki.tsunoda@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false),
('Kimi-Matias Raikkonen', 'Iceman', 'kimi.raikkonen@f1-test.com', '$2a$10$R/6ylpP6xS.1v5jPSvKzT.Gzpi322a3kUaO0EONbI/CmhfPxtp/He', false, false);