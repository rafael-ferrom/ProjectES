-- Habilita a inserção manual de IDs na tabela 'bulas'
SET IDENTITY_INSERT bulas ON;

-- Inserção de dados iniciais para a tabela de Bulas
INSERT INTO bulas (id, nome_comercial, principio_ativo, concentracao, forma_farmaceutica, apresentacao, fabricante, foto_url) VALUES
(1, 'Dipirona', 'Dipirona sódica', '500mg', 'Comprimido', 'Caixa com 20 comprimidos', 'Medley', 'https://paguemenos.vtexassets.com/arquivos/ids/644482-800-auto?v=638008072496630000&width=800&height=auto&aspect=true'),
(2, 'Paracetamol', 'Paracetamol', '750mg', 'Comprimido', 'Caixa com 20 comprimidos', 'EMS', 'https://paguemenos.vtexassets.com/arquivos/ids/641086/paracetamol-750mg-com-20-comprimidos-generico-neo-quimica-principal.jpg?v=638008025296600000'),
(3, 'Amoxicilina', 'Amoxicilina tri-hidratada', '500mg', 'Cápsula', 'Caixa com 15 cápsulas', 'Neo Química', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSWONGwEMA34vZzdyKEuEQPCW9C5kIC31BXbw&s'),
(4, 'Loratadina', 'Loratadina', '10mg', 'Comprimido', 'Caixa com 12 comprimidos', 'Medley', 'https://cdn1.staticpanvel.com.br/produtos/15/157190-15.jpg'),
(5, 'Profenid', 'Cetoprofeno', '100mg', 'Cápsula', 'Caixa com 12 cápsulas', 'Sanofi Aventis', 'https://www.santoremedio.com.br/media/produtos/7896070601765_400x400_qmTGXyz.png'),
(6, 'Minoxilina', 'Minociclina', '100mg', 'Cápsula', 'Caixa com 20 cápsulas', 'Aché', 'https://vipfarma.com.br/media/catalog/product/cache/1/image/9df78eab33525d08d6e5fb8d27136e95/a/c/ache.jpg'),
(7, 'Buscopan', 'Butilbrometo de escopolamina', '10mg', 'Comprimido', 'Caixa com 20 comprimidos', 'Boehringer Ingelheim', 'https://www.drogarianovaesperanca.com.br/imagens/600x600/buscopan-10mg-com-20-drageas-edd4e874ab.jpg'),
(8, 'Desalex', 'Desloratadina', '5mg', 'Comprimido', 'Caixa com 10 comprimidos', 'MSD', 'https://www.drogarianovaesperanca.com.br/imagens/600x600/desalex-05mg-com-100ml-2e6b0861c8.jpg');

-- Desabilita a inserção manual de IDs
SET IDENTITY_INSERT bulas OFF;

-- Inserção de dados para as instruções padrão de cada bula
INSERT INTO bula_instrucoes (bula_id, instrucao) VALUES
(1, 'Tomar com um copo de água.'),
(1, 'Não exceder a dose recomendada.'),
(2, 'Pode ser tomado com ou sem alimentos.'),
(2, 'Em caso de febre persistente, procure um médico.'),
(3, 'Completar o ciclo de tratamento, mesmo com a melhora dos sintomas.'),
(3, 'Tomar em intervalos regulares.'),
(4, 'Tomar uma vez ao dia.'),
(5, 'Tomar junto com as refeições para evitar desconforto gástrico.'),
(6, 'Evitar exposição solar excessiva durante o tratamento.'),
(7, 'Indicado para cólicas e dores abdominais.'),
(8, 'Não causa sonolência na maioria dos pacientes.');

-- Habilita a inserção manual de IDs na tabela 'users'
SET IDENTITY_INSERT users ON;

-- Inserção de usuários de exemplo
INSERT INTO users (id, name, email, password) VALUES
(1, 'Maria Silva', 'maria@gmail.com', '123456'),
(2, 'João Santos', 'joao@gmail.com', '123456');

-- Desabilita a inserção manual de IDs
SET IDENTITY_INSERT users OFF;