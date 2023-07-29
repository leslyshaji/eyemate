import demjson as demjson
from flask import *
import datetime
from dbconnection import Db
import smtplib
from email.mime.text import MIMEText
from flask_mail import Mail

from PIL import Image

app = Flask(__name__)

blind_pic_path = r"C:\eyemate\pycharm-master\static\img\\"


@app.route('/')
def h():
    return "ok"


@app.route('/Login', methods=['post', 'get'])
def login():
    userName = request.form['username']
    passwd = request.form['password']
    db = Db()
    qry = db.selectOne("select * from login where username = '" + userName + "' and password = '" + passwd + "'")

    res = {}
    if qry is not None:
        id = qry['login_id']
        type = qry['usertype']
        if type == "caretaker":
            res2 = db.selectOne("select * from caretaker where caretaker_id='" + str(id) + "'")
            res['s'] = '1'
            res['id'] = id
            res['type'] = type
            res['name'] = res2['c_name']
            res['image'] = res2['photo']
            return demjson.encode(res)
        else:
            res['s'] = 'None'
            return demjson.encode(res)
    else:
        res['s'] = 'None'
        return demjson.encode(res)


@app.route('/forgotpassword', methods=['post', 'get'])
def forgotpassword():
    v = 0
    email = request.form['fpass']
    db = Db()
    qry = db.selectOne("select * from login where username = '" + email + "'")

    if qry is not None:

        p = qry['password']
        try:
            gmail = smtplib.SMTP('smtp.gmail.com', 587)

            gmail.ehlo()

            gmail.starttls()

            gmail.login('leslyshaji047@gmail.com', 'szkhxzhfbxurxrtd')

        except Exception as e:
            print("Couldn't setup email!!" + str(e))

        msg = MIMEText("Forgot your password ? \nYour password is :- " + str(p))

        msg['Subject'] = 'Verification'

        msg['To'] = email

        msg['From'] = 'leslyshaji047@gmail.com'

        try:

            gmail.send_message(msg)
            v = 1

        except Exception as e:

            print("COULDN'T SEND EMAIL", str(e))
        res = {}
        if v == 1:
            res['s'] = '1'
            # return demjson.encode(res)
        else:
            res['s'] = '0'
            # return demjson.encode(res)
    else:
        res = {}
        res['s'] = '2'
    return demjson.encode(res)


@app.route('/sendingreview', methods=['post'])
def sendingreview():
    user_id = request.form['lid']
    review = request.form['Review']
    db = Db()
    qry = db.insert("insert into review values('','" + review + "', '" + user_id + "', curdate())")
    res = {}
    res['s'] = '1'
    return demjson.encode(res)



@app.route('/sendingcom', methods=['post'])
def sendingcom():
    user_id = request.form['lid']
    review = request.form['Review']
    db = Db()
    qry = db.insert("insert into complaint values('','" + user_id + "', '" + review + "', curdate(),'pending','0000-00-00')")
    res = {}
    res['s'] = '1'
    return demjson.encode(res)


@app.route('/viewreview', methods=['post'])
def viewreview():
    user_id = request.form['lid']
    # review = request.form['Review']
    db = Db()
    qry = db.select(
        "select * from review,caretaker  where caretaker.caretaker_id = review.user_id")
    res = {}
    res['s'] = '1'
    res['data'] = qry
    return demjson.encode(res)


@app.route('/viewcom', methods=['post'])
def viewcom():
    user_id = request.form['lid']
    # review = request.form['Review']
    db = Db()
    qry = db.select("select * from complaint  where  user_id = '" + user_id + "'")
    print(qry)
    res = {}
    res['s'] = '1'
    res['data'] = qry
    return demjson.encode(res)


