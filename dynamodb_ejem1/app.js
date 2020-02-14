var AWS = require('aws-sdk');
var express = require('express');

var app = express();
app.use(express.json()) 

AWS.config.update({
  region: "us-east-1", // North virginia - USA
});

var index = 0;

const table ="anuncios"

var db = new AWS.DynamoDB.DocumentClient();

app.get('/api/anuncios', function (req, res) {

  var params = {
      TableName : table,
  };

  db.scan(params, function(err, data) {
      if (err) {
          console.error("Error al recuperar los anuncios: ", JSON.stringify(err, null, 2));
      } else {
          res.send(data.Items);
      }
  });

});

app.post('/api/anuncios', function (req, res) {

  var anuncio = req.body;
  anuncio.id = index;

  var params = {
      TableName:table,
      Item: anuncio
  };

  db.put(params, function(err, data) {
      if (err) {
          console.error("Error al procesar el JSON:", JSON.stringify(err, null, 2));
          res.send(err);
      } else {
          console.log("Anuncio creado");
          index++;
          res.send("Anuncio creado");
      }
  });

});

app.listen(3000, function () {
  console.log('Anuncios app listening on port 3000!');
});