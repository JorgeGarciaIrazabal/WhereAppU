from InfoTable import InfoTable
import json
from Classes.QueryManager import QueryManager

__author__ = 'jorge'
from bottle import *

qm =QueryManager()
@route('/login', method = 'POST')
def login():
    get=request.POST.get('data','')
    get = json.loads(get).get
    return str(qm.insertUser(get("email",''),get("phonenumber"),get("gcmid")).get())
i=0
@route('/getRelatedContacts', method = 'POST')
def getRelatedContacts():
    global i
    phonesStrings=request.POST.get('data','')
    print i
    i+=1
    phones= qm.selectQt("* from User where phoneNumber in (%s)"%phonesStrings)
    return phones.toJson()


run(host='192.168.1.139', port=8080, debug=True)