```sh
$ git init
$ git add.
$ git commit -m "init"
$ heroku create --stack cedar
Creating severe-beach-4553... done, stack is cedar
http://severe-beach-4553.herokuapp.com/ | git@heroku.com:severe-beach-4553.git
$ heroku rename play20-mongodb-sample --app severe-beach-4553
$ heroku scale web=1
$ heroku addons:add mongolab:starter
$ git push heroku master
$ heroku open
```
