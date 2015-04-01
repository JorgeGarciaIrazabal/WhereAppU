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

@route('/getRelatedContacts', method = 'POST')
def getRelatedContacts():
    phonesStrings=request.POST.get('data','')
    phones= qm.selectQt("* from User where phoneNumber in (%s)"%phonesStrings)
    return phones.toJson()


run(host='192.168.1.139', port=8080, debug=True)