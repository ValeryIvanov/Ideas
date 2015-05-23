CREATE TABLE idea (
    id INTEGER PRIMARY KEY,
    title TEXT,
    desc TEXT,
    created_date DATE DEFAULT (datetime('now','localtime')),
    password TEXT,
    latitude REAL,
    longitude REAL,
    address TEXT
);

CREATE TABLE question (
    id INTEGER PRIMARY KEY,
    question TEXT,
    created_date DATE DEFAULT (datetime('now','localtime'))
);

CREATE TABLE ideaQuestion (
    id INTEGER PRIMARY KEY,
    ideaId INTEGER,
    questionId INTEGER,
    answer TEXT
);