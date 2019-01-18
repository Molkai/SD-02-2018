var MongoClient = require('mongodb').MongoClient;
var url = "mongodb://localhost:27017/";

MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  var dbo = db.db("Provedores-DB");

  // Maquinas provedor 1
  var myobj1 = [
    { idM: 0 , qtd_vCPU: 2, RAM: 4 , HD: 32 , preco_hora: 3.2},
    { idM: 1 , qtd_vCPU: 3, RAM: 8 , HD: 16 , preco_hora: 4.6},
    { idM: 2 , qtd_vCPU: 2, RAM: 2 , HD: 64 , preco_hora: 5.2},
    { idM: 3 , qtd_vCPU: 4, RAM: 4 , HD: 32 , preco_hora: 3.7},
    { idM: 4 , qtd_vCPU: 6, RAM: 4 , HD: 16 , preco_hora: 2.1},
    { idM: 5 , qtd_vCPU: 2, RAM: 6 , HD: 64 , preco_hora: 4.3},
    { idM: 6 , qtd_vCPU: 3, RAM: 8 , HD: 32 , preco_hora: 5.4},
    { idM: 7 , qtd_vCPU: 4, RAM: 4 , HD: 32 , preco_hora: 3.2},
    { idM: 8 , qtd_vCPU: 8, RAM: 2 , HD: 32 , preco_hora: 3.1},
    { idM: 9 , qtd_vCPU: 2, RAM: 2 , HD: 64 , preco_hora: 3.7},
    { idM: 10 , qtd_vCPU: 3, RAM: 4 , HD: 16 , preco_hora: 3.5},
    { idM: 11 , qtd_vCPU: 8, RAM: 6 , HD: 16 , preco_hora: 2.3},
    { idM: 12 , qtd_vCPU: 4, RAM: 4 , HD: 32 , preco_hora: 6.3},
    { idM: 13 , qtd_vCPU: 4, RAM: 8 , HD: 32 , preco_hora: 3.1},
  ];
  dbo.collection("maquinas0").insertMany(myobj1, function(err, res) {
    if (err) throw err;
    console.log("Número de máquinas inseridas no provedor 1: " + res.insertedCount);
    db.close();
  });

  // Maquinas provedor 2
  var myobj2 = [
    { idM: 0 , qtd_vCPU: 2, RAM: 4 , HD: 32 , preco_hora: 5.2},
    { idM: 1 , qtd_vCPU: 3, RAM: 8 , HD: 16 , preco_hora: 4.6},
    { idM: 2 , qtd_vCPU: 2, RAM: 2 , HD: 64 , preco_hora: 3.2},
    { idM: 3 , qtd_vCPU: 4, RAM: 4 , HD: 32 , preco_hora: 4.7},
    { idM: 4 , qtd_vCPU: 6, RAM: 4 , HD: 16 , preco_hora: 1.1},
    { idM: 5 , qtd_vCPU: 2, RAM: 6 , HD: 64 , preco_hora: 3.3},
    { idM: 6 , qtd_vCPU: 3, RAM: 8 , HD: 32 , preco_hora: 6.4},
    { idM: 7 , qtd_vCPU: 4, RAM: 4 , HD: 32 , preco_hora: 5.2},
  ];
  dbo.collection("maquinas1").insertMany(myobj2, function(err, res) {
    if (err) throw err;
    console.log("Número de máquinas inseridas no provedor 2: " + res.insertedCount);
    db.close();
  });

  // Maquinas provedor 3
  var myobj3 = [
    { idM: 0 , qtd_vCPU: 2, RAM: 4 , HD: 32 , preco_hora: 2.2},
    { idM: 1 , qtd_vCPU: 3, RAM: 8 , HD: 16 , preco_hora: 3.6},
    { idM: 2 , qtd_vCPU: 2, RAM: 2 , HD: 64 , preco_hora: 4.2},
    { idM: 3 , qtd_vCPU: 4, RAM: 4 , HD: 32 , preco_hora: 2.7},
    { idM: 4 , qtd_vCPU: 6, RAM: 4 , HD: 16 , preco_hora: 3.1},
    { idM: 5 , qtd_vCPU: 2, RAM: 6 , HD: 64 , preco_hora: 5.3},
    { idM: 6 , qtd_vCPU: 3, RAM: 8 , HD: 32 , preco_hora: 3.4},
    { idM: 7 , qtd_vCPU: 4, RAM: 4 , HD: 32 , preco_hora: 2.2},
    { idM: 8 , qtd_vCPU: 8, RAM: 2 , HD: 32 , preco_hora: 4.1},

  ];
  dbo.collection("maquinas2").insertMany(myobj3, function(err, res) {
    if (err) throw err;
    console.log("Número de máquinas inseridas no provedor 3: " + res.insertedCount);
    db.close();
  });

  // Maquinas provedor 4
  var myobj4 = [
    { idM: 0 , qtd_vCPU: 2, RAM: 4 , HD: 32 , preco_hora: 2.2},
    { idM: 1 , qtd_vCPU: 3, RAM: 8 , HD: 16 , preco_hora: 3.6},
    { idM: 2 , qtd_vCPU: 2, RAM: 2 , HD: 64 , preco_hora: 7.2},
    { idM: 3 , qtd_vCPU: 4, RAM: 4 , HD: 32 , preco_hora: 5.7},
  ];
  dbo.collection("maquinas3").insertMany(myobj4, function(err, res) {
    if (err) throw err;
    console.log("Número de máquinas inseridas no provedor 4: " + res.insertedCount);
    db.close();
  });
});