@app.route('/profile_caretaker', methods=['post'])
def profile_caretaker():
    user_id = request.form['lid']
    print(user_id, "kkkkkkkk")
    # review = request.form['Review']
    db = Db()
    qry = db.selectOne(
        "select * from caretaker where caretaker_id='" + user_id + "'")
    print(qry)
    res = {}
    if qry is not None:
        res['s'] = '1'
        res['c_name'] = qry['c_name']
        res['dob'] = qry['dob']
        res['place'] = qry['place']
        res['post'] = qry['post']
        res['pin'] = qry['pin']
        res['photo'] = qry['photo']
        res['email'] = qry['email']
        res['gender'] = qry['gender']
        res['phone_number'] = qry['phone_number']
    else:
        res['s'] = '0'
    return demjson.encode(res)


@app.route('/update_caretaker', methods=['post'])
def update_caretaker():
    try:
        name = request.form['name']
        dob = request.form['dob']
        gender = request.form['gender']
        place = request.form['place']
        post = request.form['post']
        pin = request.form['pin']
        email = request.form['email']
        phone = request.form['phone']
        # cid = request.form['cid']
        pic = request.files['pic']
        data = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
        pic.save(blind_pic_path + data + '.jpg')
        path = '/static/img/' + data + '.jpg'

        db = Db()
        qry1 = request.form['lid']

        qry = db.update(
            "update caretaker set photo='"+path+"',c_name='" + name + "' ,gender='" + gender + "' ,dob='" + dob + "' ,place='" + place + "',post='" + post + "' , pin='" + pin + "' ,email='" + email + "', phone_number='" + phone + "' where caretaker_id='" + qry1 + "'")
        res = {}
        res['s'] = '1'

        return demjson.encode(res)
    except Exception as e:
        print(e)
        name = request.form['name']
        dob = request.form['dob']
        gender = request.form['gender']
        place = request.form['place']
        post = request.form['post']
        pin = request.form['pin']
        email = request.form['email']
        phone = request.form['phone']
        # cid = request.form['cid']


        db = Db()
        qry1 = request.form['lid']

        qry = db.update(
            "update caretaker set c_name='" + name + "' ,gender='" + gender + "' ,dob='" + dob + "' ,place='" + place + "',post='" + post + "' , pin='" + pin + "' ,email='" + email + "', phone_number='" + phone + "' where caretaker_id='" + qry1 + "'")
        res = {}
        res['s'] = '1'

        return demjson.encode(res)


@app.route('/profile_blind', methods=['post'])
def profile_blind():
    user_id = request.form['lid']
    print(user_id, "kkkkkkkk")
    # review = request.form['Review']
    db = Db()
    qry = db.selectOne(
        "select * from blind where b_id='" + user_id + "'")
    print(qry)
    res = {}
    if qry is not None:
        res['s'] = '1'
        res['b_name'] = qry['b_name']
        res['age'] = qry['b_age']
        res['place'] = qry['b_place']
        res['post'] = qry['b_post']
        res['pin'] = qry['b_pin']
        res['photo'] = qry['b_photo']
        res['email'] = qry['b_email']
        res['district'] = qry['b_district']
        res['phone_number'] = qry['b_phone']
    else:
        res['s'] = '0'
    return demjson.encode(res)


@app.route('/update_blind', methods=['post'])
def update_blind():

    try:
        name = request.form['name']
        age = request.form['age']
        place = request.form['place']
        post = request.form['post']
        pin = request.form['pin']
        email = request.form['email']
        phone = request.form['phone']
        district = request.form['district']
        cid = request.form['cid']
        pic = request.files['pic']
        data = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
        pic.save(blind_pic_path + data + '.jpg')
        path = '/static/img/' + data + '.jpg'

        db = Db()

        qry = db.update(
            "update blind set b_photo='"+path+"',b_name='" + name + "' ,b_age='" + age + "' ,b_district='" + district + "' ,b_place='" + place + "',b_post='" + post + "' , b_pin='" + pin + "' ,b_email='" + email + "', b_phone='" + phone + "' where b_id='" + cid + "'")
        res = {}
        res['message'] = 'succes'
        return demjson.encode(res)
    except Exception as e:
        name = request.form['name']
        age = request.form['age']
        place = request.form['place']
        post = request.form['post']
        pin = request.form['pin']
        email = request.form['email']
        phone = request.form['phone']
        district = request.form['district']
        cid = request.form['cid']

        db = Db()
        qry = db.update(
            "update blind set b_name='" + name + "' ,b_age='" + age + "' ,b_district='" + district + "' ,b_place='" + place + "',b_post='" + post + "' , b_pin='" + pin + "' ,b_email='" + email + "', b_phone='" + phone + "' where b_id='" + cid + "'")
        res = {}
        res['message'] = 'succes'
        return demjson.encode(res)



