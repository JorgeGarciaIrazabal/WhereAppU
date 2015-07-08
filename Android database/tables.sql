CREATE TABLE User
(
    ID INTEGER PRIMARY KEY NOT NULL,
    __Updated INTEGER DEFAULT 0 NOT NULL,
    CreatedOn TEXT DEFAULT 'CURRENT_TIMESTAMP' NOT NULL,
    UpdatedOn TEXT DEFAULT 'current_timestamp' NOT NULL,
    Name TEXT NOT NULL,
    Eamil TEXT NOT NULL,
    GCM_ID TEXT NOT NULL,
    PhotoURL TEXT
);

CREATE TABLE Task
(
    ID INTEGER PRIMARY KEY NOT NULL,
    __Updated INTEGER DEFAULT 0 NOT NULL,
    CreatedOn TEXT DEFAULT 'CURRENT_TIMESTAMP' NOT NULL,
    UpdatedOn TEXT DEFAULT 'current_timestamp' NOT NULL,
    CreatorId INT NOT NULL,
    ReceiverId INT NOT NULL,
    Body TEXT NOT NULL,
    Type TEXT NOT NULL,
    Location INT NOT NULL,
    Schedule DATETIME
);

CREATE TABLE Place
(
    ID INTEGER PRIMARY KEY NOT NULL,
    __Updated INTEGER DEFAULT 0 NOT NULL,
    CreatedOn TEXT DEFAULT 'CURRENT_TIMESTAMP' NOT NULL,
    UpdatedOn TEXT DEFAULT 'current_timestamp' NOT NULL,
    Name Text NOT NULL,
    IconUri Text NOT NULL,
    Owner Int NOT NULL,
    Type TEXT NOT NULL,
    Longitude Real,
    Latitude Real,
    Range Int
);

CREATE INDEX ReceiverId_index ON Task(ReceiverId);

CREATE INDEX CreatorId_index ON Task(CreatorId);

CREATE INDEX PlaceId_index ON Task(CreatorId);