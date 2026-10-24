
INSERT INTO autores (nome, nacionalidade) VALUES
 ('Machado de Assis', 'Brasileira'),
 ('Clarice Lispector', 'Brasileira'),
 ('George Orwell', 'Britânica');

INSERT INTO livros (titulo, isbn, ano_publicacao, disponivel, autor_id) VALUES
 ('Dom Casmurro',        '978-85-0000-001-1', 1899, TRUE,  1),
 ('Memórias Póstumas de Brás Cubas', '978-85-0000-002-8', 1881, TRUE, 1),
 ('A Hora da Estrela',   '978-85-0000-003-5', 1977, FALSE, 2),
 ('1984',                '978-85-0000-004-2', 1949, TRUE,  3);