@app.route('/change_pwd', methods=['post'])
def change_pwd():
    current_pwd = request.form['cpass']
    new_pwd = request.form['npass']
    lid = request.form['lid']
    db = Db()
    qry = db.selectOne("select * from login where password = '" + current_pwd + "' and login_id = '" + lid + "'")
    if qry is not None:
        new_qry = db.update(
            "update login set password = '" + new_pwd + "' where login_id = '" + lid + "'")
        res = {}
        res['s'] = '1'
        return demjson.encode(res)

    else:
        res = {}
        res['s'] = '0'
        return demjson.encode(res)


@app.route('/profile_kp', methods=['post'])
def profile_kp():
    user_id = request.form['lid']
    print(user_id, "kkkkkkkk")
    # review = request.form['Review']
    db = Db()
    qry = db.selectOne(
        "select * from known_person where kp_id='" + user_id + "'")
    print(qry)
    res = {}
    if qry is not None:
        res['s'] = '1'
        res['kpname'] = qry['kpname']
        res['pphone'] = qry['pphone']
        res['pimage'] = qry['pimage']
    else:
        res['s'] = '0'
    return demjson.encode(res)



@app.route('/update_kp', methods=['post'])
def update_kp():
    try:
        name = request.form['name']
        phone = request.form['phone']
        cid = request.form['cid']
        pic = request.files['pic']
        data = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
        pic.save(blind_pic_path + data+ name + '.jpg')
        path = '/static/img/' + data+ name + '.jpg'
        unknown_image = face_recognition.load_image_file(blind_pic_path + data + name + '.jpg')
        unkonownpersons = face_recognition.face_encodings(unknown_image)

        if len(unkonownpersons) > 0:
            db = Db()
            qry = db.update(
                "update known_person set pimage='"+path+"',kpname='" + name + "' , pphone='" + phone + "' where kp_id='" + cid + "'")
            res = {}
            res['message'] = 'succes'

            return demjson.encode(res)
        else:
            res = {}
            res['message'] = '0'

            return demjson.encode(res)
    except Exception as e:
        print(e,"errrrr")
        name = request.form['name']
        phone = request.form['phone']
        cid = request.form['cid']

        db = Db()

        qry = db.update("update known_person set kpname='" + name + "' , pphone='" + phone + "' where kp_id='" + cid + "'")

        res = {}
        res['message'] = 'succes'

        return demjson.encode(res)



@app.route('/viewemergency', methods=['post'])
def view_emergency():
    user_id = request.form['lid']
    print(user_id, "kkkkkkkk")
    # review = request.form['Review']
    db = Db()
    qry = db.select(
        "select emergency.*,location.location,location.lattitude,location.longitude,blind.* from blind,emergency,location where location.blind_id=emergency.eblind_id and blind.b_id = emergency.eblind_id and blind.caretaker_id = '" + user_id + "'")
    print(qry)
    res = {}
    res['s'] = '1'
    res['data'] = qry
    return demjson.encode(res)


@app.route('/viewlocation', methods=['post'])
def view_locarion():
    user_id = request.form['lid']
    print(user_id, "kkkkkkkk")
    # review = request.form['Review']
    db = Db()
    qry = db.select(
        "select * from blind,location where blind.b_id = location.blind_id and blind.b_id= '" + user_id + "'")
    print(qry)
    res = {}
    res['s'] = '1'
    res['data'] = qry
    return demjson.encode(res)


@app.route('/blind_list', methods=['post'])
def blind_list():
    user_id = request.form['lid']
    db = Db()
    qry = db.select(
        "select * from blind,caretaker where caretaker.caretaker_id=blind.caretaker_id and caretaker.caretaker_id = '" + user_id + "'")
    print(qry, user_id)
    res = {}
    res['s'] = '1'
    res['data'] = qry
    return demjson.encode(res)


