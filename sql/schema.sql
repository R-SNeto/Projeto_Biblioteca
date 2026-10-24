
CREATE TABLE IF NOT EXISTS usuarios (
    id          SERIAL PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    login       VARCHAR(50)  NOT NULL UNIQUE,
    senha       VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS autores (
    id             SERIAL PRIMARY KEY,
    nome           VARCHAR(100) NOT NULL,
    nacionalidade  VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS livros (
    id              SERIAL PRIMARY KEY,
    titulo          VARCHAR(150) NOT NULL,
    isbn            VARCHAR(20)  UNIQUE,
    ano_publicacao  INT CHECK (ano_publicacao > 0),
    disponivel      BOOLEAN NOT NULL DEFAULT TRUE,
    autor_id        INT NOT NULL,
    CONSTRAINT fk_livros_autor FOREIGN KEY (autor_id)
        REFERENCES autores (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_livros_autor  ON livros (autor_id);
CREATE INDEX IF NOT EXISTS idx_livros_titulo ON livros (titulo);
