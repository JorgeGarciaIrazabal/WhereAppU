import os
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, Session
from DBContext.tables import User, Task, Place
from WSHubsAPI.Hub import Hub

__author__ = 'Jorge'

def getSession():
    """
    :rtype : Session
    """
    global SessionClass
    engine = create_engine('mysql://root@localhost/wau')
    SessionClass = sessionmaker(bind=engine)
    return SessionClass()



class LoggingHub(Hub):
    def logIn(self, phoneNumber, gcmId, name=None, email=None):
        session = getSession()
        user = session.query(User).filter_by(PhoneNumber = phoneNumber).first()
        if not user:
            user = User()
        user.Name, user.PhoneNumber, user.Email, user.GCM_ID = name, phoneNumber, email, gcmId
        session.add(user)
        session.commit()
        return user.ID

class SyncHub(Hub):
    def phoneNumbers(self, phoneNumberArray):
        session = getSession()
        phoneNumbers = session.query(User).filter(User.PhoneNumber.in_(phoneNumberArray))
        return phoneNumbers.all()

class Places(Hub):
    def createPlace(self,name, userId, icon, createdTime, type):
        session = getSession()
        place = Place()
        place.name,place.User,place.Icon,place.Type = name, userId,icon,type
        session.add(place)
        session.commit()
        return place.ID

    def updatePlace(self, id, name, userId, icon, createdTime, type):
        session = getSession()
        place = Place.query.get(id)
        place.name,place.User,place.Icon,place.Type = name, userId,icon,type
        session.add(place)
        session.commit()
        return place.ID

class ChatHub(Hub):
    def sendToAll(self, message):
        self.sender().otherClients.alert(message)

class TaskHub(Hub):
    def addTask(self, body, creatorId, receiverId, createdTime):
        session = getSession()
        task = Task()
        #task.CreatedOn = createdTime
        task.Creator =creatorId
        task.Receiver = receiverId
        task.Body = body
        session.add(task)
        session.commit()
        return task.ID

class PlaceConfigHub(Hub):
    def editPlace(self, place):
        pass

    def createPlace(self, place):
        pass

path= "C:/Software Projects/WhereAppU/app/src/main/res/drawable-mdpi/"
for fn in os.listdir(path):
    os.rename(path+fn, path+fn.replace("-","_"))