@app.route('/register_blind', methods=['post'])
def register_blind():
    name = request.form['name']
    age = request.form['age']
    place = request.form['place']
    post = request.form['post']
    pin = request.form['pin']
    district = request.form['district']
    email = request.form['email']
    phone = request.form['phone']
    cid = request.form['cid']
    pic = request.files['pic']
    data = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")

    pic.save(blind_pic_path + data + '.jpg')
    path = '/static/img/' + data + '.jpg'

    db = Db()
    qry1 = db.selectOne("select * from blind where b_phone = '" + phone + "'")
    if qry1 is None:
        qry = db.insert(
            "insert into blind VALUES('" + cid + "','','" + name + "' ,'" + age + "' ,'" + place + "' ,'" + post + "','" + pin + "' , '" + district + "', '" + email + "' , '" + phone + "','" + path + "')")
        res = {}
        res['s'] = '1'
        res['data'] = qry
        return demjson.encode(res)

    else:
        res = {}
        res['s'] = '0'
        return demjson.encode(res)

@app.route('/register_caretaker', methods=['post'])
def register_caretaker():
    name = request.form['name']
    dob = request.form['dob']
    gender = request.form['gender']
    place = request.form['place']
    post = request.form['post']
    pin = request.form['pin']
    email = request.form['email']
    phone = request.form['phone']
    # cid = request.form['cid']
    pic = request.files['pic']
    data = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
    passwd = request.form['password']
    pic.save(blind_pic_path+ data + '.jpg')
    path = '/static/img/' + data + '.jpg'

    db = Db()
    qry2=db.selectOne("select * from login where username='"+email+"'")
    print(qry2)
    if qry2 is  None:
        qry1 = db.insert("insert into login values(null, '" + email + "', '" + passwd + "', 'caretaker' )")
        qry = db.insert("insert into caretaker VALUES('" + str(
            qry1) + "','" + name + "' ,'" + gender + "' ,'" + dob + "' ,'" + place + "','" + post + "' , '" + pin + "' , '" + path + "','" + email + "', '" + phone + "')")
        res = {}
        res['s'] = '1'
        return demjson.encode(res)
    else:
        res={}
        res['s']='User already found!!!!!!!'
        return demjson.encode(res)


####################Blind
import face_recognition
from flask import Flask, render_template, request, redirect, jsonify
from flask.globals import session
import base64
import demjson


@app.route("/help", methods=['POST'])
def help():
    bid = request.form["bid"]
    # bid="2"
    # lat=request.form["latitude"]
    # long=request.form["longitude"]

    voice = request.form["voice"]

    qry = "INSERT INTO emergency (eblind_id, e_voice, datetime) VALUES ('" + bid + "','" + voice + "', now())"
    c = Db()
    r = {}
    c.insert(qry)
    return jsonify(status="ok", message="send successfully")


@app.route('/verify_blind', methods=['POST'])
def verify_blind():
    bid = request.form["u"]
    qry = "select * from blind,caretaker where blind.b_phone = '" + bid + "' and blind.caretaker_id=caretaker.caretaker_id"
    print(qry)
    res = {}
    db = Db()
    resp = db.selectOne(qry)
    if resp is not None:
        res['status'] = 'ok'
        res['bid'] = resp['b_id']
        res['cphone'] = resp['phone_number']
    else:
        res['status'] = '0'
    return demjson.encode(res)


@app.route("/add_knownperson", methods=['POST'])
def add_knownperson():
    name = request.form['name']
    phone = request.form['phone']
    cid = request.form['cid']
    pic = request.files['pic']
    data = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")

    pic.save(blind_pic_path + data + name + '.jpg')
    path = '/static/img/' + data + name + '.jpg'
    unknown_image = face_recognition.load_image_file(blind_pic_path + data + name + '.jpg')
    unkonownpersons = face_recognition.face_encodings(unknown_image)

    if len(unkonownpersons) > 0:
        db = Db()

        b_id = cid
        qry = db.insert(
            "insert into known_person VALUES('','" + name + "' ,'" + path + "','" + phone + "','" + str(b_id) + "')")
        res = {}
        res['status'] = '1'
        print(res)
        return demjson.encode(res)
    else:
        res = {}
        res['status'] = '0'
        print(res)
        return demjson.encode(res)


