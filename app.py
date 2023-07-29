from flask import Flask, render_template, request, redirect, session
from dbconnection import Db
import datetime
from email.mime import image
import os
from flask import *
import smtplib
from email.mime.text import MIMEText
from flask_mail import Mail

app = Flask(__name__)
app.secret_key = '234243525436'


@app.route('/')
def login():
    return render_template("Login.html", login=True)


@app.route('/login_post', methods=['post'])
def login_post():
    uname = request.form['textfield']
    passwd = request.form['textfield2']
    db = Db()
    qry = db.selectOne(
        "select * from Login where username='" + uname + "' and password='" + passwd + "' and usertype='admin'")
    if qry is not None:
        session['login_id'] = qry['login_id']
        return redirect('/home')
    else:
        return "<script>alert('Invalid username or password'); window.location='/'</script>"


@app.route('/logout')
def logout():
    session.pop('login_id', None)
    return redirect('/')


@app.route('/home')
def admin_home():
    if session.get('login_id') is not None:
        db = Db()
        log = db.selectOne("select count(login_id) from login")
        blind_count = db.selectOne("select count(b_id) from blind")
        count = [log, blind_count]
        total = count[0]['count(login_id)'] + count[1]['count(b_id)']

        b_count = db.selectOne("select count(b_id) from blind")
        c_count = db.selectOne("select count(login_id) from login where usertype='caretaker'")
        r_count = db.selectOne("select count(review_id) from review")

        blinds = ("Blind", b_count['count(b_id)'])
        caretakers = ("Caretakers", c_count['count(login_id)'])
        reviews = ("Reviews", r_count['count(review_id)'])

        return render_template("admin_home.html", blinds=blinds, caretakers=caretakers, reviews=reviews, total=total,
                               home=True)
    else:
        return redirect('/')


@app.route('/pwd')
def pwd():
    if session.get('login_id') is not None:
        return render_template("change_pwd.html", change_pwd=True)
    else:
        return redirect('/')


@app.route('/set_pwd', methods=['post'])
def set_pwd():
    current_pwd = request.form['current_pwd']
    new_pwd = request.form['new_pwd']
    re_new_pwd = request.form['re_new_pwd']
    db = Db()
    qry = db.selectOne("select * from login where password = '" + current_pwd + "'")
    if qry is not None:
        if new_pwd == re_new_pwd:
            new_qry = db.update(
                "update login set password = '" + new_pwd + "' where login_id = '" + str(session['login_id']) + "'")

            try:
                gmail = smtplib.SMTP('smtp.gmail.com', 587)

                gmail.ehlo()

                gmail.starttls()

                gmail.login('leslyshaji047@gmail.com', 'szkhxzhfbxurxrtd')

            except Exception as e:
                print("Couldn't setup email!!" + str(e))

            msg = MIMEText("Your password is :- " + str(new_pwd))

            msg['Subject'] = 'Verification'

            msg['To'] = "leslyshaji047@gmail.com"

            msg['From'] = "leslyshaji047@gmail.com"

            try:

                gmail.send_message(msg)

            except Exception as e:

                print("COULDN'T SEND EMAIL", str(e))

            return '<script> alert("Password changed successfully"); window.location="/"</script>'
        else:
            return '<script> alert("Password doesnt match"); window.location="/pwd"</script>'
    else:
        return '<script> alert("Password doesnt match"); window.location="/pwd"</script>'


@app.route('/forgot_passwd')
def forgot_passwd():
    return render_template("Forgot_passwd.html")


@app.route('/forgot_passwd_post', methods=['post'])
def forgot_passwd_post():
    email = request.form['email']
    db = Db()
    qry = db.selectOne("select * from login where username = '" + email + "' and usertype='admin'")
    if qry is not None:

        p = qry['password']
        try:
            gmail = smtplib.SMTP('smtp.gmail.com', 587)

            gmail.ehlo()

            gmail.starttls()

            gmail.login('leslyshaji047@gmail.com', 'szkhxzhfbxurxrtd')

        except Exception as e:
            print("Couldn't setup email!!" + str(e))

        msg = MIMEText("Your password is :- " + str(p))

        msg['Subject'] = 'Verification'

        msg['To'] = email

        msg['From'] = 'leslyshaji047@gmail.com'

        try:

            gmail.send_message(msg)

        except Exception as e:

            print("COULDN'T SEND EMAIL", str(e))

        return '<script> alert("Please check your mail for password"); window.location="/"</script>'
    else:
        return '<script> alert("Account doesnt exists. Please check again"); window.location="/forgot_passwd"</script>'


@app.route('/blind/<i>')
def blind(i):
    db = Db()
    qry = db.select("select * from blind where caretaker_id = '" + i + "'")
    return render_template("Blind_list.html", data=qry)


@app.route('/blind_list')
def blind_list():
    if session.get('login_id') is not None:
        db = Db()
        qry = db.select("select * from blind")
        return render_template("Blind_list.html", data=qry, blind=True)
    else:
        return redirect('/')


@app.route('/caretaker')
def caretaker():
    if session.get('login_id') is not None:
        db = Db()
        qry = db.select("select * from caretaker")
        return render_template("Caretaker.html", data=qry, caretaker=True)
    else:
        return redirect('/')




@app.route('/review')
def review():
    if session.get('login_id') is not None:
        db = Db()
        qry = db.select("select * from caretaker,review where caretaker.caretaker_id = review.user_id")
        return render_template("Review.html", data=qry, review=True)
    else:
        return redirect('/')

@app.route('/delete_review/<i>')
def delete_review(i):
    if session.get('login_id') is not None:
        db = Db()
        qry = db.delete("delete from review where review_id='"+i+"'")
        return redirect('/review')
    else:
        return redirect('/')
@app.route('/deleteblind/<i>')
def deleteblind(i):
    if session.get('login_id') is not None:
        db = Db()
        qry = db.delete("delete from blind where b_id='"+i+"'")
        return redirect('/blind_list')
    else:
        return redirect('/')
@app.route('/caretaker_delete/<i>')
def caretaker_delete(i):
    if session.get('login_id') is not None:
        db = Db()
        qry = db.delete("delete from login where login_id='"+i+"'")
        qry = db.delete("delete from caretaker where caretaker_id='"+i+"'")
        return redirect('/caretaker')
    else:
        return redirect('/')

@app.route('/c/<i>',methods=['get','post'])
def c(i):
    if session.get('login_id') is not None:
        db = Db()
        if request.method=='POST':
            qry = db.update("update complaint set reply='"+request.form['textfield']+"',r_date=curdate() where complaint_id='"+i+"'")
            return redirect('/comp')
        return render_template("c.html")
    else:
        return redirect('/')




@app.route('/comp')
def comp():
    if session.get('login_id') is not None:
        db = Db()
        qry = db.select("select * from caretaker,complaint where caretaker.caretaker_id = complaint.user_id and reply='pending'")
        return render_template("view_com.html", data=qry, com=True)
    else:
        return redirect('/')

if __name__ == '__main__':
    app.run()
