var MongoClient = require('mongodb').MongoClient;
var url = "mongodb://localhost:27017/";

MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  var dbo = db.db("Provedores-DB");
  dbo.createCollection("maquinas0", function(err, res) {
    if (err) throw err;
    console.log("Collection created!");
    db.close();
  });
  dbo.createCollection("maquinas1", function(err, res) {
    if (err) throw err;
    console.log("Collection created!");
    db.close();
  });
  dbo.createCollection("maquinas2", function(err, res) {
    if (err) throw err;
    console.log("Collection created!");
    db.close();
  });
  dbo.createCollection("maquinas3", function(err, res) {
    if (err) throw err;
    console.log("Collection created!");
    db.close();
  });
});