@app.route("/remove_blind", methods=['POST'])
def remove_blind():
    db = Db()
    bid = request.form['b_id']
    qry = db.delete("delete from blind where b_id='" + bid + "'")

    res = {}
    res['s'] = '1'
    return demjson.encode(res)


@app.route("/dlkp", methods=['POST'])
def dlkp():
    db = Db()
    bid = request.form['k']
    qry = db.delete("delete from known_person where kp_id='" + bid + "'")
    res = {}
    res['s'] = '1'
    return demjson.encode(res)


@app.route('/view_kp', methods=['post'])
def view_kp():
    user_id = request.form['lid']
    db = Db()
    qry = db.select(
        "select * from known_person,blind where blind.b_id='" + user_id + "' and blind.b_id=known_person.pblind_id")
    print(qry, user_id)
    res = {}
    res['s'] = '1'
    res['data'] = qry
    return demjson.encode(res)



@app.route("/detect", methods=['POST'])
def detection():
    voice = request.form["command"]

    if voice =='call to':
        bid = request.form["id"]
        m=request.form['m']
        print(m.split('to')[-1])
        db=Db()
        q=db.selectOne("select * from known_person where lower(kpname) like '%"+str(m.split('to')[-1])+"%' and pblind_id='"+bid+"'")
        return jsonify(status="ok", message=q['pphone'])

    if voice =='help':
        print("hhhhhhhhhhhhhhhhhhhhh")
        bid = request.form["id"]

        qry = "INSERT INTO emergency (eblind_id, e_voice, datetime) VALUES ('" + bid + "','" + voice + "', now())"
        c = Db()
        r = {}
        c.insert(qry)
        return jsonify(status="ok", message="send successfully")

    if voice == "detect":
        bid = request.form["id"]
        # # bid="2"
        photo = request.files["pic"]
        photo.save(blind_pic_path + "\\a.jpg")

        from PIL import Image

        Original_Image = Image.open(blind_pic_path + "\\a.jpg")

        rotated_image2 = Original_Image.transpose(Image.ROTATE_270)
        rotated_image2.save(blind_pic_path + "\\a_270.jpg")

        qry = "select * from known_person where pblind_id='" + bid + "'"
        print(qry)
        db = Db()
        res = db.select(qry)
        known_faces = []
        userids = []
        person_name = []

        identified = ""
        s=[]
        if res is not None:
            for result in res:
                k = result["pimage"]
                k1 = k.split("/")
                k1 = k1[len(k1) - 1]
                img = blind_pic_path + "\\" + k1
                print(img)
                b_img = face_recognition.load_image_file(img)
                b_imgs = face_recognition.face_encodings(b_img)[0]
                known_faces.append(b_imgs)
                userids.append(result["kp_id"])
                person_name.append(result["kpname"])
                print(img + "done")

            unknown_image = face_recognition.load_image_file(blind_pic_path + "\\a_270.jpg")
            unkonownpersons = face_recognition.face_encodings(unknown_image)
            unidentified=[]

            if len(unkonownpersons) > 0:

                for i in range(0, len(unkonownpersons)):
                    h = unkonownpersons[i]

                    red = face_recognition.compare_faces(known_faces, h, tolerance=0.45)  # true,false,false,false]

                    for i in range(0, len(red)):
                        if red[i] == True:
                            identified = identified + person_name[i]
                            s.append(person_name[i])
                        # else:
                        #     unidentified.append(i)
                print(len(unkonownpersons)-len(s),"",len(s))
                a=len(unkonownpersons)-len(s)
                if len(s)>0:
                    if a>0:
                        return jsonify(status="ok", message=identified+" and "+str(a) +"Unidentified person")
                    else:
                        return jsonify(status="ok", message=identified)
                else:
                    return jsonify(status="no", message="Person not in your list")
            else:
                return jsonify(status="no", message="Not a person")
    elif voice == "look":
        photo = request.files["pic"]
        photo.save(blind_pic_path + "\\" + photo.filename)
        op = ""
        import cv2
        import numpy as np

        def get_output_layers(net):

            layer_names = net.getLayerNames()

            output_layers = [layer_names[i[0] - 1] for i in net.getUnconnectedOutLayers()]

            return output_layers

        # image = cv2.imread(args.image)
        image = cv2.imread(blind_pic_path + "\\" + photo.filename)

        Width = image.shape[1]
        Height = image.shape[0]
        scale = 0.00392

        classes = None

        with open('yolov3.txt', 'r') as f:
            classes = [line.strip() for line in f.readlines()]

        COLORS = np.random.uniform(0, 255, size=(len(classes), 3))
        net = cv2.dnn.readNet('yolov3.weights', 'yolov3.cfg')
        blob = cv2.dnn.blobFromImage(image, scale, (416, 416), (0, 0, 0), True, crop=False)
        net.setInput(blob)

        outs = net.forward(get_output_layers(net))

        class_ids = []
        confidences = []
        boxes = []
        conf_threshold = 0.5
        nms_threshold = 0.4

        for out in outs:
            for detection in out:
                scores = detection[5:]
                class_id = np.argmax(scores)
                confidence = scores[class_id]
                if confidence > 0.5:
                    center_x = int(detection[0] * Width)
                    center_y = int(detection[1] * Height)
                    w = int(detection[2] * Width)
                    h = int(detection[3] * Height)
                    x = center_x - w / 2
                    y = center_y - h / 2
                    class_ids.append(class_id)
                    confidences.append(float(confidence))
                    boxes.append([x, y, w, h])

        indices = cv2.dnn.NMSBoxes(boxes, confidences, conf_threshold, nms_threshold)
        objectss = ""
        objlist=[]
        for i in indices:
            i = i[0]
            objectss = objectss + classes[class_ids[i]] + ","
            objlist.append(classes[class_ids[i]])
        print(len(boxes)-len(objlist), "Un identified", len(boxes), len(objlist))
        if len(objectss)>0:
            a=len(boxes)-len(objlist)
            if a == 0:
                return jsonify(status="ok", message=objectss)
            else:

                objectss=objectss+str(a)+" Objects cannot identified"
                return jsonify(status="ok", message=objectss)
        else:
            return jsonify(status="ok", message="The object can not identified")

    return jsonify(status="ok", message="no")



