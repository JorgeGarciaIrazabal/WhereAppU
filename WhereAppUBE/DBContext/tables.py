# coding: utf-8
from datetime import datetime
from sqlalchemy import Column, DateTime, Enum, ForeignKey, Index, Integer, String, Text, text, create_engine
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class Task(Base):
    __tablename__ = 'task'

    ID = Column(Integer, primary_key=True)
    Creator = Column(ForeignKey(u'user.ID'), nullable=False, index=True)
    CreatedOn = Column(DateTime, nullable=False, server_default=text("CURRENT_TIMESTAMP"))
    Body = Column(Text(collation=u'utf8_unicode_ci'))
    Type = Column(Enum(u'Comment', u'Scheduled', u'Place'), nullable=False, server_default=text("'Comment'"))
    Receiver = Column(ForeignKey(u'user.ID'), nullable=False, index=True)
    UpdatedOn = Column(DateTime, nullable=False, server_default=text("CURRENT_TIMESTAMP"), onupdate=datetime.now)

    rCreator = relationship(u'User', primaryjoin='Task.Creator == User.ID')
    rReceiver = relationship(u'User', primaryjoin='Task.Receiver == User.ID')

class User(Base):
    __tablename__ = 'user'

    ID = Column(Integer, primary_key=True)
    Name = Column(Text(collation=u'utf8_unicode_ci'), nullable=False)
    PhoneNumber = Column(Text(collation=u'utf8_unicode_ci'), nullable=False)
    Email = Column(Text(collation=u'utf8_unicode_ci'))
    GCM_ID = Column(Text(collation=u'utf8_unicode_ci'))
    UpdatedOn = Column(DateTime, nullable=False, server_default=text("CURRENT_TIMESTAMP"), onupdate=datetime.now)

class Place(Base):
    __tablename__ = 'place'

    ID = Column(Integer, primary_key=True)
    User = Column(ForeignKey(u'user.ID'), nullable=False, index=True)
    CreatedOn = Column(DateTime, nullable=False, server_default=text("CURRENT_TIMESTAMP"))
    Name = Column(Text(collation=u'utf8_unicode_ci'))
    Type = Column(Enum(u'Public', u'Private'), nullable=False, server_default=text("'Public'"))
    Icon = Column(Text(collation=u'utf8_unicode_ci'), nullable=False)
    UpdatedOn = Column(DateTime, nullable=False, server_default=text("CURRENT_TIMESTAMP"), onupdate=datetime.now)

    rUser = relationship(u'User', primaryjoin='Place.User == User.ID')

if __name__ == '__main__':
    from sqlalchemy.sql import select
    engine = create_engine('mysql://root@localhost/wau', echo = True)
    Base.metadata.create_all(bind = engine)
    #com = engine.connect()
    #s = select([User])
    #com.execute(s)