@app.route('/view_emergency_alert', methods=['POST'])
def view_emergency_alert():
    cid = request.form["cid"]
    lastid = request.form["lastid"]
    qry = "select * from emergency,blind where e_id>'" + lastid + "' and eblind_id=blind.b_id and blind.caretaker_id='" + cid + "' limit 1"
    print(qry)
    res = {}
    db = Db()
    resp = db.selectOne(qry)
    if resp is not None:
        res['status'] = 'ok'
        res['msg'] = resp['e_voice']
        res['bname'] = resp['b_name']
        res['eid'] = resp['e_id']
    else:
        res['status'] = '0'
    return demjson.encode(res)


@app.route("/location_updation", methods=['POST'])
def location_updation():
    lati = request.form['lati']
    longi = request.form['longi']
    place = request.form['place']
    bid = request.form['bid']

    db = Db()
    qry1 = db.selectOne("select * from location where blind_id='" + bid + "'")
    if qry1 is None:
        qry = db.insert(
            "insert into location values(null,'" + bid + "','" + place + "','" + lati + "','" + longi + "',now())")
    else:
        db.update(
            "update location set location='" + place + "',lattitude='" + lati + "',longitude='" + longi + "',datetime=now() where blind_id='" + bid + "'")
    res = {}
    res['status'] = 'ok'
    return demjson.encode(res)


if __name__ == '__main__':
    app.run(host='0.0.0.0',port="4000")
